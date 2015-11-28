package com.jjiya.android.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        int iSeq = intent.getExtras().getInt("iSeq");

        //Toast.makeText(context, "Alarm Received!", Toast.LENGTH_LONG).show();

        NotificationManager notifier = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = new Notification(R.mipmap.ic_launcher, "text", System.currentTimeMillis());

        Intent intent2 = new Intent(context, LoadingActivity.class);
        intent2.putExtra("iSeqNoti",iSeq);
        PendingIntent pender = PendingIntent.getActivity(context, 0, intent2, 0);

        notify.setLatestEventInfo(context, "TV시청예약 알림", programTitle, pender);
//        notify.flags |= Notification.FLAG_AUTO_CANCEL;
//        notify.vibrate = new long[] { 200, 200, 500, 300 };
        // notify.sound=Uri.parse("file:/");
//        notify.number++;
        notify.flags = Notification.FLAG_AUTO_CANCEL;
        notify.defaults = Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL ;

        notifier.notify(1, notify);



        Notification notification = new Notification(R.mipmap.ic_launcher, "text", System.currentTimeMillis());
        notification.setLatestEventInfo(context, "TV시청예약 알림", programTitle, pender);

        // Cancel the notification after its selected
        notify.defaults = Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
    }
}
