package com.stvn.nscreen.util;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Spannable;
import android.widget.Toast;

import com.stvn.nscreen.custom.CMAlertDialog;

/**
 * Created by leejunghoon on 15. 10. 9..
 */

public class CMAlertUtil {

    public interface InputDialogClickListener {
        public void positiveClickEvent(DialogInterface dialog, String text);
        public void negativeClickEvent(DialogInterface dialog);
    }

    private static Toast toast;
    /**
     * ToastShort 알림기능-짦은문장
     *
     * @param Context
     *            context
     * @param String
     *            message
     */
    public static void ToastShort(Context context, String message) {
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * ToastShort 알림기능-긴문장
     *
     * @param Context
     *            context
     * @param String
     *            message
     */
    public static void ToastLong(Context context, String message) {
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    /****
     * 하단버튼 없는 TextDialog
     * @param ctx
     * @param title title
     * @param msg   msg
     */
    public static void Alert(Context ctx ,String title ,String msg)
    {
        CMAlertDialog mDialog = new CMAlertDialog(ctx, CMAlertDialog.CMDialogType.DialogType1);
        mDialog.setTitle(title);
        mDialog.setMessage(msg);
        mDialog.show();
    }

    /***
     * 하단버튼 있는 다이얼로그
     * @param ctx
     * @param title
     * @param msg1
     * @param msg2
     */
    public static void Alert(Context ctx, String title,String msg1, String msg2)
    {
        CMAlertDialog mDialog = new CMAlertDialog(ctx);
        mDialog.setMessage(msg1, msg2);
        mDialog.setTitle(title);
        mDialog.show();
    }

    public static void Alert(Context ctx, String title,String msg1, String msg2,boolean isbold1,boolean isbold2)
    {
        CMAlertDialog mDialog = new CMAlertDialog(ctx);
        mDialog.setMessage(msg1,msg2, isbold1,isbold2);
        mDialog.setTitle(title);
        mDialog.show();
    }

    public static void Alert(Context ctx,String title,String msg1,String msg2,boolean isbold1,boolean isbold2,DialogInterface.OnClickListener listener, boolean isOneButton) {

        CMAlertDialog.CMDialogType type = CMAlertDialog.CMDialogType.DialogType2;
        if (isOneButton) {
            type = CMAlertDialog.CMDialogType.DialogType4;
        }

        CMAlertDialog mDialog = new CMAlertDialog(ctx, type);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2, isbold1, isbold2);
        mDialog.setPositiveButton("확인", listener);
        mDialog.show();
    }

    public static void Alert(Context ctx,String title,String msg1,String msg2,String oktext,DialogInterface.OnClickListener listener)
    {
        CMAlertDialog mDialog = new CMAlertDialog(ctx);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2);
        mDialog.setPositiveButton(oktext, listener);
        mDialog.show();
    }

    public static void Alert(Context ctx,String title,String msg1,String msg2,boolean isbold1,boolean isbold2,DialogInterface.OnClickListener listener)
    {
        CMAlertDialog mDialog = new CMAlertDialog(ctx);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2, isbold1, isbold2);
        mDialog.setPositiveButton("확인", listener);
        mDialog.show();
    }
    public static void Alert(Context ctx,String title,String msg1,String msg2,DialogInterface.OnClickListener listener, DialogInterface.OnClickListener canclelistener) {
        CMAlertDialog mDialog = new CMAlertDialog(ctx);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2);
        mDialog.setPositiveButton("확인", listener);
        mDialog.setNegativeButton("취소", canclelistener);
        mDialog.show();
    }
    public static void Alert(Context ctx,String title,String msg1,String msg2,boolean isbold1,boolean isbold2,DialogInterface.OnClickListener listener,DialogInterface.OnClickListener canclelistener)
    {
        CMAlertDialog mDialog = new CMAlertDialog(ctx);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2, isbold1, isbold2);
        mDialog.setPositiveButton("확인", listener);
        mDialog.setNegativeButton("취소", canclelistener);
        mDialog.show();
    }
    public static void Alert(Context ctx,String title,String msg1,String msg2,String ok,String cancel,boolean isbold1,boolean isbold2,DialogInterface.OnClickListener listener,DialogInterface.OnClickListener canclelistener)
    {
        CMAlertDialog mDialog = new CMAlertDialog(ctx);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2, isbold1, isbold2);
        mDialog.setPositiveButton(ok, listener);
        mDialog.setNegativeButton(cancel, canclelistener);
        mDialog.show();
    }

    public static void Alert(Context ctx, String title, String msg1, String msg2, String ok, String cancel, boolean isbold1, boolean isbold2, boolean useSecureMode, InputDialogClickListener listener) {
        CMAlertDialog mDialog = new CMAlertDialog(ctx, CMAlertDialog.CMDialogType.DialogType3);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2, isbold1, isbold2);
        mDialog.setInputSetting(4, 20, true);
        mDialog.setInputDlgButton(ok, cancel, listener);
        mDialog.show();
    }


    public static void Alert(Context ctx,String title,String msg1,Spannable msg2,String ok,String cancel,boolean isbold1,boolean isbold2,DialogInterface.OnClickListener listener,DialogInterface.OnClickListener canclelistener)
    {
        CMAlertDialog mDialog = new CMAlertDialog(ctx);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2, isbold1, isbold2);
        mDialog.setPositiveButton(ok, listener);
        mDialog.setNegativeButton(cancel, canclelistener);
        mDialog.show();
    }

    // title / msg / btnOk
    public static void Alert1(Context ctx, String title, String msg1, String msg2, boolean isbold1, boolean isbold2, DialogInterface.OnClickListener listener, boolean isOneButton) {

        CMAlertDialog.CMDialogType type = CMAlertDialog.CMDialogType.DialogType2;
        if (isOneButton) {
            type = CMAlertDialog.CMDialogType.DialogType4;
        }

        CMAlertDialog mDialog = new CMAlertDialog(ctx, type);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2, isbold1, isbold2);
        mDialog.setPositiveButton("확인", listener);
        mDialog.show();
    }

    public static void Alert1(Context ctx, String title, String msg1, String msg2, boolean isbold1, boolean isbold2, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener canclelistener) {

        CMAlertDialog mDialog = new CMAlertDialog(ctx);
        mDialog.setTitle(title);
        mDialog.setMessage(msg1, msg2, isbold1, isbold2);
        mDialog.setPositiveButton("확인", listener);
        mDialog.setNegativeButton("취소", canclelistener);
        mDialog.show();
    }
}
