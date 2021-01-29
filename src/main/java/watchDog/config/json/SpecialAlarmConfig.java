
package watchDog.config.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import watchDog.bean.config.SpecialAlarmAdviceDTO;
import watchDog.bean.config.SpecialAlarmDTO;
import watchDog.util.ObjectUtils;

/**
 * Description:
 * @author Matthew Xu
 * @date Jan 27, 2021
 */
public class SpecialAlarmConfig extends BaseJSONConfig{

	private static final String PATH = "special_alarm.json";

	private static List<SpecialAlarmDTO> advices;
	
	private static Map<String, SpecialAlarmDTO> codeAlarmMap;
	
	static{
		if(ObjectUtils.isCollectionEmpty(advices)){
			getConfig();
		}
	}
	
	public static void getConfig() {
		// init
		advices = new ArrayList<>();
		codeAlarmMap = new HashMap<>();
		
		JSONArray jsonArray = JSON.parseArray(readFromPath(basePath + PATH));
		for(int i = 0; i < jsonArray.size(); i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			SpecialAlarmDTO specialAlarmDTO = new SpecialAlarmDTO();
			String code = jsonObject.getString("code");
			specialAlarmDTO.setCode(code);
			specialAlarmDTO.setHint(jsonObject.getString("hint"));
			specialAlarmDTO.setAdvices(getAlarmAdvices((JSONArray)jsonObject.get("adviceIds")));
			advices.add(specialAlarmDTO);
			codeAlarmMap.put(code, specialAlarmDTO);
		}
	}
	
	private static List<SpecialAlarmAdviceDTO> getAlarmAdvices(JSONArray ids){
		List<SpecialAlarmAdviceDTO> list = new ArrayList<>();
		for (int i = 0; i < ids.size(); i++) {
			list.add(SpecialAlarmAdviceConfig.getAlarmAdviceById(ids.getIntValue(i)));
		}
		return list;
	}
	
	public static SpecialAlarmDTO getAlarmByCode(String code) {
		return codeAlarmMap.get(code);
	}
	
	public static boolean isIncluded(String code){
		return codeAlarmMap.keySet().contains(code);
	}
}
