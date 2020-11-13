
package watchDog.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import watchDog.controller.LoginController;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 20, 2020
 */
@WebFilter(urlPatterns = {"/site/*", "/vpn/*" })
public class LoginFilter implements Filter {
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;
		HttpSession session = req.getSession();
		String servletPath = req.getServletPath();
		if (!"success".equals(session.getAttribute("LoginStatus")) && servletPath.indexOf("/vpn/mobile")<0) {
			resp.sendRedirect(req.getContextPath() + "/login");
		} else {
			chain.doFilter(req, resp);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
