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

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.service.ShortURLMgr;
import watchDog.wechat.util.WechatUtil;

public class Auth extends HttpServlet{
	private static final Logger logger = Logger.getLogger(Auth.class);
	public Auth()
	{
		super();
	}
	@Override  
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
//		logger.info("here");
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String path = request.getParameter("path");
        String userId = "";
        //1. try session
        if(session.getAttribute("wx_userid") != null && session.getAttribute("wx_userid").toString().length()>0)
        	userId = session.getAttribute("wx_userid").toString();
//        logger.info("userId 1:"+userId);
        //2. try cookie
        if(userId == null || userId.length() == 0)
        {
	    	Cookie[] cookies = request.getCookies();
	    	if(cookies !=null){
	    	    for(int i = 0;i < cookies.length;i++){
	    	        if(cookies[i].getName().equals("wx_userid"))
	    	        {
	    	        	userId = cookies[i].getValue();
//	    	        	logger.info("cookie catched:"+userId);
	    	        	break;
	    	        }
	    	    }
	    	}
        }
//        logger.info("userId 2:"+userId);
        //3. get from wechat
    	if(userId == null || userId.length() == 0)
    	{
//    		logger.info("try get from wechat");
    		try{
    		    String code = request.getParameter("code");
    			String str = WechatUtil.getUserInfo(code);
    			JSONObject obj = JSONObject.parseObject(str);
    			userId = obj.getString("UserId");
//    			logger.info("str get from wechat:"+userId);
    			//userId = "LuoLiBin";
    			if(userId != null && userId.length()>0)
    			{
    				Cookie cookie = new Cookie("wx_userid",userId);
    			    cookie.setMaxAge(60 * 60 * 24 * 7);
    			    response.addCookie(cookie);
    			}
    		}catch(Exception ex)
    		{
    			logger.error("ex",ex);
    		}
    	}
//    	logger.info("userId 3:"+userId);
    	if(userId != null && userId.length()>0)
    	{
    		session.setAttribute("wx_userid", userId);
    	}
    	if(StringUtils.isNotBlank(path))
    	{
    		path = path.replace(ShortURLMgr.AND, "&");
    		response.sendRedirect("../"+path);
    	}
    	else
    		response.sendRedirect("../site.jsp");
    }
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
	{
		doPost(request, response);
	}
}
