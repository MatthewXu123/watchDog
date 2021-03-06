
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


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import watchDog.bean.register.RegisterationInfo;
import watchDog.bean.register.SIMCardStatus;
import watchDog.dao.RegisterationInfoDAO;
import watchDog.dao.SIMCardDAO;
import watchDog.util.HttpServletUtil;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 2, 2020
 */
@WebServlet(urlPatterns = {"/rinfo/view", "/rinfo/getData","/rinfo/edit", "/rinfo/save"})
public class RegisterationInfoController extends HttpServlet implements BaseController{

	private static final long serialVersionUID = -5031685751594766916L;
	
	private static final Logger LOGGER = Logger.getLogger(RegisterationInfoController.class);
	
	private RegisterationInfoDAO registerationInfoDAO = RegisterationInfoDAO.INSTANCE;
	
	private SIMCardDAO simCardDAO = SIMCardDAO.INSTANCE;

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
			LOGGER.error("",e);
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
	 * @date Dec 3, 2020
	 */
	private void view(HttpServletRequest req, HttpServletResponse resp){
		try {
			req.getRequestDispatcher("../rinfo.jsp").forward(req, resp);
		} catch (ServletException | IOException e) {
			LOGGER.error("",e);
		}
	}
	
	/**
	 * 
	 * Description:
	 * @param req
	 * @param resp
	 * @author Matthew Xu
	 * @date Dec 4, 2020
	 */
	private void getData(HttpServletRequest req, HttpServletResponse resp){
		try {
			resp.setHeader("Content-type", "text/html;charset=UTF-8");
			List<RegisterationInfo> rInfos = registerationInfoDAO.getAllOrderByRegisterationDate();
			OutputStream ops = resp.getOutputStream();
			Writer outputStreamWriter = new OutputStreamWriter(ops, CHAR_ENCODING_UTF8);
			PrintWriter printWriter = new PrintWriter(outputStreamWriter);
			printWriter.write(JSONObject.toJSONString(rInfos, SerializerFeature.WriteMapNullValue));
			printWriter.flush();
		} catch (Exception e) {
			LOGGER.error("",e);
		}
		
	}
	
	private void edit(HttpServletRequest req, HttpServletResponse resp){
		try {
			String simCardId = req.getParameter("simCardId");
			int currentSimCardId = StringUtils.isBlank(simCardId) ? 0 : Integer.valueOf(simCardId);
			RegisterationInfo registerationInfo = JSONObject.parseObject(req.getParameter("row"), RegisterationInfo.class);
			if(currentSimCardId != 0){
				int previousId = registerationInfo.getSimCard().getId();
				if(currentSimCardId != previousId){
					simCardDAO.updateStatus(previousId, SIMCardStatus.UNUSED);
					simCardDAO.updateStatus(currentSimCardId, SIMCardStatus.ENABLED);
					registerationInfo.setSimCard(simCardDAO.getOneById(currentSimCardId));
				}
				
			}
			
			registerationInfoDAO.updateOne(registerationInfo);
			BaseController.returnSuccess(resp);
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	private void save(HttpServletRequest req, HttpServletResponse resp){
		try {
			JSONObject infoObj = JSONObject.parseObject(req.getParameter("rinfo"));
			RegisterationInfo info = infoObj.toJavaObject(RegisterationInfo.class);
			Integer simcardId = infoObj.getInteger("simCardId");
			if(simcardId != null){
				info.setSimCard(simCardDAO.getOneById(simcardId));
				simCardDAO.updateStatus(simcardId, SIMCardStatus.ENABLED);
			}
			registerationInfoDAO.saveOne(info);
			BaseController.returnSuccess(resp);
		} catch (Exception e) {
			LOGGER.error("",e);
		}
		
	}
}
