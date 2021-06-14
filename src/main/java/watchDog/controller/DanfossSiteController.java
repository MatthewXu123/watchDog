
package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import watchDog.bean.register.RegisterationInfo;
import watchDog.bean.register.SIMCardStatus;
import watchDog.danfoss.model.Supervisor;
import watchDog.danfoss.service.SupervisorService;
import watchDog.danfoss.service.impl.SupervisorServiceImpl;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Apr 1, 2020
 */
@WebServlet(urlPatterns = { "/dsites/view", "/dsites/getData", "/dsites/edit"})
public class DanfossSiteController extends HttpServlet implements BaseController{
	
	private static final Logger logger = Logger.getLogger(DanfossSiteController.class);

	private static final long serialVersionUID = 1L;
	
	private SupervisorService supervisorService = SupervisorServiceImpl.getInstance();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*HttpSession session = req.getSession();
		if (!"success".equals(session.getAttribute("LoginStatus"))) {
			resp.sendRedirect("../login.jsp");
			return;
		}*/
		Method method = HttpServletUtil.INSTANCE.getMethod(req, resp, this);
		try {
			method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private void view(HttpServletRequest req, HttpServletResponse resp){
		try {
			req.getRequestDispatcher("../dsites.jsp").forward(req, resp);
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	private void getData(HttpServletRequest req, HttpServletResponse resp){
		try {
			resp.setHeader("Content-type", "text/html;charset=UTF-8");
			OutputStream ops = resp.getOutputStream();
			Writer outputStreamWriter = new OutputStreamWriter(ops, CHAR_ENCODING_UTF8);
			PrintWriter printWriter = new PrintWriter(outputStreamWriter);
			printWriter.write(JSONObject.toJSONString(supervisorService.findAll(), SerializerFeature.WriteMapNullValue));
			printWriter.flush();
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void edit(HttpServletRequest req, HttpServletResponse resp){
		try {
			//String simCardId = req.getParameter("id");
			//int currentSimCardId = StringUtils.isBlank(simCardId) ? 0 : Integer.valueOf(simCardId);
			Supervisor supervisor = JSONObject.parseObject(req.getParameter("row"), Supervisor.class);
			if(supervisorService.updateOne(supervisor))
				BaseController.returnSuccess(resp);
			else
				BaseController.returnFailure(resp);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	

}