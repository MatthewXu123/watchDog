
package watchDog.property.template;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 28, 2020
 */
public class PropertyConfig {

	private static final Logger logger = Logger.getLogger(PropertyConfig.class); 
	
	public static final PropertyConfig INSTANCE = new PropertyConfig();

	private Properties prop = new Properties();

	private static final String PATH_WECHAT_MSG_TEMPLATE = "templateProperties/WechatMsgTemplate.properties";
	
	private static final String PATH_LOG_TEMPLATE = "templateProperties/LogTemplate.properties";
	
	private static final String PATH_MAIL_TEMPLATE = "templateProperties/MailTemplate.properties";
	
	private static final String PATH_DANFOSS_SQL = "templateProperties/DanfossSQL.properties";
	
	private static final String PATH_DANFOSS_XML_QUERY = "templateProperties/DanfossXMLQuery.properties";
	
	private static final String PATH_DANFOSS_NOTIFICATION = "templateProperties/DanfossNotification.properties";
	
	private PropertyConfig(){};

	{
		try {
			String[] paths = new String[]{PATH_WECHAT_MSG_TEMPLATE
					, PATH_LOG_TEMPLATE
					, PATH_MAIL_TEMPLATE
					, PATH_DANFOSS_SQL
					, PATH_DANFOSS_XML_QUERY
					, PATH_DANFOSS_NOTIFICATION};
			for (String path : paths)
				prop.load(new InputStreamReader(PropertyConfig.class.getClassLoader().getResourceAsStream(path), "UTF-8"));       
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	public String getValue(String key, Object[] params){
		String value = prop.getProperty(key);
		return MessageFormat.format(value, params);
	}
	
	public String getValue(String key){
		return prop.getProperty(key);
	}
}
