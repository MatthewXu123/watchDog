
package watchDog.danfoss.service;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import watchDog.danfoss.dao.CustomizedEntityManager;
import watchDog.danfoss.service.impl.DeviceServiceImpl;
import watchDog.danfoss.service.impl.XMLQueryServiceImpl;
import watchDog.danfoss.service.impl.SupervisorServiceImpl;
import watchDog.property.template.PropertyConfig;
import watchDog.util.HttpSendUtil;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
public interface BaseService {

	public static final DeviceService DEVICE_SERVICE = DeviceServiceImpl.getInstance();
	
	public static final SupervisorService SUPERVISOR_SERVICE = SupervisorServiceImpl.getInstance();
	
	public static final XMLQueryService XML_QUERY_SERVICE = XMLQueryServiceImpl.getInstance();
	
	public static final CustomizedEntityManager CUSTOMIZED_ENTITY_MANAGER = CustomizedEntityManager.getInstance();
	
	public static final PropertyConfig PROPERTY_CONFIG = PropertyConfig.INSTANCE;
	
	default Document getXMLResult(String ip, String cmd) throws DocumentException, IOException{
		return xmlParse(sendQuery(ip, cmd));
	}
	
	default String sendQuery(String ip, String cmd) throws IOException{
		return HttpSendUtil.INSTANCE.sendPost("http://" + ip + "/danfoss/html/xml.cgi"
				, cmd
				, HttpSendUtil.CHAR_ENCODING_UTF8
				, HttpSendUtil.APPLICATION_XML);
	}
	
	default Document xmlParse(String result) throws DocumentException{
		Document doc = null;
		if(StringUtils.isNotBlank(result)){
			if(result.contains("&"))
				result = result.replaceAll("&", "&amp;");
			doc = DocumentHelper.parseText(result);
		}
		return doc;
	}
	
	default String getQueryPropertiesKey(){
		return this.getClass().getName() + "." + new Throwable().getStackTrace()[1].getMethodName();
	}
	
	default String getQueryPropertiesKeyOnlyMethod(){
		return new Throwable().getStackTrace()[1].getMethodName();
	}
	
	default Integer str2Integer(String str){
		Integer result = null;
		try {
			result = StringUtils.isBlank(str) ? null : Integer.valueOf(str);
		} catch (Exception e) {
			return result;
		}
		return result;
	}
	
	default Boolean str2Boolean(String str){
		Boolean result = null;
		try {
			result = StringUtils.isBlank(str) ? null : (str.equals("1") ? true : false);
		} catch (Exception e) {
			return result;
		}
		return result;
	}
}
