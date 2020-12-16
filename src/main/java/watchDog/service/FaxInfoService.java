package watchDog.service;

import static watchDog.util.LogUtil.faxInfoLogger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONObject;

import watchDog.bean.Device;
import watchDog.bean.FaxInfoDO;
import watchDog.dao.FaxInfoDAO;
import watchDog.property.template.FaxMsgLogTemplate;
import watchDog.property.template.PropertyConfig;
import watchDog.thread.AlarmNotificationMain;
import watchDog.util.AESUtils;
import watchDog.util.DateTool;
import watchDog.util.HttpSendUtil;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatUser;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;
import watchDog.wechat.util.sender.Sender;

/**
 * Description:
 * 
 * @author MatthewXu
 * @date Apr 24, 2019
 */
public class FaxInfoService {
	
	public static final FaxInfoService INSTANCE = new FaxInfoService();
	
	private FaxInfoDAO faxInfoDAO = FaxInfoDAO.INSTANCE;
	
	private PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
	
	private Sender sender = Sender.getInstance();
	
	public static final String FAX_CALL_REQUEST_URL = "http://dtu.carel-remote.com:8080/callingService/servlet/producer?client=rv_alarm&encrypt=ENCRYPT_CONTENT&mobile=USER_MOBILE&username=USERNAME&system=SYSTEM&description=ALARM_DESCRIPTION";
	
	public static final String PARAMETER_IMMEDIATE = "&immediate=0";

	public static final String PHONE_HINT = "☎";
	
	public static final String ENCRYPT_CONTENT = "ENCRYPT_CONTENT";
	
	public static final String USER_MOBILE = "USER_MOBILE";
	
	public static final String SYSTEM = "SYSTEM";
	
	public static final String USERNAME = "USERNAME";
	
	public static final String ALARM_DESCRIPTION = "ALARM_DESCRIPTION";
	
	private static String[] ignoreProperties = new String[]{"repeatedTimes","lastCallTime"};
	
	public static List<FaxInfoDO> faxInfoHistory = new ArrayList<>();

	private FaxInfoService(){}
	
	/**
	 * 
	 * Description: Get all the faxinfos, send them to the call server and send the wechat notifications.
	 * @author Matthew Xu
	 * @date May 28, 2020
	 */
	public void sendFaxInfo() {
		try {
		// Get all the faxinfos.
		List<FaxInfoDO> allFaxInfos = faxInfoDAO.getFaxInfoList();
		
		faxInfoHistory.forEach(faxInfoDO -> faxInfoLogger.info("历史报警:" + faxInfoDO.toString()));
		allFaxInfos.forEach(faxInfoDO -> faxInfoLogger.info("查询报警:" + faxInfoDO.toString()));
		
			if (ObjectUtils.isCollectionNotEmpty(allFaxInfos)) {
				// Filter the faxinfos to get the right ones.
				List<FaxInfoDO> correctFaxRecord = correctFaxRecord(allFaxInfos);
				if (ObjectUtils.isCollectionNotEmpty(correctFaxRecord)) {
					correctFaxRecord.forEach(faxInfoDO -> faxInfoLogger.info("过滤后报警:" + faxInfoDO.toString()));
					
					for (FaxInfoDO faxInfoDO : correctFaxRecord) {
						List<WechatUser> wechatMemberList = faxInfoDO.getWechatMemberList();
						if (ObjectUtils.isCollectionNotEmpty(wechatMemberList)) {
							for (WechatUser wechatMember : wechatMemberList) {
								// Send the request
								String url = FAX_CALL_REQUEST_URL
										.replace(USERNAME, URLEncoder.encode(wechatMember.getName(), "utf-8"))
										.replace(USER_MOBILE, wechatMember.getMobile())
										.replace(ALARM_DESCRIPTION, getFaxAlarmDescription(faxInfoDO))
										.replace(ENCRYPT_CONTENT, URLEncoder.encode(getEncryptContent(), "utf-8"))
										+ (faxInfoDO.getRepeatedTimes() > 1 ? "" : PARAMETER_IMMEDIATE);
								WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
								if(StringUtils.isBlank(configStorage.getDebug()))
								{
    								String result = HttpSendUtil.INSTANCE.sendGet(new String(url.getBytes("iso-8859-1"), "utf-8"),
    										HttpSendUtil.CHAR_ENCODING_UTF8);
    								faxInfoLogger.info(url);
    								if (isFaxCallSuccess(result)){
                                        logFaxSendSuccess(faxInfoDO, wechatMember,getAlarmDeviceDescStr(faxInfoDO.getDevices(), ","));
                                        String msg = propertyConfig.getValue(FaxMsgLogTemplate.FM_SEND.getKey(),
                                                new Object[] { faxInfoDO.getSitename(),
                                                        faxInfoDO.getAlarmFaxRuleDTO().getDescription(),
                                                        getAlarmDeviceDescStr(faxInfoDO.getDevices(), "\n"),
                                                        getWechatMemberStr(wechatMemberList), faxInfoDO.getRepeatedTimes(),
                                                        AlarmNotificationMain.alarmSurroundURL(faxInfoDO.getIdsite(), "查看报警") });
                                        sender.sendIM(new WechatMsg.Builder(msg, configStorage.getCallingMsgAgentId(),new String[] { wechatMember.getUserid() }).type(Sender.WECHAT_MSG_TYPE_USER).build());
                                    }
                                    else 
                                        logFaxSendFailed(faxInfoDO, wechatMember, result);
								}
							}
						}
					}
				} else {
					faxInfoLogger.info("本次无faxinfo");
				}
			}
		} catch (Exception e) {
			faxInfoLogger.error("", e);
		}

	}
	
