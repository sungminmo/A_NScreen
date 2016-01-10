package com.stvn.nscreen.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leejunghoon on 15. 10. 24..
 */

public class CMUtil {

    public static enum CMNetworkType {
        NotConnected, WifiConnected, AnotherConnected;
    };

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

    public static void autoMappingJsonToObject(JSONObject json, Object obj) {
        Field[] fields = obj.getClass().getFields();
        for (Field field : fields) {
            if (json.optString(field.getName()) != null) {
                try {
                    field.set(obj, json.optString(field.getName()));
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 네트워크 연결여부 확인
     * */
    public static CMNetworkType isNetworkConnectedType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetInfo == null) {
            return CMNetworkType.NotConnected;
        } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return CMNetworkType.WifiConnected;
        } else {
            return CMNetworkType.AnotherConnected;
        }
    }
}
