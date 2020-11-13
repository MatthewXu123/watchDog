package watchDog.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.bean.SLAResult;
import watchDog.listener.Dog;
import watchDog.service.AlarmService;
import watchDog.service.AlarmService.AlarmManageResult;
import watchDog.service.AlarmService.energyWeek;
import watchDog.service.EnergyService;
import watchDog.service.RequestService;
import watchDog.util.DateTool;

public class GetData extends HttpServlet{
	private static final Logger logger = Logger.getLogger(GetData.class);
	public static final String type_week = "w";
	public static final String type_month = "m";
	public static final String type_date_alarm = "alarm";
	public static final String type_date_energy = "energy";
	public static final String type_date_alarm_manage = "alarmMange";
	
	public GetData()
	{
		super();
	}
	@Override  
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");
        
        String DataType = request.getParameter("DataType");
        String type = request.getParameter("type");
        PrintWriter out = response.getWriter();
        String result="";
        String userId= RequestService.validation(request, "wx_userid", "wx_userid");
        if(StringUtils.isBlank(userId))
        	return;

        String startday= request.getParameter("startday");
        Map<String, List<Integer>> generalWechatMemberSiteMap = Dog.getInstance().getWechatApplicationThread().getGeneralWechatMemberSiteMap();
        List<Integer> siteIdList = generalWechatMemberSiteMap.get(userId);
        int[] sistes = new int[siteIdList.size()];
        for(int i = 0; i< sistes.length;i++){
        	sistes[i] = siteIdList.get(i);
        }
        
        if(sistes.length==0){
        	out.print(result);
        	return;
        }
        
        if(type_date_alarm.equals(DataType)){
        	Date startDate = null;
        	if(type.equalsIgnoreCase("w")){
        		startDate = DateTool.parse(startday, "yyyy-MM-dd");
        	}else{
                startDate = DateTool.parse(startday, "yyyy-MM");
            }
            List<SLAResult> sla = AlarmService.getSLAResult(sistes, type, startDate);
            result = JSON.toJSONString(sla);
        }
        else if(type_date_alarm_manage.equals(DataType)){
        	Date startDate = null;
        	if(type.equalsIgnoreCase("w")){
        		startDate = DateTool.parse(startday, "yyyy-MM-dd");
        	}else{
                startDate = DateTool.parse(startday, "yyyy-MM");
            }
        	List<AlarmManageResult> al = AlarmService.getAlarmManageResult(sistes, type, startDate);
            result = JSON.toJSONString(al);
        }
        else if(type_date_energy.equals(DataType)){
        	int typePeriodW = EnergyService.WEEK_PERIOD;
        	
        	List<energyWeek> energy = EnergyService.getEnergyResult(sistes,type,typePeriodW, AlarmService.NO_VARIABLE,startday);
            result = JSON.toJSONString(energy);
        }
        
        
        
        
          

        out.print(result);  
        return;
    }
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
	{
		doPost(request, response);
	}
}
