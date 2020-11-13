package watchDog.service;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import watchDog.bean.SiteInfo;
import watchDog.dao.SiteInfoDAO;
import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.RecordSet;
import watchDog.listener.Dog;
import watchDog.util.DateTool;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;
import watchDog.wechat.util.sender.Sender;

public class SiteInfoService {
    //buy_202001:1
    //202001: buy at 2020-01
    //1: one year
    public static final String BUY_YEAR_TAG = "buy_";
    public static final String START_TAG = "start:";
    public static String getTag(SiteInfo siteInfo,String tag)
    {
        if(siteInfo == null || siteInfo.getSupervisorTags() == null || siteInfo.getSupervisorTags().length == 0)
            return null;
        for(String t:siteInfo.getSupervisorTags())
        {
            if(t.startsWith(tag))
                return t.replace(tag, "");
        }
        return null;
    }
    public static void commissioning(SiteInfo s)
    {
        if(s != null)
        {
            SiteInfoDAO.INSTANCE.updateCheckingNetwork(s.getSupervisorId() , true);
            s.setCheckNetwork(true);
            String year = getTag(s,BUY_YEAR_TAG);
            if(s.getDeadline() == null && year != null)
            {
                Date date = DateTool.getFirstDayOfMonth();
                DateTool.add(date, -1, Calendar.DATE);
                DateTool.add(date, 1, Calendar.MONTH);
                DateTool.add(date, 1, Calendar.YEAR);
                SiteInfoDAO.INSTANCE.updateDeadline(s.getSupervisorId(), date);
            }
            else if(s.getDeadline() == null && year == null)
            {
                Sender wx = Sender.getInstance();
                WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
                WechatMsg.Builder b = new WechatMsg.Builder(s.getDescription()+"["+s.getIp()+"] 没有年份信息",configStorage.getCallingMsgAgentId(),new String[]{"nemoge"})
                        .type(Sender.WECHAT_MSG_TYPE_USER);
                wx.sendIM(b.build());
            }
            String start = getTag(s,START_TAG);
            if(StringUtils.isBlank(start))
                addSiteTag(s.getSupervisorId(),START_TAG+DateTool.format(new Date()));
        }
    }
    public static void addSiteTag(int id,String tag)
    {
        boolean exist = false;
        String sql = "select * from tags.supervisortags where kidsupervisor=? ";
        Object[] params = {id};
        try {
            RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql, params);
            if(rs != null && rs.size()>0)
                exist = true;
        
            if(exist)
            {
                sql = "update tags.supervisortags set tags = array_append(tags, '"+tag+"') where kidsupervisor = ?";
                params = new Object[]{id};
                    DatabaseMgr.getInstance().executeUpdate(sql, params);
            }
            else
            {
                sql = "insert into tags.supervisortags values(?,'{\""+tag+"\"}')";
                params = new Object[]{id};
                DatabaseMgr.getInstance().executeUpdate(sql, params);
                
            }
        }
        catch(DataBaseException ex)
        {
            
        }
    }
    @Test
    public void t()
    {
        addSiteTag(142,"a");
    }
}
