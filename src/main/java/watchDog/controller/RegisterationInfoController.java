
package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import watchDog.bean.register.RegisterationInfo;
import watchDog.dao.RegisterationInfoDAO;
import watchDog.dao.SIMCardDAO;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 2, 2020
 */
@WebServlet(urlPatterns = {"/rinfo/view", "/rinfo/getData","/rinfo/edit"})
public class RegisterationInfoController extends HttpServlet implements BaseController{

	private static final long serialVersionUID = -5031685751594766916L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterationInfoController.class);
	
	private RegisterationInfoDAO registerationInfoDAO = RegisterationInfoDAO.INSTANCE;
	
	private SIMCardDAO simCardDAO = SIMCardDAO.INSTANCE;

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
	
	/**
	 * 
	 * Description:
	 * @param req
	 * @param resp
	 * @author Matthew Xu
	 * @date Dec 3, 2020
	 */
	private void view(HttpServletRequest req, HttpServletResponse resp){
		try {
			req.getRequestDispatcher("../rinfo.jsp").forward(req, resp);
		} catch (ServletException | IOException e) {
			LOGGER.error("",e);
		}
	}
	
	/**
	 * 
	 * Description:
	 * @param req
	 * @param resp
	 * @author Matthew Xu
	 * @date Dec 4, 2020
	 */
	private void getData(HttpServletRequest req, HttpServletResponse resp){
		try {
			resp.setHeader("Content-type", "text/html;charset=UTF-8");
			List<RegisterationInfo> rInfos = registerationInfoDAO.getAll();
			OutputStream ops = resp.getOutputStream();
			Writer outputStreamWriter = new OutputStreamWriter(ops, CHAR_ENCODING_UTF8);
			PrintWriter printWriter = new PrintWriter(outputStreamWriter);
			printWriter.write(JSON.toJSONString(rInfos));
			printWriter.flush();
		} catch (Exception e) {
			LOGGER.error("",e);
		}
		
	}
	
	private void edit(HttpServletRequest req, HttpServletResponse resp){
		try {
			String parameter = req.getParameter("row");
			String attr = (String) req.getAttribute("row");
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}
}
