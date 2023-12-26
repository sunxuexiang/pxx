package com.wanmi.sbc.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangjin on 2017/4/28.
 */
public class DateUtil {

    public static final String FMT_DATE_1 = "yyyy-MM-dd";

    public static final String FMT_TIME_1 = "yyyy-MM-dd HH:mm:ss";

    public static final String FMT_TIME_2 = "yyyy-MM-dd HH:mm";

    public static final String FMT_TIME_3 = "yyyyMMddHHmmss";

    public static final String FMT_DATE_3 = "MMddHH";

    public static final String FMT_TIME_4 = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String FMT_TIME_5 = "yyyyMMdd";

    public static final String FMT_TIME_6 = "yyyy-MM-dd HH";

    public static final String FMT_TIME_7 = "yyyyMMddHH";

    /**
     * 转换类型 string to LocalDateTime
     *
     * @param time time
     * @return LocalDateTime
     */
    public static LocalDateTime parseDate(String time, String fmt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fmt);
        return LocalDateTime.of(LocalDate.parse(time, formatter), LocalTime.MIN);
    }

    public static LocalDateTime parseDate(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FMT_TIME_2);
        return LocalDateTime.of(LocalDate.parse(time, formatter), LocalTime.MIN);
    }

    /**
     * 转换类型 string to LocalDateTime
     *
     * @param time time
     * @return LocalDateTime
     */
    public static String getDate(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FMT_DATE_1);
        return formatter.format(time);
    }

    /**
     * 转换类型 string to LocalDateTime
     *
     * @param time time
     * @return LocalDateTime
     */
    public static String getDateTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FMT_TIME_1);
        return formatter.format(time);
    }

    /**
     * 转换类型 string to LocalDateTime
     * 2017-06-23 -> 2017-06-23 00:00
     *
     * @param time time
     * @return LocalDateTime
     */
    public static LocalDateTime parseDay(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FMT_DATE_1);
        return LocalDate.parse(time, formatter).atStartOfDay();
    }

    /**
     * 转换类型 String to LocalDateTime
     * 2021-01-01 10:10:10
     * @param time
     * @return LocalDateTime
     */
    public static LocalDateTime parseDayTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FMT_TIME_1);
        return LocalDateTime.parse(time,formatter);
    }


    /**
     * 获取全部当前时间
     * @return
     */
    public static String nowTime(){
        return format(LocalDateTime.now(), FMT_TIME_1);
    }
    /**
     * 获取全部当前时间
     * @return
     */
    public static String nowTimeString(){
        String oneHoursAgoTime="";
        Date dt = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        //rightNow.add(Calendar.DATE, -1);
        rightNow.add(Calendar.DAY_OF_MONTH, -1);
        Date dt1=rightNow.getTime();
        oneHoursAgoTime = sdf.format(dt1);
        return oneHoursAgoTime;
    }
    /**
     * 获取当前时间
     * @return
     */
    public static String nowDate(){
        return format(LocalDateTime.now(),FMT_DATE_1);
    }

    /**
     * 获取当前时间 到小时
     * @return
     */
    public static String nowHourTime(){
        return format(LocalDateTime.now(),FMT_TIME_6);
    }

    /**
     * 获取昨天时间
     * @return
     */
    public static String yesterdayDate(){
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);

        return format(cal.getTime(),FMT_DATE_1);
    }

    /**
     * 获取明天时间
     * @return
     */
    public static String tomorrowDate(){
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,1);

        return format(cal.getTime(),FMT_DATE_1);
    }


    /**
     * 转换类型  LocalDateTime to string
     *
     * @param time time
     * @return LocalDateTime
     */
    public static String format(LocalDateTime time, String fmt) {
        return time.format(DateTimeFormatter.ofPattern(fmt));
    }

    /**
     * 转换类型  Date to string
     *
     * @param time time
     * @return LocalDateTime
     */
    public static String format(Date time, String fmt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
        return simpleDateFormat.format(time);
    }

    /**
     * 转换类型  string to LocalDateTime
     *
     * @param time time
     * @return LocalDateTime
     */
    public static LocalDateTime parse(String time, String fmt) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(fmt));
    }

    /**
     * 时间类型转换为LocalDateTime, 针对*request中带有canEmpty注解的时间属性
     * 如果没有canEmpty注解, 需要在外层判断为空则不调用该方法
     * 使用时需要位于copyProperties上方
     * 场景: 修改信息,
     *   有值:
     *     1.前端如果修改值, 传递到后端则为yyyy-MM-dd类型, 长度为10
     *     2.如果不做修改则为yyyy-MM-dd HH:mm:ss.SSS类型
     *   无值:
     *     保存为null
     * @param time 时间
     * @return
     */
    public static LocalDateTime parseDayCanEmpty(String time) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(FMT_DATE_1);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(FMT_TIME_4);
        if(StringUtils.isNotBlank(time)) {
            if(time.length() == 10) {
                return LocalDateTime.of(LocalDate.parse(time, formatter1), LocalTime.MIN);
            } else {
                return LocalDateTime.parse(time, formatter2);
            }
        }
        return null;
    }

    /**
     * 获取当天剩余多少秒
     *
     * 使用plusDays加传入的时间加1天，将时分秒设置成0
     *
     * 使用ChronoUnit.SECONDS.between方法，传入两个LocalDateTime对象即可得到相差的秒数
     * @return
     */
    public static Long getSeconds(){
        LocalDateTime midnight = LocalDateTime.ofInstant(new Date().toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = getLocalDateTimeByDateTime(new Date());
        return ChronoUnit.SECONDS.between(currentDateTime, midnight);
    }


    /***
     * @desc  时间dateTime转LocalDateTime
     * @author shiy  2023/6/19 20:07
    */
    public static LocalDateTime getLocalDateTimeByDateTime(Date dateTime) {
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(dateTime.toInstant(),
                ZoneId.systemDefault());
        return currentDateTime;
    }

    /**
     * 获取当天0点
     */
    public static String getStartToday(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        return format(calendar.getTime(), FMT_TIME_1);
    }

    /**
     * 获取当天23点59:59
     */
    public static String getEndToday(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        return format(calendar.getTime(), FMT_TIME_1);
    }

    /**
     * 转化
     * 2022-01-03 00:00:00  ==> 2022-01-03
     */
    public static String parseDayStartTime(String datetime) {
        Date date = null;
        try {
            date = new SimpleDateFormat(FMT_TIME_1).parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return format(calendar.getTime(), FMT_DATE_1);
    }

    /**
     * 获取当月第一天
     */
    public static String getStartMonth(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        return format(c.getTime(), FMT_TIME_1);
    }

    /**
     * 获取当月最后一天23点59:59
     */
    public static String getEndMonth(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY,23);
        c.set(Calendar.MINUTE,59);
        c.set(Calendar.SECOND,59);

        return format(c.getTime(), FMT_TIME_1);
    }
    //获取一天的开始时间，2017,7,22 00:00
    public static LocalDateTime getDayStart(LocalDateTime time) {
        return time.withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    //获取一天的结束时间，2017,7,22 23:59:59.999999999
    public static LocalDateTime getDayEnd(LocalDateTime time) {
        return time.withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999);
    }


    /**
     * 获取最近n天
     */
    private static  Date getDateAdd(int days){
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }

    private static  Date getDateAddEnd(int days){
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }

    public static List<Map<String,String>> getDaysBetwwen(int days){ //最近几天日期
//        List<String> dayss = Lists.newArrayList();
        List<Map<String,String>> dayMap = Lists.newArrayList();

        //开始时间
        Calendar start = Calendar.getInstance();
        start.setTime(getDateAdd(days));
        Long startTIme = start.getTimeInMillis();

        //结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(getDateAddEnd(days));
        Long endTime = end.getTimeInMillis();
        Long oneDay = 1000 * 60 * 60 * 24l;

        //当前时间23：59：59
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        long millis = calendar.getTimeInMillis();

        while (startTIme <= millis) {
            Date s = new Date(startTIme);
            Date e = new Date(endTime);
            HashMap<String, String> dateMap = Maps.newHashMap();
            dateMap.put(format(s, FMT_TIME_1),format(e, FMT_TIME_1));
            dayMap.add(dateMap);
            startTIme += oneDay;
            endTime += oneDay;
        }
        return dayMap;
    }

    //以天为单位计算 获取开始时间
    public static String getDayAdd(Integer days) {
        Date dateAdd = getDateAdd(days);
        return format(dateAdd,FMT_TIME_1);
    }

    /**
     * 获取过去N天的日期
     * @param days
     * @param format
     * @return
     */
    public static String getDayAddEnd(int days,String format) {
        Date dateEnd = getDateAddEnd(days);
        return format(dateEnd,format);
    }

    public static void main(String[] args) {
//        for(int i = 1; i <= 7; i++) {
//            System.out.println(getDayAddEnd(i,DateUtil.FMT_TIME_5));
//        }
        System.out.println(getSeconds());
        System.out.println(getSeconds());
    }

    /**
     * 获取指定月份的第一天
     * @param year
     * @param month
     * @return
     */
    public static Date getFirstDay(int year, int month) {
        // 获取Calendar类的实例
        Calendar c = Calendar.getInstance();
        // 设置年份
        c.set(Calendar.YEAR, year);
        // 设置月份，因为月份从0开始，所以用month - 1
        c.set(Calendar.MONTH, month - 1);
        // 设置日期
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c.getTime();
    }

    /**
     * 获取指定月份的最后一天
     * @param year
     * @param month
     * @return
     */
    public static Date getLastDay(int year, int month) {
        // 获取Calendar类的实例
        Calendar c = Calendar.getInstance();
        // 设置年份
        c.set(Calendar.YEAR, year);
        // 设置月份，因为月份从0开始，所以用month - 1
        c.set(Calendar.MONTH, month - 1);
        // 获取当前时间下，该月的最大日期的数字
        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 将获取的最大日期数设置为Calendar实例的日期数
        c.set(Calendar.DAY_OF_MONTH, lastDay);

        return c.getTime();
    }

    /**
     * 获取指定日期所在月份开始的时间戳
     * @param date 指定日期
     * @return
     */
    public static LocalDateTime getMonthBegin(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date time = null;
        try {
            time = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(time);

        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳

        Instant instant = c.getTime().toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 获取指定日期所在月份结束的时间戳
     * @param date 指定日期
     * @return
     */
    public static LocalDateTime getMonthEnd(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date time = null;
        try {
            time = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(time);

        //设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND,59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳

        Instant instant = c.getTime().toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 将localDateTime格式转换为Date格式
     *
     * @param localDateTime
     * @return
     */
    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 计算两个日期之间间隔的天数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long intervalDay(Date startTime, Date endTime) {
        long start = startTime.getTime();
        long end = endTime.getTime();
        long interval = (long) ((end - start) / (1000 * 60 * 60 * 24) + 0.5);
        return interval;
    }


    private static final Map<Integer, Character> charMap = new HashMap<Integer, Character>();
    private static final Pattern p = Pattern.compile("^(\\d{4})\\D*(\\d{2})\\D*(\\d{2})\\D*(\\d{2})\\D*(\\d{2})\\D*(\\d{2})");

    static {
        charMap.put(1, 'y');
        charMap.put(2, 'M');
        charMap.put(3, 'd');
        charMap.put(4, 'H');
        charMap.put(5, 'm');
        charMap.put(6, 's');
    }



    /**
     * 任意日期字符串转换为Date，不包括无分割的纯数字（13位时间戳除外） ，日期时间为数字，年月日时分秒，但没有毫秒
     *
     * @param dateString 日期字符串
     * @return Date
     */
    public static Date stringToDate(String dateString) {
        dateString = dateString.trim().replaceAll("[\\D*]", "");
        if (Pattern.matches("^[-+]?\\d{13}$", dateString)) {//支持13位时间戳
            return new Date(Long.parseLong(dateString));
        }
        if (dateString.length() < 4) {
            dateString = StringUtils.rightPad(dateString, 4, '0');
        }
        String year = dateString.substring(0,4),month,day;
        int t1,t2,t3;
        if (dateString.length() < 14) {
            StringBuilder tmpBuilder = new StringBuilder(dateString);
            switch (dateString.length()) {
                case 4:
                    tmpBuilder.append("0101000000");
                    break;
                case 5:
                    tmpBuilder.insert(4, "0").append("01000000");
                    break;
                case 6:
                    t1= Integer.parseInt( dateString.substring(4,5));
                    if(t1>1){
                        tmpBuilder.insert(4,"0");
                        tmpBuilder.insert(6,"0");
                        tmpBuilder.append("000000");
                    }else {
                        tmpBuilder.append("01000000");
                    }
                    break;
                case 7:
                    t1= Integer.parseInt( dateString.substring(4,5));
                    if(t1>1) {
                        tmpBuilder.insert(4,"0").append("000000");
                    } else {
                        tmpBuilder.insert(6, "0").append("000000");
                    }
                    break;
                case 8:
                    tmpBuilder.append("000000");
                    break;
                case 9:
                    tmpBuilder.insert(8, "0").append("0000");
                    break;
                case 10:
                    tmpBuilder.append("0000");
                    break;
                case 11:
                    tmpBuilder.insert(10, "0").append("00");
                    break;
                case 12:
                    tmpBuilder.append("00");
                    break;
                case 13:
                    tmpBuilder.insert(12, "0");
                    break;
                default:
                    break;
            }
            dateString = tmpBuilder.toString();
        }

        Matcher m = p.matcher(dateString);
        StringBuilder sb = new StringBuilder(dateString);
        if (m.find(0)) {//从被匹配的字符串中，充index = 0的下表开始查找能够匹配pattern的子字符串。m.matches()的意思是尝试将整个区域与模式匹配，不一样。
            int count = m.groupCount();
            for (int i = 1; i <= count; i++) {
                for (Map.Entry<Integer, Character> entry : charMap.entrySet()) {
                    if (entry.getKey() == i) {
                        sb.replace(m.start(i), m.end(i), replaceEachChar(m.group(i), entry.getValue()));
                    }
                }
            }
        } else {
            return null;
        }
        String format = sb.toString();
        SimpleDateFormat sf = new SimpleDateFormat(format);
        try {
            return sf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将指定字符串的所有字符替换成指定字符，跳过空白字符
     *
     * @param s 被替换字符串
     * @param c 字符
     * @return 新字符串
     */
    public static String replaceEachChar(String s, Character c) {
        StringBuilder sb = new StringBuilder("");
        for (Character c1 : s.toCharArray()) {
            if (c1 != ' ') {
                sb.append(String.valueOf(c));
            }
        }
        return sb.toString();
    }

    /**
     * 获取全部当前时间
     * @return
     */
    public static String getDayString(int num){
        Date dt = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.DAY_OF_MONTH, num);
        Date dt1=rightNow.getTime();
        return sdf.format(dt1);
    }

    /**
     * 判断给定日期是否是当月的最后一天
     * @param date
     * @return
     */
    public static boolean isLastDayOfMonth(Date date) {
        //1、创建日历类
        Calendar calendar = Calendar.getInstance();
        //2、设置当前传递的时间，不设就是当前系统日期
        calendar.setTime(date);
        //3、data的日期是N，那么N+1【假设当月是30天，30+1=31，如果当月只有30天，那么最终结果为1，也就是下月的1号】
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
        //4、判断是否是当月最后一天【1==1那么就表明当天是当月的最后一天返回true】
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }else{
            return false;
        }
    }

}
