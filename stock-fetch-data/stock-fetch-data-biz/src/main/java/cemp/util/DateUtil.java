package cemp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    public static DateTimeFormatter dtf_ds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat fmt_dd = new SimpleDateFormat("yyyy-mm-dd");
    public static SimpleDateFormat fmt_ds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date Date2Str(String date){
            Date d = cn.hutool.core.date.DateUtil.parse(date,"yyyy-MM-dd").toJdkDate();
            return d;
    }

    public static String getDatePrefix(){
        LocalDate now = LocalDate.now();
        String datePrefix = String.format("%s-%s-%s ",now.getYear(),String.format("%02d", now.getMonthValue()),String.format("%02d", now.getDayOfMonth()));
        return datePrefix;
    }

    public static String getToday(){
        LocalDate now = LocalDate.now();
        String datePrefix = String.format("%s-%s-%s",now.getYear(),String.format("%02d", now.getMonthValue()),String.format("%02d", now.getDayOfMonth()));
        return datePrefix;
    }
}
