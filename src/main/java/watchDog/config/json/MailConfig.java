
package watchDog.config.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import watchDog.bean.config.MailDTO;
import watchDog.util.ObjectUtils;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 25, 2020
 */
public class MailConfig extends BaseJSONConfig{
	
	private static final String DEVMDL_CONFIG_PTAH = "mail.json";
	
	private static final String IDENTIFIER_SITES_OUT_OF_SERVICE = "1";
	
	private static final String IDENTIFIER_DAILY_ALARM = "2";
	
	private static List<MailDTO> mailDTOs;
	
	private static Map<String, MailDTO> identifierMailDTOMap = new HashMap<>();
	
	static {
		if(ObjectUtils.isCollectionEmpty(mailDTOs))
			getConfig();
	}
	
	public static void getConfig() {
		mailDTOs = JSONObject.parseArray(readFromPath(basePath + DEVMDL_CONFIG_PTAH), MailDTO.class);
		for (MailDTO mailDTO : mailDTOs) {
			identifierMailDTOMap.put(mailDTO.getIdentifier(), mailDTO);
		}
	}
	
	private static MailDTO getMailDTOByIdentifer(String identifier){
		return identifierMailDTOMap.get(identifier);
	}
	
	public static MailDTO getSitesOutOfServiceMailConfig(){
		return getMailDTOByIdentifer(IDENTIFIER_SITES_OUT_OF_SERVICE);
	}
	
	public static MailDTO getDailyAlarmMailConfig(){
		return getMailDTOByIdentifer(IDENTIFIER_DAILY_ALARM);
	}

	public static void main(String[] args) {
		System.out.println(getDailyAlarmMailConfig());
	}
	
	

}
