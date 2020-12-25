
package watchDog.config.json;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import watchDog.property.template.PropertyConfig;

/**
 * Description:
 * @author Matthew Xu
 * @date May 9, 2020
 */
public abstract class BaseJSONConfig {

	private static final Logger logger = Logger.getLogger(BaseJSONConfig.class);
	
	protected static String basePath = "C:\\watchDog\\conf\\";
	
	protected static PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
	
	public static void refreshConfig(){
		DeviceModelConfig.getConfig();
		UnitConfig.getConfig();
		FaxRuleConfig.getConfig();
		CommunityConfig.getConfig();
		MailConfig.getConfig();
	}
	
	protected static String readFromPath(String path){
    	StringBuilder result = new StringBuilder();
    	try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(path), "UTF-8");
			BufferedReader breader = new BufferedReader(isr);
			String line;
			while((line = breader.readLine()) != null){
				result.append(line);
			}
		} catch (FileNotFoundException e) {
			logger.info("The file doesn't exist:" + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return result.toString();
    }
}
