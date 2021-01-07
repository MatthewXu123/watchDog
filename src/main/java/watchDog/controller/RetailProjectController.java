
package watchDog.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

import watchDog.service.RetailProjectService;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 2, 2020
 */
@WebServlet(urlPatterns = {"/retail/view", "/retail/getData"})
public class RetailProjectController extends HttpServlet implements BaseController{

	private static final long serialVersionUID = -5031685751594766916L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RetailProjectController.class);
	
	private static final RetailProjectService retailProjectService = RetailProjectService.INSTANCE;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Method method = HttpServletUtil.INSTANCE.getMethod(req, resp, this);
		
		/*HttpSession session = req.getSession();
		if(!method.getName().equals("get") && !"success".equals(session.getAttribute("LoginStatus"))){
			resp.sendRedirect("../login.jsp");
			return;
        } */
		
		try {
			method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("",e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	private void view(HttpServletRequest req, HttpServletResponse resp){
		try {
			req.getRequestDispatcher("../retail.jsp").forward(req, resp);
		} catch (ServletException | IOException e) {
			LOGGER.error("",e);
		}
	}
	
	private void getData(HttpServletRequest req, HttpServletResponse resp){
		try {
			BaseController.returnResult(resp, JSONArray.toJSONString(retailProjectService.getAll()));
		} catch (Exception e) {
			LOGGER.error("",e);
		}
		
	}
}
