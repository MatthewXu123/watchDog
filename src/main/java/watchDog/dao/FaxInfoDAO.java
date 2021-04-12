package watchDog.dao;
import static watchDog.config.json.FaxRuleConfig.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import watchDog.bean.FaxInfoDO;
import watchDog.bean.Device;
import watchDog.bean.config.AlarmFaxRuleDTO;
import watchDog.config.json.FaxRuleConfig;
import watchDog.database.RecordSet;
import watchDog.listener.Dog;
import watchDog.thread.WechatApplicationThread;
import watchDog.util.DateTool;
import watchDog.util.ObjectUtils;
import watchDog.util.ValueRetrieve;
import watchDog.wechat.bean.WechatUser;

import static watchDog.util.LogUtil.*;
/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 21, 2020
 */
public class FaxInfoDAO extends BaseDAO {

	public static final FaxInfoDAO INSTANCE = new FaxInfoDAO();

	private TagDAO tagDAO = TagDAO.INSTANCE;
	
	private static final WechatApplicationThread wat = Dog.getInstance().getWechatApplicationThread();
	
	private static final int CABINET_LT = 0;
	private static final int CABINET_MT = 1;
	// The threshold value of the tEu for LT cabinets.
	private static final double THRESHOLD_LT_TEU = -10;
	// The threshold value of the tEu for MT cabinets.
	private static final double THRESHOLD_MT_TEU = 10;
	// The threshold value of the account of high-temp alarms.
	private static final int THRESHOLD_COUNT_HT_ALARMS = 4;
	
	private static final String DEVMOD_CODE_MPXPROV4 = "mpxprov4";
	
	private static final int DO_NOT_DISTURB_BEGIN = 22;
	
	private static final int DO_NOT_DISTURB_END = 8;
	
	private static final String TAG_CALL_IGNORE = "#c_ignore";
	
	private static final String[] highTempAlarmCodes = {CODE_TAG_HIGH_TEMP,CODE_HIGH_TEMP,CODE_LT_HIGH_TEMP,CODE_MT_HIGH_TEMP};
	
	private Map<String, String> devmodCodeMap = new HashMap<String, String>(){{
		put(DEVMOD_CODE_MPXPROV4, "Po4");
	}};
	
	private FaxInfoDAO() {
	}

	public List<FaxInfoDO> getFaxInfoList(){
		List<AlarmFaxRuleDTO> alarmFaxRuleDTOs = new ArrayList<>(FaxRuleConfig.getAlarmFaxRuleDTOs());
		
		List<FaxInfoDO> faxList = new ArrayList<>();
		if (ObjectUtils.isCollectionNotEmpty(alarmFaxRuleDTOs)) {
			// High Temp alarms
			faxList.addAll(getHighTempFaxList(alarmFaxRuleDTOs));
			
			// Unit offline alarms
			AlarmFaxRuleDTO alarmFaxRuleDTO = removeAlarmFaxRuleDTOSimpleByAlarmCode(CODE_UNIT_OFFLINE, alarmFaxRuleDTOs);
			if(alarmFaxRuleDTO != null){
				List<FaxInfoDO> unitOfflineFaxInfos = getList(CODE_UNIT_OFFLINE, alarmFaxRuleDTO);
				// If the unit-offline alarms happen with the specified high-temp alarms whose count has reached the threshold value,
				// we can consider that the unit has crashed.
				unitOfflineFaxInfos.forEach(faxInfoDO ->{
					if(isUnitOfflineTruly(faxInfoDO))
						faxList.add(faxInfoDO);
				});
			}
			// Common alarms.
			if(ObjectUtils.isCollectionNotEmpty(alarmFaxRuleDTOs))
				faxList.addAll(getList(null, getCommonSql(getCommonQueryCondition(alarmFaxRuleDTOs), getQueryDelay(alarmFaxRuleDTOs.get(0)))));
		}
		return faxList;
	}
	
	/**
	 * 
	 * Description:
	 * @param alarmCodes
	 * @param alarmFaxRuleDTOs
	 * @return
	 * @author Matthew Xu
	 * @date Jul 21, 2020
	 */
	private List<FaxInfoDO> getHighTempFaxList(List<AlarmFaxRuleDTO> alarmFaxRuleDTOs){
		List<FaxInfoDO> faxList = new ArrayList<>();
		for (String alarmCode : highTempAlarmCodes) {
			AlarmFaxRuleDTO alarmFaxRuleDTO = removeAlarmFaxRuleDTOSimpleByAlarmCode(alarmCode, alarmFaxRuleDTOs);
			if(alarmFaxRuleDTO != null)
				faxList.addAll(getList(alarmCode,alarmFaxRuleDTO));
		}
		return faxList;
	}
	
