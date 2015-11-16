package com.jjiya.android.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kimwoodam on 2015. 11. 16..
 */
public class CMDateUtil {

    /**
     * 라이센스 남은 일자를 반환한다.
     * */
    public static String getLicenseRemainDate(String licenseDate) {
        licenseDate = licenseDate.replace(" ", "").replace("-", "").replace(":", "").replace("/", "").replace(".", "");
        Date today = new Date();

        Locale currentLocale = new Locale("KOREAN", "KOREA");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", currentLocale);

        String returnValue = "";

        try {
            Date beginDate = formatter.parse(formatter.format(today));
            Date endDate = formatter.parse(licenseDate);

            long diff = (endDate.getTime() - beginDate.getTime()) / 1000; // 초단위로 변환

            long diffYears = diff / (365*24 * 60 * 60); // 년도
            long diffDays = diff / (24 * 60 * 60); // 일자
            long diffTimes = diff / (60 * 60); // 시간
            long diffMinutes = diff / (60); // 분
            if (diffYears > 0) {
                returnValue = diffYears+"년 남음";
            } else if (diffDays > 0) {
                returnValue = diffDays+"일 남음";
            } else if (diffTimes > 0) {
                returnValue = diffTimes+"시간 남음";
            } else if (diffMinutes > 0) {
                returnValue = diffMinutes+"분 남음";
            } else if (diff > 0) {
                returnValue = diff+"초 남음";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public static String findDateWithFormat(String findDate, String format) {
        findDate = findDate.replace(" ", "").replace("-", "").replace(":", "").replace("/", "").replace(".", "");

        Locale currentLocale = new Locale("KOREAN", "KOREA");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", currentLocale);
        SimpleDateFormat returnFormatter = new SimpleDateFormat(format, currentLocale);
        String returnValue = "";
        try {
            Date fDate = formatter.parse(findDate);
            returnValue = returnFormatter.format(fDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    /**
     * 특정 날짜에 해당하는 요일 반환
     * */
    public static String getDayOfWeek(String findDate) {

        Calendar calendar = Calendar.getInstance();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", currentLocale);
        try {
            Date date = formatter.parse(findDate);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String returnValue = "";
        int day_of_week = calendar.get (Calendar.DAY_OF_WEEK);
        if (day_of_week == 1) {
            returnValue = "일";
        } else if (day_of_week == 2) {
            returnValue = "월";
        } else if (day_of_week == 3) {
            returnValue = "화";
        } else if (day_of_week == 4) {
            returnValue = "수";
        } else if (day_of_week == 5) {
            returnValue = "목";
        } else if (day_of_week == 6) {
            returnValue = "금";
        } else if (day_of_week == 7) {
            returnValue = "토";
        }
        return returnValue;
    }
}
