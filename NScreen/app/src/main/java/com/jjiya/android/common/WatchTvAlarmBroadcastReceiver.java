package com.jjiya.android.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.stvn.nscreen.LoadingActivity;
import com.stvn.nscreen.R;

/**
 * Created by swlim on 2015. 11. 10..
 */
public class WatchTvAlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String programTitle = intent.getExtras().getString("programTitle");
        String striSeq = intent.getExtras().getString("iSeq");
        int iSeq = Integer.valueOf(striSeq);

        //Toast.makeText(context, "Alarm Received!", Toast.LENGTH_LONG).show();

//        NotificationManager notifier = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notify = new Notification(R.mipmap.ic_launcher, "text", System.currentTimeMillis());

        Intent intent2 = new Intent(context, LoadingActivity.class);
        intent2.putExtra("iSeq",striSeq);
//        PendingIntent pender = PendingIntent.getActivity(context, 0, intent2, 0);

//        notify.setLatestEventInfo(context, "TV시청예약 알림", programTitle, pender);
//        notify.flags |= Notification.FLAG_AUTO_CANCEL;
//        notify.vibrate = new long[] { 200, 200, 500, 300 };
        // notify.sound=Uri.parse("file:/");
//        notify.number++;
//        notify.flags = Notification.FLAG_AUTO_CANCEL;
//        notify.defaults = Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL ;
//
//        notifier.notify(1, notify);

//        Notification notification = new Notification(R.mipmap.ic_launcher, "text", System.currentTimeMillis());
//        notification.setLatestEventInfo(context, "TV시청예약 알림", programTitle, pender);

        // Cancel the notification after its selected
//        notify.defaults = Notification.DEFAULT_VIBRATE;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        // 로컬푸쉬는 핸드폰문자 인증으로 대체하기로 했음.
//        String sKey   = UUID.randomUUID().toString().substring(0, 5);
//        String ticker = "본인인증";
//        String title  = "생활톡 알림";
//        String text   = "인증번호 : " + sKey;
//        mPref.put(JYSharedPreferences.PUSH_AUTH_BONIN, sKey);
        long[] patten = {500,500,500,500};
        //Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String title = context.getString(R.string.app_name) + " 시청예약 알림";
        NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher).
                setContentTitle(title).
                setContentText(programTitle).
                setAutoCancel(true).
                setContentIntent(pendingIntent).
                setVibrate(patten);
                //setSound(sound);
        nm.notify(0 , notificationBuilder.build());
    }
}
