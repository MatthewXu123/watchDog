
package watchDog.config.json;

import com.alibaba.fastjson.JSONObject;

import watchDog.bean.config.MailDTO;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 25, 2020
 */
public class MailConfig extends BaseJSONConfig{
	
	private static final String DEVMDL_CONFIG_PTAH = "mail.json";
	
	private static MailDTO mailDTO;
	
	static {
		if(mailDTO == null)
			getConfig();
	}
	
	public static void getConfig() {
		mailDTO = JSONObject.parseObject(readFromPath(basePath + DEVMDL_CONFIG_PTAH), MailDTO.class);
	}


}
