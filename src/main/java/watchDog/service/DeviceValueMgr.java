package watchDog.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import watchDog.bean.DeviceValueBean;
import watchDog.config.json.DeviceModelConfig;
import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.Record;
import watchDog.database.RecordSet;
import watchDog.util.DateTool;
import watchDog.util.MyHashMap;
import watchDog.util.ValueRetrieve;

public class DeviceValueMgr {
	private Date currentDate = new Date();
	private Map<String,Integer> userQueryCounter = new HashMap<String,Integer>();
	private static int threadCounter = 0;
	private static final int MAXTHREAD = 20;
	private static final int VALIDSECONDS = 5;
	private static final String SPLITTER = "---";
	private static final String CODE_VALUE_SPLITTER = "::::";
	
	Map<String,List<String>> siteONOFF = new HashMap<String,List<String>>();
	
	public static String[] MPXPROCodes = {
		//status
		"d73",//制冷状态
		"s_ReleFan",//风机状态
		"s_ReleLight",//灯光状态
		"s_DEF",//除霜状态
		//主要参数
		"regulation",//控制温度
		"airoff",//出风温度
		"airon",//回风温度
		"d/1",//除霜温度
		"s_SetpointWork",//温度设定值
		"Po1",//过热度值
		"s_EvaporationPression",//蒸发压力值
		"Po3",//吸气温度(tGs)???吸气温度值
		"Po4",//吸气压力饱和温度
		"Po2",//电子膨胀阀开度
		"s_PF",//电子膨胀阀开启步数 
//		"s_TrimHeaterOutput",//防露加热模拟输出状态
		//设定值
		"St",//温度控制设定值
		"dt1",//除霜结束温度设定值
		"dP1",//最大除霜持续时间
		"AH"//高温报警设定值
	};
	MyHashMap<String,DeviceValueBean> valueMap = new MyHashMap<String,DeviceValueBean>(500);
	public static DeviceValueMgr instance = null;
	
	public static DeviceValueMgr getInstance()
	{
		if(instance == null)
			instance = new DeviceValueMgr();
		return instance;
	}
	
	private DeviceValueMgr(){}
	
	//get value regardless if it is latest or not
	//if it is old, ask to refresh anyway
	public DeviceValueBean getValueQuick(String ip,String devCode)
	{
		String key = ip+SPLITTER+devCode;
		DeviceValueBean value = valueMap.get(key);
		if(value != null)
		{
			if(DateTool.diff(new Date(), value.getDate())<=VALIDSECONDS*1000)
				return value;
		}
		if(threadCounter>MAXTHREAD)
			return null;
		NewThread thread = new NewThread(ip,devCode);
		thread.start();
		if(value != null)
			return value;
		return null;
	}

	//return lastest value, could be slow
	public DeviceValueBean getValueSlow(String ip,String devCode)
	{
		String key = ip+SPLITTER+devCode;
		DeviceValueBean value = valueMap.get(key);
		if(value != null)
		{
			if(DateTool.diff(new Date(), value.getDate())<=VALIDSECONDS*1000)
				return value;
		}
		
		String[] codeArray = DeviceModelConfig.getCodeArray(DeviceModelConfig.getdevmdlCode(ip, devCode));
		
		Map<String,String> values = ValueRetrieve.getValue(ip, devCode, codeArray);
		if(values != null)
		{
			DeviceValueBean bean = new DeviceValueBean(new Date(),values);
			valueMap.put(key, bean);
			return bean;
		}

		return null;
	}
	
