package com.easing.commons.android.value.time;

import com.easing.commons.android.format.TimeUtil;

import java.io.Serializable;
import java.util.Calendar;

public class YMD implements Serializable {
	
	public int year;
	public int month;
	public int day;
	
	public static YMD create() {
		return new YMD();
	}
	
	public static YMD create(int y, int m, int d) {
		YMD ymd = new YMD();
		ymd.year = y;
		ymd.month = m;
		ymd.day = d;
		return ymd;
	}
	
	@Override
	public String toString() {
		Calendar calendar = TimeUtil.getCalendar(year, month, day);
		String date = TimeUtil.formatCalendar(calendar, TimeUtil.FORMAT_3);
		return date;
	}
}
