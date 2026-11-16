package com.ekbatan.hse;

import android.app.*;
import android.content.*;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
    static final String CHANNEL="hse_reminders";
    @Override public void onReceive(Context c,Intent i){
        long id=i.getLongExtra("id",-1L); String title=i.getStringExtra("title"), body=i.getStringExtra("body");
        NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel(CHANNEL,"یادآوری‌های HSE",NotificationManager.IMPORTANCE_HIGH));
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,CHANNEL):new Notification.Builder(c);
        Notification n=b.setContentTitle(title==null?"یادآوری HSE":title).setContentText(body==null?"":body).setSmallIcon(android.R.drawable.ic_dialog_info).setAutoCancel(true).build();
        nm.notify((int)(System.currentTimeMillis()%Integer.MAX_VALUE),n); if(id>0){LibraryDb db=new LibraryDb(c);try{db.setReminderDone(id,true);}finally{db.close();}}
    }
}
