
package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import watchDog.bean.result.ResultFactory;
import watchDog.dao.SiteInfoDAO;

/**
 * Description:
 * @author Matthew Xu
 * @date May 13, 2020
 */
public interface BaseController {

	static final String CHAR_ENCODING_UTF8 = "UTF-8";
	
	static final SiteInfoDAO siteInfoDAO = SiteInfoDAO.INSTANCE;
	
	public static void returnSuccess(HttpServletResponse resp) {
		returnResult(resp, JSONObject.toJSONString(ResultFactory.getSuccessResult()));
	}
	
	public static void returnFailure(HttpServletResponse resp) {
		returnResult(resp, JSONObject.toJSONString(ResultFactory.getFailResult()));
	}
	
	public static void returnFailure(HttpServletResponse resp, String msg) {
		returnResult(resp, JSONObject.toJSONString(ResultFactory.getFailResult(msg)));
	}
	
	public static void returnResult(HttpServletResponse resp, String result) {
		try {
			resp.setHeader("Content-type", "text/html;charset=UTF-8");
			OutputStream ops = resp.getOutputStream();
			Writer outputStreamWriter = new OutputStreamWriter(ops, CHAR_ENCODING_UTF8);
			PrintWriter printWriter = new PrintWriter(outputStreamWriter);
			printWriter.write(result);
			printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
