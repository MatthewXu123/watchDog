package watchDog.service;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.postgresql.jdbc4.Jdbc4Array;

import watchDog.bean.SiteInfo;
import watchDog.dao.SimpleCallingDAO;
import watchDog.database.Record;
import watchDog.listener.Dog;
import watchDog.property.template.FaxMsgLogTemplate;
import watchDog.property.template.PropertyConfig;
import watchDog.thread.AlarmNotificationMain;
import watchDog.thread.WechatApplicationThread;
import watchDog.util.DateTool;
import watchDog.util.HttpSendUtil;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.bean.WechatUser;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;
import watchDog.wechat.util.sender.Sender;

public class SimpleCallingService {
    private static final Logger logger = Logger.getLogger(SimpleCallingService.class);
    private static final String CODE_NO1 = "NO1";
    private static final String CODE_NO2 = "NO2";
    private static final String CODE_NO3 = "NO3";
    private static final String CODE_NO4 = "NO4";
    private static final String[] CODES = {CODE_NO1,CODE_NO2,CODE_NO3,CODE_NO4};
    
    public static final String[] PRACK_CALL_VARCODE = {"Al_Com_High_Press_L1","Al_High_Cond_Press_L1","AL_Com_Phase_Error_L1","AL_High_Suct_Press"};
    private static SimpleCallingService me = new SimpleCallingService();
    private Date lastRepeatTime = null;
    private Date lastRunDate = null;
    public static SimpleCallingService getInstance()
    {
        return me;
    }
    public void start()
    {
    	deleteResetAlarm();
    	boolean canRepeat = canRepeat();
    	Date now = new Date();
    	
        String[] idSites = getSitesWithCallingUser();
        if(ObjectUtils.isArrayNotEmpty(idSites)){
        	Map<WechatUser,String> willCallServerMap = new HashMap<>();
            Map<CallingLogKey,CallingLogValue> callingLogMap = SimpleCallingDAO.getCallingLog();
            for(String CODE:CODES)
            {
                try{
                    Method method = SimpleCallingDAO.class.getMethod("getAlarm"+CODE,String[].class, String.class);
                    List<SimpleAlarm> alarms = (List<SimpleAlarm>)method.invoke(null, idSites, CODE);
                    filter(CODE,willCallServerMap,alarms,callingLogMap,getNum(CODE));
                }catch(Exception ex){
                    logger.error("",ex);
                }
            }
            handleWillCallServerMap(willCallServerMap);
        }
        
        if(canRepeat)
            this.lastRepeatTime = now;
        this.lastRunDate = now;
    }
    private void handleWillCallServerMap(Map<WechatUser,String> willCallServerMap)
    {
        if(willCallServerMap != null)
        {
            Iterator it = willCallServerMap.entrySet().iterator();
            while(it.hasNext())
            {
                try{
                    Entry<WechatUser,String> en = (Entry)it.next();
                    WechatUser m = en.getKey();
                    String content = en.getValue();
                    String url = FaxInfoService.FAX_CALL_REQUEST_URL
                      .replace(FaxInfoService.USERNAME, URLEncoder.encode(m.getName(), "utf-8"))
                      .replace(FaxInfoService.USER_MOBILE, m.getMobile())
                      .replace(FaxInfoService.ALARM_DESCRIPTION, URLEncoder.encode(content, "utf-8"))
                      .replace(FaxInfoService.ENCRYPT_CONTENT, URLEncoder.encode(FaxInfoService.getEncryptContent(), "utf-8"))
                      + FaxInfoService.PARAMETER_IMMEDIATE;
                    WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
                    if(StringUtils.isBlank(configStorage.getDebug()))
                    {
                        logger.info(url);
                        String result = HttpSendUtil.INSTANCE.sendGet(new String(url.getBytes("iso-8859-1"), "utf-8"),
                              HttpSendUtil.CHAR_ENCODING_UTF8);  
                    }
                }catch(Exception ex)
                {
                    logger.error("",ex);
                }
            }
        }
    }
    private void filter(String CODE,Map<WechatUser,String> willCallServerMap,List<SimpleAlarm> alarms,Map<CallingLogKey,CallingLogValue> callingLogMap,int num)
    {
        if(alarms == null)
            return;
        boolean canRepeat = canRepeat();
        for(SimpleAlarm a:alarms)
        {
            processNew(CODE,willCallServerMap,a,callingLogMap,num);
            if(canRepeat)
                processRepeat(CODE,willCallServerMap,a,callingLogMap,num);
        }
    }
    private boolean canRepeat()
    {
        if(lastRepeatTime == null)
            return true;
        if(DateTool.diff(new Date(), lastRepeatTime)>=10*60*1000)
            return true;
        return false;
    }
    private void deleteResetAlarm()
    {
        if(this.lastRunDate == null || !DateTool.isSameDay(this.lastRunDate, new Date()))
            SimpleCallingDAO.deleteCallingLog();
    }
    private void processNew(String CODE,Map<WechatUser,String> callServerMap,SimpleAlarm alarm,Map<CallingLogKey,CallingLogValue> callingLogMap,int num)
    {
        List<Integer> index = new ArrayList<>();
        for(int i=0;i<alarm.getIdAlarms().length;i++)
        {
            //filtered
            if(alarm.getIdAlarms()[i] == null)
                continue;
            int idAlarm = alarm.getIdAlarms()[i];
            CallingLogKey key = new CallingLogKey(alarm.getIdSupervisor(),idAlarm);
            if(!callingLogMap.containsKey(key))
                index.add(i);
        }
        if(index.size()>=num)
        {
            SiteInfo site = Dog.getInstance().getSiteInfoByIdSite(alarm.getIdSupervisor());
            WechatApplicationThread wat = Dog.getInstance().getWechatApplicationThread();
            List<WechatUser> mList = new ArrayList<>();
            List<WechatUser> t1 = wat.getMessageReceiver(site.getTagId(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_1);
            List<WechatUser> t2 = wat.getMessageReceiver(site.getTagId2(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_1);
            List<WechatUser> t3 = wat.getMessageReceiver(site.getSupervisorId(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_1);
            if(t1 != null)
                mList.addAll(t1);
            if(t2 != null)
                mList.addAll(t2);
            if(t3 != null)
                mList.addAll(t3);
            if(mList.size()>0)
            {
                PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
                
                String alarmDescription = getAlarmInfo(CODE,alarm.getDeviceDescriptions(),alarm.getVarcodes(),alarm.getVarDescriptions(),alarm.getStartTimes(),index);
                String receivers = getName(mList);
                String link = AlarmNotificationMain.alarmSurroundURL(site.getSupervisorId(), "查看报警");
                String cancel = AlarmNotificationMain.cancelCallingSurroundURL();
                //mm_simle_calling_new=新报警☎\n门店：{0}\n{1}\n设备：{2}提醒：{3}将会收到电话{4}
                String msg = propertyConfig.getValue(FaxMsgLogTemplate.FM_SIMPLE_CALLING_NEW.getKey(),
                        new Object[] { site.getDescription(),
                                alarmDescription,
                                receivers, 
                                link,
                                cancel}
                );
                String serverMsg = "New,"+
                                site.getDescription()+","+
                                alarmDescription.replaceAll("\n", ",");
                for (WechatUser m : mList) 
                {
                    appendMessage(callServerMap,m,serverMsg);
                    sendIM(m,serverMsg,msg);
                }
            }
            insertCallingLog(null,alarm.getIdSupervisor(),alarm.getIdAlarms(),index);
        }
    }
    private void processRepeat(String CODE,Map<WechatUser,String> callServerMap,SimpleAlarm alarm,Map<CallingLogKey,CallingLogValue> callingLogMap,int num)
    {
        List<Integer> index = new ArrayList<>();
        //repeatType
        //1: sodier repeat
        //2: officer repeat
        //3: general repeat
        for(int repeatType=1;repeatType<=3;repeatType++)
        {
            boolean called = false;
            for(int i=0;i<alarm.getIdAlarms().length;i++)
            {
                //filtered
                if(alarm.getIdAlarms()[i] == null)
                    continue;
                int idAlarm = alarm.getIdAlarms()[i];
                Date startTime = alarm.getStartTimes()[i];
                CallingLogKey key = new CallingLogKey(alarm.getIdSupervisor(),idAlarm);
                CallingLogValue v = callingLogMap.get(key);
                if(v != null)
                {
                    if(v.getCount()<=repeatType && DateTool.diff(new Date(), startTime)>=60*60*1000*repeatType)
                        index.add(i);
                }
            }
            if(index.size()>=num)
            {
                called = true;
                SiteInfo site = Dog.getInstance().getSiteInfoByIdSite(alarm.getIdSupervisor());
                List<WechatUser> mList = getRepeatWechatMemberList(site,repeatType);
                if(mList.size()>0)
                {
                    PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
                    String alarmDescription = getAlarmInfo(CODE,alarm.getDeviceDescriptions(),alarm.getVarcodes(),alarm.getVarDescriptions(),alarm.getStartTimes(),index);
                    //String CODE,String[] deviceDescription,String[] varcodes,String[] varDescription,Date[] startTime,List<Integer> index
                    String receivers = getName(mList);
                    String link = AlarmNotificationMain.alarmSurroundURL(site.getSupervisorId(), "查看报警");
                    String cancel = AlarmNotificationMain.cancelCallingSurroundURL();
                    //mm_simle_calling_new=新报警☎\n门店：{0}\n{1}\n设备：{2}提醒：{3}将会收到电话{4}
                    String msg = propertyConfig.getValue(FaxMsgLogTemplate.FM_SIMPLE_CALLING_REPEAT.getKey(),
                        new Object[] { 
                              repeatType,
                              site.getDescription(),
                              alarmDescription,
                              receivers, 
                              link,
                              cancel}
                    );
                    String serverMsg = 
                            "RP,"+
                            site.getDescription()+","+
                            alarmDescription.replaceAll("\n", ",");
                    for (WechatUser m : mList) 
                    {
                        appendMessage(callServerMap,m,serverMsg);
                        sendIM(m,serverMsg,msg);
                    }
                }
                insertCallingLog(callingLogMap,alarm.getIdSupervisor(),alarm.getIdAlarms(),index);
            }
            //why called == true, break?
            //each time, notify only one level
            //if it is very old alarms, will continue to notify level1, level2, level3 at 3 timer runnings, but not one timer running
            if(called)
                break;
        }
    }
    private List<WechatUser> getRepeatWechatMemberList(SiteInfo site,int repeatType)
    {
        WechatApplicationThread wat = Dog.getInstance().getWechatApplicationThread();
        List<WechatUser> mList = new ArrayList<>();
        //1: officer repeat
        //2: general repeat
        if(repeatType == 1 || repeatType == 2)
        {
            List<WechatUser> t1 = wat.getMessageReceiver(site.getTagId(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_2);
            if(t1 != null)
                mList.addAll(t1);
            List<WechatUser> t2 = wat.getMessageReceiver(site.getTagId2(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_2);
            if(t2 != null)
                mList.addAll(t2);
            List<WechatUser> t3 = wat.getMessageReceiver(site.getTagId3(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_2);
            if(t3 != null)
                mList.addAll(t3);
            //users from tag
            List<WechatUser> t4 = wat.getMessageReceiver(site.getSupervisorId(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_2);
            if(t4 != null)
                mList.addAll(t4);
        }
        //3: general repeat
        else if(repeatType == 3)
        {
            List<WechatUser> t1 = wat.getMessageReceiver(site.getTagId(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_3);
            if(t1 != null)
                mList.addAll(t1);
            List<WechatUser> t2 = wat.getMessageReceiver(site.getTagId2(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_3);
            if(t2 != null)
                mList.addAll(t2);
            List<WechatUser> t3 = wat.getMessageReceiver(site.getTagId3(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_3);
            if(t3 != null)
                mList.addAll(t3);
            //users from tag
            List<WechatUser> t4 = wat.getMessageReceiver(site.getSupervisorId(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_3);
            if(t4 != null)
                mList.addAll(t4);
            boolean contains_cg = false;
            for(WechatUser m:mList)
            {
                if(m.getName().endsWith(WechatApplicationThread.SIMPLE_CALLING_SUFFIX_GENERAL))
                {
                    contains_cg = true;
                    break;
                }
            }
            //at 3rd repeat, call only when there is call_general 
            if(!contains_cg)
                mList.clear();
        }
        return mList;
    }
    private String getName(List<WechatUser> mList)
    {
        String result = "";
        if(mList == null)
            return result;
        for(WechatUser m:mList)
        {
            if(StringUtils.isBlank(result))
                result += m.getName();
            else
                result += ","+m.getName();
        }
        return result;
    }
    private String getAlarmInfo(String CODE,String[] deviceDescription,String[] varcodes,String[] varDescription,Date[] startTime,List<Integer> index)
    {
        String result = "";
        for(int i:index)
        {
            result += "\n设备:"+deviceDescription[i];
            result += "\n报警:"+varDescription[i];
            if(CODE.equals(CODE_NO2))
            {
                String varCode = varcodes[i];
                if(varCode == PRACK_CALL_VARCODE[0])
                {
                    result += "\n建议:检查高压开关，检查冷凝器";
                }
                else if(varCode == PRACK_CALL_VARCODE[1])
                {
                    result += "\n建议:检查冷凝器";
                }
                else if(varCode == PRACK_CALL_VARCODE[2])
                {
                    result += "\n建议:检查机组供电电源";
                }
                else if(varCode == PRACK_CALL_VARCODE[3])
                {
                    result += "\n建议:请检查压缩机运行情况";
                }
            }
            result += "\n开始:"+DateTool.msgTime(startTime[i]);
        }
        return result;
    }
    private void insertCallingLog(Map<CallingLogKey,CallingLogValue> callingLogMap,int idSupervisor,Integer[] idAlarms,List<Integer> index)
    {
        for(int i:index)
        {
            if(idAlarms[i] == null)
                continue;
            int idAlarm = idAlarms[i];
            SimpleCallingDAO.insertCallingTog(idSupervisor, idAlarm);
            if(callingLogMap != null)
            {
                CallingLogKey key = new CallingLogKey(idSupervisor,idAlarm);
                CallingLogValue v = callingLogMap.get(key);
                if(v != null)
                {
                    v.addCount();
                }
            }
        }
    }
    public void sendIM(WechatUser wechatMember,String callServerContent,String wechatContent)
    {
        try{
            Sender sender = Sender.getInstance();
//            String url = FaxInfoService.FAX_CALL_REQUEST_URL
//                    .replace(FaxInfoService.USERNAME, URLEncoder.encode(wechatMember.getName(), "utf-8"))
//                    .replace(FaxInfoService.USER_MOBILE, wechatMember.getMobile())
//                    .replace(FaxInfoService.ALARM_DESCRIPTION, URLEncoder.encode(callServerContent, "utf-8"))
//                    .replace(FaxInfoService.ENCRYPT_CONTENT, URLEncoder.encode(FaxInfoService.getEncryptContent(), "utf-8"))
//                    + (immediately?FaxInfoService.PARAMETER_IMMEDIATE:"");
            WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
            if(StringUtils.isBlank(configStorage.getDebug()))
            {
//                String result = HttpSendUtil.INSTANCE.sendGet(new String(url.getBytes("iso-8859-1"), "utf-8"),
//                        HttpSendUtil.CHAR_ENCODING_UTF8);
                sender.sendIM(new WechatMsg.Builder(wechatContent, configStorage.getCallingMsgAgentId())
                		.userIds(new String[] { wechatMember.getUserid() }).build());
            }
        }catch(Exception ex)
        {
            logger.error("",ex);
        }
    }
    private void appendMessage(Map<WechatUser,String> callServerMap,WechatUser wechatMember,String callServerContent)
    {
        String msg = "";
        if(callServerMap.containsKey(wechatMember))
        {
            msg = callServerMap.get(wechatMember);
            msg += ";"+callServerContent;
        }
        else
            msg = callServerContent;
        callServerMap.put(wechatMember, msg);
    }
    private int getNum(String CODE)
    {
        switch(CODE)
        {
            case CODE_NO1:
               if(DateTool.isNowInPeriods(22, 7))
                    return 3;
                else
                    return 1;
            case   CODE_NO2:
                return 1;
                default:
                    return 1;
        }
    }
//    private Integer[] newArray(Integer[] array,List<Integer> index)
//    {
//        Integer[] result = new Integer[index.size()];
//        for(int i=0;i<index.size();i++)
//        {
//            result[i] = array[index.get(i)];
//        }
//        return result;
//    }
//    private static String[] newArray(String[] array,List<Integer> index)
//    {
//        String[] result = new String[index.size()];
//        for(int i=0;i<index.size();i++)
//        {
//            result[i] = array[index.get(i)];
//        }
//        return result;
//    }
    private String[] getSitesWithCallingUser()
    {
        List<String> result = new ArrayList<>();
        WechatApplicationThread wat = Dog.getInstance().getWechatApplicationThread();
        Iterator sites = Dog.getInstance().getAllSites();
        if(sites != null)
        {
            while(sites.hasNext())
            {
                Entry<String,SiteInfo> en = (Entry)sites.next();
                SiteInfo site = en.getValue();
                //Kevin
                //StringTool.isInArray(site.getSupervisorTags(), SIMPLE_CALLING_TAG) && !StringTool.isInArray(site.getSupervisorTags(), IGNORE_SIMPLECALLING_TAG)
                if(true)
                {
                    List<WechatUser> t1 = wat.getMessageReceiver(site.getTagId(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_3);
                    List<WechatUser> t2 = wat.getMessageReceiver(site.getTagId2(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_3);
                    List<WechatUser> t3 = wat.getMessageReceiver(site.getTagId3(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_3);
                    List<WechatUser> t4 = wat.getMessageReceiver(site.getSupervisorId(), WechatApplicationThread.SIMPLE_CALLING_SUFFIX_3);
                    if((t1 != null && t1.size()>0) || (t2 != null && t2.size()>0) || (t3 != null && t3.size()>0)|| (t4 != null && t4.size()>0))
                        result.add(site.getSupervisorId()+"");
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }
    public class SimpleAlarm 
    {
        private int idSupervisor = 0;
        private Integer[] idAlarms = null;
        private String[] varcodes = null;
        private String[] varDescriptions = null;
        private String[] deviceDescriptions = null;
        private Date[] startTimes = null;
        private String[] deviceTags = null;
        //private String CODE = null;
        
        public SimpleAlarm(Record r) throws SQLException
        {
            this.idSupervisor = (int)r.get("idsupervisor");
            Jdbc4Array a = (Jdbc4Array)r.get("idalarms");
            if(a != null)
                idAlarms = (Integer[])a.getArray();
            a = (Jdbc4Array)r.get("varcodes");
            if(a != null)
                varcodes = (String[])a.getArray();
            a = (Jdbc4Array)r.get("var_descriptions");
            if(a != null)
                varDescriptions = (String[])a.getArray();
            a = (Jdbc4Array)r.get("device_descriptions");
            if(a != null)
                this.deviceDescriptions = (String[])a.getArray();
            a = (Jdbc4Array)r.get("starttimes");
            if(a != null)
                this.startTimes = (Date[])a.getArray();
            a = (Jdbc4Array)r.get("device_tags");
            if(a != null)
                this.deviceTags = (String[])a.getArray();
        }
        public int getIdSupervisor() {
            return idSupervisor;
        }
        
        public Integer[] getIdAlarms() {
            return idAlarms;
        }
        public String[] getVarcodes() {
            return varcodes;
        }
        public String[] getDeviceDescriptions() {
            return deviceDescriptions;
        }
//        public String getCODE() {
//            return CODE;
//        }
        public void setIdSupervisor(int idSupervisor) {
            this.idSupervisor = idSupervisor;
        }
        public void setIdAlarms(Integer[] idAlarms) {
            this.idAlarms = idAlarms;
        }
        public void setVarcodes(String[] varcodes) {
            this.varcodes = varcodes;
        }
        public void setDeviceDescriptions(String[] deviceDescriptions) {
            this.deviceDescriptions = deviceDescriptions;
        }
        public Date[] getStartTimes() {
            return startTimes;
        }
        public String[] getDeviceTags() {
            return deviceTags;
        }
        public String[] getVarDescriptions() {
            return varDescriptions;
        }
        
    }
    public class CallingLogValue
    {
        private int count = 0;
        private Date lastCallTime = null;
        public CallingLogValue(Record r)
        {
            this.count = (int)r.get("count");
            this.lastCallTime = (Date)r.get("last_call_time");
        }
        public int getCount() {
            return count;
        }
        public void addCount()
        {
            count++;
        }
        public Date getLastCallTime() {
            return lastCallTime;
        }
        
    }
    public class CallingLogKey
    {
        private int idSupervisor = 0;
        private int idAlarm = 0;
        
        public CallingLogKey(int idSupervisor, int idAlarm) {
            super();
            this.idSupervisor = idSupervisor;
            this.idAlarm = idAlarm;
        }
        public CallingLogKey(Record r)
        {
            this.idSupervisor = (int)r.get("idsupervisor");
            this.idAlarm = (int)r.get("idalarm");
        }
        public int getIdSupervisor() {
            return idSupervisor;
        }
        public int getIdAlarm() {
            return idAlarm;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + idAlarm;
            result = prime * result + idSupervisor;
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CallingLogKey other = (CallingLogKey) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (idAlarm != other.idAlarm)
                return false;
            if (idSupervisor != other.idSupervisor)
                return false;
            return true;
        }
        private SimpleCallingService getOuterType() {
            return SimpleCallingService.this;
        }
        
    }
    
}
