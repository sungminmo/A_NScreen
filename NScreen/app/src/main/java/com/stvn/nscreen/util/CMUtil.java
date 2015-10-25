package com.stvn.nscreen.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leejunghoon on 15. 10. 24..
 */
public class CMUtil {

    public static int parseInt(String str)
    {
        int result = 0;
        try
        {
            result = Integer.parseInt(str.trim());
        }
        catch(Exception e)
        {
            result = 0;
        }

        return result;
    }

    /***
     * Date를 원하는 형태의 String 형태로 변환
     * @return
     */

    public static String getConverDateString(String date, String fromFormat, String toFormat)
    {
        if(TextUtils.isEmpty(date))
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(fromFormat);
        Date d = null;
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sdf.applyPattern(toFormat);
        return sdf.format(d);
    }
}
