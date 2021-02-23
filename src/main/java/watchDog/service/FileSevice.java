
package watchDog.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import watchDog.bean.SiteInfo;
import watchDog.listener.Dog;
import watchDog.util.CSVUtils;
import watchDog.util.DateTool;
import watchDog.wechat.bean.WechatUser;

/**
 * Description:
 * @author Matthew Xu
 * @date Jul 16, 2020
 */
public class FileSevice {

	public static final FileSevice INSTANCE = new FileSevice();
	
	private FileSevice(){
		
	}
	
	/**
	 * Description:
	 * 
	 * @param csvHeaders
	 * @return
	 * @author Matthew Xu
	 * @date Apr 1, 2020
	 */
	public static List<List<Object>> getSiteDataList(String[] csvHeaders) {
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		try {
			List<SiteInfo> siteList = Dog.getInstance().getInfos();
			for (SiteInfo site : siteList) {
				List<Object> row = new ArrayList<>();
				for (String header : csvHeaders) {
					row.add(CSVUtils.getFieldValueByFieldName(header, site));
				}
				dataList.add(row);
			}
		} catch (Exception e) {
		}
		return dataList;
	}
	
	/**
	 * 
	 * Description:
	 * @param csvHeaders
	 * @return
	 * @author Matthew Xu
	 * @date Jul 16, 2020
	 */
	public static List<List<Object>> getSiteMemberList(){
		Map<SiteInfo, List<WechatUser>> siteWechatMemberMap = Dog.getInstance().getWechatApplicationThread().getSiteWechatMemberMap4Export();
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		for (Entry<SiteInfo, List<WechatUser>> siteMemberMap : siteWechatMemberMap.entrySet()) {
			SiteInfo siteInfo = siteMemberMap.getKey();
			List<WechatUser> members = siteMemberMap.getValue();
			
			List<Object> row = new ArrayList<>();
			row.add(siteInfo.getManDescription());
			row.add(siteInfo.getCusDescription());
			row.add(siteInfo.getDescription());
			String tagId = siteInfo.getTagId();
			String tagId2 = siteInfo.getTagId2();
			String soldierGroupStr = "";
			String officerGroupStr = "";
			for (WechatUser member : members) {
				String[] department = member.getDepartment();
				List<String> departmentList = Arrays.asList(department);
				if(departmentList.contains(tagId))
					soldierGroupStr += member.getName() + "(" + member.getMobile() + ");" ;
				if(departmentList.contains(tagId2))
					officerGroupStr += member.getName() + "(" + member.getMobile() + ");" ;
			}
			row.add(StringUtils.isBlank(soldierGroupStr) ? soldierGroupStr : soldierGroupStr.substring(0, soldierGroupStr.length() - 1));
			row.add(StringUtils.isBlank(officerGroupStr) ? officerGroupStr : officerGroupStr.substring(0, officerGroupStr.length() - 1));
			String str = siteInfo.getSiteURL();
			row.add(StringUtils.isBlank(str)?"":str);
			row.add(DateTool.format(siteInfo.getDeadline()));
			dataList.add(row);
		}
		return dataList;
	}
}
