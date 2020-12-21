package watchDog.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.Record;
import watchDog.database.RecordSet;
import watchDog.service.SimpleCallingService;
import watchDog.util.DateTool;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;

public class SimpleCallingDAO {
    private static final Logger logger = Logger.getLogger(SimpleCallingDAO.class);
    public static void insertCallingTog(int idSupervisor,int idAlarm)
    {
        String sql = "insert into wechat.calling_log(idsupervisor,idalarm) values(?,?)";
        Object[] params = {idSupervisor,idAlarm};
        try {
            DatabaseMgr.getInstance().executeUpdate(sql, params);
        } catch (DataBaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static Map<SimpleCallingService.CallingLogKey,SimpleCallingService.CallingLogValue> getCallingLog()
    {
        Map<SimpleCallingService.CallingLogKey,SimpleCallingService.CallingLogValue> map = new HashMap<>();
        String sql = ""+
                " select a.idsupervisor,b.idalarm,count,last_call_time from "+
                " ( "+
                " select idsupervisor,idalarm,max(insert_time) as last_call_time from wechat.calling_log "+
                " group by idsupervisor,idalarm "+
                " )as a "+
                " inner join "+
                " ( "+
                " select idsupervisor,idalarm,count(*)::int from wechat.calling_log "+
                " group by idsupervisor,idalarm "+
                " )as b on a.idsupervisor=b.idsupervisor and a.idalarm=b.idalarm ";
        try {
            RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql);
            if(rs != null)
            {
                for(int i=0;i<rs.size();i++)
                {
                    Record r = rs.get(i);
                    
                    SimpleCallingService.CallingLogKey key = SimpleCallingService.getInstance().new CallingLogKey(r);
                    SimpleCallingService.CallingLogValue value = SimpleCallingService.getInstance().new CallingLogValue(r);
                    map.put(key, value);
                }
            }
        } catch (DataBaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }
    public static void deleteCallingLog()
    {
        String sql = ""+
                " delete from wechat.calling_log as c "+
                " where not exists "+
                " (select * from lgalarmactive as a where a.kidsupervisor=c.idsupervisor and a.idalarm=c.idalarm) ";
        try {
            DatabaseMgr.getInstance().executeUpdate(sql);
        } catch (DataBaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //#1, high temperature, LT
    public static List<SimpleCallingService.SimpleAlarm> getAlarmNO1(String[] siteIds,String CODE)
    {
        String condition = ""+
              " and (cfvarmdl.parameter='hightempalarm' or v.description = '高温报警' or v.description like '%高温报警_c_%') "+
              " and (d.description like '%LT%' or d.description like '%低温%' or d.description like '%冷冻%' or d.description like '%LAT%' or tagdevice.tag is not null) "+
              " and (a.starttime+interval '10 minutes'<now() or a.inserttime+interval '10 minutes'<now() )"+
              " and s.id in ("+String.join(",", siteIds)+") ";
        return getAlarm(condition,CODE);
    }
    //#2, pRackCNL1
    public static List<SimpleCallingService.SimpleAlarm> getAlarmNO2(String[] siteIds,String CODE)
    {
        String condition = ""+
              " and d.devmodcode = 'pRackCNL1' "+
              getVarCondition(SimpleCallingService.PRACK_CALL_VARCODE)+
              " and (a.starttime+interval '10 minutes'<now() or a.inserttime+interval '10 minutes'<now() )"+
              " and s.id in ("+String.join(",", siteIds)+") ";
        return getAlarm(condition,CODE);
    }
    //#3 电话报警白天
    public static List<SimpleCallingService.SimpleAlarm> getAlarmNO3(String[] siteIds,String CODE)
    {
        String condition = ""+
              (DateTool.isNowInPeriods(22, 7)?" and false ":" and true ")+
              " and (v.description like '%_d_电话报警') "+
              " and (a.starttime+interval '10 minutes'<now() or a.inserttime+interval '10 minutes'<now() )"+
              " and s.id in ("+String.join(",", siteIds)+") ";
        return getAlarm(condition,CODE);
    }
    //#4 电话报警全天
    public static List<SimpleCallingService.SimpleAlarm> getAlarmNO4(String[] siteIds,String CODE)
    {
        String condition = ""+
              " and (v.description like '%_a_电话报警') "+
              " and (a.starttime+interval '10 minutes'<now() or a.inserttime+interval '10 minutes'<now() )"+
              " and s.id in ("+String.join(",", siteIds)+") ";
        return getAlarm(condition,CODE);
    }
    private static String getVarCondition(String[] varCodes)
    {
        String condition = " and (";
        boolean first = true;
        for(String var :varCodes)
        {
            if(first)
            {
                condition += "v.code='"+var+"' ";
                first = false;
            }
            else
                condition += " or v.code='"+var+"' ";
        }
        condition += ") ";
        return condition;
    }
    public static List<SimpleCallingService.SimpleAlarm> getAlarm(String condition,String CODE)
    {
        WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
        List<SimpleCallingService.SimpleAlarm> result = new ArrayList<>();
        String sql = ""+
            " select s.id as idsupervisor,array_agg(a.idalarm) as idalarms,array_agg(v.code) as varcodes,"+
            "array_agg(d.description) as device_descriptions, array_agg(v.description) as var_descriptions, "+
            "array_agg(a.starttime) as starttimes,array_agg(tagdevice.tag) as device_tags "+
            " from lgalarmactive a  "+
            " inner join cfsupervisors s on a.kidsupervisor = s.id "+
            " inner join lgdevice d on d.kidsupervisor = s.id "+
            " inner join lgvariable v on v.iddevice = d.iddevice and v.idvariable=a.idvariable and v.kidsupervisor = s.id "+
            " left join cfdevmdl e on e.code = d.devmodcode "+
            " left join cfvarmdl on e.id = cfvarmdl.iddevmdl and v.code = cfvarmdl.code "+
            " left join  "+
            " (  "+
            "       SELECT distinct kidsupervisor,iddevice,tag  "+
            "   FROM (  "+
            "       SELECT kidsupervisor,kiddevice as iddevice,unnest(tags) tag  "+
            "       FROM tags.devicestags) x  "+
            "   WHERE lower(tag) LIKE '#c_%'  "+
            " ) as tagdevice on a.kidsupervisor=tagdevice.kidsupervisor and a.iddevice=tagdevice.iddevice "+
            " where a.ackremotetime is null  "+
            (StringUtils.isBlank(configStorage.getDebug())?" and a.starttime+interval '24 hour'>now() ":"")+
            condition+ 
            " group by s.id "+
            " order by s.id";
        try {
            RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql);
            if(rs != null)
            {
                for(int i=0;i<rs.size();i++)
                {
                    Record r = rs.get(i);
                    SimpleCallingService.SimpleAlarm a = SimpleCallingService.getInstance().new SimpleAlarm(r);
                    result.add(a);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(sql);
            logger.error(" ",e);
        }
        return result;
    }
}
