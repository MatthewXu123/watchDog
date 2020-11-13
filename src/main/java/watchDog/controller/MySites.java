package watchDog.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import watchDog.wechat.util.WechatUtil;

public class MySites extends HttpServlet{
	private static final Logger logger = Logger.getLogger(MySites.class);
	public MySites()
	{
		super();
	}
	@Override  
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String userId = "";
        //1. try session
        if(session.getAttribute("wx_userid") != null && session.getAttribute("wx_userid").toString().length()>0)
        	userId = session.getAttribute("wx_userid").toString();
        //2. try cookie
        if(userId == null || userId.length() == 0)
        {
	    	Cookie[] cookies = request.getCookies();
	    	if(cookies !=null){
	    	    for(int i = 0;i < cookies.length;i++){
	    	        if(cookies[i].getName().equals("wx_userid"))
	    	        {
	    	        	userId = cookies[i].getValue();
	    	        	logger.info("cookie catched:"+userId);
	    	        	break;
	    	        }
	    	    }
	    	}
        }
        //3. get from wechat
    	if(userId == null || userId.length() == 0)
    	{
    		try{
    		    String code = request.getParameter("code");
    			String str = WechatUtil.getUserInfo(code);
    			JSONObject obj = JSONObject.parseObject(str);
    			userId = obj.getString("UserId");
    			//userId = "LuoLiBin";
    			if(userId != null && userId.length()>0)
    			{
    				Cookie cookie = new Cookie("wx_userid",userId);
    			    cookie.setMaxAge(60 * 60 * 24 * 7);
    			    response.addCookie(cookie);
    			}
    		}catch(Exception ex)
    		{
    			
    		}
    	}
    	if(userId != null && userId.length()>0)
    	{
    		session.setAttribute("wx_userid", userId);
//    		List<SiteInfo> sites = Dog.getInstance().getTagMemberThread().getSitesByUserId(userId); 
//    		if(sites != null && sites.size() == 1)
//    		{
//    			SiteInfo s = sites.get(0);
//    			response.sendRedirect(s.getSiteURL());
//    			logger.info("redirect to:"+s.getSiteURL());
//    			return;
//    		}
//    		request.setAttribute("sites", sites);
    	}
    	response.sendRedirect("../site.jsp");
    }
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
	{
		doPost(request, response);
	}
}
