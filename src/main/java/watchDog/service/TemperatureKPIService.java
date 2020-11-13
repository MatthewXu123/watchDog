package watchDog.service;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.chanjar.weixin.common.util.StringUtils;

import org.apache.log4j.Logger;

import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.Record;
import watchDog.database.RecordSet;
import watchDog.util.DateTool;

public class TemperatureKPIService {
	private static final Logger logger = Logger.getLogger(AlarmService.class);
	private static final int NO_HIGHEST = 20;
	private static final int NO_LOWEST = 20;
	//group by daily
	//timeRange must
	private static final Object[] Integer = null;
	
	public static void createTempreatureACK(String type,Date[] timeRange)
	{
		String sql = "delete from private_temperature_kpi_result where type=? and startday>=?;";
		Object[] params = {type,timeRange[0]};
		try {
			DatabaseMgr.getInstance().executeUpdate(sql,params);
			createSiteTemperatureKPI(type,timeRange);
		} catch (DataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createSiteTemperatureKPI(String type,Date[] timeRange)
	{
		long minutesOfPeriod = (timeRange[1].getTime() - timeRange[0].getTime()) / (1000*60);
		
		try{
			String sql = "select t.kidsupervisor,t.kiddevice,t.kidsubdevice,t.overcritical,t.overdifferential,t.oversetpoint,t.undersetpoint,t.undercritical,t.defrostcount,d.devicedescription,d.subdevicedescription from Hstemperature as t "+
					"inner join vwtempkpisubdevices as d on t.kidsupervisor=d.kidsupervisor and t.kiddevice=d.iddevice and t.kidsubdevice=d.subdevice ";
			if(timeRange != null && timeRange[0] != null && timeRange[1] != null)
			{
				sql += "where t.day >='"+DateTool.format(timeRange[0], "yyyy-MM-dd")+"' and t.day <'"+DateTool.format(timeRange[1], "yyyy-MM-dd")+"' ";
			}
			sql +=" order by t.kidsupervisor,t.kiddevice,t.kidsubdevice";
			
			RecordSet rs1 = DatabaseMgr.getInstance().executeQuery(sql);
			int kidsupervisor = -1;
			String devicedescription = "";
			int iddevice = -1;
			int kidsubdevice = -1;
			int tempSite = -1;
			int tempDevice = -1;
			int tempSubDevice = -1;
			String tempDeviceDescription = "";
			long defrostMinutes = 0;
			long overDiffOverKMinutes = 0;
			long overDiffUnderKMinutes = 0;
			long underDiffOverSetPointMinutes = 0;
			long underSetpointOverKMinutes = 0;
			long underSetpointUnderKMinutes = 0;
			long undefinedMinutes = 0;			
			if(rs1 != null && rs1.size()>0){
				for(int m=0;m<rs1.size();m++)
				{
					Record r1 = rs1.get(m);
					kidsupervisor = (Integer)r1.get("kidsupervisor");
					iddevice  = (Integer)r1.get("kiddevice");
					kidsubdevice = (Integer)r1.get("kidsubdevice");
					String device = (String)r1.get("devicedescription");
					String subdevice = (String)r1.get("subdevicedescription");
					if(StringUtils.isBlank(subdevice))
						devicedescription = device;
					else
						devicedescription = device +" - "+subdevice;
					overDiffOverKMinutes += getval(r1,"overcritical");
					overDiffUnderKMinutes += getval(r1,"overdifferential");
					underDiffOverSetPointMinutes += getval(r1,"oversetpoint");
					underSetpointOverKMinutes += getval(r1,"undersetpoint");
					underSetpointUnderKMinutes += getval(r1,"undercritical");
					defrostMinutes += getval(r1,"defrostcount");
					if(tempSite == -1)
					{
						tempSite = kidsupervisor;
						tempDevice = iddevice;
						tempSubDevice = kidsubdevice;
						tempDeviceDescription = devicedescription;
					}
					if(tempSite != kidsupervisor || tempDevice != iddevice || tempSubDevice != kidsubdevice)
					{
						insertData(type,timeRange[0],tempSite,tempDevice,tempDeviceDescription,defrostMinutes,overDiffOverKMinutes,overDiffUnderKMinutes,
								underDiffOverSetPointMinutes,underSetpointOverKMinutes,underSetpointUnderKMinutes,undefinedMinutes,minutesOfPeriod);
						tempSite = kidsupervisor;
						tempDevice = iddevice;
						tempSubDevice = kidsubdevice;
						tempDeviceDescription = devicedescription;
						defrostMinutes = 0;
						overDiffOverKMinutes = 0;
						overDiffUnderKMinutes = 0;
						underDiffOverSetPointMinutes = 0;
						underSetpointOverKMinutes = 0;
						underSetpointUnderKMinutes = 0;
						undefinedMinutes = 0;			
					}
				}
				insertData(type,timeRange[0],tempSite,tempDevice,tempDeviceDescription,defrostMinutes,overDiffOverKMinutes,overDiffUnderKMinutes,
						underDiffOverSetPointMinutes,underSetpointOverKMinutes,underSetpointUnderKMinutes,undefinedMinutes,minutesOfPeriod);
			}
				
		} catch (Exception ex) {
			logger.error("error",ex);
		}
	}
	
	public static void insertData(String type,Date startday,Integer idsite,Integer iddevice,String devicedescription,long defrostMinutes,long overDiffOverKMinutes,long overDiffUnderKMinutes,
			long underDiffOverSetPointMinutes,long underSetpointOverKMinutes,long underSetpointUnderKMinutes,long undefinedMinutes,long minutesOfPeriod){
		int defrost;
		int overDiffOverK;
		int overDiffUnderK;
		int underDiffOverSetPoint;
		int underSetpointOverK;
		int underSetpointUnderK;
		int undefined;
		
		defrost = new Long ( (100*defrostMinutes) / minutesOfPeriod).intValue();
		long samples100percent = overDiffOverKMinutes+overDiffUnderKMinutes+underDiffOverSetPointMinutes+underSetpointOverKMinutes+underSetpointUnderKMinutes+undefinedMinutes;
		if (samples100percent>0){
			overDiffOverK = new Long ( (100*overDiffOverKMinutes) / samples100percent).intValue();
			overDiffUnderK = new Long ( (100*overDiffUnderKMinutes) / samples100percent).intValue();
			underDiffOverSetPoint = new Long ( (100*underDiffOverSetPointMinutes) / samples100percent).intValue();
			underSetpointOverK = new Long ( (100*underSetpointOverKMinutes) / samples100percent).intValue();
			underSetpointUnderK = new Long ( (100*underSetpointUnderKMinutes) / samples100percent).intValue();
			undefined = new Long ( (100*undefinedMinutes) / samples100percent).intValue();
			
			int sum = overDiffOverK + overDiffUnderK + underDiffOverSetPoint + underSetpointOverK+ underSetpointUnderK;

			if (sum != 0) {
				while (sum != 100) {

						if (	overDiffOverK >= overDiffUnderK && 
								overDiffOverK >= underDiffOverSetPoint &&
								overDiffOverK >= underSetpointOverK &&
								overDiffOverK >= underSetpointUnderK &&
								overDiffOverK >= undefined
							)
							if (sum>100)
								overDiffOverK--;
							else
								overDiffOverK++;
						else if (	overDiffUnderK >= overDiffOverK && 
									overDiffUnderK >= underDiffOverSetPoint &&
									overDiffUnderK >= underSetpointOverK &&
									overDiffUnderK >= underSetpointUnderK&&
									overDiffUnderK >= undefined)
							if (sum>100)
								overDiffUnderK--;
							else
								overDiffUnderK++;
						else if (	underDiffOverSetPoint >= overDiffOverK && 
									underDiffOverSetPoint >= overDiffUnderK &&
									underDiffOverSetPoint >= underSetpointOverK &&
									underDiffOverSetPoint >= underSetpointUnderK&&
									underDiffOverSetPoint >= undefined)
							if (sum>100)
								underDiffOverSetPoint--;
							else
								underDiffOverSetPoint++;
						else if (	underSetpointOverK >= overDiffOverK && 
									underSetpointOverK >= overDiffUnderK &&
									underSetpointOverK >= underDiffOverSetPoint &&
									underSetpointOverK >= underSetpointUnderK&&
									underSetpointOverK >= undefined)
							if (sum>100)
								underSetpointOverK--;
							else
								underSetpointOverK++;
						else if (	underSetpointUnderK >= overDiffOverK && 
									underSetpointUnderK >= overDiffUnderK &&
									underSetpointUnderK >= underDiffOverSetPoint &&
									underSetpointUnderK >= underSetpointOverK&&
									underSetpointUnderK >= undefined)
							if (sum>100)
								underSetpointUnderK--;
							else
								underSetpointUnderK++;
						
						else if (	undefined >= overDiffOverK && 
								undefined >= overDiffUnderK &&
								undefined >= underDiffOverSetPoint &&
								undefined >= underSetpointOverK&&
								undefined >= underSetpointUnderK)
						if (sum>100)
							undefined--;
						else
							undefined++;
					sum =  overDiffOverK + overDiffUnderK + underDiffOverSetPoint + underSetpointOverK+ underSetpointUnderK+undefined;
				}
			}
		}
		else {
			overDiffOverK = 0;
			overDiffUnderK = 0;
			underDiffOverSetPoint = 0;
			underSetpointOverK = 0;
			underSetpointUnderK = 0;
			undefined=100;
		}
		
//		if(underSetpointUnderK>=NO_HIGHEST || overDiffOverK>=NO_LOWEST){
			String sql="insert into private_temperature_kpi_result values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Object[] params = new Object[13];
			params[0]=type;
			params[1]=startday;
			params[2]=idsite;
			params[3]=iddevice;
			params[4]=devicedescription;
			params[5]=overDiffOverK;
			params[6]=overDiffUnderK;
			params[7]=underDiffOverSetPoint;
			params[8]=underSetpointOverK;
			params[9]=underSetpointUnderK;
			params[10]=undefined;
			params[11]=defrost;
			params[12]=defrostMinutes;
			try {
				DatabaseMgr.getInstance().executeUpdate(sql,params);
			} catch (DataBaseException e) {
				logger.error("", e);
			}
//		}
		
	}
	
	public static List<TemperatureKPI> getTemperatureKPIStatistic(int kidsupervisor,Date startDay,String type){		
		List<TemperatureKPI> result = new ArrayList<TemperatureKPI>();
		Object[] params = null;
		String sql = "select * from private_temperature_kpi_result";
		sql += " where idsite=? and  startday='"+ DateTool.format(startDay, "yyyy-MM-dd") + "'";
//		sql += " and (t1>? or t5>?)";
		sql += " and type=? "+
			   " order by t3,t1 desc ";
		params = new Object[2]; 
		params[0]=kidsupervisor;
		params[1]=type;
//		params[1]=NO_LOWEST;
//		params[2]=NO_HIGHEST;
		try{
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				for(int i=0;i<rs.size();i++)
				{
					Record r = rs.get(i);
					String devicedescription = (String)r.get("devicedescription");
					if(devicedescription.length()>27)
						devicedescription = devicedescription.substring(0,24)+"...";
					int overDiffOverK = (Integer) r.get("t1");
					int overDiffUnderK = (Integer) r.get("t2");
					int underDiffOverSetPoint = (Integer) r.get("t3");
					int underSetpointOverK = (Integer) r.get("t4");
					int underSetpointUnderK = (Integer) r.get("t5");
					int undefined = (Integer) r.get("t6");
					int defrost = (Integer) r.get("defrost");
					int defrostMinutes = (Integer) r.get("defrostminutes");
					
					result.add(new TemperatureKPI(devicedescription, overDiffOverK, overDiffUnderK, underDiffOverSetPoint, 
							underSetpointOverK, underSetpointUnderK, undefined, String.valueOf(defrost)+"%("+String.valueOf(defrostMinutes)+"分)"));
				}
				
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return result;
	}
	
	
	
	public static long getval(Record r,String column) throws SQLException{
		long val = 0;
		Array arg =(Array)r.get(column);
		Integer[] tm = (Integer[])arg.getArray();
		for(int n=0;n<tm.length;n++){
			val += tm[n]!= null?tm[n]:0;
		}
		return val;
	}
	
	
	public static class TemperatureKPI{
		String devicedescription;
		int per1;
		int per2;
		int per3;
		int per4;
		int per5;
		int per6;
		String deforst;
		
		
		public TemperatureKPI(String devicedescription,int per1,int per2,int per3,int per4,int per5,int per6,String deforst)
		{
			this.devicedescription=devicedescription;
			this.per1=per1;
			this.per2=per2;
			this.per3=per3;
			this.per4=per4;
			this.per5=per5;
			this.deforst=deforst;
		}


		public String getDevicedescription() {
			return devicedescription;
		}


		public void setDevicedescription(String devicedescription) {
			this.devicedescription = devicedescription;
		}


		public int getPer1() {
			return per1;
		}


		public void setPer1(int per1) {
			this.per1 = per1;
		}


		public int getPer2() {
			return per2;
		}


		public void setPer2(int per2) {
			this.per2 = per2;
		}


		public int getPer3() {
			return per3;
		}


		public void setPer3(int per3) {
			this.per3 = per3;
		}


		public int getPer4() {
			return per4;
		}


		public void setPer4(int per4) {
			this.per4 = per4;
		}


		public int getPer5() {
			return per5;
		}


		public void setPer5(int per5) {
			this.per5 = per5;
		}


		public int getPer6() {
			return per6;
		}


		public void setPer6(int per6) {
			this.per6 = per6;
		}


		public String getDeforst() {
			return deforst;
		}


		public void setDeforst(String deforst) {
			this.deforst = deforst;
		}


		@Override
		public String toString() {
			return "TemperatureKPI [devicedescription=" + devicedescription + ", per1=" + per1 + ", per2=" + per2
					+ ", per3=" + per3 + ", per4=" + per4 + ", per5=" + per5 + ", per6=" + per6 + ", deforst=" + deforst
					+ "]";
		}

		
		
	}
	
	
}
	