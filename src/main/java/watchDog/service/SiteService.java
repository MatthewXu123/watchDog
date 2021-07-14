package watchDog.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.apache.log4j.Logger;


import watchDog.bean.SiteInfo;
import watchDog.bean.config.MailDTO;
import watchDog.config.json.MailConfig;
import watchDog.dao.SiteInfoDAO;
import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.RecordSet;
import watchDog.property.template.MailTemplate;
import watchDog.property.template.PropertyConfig;
import watchDog.util.DateTool;
import watchDog.util.MailUtil;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;
import watchDog.wechat.util.sender.Sender;

public class SiteService {
   
    private static final Logger LOGGER = Logger.getLogger(SiteService.class);
    
	public static final SiteService INSTANCE = new SiteService();
	
	private MailDTO mailDTO = MailConfig.getSitesOutOfServiceMailConfig();
	
	private PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
	
	private static final String HINT_MISSING = "暂未填写";
	
	 //buy_202001:1
    //202001: buy at 2020-01
    //1: one year
    public static final String BUY_YEAR_TAG = "buy_";
    public static final String CST_TAG = "cst_";
	
    
    public static String getTag(SiteInfo siteInfo,String tag)
    {
        try{
            if(siteInfo == null || siteInfo.getSupervisorTags() == null || siteInfo.getSupervisorTags().length == 0)
                return null;
            for(String t:siteInfo.getSupervisorTags())
            {
                if(t.startsWith(tag))
                    return t.replace(tag, "");
            }
        }catch(Exception ex)
        {
            LOGGER.error("",ex);
        }
        return null;
    }
    public static void commissioning(SiteInfo s)
    {
        try{
            if(s != null)
            {
                SiteInfoDAO.INSTANCE.updateCheckingNetwork(s.getSupervisorId() , true);
                s.setCheckNetwork(true);
                String year = getTag(s,BUY_YEAR_TAG);
                String cst_date = getTag(s,CST_TAG);
                if(StringUtils.isBlank(cst_date))
                {
                    addSiteTag(s.getSupervisorId(),CST_TAG+DateTool.format(new Date()));
                    if(s.getDeadline() == null && year != null)
                    {
                        try{
                            Date date = DateTool.getMonthLastDay();
                            date = DateTool.add(date, Integer.valueOf(year),Calendar.YEAR );
                            SiteInfoDAO.INSTANCE.updateDeadline(s.getSupervisorId(), date);
                        }catch(Exception ex)
                        {
                            
                        }
                    }
                    else if(s.getDeadline() == null && year == null)
                    {
                        Sender wx = Sender.getInstance();
                        WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
                        WechatMsg wechatMsg = new WechatMsg.Builder(s.getDescription()+"["+s.getIp()+"] 没有年份信息"
                                , configStorage.getCallingMsgAgentId()).userIds(new String[]{"nemoge"}).build();
                        wx.sendIM(wechatMsg);
                    }
                }
            }
        }catch(Exception ex)
        {
            LOGGER.error("",ex);
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
    /**
     * 
     * Description:
     * @return
     * @author Matthew Xu
     * @date Dec 25, 2020
     */
    public static Map<Integer, List<SiteInfo>> getSitesOutOfService(){
    	// The list including sites already out of service.
    	List<SiteInfo> list0 = new ArrayList<>();
    	// The list including sites out of service in one month.
        List<SiteInfo> list1 = new ArrayList<>();
        // The list including sites out of service in two months.
        List<SiteInfo> list2 = new ArrayList<>();
        Map<Integer, List<SiteInfo>> map = new HashMap<>();
        try {
            List<SiteInfo> infosWithTags = SiteInfoDAO.INSTANCE.getList(true);
            infosWithTags = infosWithTags
            		.stream()
            		.filter(x -> x.getDeadline() != null).collect(Collectors.toList());
            sortSitesOutOfService(infosWithTags);
            for (SiteInfo siteInfo : infosWithTags) {
                if(siteInfo.getDeadline() != null){
                    int diff = DateTool.diffMonths(siteInfo.getDeadline(), new Date());
                    if(diff <= 0)
                    	list0.add(siteInfo);
                    if(0 < diff && diff <= 1)
                    	list1.add(siteInfo);
                    if(1 < diff && diff <= 2)
                    	list2.add(siteInfo);
                }
            }
        } catch (Exception e) {
            LOGGER.error("" ,e);
        }
        map.put(0, list0);
        map.put(1, list1);
        map.put(2, list2);
        return map;
    }
    
    private static void sortSitesOutOfService(List<SiteInfo> siteInfos){
    	if(ObjectUtils.isCollectionNotEmpty(siteInfos)){
            Collections.sort(siteInfos, new Comparator<SiteInfo>() {
                @Override
                public int compare(SiteInfo o1, SiteInfo o2) {
                    return  (int)DateTool.diffDays(o1.getDeadline(), o2.getDeadline());
                }
            });
        }
    }
    
    public void sendServiceMails(){
		try {
			Map<Integer, List<SiteInfo>> sitesOutOfService = SiteService.getSitesOutOfService();
			List<SiteInfo> list0 = sitesOutOfService.get(0);
			List<SiteInfo> list1 = sitesOutOfService.get(1);
			List<SiteInfo> list2 = sitesOutOfService.get(2);
			if(ObjectUtils.isCollectionNotEmpty(list1) || ObjectUtils.isCollectionNotEmpty(list2)){
				String title = propertyConfig.getValue(MailTemplate.MAIL_OOS_TITLE.getKey());
				String content = "";
				if(ObjectUtils.isCollectionNotEmpty(list1))
					content += getMailContent(1, list1);
				if(ObjectUtils.isCollectionNotEmpty(list2))
					content += getMailContent(2, list2);
				if(ObjectUtils.isCollectionNotEmpty(list0))
					content += getMailContent(0, list0);
				MailUtil.sendMail(mailDTO, title, content);
			}else{
				LOGGER.info(propertyConfig.getValue(MailTemplate.MAIL_OOS_EMPTY.getKey()));
			}
			
		} catch (Exception e) {
			LOGGER.error("" ,e);
		}
	}
	
	/**
	 * 
	 * Description:
	 * @param month
	 * @param list
	 * @return
	 * @author Matthew Xu
	 * @date Jan 18, 2021
	 */
	private String getMailContent(int month, List<SiteInfo> list){
		String content = month == 0 ? propertyConfig.getValue(MailTemplate.MAIL_OOS_CONTENT_ALREADY.getKey())
				: propertyConfig.getValue(MailTemplate.MAIL_OOS_CONTENT.getKey(), new Object[]{month});
		for (SiteInfo siteInfo : list) {
			content += propertyConfig.getValue(MailTemplate.MAIL_OOS_BODY.getKey(),
					new Object[]{siteInfo.getDescription()
							, StringUtils.isNotBlank(siteInfo.getCusDescription()) ? siteInfo.getCusDescription() : HINT_MISSING
							, StringUtils.isNotBlank(siteInfo.getManDescription()) ? siteInfo.getManDescription() : HINT_MISSING
							, DateTool.format(siteInfo.getDeadline())});
		}
		return content;
	}
    
}
