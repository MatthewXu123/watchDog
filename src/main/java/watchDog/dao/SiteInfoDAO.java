package watchDog.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import watchDog.bean.SiteInfo;
import watchDog.database.DataBaseException;
import watchDog.database.Record;
import watchDog.database.RecordSet;
import watchDog.service.AlarmService;
import watchDog.util.DateTool;

/**
 * Description:
 * @author Matthew Xu
 * @date May 14, 2020
 */
public class SiteInfoDAO extends BaseDAO{
	
	public static final SiteInfoDAO INSTANCE = new SiteInfoDAO();
	
	private SiteInfoDAO(){}

	public boolean isSiteExist(int supervisorId)
	{
		String sql = "select count(*)::int from private_wechat_receiver where supervisor_id=?";
		Object[] params = {supervisorId};
		RecordSet rs = null;
        try{
	        rs = dataBaseMgr.executeQuery(sql,params);
	        if((int)rs.get(0).get(0)>0)
	        	return true;
        } catch (Exception e) {
		}
        return false;
	}
	
	public List<SiteInfo> getList(Boolean hasTag){
		String pattern = "yyyy-MM-dd HH:mm:ss";
		String last30 = DateTool.format(DateTool.addDays(-30),pattern);
		List<SiteInfo> result = new ArrayList<SiteInfo>();
		String sql_add = "";
		if(hasTag == null)
			sql_add = "";
		else
		{
			if(hasTag)
				sql_add = "and private_wechat_receiver.tag_id is not null and private_wechat_receiver.tag_id <> '' ";
			else
				sql_add = "and (private_wechat_receiver.tag_id is null or private_wechat_receiver.tag_id ='') ";
			//sql_add += " and cfsupervisors.ipaddress like '192.%' order by cast(replace(cfsupervisors.ipaddress,'192.168.88.','') as integer)";
			//sql_add += " order by cast(split_part(cfsupervisors.ipaddress,'.',4) as integer)";
		}
		String sql = "select cfsupervisors.description,cfsupervisors.ipaddress,cfsupervisors.ident,cfsupervisors.ktype,cfsupervisors.id as idsite,status.lastsynch,"
				+ "private_wechat_receiver.*,cfcommunities1.description as mandescription,ltree2text(cfcommunities1.node) as mannode, "
				+ "cfcommunities2.description as cusdescription,ltree2text(cfcommunities2.node) as cusnode,"
				+ "stags.tags as supervisortags,cfsupervisors.probeissue,";
		if(hasTag == null)
				sql += "last30.cnt,last30hight.cnt as cnt_high,active.cnt as active_cnt ";
		else
				sql += "0 as cnt,0 as cnt_high,0 as active_cnt ";
		sql += "from cfsupervisors left join private_wechat_receiver on "
		+ "private_wechat_receiver.supervisor_id = cfsupervisors.id "
		+ "inner join cfcompany as p on cfsupervisors.ksite=p.code "
		+ "left join cfcommunities cfcommunities1 on cfcommunities1.node=any(p.communities) and  subltree(cfcommunities1.node,0,1) = 'MAN' "
		+ "left join cfcommunities cfcommunities2 on cfcommunities2.node=any(p.communities) and  subltree(cfcommunities2.node,0,1) = 'CUS' "
		+ "left join lgsupervstatus as status on cfsupervisors.id=status.kidsupervisor "
		+ "left join tags.supervisortags stags on cfsupervisors.id = stags.kidsupervisor ";
		if(hasTag == null)
				sql += "left join( "
				+ "select reset.kidsupervisor,count(*)::int as cnt from lgalarmrecall as reset "
				+ "inner join lgdevice as device on device.kidsupervisor=reset.kidsupervisor and device.iddevice=reset.iddevice  "
				+ "where endtime>='"+last30+"' and "+AlarmService.importantAlarmSQL("reset") 
				+ "group by reset.kidsupervisor "
				+ ") as last30 on  last30.kidsupervisor=cfsupervisors.id "
				+ "left join( "
				+ "select reset.kidsupervisor,count(*)::int as cnt from lgalarmrecall as reset "
				+ "inner join lgvariable as var on var.kidsupervisor=reset.kidsupervisor and var.idvariable=reset.idvariable "
				+ "inner join lgdevice as device on device.kidsupervisor=var.kidsupervisor and device.iddevice=var.iddevice  "
				+ "where endtime>='"+last30+"' and (var.description='高温报警') "
				+ "group by reset.kidsupervisor "
				+ ") as last30hight on  last30hight.kidsupervisor=cfsupervisors.id "
				+ "left join( "
				+ "select active.kidsupervisor,count(*)::int as cnt "
				+ "from lgalarmactive as active  "
				+ "inner join lgdevice as device on device.kidsupervisor=active.kidsupervisor and device.iddevice=active.iddevice  "
				+ "where "+AlarmService.importantAlarmSQL("active")
				+ "group by active.kidsupervisor "
				+ ") as active on  active.kidsupervisor=cfsupervisors.id ";
				sql += "where cfsupervisors.ktype<>'remote' "+sql_add;
				sql += " order by cfsupervisors.ipaddress ";
		RecordSet rs = null;
        try{
	        rs = dataBaseMgr.executeQuery(sql);
	        boolean loadEnergy = false;
	        for(int i=0;i<rs.size();i++)
	        {
	        	Record r = rs.get(i);
	        	if(hasTag == null)
	        		loadEnergy = true;
	        	SiteInfo s = new SiteInfo(r,loadEnergy);
	        	result.add(s);
	        }
        }
        catch(Exception ex){
        	ex.printStackTrace();
        }
        //Collections.sort(result);
		return result;
	}
	
