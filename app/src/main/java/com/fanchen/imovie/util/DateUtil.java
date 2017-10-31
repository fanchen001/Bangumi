package com.fanchen.imovie.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 时间/日期相关工具类
 * @author fanchen
 *
 */
public class DateUtil {
	
	/** 时间日期格式化到年月日时分秒. */
	public static final String DATEFORMATYMDHMS = "yyyy-MM-dd HH:mm:ss";
	
	/** 时间日期格式化到年月日. */
	public static final String DATEFORMATYMD = "yyyy-MM-dd";
	
	/** 时间日期格式化到年月. */
	public static final String DATEFORMATYM = "yyyy-MM";
	
	/** 时间日期格式化到年月日时分. */
	public static final String DATEFORMATYMDHM = "yyyy-MM-dd HH:mm";
	
	/** 时间日期格式化到月日. */
	public static final String DATEFORMATMD = "MM/dd";
	
	/** 时分秒. */
	public static final String DATEFORMATHMS = "HH:mm:ss";
	
	/** 时分. */
	public static final String DATEFORMATHM = "HH:mm";
	
	/** 上午. */
    public static final String AM = "AM";

    /** 下午. */
    public static final String PM = "PM";
    
    /**
     * @param datdString Thu May 18 2017 00:00:00 GMT+0800 (中国标准时间)
     * @return 年月日;
     */
    public static String parseTime(String datdString) {
        datdString = datdString.replace("GMT", "").replaceAll("\\(.*\\)", "");
        //将字符串转化为date类型，格式2016-10-12
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
        Date dateTrans = null;
        try {
            dateTrans = format.parse(datdString);
            return new SimpleDateFormat("yyyy-MM-dd").format(dateTrans).replace("-","/");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datdString;

    }

	/**
	 *  获取当前时间戳
	 * @return 时间戳
	 */
	public static long getCurrentTime(){
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static String getTimeByCalendar(){
        Calendar cal = Calendar.getInstance();
        int month=cal.get(Calendar.MONTH);//获取月份
        int day=cal.get(Calendar.DATE);//获取日
        return (month+1)+"月"+day+"日";
    }

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return timeStr;
	}

	/**
	 * 格式化12小时制<br>
	 * 格式：yyyy-MM-dd hh-MM-ss
	 * @param time 时间
	 * @return
	 */
	public static String format12Time(long time){
		return format(time,DATEFORMATYMDHMS);
	}
	
	/**
	 * 格式化24小时制<br>
	 * 格式：yyyy-MM-dd HH-MM-ss
	 * @param time 时间
	 * @return
	 */
	public static String format24Time(long time){
		return format(time,DATEFORMATYMDHMS);
	}
	
	/**
	 * 格式化时间,自定义标签
	 * @param time 时间
	 * @param pattern 格式化时间用的标签
	 * @return
	 */
	public static String format(long time,String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date(time));
	}
	
	/**
	 * 获取当前天
	 * @return 天
	 */
	@SuppressWarnings("static-access")
	public static int getCurrentDay(){
		Calendar calendar = Calendar.getInstance();
		return calendar.DAY_OF_MONTH;
	}
	
	/** 获取当前月
	 * @return 月
	 */
	@SuppressWarnings("static-access")
	public static int getCurrentMonth(){
		Calendar calendar = Calendar.getInstance();
		return calendar.MONTH;
	}
	
	
	/** 获取当前年
	 * @return 年
	 */
	@SuppressWarnings("static-access")
	public static int getCurrentYear(){
		Calendar calendar = Calendar.getInstance();
		return calendar.YEAR;
	}
	
