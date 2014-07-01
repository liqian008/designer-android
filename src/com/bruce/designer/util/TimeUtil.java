package com.bruce.designer.util;

import java.text.SimpleDateFormat;

public class TimeUtil {

	public static final SimpleDateFormat SDF_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

	public static final long TIME_UNIT_SECOND = 1000;
	public static final long TIME_UNIT_MINUTE = TIME_UNIT_SECOND * 60;
	public static final long TIME_UNIT_HOUR = TIME_UNIT_MINUTE * 60;
	public static final long TIME_UNIT_DAY = TIME_UNIT_HOUR * 24;
	public static final long TIME_UNIT_WEEK = TIME_UNIT_DAY * 7;
	public static final long TIME_UNIT_MONTH = TIME_UNIT_DAY * 30;
	public static final long TIME_UNIT_YEAR = TIME_UNIT_DAY * 365;

	public static String displayTime(long time) {
		long between = (System.currentTimeMillis() -time);

		long year = between / TIME_UNIT_YEAR;
		long month = between / TIME_UNIT_MONTH;
		long week = between / TIME_UNIT_WEEK;
		long day = between / TIME_UNIT_DAY;
		long hour = between / TIME_UNIT_HOUR;
		long minute = between / TIME_UNIT_MINUTE;
		long second = between / TIME_UNIT_SECOND;

		String timeStr;

		if (year > 0) {
			timeStr = year + "年前";
		} else if (month > 0) {
			timeStr = month + "个月前";
		} else if (week > 0) {
			timeStr = week + "周前";
		} else if (day > 0) {
			timeStr = day + "天前";
		} else if (hour > 0) {
			timeStr = hour + "小时前";
		} else if (minute > 0) {
			timeStr = minute + "分钟前";
		} else {
			timeStr = second + "秒前";
		}
		System.out.println(timeStr);
		return timeStr;
	}
}
