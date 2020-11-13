
package watchDog.service;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.junit.Test;

import watchDog.util.AESUtils;
import watchDog.util.DateTool;
import watchDog.util.HttpSendUtil;

/**
 * Description:
 * @author Matthew Xu
 * @date Jul 9, 2020
 */
public class FaxInfoServiceTest {

	private static final String FAX_CALL_REQUEST_URL = "http://dtu.carel-remote.com:8080/callingService/servlet/producer?client=rv_alarm&encrypt=ENCRYPT_CONTENT&mobile=USER_MOBILE&username=USERNAME&system=system&description=ALARM_DESCRIPTION";
	
	@Test
	public void test() throws UnsupportedEncodingException {
		String url = FAX_CALL_REQUEST_URL
				.replace("USERNAME", URLEncoder.encode("test", "utf-8"))
				.replace("USER_MOBILE", "15371810209")
				.replace("ALARM_DESCRIPTION", "test-alarm")
				.replace("ENCRYPT_CONTENT", URLEncoder.encode(AESUtils.encrypt("date is " + DateTool.format(new Date(), "dd-MM-yyyy"), "JKLJQ48UJSJF49jksjfjk9JASFI0JL"), "utf-8"))
				+ "&immediate=0";
		String result = HttpSendUtil.INSTANCE.sendGet(new String(url.getBytes("iso-8859-1"), "utf-8"),
				HttpSendUtil.CHAR_ENCODING_UTF8);
		System.out.println(result);
	}

}
