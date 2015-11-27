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
    public static long getRemainWatchingTime(String viewablePeriod, String purchaseDate, Date compareDate) {
        purchaseDate = purchaseDate.replace(" ", "").replace("-", "").replace(":", "").replace("/", "").replace(".", "");
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", currentLocale);

        String viewable = String.valueOf((Integer.parseInt(viewablePeriod.substring(0, 4)) * 365) + (Integer.parseInt(viewablePeriod.substring(5, 7)) * 30 ) + Integer.parseInt(viewablePeriod.substring(8, 10)));
        long diffDay = 0;

        try {
            Date endDate = formatter.parse(purchaseDate);

            long diff = (compareDate.getTime()-endDate.getTime()) / 1000; // 초단위로 변환
            diffDay = diff / (24 * 60 * 60);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Long.valueOf(viewable) - diffDay;
    }

    /**
     * 라이센스 남은 일자를 분으로 변환하여 반환한다.
     * */
    public static long getLicenseRemainMinute(String licenseDate, Date compareDate) {
        licenseDate = licenseDate.replace(" ", "").replace("-", "").replace(":", "").replace("/", "").replace(".", "");

        Locale currentLocale = new Locale("KOREAN", "KOREA");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", currentLocale);

        long diffMinutes = 0;

        try {
            Date beginDate = formatter.parse(formatter.format(compareDate));
            Date endDate = formatter.parse(licenseDate);

            long diff = (endDate.getTime() - beginDate.getTime()) / 1000; // 초단위로 변환
            diffMinutes = diff / (60); // 분

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diffMinutes;
    }

    /**
     * 입력받은 날짜를 분으로 변환하여 반환한다.
     * */
    public static long changeSecondToDate(String strDate) {
        strDate = strDate.replace(" ", "").replace("-", "").replace(":", "").replace("/", "").replace(".", "");
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", currentLocale);

        long second = 0;

        try {
            Date date = formatter.parse(strDate);
            second = date.getTime() / 1000; // 초단위로 변환

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return second;
    }


    /**
     * 라이센스 남은 일자를 반환한다.
     * */
    public static String getLicenseRemainDate(String licenseDate, Date compareDate) {
        licenseDate = licenseDate.replace(" ", "").replace("-", "").replace(":", "").replace("/", "").replace(".", "");

        Locale currentLocale = new Locale("KOREAN", "KOREA");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", currentLocale);

        String returnValue = "";

        try {
            Date beginDate = formatter.parse(formatter.format(compareDate));
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
//                returnValue = diffMinutes+"분 남음";
                returnValue = "1시간 남음";
            } else if (diff > 0) {
//                returnValue = diff+"초 남음";
                returnValue = "1시간 남음";
            } else if (diff < 0) {
                returnValue = "기간만료";
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
            // swlim. findDate가 "2015-11-26 13:16:26"일 경우에 처리를 추가 했음.
            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", currentLocale);
            Date date = null;
            if ( findDate.contains("-") || findDate.contains(":") ) {
                date = formatter2.parse(findDate);
            } else {
                date = formatter.parse(findDate);
            }
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
