package com.wallpo.android.service;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.activity.ChatActivity;
import com.wallpo.android.activity.MessageActivity;
import com.wallpo.android.utils.updatecode;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        Log.d("TAG", "onReceive: receiver");
        String type = intent.getStringExtra("sendtype");

        Log.d("TAG", "onReceive: receiver with " + type);

        if(type == null){
            Log.d("TAG", "onReceive: Error Null");
            return;
        }
        
        if (type.equals("chatactivity")) {

            Intent i = new Intent(context, MessageActivity.class);

            MessageActivity.messagetype = "olduser";
            MessageActivity.redirectto = "go";
/*
            sharedPreferences.edit().putString("useridprofile", intent.getStringExtra("useridprofile")).apply();
            sharedPreferences.edit().putString("usernameprofile", intent.getStringExtra("usernameprofile")).apply();
            sharedPreferences.edit().putString("displaynameprofile", intent.getStringExtra("displaynameprofile")).apply();
            sharedPreferences.edit().putString("displaynameprofile", intent.getStringExtra("displaynameprofile")).apply();
            sharedPreferences.edit().putString("verifiedprofile", intent.getStringExtra("verifiedprofile")).apply();
            sharedPreferences.edit().putString("descriptionprofile", intent.getStringExtra("descriptionprofile")).apply();
            sharedPreferences.edit().putString("websitesprofile", intent.getStringExtra("websitesprofile")).apply();
            sharedPreferences.edit().putString("categoryprofile", intent.getStringExtra("categoryprofile")).apply();
            sharedPreferences.edit().putString("backphotoprofile", intent.getStringExtra("backphotoprofile")).apply();
            sharedPreferences.edit().putString("subscribedprofile", intent.getStringExtra("subscribedprofile")).apply();
            sharedPreferences.edit().putString("subscriberprofile", intent.getStringExtra("subscriberprofile")).apply();
*/
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);

        }

        if (type.equals("startservice")){
        //    context.startService(new Intent(context, ChatMessageService.class));;
        }
        if (type.equals("wallaperset")){
            String posturl = intent.getStringExtra("posturl");
            String typeurl = intent.getStringExtra("type");
            String photoid = intent.getStringExtra("photoid");

            if (posturl.isEmpty()) {
                Intent clickintent = new Intent(context, MainActivity.class);
                clickintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(clickintent);
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("WALLPAPER NOTIFICATION",
                            "Wallpaper Setter", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager = context.getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WALLPAPER NOTIFICATION");
                builder.setSmallIcon(R.drawable.logo);
                builder.setContentTitle("Error while setting wallpaper.");
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(1402, builder.build());
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("WALLPAPER NOTIFICATION", "Wallpo Posts", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = context.getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WALLPAPER NOTIFICATION");
            builder.setSmallIcon(R.drawable.logo);
            builder.setContentTitle("Please wait, Adjusting Wallpaper for your device.");
            builder.setAutoCancel(true);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1402, builder.build());


            if (posturl.contains(".mp4")) {
                updatecode.changelivewallpaper(context, posturl, Integer.parseInt(photoid), null);
            } else {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(() -> {
                    Picasso.get().load(posturl).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            if (bitmap == null) {
                                Toast.makeText(context, context.getResources().getString(R.string.imagenotloadedproperly), Toast.LENGTH_SHORT).show();
                                builder.setContentTitle("Error while, Adjusting Wallpaper for your device.");
                                return;
                            }

                            if (typeurl.equals("lockscreen")) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    updatecode.changelockscreen(context, null, bitmap, Integer.parseInt(photoid));
                                }
                            } else {
                                updatecode.changehomescreen(context, null, bitmap, Integer.parseInt(photoid));
                            }
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            builder.setContentTitle("Error while, Adjusting Wallpaper for your device.");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                });

            }

        }
    }
}