	/**
	 * 
	 * Description:
	 * @param alarmCode
	 * @param alarmFaxRuleDTO
	 * @return
	 * @author Matthew Xu
	 * @date Jul 21, 2020
	 */
	private List<FaxInfoDO> getList(String alarmCode, AlarmFaxRuleDTO alarmFaxRuleDTO) {
		int queryDelay = getQueryDelay(alarmFaxRuleDTO);
		String sql = null;
		if(StringUtils.isNotBlank(alarmCode))
			sql = getSpecialSql(alarmCode,queryDelay);
		return getList(alarmCode, sql);
	}
	
	/**
	 * 
	 * Description:
	 * @param alarmCode
	 * @param sql
	 * @return
	 * @author Matthew Xu
	 * @date Jun 28, 2020
	 */
	private List<FaxInfoDO> getList(String alarmCode, String sql) {
		List<FaxInfoDO> list = new ArrayList<>();
		try {
			RecordSet rs = dataBaseMgr.executeQuery(sql);
			if (rs != null && rs.size() > 0) {
				for (int i = 0; i < rs.size(); i++) {
					int kidsupervisor = (int) rs.get(i).get("idsite");
					if(tagDAO.isTagIgnore(kidsupervisor, TAG_CALL_IGNORE)){
						faxInfoLogger.info("id：" + kidsupervisor + " 该店已被忽略");
						continue;
					}
					// AlarmFaxRuleDTO
					String manNode =  (String)rs.get(i).get("mannode");
					String cusNode =  (String)rs.get(i).get("cusnode");
					String realAlarmCode = StringUtils.isBlank(alarmCode) ? (String)rs.get(i).get("alarmcode") : alarmCode;
					AlarmFaxRuleDTO alarmFaxRuleDTO = getAlarmFaxRuleDTOByAlarmCode(realAlarmCode, manNode, cusNode);
					// If the community doesn't have this kind of alarm, skip.
					if(alarmFaxRuleDTO == null)
						continue;
					
					// idalarms
					String[] idalarmArr = ((String) rs.get(i).get("idalarmarray")).split(",");
					if(!isAlarmCountEnough(idalarmArr.length, manNode, cusNode, realAlarmCode))
						continue;
					List<String> idalarmList = Arrays.asList(idalarmArr);
					
					// Devices
					String[] deviceArr = ((String) rs.get(i).get("devicearray")).split(",");
					String[] deviceDescArray = ((String) rs.get(i).get("devicedescarray")).split(",");
					Set<Device> deviceSet = new TreeSet<>();
					for(int j = 0; j < deviceArr.length ; j++)
						deviceSet.add(new Device(kidsupervisor, Integer.valueOf(deviceArr[j]), deviceDescArray[j]));
					
					// Complete the faxinfo
					FaxInfoDO faxInfo = new FaxInfoDO();
					faxInfo.setIdsite(kidsupervisor);
					faxInfo.setSitename((String)rs.get(i).get("sitename"));
					faxInfo.setIpaddress((String)rs.get(i).get("ipaddress"));
					faxInfo.setAgentId((String)rs.get(i).get("agentid"));
					faxInfo.setAlarmFaxRuleDTO(alarmFaxRuleDTO);
					faxInfo.setIdalarmList(idalarmList);
					List<WechatUser> wechatMemberList = new ArrayList<>();
					
					if(StringUtils.isNotBlank(realAlarmCode) && realAlarmCode.equals(CODE_UNIT_OFFLINE)){
						WechatUser wechatMember = new WechatUser();
						wechatMember.setName("Neil");
						wechatMember.setUserid("Neil");
						wechatMember.setMobile("18616686217");
						wechatMemberList = Arrays.asList(wechatMember);
					}else {
						wechatMemberList.addAll(wat.getAlarmFaxCallUsers((String)rs.get(i).get("tag_id")));
						wechatMemberList.addAll(wat.getAlarmFaxCallUsers((String)rs.get(i).get("tag_id2")));
					}
					faxInfo.setWechatMemberList(wechatMemberList);
					faxInfo.setDevices(deviceSet);
					list.add(faxInfo);
				}
			}
		} catch (Exception e) {
			faxInfoLogger.error("", e);
		}
		return list;
	}
	
	
	/**
	 * 
	 * Description:
	 * @param faxInfoDO
	 * @return
	 * @author Matthew Xu
	 * @date Jun 29, 2020
	 */
	private boolean isUnitOfflineTruly(FaxInfoDO faxInfoDO) {
		Set<Device> devices = faxInfoDO.getDevices();
		for (Device device : devices) {
			String deviceDesc = device.getDescription();
			if(deviceDesc.contains("低温") || deviceDesc.contains("LT"))
				if(isCabinetOfflineTruly(faxInfoDO.getIdsite(), faxInfoDO.getIpaddress(), CABINET_LT))
					return true;
			else if(deviceDesc.contains("中温") || deviceDesc.contains("MT"))
				if(isCabinetOfflineTruly(faxInfoDO.getIdsite(), faxInfoDO.getIpaddress(), CABINET_MT))
					return true;
		}
		return false;
	}
	
