
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

import watchDog.dao.PropertyDAO;
import watchDog.dao.RegisterationInfoDAO;
import watchDog.dao.SIMCardDAO;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 2, 2020
 */
@WebServlet(urlPatterns = "/rinfo/view")
public class RegisterationInfoController extends HttpServlet implements BaseController{

	private static final long serialVersionUID = -5031685751594766916L;
	
	private RegisterationInfoDAO registerationInfoDAO = RegisterationInfoDAO.INSTANCE;
	
	private SIMCardDAO simCardDAO = SIMCardDAO.INSTANCE;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Method method = HttpServletUtil.INSTANCE.getMethod(req, resp, this);
		
		HttpSession session = req.getSession();
		if(!method.getName().equals("get") && !"success".equals(session.getAttribute("LoginStatus"))){
			resp.sendRedirect("../login.jsp");
			return;
        } 
		
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
			e.printStackTrace();
		}
	}
}
