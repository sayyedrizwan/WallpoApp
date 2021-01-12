package com.wallpo.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.utils.VideoWallpaperMuteService;

public class StartForeService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, "RUNNING SEVICE")
                .setContentTitle("Setting Live Wallpaper..")
                .setSmallIcon(R.mipmap.logo)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(00101, notification);

        new Thread(() -> {
            Intent intents = new Intent(
                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intents.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this,
                    VideoWallpaperMuteService.class));
            intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intents);

        }).start();
/*
        new Handler().postDelayed(() -> {

            stopForeground(true);

            stopService(new Intent(this, StartForeService.class));

        }, 1000);*/

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "RUNNING SEVICE",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