	/**
	 * long转时间格式
	 * 
	 * @param time
	 *            时间
	 * @return
	 */
	public static String transitionTime(long time) {
		long temp1 = time / 1000;
		long temp2 = temp1 / 60;
		long temp3 = temp2 / 60;
		String h = temp3 % 60 + "";
		String m = temp2 % 60 + "";
		String s = temp1 % 60 + "";

		if (m.length() < 2) {
			m = "0" + m;
		}
		if (s.length() < 2) {
			s = "0" + s;
		}
		String time1 = h + ":" + m + ":" + s;
		return time1;
	}
	
	
	/**
     * 将时间转换为中文
     * @param datetime
     * @return
     */
	public static String DateToChineseString(Date datetime){
        Date today=new Date();
        long   seconds   =   (today.getTime()-   datetime.getTime())/1000; 

        long year=  seconds/(24*60*60*30*12);// 相差年数
        long   month  =   seconds/(24*60*60*30);//相差月数
        long   date   =   seconds/(24*60*60);     //相差的天数 
        long   hour   =   (seconds-date*24*60*60)/(60*60);//相差的小时数 
        long   minute   =   (seconds-date*24*60*60-hour*60*60)/(60);//相差的分钟数 
        long   second   =   (seconds-date*24*60*60-hour*60*60-minute*60);//相差的秒数 
        
        if(year>0){
            return year + "年前";
        }
        if(month>0){
            return month + "月前";
        }
        if(date>0){
            return date + "天前";
        }
        if(hour>0){
            return hour + "小时前";
        }
        if(minute>0){
            return minute + "分钟前";
        }
        if(second>0){
            return second + "秒前";
        }
        return "未知时间";
    }
	
	
	/**
	 * 取指定日期为星期几.
	 *
	 * @param strDate 指定日期
	 * @param inFormat 指定日期格式
	 * @return String   星期几
	 */
    public static String getWeekNumber(String strDate,String inFormat) {
      String week = "星期日";
      Calendar calendar = new GregorianCalendar();
      DateFormat df = new SimpleDateFormat(inFormat);
      try {
		   calendar.setTime(df.parse(strDate));
	  } catch (Exception e) {
		  return "错误";
	  }
      int intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
      switch (intTemp){
        case 0:
          week = "星期日";
          break;
        case 1:
          week = "星期一";
          break;
        case 2:
          week = "星期二";
          break;
        case 3:
          week = "星期三";
          break;
        case 4:
          week = "星期四";
          break;
        case 5:
          week = "星期五";
          break;
        case 6:
          week = "星期六";
          break;
      }
      return week;
    }

	public static int getWeekNumberFromString(String date){
		int week = -1;
		switch (date){
			case "周一":
				week = 1;
				break;
			case "周二":
				week = 2;
				break;
			case "周三":
				week = 3;
				break;
			case "周四":
				week = 4;
				break;
			case "周五":
				week = 5;
				break;
			case "周六":
				week = 6;
				break;
			case "周日":
				week = 7;
				break;
		}
		return week;
	}

	public static String getWeekNumberFromInt(int week){
		String str = null;
		switch (week){
			case 1:
				str = "周一";
				break;
			case 2:
				str = "周二";
				break;
			case 3:
				str = "周三";
				break;
			case 4:
				str = "周四";
				break;
			case 5:
				str = "周五";
				break;
			case 6:
				str = "周六";
				break;
			case 7:
				str = "周日";
				break;
		}
		return str;
	}

