package com.stephen.cli.project.library;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Stephen on 2015/9/7.
 */
public class DateUtil {
    private static final long ONE_MINUTE = 60;
    private static final long ONE_HOUR = 3600;
    private static final long ONE_DAY = 86400;
    private static final long ONE_MONTH = 2592000;
    private static final long ONE_YEAR = 31104000;

    //距离今天多久
    public static String fromToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        long time = date.getTime() / 1000;
        long now = new Date().getTime() / 1000;
        long ago = now - time;
        if (ago <= ONE_HOUR) {
            int minute = (int)(ago / ONE_MINUTE);
            if (0 == minute){
                return "刚刚";
            }//end of if
            return  minute + "分钟前";
        }else if (ago <= ONE_DAY)
            return ago / ONE_HOUR + "小时" + (ago % ONE_HOUR / ONE_MINUTE)+ "分钟前";
        else if (ago <= ONE_DAY * 2)
            return "昨天" + calendar.get(Calendar.HOUR_OF_DAY) + "点"+ calendar.get(Calendar.MINUTE) + "分";
        else if (ago <= ONE_DAY * 3)
            return "前天" + calendar.get(Calendar.HOUR_OF_DAY) + "点"+ calendar.get(Calendar.MINUTE) + "分";
        else if (ago <= ONE_MONTH) {
            long day = ago / ONE_DAY;
            return day + "天前" + calendar.get(Calendar.HOUR_OF_DAY) + "点"+ calendar.get(Calendar.MINUTE) + "分";
        } else if (ago <= ONE_YEAR) {
            long month = ago / ONE_MONTH;
            long day = ago % ONE_MONTH / ONE_DAY;
            return month + "个月" + day + "天前"+ calendar.get(Calendar.HOUR_OF_DAY) + "点"+ calendar.get(Calendar.MINUTE) + "分";
        } else {
            long year = ago / ONE_YEAR;
            int month = calendar.get(Calendar.MONTH) + 1;// JANUARY which is 0 so month+1
            return year + "年前" + month + "月" + calendar.get(Calendar.DATE)+ "日";
        }

    }

    //距离截止日期还有多长时间
    public static String fromDeadline(Date date) {
        long deadline = date.getTime() / 1000;
        long now = (new Date().getTime()) / 1000;
        long remain = deadline - now;
        if (remain <= ONE_HOUR)
            return "只剩下" + remain / ONE_MINUTE + "分钟";
        else if (remain <= ONE_DAY)
            return "只剩下" + remain / ONE_HOUR + "小时"+ (remain % ONE_HOUR / ONE_MINUTE) + "分钟";
        else {
            long day = remain / ONE_DAY;
            long hour = remain % ONE_DAY / ONE_HOUR;
            long minute = remain % ONE_DAY % ONE_HOUR / ONE_MINUTE;
            return "只剩下" + day + "天" + hour + "小时" + minute + "分钟";
        }

    }

    //距离今天的绝对时间
    public static String toToday(Date date) {
        long time = date.getTime() / 1000;
        long now = (new Date().getTime()) / 1000;
        long ago = now - time;
        if (ago <= ONE_HOUR)
            return ago / ONE_MINUTE + "分钟";
        else if (ago <= ONE_DAY)
            return ago / ONE_HOUR + "小时" + (ago % ONE_HOUR / ONE_MINUTE) + "分钟";
        else if (ago <= ONE_DAY * 2)
            return "昨天" + (ago - ONE_DAY) / ONE_HOUR + "点" + (ago - ONE_DAY)
                    % ONE_HOUR / ONE_MINUTE + "分";
        else if (ago <= ONE_DAY * 3) {
            long hour = ago - ONE_DAY * 2;
            return "前天" + hour / ONE_HOUR + "点" + hour % ONE_HOUR / ONE_MINUTE
                    + "分";
        } else if (ago <= ONE_MONTH) {
            long day = ago / ONE_DAY;
            long hour = ago % ONE_DAY / ONE_HOUR;
            long minute = ago % ONE_DAY % ONE_HOUR / ONE_MINUTE;
            return day + "天前" + hour + "点" + minute + "分";
        } else if (ago <= ONE_YEAR) {
            long month = ago / ONE_MONTH;
            long day = ago % ONE_MONTH / ONE_DAY;
            long hour = ago % ONE_MONTH % ONE_DAY / ONE_HOUR;
            long minute = ago % ONE_MONTH % ONE_DAY % ONE_HOUR / ONE_MINUTE;
            return month + "个月" + day + "天" + hour + "点" + minute + "分前";
        } else {
            long year = ago / ONE_YEAR;
            long month = ago % ONE_YEAR / ONE_MONTH;
            long day = ago % ONE_YEAR % ONE_MONTH / ONE_DAY;
            return year + "年前" + month + "月" + day + "天";
        }
    }

    //比较两个日期
    public static int dateCompare(String dateFormat,String startDate,String endDate){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        try {
            startCal.setTime(sdf.parse(startDate));
            endCal.setTime(sdf.parse(endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startCal.add(Calendar.DAY_OF_MONTH, 1);
        endCal.add(Calendar.DAY_OF_MONTH, 1);
        return startCal.compareTo(endCal);//(startCal < endCal)-1,(startCal == endCal)0,(startCal > endCal)1
    }

    //毫秒转日期(格式比如:yyyy-MM-dd HH:mm:ss)
    public static String millisToDate(long times,String dateFormat){
        try {
            Date dat=new Date(times);
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(dat);
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            return format.format(gc.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return dateFormat;
        }
    }

    //long类型时间格式化
    public static String convertToTime(long time) {
        return convertToTime(time, "yyyy-MM-dd HH:mm:ss");
    }
    public static String convertToTime(long time,String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        Date date = new Date(time);
        return df.format(date);
    }

    //日期转毫秒
    public static Long dateToMillis(String date,String dateFormat){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat(dateFormat).parse(date));
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //月份加减
    public static String monthAdditionSubtraction(String curDate,String dateFormat,int month){
        DateFormat format = new SimpleDateFormat(dateFormat);
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = format.parse(curDate);
            calendar.setTime(date);
            calendar.add(calendar.MONTH,month);//属性很多,可以操作各种时间日期
            return format.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //日期加减(day就是要加减的天数,传正加天数,传负数减天数)
    public static Long dayAdditionSubtractionRetLong(String curDate, String dateFormat, int day){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat(dateFormat)).parse(curDate));
            calendar.add(calendar.DAY_OF_MONTH,day);//属性很多,可以操作各种时间日期
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String dayAdditionSubtractionRetString(String curDate, String dateFormat, int day){
        DateFormat format = new SimpleDateFormat(dateFormat);
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = format.parse(curDate);
            calendar.setTime(date);
            calendar.add(calendar.DAY_OF_MONTH,day);//属性很多,可以操作各种时间日期
            return format.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String[] dayAdditionSubtractionRetAry(String nowDate, String dateFormat, int day, boolean isToDay){
        DateFormat format = new SimpleDateFormat(dateFormat, Locale. getDefault());
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = format.parse(nowDate);
            calendar.setTime(date);
            calendar.add(calendar.DAY_OF_MONTH, day);//属性很多,可以操作各种时间日期
            String[] weekAry = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            Calendar calendarS = Calendar.getInstance();
            calendarS.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0, 0, 0);// 获取当天00点00分00秒
            Calendar calendarE = Calendar.getInstance();
            calendarE.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),23, 59, 59);// 获取当天23点59分59秒

            //返回为: String[]{dateStr, dateMill, dateMillS, dateMillE, weekStr, isToDay, year, month, day}
            return new String[]{ format.format(calendar.getTime()), ""+calendar.getTimeInMillis(), ""+calendarS.getTimeInMillis(), ""+calendarE.getTimeInMillis(), (!isToDay ? (weekDay < weekAry.length ? weekAry[weekDay] : "未知") : "今天"),
                    isToDay ? "YES" : "NO", ""+calendar.get(Calendar.YEAR), ""+calendar.get(Calendar.MONTH), ""+calendar.get(Calendar.DAY_OF_MONTH)};
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    //日期转星期
    public static String dateToWeek(String nowDate, String dateFormat) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            Calendar calendar = Calendar.getInstance(); // 获得一个日历
            //calendar.setFirstDayOfWeek(Calendar.MONDAY);
            Date date = format.parse(nowDate);
            calendar.setTime(date);
            int w = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
            if (w < 0) w = 0;
            return weekDays[w];
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    //得到这个月有多少天
    public static int getMonthCountDay(int year,int month) {
        if (0 == month)return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        calendar.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
        return calendar.get(Calendar.DATE);
    }
    //得到时间差
    public static Long[] getTwoDateDifferenceDates(String dateFormat,String date1,String date2){
        try {
            if(TextUtils.isEmpty(date1))return null;
            if(TextUtils.isEmpty(date2))return null;
            SimpleDateFormat df = new SimpleDateFormat(dateFormat);
            Date Date1 = df.parse(date1);
            Date Date2 = df.parse(date2);
            long l=Math.abs(Date1.getTime()-Date2.getTime());
            //long l=Date1.getTime()-Date2.getTime();
            long day=l/(24*60*60*1000);
            long hour=(l/(60*60*1000)-day*24);
            long min=((l/(60*1000))-day*24*60-hour*60);
            long second=(l/1000-day*24*60*60-hour*60*60-min*60);
            //System.out.println(date1+"======="+date2+"===相差:"+day+"day/"+hour+"hour/"+min+"min/"+second+"s");
            return (new Long[]{day,hour,min,second});
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    //得到当前日期
    public static String getSystemCurDateTime(String dateFormat){//24小时制:yyyy-MM-dd HH:mm:ss,12小时制:yyyy-MM-dd hh:mm:ss
        return (new SimpleDateFormat(dateFormat, Locale.getDefault())).format(new Date());
    }

    public static Date stringToDate(String strDate,String dateFormat){
        try {
            return new SimpleDateFormat(dateFormat).parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws ParseException {
        /*String deadline = "2012-12-30 12:45:59";
        Date date = new Date();
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = simple.parse(deadline);
        System.out.println(simple.format(date));*/

        /*String before = "2015-9-7 0:0:59";
        date = simple.parse(before);
        System.out.println(DateUtils.fromToday(date));

        System.out.println(DateUtils.getFullDate());
        System.out.println(DateUtils.getDate());*/

        /*String curStr = DateUtil.getSystemCurDateTime("yyyy-MM-dd")+" 00:00:00";
        System.out.println(curStr+"/"+dateToMillis(curStr,"yyyy-MM-dd HH:mm:ss")+"/"+millisToDate(dateToMillis(curStr,"yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss"));
        System.out.println(dayAdditionSubtractionRetString(curStr, "yyyy-MM-dd HH:mm:ss", 0));
        System.out.println(dayAdditionSubtractionRetLong(curStr, "yyyy-MM-dd HH:mm:ss", -1));*/
        //System.out.println(millisToDate(1450195200000l ,"yyyy-MM-dd"));
        //System.out.println(millisToDate(1451059200000l,"yyyy-MM-dd"));
        /*String text = "stephenPatch_10.jar";
        System.out.println("1:"+text.substring(0,text.indexOf("_")));
        System.out.println("2:"+Integer.parseInt(text.substring(text.indexOf("_")+1,text.lastIndexOf("."))));*/
        /*String string = "ssss";
        System.out.println("1:"+string);
        try {
            string = null;
            System.out.println("2:"+string);
            System.out.println("3:"+string.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("4:"+string);*/

        //System.out.println(millisToDate(1469635200000l,"yyyy-MM-dd"));
        /*Map<String,String> testMap = new LinkedHashMap<String, String>();
        testMap.put("2222_222233_55555#0.mp3","5555555555555555555555550.do");
        testMap.put("2222_222233_55555#3.mp3","5555555555555555555555553.do");
        testMap.put("2222_222233_55555#5.mp3","5555555555555555555555555.do");
        testMap.put("2222_222233_55555#4.mp3","5555555555555555555555554.do");
        testMap.put("2222_222233_55555#1.mp3","5555555555555555555555551.do");
        testMap.put("2222_222233_55555#2.mp3","5555555555555555555555552.do");
        testMap.put(null,null);
        testMap.put("2222_222233_55555#6.mp3",null);

        testMap.remove(null);

        List<String> infoIds = new ArrayList<String>(testMap.keySet());

        //前
        for (int i = 0; i < infoIds.size(); i++) {
            String id = infoIds.get(i).toString();
            System.out.println(id+"===前==>"+testMap.get(id));
        }
        System.out.println("======================================================>");*/
        /*Collections.sort(infoIds, new Comparator<String>() {//排序
            @Override
            public int compare(String lhs, String rhs) {
                System.out.println(lhs.substring(lhs.lastIndexOf("#")+1,lhs.lastIndexOf(".mp3"))+"====="+lhs+"==+++==="+rhs.substring(rhs.lastIndexOf("#")+1,rhs.lastIndexOf(".mp3"))+"====="+rhs);
                return Integer.parseInt(lhs.substring(lhs.lastIndexOf("#")+1,lhs.lastIndexOf(".mp3")))-Integer.parseInt(rhs.substring(rhs.lastIndexOf("#")+1,rhs.lastIndexOf(".mp3")));
            }
        });*/

        //后
        /*for (int i = 0; i < infoIds.size(); i++) {
            String id = infoIds.get(i).toString();
            System.out.println(id+"===后==>"+testMap.get(id));
        }*/

        /*List<String> testList = new ArrayList<String>();
        testList.add("----------------------------1");
        testList.add("----------------------------2");
        testList.add("----------------------------3");
        testList.add(null);
        for(int i = 0; i < testList.size(); i++) System.out.println(testList.get(i));
        testList.remove(2);
        for(int j = 0; j < testList.size(); j++) System.out.println("=====>"+testList.get(j));*/

        String string = "[{\"pay_method\":1,\"pay_method_name\":\"等额本息\"},{\"pay_method\":2,\"pay_method_name\":\"等额本金\"},{\"pay_method\":3,\"pay_method_name\":\"按月还息到期还本\"},{\"pay_method\":4,\"pay_method_name\":\"分期：提前收手续费\"},{\"pay_method\":5,\"pay_method_name\":\"分期：每月付手续费\"},{\"pay_method\":6,\"pay_method_name\":\"按月付息按年还本\"},{\"pay_method\":7,\"pay_method_name\":\"不规则还款\"},{\"pay_method\":8,\"pay_method_name\":\"随借随还\"}]";
        System.out.println(string);
    }
}
