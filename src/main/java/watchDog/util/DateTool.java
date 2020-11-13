package watchDog.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author CuiRan
 * @version 1.0.0
 * @desc
 */
public class DateTool {
	/**
	 * 缺省的日期显示格式： yyyy-MM-dd
	 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * 缺省的日期时间显示格式：yyyy-MM-dd HH:mm:ss
	 */
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 1s中的毫秒数
	 */
	//private static final int MILLIS = 1000;

	/**
	 * 一年当中的月份数
	 */
	private static final int MONTH_PER_YEAR = 12;

	public static final String CONSTANT_DAY = "day";
	public static final String CONSTANT_HOUR = "hour";
	public static final String CONSTANT_MINUTE = "minute";
	public static final String CONSTANT_SECOND = "second";
	
	/**
	 * 私有构造方法，禁止对该类进行实例化
	 */
	private DateTool() {
	}

	/**
	 * 得到系统当前日期时间
	 * 
	 * @return 当前日期时间
	 */
	public static Date getNow() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 得到用缺省方式格式化的当前日期
	 * 
	 * @return 当前日期
	 */
	public static String getDate() {
		return getDateTime(DEFAULT_DATE_FORMAT);
	}

	/**
	 * 得到用缺省方式格式化的当前日期及时间
	 * 
	 * @return 当前日期及时间
	 */
	public static String getDateTime() {
		return getDateTime(DEFAULT_DATETIME_FORMAT);
	}

	/**
	 * 得到系统当前日期及时间，并用指定的方式格式化
	 * 
	 * @param pattern
	 *            显示格式
	 * @return 当前日期及时间
	 */
	public static String getDateTime(String pattern) {
		Date datetime = Calendar.getInstance().getTime();
		return format(datetime, pattern);
	}

	/**
	 * 得到用指定方式格式化的日期
	 * 
	 * @param date
	 *            需要进行格式化的日期
	 * @param pattern
	 *            显示格式
	 * @return 日期时间字符串
	 */
	public static String format(Date date)
	{
		if(date == null)
			return "";
		return format(date,"yyyy-MM-dd");
	}
	public static String format(Date date, String pattern) {
		if (null == pattern || "".equals(pattern)) {
			pattern = DEFAULT_DATETIME_FORMAT;
		}
		if(date == null)
			return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}

