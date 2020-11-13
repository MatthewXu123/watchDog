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

import org.apache.log4j.Logger;

import watchDog.service.RequestService;
import watchDog.util.HttpServletUtil;

@WebServlet(urlPatterns = {"/login"})
public class LoginController extends HttpServlet implements BaseController {
	private static final Logger logger = Logger.getLogger(LoginController.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Method method = HttpServletUtil.INSTANCE.getShortMethod(req, resp, this);
		try {
			method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
	{
		doPost(request, response);
	}
	
    protected void login(HttpServletRequest req, HttpServletResponse resp)  
            throws ServletException, IOException {  
    	req.setCharacterEncoding("UTF-8");  
        resp.setCharacterEncoding("UTF-8");
        String userName=req.getParameter("userName");;
        String password= req.getParameter("password");
        HttpSession session = req.getSession();
        
        if("adminChat".equals(userName) && RequestService.getPwd().equals(password)){
        	session.setAttribute("LoginStatus", "success");
        	resp.sendRedirect(req.getContextPath() + "/site/view");
        }else{
        	req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
	
}
