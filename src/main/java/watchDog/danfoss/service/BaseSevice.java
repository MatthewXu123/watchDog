
package watchDog.danfoss.service;

import java.io.IOException;

import watchDog.util.HttpSendUtil;
import watchDog.util.ValueRetrieve;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
public interface BaseSevice {

	default String sendQuery(String ip, String cmd) throws IOException{
		//return ValueRetrieve.sendHttpsPost("http://" + ip + "/danfoss/html/xml.cgi", cmd);
		return HttpSendUtil.INSTANCE.sendPost("http://" + ip + "/danfoss/html/xml.cgi"
				, cmd
				, HttpSendUtil.CHAR_ENCODING_UTF8
				, HttpSendUtil.APPLICATION_URLENCODED);
	}
}
