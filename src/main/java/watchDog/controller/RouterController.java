
package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;

import watchDog.dao.PropertyDAO;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * @author Matthew Xu
 * @date May 18, 2020
 */
@WebServlet(urlPatterns = {"/router/view","/router/manage","/router/get" })
public class RouterController extends HttpServlet implements BaseController{

	private static final long serialVersionUID = -5031685751594766916L;
	
	private PropertyDAO propertyDAO = PropertyDAO.INSTANCE;

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
	 * @date May 18, 2020
	 */
	private void view(HttpServletRequest req, HttpServletResponse resp){
		try {
			req.setAttribute("routerinfo", propertyDAO.getOne("routerinfo"));
			req.getRequestDispatcher("../router.jsp").forward(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void manage(HttpServletRequest req, HttpServletResponse resp){
		String routerinfo = req.getParameter("routerinfo");
		String value = propertyDAO.getOne("routerinfo");
		if(value == null){
			propertyDAO.saveOne("routerinfo", routerinfo);
		}else {
			propertyDAO.updateOne("routerinfo", routerinfo);
		}
		try {
			resp.sendRedirect("../router/view");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp){
		String mac = req.getParameter("mac");
		String routerinfo = propertyDAO.getOne("routerinfo");
		String[] split = routerinfo.split(";");
		String username = null;
		String password = null;
		for (String info : split) {
			String[] subSplit = info.split("&");
			for(int i = 0; i < subSplit.length; i ++){
				if(subSplit[0].trim().equals(mac)){
					username = subSplit[1];
					password = subSplit[2];
					break;
				}
				
			}
		}
		OutputStream outputStream;
		try {
			outputStream = resp.getOutputStream();
			Writer outputStreamWriter = new OutputStreamWriter(outputStream, CHAR_ENCODING_UTF8);
			PrintWriter printWriter = new PrintWriter(outputStreamWriter);
			Map<String, String> map = new HashMap<>();
			map.put("username", username);
			map.put("password", password);
			printWriter.write(JSON.toJSONString(map));
			printWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
