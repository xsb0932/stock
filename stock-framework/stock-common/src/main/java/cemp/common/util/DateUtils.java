package cemp.common.util;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtils {

    public final static String  FDT_PATTERN_LOCAL = "yyyy-MM-dd HH:mm:ss";
    public final static String  FDT_PATTERN_UTC = "yyyy-MM-dd'T'HH:mm:ssz";

    public static String getUTC(String timeStr){
        //String start = "2023-1-1 "+"00:00:00";
        DateFormat dtfUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
        try{
            Date date1 = simpleDateFormat.parse(timeStr);
            Calendar ca = Calendar.getInstance();
            ca.setTime(date1);
            ca.add(Calendar.HOUR,-8);//日期减8小时
            Date dt1=ca.getTime();
            String reslut = dtfUTC.format(dt1);
            return reslut;
        }catch (ParseException e) {
            System.out.println("获取UTC时间，并减少8小时异常");
            System.out.println(e.getMessage());
//            logger.info();
//            logger.error(e.getMessage(), e);
        }
        return "";
    }



    public static ZonedDateTime time2ZDT(String time,String pattern){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        return ZonedDateTime.parse(time,fmt);
    }

    public static ZonedDateTime utc2BJDatetime(String utcTime){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
        try{
            ZonedDateTime uTime = ZonedDateTime.parse(utcTime,fmt);
            ZonedDateTime sTime =  uTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
            return sTime;

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String utc2BJStr(String utcTime){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime time = utc2BJDatetime(utcTime);
        return time.format(fmt);
    }
}
