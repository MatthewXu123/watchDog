
package watchDog.util;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Description:
 * @author Matthew Xu
 * @date May 14, 2020
 */
public class RegexUtil {

	// Example: 2020-02-02
	private static final String DATE_STRING_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
	
	private static final String IP_REGEX = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
	
	public static boolean matchDateString(String targetStr){
		return Pattern.compile(DATE_STRING_REGEX).matcher(targetStr).matches();
	}
	public static boolean match(String regex,String str)
	{
	    return Pattern.compile(regex).matcher(str).matches();
	}
	
	public static boolean matchIP(String ip){
		return Pattern.compile(IP_REGEX).matcher(ip).matches();
	}
	
	@Test
	public void t()
	{
	    boolean a = match("^START:\\d{4}-\\d{2}-\\d{2}$","START:2020-01-01");
	    int i=0;
	}
}
