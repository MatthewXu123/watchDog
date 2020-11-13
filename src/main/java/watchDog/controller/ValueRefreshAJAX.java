package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import watchDog.bean.DeviceValueBean;
import watchDog.service.DeviceValueMgr;
import watchDog.util.DateTool;

import com.alibaba.fastjson.JSON;

public class ValueRefreshAJAX extends HttpServlet{

	private static final long serialVersionUID = 318130974894549262L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");
        String ip=request.getParameter("ip");
        String devCode = request.getParameter("devCode");
    	Map<String,String> valueMap = new HashMap<String,String>();
        DeviceValueBean valueBean = DeviceValueMgr.getInstance().getValueSlow(ip, devCode);
    	if(valueBean != null)
    	{
    		String lastQueryTime = DateTool.format(valueBean.getDate(), "yyyy-MM-dd HH:mm:ss");
    		valueMap = valueBean.getValues();
    		valueMap.put("lastQueryTime", lastQueryTime);
    	}
        String json = JSON.toJSONString(valueMap);
        response.setHeader("Cache-Control", "no-cache");
        OutputStream outputStream = response.getOutputStream();
        Writer outputStreamWriter = new OutputStreamWriter(outputStream, "UTF8");
        Writer printWriter = new PrintWriter(outputStreamWriter);
        printWriter.write(json);
        printWriter.flush();
        
    }
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
	{
		doPost(request, response);
	}
}
