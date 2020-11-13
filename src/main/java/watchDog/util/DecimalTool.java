package watchDog.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DecimalTool {

	public static final int ADD = 1;
	public static final int SUBTRACT = 2;
	public static final int MULTIPLY = 3;
	public static final int DEVIDE = 4;
	public static String format(double v)
	{
		return format(v,"#,###.###");
	}
	public static String format(int v)
	{
		return format((double)v);
	}
	public static String format(double v,String format)
	{
		DecimalFormat df=new DecimalFormat(format);
		return df.format(v);
	}
	public static int compare(double v1,double v2)
	{
		BigDecimal data1 = new BigDecimal(v1);  
	    BigDecimal data2 = new BigDecimal(v2);
	    return data1.compareTo(data2);
	}
	public static double multiplyFactor(double v1,BigDecimal factor)
	{
		BigDecimal b1 = new BigDecimal(v1);
		b1 = b1.multiply(factor).setScale(2, BigDecimal.ROUND_HALF_UP);
		return b1.doubleValue();
	}
	public static double getDiffPercent(double basic,double price)
	{
		return (price-basic)/basic;
	}
	
	public static double decimal(double v,int number)
	{
		BigDecimal b = new BigDecimal(v);  
		return b.setScale(number,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static BigDecimal BigDecimal(int type,double v1,double v2) throws Exception
	{
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		switch(type)
		{
			case ADD:
				return b1.add(b2);
			case SUBTRACT:
				return b1.subtract(b2);
			case MULTIPLY:
				return b1.multiply(b2);
			case DEVIDE:
				return b1.divide(b2);
			default:
				throw new Exception("no operation");
		}
	}
	public static BigDecimal BigDecimal(int type,BigDecimal b1,double v2) throws Exception
	{
		BigDecimal b2 = new BigDecimal(v2);
		switch(type)
		{
			case ADD:
				return b1.add(b2);
			case SUBTRACT:
				return b1.subtract(b2);
			case MULTIPLY:
				return b1.multiply(b2);
			case DEVIDE:
				return b1.divide(b2);
			default:
				throw new Exception("no operation");
		}
	}
}