	/**
	 * 
	 * Description: check if the unit-offline alarms are with the HT alarms of the LT cabinets or MT cabinets.
	 * @param idsite
	 * @param ipaddress
	 * @param cabinetType
	 * @return
	 * @author Matthew Xu
	 * @date May 28, 2020
	 */
	private boolean isCabinetOfflineTruly(int idsite, String ipaddress,int cabinetType){
		int thresholdCount = 0;
		boolean flag = false;
		try {
			String cabinetCondition = "";
			double thresholdValue = 0;
			if(cabinetType == CABINET_LT){
				cabinetCondition += " and (d.description like '%LT%' or d.description like '%低温%' or d.description like '%冷冻%' or d.description like '%LAT%')";
				thresholdValue = THRESHOLD_LT_TEU;
			}else if(cabinetType ==CABINET_MT){
				cabinetCondition += " and (d.description like '%MT%' or d.description like '%中温%' or d.description like '%冷藏%') ";
				thresholdValue = THRESHOLD_MT_TEU;
			}
			
			String sql = " select d.description,d.code as devcode,d.devmodcode from lgdevice d "
					+ " where d.devmodcode like 'mpxprov4'"
					+ cabinetCondition
					+ " and d.kidsupervisor = ? order by random() limit 10";
			RecordSet rs = dataBaseMgr.executeQuery(sql, new Object[]{idsite});
			if (rs != null && rs.size() > 0) {
				for(int i = 0; i < rs.size(); i++){
					if(flag)
						break;
					// Get the real-time value of the specified variables.
					String devmodcode =  (String)rs.get(i).get("devmodcode");
					String devcode =  (String)rs.get(i).get("devcode");
					Map<String, String> codeValueMap = ValueRetrieve.getValue(ipaddress, devcode, devmodCodeMap.get(devmodcode));
					faxInfoLogger.info(ipaddress + ":" + devcode + ":" + codeValueMap);
					if(ObjectUtils.isMapNotEmpty(codeValueMap)){
						for(String value : codeValueMap.values()){
							if(!value.contains("*") && Double.parseDouble(value) >= thresholdValue){
								thresholdCount ++ ;
								if(thresholdCount >= THRESHOLD_COUNT_HT_ALARMS){
									flag = true;
									break;
								}
							}
								
						}
					}
				}
			}
		} catch (Exception e) {
			faxInfoLogger.error("",e);
		}
		return flag; 
	}
	

	/**
	 * 
	 * Description: Get the query conditions of the common alarms which can be specified by the alarm codes.
	 * @param deptMemberDTO
	 * @return
	 * @author Matthew Xu
	 * @date May 22, 2020
	 */
	private String getCommonQueryCondition(List<AlarmFaxRuleDTO> alarmFaxRuleDTOs) {
		try {
			String queryCondition = "";
			for (AlarmFaxRuleDTO alarmFaxRuleDTO : alarmFaxRuleDTOs) {
				String alarmCode = alarmFaxRuleDTO.getAlarmCode();
				queryCondition = getCodeQueryCondition(alarmCode, queryCondition);
			}
			queryCondition += ")";
			return queryCondition;
		} catch (Exception e) {
			faxInfoLogger.error("",e);
		}
		return null;
	}

	/**
	 * 
	 * Description: Designed for the special alarms such as the high temperture
	 * alarms, unit offline alarms.
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 25, 2020
	 */
	private String getCodeQueryCondition(String alarmCode, String sqlWhere) {
		if (StringUtils.isBlank(sqlWhere))
			return sqlWhere + " (v.code = " + "'" + alarmCode + "'";
		else {
			return sqlWhere + " or v.code = " + "'" + alarmCode + "'";
		}

	}

