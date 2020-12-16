package watchDog.wechat.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import me.chanjar.weixin.common.exception.WxErrorException;
import watchDog.bean.Property;
import watchDog.service.PropertyMgr;
import watchDog.util.HttpSendUtil;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatDept;
import watchDog.wechat.bean.WechatPostTag;
import watchDog.wechat.bean.WechatUser;
import watchDog.wechat.bean.WechatResult;
import watchDog.wechat.bean.WechatTag;
import watchDog.wechat.service.WechatService;

public class WechatUtil {
	private static final Logger logger = Logger.getLogger(WechatUtil.class);
	
	private static String getAccessTokenURL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRECT";
	private static String getTagsList = "https://qyapi.weixin.qq.com/cgi-bin/tag/list?access_token=ACCESS_TOKEN";
	private static String getTagMembers = "https://qyapi.weixin.qq.com/cgi-bin/tag/get?access_token=ACCESS_TOKEN&tagid=TAGID";
	// WechatMember
	private static String getWechatMemberSimpleListURL = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD";
	private static String getWechatMemberListURL = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD";
	private static String getWeChatUserURL = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&userid=USERID";
	private static String updateMemberURL = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=ACCESS_TOKEN";
	// WechatDept
	private static String getWechatDeptListURL = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN&id=ID";
	private static String getUserInfo = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE";
	private static String deleteDeptURL = "https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token=ACCESS_TOKEN&id=ID";
	private static String updateWechatDeptURL = "https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=ACCESS_TOKEN";
	private static String createDeptURL = "https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=ACCESS_TOKEN";
	// WechatTag
	private static String getTagListURL = "https://qyapi.weixin.qq.com/cgi-bin/tag/list?access_token=ACCESS_TOKEN";
	private static String deleteTagURL = "https://qyapi.weixin.qq.com/cgi-bin/tag/delete?access_token=ACCESS_TOKEN&tagid=TAGID";
	private static String getTagUserListURL = "https://qyapi.weixin.qq.com/cgi-bin/tag/get?access_token=ACCESS_TOKEN&tagid=TAGID";
	private static String addTagUserURL = "https://qyapi.weixin.qq.com/cgi-bin/tag/addtagusers?access_token=ACCESS_TOKEN";
	private static String createTagURL = "https://qyapi.weixin.qq.com/cgi-bin/tag/create?access_token=ACCESS_TOKEN";
	
	public static final String FECTH_CHILD = "1";
	public static final String DONT_FECTH_CHILD = "0";
	
	private static PropertyMgr propertyMgr = PropertyMgr.getInstance();

	/**
	 * Description:
	 * @return
	 * @author Matthew Xu
	 * @date May 12, 2020
	 */
	private static String getAccessToken(){
		String accessToken = "";
		Property accessTokenProperty = propertyMgr.getProperty(PropertyMgr.WECHAT_ACCESS_TOKEN);
		Property expireTimeProperty = propertyMgr.getProperty(PropertyMgr.WECHAT_EXPIRE_TIME);
		if(accessTokenProperty == null || StringUtils.isBlank(accessTokenProperty.getValue())
				|| expireTimeProperty == null || StringUtils.isBlank(expireTimeProperty.getValue())){
			accessToken = updateAccessToken();
		}else {
			long expireTime = Long.valueOf(expireTimeProperty.getValue());
			accessToken = accessTokenProperty.getValue();
			if(expireTime < System.currentTimeMillis()){
				accessToken = updateAccessToken();
			}
		}
		return accessToken;
	}
	
	public static String updateAccessToken(){
		String accessToken = "";
		try {
			long expireTime = System.currentTimeMillis() + 7200 * 1000;
			accessToken = WechatService.getInstance().getWxCpService().getAccessToken();
			if(!accessToken.equals(propertyMgr.getProperty(PropertyMgr.WECHAT_ACCESS_TOKEN).getValue())){
				propertyMgr.update(PropertyMgr.WECHAT_EXPIRE_TIME, String.valueOf(expireTime));
				propertyMgr.update(PropertyMgr.WECHAT_ACCESS_TOKEN, accessToken);
			}
			
		} catch (WxErrorException e) {
			logger.error("",e);
		}
		return accessToken;
		
	}
	
