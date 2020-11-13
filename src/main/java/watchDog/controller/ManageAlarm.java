package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.listener.Dog;
import watchDog.service.AlarmManageService;
import watchDog.service.AlarmService;
import watchDog.service.RequestService;

public class ManageAlarm extends HttpServlet {
	private static final Logger logger = Logger.getLogger(ManageAlarm.class);

	public ManageAlarm() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		String userId = RequestService.validation(request, "wx_userid", "wx_userid");
		if (StringUtils.isBlank(userId))
			return;
		String user = Dog.getInstance().getWechatApplicationThread().getWechatMemberByUserId(userId).getName();
		int idSite = Integer.valueOf(request.getParameter("idsite"));
		String cmd = request.getParameter("cmd");
		String tmp = request.getParameter("idalarms");
		String[] ss = tmp.split(",");
		int[] idAlarms = new int[ss.length];
		int i = 0;
		for (String s : ss) {
			idAlarms[i++] = Integer.valueOf(s);
		}
		String comments = "";
		int tableType = AlarmManageService.TABLE_TYPE_ACTIVE;
		if (AlarmManageService.ACKNOWLEDGE.equals(cmd)){
			comments = request.getParameter("ackComments");
			AlarmManageService.ack(idSite, idAlarms, user, comments);
		}
		else if (AlarmManageService.MANAGE.equals(cmd)) {
			String tableTypeStr = request.getParameter("table_type");
			if (StringUtils.isNotBlank(tableTypeStr))
				tableType = Integer.valueOf(tableTypeStr);
			String manageType = request.getParameter("manage_type");
			AlarmManageService.manage(idSite, tableType, idAlarms, user, manageType);
		} else if (AlarmManageService.RESET.equals(cmd)) {
			comments = request.getParameter("resetComments");
			AlarmManageService.reset(idSite, idAlarms, user, comments);
		}
		if (tableType == AlarmManageService.TABLE_TYPE_ACTIVE) {
			response.sendRedirect("../alarm.jsp?idsite=" + idSite);
		} else if (tableType == AlarmManageService.TABLE_TYPE_RESET) {
			Map<String, String> usespare = AlarmService.getUsespareByIdalarm(idSite, tableType, idAlarms);
			String uJson = JSON.toJSONString(usespare);
			OutputStream outputStream = response.getOutputStream();
			Writer outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
			Writer printWriter = new PrintWriter(outputStreamWriter);
			printWriter.write(uJson);
			printWriter.flush();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
