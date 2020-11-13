
package watchDog.config.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.RecordSet;

/**
 * Description:
 * @author Matthew Xu
 * @date May 9, 2020
 */
public class DeviceModelConfig extends BaseJSONConfig{

	private static final String[] SET_VALUE = {"digital", "main", "setpoint"};
	
	private static final String DEVMDL_CONFIG_PTAH = "devmdl.json";
	
	private static JSONObject devmdlJson;
	
	public static void getConfigJSONStr() {
		devmdlJson = JSON.parseObject(readFromPath(basePath + DEVMDL_CONFIG_PTAH));
	}

	public static List<LinkedHashMap<String, Object>> getCodeMapList(String devmdlCode) {
		JSONObject subDevmdlJson = devmdlJson.getJSONObject(devmdlCode);
		if(subDevmdlJson == null){
			return null;
		}
		LinkedHashMap<String, String[]> subDevmdlMap = JSON.parseObject(subDevmdlJson.toJSONString(), new TypeReference<LinkedHashMap<String, String[]>>() {
        });
		List<LinkedHashMap<String, Object>> codeList = new ArrayList<LinkedHashMap<String, Object>>();
		for (String setvalue : SET_VALUE) {
			String[] params = subDevmdlMap.get(setvalue);
			LinkedHashMap<String, Object> codeMap = new LinkedHashMap<>();
			if(params.length != 0){
				for (String param : params) {
					String[] split = param.split("\\$\\$");
					codeMap.put(split[0], split[1]);
				}
			}else{
				codeMap = null;
			}
			codeList.add(codeMap);
		}
		return codeList;
	}
	
	public static boolean isDevmdlCode(String devmdlCode) {
		if(devmdlJson == null)
			return false;
		JSONObject subDevmdlJson = devmdlJson.getJSONObject(devmdlCode);
		if (subDevmdlJson == null) {
			return false;
		}
		return true;
	}
	
	public static String[] getCodeArray(String devmdlCode) {
		if(devmdlJson == null){
			return null;
		}
		JSONObject subDevmdlJson = devmdlJson.getJSONObject(devmdlCode);
		if(subDevmdlJson == null){
			return null;
		}
		LinkedHashMap<String, String[]> subDevmdlMap = JSON.parseObject(subDevmdlJson.toJSONString(), new TypeReference<LinkedHashMap<String, String[]>>() {
        });
		List<String> codeList = new ArrayList<String>();
		for (String setvalue : SET_VALUE) {
			String[] params = subDevmdlMap.get(setvalue);
			for (String param : params) {
				String[] split = param.split("\\$\\$");
				codeList.add(split[0]);
			}
		}
		
        String[] codeArray = new String[codeList.size()];
        int i = 0;
        for (String codeListValue : codeList) {
			codeArray[i++] = codeListValue;
		}
		return codeArray;
	}
	
	public static String getdevmdlCode(String ip, String devCode) {
		String sql = "select d.devmodcode from lgdevice as d inner join cfsupervisors as s on d.kidsupervisor=s.id where d.code=? and s.ipaddress=?";
		Object[] params = new Object[]{devCode, ip};
		String devmdlCode = null;
		try {
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql, params);
			if(rs != null && rs.size() > 0){
				devmdlCode = (String) rs.get(0).get(0);
			}
		} catch (DataBaseException e) {
			e.printStackTrace();
		}
		return devmdlCode;
	}

}