	/**
	 * 
	 * Description:
	 * @param currentFaxRecord
	 * @return
	 * @author Matthew Xu
	 * @date Jun 29, 2020
	 */
	public List<FaxInfoDO> correctFaxRecord(List<FaxInfoDO> currentFaxRecord) {
		Date currentTime = new Date();
		// Initialize the lastFaxRecord 
		if (ObjectUtils.isCollectionEmpty(faxInfoHistory)) {
			Iterator<FaxInfoDO> currentIterator = currentFaxRecord.iterator();
			while(currentIterator.hasNext()){
				FaxInfoDO baseFaxInfoDO = currentIterator.next();
				if(baseFaxInfoDO.isWithMobile()){
					baseFaxInfoDO.setLastCallTime(currentTime);
					baseFaxInfoDO.updateRepeatedTimes();
				}else {
					currentIterator.remove();
					logFaxNoMobile(baseFaxInfoDO);
				}
			}
			faxInfoHistory.addAll(currentFaxRecord);
			return currentFaxRecord;
		} else {
			Iterator<FaxInfoDO> lastIterator = faxInfoHistory.iterator();
			// Use the additional container to store the new faxinfos to avoid the java.util.ConcurrentModificationException
			List<FaxInfoDO> newFaxInfoList = new ArrayList<>();
			while(lastIterator.hasNext()){
				FaxInfoDO lastFaxInfo = lastIterator.next();
				Iterator<FaxInfoDO> currentIterator = currentFaxRecord.iterator();
				while(currentIterator.hasNext()){
					FaxInfoDO currentFaxInfo = currentIterator.next();
					// If the faxInfo is old...
					if(faxInfoHistory.contains(currentFaxInfo)){
						if(lastFaxInfo.equals(currentFaxInfo))
							handleExistedFaxInfos(lastFaxInfo, currentFaxInfo, currentIterator, currentTime);
					}else{
						if(currentFaxInfo.isWithMobile()){
							if(!newFaxInfoList.contains(currentFaxInfo)){
								currentFaxInfo.setLastCallTime(currentTime);
								currentFaxInfo.updateRepeatedTimes();
								newFaxInfoList.add(currentFaxInfo);
							}
						}else {
							logFaxNoMobile(currentFaxInfo);
							currentIterator.remove();
						}
					}
				}
			}
			faxInfoHistory.addAll(newFaxInfoList);
			return currentFaxRecord;
		}
	}
	
	/**
	 * 
	 * Description:
	 * @param lastFaxInfo
	 * @param currentFaxInfo
	 * @param currentIterator
	 * @param currentTime
	 * @param isTag
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private void handleExistedFaxInfos(FaxInfoDO lastFaxInfo, FaxInfoDO currentFaxInfo, Iterator<FaxInfoDO> currentIterator, Date currentTime){
		if(ObjectUtils.isOneContainOther(lastFaxInfo.getIdalarmList(), currentFaxInfo.getIdalarmList())){
			if (isFaxInfoLongEnoughToRedial(lastFaxInfo)) {
				if(currentFaxInfo.isWithMobile() && lastFaxInfo.isCallTimesNotEnough()){
					lastFaxInfo.updateRepeatedTimes();
					lastFaxInfo.setLastCallTime(currentTime);
					currentFaxInfo.setRepeatedTimes(lastFaxInfo.getRepeatedTimes());
				}else {
					if(!lastFaxInfo.isCallTimesNotEnough())
						logFaxCallTimesEnough(lastFaxInfo);
					else if(!currentFaxInfo.isWithMobile())
						logFaxNoMobile(currentFaxInfo);
					currentIterator.remove();
				}
			}else {
				logFaxDelayNotEnough(currentFaxInfo);
				currentIterator.remove();
			}
		}else {
			// If the devicetags or devices are new or they are changed, the repeated times will be reset to once.
			if(currentFaxInfo.isWithMobile()){
				currentFaxInfo.setRepeatedTimes(1);
				lastFaxInfo.setRepeatedTimes(1);
				lastFaxInfo.setLastCallTime(currentTime);
			}else {
				logFaxNoMobile(currentFaxInfo);
				currentIterator.remove();
			}
		}
		BeanUtils.copyProperties(currentFaxInfo, lastFaxInfo, ignoreProperties);
	}
	
	/**
	 * 
	 * Description:
	 * @param result
	 * @return
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private boolean isFaxCallSuccess(String result){
		int status = (int)(JSONObject.parseObject(result).get("result"));
		if(status == 1){
			return true;
		}else if(status == 0) {
			return false;
		}
		return false;
		
	}
	
	/**
	 * 
	 * Description:
	 * @param faxInfoDO
	 * @return
	 * @author Matthew Xu
	 * @date Jul 2, 2020
	 */
	private boolean isFaxInfoLongEnoughToRedial(FaxInfoDO faxInfoDO){
		return DateTool.diffTime(new Date(), faxInfoDO.getLastCallTime(), DateTool.CONSTANT_MINUTE) > faxInfoDO.getAlarmFaxRuleDTO().getMinCallDelay();
	}
	
