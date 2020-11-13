package watchDog.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import watchDog.bean.SiteInfo;
import watchDog.util.HttpServletUtil;

/**
 * Servlet implementation class SiteController
 */
@WebServlet(urlPatterns={"/site/view","/site/manage"})
public class SiteController extends HttpServlet implements BaseController{
	private static final Logger logger = Logger.getLogger(SiteController.class);
	
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Method method = HttpServletUtil.INSTANCE.getMethod(req, resp, this);
		try {
			method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("",e);
		}
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	private void view(HttpServletRequest req, HttpServletResponse resp){
		try {
			req.setAttribute("siteInfoList", siteInfoDAO.getList(null));
			req.getRequestDispatcher("../infoMgr.jsp").forward(req, resp);
		} catch (ServletException | IOException e) {
			logger.error("",e);
		}
	}
	
	private void manage(HttpServletRequest req, HttpServletResponse resp) {  
		try {
			req.setCharacterEncoding(CHAR_ENCODING_UTF8);
			Map<String, String[]> parameterMap = req.getParameterMap();
			Set<String> keySet = parameterMap.keySet();
			List<String> keyList = new ArrayList<>(keySet);
			// The size is the total of sites submitted.
			int size = parameterMap.get(keyList.get(0)).length;
			for(int i = 0; i < size; i++){
				Map<String, Object> map = new HashMap<>();
				for (String key : keySet) {
					String[] attrs = parameterMap.get(key);
					map.put(key, attrs[i]);
				}
				String jsonStr = JSON.toJSONString(map);
				SiteInfo siteInfo = JSON.parseObject(jsonStr, SiteInfo.class);
				logger.info(siteInfo.toString());
				if(!siteInfoDAO.isSiteExist(siteInfo.getSupervisorId()))
	        		siteInfoDAO.saveOne(siteInfo.getSupervisorId(),
	        				siteInfo.getDeadline(), 
	        				siteInfo.getCheckNetwork(),
	        				"6", 
	        				siteInfo.getChannel(), 
	        				siteInfo.getTagId(), 
	        				siteInfo.getTagId2(), 
	        				siteInfo.getTagId3(),
	        				siteInfo.getComment());
	        	else
	        		siteInfoDAO.updateOne(siteInfo.getSupervisorId(), siteInfo.getDeadline(), 
	        				siteInfo.getCheckNetwork(), siteInfo.getAgentId(), 
	        				siteInfo.getChannel(), siteInfo.getTagId(), 
	        				siteInfo.getTagId2(), siteInfo.getTagId3(),siteInfo.getComment());
			}
			resp.sendRedirect(req.getContextPath() + "/site/view");
		} catch (Exception e) {
			logger.error("",e);
		}
		
    }
}
