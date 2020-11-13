package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import watchDog.bean.AlarmAppend;
import watchDog.service.AlarmService;
import watchDog.service.DeviceValueMgr;

/**
* Description: 用于alarm.jsp
* @author MatthewXu
* @date Mar 29, 2019
*/
public class DeviceCheckAJAX extends HttpServlet{

	private static final long serialVersionUID = 1L;

	/* @author Matthew Xu
	 * @date Mar 29, 2019
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String servletPath = req.getServletPath();
		String methodName = servletPath.substring(1, servletPath.length() - 3);
		try {
			Method method = getClass().getDeclaredMethod(methodName, HttpServletRequest.class,HttpServletResponse.class);
			method.invoke(this, req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* @author Matthew Xu
	 * @date Mar 29, 2019
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	/**
	 * 检查站点是否在线
	 * @param req
	 * @param resp
	 * @throws Exception
	 * @author MatthewXu
	 * @date Apr 1, 2019
	 */
	private void checkSite(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		
		String idsite = req.getParameter("idsite");
		Integer kidsupervisor = 0;
		if(idsite != null && idsite.length() != 0){
			kidsupervisor = Integer.valueOf(idsite);
		}
			
		Map<String, List<AlarmAppend>> activeAlarm = AlarmService.getActiveAlarm(new int[]{kidsupervisor});
		List<AlarmAppend> alarms = activeAlarm.get("i");
		alarms.addAll(activeAlarm.get("u"));
		AlarmAppend exampleAlarm = null;
		boolean isSiteOnline = false;
		Map<String, String> alarmMap = new HashMap<>();
		if(alarms.size() > 0){
			exampleAlarm = alarms.get(0);
			isSiteOnline = DeviceValueMgr.isSiteOnline(exampleAlarm.getIp(), exampleAlarm.getDevCode());
		}
		
		if(isSiteOnline){
			for (AlarmAppend alarmAppend : alarms) {
				alarmMap.put(String.valueOf(alarmAppend.getIdAlarm()), String.valueOf(DeviceValueMgr.isDeviceOnline(alarmAppend.getIp(), alarmAppend.getDevCode())));
			}
		}
		alarmMap.put("issiteonline", String.valueOf(isSiteOnline));
		
		String json = JSON.toJSONString(alarmMap);
		OutputStream os = resp.getOutputStream();
		Writer osw = new OutputStreamWriter(os, "utf-8");
		Writer pw = new PrintWriter(osw);
		pw.write(json);
		pw.flush();
		
	}
	
	/**
	 * 检查设备是否离线
	 * @param req
	 * @param resp
	 * @throws Exception
	 * @author MatthewXu
	 * @date Apr 1, 2019
	 */
	private void checkDevice(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		
		OutputStream os = resp.getOutputStream();
		Writer osw = new OutputStreamWriter(os, "utf-8");
		Writer pw = new PrintWriter(osw);
		pw.write(String.valueOf(DeviceValueMgr.isDeviceOnline(req.getParameter("ip"), req.getParameter("devCode"))));
		pw.flush();
	}
}
