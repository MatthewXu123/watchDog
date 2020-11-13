package watchDog.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.chanjar.weixin.common.util.StringUtils;


public class StringTool {

	public static String format(String str,int len)
	{
		return format(str,len," ");
	}
	public static String format(String str, int len, String c) {
		if (str == null || str.length() == 0) {
			str = "";
		}
		if (str.length() == len) {
			return str;
		}
		if (str.length() > len) {
			return str.substring(0, len);
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() < len) {
			sb.append(c);
		}
		return sb.toString();
	}
	public static void output(int logType,String... args)
	{
		StringBuffer b = new StringBuffer();
		if(args.length == 1)
			b.append(args[0]);
		else
			for(String str: args)
			{
				b.append(StringTool.format(str,15));
			}
	}
	public static <T>List<T> removeFromArray(T[] array,T element)
	{
		if(array == null)
			return null;
		List<T> list = new ArrayList<T>();
		for(T t:array)
		{
			if(!t.equals(element))
			{
				list.add(t);
			}
		}
		return list;
	}
	
	public static <T>List<T> toArray(T[] array)
	{
		if(array == null)
			return new ArrayList<T>();
		List<T> list = new ArrayList<T>();
		for(T t:array)
		{
			list.add(t);
		}
		return list;
	}
	
	public static String getStringInQuotation(String str)
	{
		String key = "\"";
		if(!(str.indexOf(key)>=0 && str.indexOf(key) != str.lastIndexOf(key)))
			return null;
		return str.substring(str.indexOf(key)+1,str.lastIndexOf(key));
	}
	public static String utf8(String str)
	{
		try {
			return new String(str.getBytes("ISO8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static int strNum(String string,String str)
	{
		if(StringUtils.isBlank(string)||StringUtils.isBlank(str))
			return -1;
		int l_total = string.length();
		string = string.replaceAll(str, "");
		int l = string.length();
		return l_total-l;
	}
	/**
	 * 数组转换成字符串
	 * @param array
	 * @param split
	 * @return
	 * @author MatthewXu
	 * @param <T>
	 * @date Jun 4, 2019
	 */
	public static String arrayToString(int[] array,String split){
		if(array == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < array.length; i++){
			sb.append(array[i]);
			if(i < array.length - 1)
				sb.append(split);
		}
		return sb.toString();
	}
	
	public static boolean isInArray(String[] array,String str)
	{
	    if(array == null || str == null)
	        return false;
	    for(String s:array)
	    {
	        if(str.equals(s))
	            return true;
	    }
	    return false;
	}
}
