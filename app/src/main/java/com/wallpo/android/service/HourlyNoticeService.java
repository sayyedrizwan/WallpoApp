package com.wallpo.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class HourlyNoticeService {
    private Context context;

    public HourlyNoticeService(Context context) {
        this.context = context;
    }

    public void setHourlyService() {
        Intent intent = new Intent(context, HourlyNotice.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 4, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(am != null){
            long triggerAfter = 120 * 60 * 1000;
            long triggerEvery = 120 * 60 * 1000;
            am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAfter, triggerEvery, sender);
        }
    }

    public void cancelHourlyService(){
        Intent intent = new Intent(context, HourlyNotice.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 4, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(am != null){
            am.cancel(sender);
        }
    }

}
