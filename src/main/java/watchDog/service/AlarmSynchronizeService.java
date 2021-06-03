/**
*
*/
package watchDog.service;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import watchDog.database.DatabaseMgr;
import watchDog.database.RecordSet;
import watchDog.util.DateTool;
import watchDog.util.ObjectUtils;
import watchDog.util.ValueRetrieve;

public class AlarmSynchronizeService {  
      
	private static final Logger logger = Logger.getLogger(ValueRetrieve.class);
    
	private static Map<String, String> getAlarmFromBoss(String ip, String iddevice, String start, int length) {
		Map<String, String> result = new HashMap<>();
		Document doc = null;
		String xml = "";
		try {
			String postStr = "input=<requests><login userName=\"xml\" password=\"xml_query_3#\" /><request type=\"alarmList\" language=\"EN_en\">";
			postStr += "<element";
			if (iddevice != null)
				postStr += " idDevice= \"" + iddevice + "\" ";
			if (start != null)
				postStr += " start= \"" + start + "\" ";
			postStr += " length= \"" + length + "\" />";
			postStr += "</request></requests>";
			xml = ValueRetrieve.sendHttpsPost("https://" + ip + "/boss/servlet/MasterXML", postStr);
			if(xml.contains("&"))
				xml = xml.replaceAll("&", "&amp;");
			if(StringUtils.isBlank(xml))
				return result;
			doc = DocumentHelper.parseText(xml);
			if(doc != null){
				Element root = doc.getRootElement();
				List<Element> alarms = root.selectNodes("//alarm");
				if (ObjectUtils.isCollectionNotEmpty(alarms)) {
					int i = 0;
					for (Element a : alarms) {
						String idalarm = (String) a.attributeValue("idalarm");
						String endTime = (String) a.attributeValue("endtime");
						String starttime = (String) a.attributeValue("starttime");

						if ("".equals(endTime)) // 只有idalarm才是唯一的
							result.put(idalarm, "true");
						else {
							result.put(idalarm, "false");
						}
						i++;
						if (i == alarms.size())
							result.put("endTime", starttime);// starttime
					}
				}
			}
		} catch (Exception e) {
			logger.error("ip:" + ip, e);
			logger.info(xml.substring(xml.lastIndexOf("<alarm"), xml.length() - 1));
		}
		return result;
	}
    
    public static Map<String,String> getAlarmFromBoss(String ip) throws IOException{
    	return getAlarmFromBoss(ip,null,null,200);
    }
    
    public static Map<String,String> getAlarmFromBoss(String ip,String iddevice,String start) throws IOException{
    	return getAlarmFromBoss(ip,iddevice,start,3);
    }
    
    public static void main(String[] args) throws IOException {
		getAlarmFromBoss("192.168.90.67");
	}
    
    public static void doit(String ip,Integer supervisorId) throws IOException {
    	String sql = "select lgalarmactive.idalarm,lgalarmactive.starttime,lgalarmactive.iddevice "
    			+ " from lgalarmactive "
    			+ " where lgalarmactive.kidsupervisor=? and lgalarmactive.iddevice>? ;";
    	
    	Object[] params = {supervisorId,0};
		try{
			long s = System.currentTimeMillis();
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql, params);
			if(rs != null && rs.size() > 0){
				Map<String,String> map =  getAlarmFromBoss(ip);
				if(ObjectUtils.isMapNotEmpty(map)){
					String idAlarmStr = "";
					String iddevice = "";
					int minCheckNo = 200;
					
					for(int i=0;i<rs.size();i++)
					{
						Integer idalarm = (Integer)rs.get(i).get(0);
						Date starttime = (Date)rs.get(i).get(1);
						String tmp = String.valueOf(idalarm);
						iddevice = String.valueOf((Integer)rs.get(i).get("iddevice"));
						
						if("false".equals(map.get(tmp))){
							//reset
							idAlarmStr += tmp+";";
						}
						else if("true".equals(map.get(tmp)))
							continue;
						else{//2分法判断该报警的大概发生时间
							//do the before idalarm
							if("true".equals(map.get(tmp)))
								continue;
							
							int max = 10000;
							int min =0;
							int findNo = max;
							
							Map<String,String> mapBefore =  new HashMap<String,String>();
							mapBefore =  getAlarmFromBoss(ip,iddevice,null,200);
							boolean needLoop = true;
							if(ObjectUtils.isMapEmpty(mapBefore))
								continue;	
							Date endTimeBefore = DateTool.parse(mapBefore.get("endTime"),DateTool.DEFAULT_DATETIME_FORMAT);
							if("false".equals(mapBefore.get(tmp))){
								idAlarmStr += tmp+";";
								needLoop = false;
								continue;
							}
							else if("true".equals(map.get(tmp)))
								continue;
							if(mapBefore.get(tmp)==null && starttime.after(endTimeBefore)){
								needLoop = false;
								continue;
							}
							if(mapBefore.size()<200){
								needLoop = false;
								continue;
							}
	
							while(needLoop){
								if((max-min)<=minCheckNo){
									mapBefore =  getAlarmFromBoss(ip,iddevice,String.valueOf(min),max-min+1);
									if("false".equals(mapBefore.get(tmp))){
										//reset
										idAlarmStr += tmp+";";
									}
									break;
								}
								mapBefore =  getAlarmFromBoss(ip,iddevice,String.valueOf(findNo));
								if(mapBefore.size()==0){// down
									max = findNo;
									findNo = max-((max-min)%2==0?(max-min)/2:(max-min+1)/2);
									continue;
								}
								if("false".equals(mapBefore.get(tmp))){
									idAlarmStr += tmp+";";
									break;
								}
								else if("true".equals(map.get(tmp)))
									break;
								endTimeBefore = DateTool.parse(mapBefore.get("endTime"),DateTool.DEFAULT_DATETIME_FORMAT);
								if(starttime.before(endTimeBefore)){//Remote时间在boss时间之前，findNo 将增加。
									min = findNo;
								}else{
									max = findNo;	
								}
								findNo = max-((max-min)%2==0?(max-min)/2:(max-min+1)/2);
								continue;
							}	
						}
					}
					if(idAlarmStr.endsWith(";"))
						idAlarmStr = idAlarmStr.substring(0, idAlarmStr.length()-1);
					if(StringUtils.isNotBlank(idAlarmStr)){
						String[] arr = idAlarmStr.split(";");
						int[] idAlarms = new int[arr.length];
						for(int m = 0; m< arr.length;m++){
							idAlarms[m] = Integer.parseInt(arr[m]);
						}
						logger.info("ip:" + ip + ",supervisorId:" + supervisorId + ",reset num:"+idAlarms.length+",idalarms:" + idAlarmStr);
						AlarmManageService.reset(supervisorId, idAlarms, "auto", "",false);
					}
				}
				
				
			}
		}catch(Exception e)
		{
			logger.error("ip:" + ip, e);
		}
    	
    }
    
}  