	/**
	 * 
	 * Description:
	 * @param commonQueryCondition
	 * @return
	 * @author Matthew Xu
	 * @date Jun 28, 2020
	 */
	private String getCommonSql(String commonQueryCondition, int queryDelay) {
		String sql = " select s.id as idsite, s.description as sitename,s.ipaddress,v.code as alarmcode,v.description as alarmdesc,p.tag_id,p.tag_id2,"
				+ " ltree2text(c1.node) as mannode, ltree2text(c2.node) as cusnode,"
				+ " string_agg(cast(a.iddevice as varchar) ,',') as devicearray,"
				+ " string_agg(cast(d.description as varchar) ,',') as devicedescarray,"
				+ " string_agg(cast(a.idalarm as varchar) ,',') as idalarmarray,"
				+ " p.agent_id as agentId "
				+ " from lgalarmactive a"
				+ " inner join cfsupervisors s on a.kidsupervisor = s.id"
				+ " inner join lgdevice d on d.kidsupervisor = s.id"
				+ " inner join lgvariable v on v.iddevice = d.iddevice and v.idvariable=a.idvariable and v.kidsupervisor = s.id"
				+ " inner join private_wechat_receiver p on p.supervisor_id = s.id "
				+ " inner join cfcompany on s.ksite=cfcompany.code "
				+ " left join cfcommunities c1 on c1.node=any(cfcompany.communities) and subltree(c1.node,0,1) = 'MAN' "
				+ " left join cfcommunities c2 on c2.node=any(cfcompany.communities) and subltree(c2.node,0,1) = 'CUS' "
				+ " where a.ackremotetime is null and " + commonQueryCondition
				+ " and d.devmodcode = 'pRackCNL1'"
				+ " and now()-a.inserttime>=interval '" + queryDelay + " minute'"
				+ " group by s.id,s.description,p.tag_id,p.tag_id2,v.code,v.description,s.ipaddress,mannode, cusnode,agentId";
		return sql;
	}

