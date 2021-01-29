
package watchDog.config.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import watchDog.bean.config.SpecialAlarmAdviceDTO;
import watchDog.util.ObjectUtils;

/**
 * Description:
 * @author Matthew Xu
 * @date Jan 27, 2021
 */
public class SpecialAlarmAdviceConfig extends BaseJSONConfig{

	private static final String PATH_ALARM_ADVICE_CONFIG = "special_alarm_advice.json";
	
	private static List<SpecialAlarmAdviceDTO> advices = new ArrayList<>();
	
	private static Map<Integer, SpecialAlarmAdviceDTO> idAlarmAdviceMap = new HashMap<>();
	
	static{
		if(ObjectUtils.isCollectionEmpty(advices)){
			getConfig();
		}
	}
	
	public static void getConfig() {
		advices = JSON.parseArray(readFromPath(basePath + PATH_ALARM_ADVICE_CONFIG), SpecialAlarmAdviceDTO.class);
		for (SpecialAlarmAdviceDTO advice : advices) {
			idAlarmAdviceMap.put(advice.getId(), advice);
		}
	}
	
	public static SpecialAlarmAdviceDTO getAlarmAdviceById(int id){
		return idAlarmAdviceMap.get(id);
	}
}
