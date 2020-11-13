package watchDog.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.postgresql.jdbc4.Jdbc4Array;

import watchDog.controller.GetData;
import watchDog.database.DatabaseMgr;
import watchDog.database.Record;
import watchDog.database.RecordSet;
import watchDog.service.AlarmService.AlarmMonth;
import watchDog.service.AlarmService.energyWeek;
import watchDog.util.DateTool;

public class EnergyService {
	private static final Logger logger = Logger.getLogger(EnergyService.class);
	
	public static final int MONTH_PERIOD = 1;
	public static final int DAY_PERIOD = 2;
	public static final int HOUR_PERIOD = 3;
	public static final int WEEK_PERIOD = 4;
	
	public static final int GLOBAL_METER_ID = -101;
	
	public static int getKWHLastMonth(int idsite)
	{
		int result = 0;
		String sql = "select count(*)::int from cfkpienergy "+
				"where kidsupervisor=? and global=true";
		Object[] params = {idsite};
		boolean hasGlobal = false;
		try {
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				if((int)rs.get(0).get(0)>0)
					hasGlobal = true;
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		sql = "select sum(data.total) from vwenergysubdevices as vi "+
				"inner join hsconsumption as data on vi.kidsupervisor=data.kidsupervisor and vi.idvariable=data.kidvariable "+
				"where vi.kidsupervisor=? and to_char(day,'YYYY-MM')=? ";
		if(hasGlobal)
			sql += " and vi.global=true";
		params = new Object[]{idsite,DateTool.format(DateTool.addMonths(-1),"yyyy-MM")};
		
		try {
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0 && rs.get(0).get(0) != null)
			{
				result = (int)(float)rs.get(0).get(0);
			}
		} catch (Exception ex) {
			logger.error("error ",ex);
		}
		return result;
	}
	

	//static week for hq
	public static List<energyWeek> getEnergyResult(int[] idsite,String type, int periodType,int idMeterVariable,String startday){
		List<energyWeek> result = new ArrayList<energyWeek>();
		String sites="(";
		for(int i=0;i<idsite.length;i++){
			sites +=(idsite[i]+",");
		}
		if(sites.endsWith(","))
			sites=sites.substring(0, sites.length()-1);
		sites +=")";
		
		String sql = "select kidsupervisor,count(*)::int from cfkpienergy "+
				"where kidsupervisor in "+sites+" and global=true group by kidsupervisor";
		
		String sitesGlobal="";
		String siteNoGlobal=sites;
		if(idMeterVariable == AlarmService.NO_VARIABLE)
		{
			try {
				RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,null);
				if(rs != null && rs.size()>0)
				{
					String re = "";
					for(int i=0;i<rs.size();i++){
						if((int)rs.get(i).get(1)>0){
							re=((Integer)rs.get(i).get(0)+"");
							sitesGlobal +=(re+",");
							
							siteNoGlobal = siteNoGlobal.replace("("+re+",", "(-1,");
							siteNoGlobal = siteNoGlobal.replace(","+re+")", ",-1)");
							siteNoGlobal = siteNoGlobal.replace(","+re+",", ",-1,");
							siteNoGlobal = siteNoGlobal.replace("("+re+")", "(-1)");
							
						}
					}
					if(sitesGlobal.endsWith(","))
						sitesGlobal=sitesGlobal.substring(0, sitesGlobal.length()-1);
					if(!"".equals(sitesGlobal))
						sitesGlobal ="("+sitesGlobal+")";
					
				}
			} catch (Exception ex) {
				logger.error("error",ex);
			}
		}
		
		
		if(!"".equals(sitesGlobal))
			result = getEnergyResult(sitesGlobal,true,type,periodType,idMeterVariable,startday,result);
		if(!"".equals(siteNoGlobal))		
			result = getEnergyResult(siteNoGlobal,false,type,periodType,idMeterVariable,startday,result);
		
