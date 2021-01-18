
package watchDog.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import watchDog.bean.SiteInfo;
import watchDog.bean.config.MailDTO;
import watchDog.config.json.MailConfig;
import watchDog.property.template.MailTemplate;
import watchDog.property.template.PropertyConfig;
import watchDog.util.DateTool;
import watchDog.util.MailUtil;
import watchDog.util.ObjectUtils;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 25, 2020
 */
public class MailService{
	
	private static final Logger LOGGER = Logger.getLogger(MailService.class);
	
	public static final MailService INSTANCE = new MailService();
	
	private MailDTO mailDTO = MailConfig.getMailDTO();
	
	private PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
	
	private static final String HINT_MISSING = "暂未填写";
	
	private MailService(){
	}
	
	public void sendServiceMails(){
		try {
			Map<Integer, List<SiteInfo>> sitesOutOfService = SiteInfoService.getSitesOutOfService();
			List<SiteInfo> list1 = sitesOutOfService.get(1);
			List<SiteInfo> list2 = sitesOutOfService.get(2);
			if(ObjectUtils.isCollectionNotEmpty(list1) || ObjectUtils.isCollectionNotEmpty(list2)){
				String title = propertyConfig.getValue(MailTemplate.MAIL_OOS_TITLE.getKey());
				String content = "";
				if(ObjectUtils.isCollectionNotEmpty(list1))
					content += getMailContent(1, list1);
				if(ObjectUtils.isCollectionNotEmpty(list2))
					content += getMailContent(2, list2);
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
		String content = propertyConfig.getValue(MailTemplate.MAIL_OOS_CONTENT.getKey(), new Object[]{month});
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