	public String getUnitOffDesc(String ip,String[] devCode,String varCode,Map<String,String> deviceInfo) throws Exception
	{
		String result ="";
		String deviceStr="";
		Integer No = 0;
		String[] varCodes = varCode.split(CODE_VALUE_SPLITTER);
		if(varCodes.length != 2)
			return result;
		Map<String,String> values = ValueRetrieve.getValue(ip,devCode,varCodes[0]);
		List<String> lBefore = siteONOFF.get(ip);
		List<String> lAfter = new ArrayList<String>();
		boolean hasNew = false;  // 判断当前站点是否有新增设备关机
		if(values!=null){
			Iterator<Map.Entry<String, String>> entries = values.entrySet().iterator(); 
			while (entries.hasNext()) { 
			  Map.Entry<String, String> entry = entries.next(); 
			  if(entry.getValue().equals(varCodes[1])){
				  No++;
				  String address = entry.getKey().split(";")[0];
				  deviceStr+=deviceInfo.get(address)+"\n";
				  lAfter.add(address);
				  if((lBefore==null || !lBefore.contains(address)) && !hasNew)
					  hasNew = true;
			  }  
			}
		}
		
		if(No>0 && hasNew)
			result = "如下" + No+"个设备已关机: \n\n"+deviceStr.substring(0, deviceStr.length()-1);
		siteONOFF.put(ip, lAfter);
		return result;
	}
	
	class NewThread extends Thread
	{
		
		String ip,devCode;
		public NewThread(String ip,String devCode)
		{
			super("DeviceValueNewThread");
			this.ip = ip;
			this.devCode = devCode;
			threadCounter++;
		}
		public void run()
		{
			getValueSlow(ip,devCode);
			threadCounter--;
		}
	}
	public static String[] getSiteDeviceDescription(String ip,String devCode)
	{
		String[] info = {"","",""};
		String sql = "select d.description as device,s.description as site,s,id as idsite,d.iddevice as iddevice from lgdevice as d "+
			"inner join cfsupervisors as s on d.kidsupervisor=s.id "+
			"where s.ipaddress=? and d.code=?";
		Object[] params = {ip,devCode};
		try
		{
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				info[0] = (String)rs.get(0).get("site");
				info[1] = (String)rs.get(0).get("device");
				int idsite = (int)rs.get(0).get("idsite");
				int iddevice = (int)rs.get(0).get("iddevice");
				info[2] = ShortURLMgr.getInstance().getDeviceURL(ip,idsite,iddevice);
				return info;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return info;
	}
	public static List<Map<String, String>> getSiteDeviceList(int idsite) {
		String sql = "select d.description as device,d.devmodcode as devmdlcode,d.iddevice as iddevice,s.ipaddress as ip, d.code as devcode from lgdevice as d " 
				+ " inner join cfsupervisors as s on d.kidsupervisor=s.id" 
				+ " where d.kidsupervisor=?";
		List<Map<String, String>> list = new ArrayList<>();
		try {
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql, new Object[]{idsite});
			if(rs != null && rs.size() > 0){
				for(int i = 0; i < rs.size(); i++){
					Map<String, String> map = new HashMap<>();
					Record record = rs.get(i);
					if((Integer)record.get("iddevice") > 1){
						map.put("device",(String)record.get("device"));
						map.put("devmdlcode", String.valueOf(DeviceModelConfig.isDevmdlCode((String)record.get("devmdlcode"))));
						map.put("ip",(String)record.get("ip"));
						map.put("devcode",(String)record.get("devcode"));
						list.add(map);
					}
				}
			}
		} catch (DataBaseException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 一个设备如果取值为***，就判断为设备离线。
	 * @param ip
	 * @param devCode
	 * @return
	 * @author MatthewXu
	 * @date Mar 27, 2019
	 */
	public static boolean isDeviceOnline(String ip, String devCode) {
		DeviceValueBean value = DeviceValueMgr.getInstance().getValueSlow(ip, devCode);
		//DeviceValueBean value = DeviceValueMgr.getInstance().getValueQuick(ip, devCode);
		if(value == null){
			return false;
		}else{
			for (Entry<String, String> vMap : value.getValues().entrySet()) {
				if(vMap.getValue().equals("***")){
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 一个设备如果取值为null，就判断为站点离线。
	 * @param ip
	 * @param devCode
	 * @return
	 * @author MatthewXu
	 * @date Mar 27, 2019
	 */
	public static boolean isSiteOnline(String ip, String devCode) {
		DeviceValueBean value = DeviceValueMgr.getInstance().getValueSlow(ip, devCode);
		//DeviceValueBean value = DeviceValueMgr.getInstance().getValueQuick(ip, devCode);
		if(value == null)
			return false;
		return true;
	}
}