	public static String getUserInfo(String code) {
		String url = getUserInfo.replace("ACCESS_TOKEN", getAccessToken());
		url = url.replace("CODE", code);
		String result = HttpSendUtil.INSTANCE.sendGet(url, "UTF-8");
		return result;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param uid
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	public static WechatUser getMemberByUserId(String uid) {
		try {
			if (StringUtils.isBlank(uid))
				return null;
			return JSONObject.parseObject(
					HttpSendUtil.INSTANCE.sendGet(
							getWeChatUserURL.replace("ACCESS_TOKEN", getAccessToken()).replace("USERID", uid), "UTF-8"),
					WechatUser.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;

	}

	/**
	 * 
	 * Description: Get the departments of the target deptId.
	 * @param deptId
	 * @return
	 * @author Matthew Xu
	 * @date May 14, 2020
	 */
	public static List<WechatDept> getDeptListByDeptId(String deptId) {

		try {
			if (StringUtils.isBlank(deptId))
				return null;
			String wechatDeptListStr = HttpSendUtil.INSTANCE.sendGet(
					getWechatDeptListURL.replace("ACCESS_TOKEN", getAccessToken()).replace("ID", deptId), "UTF-8");
			return JSON.parseObject(wechatDeptListStr).getJSONArray("department").toJavaList(WechatDept.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * 
	 * Description:
	 * @param parentId
	 * @param deptName
	 * @return
	 * @author Matthew Xu
	 * @date Jun 8, 2020
	 */
	public static String isDeptExistByName(String parentId, String deptName){
		if(StringUtils.isBlank(parentId) || StringUtils.isBlank(deptName))
			return null;
		List<WechatDept> depts = getDeptListByDeptId(parentId);
		if(ObjectUtils.isCollectionNotEmpty(depts)){
			for (WechatDept wechatDept : depts) {
				if(wechatDept.getName().equals(deptName)){
					return wechatDept.getId();
				}
			}
		}
		return null;
		
	}
	
	/**
	 * 
	 * Description: get all the departments which have the members.
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	public static List<WechatDept> getDeptListAndMembers(String wechatDeptId) {
		try {
			if(StringUtils.isBlank(wechatDeptId))
				return null;
			List<WechatDept> wechatDeptList = getDeptListByDeptId(wechatDeptId);
			for (WechatDept wechatDept : wechatDeptList) {
				List<WechatUser> wechatMemberList = getMemberByDeptId(wechatDept.getId(), DONT_FECTH_CHILD);
				wechatDept.setWechatMemberList(wechatMemberList);
			}
			return wechatDeptList;
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
	/**
	 * 
	 * Description:
	 * @param wechatDeptId
	 * @return
	 * @author Matthew Xu
	 * @date Nov 25, 2020
	 */
	public static WechatDept getWechatDeptById(String wechatDeptId){
		try {
			List<WechatDept> wechatDepts =  getDeptListByDeptId(wechatDeptId);
			for (WechatDept wechatDept : wechatDepts) {
				if(wechatDept.getId().equals(wechatDeptId))
					return wechatDept;
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
	/**
	 * 
	 * Description: get all the department.
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	public static Map<String, List<WechatUser>> getDeptIdMemberMap(String baseWechatDeptId) {
		if(StringUtils.isBlank(baseWechatDeptId))
			return null;
		Map<String, List<WechatUser>> deptIdWechatMemberMap = new HashMap<>();
		List<WechatDept> wechatDeptList = getDeptListByDeptId(baseWechatDeptId);
		for (WechatDept wechatDept : wechatDeptList) {
			deptIdWechatMemberMap.put(wechatDept.getId(), getMemberByDeptId(wechatDept.getId(), DONT_FECTH_CHILD));
		}
		return deptIdWechatMemberMap;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param deptId
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	public static List<WechatUser> getMemberByDeptId(String wechatDeptId, String isFetchChild) {
		try {
			if(StringUtils.isBlank(wechatDeptId))
				return null;
			String WechatMemberListStr = HttpSendUtil.INSTANCE.sendGet(
					getWechatMemberListURL.replace("ACCESS_TOKEN", getAccessToken()).replace("DEPARTMENT_ID", wechatDeptId).replace("FETCH_CHILD", isFetchChild), "UTF-8");
			return JSON.parseObject(WechatMemberListStr).getJSONArray("userlist").toJavaList(WechatUser.class);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
		
	}

	/**
	 * Description:
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	public static List<WechatUser> getAllMembers(){
		return getMemberByDeptId("1",FECTH_CHILD);
	}
	
	
	/**
	 * Description:
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	public static Map<String, WechatUser> getMemberMap() {
		try {
			List<WechatUser> wechatMemberList = getMemberByDeptId("1", FECTH_CHILD);
			Map<String, WechatUser> weChatMemberMap = new HashMap<>();
			if (ObjectUtils.isCollectionNotEmpty(wechatMemberList)) {
				for (WechatUser wechatMember : wechatMemberList) {
					weChatMemberMap.put(wechatMember.getUserid(), wechatMember);
				}
			}
			return weChatMemberMap;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;

	}
	/**
	 * 
	 * Description:
	 * 
	 * @param deptId
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	public static boolean isDeptEmptyOfMember(String deptId) {
	    try {
	    	List<WechatUser> memberList = getMemberByDeptId(deptId,DONT_FECTH_CHILD);
			return memberList == null || memberList.size() == 0;
			} catch (Exception e) {
				logger.error("",e);
			}
			return false;
		
	}
	
	/**
	 * 
	 * Description: To check if the detptId has the corresponding department.
	 * @param deptId
	 * @return
	 * @author Matthew Xu
	 * @date May 14, 2020
	 */
	public static boolean isDeptExist(String deptId){
	    try {
	    	if(StringUtils.isBlank(deptId))
				return false;
			List<WechatDept> wechatDeptList = getDeptListByDeptId(deptId);
			return ObjectUtils.isCollectionNotEmpty(wechatDeptList);
			} catch (Exception e) {
				logger.error("",e);
			}
			return false;
		
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public static boolean isUserExist(String userId) {
		return getMemberByUserId(userId) != null ? true : false;
	}
	
	/**
	 * 
	 * Description: Create the department. The fields 'name' and 'parentId' are required.
	 * @param wechatDept
	 * @author Matthew Xu
	 * @date May 14, 2020
	 */
	public static String createDept(WechatDept wechatDept){
	    try {
	    	String params = JSON.toJSONString(wechatDept);
			String sendResult = HttpSendUtil.INSTANCE
					.sendPost(createDeptURL.replace("ACCESS_TOKEN", getAccessToken()), params, HttpSendUtil.CHAR_ENCODING_UTF8, HttpSendUtil.APPLICATION_JSON);
			return JSON.parseObject(sendResult).getString("id");
			} catch (Exception e) {
				logger.error("",e);
			}
			return null;
		
	}
	
	/**
	 * 
	 * Description:
	 * @param wechatMember
	 * @return
	 * @author Matthew Xu
	 * @date May 15, 2020
	 */
	public static String updateMember(WechatUser wechatMember){
		try {
			String params = JSON.toJSONString(wechatMember);
			return HttpSendUtil.INSTANCE
					.sendPost(updateMemberURL.replace("ACCESS_TOKEN", getAccessToken()), params, HttpSendUtil.CHAR_ENCODING_UTF8, HttpSendUtil.APPLICATION_JSON);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
		
	}
	
	/**
	 * 
	 * Description:
	 * @param wechatDept
	 * @return
	 * @author Matthew Xu
	 * @date Nov 25, 2020
	 */
	public static WechatResult updateWechatDept(WechatDept wechatDept){
		try {
			String params = JSON.toJSONString(wechatDept);
			return JSONObject.parseObject(HttpSendUtil.INSTANCE
					.sendPost(updateWechatDeptURL.replace("ACCESS_TOKEN", getAccessToken()), params, HttpSendUtil.CHAR_ENCODING_UTF8, HttpSendUtil.APPLICATION_JSON), WechatResult.class);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
		
	}
	
	/**
	 * 
	 * Description:
	 * @param deptId
	 * @return
	 * @author Matthew Xu
	 * @date May 15, 2020
	 */
	public static String deleteDept(String deptId){
		try {
			if(StringUtils.isBlank(deptId))
				return null;
			return HttpSendUtil.INSTANCE
					.sendGet(deleteDeptURL.replace("ACCESS_TOKEN", getAccessToken()).replace("ID", deptId), "UTF-8");
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
		
	}
	
	//WechatTag
	public static List<WechatTag> getTagList(){
		try {
			String tagList = HttpSendUtil.INSTANCE.sendGet(
					getTagListURL.replace("ACCESS_TOKEN", getAccessToken()), "UTF-8");
			return JSON.parseObject(tagList).getJSONArray("taglist").toJavaList(WechatTag.class);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
	public static WechatResult deleteTagById(String tagId) {
		try {
			return JSONObject.parseObject(HttpSendUtil.INSTANCE
					.sendGet(deleteTagURL.replace("ACCESS_TOKEN", getAccessToken()).replace("TAGID", tagId), "UTF-8"), WechatResult.class);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
	public static List<WechatUser> getTagUserList(String tagId) {
		try {
			String tagUserList = HttpSendUtil.INSTANCE.sendGet(
					getTagUserListURL.replace("ACCESS_TOKEN", getAccessToken()).replace("TAGID", tagId), "UTF-8");
			return JSON.parseObject(tagUserList).getJSONArray("userlist").toJavaList(WechatUser.class);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
	public static WechatResult addTagUser(WechatPostTag wechatPostTag){
		try {
			String params = JSON.toJSONString(wechatPostTag);
			return JSONObject.parseObject(HttpSendUtil.INSTANCE
					.sendPost(addTagUserURL.replace("ACCESS_TOKEN", getAccessToken()), params, HttpSendUtil.CHAR_ENCODING_UTF8, HttpSendUtil.APPLICATION_JSON), WechatResult.class);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
	public static WechatResult createTag(WechatPostTag wechatPostTag){
		try {
			String params = JSON.toJSONString(wechatPostTag);
			return JSONObject.parseObject(HttpSendUtil.INSTANCE
					.sendPost(createTagURL.replace("ACCESS_TOKEN", getAccessToken()), params, HttpSendUtil.CHAR_ENCODING_UTF8, HttpSendUtil.APPLICATION_JSON), WechatResult.class);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
}
