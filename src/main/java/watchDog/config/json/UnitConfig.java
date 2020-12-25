
package watchDog.config.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import watchDog.util.ObjectUtils;

/**
 * Description:
 * @author Matthew Xu
 * @date May 9, 2020
 */
public class UnitConfig extends BaseJSONConfig{

	private static final String UNIT_CONFIG_PTAH = "unit.json";
	
	private static Map unitOFFMap;
	
	static{
		if(ObjectUtils.isMapEmpty(unitOFFMap))
			getConfig();
	}
	
	public static void getConfig() {
		unitOFFMap = JSON.parseObject(readFromPath(basePath + UNIT_CONFIG_PTAH)).toJavaObject(Map.class);		
	}
	
	public static String[] getUnitOFFDevmdlCode()
	{
		if(unitOFFMap == null)
			return new String[0];
		Iterator it = unitOFFMap.keySet().iterator();
		List<String> list = new ArrayList<>();
		while(it.hasNext())
		{
			list.add((String)it.next());
		}
		return (String[])list.toArray(new String[list.size()]);
	}
	public static String getUnitOFFVar(String devcode)
	{
		if(unitOFFMap == null)
			return null;
		return (String)unitOFFMap.get(devcode);
	}
	
}