		return result;
	}
	
	public static Date getMonday(Date start){
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if(dayWeek==1){
            dayWeek = 8;
        }
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);
		Date mondayDate = cal.getTime();
		return mondayDate;
	}
	
	public static String getMonth(String startday,Integer month){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date firstOfMonth= new Date();
		try {
			firstOfMonth = sdf.parse(startday+"-01");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String dayStr2 = DateTool.format(DateTool.addMonths(firstOfMonth,month), "yyyy-MM-dd ").replaceAll(" ", "");
		return dayStr2.substring(0,dayStr2.lastIndexOf("-"));
	}
	
//	public static String getMonday(Date firstOfMonth,Integer month){
//		String dayStr2 = DateTool.format(DateTool.addMonths(firstOfMonth,month), "yyyy-MM-dd ").replaceAll(" ", "");
//		return dayStr2.substring(0,dayStr2.lastIndexOf("-")-1);
//	}
	
	public static  List<energyWeek> getEnergyResult(String sites,Boolean hasGlobal,String type,int periodType,int idMeterVariable,String startday,List<energyWeek> result){
		
		Date[] timeRange = DateTool.getLastWeekRange();    
		
		String dayStr1 = "";
		String dayStr2 = "";
		String dayStr3 = "";
		String dayStr4 = "";
		String endStr = "";

		if(GetData.type_month.equals(type)){
			dayStr1 = startday;
			dayStr2 = getMonth(startday,-1);
			dayStr3 = getMonth(startday,-2);
			dayStr4 = getMonth(startday,-3);
			
		}
		else if(GetData.type_week.equals(type))
		{
			timeRange[0] = DateTool.add(DateTool.parse(startday, "yyyy-MM-dd"), 7, Calendar.DATE);
			timeRange[1] = DateTool.add(timeRange[0], -7*4, Calendar.DATE);
			endStr = DateTool.format(DateTool.add(timeRange[0], 7, Calendar.DATE), "yyyy-MM-dd ").replaceAll(" ", "");
			Date mondayDate = getMonday(DateTool.parse(startday, "yyyy-MM-dd"));
			if(timeRange != null){
				
				dayStr1 = DateTool.format(mondayDate, "yyyy-MM-dd ").replaceAll(" ", "");
				dayStr2 = DateTool.format(DateTool.addDays(mondayDate,-7), "yyyy-MM-dd ").replaceAll(" ", "");
				dayStr3 = DateTool.format(DateTool.addDays(mondayDate,-7*2), "yyyy-MM-dd ").replaceAll(" ", "");
				dayStr4 = DateTool.format(DateTool.addDays(mondayDate,-7*3), "yyyy-MM-dd ").replaceAll(" ", "");
			}
		}
		
		
		String SQL_month="to_char( day::date -(case extract (dow from day::date) when 0 then 6 else  extract (dow from day::date)-1 end ||'day')::interval,'yyyy-MM-dd') as month,";
		if(GetData.type_month.equals(type)){
			SQL_month="to_char( day, 'yyyy-mm') as month,";
		}
		
		String sql = "select data.kidsupervisor id,data.description description,"+SQL_month;
		sql += "sum(data.total)::int as cnt from vwenergysubdevices as vi "+
				"inner join (select kidsupervisor,total,kidvariable,day,description from hsconsumption inner join cfsupervisors on hsconsumption.kidsupervisor=cfsupervisors.id) as data on vi.kidsupervisor=data.kidsupervisor and vi.idvariable=data.kidvariable "+
				"where vi.kidsupervisor in "+sites;
		if(GetData.type_month.equals(type)){
			sql += " and to_char( day, 'yyyy-mm')>='"+dayStr4+"' and to_char( day, 'yyyy-mm')<='"+startday+"' ";
		}else{
			if(timeRange == null)
				sql += "and day>='"+DateTool.format(timeRange[1])+"' ";
			else
				sql += " and day>='"+DateTool.format(timeRange[1])+"' and day<'"+DateTool.format(timeRange[0])+"' ";
		}
		
		if(hasGlobal)
			sql += " and vi.global=true ";
		sql += "group by id,description, month order by id,month";
		
		
		 
		if(idMeterVariable == AlarmService.NO_VARIABLE)
		{
			try {
				RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,null);
				if(rs != null && rs.size()>0)
				{
					int startSite = 0;
					String siteName="";
					
					int va1 = 0;
					int va2 = 0;
					int va3 = 0;
					int va4 = 0;
					for(int i=0;i<rs.size();i++){
						
						int siteIdCurr = (Integer)rs.get(i).get(0);
						String dateStr = (String)rs.get(i).get(2);
						Integer energy = (Integer)rs.get(i).get(3);
						if(startSite!=siteIdCurr){
							if(i!=0){//add List
								result.add( new AlarmService.energyWeek(siteName,va1,va2,va3,va4));
							}
							siteName = (String)rs.get(i).get(1);
							va1 = 0;
							va2 = 0;
							va3 = 0;
							va4 = 0;
							if(dayStr1.equals(dateStr))
								va1 = energy;
							else if(dayStr2.equals(dateStr))
								va2 = energy;
							else if(dayStr3.equals(dateStr))
								va3 = energy;
							else if(dayStr4.equals(dateStr))
								va4 = energy;
							startSite = siteIdCurr;
						}else{
							if(dayStr1.equals(dateStr))
								va1 = energy;
							else if(dayStr2.equals(dateStr))
								va2 = energy;
							else if(dayStr3.equals(dateStr))
								va3 = energy;
							else if(dayStr4.equals(dateStr))
								va4 = energy;
						}
						if(rs.size()-1==i){//add List: the last
							result.add( new AlarmService.energyWeek(siteName,va1,va2,va3,va4));
						}	
					}
				}
			} catch (Exception ex) {
				logger.error("error",ex);
			}
		}
		
		
		return result;
	}
	
	
	//stastic month
	public static LinkedHashMap<String,List<AlarmMonth>> getKWHMonthStatistics(int idsite,int periodType,int idMeterVariable,Date[] timeRange)
	{
		String format_str = "";
		if(periodType == MONTH_PERIOD)
			format_str = "YYYY-MM";
		else if(periodType == DAY_PERIOD)
			format_str = "YYYY-MM-DD";
		else if(periodType == WEEK_PERIOD)
			format_str = "YYYY-MM-DD";
		LinkedHashMap<String,List<AlarmMonth>> result = new LinkedHashMap<String,List<AlarmMonth>>();
		Map<String,AlarmMonth> tmp = new HashMap<String,AlarmMonth>();
		Date monthBegin = DateTool.getMonthBegin(new Date());
		Date timeStart = DateTool.addMonths(monthBegin, -8);
		String sql = "select count(*)::int from cfkpienergy "+
				"where kidsupervisor=? and global=true";
		String dayStr = "";
		if(timeRange != null)
		 dayStr = DateTool.format(timeRange[0], "yyyy-MM-dd ");
		Object[] params = {idsite};
		boolean hasGlobal = false;
		if(idMeterVariable == AlarmService.NO_VARIABLE)
		{
			try {
				RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
				if(rs != null && rs.size()>0)
				{
					if((int)rs.get(0).get(0)>0)
						hasGlobal = true;
				}
			} catch (Exception ex) {
				logger.error("error",ex);
			}
			if(periodType == MONTH_PERIOD || periodType == DAY_PERIOD || periodType == WEEK_PERIOD)
			{
				sql = "select to_char(day,'"+format_str+"') as month ,";
				if(periodType == WEEK_PERIOD)
					sql = "select to_char( day::date -(case extract (dow from day::date) when 0 then 6 else  extract (dow from day::date)-1 end ||'day')::interval,'yyyy-MM-dd') as month,";
				sql += "sum(data.total)::int as cnt from vwenergysubdevices as vi "+
						"inner join hsconsumption as data on vi.kidsupervisor=data.kidsupervisor and vi.idvariable=data.kidvariable "+
						"where vi.kidsupervisor=? "; 
				if(timeRange == null)
					sql += "and day>='"+DateTool.format(timeStart)+"' ";
				else
					sql += " and day>='"+DateTool.format(timeRange[0])+"' and day<'"+DateTool.format(timeRange[1])+"' ";
				if(hasGlobal)
					sql += " and vi.global=true ";
					sql += "group by month "+
					"order by month";
				
				params = new Object[]{idsite};
				
				try {
					RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
					if(rs != null && rs.size()>0)
					{
						for(int i=0;i<rs.size();i++)
						{
							Record r = rs.get(i);
							String month = (String)r.get("month");
							int cnt = -1;
							if(r.get("cnt") != null)
								cnt = (int)r.get("cnt");
							tmp.put(month,new AlarmMonth(month,cnt));
						}
					}
				} catch (Exception ex) {
					logger.error("error ",ex);
				}
			}
			else if(periodType == HOUR_PERIOD)
			{
				sql = "select data.daydivisions as cnt from vwenergysubdevices as vi "+
						"inner join hsconsumption as data on vi.kidsupervisor=data.kidsupervisor and vi.idvariable=data.kidvariable "+
						"where vi.kidsupervisor=? ";
					if(timeRange != null)
						sql += " and day>='"+DateTool.format(timeRange[0])+"' and day<'"+DateTool.format(timeRange[1])+"' ";
					if(hasGlobal)
						sql += " and vi.global=true ";
					
					params = new Object[]{idsite};
					
					try {
						RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
						if(rs != null && rs.size()>0)
						{
							for(int i=0;i<rs.size();i++)
							{
								Record r = rs.get(i);
								Jdbc4Array s = (Jdbc4Array)r.get(0);
								Float[] values = (Float[])s.getArray();
								for(int j=0;j<24;j++)
								{
									float sum = 0;
									if(values[j*2] != null)
										sum += values[j*2];
									if(values[j*2+1] != null)
										sum += values[j*2+1];
									String hour = dayStr+String.format("%02d", j);
									AlarmMonth m = tmp.get(hour);
									if(m == null)
										tmp.put(hour, new AlarmMonth(hour,sum));
									else
										m.setNumFloat(m.getNumFloat()+sum);
								}
							}
						}
					} catch (Exception ex) {
						logger.error("error ",ex);
					}
			}
			
			if(periodType == MONTH_PERIOD)
				result.put(GLOBAL_METER_ID+"_总电量",AlarmService.fixResult(timeRange == null ? timeStart : timeRange[0],tmp));
			else if(periodType == WEEK_PERIOD)
				result.put(GLOBAL_METER_ID+"_总电量",AlarmService.fixResultW(timeRange,tmp));
			else if(periodType == DAY_PERIOD)
				result.put(GLOBAL_METER_ID+"_总电量",AlarmService.fixResult(timeRange,tmp));
			else if(periodType == HOUR_PERIOD)
				result.put(GLOBAL_METER_ID+"_总电量",AlarmService.fixResultDaily(timeRange,tmp));
		}
		tmp = null;
		
		if(periodType == MONTH_PERIOD || periodType == DAY_PERIOD)
		{
			sql = "select vi.descriptiondevice,vi.descriptionsubdevice,vi.idvariable,to_char(day,'"+format_str+"') as month ,sum(data.total)::int as cnt from vwenergysubdevices as vi "+
					"inner join hsconsumption as data on vi.kidsupervisor=data.kidsupervisor and vi.idvariable=data.kidvariable "+
					"where vi.kidsupervisor=? ";
			if(idMeterVariable != AlarmService.NO_VARIABLE)
				sql += "and vi.idvariable=? ";
			if(timeRange == null)
				sql += "and day>='"+DateTool.format(timeStart)+"' ";
			else
				sql += " and day>='"+DateTool.format(timeRange[0])+"' and day<'"+DateTool.format(timeRange[1])+"' ";
			sql += "group by vi.descriptiondevice,vi.descriptionsubdevice,vi.idvariable,month "+
					"order by vi.descriptiondevice,vi.descriptionsubdevice,vi.idvariable,month ";
			
			params = new Object[]{idsite};
			
			try {
				RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
				if(rs != null && rs.size()>0)
				{
					String meterTmp = null;
					int idTmp = -1;
					for(int i=0;i<rs.size();i++)
					{
						Record r = rs.get(i);
						String descriptiondevice = (String)r.get("descriptiondevice");
						String descriptionsubdevice = (String)r.get("descriptionsubdevice");
						String meter = (descriptionsubdevice==null ||descriptionsubdevice.length()==0)?descriptiondevice:descriptionsubdevice;
						int idvariable = (int)r.get("idvariable");
						String month = (String)r.get("month");
						int cnt = 0;
						if(r.get("cnt") != null)
							cnt = (int)r.get("cnt");
						if(meterTmp == null || !meterTmp.equals(meter))
						{
							if(tmp != null)
							{
								String key = idvariable+"_"+meterTmp;
								if(periodType == EnergyService.MONTH_PERIOD)
									result.put(key, AlarmService.fixResult(timeRange == null ? timeStart : timeRange[0],tmp));
								else if(periodType == EnergyService.DAY_PERIOD)
									result.put(key, AlarmService.fixResult(timeRange,tmp));
								else if(periodType == EnergyService.HOUR_PERIOD)
									result.put(key, AlarmService.fixResultDaily(timeRange,tmp));
							}
							tmp = new HashMap<String,AlarmMonth>();
						}
						tmp.put(month,new AlarmMonth(month,cnt));
						meterTmp = meter;
						idTmp = idvariable;
					}
					if(tmp != null)
					{
						String key = idTmp+"_"+meterTmp;
						if(periodType == EnergyService.MONTH_PERIOD)
							result.put(key, AlarmService.fixResult(timeRange == null ? timeStart : timeRange[0],tmp));
						else if(periodType == EnergyService.DAY_PERIOD)
							result.put(key, AlarmService.fixResult(timeRange,tmp));
						else if(periodType == EnergyService.HOUR_PERIOD)
							result.put(key, AlarmService.fixResultDaily(timeRange,tmp));
					}
				}
			} catch (Exception ex) {
				logger.error("error ",ex);
			}
		}
		else if(periodType == HOUR_PERIOD)
		{
			sql = "select vi.descriptiondevice,vi.descriptionsubdevice,vi.idvariable,data.daydivisions as cnt from vwenergysubdevices as vi "+
					"inner join hsconsumption as data on vi.kidsupervisor=data.kidsupervisor and vi.idvariable=data.kidvariable "+
					"where vi.kidsupervisor=? ";
			if(idMeterVariable != AlarmService.NO_VARIABLE)
				sql += "and vi.idvariable=? ";
			if(timeRange != null)
				sql += " and day>='"+DateTool.format(timeRange[0])+"' and day<'"+DateTool.format(timeRange[1])+"' ";
			
			params = new Object[]{idsite};
			
			try {
				RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
				if(rs != null && rs.size()>0)
				{
					for(int i=0;i<rs.size();i++)
					{
						tmp = new HashMap<String,AlarmMonth>();
						Record r = rs.get(i);
						String descriptiondevice = (String)r.get("descriptiondevice");
						String descriptionsubdevice = (String)r.get("descriptionsubdevice");
						String meter = (descriptionsubdevice==null ||descriptionsubdevice.length()==0)?descriptiondevice:descriptionsubdevice;
						int idvariable = (int)r.get("idvariable");
						Jdbc4Array s = (Jdbc4Array)r.get("cnt");
						Float[] values = (Float[])s.getArray();
						for(int j=0;j<24;j++)
						{
							float sum = 0;
							if(values[j*2] != null)
								sum += values[j*2];
							if(values[j*2+1] != null)
								sum += values[j*2+1];
							String hour = dayStr+String.format("%02d", j);
							tmp.put(hour, new AlarmMonth(hour,sum));
						}
						String key = idvariable+"_"+meter;
						result.put(key, AlarmService.fixResultDaily(timeRange,tmp));
					}
				}
			} catch (Exception ex) {
				logger.error("error ",ex);
			}
		}
		return result;
	}
