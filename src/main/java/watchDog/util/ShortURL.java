package watchDog.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ShortURL {
	public static String shorten(String longURL)
	{
		Date d1 = new Date();
		String path = "http://api.t.sina.com.cn/short_url/shorten.json?source=3271760578&url_long="+longURL;
		try{
			String result = HttpSendUtil.INSTANCE.sendGet(path,"UTF-8");
			JSONArray array = JSONArray.parseArray(result);
			for(Object o:array)
			{
				JSONObject oo = (JSONObject)o;
				return oo.get("url_short").toString();
			}
		}
		catch(Exception ex)
		{
			return longURL;
		}
		finally{
			Date d2 = new Date();
			System.out.println(d2.getTime()-d1.getTime()+":"+longURL);
		}
		return longURL;
	}
	public static void main(String[] args)
	{
		System.out.println(shorten("https://remote.carel-china.com/RVRP@-20445133591832669936160@/boss/"));
		System.out.println(getURLEncoderString("https://remote.carel-china.com/RVRP@-2044513454183266993669@/boss/?folder=dtlview&bo=BDtlView&curTab=tab1name&iddev=1"));
	}
	public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String URLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
