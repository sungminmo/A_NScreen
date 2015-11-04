package com.stvn.nscreen.util;

import android.util.Log;

/**
 * debug message 로그 출력
 * Created by kimwoodam on 2015. 9. 19..
 */

public class CMLog {

    private static boolean DEBUG_VIEW_MODE = true;

    public static void d(String t,String message) {
        if (DEBUG_VIEW_MODE) {
            String tag = "";
            String temp = new Throwable().getStackTrace()[1].getClassName();
            if (temp != null) {
                int lastDotPos = temp.lastIndexOf(".");
                tag = temp.substring(lastDotPos + 1);
            }
            String methodName = new Throwable().getStackTrace()[1]
                    .getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            String logText = "[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message;
            Log.d(t, logText);
        }
    }

    public static void w(String t,String message) {
        if (DEBUG_VIEW_MODE) {
            String tag = "";
            String temp = new Throwable().getStackTrace()[1].getClassName();
            if (temp != null) {
                int lastDotPos = temp.lastIndexOf(".");
                tag = temp.substring(lastDotPos + 1);
            }
            String methodName = new Throwable().getStackTrace()[1]
                    .getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            String logText = "[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message;
            Log.w(t, logText);
        }
    }

    public static void e(String t,String message) {
        if (DEBUG_VIEW_MODE) {
            String tag = "";
            String temp = new Throwable().getStackTrace()[1].getClassName();
            if (temp != null) {
                int lastDotPos = temp.lastIndexOf(".");
                tag = temp.substring(lastDotPos + 1);
            }
            String methodName = new Throwable().getStackTrace()[1]
                    .getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            String logText = "[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message;
            Log.e(t, logText);
        }
    }

    public static void i(String t,String message) {
        if (DEBUG_VIEW_MODE) {
            String tag = "";
            String temp = new Throwable().getStackTrace()[1].getClassName();
            if (temp != null) {
                int lastDotPos = temp.lastIndexOf(".");
                tag = temp.substring(lastDotPos + 1);
            }
            String methodName = new Throwable().getStackTrace()[1]
                    .getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            String logText = "[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message;
            Log.i(t, logText);
        }
    }
}