	/**
	 * 得到当前年份
	 * 
	 * @return 当前年份
	 */
	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);

	}

	/**
	 * 得到当前月份
	 * 
	 * @return 当前月份
	 */
	public static int getCurrentMonth() {
		// 用get得到的月份数比实际的小1，需要加上
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 得到当前日
	 * 
	 * @return 当前日
	 */
	public static int getCurrentDay() {
		return Calendar.getInstance().get(Calendar.DATE);
	}

	/**
	 * @param date
	 * @param minutes
	 * @return
	 * @author MatthewXu
	 * @date May 9, 2019
	 */
	public static Date addMinutes(Date date, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + minutes);
		return calendar.getTime();
	}
	
	/**
	 * 获取指定日期的月份
	 * @param date
	 * @return
	 * @author MatthewXu
	 * @date Apr 1, 2019
	 */
	public static int getAppointedMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}
	
	/**
	 * 取得当前日期以后若干天的日期。如果要得到以前的日期，参数用负数。 例如要得到上星期同一天的日期，参数则为-7
	 * 
	 * @param days
	 *            增加的日期数
	 * @return 增加以后的日期
	 */
	public static Date addDays(int days) {
		return add(getNow(), days, Calendar.DATE);
	}

	/**
	 * 取得指定日期以后若干天的日期。如果要得到以前的日期，参数用负数。
	 * 
	 * @param date
	 *            基准日期
	 * @param days
	 *            增加的日期数
	 * @return 增加以后的日期
	 */
	public static Date addDays(Date date, int days) {
		return add(date, days, Calendar.DATE);
	}

	/**
	 * 取得当前日期以后某月的日期。如果要得到以前月份的日期，参数用负数。
	 * 
	 * @param months
	 *            增加的月份数
	 * @return 增加以后的日期
	 */
	public static Date addMonths(int months) {
		return add(getNow(), months, Calendar.MONTH);
	}

	/**
	 * 取得指定日期以后某月的日期。如果要得到以前月份的日期，参数用负数。 注意，可能不是同一日子，例如2003-1-31加上一个月是2003-2-28
	 * 
	 * @param date
	 *            基准日期
	 * @param months
	 *            增加的月份数
	 * @return 增加以后的日期
	 */
	public static Date addMonths(Date date, int months) {
		return add(date, months, Calendar.MONTH);
	}

	/**
	 * 内部方法。为指定日期增加相应的天数或月数
	 * 
	 * @param date
	 *            基准日期
	 * @param amount
	 *            增加的数量
	 * @param field
	 *            增加的单位，年，月或者日
	 * @return 增加以后的日期
	 */
	public static Date add(Date date, int amount, int field) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.add(field, amount);

		return calendar.getTime();
	}

	/**
	 * 通过date对象取得格式为小时:分钟的实符串
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getHourMin(Date date) {
		StringBuffer sf = new StringBuffer();
		sf.append(date.getHours());
		sf.append(":");
		sf.append(date.getMinutes());
		return sf.toString();
	}

	/**
	 * 精确计算两个时间差值
	 * @param one
	 * @param two
	 * @param type day,hour,minute,second
	 * @return
	 * @author MatthewXu
	 * @date May 13, 2019
	 */
	public static long diffTime(Date one, Date two, String type) {
		if(one == null || two == null)
			return 999999;
		int factor = 0;
		if(type.equalsIgnoreCase(CONSTANT_DAY))
			factor = 24 * 60 * 60;
		if(type.equalsIgnoreCase(CONSTANT_HOUR))
			factor = 60 * 60;
		if(type.equalsIgnoreCase(CONSTANT_MINUTE))
			factor = 60;
		if(type.equalsIgnoreCase(CONSTANT_SECOND))
			factor = 1;
		final int MILISECONDS = factor * 1000;
		BigDecimal r = new BigDecimal(new Double((one.getTime() - two.getTime()))
				/ MILISECONDS);
		return Math.round(r.doubleValue());
	}
	
	/**
	 * 计算两个日期相差天数。 用第一个日期减去第二个。如果前一个日期小于后一个日期，则返回负数
	 * 
	 * @param one
	 *            第一个日期数，作为基准
	 * @param two
	 *            第二个日期数，作为比较
	 * @return 两个日期相差天数
	 */
	public static long diffDays(Date one, Date two) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(one);
		calendar.set(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONDAY),
				calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		Date d1 = calendar.getTime();
		calendar.clear();
		calendar.setTime(two);
		calendar.set(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONDAY),
				calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		Date d2 = calendar.getTime();
		final int MILISECONDS = 24 * 60 * 60 * 1000;
		BigDecimal r = new BigDecimal(new Double((d1.getTime() - d2.getTime()))
				/ MILISECONDS);
		return Math.round(r.doubleValue());
	}

	/**
	 * 计算两个日期相差月份数 如果前一个日期小于后一个日期，则返回负数
	 * 
	 * @param one
	 *            第一个日期数，作为基准
	 * @param two
	 *            第二个日期数，作为比较
	 * @return 两个日期相差月份数
	 */
	public static int diffMonths(Date one, Date two) {

		Calendar calendar = Calendar.getInstance();

		// 得到第一个日期的年分和月份数
		calendar.setTime(one);
		int yearOne = calendar.get(Calendar.YEAR);
		int monthOne = calendar.get(Calendar.MONDAY);
		// 得到第二个日期的年份和月份
		calendar.setTime(two);
		int yearTwo = calendar.get(Calendar.YEAR);
		int monthTwo = calendar.get(Calendar.MONDAY);

		return (yearOne - yearTwo) * MONTH_PER_YEAR + (monthOne - monthTwo);
	}

	/**
	 * 获取某一个日期的年份
	 * 
	 * @param d
	 * @return
	 */
	public static int getYear(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 将一个字符串用给定的格式转换为日期类型。 <br>
	 * 注意：如果返回null，则表示解析失败
	 * 
	 * @param datestr
	 *            需要解析的日期字符串
	 * @param pattern
	 *            日期字符串的格式，默认为"yyyy-MM-dd"的形式
	 * @return 解析后的日期
	 */
	public static Date parse(String datestr, String pattern) {
		Date date = null;

		if (null == pattern || "".equals(pattern)) {
			pattern = DEFAULT_DATE_FORMAT;
		}

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			date = dateFormat.parse(datestr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}
	public static Date parse(String datestr) {
		return parse(datestr,"yyyy-MM-dd");
	}

	/**
	 * 返回本月的最后一天
	 * 
	 * @return 本月最后一天的日期
	 */
	public static Date getMonthLastDay() {
		return getMonthLastDay(getNow());
	}

	/**
	 * 返回给定日期中的月份中的最后一天
	 * 
	 * @param date
	 *            基准日期
	 * @return 该月最后一天的日期
	 */
	public static Date getMonthLastDay(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		// 将日期设置为下一月第一天
		calendar.set(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH) + 1, 1);

		// 减去1天，得到的即本月的最后一天
		calendar.add(Calendar.DATE, -1);

		return calendar.getTime();
	}

	/**
	 * 计算两个具体日期之间的秒差，第一个日期-第二个日期
	 * 
	 * @param date1
	 * @param date2
	 * @param onlyTime
	 *            是否只计算2个日期的时间差异，忽略日期，true代表只计算时间差
	 * @return
	 */
	public static long diff(Date date1, Date date2, boolean onlyTime) {
		if (onlyTime) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date1);
			// calendar.set(1984, 5, 24);
			long t1 = calendar.getTimeInMillis();
			calendar.setTime(date2);
			// calendar.set(1984, 5, 24);
			long t2 = calendar.getTimeInMillis();
			return (t1 - t2) ;
		} else {
			return (date1.getTime() - date2.getTime());
		}
	}

	/**
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long diff(Date date1, Date date2) {
		return diff(date1, date2, false);
	}

	/**
	 * 根据日期确定星期几:1-星期日，2-星期一.....s
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		int mydate = cd.get(Calendar.DAY_OF_WEEK);
		return mydate;
	}

	// /**
	// * 将2010-06-01转换为20100601格式
	// * @param date
	// * @return
	// */
	// public static String toVODate(String date) {
	// if (StringUtil.isEmpty(date)) {
	// //return DateFormatUtils.format(new Date(), "yyyy-MM-dd");
	// return "";
	// }
	// Date tdate;
	// try {
	// tdate = new SimpleDateFormat("yyyyMMdd").parse(date);
	// } catch (ParseException e) {
	// e.printStackTrace();
	// throw new SmsException("日期转换异常!");
	// }
	// return DateFormatUtils.format(tdate, "yyyy-MM-dd");
	// }
	//
	// /**
	// * 将20100601转换为2010-06-01格式
	// * @param date
	// * @return
	// */
	// public static String toDomainDate(String date) {
	// if (StringUtil.isEmpty(date)) {
	// return "";
	// }
	// Date tdate;
	// try {
	// tdate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
	// } catch (ParseException e) {
	// throw new BusinessException("上收时间或者启用时间格式不正确!");
	// }
	// return DateFormatUtils.format(tdate, "yyyyMMdd");
	// }

	/**
	 * 验证用密码是否在有效期内(跟当前日期比较)
	 * 
	 * @param format
	 *            "yyyyMMdd"
	 * @param validDate
	 * @return
	 */
	public static boolean isValidDate(String validDate, String format) {
		Date valid = parse(validDate, format);
		Date now = new Date();
		String nowStr = new SimpleDateFormat(format).format(now);
		try {
			now = new SimpleDateFormat(format).parse(nowStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return valid.after(now);
	}
	/**
	 * 简单判断下日期和日期格式是否相符
	 * @param dateStr
	 * @param format
	 * @return
	 * @author MatthewXu
	 * @date Mar 29, 2019
	 */
	public static boolean isQualifiedDate(String dateStr, String format) {
		/*SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			dateFormat.setLenient(false);
			dateFormat.parse(dateStr);
		} catch (ParseException e) {
			return false;
		}
		return true;*/
		return dateStr.split("-").length == format.split("-").length;
	}
	
	public static boolean isSameDay(Date day1, Date day2) {
		if(day1 == null || day2 == null)
			return false;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String ds1 = sdf.format(day1);
	    String ds2 = sdf.format(day2);
	    if (ds1.equals(ds2)) {
	        return true;
	    } else {
	        return false;
	    }
	}
	public static boolean isSameMonth(Date day1, Date day2) {
		if(day1 == null || day2 == null)
			return false;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	    String ds1 = sdf.format(day1);
	    String ds2 = sdf.format(day2);
	    if (ds1.equals(ds2)) {
	        return true;
	    } else {
	        return false;
	    }
	}

	public static String msgTime(Date time)
	{
		if(time == null)
			return "";
		Date now = new Date();
		if(isSameDay(time,now))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			return sdf.format(time);
		}
		now = addDays(now,-1);
		if(isSameDay(time,now))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			return "昨天"+sdf.format(time);
		}
		now = addDays(now,-1);
		if(isSameDay(time,now))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			return " 前天 "+sdf.format(time);
		}
		if(isSameMonth(time,new Date()))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd日 HH:mm");
			return sdf.format(time);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
		return sdf.format(time);
	}
	public static String getAlarmDuration(Date startTime,Date endTime)
	{
		if(endTime == null)
			return "";
		String result = "";
		long l = DateTool.diff(endTime,startTime);
	    long day=l/(24*60*60*1000);
	    long hour=(l/(60*60*1000)-day*24);
	    long min=((l/(60*1000))-day*24*60-hour*60);
	    long s=(l/1000-day*24*60*60-hour*60*60-min*60);
	    if(day>0)
	    	result += day+"天";
	    if(hour>0)
	    	result += hour+"时";
	    if(min>0)
	    	result += min+"分";
	    if(day ==0  && hour ==0  && min == 0 && s>0)
	    	result = s+"秒";
	    return result;
	}
	public static Date getMonthBegin(Date time)
	{
		String str = DateTool.format(time, "yyyy-MM");
	    return DateTool.parse(str+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
	}
	//8 days
	public static Date[] getLastWeekRange()
	{
		Date[] range = new Date[2];
		Date day = DateTool.addDays(-7);
		Calendar c = Calendar.getInstance();
		c.setTime(day);
		int week = c.get(Calendar.DAY_OF_WEEK);
		if(week == Calendar.SUNDAY)
		{
			range[0] = DateTool.addDays(day, -6);
			range[1] = day;
		}
		else
		{
			range[0] = DateTool.addDays(day,-(week-Calendar.MONDAY));
			//8 days, because we use day>=start && day<end
			range[1] = DateTool.addDays(day,(9-week));
		}
		return range;
	}
	
	/**
	 * 获取本月第一天零时零分零秒
	 * @return
	 * @author MatthewXu
	 * @date Mar 21, 2019
	 */
	public static Date getFirstDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 2018-11-07 to 2018-11
	 * @param string
	 * @return
	 * @author MatthewXu
	 * @date Mar 22, 2019
	 */
	public static String getMonthFromDay(String datestr) {
		if(datestr.split("-").length  == 2)
			return datestr;
		return DateTool.format(DateTool.parse(datestr,"yyyy-MM-dd"),"yyyy-MM");
	}
	/**
	 * 2018-11 to 2018-11-01
	 * @param monthstr
	 * @return
	 * @author MatthewXu
	 * @date Mar 28, 2019
	 */
	public static String getDayFromMonth(String monthstr) {
		if(monthstr.split("-").length == 3)
			return monthstr;
		return DateTool.format(DateTool.parse(monthstr,"yyyy-MM"),"yyyy-MM-dd");
	}
	/**
	 * @return
	 * @author MatthewXu
	 * @date Mar 22, 2019
	 */
	public static Date getFirstDayOfLastMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	
	public static Calendar getInstance0()
	{
		return getInstanceDate(0,0,0);
	}
	
	public static Calendar getInstanceDate(int hour,int minute,int second) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
        return cal;
	}
	
	/**
	 * 
	 * Description:
	 * @param start
	 * @param end
	 * @return
	 * @author Matthew Xu
	 * @date Jun 4, 2020
	 */
	public static boolean isNowInPeriods(int start, int end){
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if(start <= end)
			return start <= hour && hour<= end;
		else {
			return (start <= hour && hour <=24) || (0 <= hour && hour <= end);
		}
	}
	
	public static boolean isTodayWorkday(){
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return (1 <= dayOfWeek && dayOfWeek <= 5) ? true : false;
	}
}