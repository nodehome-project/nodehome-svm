package io.nodehome.svm.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtil {

	public static long getTimeStamp(String filterDateFrom) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(filterDateFrom);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long timestamp = date.getTime();
		return timestamp;
	}
	
	public static long getTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime();
	}
	
	public static long calcTimeStamp(long time, int c, int sec) {
		if(c==1) {	// add
			time = time + (sec);	
		} else {	// minus
			time = time - (sec);
		}
		return time;
	}
	
	public static String getTimeStampFomat() {
		String rtnStr = null;

		// Set pattern to convert to string (year - month - day: minute: second: second (midnight second))
		String pattern = "yyyyMMddhhmmssSSS";

		try {
			SimpleDateFormat sdfCurrent = new SimpleDateFormat(pattern, Locale.KOREA);
			Timestamp ts = new Timestamp(System.currentTimeMillis());

			rtnStr = sdfCurrent.format(ts.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtnStr;
	}
    
	/**
	 * Returns the date as a date type.
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date getDate(String dateStr, String format) {
		 SimpleDateFormat sdf = new SimpleDateFormat(format);
		 Date date = null;
		 try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			date = null;
		}

		 return date;
	}

	/**
	 * Returns the date after a few days in a String date.
	 * @param dateStr	yyyyMMdd
	 * @param format	yyyyMMdd
	 * @return
	 */
	public static String getPlusMinusDate(String dateStr, int days, String format) {
		if( isNull(dateStr) ) {
			return "";
		}

		Calendar cal = Calendar.getInstance();
		cal.set( Integer.parseInt(dateStr.substring(0, 4)),
				Integer.parseInt(dateStr.substring(4, 6))-1,
				Integer.parseInt(dateStr.substring(6, 8)));

		return getPlusMinusDate(cal.getTime(), days, format);
	}

	/**
	 * Returns the date after a few days in a String date.
	 * default format은 yyyyMMdd
	 * @param Date date
	 * @return String
	 */
	public static String getPlusMinusDate(Date date, int days) {
		return getPlusMinusDate(date, days, "yyyyMMdd");
	}

	/**
	 * Returns the date after a few days in a String date.
	 * @param Date date
	 * @param String format
	 * @return String
	 */
	public static String getPlusMinusDate(Date date, int days, String pFormat) {
		String format = pFormat;
		Calendar calen = Calendar.getInstance(Locale.KOREAN);
		calen.setTime(date);
		calen.setTimeInMillis(calen.getTimeInMillis() + (60L * 60 * 24 * 1000 * days));
		if( isNull(format) ) {
			format = "yyyyMMdd";
		}

		return getDate(calen.getTime(), format);
	}

	/**
	 * Date difference between two Date
	 * @param Date startDate
	 * @param Date endDate
	 * @return long
	 */
	public static long dayToDay(Date startDate, Date endDate) {
		long dayToday = 0;
		dayToday = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
		return dayToday;
	}

	/**
	 * get date
	 * @param String format
	 * @return String
	 */
	public static String getDate(String format) {
		return getDate((new Date()), format);
	}

	/**
	 * get date
	 * @param Date date
	 * @param String format
	 * @return String
	 */
	public static String getDate(Date date , String format) {

		if(date == null)return "";
		return (new SimpleDateFormat(format, Locale.KOREAN)).format(date);
	}

	/**
	 * weekday 1 (Sun) 2 (Mon) 3 (Tue) 4 (Wed) 5 (Thu) 6 (Fri)
	 * The day of the month is retrieved.
	 * @param dateStr ex)200511
	 * @return
	 */
	public static HashMap weekDays(String dateStr) {

        HashMap map = new HashMap();
        if( isNull(dateStr) || dateStr.length() < 6) {
        	return map;
        }

        String yearStr = dateStr.substring(0, 4);
        String monthStr = dateStr.substring(4, 6);

        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, (month-1), 1);

        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);

        for(int i = 1; i <= lastDay; i++) {
            map.put( String.valueOf(i), String.valueOf(weekday%7) );
            weekday++;
        }
        map.put("lastDay",	lastDay);

        return map;
    }

	/** The specified date returns the parking of the month.
	 * @param sDate date, for example 20040315
	 * @return Value for parking (1: 1 week, 2: 2 weeks, 3: 3 weeks, 4: 4 weeks, 5: 5 weeks)
	 */
	public static int getWeekth(String sDate) {

		if (sDate == null || sDate.length() != 8)
			return -1;

		Calendar c = Calendar.getInstance(Locale.KOREA);
		c.set( Integer.parseInt(sDate.substring(0, 4)), Integer.parseInt(sDate.substring(4, 6)) - 1, Integer.parseInt(sDate.substring(6)) );

		GregorianCalendar gc = new GregorianCalendar(Locale.KOREA);
		gc.setTime(c.getTime());

		return c.get(Calendar.WEEK_OF_MONTH);
	}

	/**
	 * Last date of the corresponding month
	 * @param yearMonth
	 * @return
	 */
	public static int getLastDay(int year, int month) {

		Calendar calendar = Calendar.getInstance();
        calendar.set(year, (month-1), 1);

        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        return lastDay;
	}

	/**
	 * Divides the date in YYYYMMDD format into divisions and returns
	 * @param dateTime	20060704
	 * @param div	-
	 * @return
	 */
    public static String getParseDateString(String dateTime, String div) {

    	if(dateTime == null || dateTime.length() < 8) return dateTime;
 		if ( dateTime != null ) {
			String year = dateTime.substring(0, 4);
			String month = dateTime.substring(4, 6);
			String day = dateTime.substring(6, 8);

			return year + div + month + div + day;
		} else {
			return "";
		}
    }
    
    public static String getParseDateString(String dateTime) {

    	if(dateTime == null || dateTime.length() < 8) return dateTime;
 		if ( dateTime != null ) {
			String year = dateTime.substring(0, 4);
			String month = dateTime.substring(4, 6);
			String day = dateTime.substring(6, 8);

			return year + "Year" + month + "Month" + day + "Day";
		} else {
			return "";
		}
    }
    
    public static long getDayDistance(String startDate, String endDate) throws Exception {
	    return getDayDistance(startDate, endDate, null);
	}

	public static long getDayDistance(String startDate, String endDate, String pFormat) throws Exception {
		String format = pFormat;
	    if(format == null) format = "yyyyMMdd";
	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    long day2day = 0L;
	    try {
	        Date sDate = sdf.parse(startDate);
	        Date eDate = sdf.parse(endDate);
	        day2day = (eDate.getTime() - sDate.getTime()) / 0x5265c00L;
	    } catch(Exception e) {
	        throw new Exception("wrong format string");
	    }
	    return Math.abs(day2day);
	}

	// Calculate date based on current date
	public static String getFormatDate(String format, String term_gubun,int term) {
		String ret_date = "";
		Calendar cur_date = Calendar.getInstance(); // Current date
		SimpleDateFormat formatter; // Date format

		term_gubun = term_gubun.toUpperCase();
		if ("".equals(term_gubun))
			term_gubun = "NOW";

		try {
			if (term_gubun.equals("Y") || term_gubun.equals("M")
					|| term_gubun.equals("D") || term_gubun.equals("NOW")) {
				formatter = new SimpleDateFormat(format);


				if (term_gubun.equals("Y")) {
					cur_date.add(Calendar.YEAR, term);
				} else if (term_gubun.equals("M")) {
					cur_date.add(Calendar.MONTH, term);
				} else if (term_gubun.equals("D")) {
					cur_date.add(Calendar.DAY_OF_MONTH, term);
				}

				ret_date = formatter.format(cur_date.getTime());
			}
			return ret_date.trim();
		} catch (Exception e) {
			System.out.println("[MessageUtil::getDateFormt] Error = " + e);
		}
		return "";
	}

	private static String displayTimeZone(TimeZone tz) {

		long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
		long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                                  - TimeUnit.HOURS.toMinutes(hours);
		// avoid -4:-30 issue
		minutes = Math.abs(minutes);

		String result = "";
		if (hours > 0) {
			result = String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID());
		} else {
			result = String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
		}

		return result;
	}

	public static String getUnixLongToTime(long lUnixTime) {
		Date datePacket = new Date(lUnixTime);
		SimpleDateFormat sfDate = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		return sfDate.format(datePacket); 
	}
//	public static String getUnixLongToTime(long lUnixTime) {
//		Date datePacket = new Date(lUnixTime * 1000);
//		SimpleDateFormat sfDate = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
//		return sfDate.format(datePacket); 
//	}

	public static long convertLocalTimeToUTC(long pv_localDateTime) {
		long lv_UTCTime = pv_localDateTime;
		
		TimeZone z = TimeZone.getDefault();
		int offset = z.getOffset(pv_localDateTime);
		lv_UTCTime = pv_localDateTime - offset;
		return lv_UTCTime;
	}

	public static long convertUTCToLocalTime(long pv_UTCDateTime) {
		long lv_localDateTime = pv_UTCDateTime;
		
		TimeZone z = TimeZone.getDefault();
		int offset = z.getOffset(pv_UTCDateTime);
		lv_localDateTime = pv_UTCDateTime + offset;
		return lv_localDateTime;
	}
	
	public static boolean isNull(String val) {
		if(val==null) return true;
		return false;
	}
}
