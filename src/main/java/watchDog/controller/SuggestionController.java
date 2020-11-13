
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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import watchDog.bean.PageVPNInfo;
import watchDog.dao.SuggestionDAO;
import watchDog.service.VPNService;
import watchDog.util.HttpServletUtil;

@WebServlet(urlPatterns = { "/suggestion/create", "/suggestion/submit"})
public class SuggestionController extends HttpServlet implements BaseController {
    private static final Logger logger = Logger.getLogger(MySites.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		Method method = HttpServletUtil.INSTANCE.getMethod(req, resp, this);
		try {
		    req.setCharacterEncoding("UTF-8");
			method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private void create(HttpServletRequest req, HttpServletResponse resp) {
		try {
			req.getRequestDispatcher("../suggestion.jsp").forward(req, resp);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void submit(HttpServletRequest req, HttpServletResponse resp) {
	    try {
	        String userId = req.getParameter("userid");
	        String content = req.getParameter("content");
	        String p = "";
	        if(StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(content))
	        {
    	        logger.info(userId+" "+content);
    	        SuggestionDAO.insert(userId, content);
    	        p = "?type=created";
	        }
            resp.sendRedirect("create"+p);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	
}
