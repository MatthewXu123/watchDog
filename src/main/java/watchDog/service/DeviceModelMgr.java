package watchDog.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.RecordSet;

public class DeviceModelMgr {
	private static String[] SET_VALUE = {"digital", "main", "setpoint"};
	private static JSONObject devmdlJson;
	private static Map unitOFFMap;
	
	public static void getdevmdlJson(){
		StringBuilder devmdlStr = new StringBuilder();
    	try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream("C:\\watchDog\\conf\\devmdl.json"), "UTF-8");
			BufferedReader breader = new BufferedReader(isr);
			String line;
			while((line = breader.readLine()) != null){
				devmdlStr.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	devmdlJson = JSON.parseObject(devmdlStr.toString());
    
    	StringBuilder unitOffStr = new StringBuilder();
    	try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream("C:\\watchDog\\conf\\unitOFF.json"), "UTF-8");
			BufferedReader breader = new BufferedReader(isr);
			String line;
			while((line = breader.readLine()) != null){
				unitOffStr.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	JSONObject unitOFFJson = JSON.parseObject(unitOffStr.toString());
    	unitOFFMap = unitOFFJson.toJavaObject(Map.class);
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
	public static void main(String[] args)
	{
		getdevmdlJson();
		String[] r = getUnitOFFDevmdlCode();
		int a = 1;
		int b = a+1;
	}
}
