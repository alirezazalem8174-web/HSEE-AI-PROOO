package com.ekbatan.hse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) && !Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) return;
        new Thread(() -> {
            LibraryDb db = new LibraryDb(context);
            try {
                AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                for(LibraryDb.ReminderRow r: db.pendingReminders(System.currentTimeMillis())) {
                    Intent i=new Intent(context,ReminderReceiver.class).putExtra("id",r.id).putExtra("title",r.title).putExtra("body",r.body);
                    PendingIntent p=PendingIntent.getBroadcast(context,(int)(r.id%Integer.MAX_VALUE),i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
                    if(Build.VERSION.SDK_INT>=23)am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,r.at,p);else am.set(AlarmManager.RTC_WAKEUP,r.at,p);
                }
            } finally { db.close(); }
        }).start();
    }
}
