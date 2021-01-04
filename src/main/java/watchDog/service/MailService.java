
package watchDog.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


import watchDog.bean.SiteInfo;
import watchDog.bean.config.MailDTO;
import watchDog.config.json.MailConfig;
import watchDog.property.template.MailTemplate;
import watchDog.property.template.PropertyConfig;
import watchDog.util.DateTool;
import watchDog.util.MailUtil;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 25, 2020
 */
public class MailService {
	
	private static final Logger LOGGER = Logger.getLogger(MailService.class);
	
	public static final MailService INSTANCE = new MailService();
	
	private MailDTO mailDTO = MailConfig.getMailDTO();
	
	private PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
	
	private static final String HINT_MISSING = "暂未填写";
	
	private MailService(){
	}
	
	public void sendServiceMails(List<SiteInfo> siteInfos){
		try {
			String title = propertyConfig.getValue(MailTemplate.MAIL_OOS_TITLE.getKey());
			String content = propertyConfig.getValue(MailTemplate.MAIL_OOS_CONTENT.getKey());
			for (SiteInfo siteInfo : siteInfos) {
				content += propertyConfig.getValue(MailTemplate.MAIL_OOS_BODY.getKey(),
						new Object[]{siteInfo.getDescription()
								, StringUtils.isNotBlank(siteInfo.getCusDescription()) ? siteInfo.getCusDescription() : HINT_MISSING
								, StringUtils.isNotBlank(siteInfo.getManDescription()) ? siteInfo.getManDescription() : HINT_MISSING
								, DateTool.format(siteInfo.getDeadline())});
			}
			
			MailUtil.sendMail(mailDTO, title, content);
		} catch (Exception e) {
			LOGGER.error("" ,e);
		}
	}
	
}
