
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

import watchDog.service.DataTrafficService;
import watchDog.service.FileSevice;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Apr 1, 2020
 */
@WebServlet(urlPatterns = { "/dsite/view"})
public class DanfossSiteController extends HttpServlet implements BaseController{

	private static final long serialVersionUID = 1L;
	
	private static final FileSevice fileService = FileSevice.INSTANCE;
	
	private DataTrafficService dataTrafficService = DataTrafficService.INSTANCE;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		if (!"success".equals(session.getAttribute("LoginStatus"))) {
			resp.sendRedirect("../login.jsp");
			return;
		}
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

	

}