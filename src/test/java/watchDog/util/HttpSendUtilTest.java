
package watchDog.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

import watchDog.service.FaxInfoService;

/**
 * Description:
 * @author Matthew Xu
 * @date Jun 4, 2020
 */
public class HttpSendUtilTest {

	@Test
	public void test() throws UnsupportedEncodingException {
		String string =  FaxInfoService.INSTANCE.getEncryptContent();
		string = URLEncoder.encode(string, "utf-8");
		String url = "http://dtu.carel-remote.com:8080/callingService/servlet/producer?client=sz1&encrypt=ENCRYPT_CONTENT&mobile=15366203524&username=matthew&system=system&description=高温&immediate=0".
				replace("ENCRYPT_CONTENT", string);
		String string2 = new String(url.getBytes("iso-8859-1"),"utf-8");
		String result =  HttpSendUtil.INSTANCE.sendGet(string2, HttpSendUtil.CHAR_ENCODING_UTF8);
		System.out.println(result);
	}

}