//	public static List<Integer> getIdList(LinkedHashMap<String,List<AlarmMonth>> map)
//	{
//		List<Integer> result = new ArrayList<Integer>();
//		Iterator it = map.entrySet().iterator();
//		while(it.hasNext())
//		{
//			Entry entry = (Entry) it.next();
//			String key = (String)entry.getKey();
//			int id = Integer.valueOf(key.split("_")[0]);
//			result.add(id);
//		}
//		return result;
//	}
	public static LinkedHashMap<String,List<AlarmMonth>> getTotalMeter(boolean global,LinkedHashMap<String,List<AlarmMonth>> map)
	{
		LinkedHashMap<String,List<AlarmMonth>> result = new LinkedHashMap<String,List<AlarmMonth>>();
		Iterator it = map.entrySet().iterator();
		while(it.hasNext())
		{
			Entry entry = (Entry) it.next();
			String key = (String)entry.getKey();
			int id = Integer.valueOf(key.split("_")[0]);
			if(global)
			{
				if(id == EnergyService.GLOBAL_METER_ID)
				{
					result.put(key, (List<AlarmMonth>)entry.getValue());
					return result;
				}
				else
					return null;
			}
			else
			{
				if(id != GLOBAL_METER_ID)
					result.put(key, (List<AlarmMonth>)entry.getValue());
			}	
		}
		return result;
	}
	public static void main(String[] args)
	{
		//getKWHMonthStatistics(129,MONTH_PERIOD,null);
		getKWHMonthStatistics(129,HOUR_PERIOD,AlarmService.NO_VARIABLE,new Date[]{DateTool.parse("2018-5-1"),DateTool.parse("2018-5-2")} );
	}
}