	/**
	 * 
	 * Description:
	 * @return
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	public static String getEncryptContent(){
		return AESUtils.encrypt("date is " + DateTool.format(new Date(), "dd-MM-yyyy"), "JKLJQ48UJSJF49jksjfjk9JASFI0JL");
		
	}
	
	/**
	 * 
	 * Description:
	 * @param faxInfoDO
	 * @return
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private String getFaxAlarmDescription(FaxInfoDO faxInfoDO){
		try {
			return URLEncoder.encode(faxInfoDO.getSitename() + ":" 
					+ faxInfoDO.getAlarmFaxRuleDTO().getDescription() + ":" 
					+ getAlarmDeviceDescStr(faxInfoDO.getDevices(), ",") + ":" 
					+ faxInfoDO.getRepeatedTimes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			faxInfoLogger.error("",e);;
		}
		return "";
	}
	
	/**
	 * 
	 * Description:
	 * @param wechatMembers
	 * @return
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private String getWechatMemberStr(List<WechatUser> wechatMembers){
		String wechatMemberStr = "";
		for (WechatUser wechatMember : wechatMembers) {
			wechatMemberStr += wechatMember.getName() + ",";
		}
		return wechatMemberStr.substring(0, wechatMemberStr.length() - 1);
	}
	
	/**
	 * 
	 * Description:
	 * @param devices
	 * @param separator
	 * @return
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private String getAlarmDeviceDescStr(Set<Device> devices, String separator){
		String alarmDeviceDescStr = "";
		for (Device device : devices) {
			alarmDeviceDescStr += device.getDescription() + separator;
		}
		return StringUtils.isBlank(separator) ? alarmDeviceDescStr : alarmDeviceDescStr.substring(0, alarmDeviceDescStr.length() - 1);
	}
	
	/**
	 * 
	 * Description:
	 * @param faxInfoDO
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private void logFaxCallTimesEnough(FaxInfoDO faxInfoDO){
		faxInfoLogger.info(propertyConfig.getValue(FaxMsgLogTemplate.FL_CALL_TIMES_ENOUGH.getKey(), 
				new Object[]{faxInfoDO.getSitename(),faxInfoDO.getAlarmFaxRuleDTO().getDescription(), faxInfoDO.getAlarmFaxRuleDTO().getMaxCallTimes()}));
	}
	
	/**
	 * 
	 * Description:
	 * @param faxInfoDO
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private void logFaxDelayNotEnough(FaxInfoDO faxInfoDO){
		faxInfoLogger.info(propertyConfig.getValue(FaxMsgLogTemplate.FL_DELAY_NOT_ENOUGH.getKey(), 
				new Object[]{faxInfoDO.getSitename(),faxInfoDO.getAlarmFaxRuleDTO().getDescription(), faxInfoDO.getAlarmFaxRuleDTO().getMinCallDelay()}));
	}
	
	/**
	 * 
	 * Description:
	 * @param faxInfoDO
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private void logFaxNoMobile(FaxInfoDO faxInfoDO){
		faxInfoLogger.info(propertyConfig.getValue(FaxMsgLogTemplate.FL_NO_MOBILE.getKey(), 
				new Object[]{faxInfoDO.getSitename(),faxInfoDO.getAlarmFaxRuleDTO().getDescription()}));
	}
	
	/**
	 * 
	 * Description:
	 * @param faxInfoDO
	 * @param wechatMember
	 * @param faxDeviceStr
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private void logFaxSendSuccess(FaxInfoDO faxInfoDO, WechatUser wechatMember, String faxDeviceStr){
		faxInfoLogger.info(propertyConfig.getValue(FaxMsgLogTemplate.FL_SEND_SUCCESS.getKey(), 
				new Object[]{faxInfoDO.getSitename(),faxInfoDO.getAlarmFaxRuleDTO().getDescription(), wechatMember.getName(), wechatMember.getMobile(), faxDeviceStr}));
	}
	
	/**
	 * 
	 * Description:
	 * @param faxInfoDO
	 * @param wechatMember
	 * @param comment
	 * @author Matthew Xu
	 * @date Jun 22, 2020
	 */
	private void logFaxSendFailed(FaxInfoDO faxInfoDO, WechatUser wechatMember, String comment){
		faxInfoLogger.info(propertyConfig.getValue(FaxMsgLogTemplate.FL_SEND_FAILED.getKey(), 
				new Object[]{faxInfoDO.getSitename(),faxInfoDO.getAlarmFaxRuleDTO().getDescription(), wechatMember.getName(), wechatMember.getMobile(),comment}));
	}
	
}
