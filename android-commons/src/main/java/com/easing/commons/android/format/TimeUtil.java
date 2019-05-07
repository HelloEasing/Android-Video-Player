package com.easing.commons.android.format;

import android.annotation.TargetApi;

import com.easing.commons.android.value.time.YMD;
import com.easing.commons.android.value.time.TimeZone;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;

@TargetApi(26)
public class TimeUtil {

    public static final String FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_2 = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMAT_3 = "yyyy-MM-dd";
    public static final String FORMAT_4 = "yyyy/MM/dd";
    public static final String FORMAT_5 = "yyyy年MM月dd日";
    public static final String FORMAT_6 = "yyyy年MM月";
    public static final String FORMAT_7 = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String FORMAT_8 = "yyyy年MM月dd日 HH时mm分";
    public static final String FORMAT_10 = "yyyy年MM月dd日HH时mm分ss秒";

    public static final List<String> FORMATS = new ArrayList();

    static {
        FORMATS.add(FORMAT_1);
        FORMATS.add(FORMAT_2);
        FORMATS.add(FORMAT_3);
        FORMATS.add(FORMAT_4);
        FORMATS.add(FORMAT_5);
        FORMATS.add(FORMAT_6);
        FORMATS.add(FORMAT_7);
        FORMATS.add(FORMAT_8);
    }

    // ==================== Text ====================

    public static String now() {
        return TimeUtil.formatDate(new Date(), null);
    }

    public static String now(String format) {
        if (format == null)
            format = FORMAT_1;
        return TimeUtil.formatDate(new Date(), format);
    }

    //sec -> HH:mm:ss
    public static String toHMSTime(Number sec) {
        int h = sec.intValue() / 60 / 60;
        int m = sec.intValue() / 60;
        int s = sec.intValue() % 60;
        if (h == 0)
            return MathUtil.keepInt(m, 2) + ":" + MathUtil.keepInt(s, 2);
        return MathUtil.keepInt(h, 2) + ":" + MathUtil.keepInt(m, 2) + ":" + MathUtil.keepInt(s, 2);
    }

    //min -> HH:mm
    public static String toHMTime(int min) {
        String h = MathUtil.keepInt(min / 60, 2);
        String m = MathUtil.keepInt(min % 60, 2);
        return h + ":" + m;
    }

    //HH:mm
    public static String toHMTime(String time) {
        time = time.trim().replaceAll("：", ":");
        String h = time.split(":")[0];
        String m = time.split(":")[1];
        h = MathUtil.keepInt(Integer.valueOf(h), 2);
        m = MathUtil.keepInt(Integer.valueOf(m), 2);
        return h + ":" + m;
    }

    //mm:ss
    public static String toMSTime(String time) {
        time = time.trim().replaceAll("：", ":");
        String m = time.split(":")[0];
        String s = time.split(":")[1];
        m = MathUtil.keepInt(Integer.valueOf(m), 2);
        s = MathUtil.keepInt(Integer.valueOf(s), 2);
        return m + ":" + s;
    }

    //sec -> mm:ss
    public static String secToMSTime(int sec) {
        String m = MathUtil.keepInt(sec / 60, 2);
        String s = MathUtil.keepInt(sec % 60, 2);
        return m + ":" + s;
    }

    //ms -> mm:ss
    public static String msToMSTime(int ms) {
        return TimeUtil.secToMSTime(ms / 1000);
    }

    public static String addHMHour(String hmTime, int hour) {
        hmTime = toHMTime(hmTime);
        int h = Integer.valueOf(hmTime.substring(0, 2));
        return toHMTime(h + hour + hmTime.substring(2));
    }

    // ==================== Long ====================

    public static long timestamp() {
        return System.currentTimeMillis();
    }

    public static long millisOfNow() {
        return System.currentTimeMillis();
    }

    //当天过了多少秒
    public static long millisOfDay() {
        return (System.currentTimeMillis() + 8 * 60 * 60 * 1000) % (24 * 60 * 60 * 1000);
    }

    public static String formatDate(Long ms) {
        if (ms == null || ms == 0)
            return null;
        Date date = new Date(ms);
        return TimeUtil.formatDate(date, null);
    }

    public static String formatDate(long ms, String format) {
        Date date = new Date(ms);
        return TimeUtil.formatDate(date, format);
    }

    public static String msToText(String msText) {
        long ms = Long.valueOf(msText);
        Date date = new Date(ms);
        return TimeUtil.formatDate(date, null);
    }

    public static String msToText(String msText, String format) {
        long ms = Long.valueOf(msText);
        Date date = new Date(ms);
        return TimeUtil.formatDate(date, format);
    }

    // ==================== Date ====================

    public static Date date() {
        return new Date();
    }

