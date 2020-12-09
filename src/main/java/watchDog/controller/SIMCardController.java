
package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import watchDog.bean.register.SIMCard;
import watchDog.bean.register.SIMCardStatus;
import watchDog.dao.SIMCardDAO;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Dec 9, 2020
 */
@WebServlet(urlPatterns = { "/simcard/get" })
public class SIMCardController extends HttpServlet implements BaseController{

	private static final long serialVersionUID = -5031685751594766916L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SIMCardController.class);

	private SIMCardDAO simCardDAO = SIMCardDAO.INSTANCE;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Method method = HttpServletUtil.INSTANCE.getMethod(req, resp, this);
		try {
			method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp){
		try {
			List<SIMCard> list = simCardDAO.getAll();
			resp.setHeader("Content-type", "text/html;charset=UTF-8");
			OutputStream ops = resp.getOutputStream();
			Writer outputStreamWriter = new OutputStreamWriter(ops, CHAR_ENCODING_UTF8);
			PrintWriter printWriter = new PrintWriter(outputStreamWriter);
			printWriter.write(JSONObject.toJSONString(list));
			printWriter.flush();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}
}
