package watchDog.service;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.wechat.util.sender.Sender;

public class RequestService {

	public static String validation(HttpServletRequest request,String sessionKey,String cookieKey)
	{
		Sender wechat = Sender.getInstance();
		if(wechat.isDebug() != null)
			return wechat.isDebug();
		if(request != null)
		{
			if(StringUtils.isNotBlank(sessionKey))
			{
				HttpSession s = request.getSession();
				if(s.getAttribute(sessionKey) != null && s.getAttribute(sessionKey).toString().length()>0)
					return s.getAttribute(sessionKey).toString();
			}
			if(StringUtils.isNotBlank(cookieKey))
			{
				Cookie[] cookies = request.getCookies();
		    	if(cookies !=null){
		    	    for(int i = 0;i < cookies.length;i++){
		    	        if(cookies[i].getName().equals(cookieKey))
		    	        {
		    	        	return cookies[i].getValue();
		    	        }
		    	    }
		    	}
			}
		}
		return null;
	}
	
	/**
	 * @return
	 * @author MatthewXu
	 * @date May 8, 2019
	 */
	public static String getPwd() {
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String month = String.valueOf(calendar.get(calendar.MONDAY)+1);
        String day = String.valueOf(calendar.get(calendar.DATE));
        String rightPassword = "Carel"+ month.substring(month.length()-1,month.length())+day.substring(day.length()-1,day.length());
		return rightPassword;
	}

}
