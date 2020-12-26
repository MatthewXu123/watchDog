
package watchDog.property.template;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
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
	
	private PropertyConfig(){};

	{
		try {
			String[] paths = new String[]{PATH_WECHAT_MSG_TEMPLATE, PATH_LOG_TEMPLATE, PATH_MAIL_TEMPLATE};
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
