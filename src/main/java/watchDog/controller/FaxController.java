/*
package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import watchDog.bean.FaxInfo;
import watchDog.service.AlarmService;
import watchDog.service.FaxService;
import watchDog.service.RequestService;
import watchDog.util.HttpServletUtils;

*//**
 * Description:
 * @author Matthew Xu
 * @date May 21, 2020
 *//*
@WebServlet(urlPatterns = {"/fax/check"})
public class FaxController extends HttpServlet implements BaseController{

	private static final long serialVersionUID = 7215561785867271357L;
	
	private static Date lastQueryTime = null;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Method method = HttpServletUtils.INSTANCE.getShortMethod(req, resp, this);
		try {
			method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}
	
	private void check(HttpServletRequest req, HttpServletResponse resp){
		try {
			req.setCharacterEncoding("UTF-8");
			resp.setCharacterEncoding("UTF-8");
			String user = req.getParameter("user");
			String password = req.getParameter("pwd");
			String result = "";
			if (user != null && user.length() != 0 && password != null && password.length() != 0 ) {
				if ("adminChat".equals(user) && RequestService.getPwd().equals(password)) {
					if(lastQueryTime == null) {
						lastQueryTime = new Date();
					}
					List<FaxInfo> currentFaxRecord = AlarmService.getFaxAlarms(lastQueryTime);
					currentFaxRecord = FaxService.fixFaxRecord(currentFaxRecord);
					if (currentFaxRecord != null && currentFaxRecord.size() > 0) {
						FaxService.sendFaxNotification(currentFaxRecord);
						result = JSON.toJSONString(currentFaxRecord);
					} else {
						result = "0";
					}
				} else {
					result = "1";
				}
			} else {
				result = "2";
			}
			lastQueryTime = new Date();
			OutputStream os = resp.getOutputStream();
			Writer osw = new OutputStreamWriter(os, "UTF-8");
			Writer pw = new PrintWriter(osw);
			pw.write(result);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
*/