	/**
	 * 
	 * Description: Get the queries of the special alarms.
	 * @param lastFaxQueryTime
	 * @param alarmCode
	 * @param communityCode
	 * @return
	 * @author Matthew Xu
	 * @date May 28, 2020
	 */
	private String getSpecialSql(String alarmCode, int queryDelay) {
		String sql = "";
		boolean isWithDeviceTags = alarmCode.equals(CODE_TAG_HIGH_TEMP);
		if (alarmCode.equals(CODE_UNIT_OFFLINE)) {
			sql += " select s.id as idsite, s.description as sitename,string_agg(cast(a.iddevice as varchar) ,',') as devicearray,p.tag_id,p.tag_id2,s.ipaddress, "
					+ " string_agg(cast(d.description as varchar) ,',') as devicedescarray,"
					+ " string_agg(cast(a.idalarm as varchar) ,',') as idalarmarray,"
					+ " ltree2text(c1.node) as mannode, ltree2text(c2.node) as cusnode,"
					+ " p.agent_id as agentId "
					+ " from lgalarmactive a"
					+ " inner join cfsupervisors s on a.kidsupervisor = s.id"
					+ " inner join lgdevice d on d.kidsupervisor = s.id"
					+ " inner join lgvariable v on v.iddevice = d.iddevice and v.idvariable=a.idvariable and v.kidsupervisor = s.id"
					+ " inner join private_wechat_receiver p on p.supervisor_id = s.id "
					+ " inner join cfcompany on s.ksite=cfcompany.code "
					+ " left join cfcommunities c1 on c1.node=any(cfcompany.communities) and subltree(c1.node,0,1) = 'MAN' "
					+ " left join cfcommunities c2 on c2.node=any(cfcompany.communities) and subltree(c2.node,0,1) = 'CUS' "
					+ " where d.devmodcode='pRackCNL1' "
					+ " and v.addressin= 0 and v.code='OFFLINE' "
					+ " and now()-a.inserttime>=interval '" + queryDelay + " minute'"
					+ " group by s.id,s.description,p.tag_id,p.tag_id2,s.ipaddress,mannode,cusnode,agentId";
		}else {
			String descriptionLimit = "";
			if (alarmCode.equals(CODE_LT_HIGH_TEMP)) {
				descriptionLimit += " and (d.description like '%LT%' or d.description like '%低温%' or d.description like '%冷冻%' or d.description like '%LAT%') ";
			}else if (alarmCode.equals(CODE_MT_HIGH_TEMP)) {
				descriptionLimit += " and (d.description like '%MT%' or d.description like '%中温%' or d.description like '%冷藏%') ";
			}
			sql += " select s.id as idsite, s.description as sitename,"
					+ " string_agg(cast(d.iddevice as varchar) ,',') as devicearray,p.tag_id,p.tag_id2,s.ipaddress,"
					+ " string_agg(cast(d.description as varchar) ,',') as devicedescarray,"
					+ " string_agg(cast(a.idalarm as varchar) ,',') as idalarmarray,"
					+ " ltree2text(c1.node) as mannode, ltree2text(c2.node) as cusnode, "
					+ " p.agent_id as agentId "
					+ " from lgalarmactive a "
					+ " inner join cfsupervisors s on a.kidsupervisor = s.id"
					+ " inner join lgdevice d on d.kidsupervisor = s.id"
					+ " inner join lgvariable v on v.iddevice = d.iddevice and v.idvariable=a.idvariable and v.kidsupervisor = s.id"
					+ " inner join cfdevmdl e on e.code = d.devmodcode"
					+ " inner join cfvarmdl on e.id = cfvarmdl.iddevmdl and v.code = cfvarmdl.code"
					+ " inner join private_wechat_receiver p on p.supervisor_id = s.id "
					+ " inner join cfcompany on s.ksite=cfcompany.code "
					+ " left join cfcommunities c1 on c1.node=any(cfcompany.communities) and subltree(c1.node,0,1) = 'MAN' "
					+ " left join cfcommunities c2 on c2.node=any(cfcompany.communities) and subltree(c2.node,0,1) = 'CUS' ";
					if(isWithDeviceTags)
                    {
                         sql += " inner join "
                                 + " ( "
                                 + "       SELECT distinct kidsupervisor,kiddevice,tag "
                                 + "   FROM ( "
                                 + "       SELECT kidsupervisor,kiddevice,unnest(tags) tag "
                                 + "       FROM tags.devicestags) x "
                                 + "   WHERE lower(tag) LIKE '#c_%' "
                                 + "   ) as tagdevice on a.kidsupervisor=tagdevice.kidsupervisor and a.iddevice=tagdevice.kiddevice and EXTRACT(epoch FROM CAST(now() AS TIMESTAMP)) - EXTRACT(epoch FROM CAST(a.inserttime AS TIMESTAMP)) >" 
                                 + (isNowDisturbPeriod() ? "(cast((string_to_array(tagdevice.tag,'_'))[3] as integer) *60)" : "(cast((string_to_array(tagdevice.tag,'_'))[2] as integer) *60)");
                    }
					sql += " where a.ackremotetime is null "
					+ " and (cfvarmdl.parameter='hightempalarm' or v.description = '高温报警')";
					if(!isWithDeviceTags)
					{
					    sql += "and (a.kidsupervisor,a.iddevice) not in ("
					            +"SELECT distinct kidsupervisor,kiddevice "
		                                 + "   FROM ( "
		                                 + "       SELECT kidsupervisor,kiddevice,unnest(tags) tag "
		                                 + "       FROM tags.devicestags) x "
		                                 + "   WHERE lower(tag) LIKE '#c_%' "
					            + ")";
					}
					sql += descriptionLimit
					+ " and now()-a.inserttime>=interval '" + queryDelay + " minute'"
					+ " group by s.id,s.description,p.tag_id,p.tag_id2,s.ipaddress,mannode, cusnode,agentId";
		}

		return sql;

	}
	
	/**
	 * 
	 * Description:
	 * @return
	 * @author Matthew Xu
	 * @date Jul 21, 2020
	 */
	private static boolean isNowDisturbPeriod(){
		return DateTool.isNowInPeriods(DO_NOT_DISTURB_BEGIN, DO_NOT_DISTURB_END);
	}
	
	/**
	 * 
	 * Description:
	 * @param alarmFaxRuleDTO
	 * @return
	 * @author Matthew Xu
	 * @date Jul 21, 2020
	 */
	private int getQueryDelay(AlarmFaxRuleDTO alarmFaxRuleDTO){
		return isNowDisturbPeriod() ? alarmFaxRuleDTO.getNightQueryDelay() : alarmFaxRuleDTO.getDayQueryDelay();
	}

}
