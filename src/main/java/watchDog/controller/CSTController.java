
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

import com.alibaba.fastjson.JSON;

import watchDog.bean.PageVPNInfo;
import watchDog.bean.SiteInfo;
import watchDog.listener.Dog;
import watchDog.service.SiteInfoService;
import watchDog.service.VPNService;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 12, 2020
 */
@WebServlet(urlPatterns = { "/cst/submit","/cst/mobile555666888","/cst/query" })
public class CSTController extends HttpServlet implements BaseController {
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
	 * @throws IOException 
	 * @throws ServletException 
	 * @date May 13, 2020
	 */
	private void mobile555666888(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
	    req.setAttribute("ok", "ok");
	    req.getRequestDispatcher("../cst.jsp").forward(req, resp);
	}
	private void submit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    String ip = req.getParameter("ip");
	    SiteInfo s = Dog.getInstance().getSiteInfoByIP(ip);
	    if(s != null)
	    {
	        SiteInfoService.commissioning(s);
	        String json = JSON.toJSONString("OK");
	        OutputStream os = resp.getOutputStream();
	        Writer osw = new OutputStreamWriter(os, "utf-8");
	        Writer pw = new PrintWriter(osw);
	        pw.write(json);
	        pw.flush();
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
	private void query(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    String ip = req.getParameter("ip");
	    SiteInfo site = Dog.getInstance().getSiteInfoByIP(ip);
	    CSTObject o = null;
	    if(site != null)
	    {
	        String account = account(site.getIp());
	        String publicIP = "公网IP: ";
	        if(StringUtils.isNotBlank(account))
	        {
	            PageVPNInfo info = VPNService.INSTANCE.getVPNInfo(account);
	            if(info != null)
	                publicIP += info.getPublicIP();
	        }
	        //set to true already, warning
	        if(site.getCheckNetwork() != null && site.getCheckNetwork())
	        {
	            o = new CSTObject(site.getDescription(),"已经确认过了",publicIP);
	        }
	        else
	        {
	            o = new CSTObject(site.getDescription(),"",publicIP);
	        }
	    }
	    else
	    {
	        o = new CSTObject("","站点不存在,有可能的错误<br> 1.vpn IP地址错误，请确认正确的IP<br>2. 站点未创建，请在Remote网站查询确认","");
	    }
		String json = JSON.toJSONString(o);
		OutputStream os = resp.getOutputStream();
        Writer osw = new OutputStreamWriter(os, "utf-8");
        Writer pw = new PrintWriter(osw);
        pw.write(json);
        pw.flush();
	}
	public static String account(String ip)
    {
        if(ip.startsWith("192.168.88."))
            return "pvp"+ip.replace("192.168.88.", "");
        if(ip.startsWith("192.168.89.") || ip.startsWith("192.168.90.") || ip.startsWith("192.168.91.")
          ||ip.startsWith("192.168.92.") || ip.startsWith("192.168.93.") || ip.startsWith("192.168.94.")
          ||ip.startsWith("192.168.95.") || ip.startsWith("192.168.96.") || ip.startsWith("192.168.97."))
        {
            return "pvp"+ip.replace("192.168.", "").replace(".", "");
        }
        return null;
    }
	class CSTObject{
	    public CSTObject(String siteName)
	    {
	        this.siteName = siteName;
	    }
	    
	    public CSTObject(String siteName, String errorInfo,String info) {
            super();
            this.siteName = siteName;
            this.errorInfo = errorInfo;
            this.info = info;
        }

        private String siteName = null;
	    private String errorInfo = null;
	    public String info = null;
	    public String getSiteName()
	    {
	        return siteName;
	    }
	    public String getErrorInfo()
	    {
	        return errorInfo;
	    }
	    public String getInfo()
	    {
	        return info;
	    }
	}
}