    public static Date getDate(int y, int m, int d) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d);
        return calendar.getTime();
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Date addDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    public static Date addMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    public static Date subMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        date = calendar.getTime();
        return date;
    }

    public static Date subDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return date;
    }

    @SneakyThrows
    public static Date parseDate(String date, String format) {
        if (format == null)
            format = "YYYY-MM-DD HH:mm:ss";
        return new SimpleDateFormat(format).parse(date);
    }

    @SneakyThrows
    public static String formatDate(Date date, String format) {
        if (format == null)
            format = "yyyy-MM-dd HH:mm:ss";
        return new SimpleDateFormat(format).format(date);
    }

    //将日期从一个格式转换为另一个格式
    @SneakyThrows
    public static String switchDateFormat(String date, String oldFormat, String newFormat) {
        Date d = TimeUtil.parseDate(date, oldFormat);
        return TimeUtil.formatDate(d, newFormat);
    }

    //解析任意格式的日期
    @SneakyThrows
    public static Date parseDate(String date) {
        try {
            return new Date(Long.parseLong(date));
        } catch (Exception e) {
        }

        for (String format : FORMATS)
            try {
                return new SimpleDateFormat(format).parse(date);
            } catch (Exception e) {
            }

        throw new RuntimeException("unknown date formatDate");
    }

    //任意格式的日期，转换为指定格式
    @SneakyThrows
    public static String formatDate(String date, String format) {
        Date d = parseDate(date);
        if (format == null)
            format = FORMAT_1;
        return new SimpleDateFormat(format).format(d);
    }

    // ==================== DateTime ====================

    public static LocalDateTime getDateTime(Date date) {
        return LocalDateTime.parse(TimeUtil.formatDate(date, "yyyy-MM-dd'T'HH:mm:ss"));
    }

    public static long getTimeMillis(LocalDateTime t, TimeZone zone) {
        Date date = TimeUtil.getZoneDate(t, zone);
        return date.getTime();
    }

    public static Date getZoneDate(LocalDateTime t, TimeZone zone) {
        if (zone == null)
            zone = zone.DEFAULT;
        return Date.from(t.atZone(zone.getZoneId()).toInstant());
    }

    public static LocalDate copy(LocalDate d) {
        return LocalDate.of(d.getYear(), d.getMonth(), d.getDayOfMonth());
    }

    public static String formatDate(LocalDate d, String format) {
        if (format == null)
            format = FORMAT_1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return d.format(formatter);
    }

    public static String formatDateTime(LocalDateTime t, String format) {
        if (format == null)
            format = FORMAT_1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return t.format(formatter);
    }

    public static String formatZoneTime(long t, TimeZone zone, String format) {
        if (format == null)
            format = FORMAT_1;
        if (zone == null)
            zone = zone.DEFAULT;
        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochSecond(t / 1000), zone.getZoneId());
        String timeText = TimeUtil.formatDateTime(dt, format);
        return timeText;
    }

    // ==================== Calendar ====================

    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    //从年月日获取Calendar
    public static Calendar getCalendar(int y, int m, int d) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d);
        return calendar;
    }

    public static Calendar copy(Calendar calendar) {
        Calendar copyCalendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int M = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DATE);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        copyCalendar.set(y, M, d, h, m, s);
        return copyCalendar;
    }

    //从字符串解析Calendar
    @SneakyThrows
    public static Calendar parseCalendar(String text, String format) {
        if (format == null)
            format = FORMAT_1;
        Date date = new SimpleDateFormat(format).parse(text);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    //格式化Calendar
    @SneakyThrows
    public static String formatCalendar(Calendar calendar, String format) {
        if (format == null)
            format = FORMAT_1;
        return new SimpleDateFormat(format).format(calendar.getTime());
    }

    //判断是否在某个区间内
    public static boolean inTimeZone(String timeText, Calendar startTime, Calendar endTime, String format) {
        if (format == null)
            format = TimeUtil.FORMAT_1;
        String startTimeText = TimeUtil.formatCalendar(startTime, format);
        String endTimeText = TimeUtil.formatCalendar(endTime, format);
        return timeText.compareTo(startTimeText) >= 0 && timeText.compareTo(endTimeText) < 0;
    }

    //判断是否在某个区间内
    public static boolean inTimeZone(Calendar time, Calendar startTime, Calendar endTime, String format) {
        if (format == null)
            format = TimeUtil.FORMAT_1;
        String timeText = TimeUtil.formatCalendar(time, format);
        String startTimeText = TimeUtil.formatCalendar(startTime, format);
        String endTimeText = TimeUtil.formatCalendar(endTime, format);
        return timeText.compareTo(startTimeText) >= 0 && timeText.compareTo(endTimeText) < 0;
    }

    // ==================== YMD ====================

    public static YMD getYmd() {
        Calendar calendar = Calendar.getInstance();
        return YMD.create(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
    }

    // ==================== Tool Function ====================

    //判断是不是HH:mm格式的时间
    public static boolean isHMTime(String time) {
        if (!time.contains(":"))
            return false;
        try {
            String[] hm = time.split(":");
            int h = Integer.valueOf(hm[0]);
            int m = Integer.valueOf(hm[1]);
            if (h < 0 || h > 23)
                return false;
            if (m < 0 || m > 59)
                return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //HH:mm格式的时间转换为，相当于当天00:00的毫秒数
    public static long HMTimeToMillis(String time) {
        String[] hm = time.split(":");
        int h = Integer.valueOf(hm[0]);
        int m = Integer.valueOf(hm[1]);
        return (h * 60 + m) * 1000;
    }
}
