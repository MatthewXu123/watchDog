package watchDog.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import watchDog.util.DateTool;

/**
* Description: 
* @author MatthewXu
* @date May 9, 2019
*/
public class FaxTypeMgr {

	private static JSONObject faxTypeJson;
	
	/**
	 * 读取配置文件，并转为json
	 * @author MatthewXu
	 * @date May 15, 2019
	 */
	public static void getFaxTypeJSON(){
		StringBuilder faxtype = new StringBuilder();
    	try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream("C:\\watchDog\\conf\\faxtype.json"), "UTF-8");
			BufferedReader breader = new BufferedReader(isr);
			String line;
			while((line = breader.readLine()) != null){
				faxtype.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	faxTypeJson = JSON.parseObject(faxtype.toString());
	}
	
	/**
	 * 获取不同状态的传真报警类型配置
	 * @param alarmStatus active或reset
	 * @return
	 * @author MatthewXu
	 * @date May 15, 2019
	 */
	public static Map<String, Integer> getTypeMap(String alarmStatus) {
		Map<String, Integer> typeMap = null;
		JSONObject typeMapJson = faxTypeJson.getJSONObject(alarmStatus);
		if(typeMapJson != null){
			typeMap = JSON.toJavaObject(typeMapJson, Map.class);
		}
		return typeMap;
	}
	
	/**
	 * @param typeMap
	 * @return
	 * @author MatthewXu
	 * @date May 15, 2019
	 */
	public static String getCodeWhere(Map<String, Integer> typeMap) {
		String codeWhere = "";
		if(typeMap != null && typeMap.size() !=0){
			List<String> keyList = new ArrayList<>(typeMap.keySet());
			String[] types = keyList.toArray(new String[keyList.size()]);
			for(int i = 0 ;i < types.length ; i++){
					if(i == 0)
						codeWhere += " (v.code = "+ "'" + types[i] + "'";
					else
						codeWhere += " or v.code = "+ "'" + types[i] + "'";
			}
			codeWhere += ")";
		}
		return codeWhere;
	}
	
}
