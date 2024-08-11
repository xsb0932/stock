package cemp.util;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtils {

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
}
