
package watchDog.util;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 13, 2020
 */
public class HttpServletUtil {

	public static HttpServletUtil INSTANCE = new HttpServletUtil();
	
	private HttpServletUtil(){}
	
	/**
	 * 
	 * Description: The url should be "/xxx/yyy" and the method name is "yyy".
	 * @param req
	 * @param resp
	 * @param servlet
	 * @return
	 * @author Matthew Xu
	 * @date May 20, 2020
	 */
	public Method getMethod(HttpServletRequest req, HttpServletResponse resp, HttpServlet servlet) {
		String servletPath = req.getServletPath();
		int lastIndex = servletPath.lastIndexOf("/");
		String methodName = servletPath.substring(lastIndex + 1, servletPath.length());
		Method method = null;
		try {
			method = servlet.getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
			if(method == null)
				return null;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return method;
	}
	
	/**
	 * 
	 * Description:The url should be "/xxx" and the name should be "xxx".
	 * @param req
	 * @param resp
	 * @param servlet
	 * @return
	 * @author Matthew Xu
	 * @date May 20, 2020
	 */
	public Method getShortMethod(HttpServletRequest req, HttpServletResponse resp, HttpServlet servlet) {
		String servletPath = req.getServletPath();
		int lastIndex = servletPath.indexOf("/");
		String methodName = servletPath.substring(lastIndex + 1, servletPath.length());
		Method method = null;
		try {
			method = servlet.getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
			if(method == null)
				return null;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return method;
	}
}