	public void saveOne(int supervisorId, Date deadline, boolean checkNetWorkFlag, String agentId, int channel, String tagId, String tagId2, String tagId3,String comment ){
		Object[] params = new Object[]{supervisorId,deadline,checkNetWorkFlag,agentId,channel,tagId,tagId2,tagId3,comment};
		String sql="insert into private_wechat_receiver(supervisor_id,deadline,checknetwork,agent_id,channel,tag_id,tag_id2,tag_id3,comment) values(?,?,?,?,?,?,?,?,?);";
		try {
			dataBaseMgr.executeUpdate(sql, params);
		} catch (DataBaseException e) {
			e.printStackTrace();
		}
	}
	
	public void updateOne(int supervisorId, Date deadline, boolean checkNetWorkFlag, String agentId, int channel, String tagId, String tagId2, String tagId3, String comment){
		String sql="update private_wechat_receiver set deadline = ?,checknetwork = ?,agent_id=?,channel=?,tag_id=?,tag_id2=?,tag_id3=?,comment=? where supervisor_id=?;";
		Object[] params = new Object[]{deadline,checkNetWorkFlag,agentId,channel,tagId,tagId2,tagId3,comment,supervisorId};
		try {
			dataBaseMgr.executeUpdate(sql, params);
		} catch (DataBaseException e) {
			e.printStackTrace();
		}
	}
	public void updateDeadline(int supervisorId, Date deadline){
        String sql="update private_wechat_receiver set deadline = ? where supervisor_id=?;";
        Object[] params = new Object[]{deadline,supervisorId};
        try {
            dataBaseMgr.executeUpdate(sql, params);
        } catch (DataBaseException e) {
            e.printStackTrace();
        }
    }
	public void updateCheckingNetwork(int supervisorId, boolean checkNetWorkFlag){
        String sql="update private_wechat_receiver set checknetwork = ? where supervisor_id=?;";
        Object[] params = new Object[]{checkNetWorkFlag,supervisorId};
        try {
            dataBaseMgr.executeUpdate(sql, params);
        } catch (DataBaseException e) {
            e.printStackTrace();
        }
    }

}