	public static String getWeekNumber() {
		String currentDate = getCurrentDate("yyyy-MM-dd");
		Calendar calendar = new GregorianCalendar();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calendar.setTime(df.parse(currentDate));
		} catch (Exception e) {
			return "周日";
		}
		int intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		String week = null;
		switch (intTemp){
			case 0:
				week = "周日";
				break;
			case 1:
				week = "周一";
				break;
			case 2:
				week = "周二";
				break;
			case 3:
				week = "周三";
				break;
			case 4:
				week = "周四";
				break;
			case 5:
				week = "周五";
				break;
			case 6:
				week = "周六";
				break;
		}
		return week;
	}
    
    /**
	 * 描述：获取本周的某一天.
	 *
	 * @param format the format
	 * @param calendarField the calendar field
	 * @return String String类型日期时间
	 */
	private static String getDayOfWeek(String format,int calendarField) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			int week = c.get(Calendar.DAY_OF_WEEK);
			if (week == calendarField){
				strDate = mSimpleDateFormat.format(c.getTime());
			}else{
				int offectDay = calendarField - week;
				if (calendarField == Calendar.SUNDAY) {
					offectDay = 7-Math.abs(offectDay);
				} 
				c.add(Calendar.DATE, offectDay);
				strDate = mSimpleDateFormat.format(c.getTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}
    /**
	 * 描述：获取本月第一天.
	 *
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getFirstDayOfMonth(String format) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			//当前月的第一天
			c.set(GregorianCalendar.DAY_OF_MONTH, 1);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;

	}
    /**
	 * 描述：获取本月最后一天.
	 *
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getLastDayOfMonth(String format) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			// 当前月的最后一天
			c.set(Calendar.DATE, 1);
			c.roll(Calendar.DATE, -1);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}
	/**
	 * 描述：获取表示当前日期的0点时间毫秒数.
	 *
	 * @return the first time of day
	 */
	public static long getFirstTimeOfDay() {
		Date date = null;
		try {
			String currentDate = getCurrentDate(DATEFORMATYMD);
			date = getDateByFormat(currentDate+" 00:00:00",DATEFORMATYMDHMS);
			return date.getTime();
		} catch (Exception e) {
		}
		return -1;
	}
	
	/**
	 * 描述：获取milliseconds表示的日期时间的字符串.
	 *
	 * @param milliseconds the milliseconds
	 * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String 日期时间字符串
	 */
	public static String getStringByFormat(long milliseconds,String format) {
		String thisDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			thisDateTime = mSimpleDateFormat.format(milliseconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thisDateTime;
	}
	
	/**
	 * 描述：获取表示当前日期时间的字符串.
	 *
	 * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String String类型的当前日期时间
	 */
	public static String getCurrentDate(String format) {
		String curDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			Calendar c = new GregorianCalendar();
			curDateTime = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curDateTime;

	}
	
	/**
	 * 描述：获取表示当前日期24点时间毫秒数.
	 *
	 * @return the last time of day
	 */
	public static long getLastTimeOfDay() {
		Date date = null;
		try {
			String currentDate = getCurrentDate(DATEFORMATYMD);
			date = getDateByFormat(currentDate+" 24:00:00",DATEFORMATYMDHMS);
			return date.getTime();
		} catch (Exception e) {
		}
		return -1;
	}
    
	/**
	 * 描述：判断是否是闰年()
	 * <p>(year能被4整除 并且 不能被100整除) 或者 year能被400整除,则该年为闰年.
	 *
	 * @param year 年代（如2012）
	 * @return boolean 是否为闰年
	 */
	public static boolean isLeapYear(int year) {
		if ((year % 4 == 0 && year % 400 != 0) || year % 400 == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 描述：计算两个日期所差的天数.
	 *
	 * @param milliseconds1 the milliseconds1
	 * @param milliseconds2 the milliseconds2
	 * @return int 所差的天数
	 */
	public static int getOffectDay(long milliseconds1, long milliseconds2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(milliseconds1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(milliseconds2);
		//先判断是否同年
		int y1 = calendar1.get(Calendar.YEAR);
		int y2 = calendar2.get(Calendar.YEAR);
		int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
		int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
		int maxDays = 0;
		int day = 0;
		if (y1 - y2 > 0) {
			maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 + maxDays;
		} else if (y1 - y2 < 0) {
			maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 - maxDays;
		} else {
			day = d1 - d2;
		}
		return day;
	}
	
	/**
	 * 描述：计算两个日期所差的小时数.
	 *
	 * @param date1 第一个时间的毫秒表示
	 * @param date2 第二个时间的毫秒表示
	 * @return int 所差的小时数
	 */
	public static int getOffectHour(long date1, long date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(date2);
		int h1 = calendar1.get(Calendar.HOUR_OF_DAY);
		int h2 = calendar2.get(Calendar.HOUR_OF_DAY);
		int h = 0;
		int day = getOffectDay(date1, date2);
		h = h1-h2+day*24;
		return h;
	}
	
	/**
	 * 描述：计算两个日期所差的分钟数.
	 *
	 * @param date1 第一个时间的毫秒表示
	 * @param date2 第二个时间的毫秒表示
	 * @return int 所差的分钟数
	 */
	public static int getOffectMinutes(long date1, long date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(date2);
		int m1 = calendar1.get(Calendar.MINUTE);
		int m2 = calendar2.get(Calendar.MINUTE);
		int h = getOffectHour(date1, date2);
		int m = 0;
		m = m1-m2+h*60;
		return m;
	}

	/**
	 * 描述：获取本周一.
	 *
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getFirstDayOfWeek(String format) {
		return getDayOfWeek(format,Calendar.MONDAY);
	}

	/**
	 * 描述：获取本周日.
	 *
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getLastDayOfWeek(String format) {
		return getDayOfWeek(format,Calendar.SUNDAY);
	}
	
	 /**
		 * 描述：String类型的日期时间转化为Date类型.
		 *
		 * @param strDate String形式的日期时间
		 * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
		 * @return Date Date类型日期时间
		 */
		public static Date getDateByFormat(String strDate, String format) {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			Date date = new Date();
			try {
				date = mSimpleDateFormat.parse(strDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date;
		}
		
		/**
		 * 描述：Date类型转化为String类型.
		 *
		 * @param date the date
		 * @param format the format
		 * @return String String类型日期时间
		 */
		public static String getStringByFormat(Date date, String format) {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			String strDate = null;
			try {
				strDate = mSimpleDateFormat.format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return strDate;
		}
		
}
