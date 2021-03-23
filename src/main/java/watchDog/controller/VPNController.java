
package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.bean.PageVPNInfo;
import watchDog.service.VPNService;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 12, 2020
 */
@WebServlet(urlPatterns = { "/vpn/view", "/vpn/manage","/vpn/mobile555666888" })
public class VPNController extends HttpServlet implements BaseController {
	private VPNService vpnService = VPNService.INSTANCE;
	private static final int TYPE_KICK_PID = 1;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
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

	/**
	 * 
	 * Description: Get the output of the VPN server about the router status.
	 * 
	 * @param req
	 * @param resp
	 * @author Matthew Xu
	 * @date May 13, 2020
	 */
	private void mobile555666888(HttpServletRequest req, HttpServletResponse resp)
	{
	    view(req,resp);
	}
	private void view(HttpServletRequest req, HttpServletResponse resp) {
	    List<PageVPNInfo> lines = vpnService.getVPNServerOutPut();
	    String siteName = req.getParameter("siteName");
	    List<PageVPNInfo> list = new ArrayList<>();
	    if(StringUtils.isNotBlank(siteName) && lines != null)
	    {
	        for(PageVPNInfo info:lines)
	        {
	            if(info.getIp().indexOf(siteName)>=0)
	                list.add(info);
	        }
	    }
	    else
	        list = lines;
		try {
			req.setAttribute("lines", list);
			req.getRequestDispatcher("../vpn.jsp").forward(req, resp);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Description: Manage the router.
	 * 
	 * @param req
	 * @param resp
	 * @author Matthew Xu
	 * @throws IOException
	 * @date May 13, 2020
	 */
	private void manage(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String result = "";
			Integer type = Integer.valueOf(req.getParameter("type"));
			if (type == TYPE_KICK_PID) {
				String pid = req.getParameter("pid");
				result = vpnService.getPIDKicked(pid);
			}
			OutputStream outputStream = resp.getOutputStream();
			Writer outputStreamWriter = new OutputStreamWriter(outputStream, "UTF8");
			PrintWriter printWriter = new PrintWriter(outputStreamWriter);
			printWriter.write(result);
			printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
