package com.wallpo.android.service;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.extra.DeepLinkActivity;
import com.wallpo.android.roomdbs.Notificationdb;
import com.wallpo.android.roomdbs.Roomdb;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class FCMservice extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull final String s) {
        super.onNewToken(s);

        SharedPreferences sharedPreferences = getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);

        String accounts = sharedPreferences.getString("accounts", "").replace(" ", "");

        if (!accounts.equals("")) {
            try {
                String[] acc = accounts.split(",");
                for (String account : acc) {
                    String id = account.trim();
                    String str = id;
                    String kept = str.substring(0, str.indexOf("-"));
                    String remainder = str.substring(str.indexOf(",") + 1, str.length());

                    if (!kept.isEmpty()) {

                        String tz = TimeZone.getDefault().getID();
                        OkHttpClient client = new OkHttpClient();

                        RequestBody postData = new FormBody.Builder().add("userid", kept)
                                .add("fcmtoken", s).add("date", tz).add("type", "fcmupdate").build();

                        okhttp3.Request request = new okhttp3.Request.Builder()
                                .url(URLS.lastseenuser)
                                .post(postData)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .build();

                        client.newCall(request).enqueue(new okhttp3.Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("FCM", "onFailure: ", e);
                            }

                            @Override
                            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                                final String data = response.body().string().replaceAll(",\\[]", "");

                                Log.d("FCM", "onResponse: " + data);

                                sharedPreferences.edit().putString("fcmtoken", s).apply();


                            }
                        });

                    }
                }

            } catch (StringIndexOutOfBoundsException e) {
                Log.d("TAG", "onNewToken: " + e);
            }

        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        if (remoteMessage.getData().size() > 0) {

            String title = remoteMessage.getData().get("title");
            String messgae = remoteMessage.getData().get("message");
            String imageurl = remoteMessage.getData().get("imageurl");
            String displayname = remoteMessage.getData().get("displayname");
            String extraid = remoteMessage.getData().get("extraid");
            String profilephoto = remoteMessage.getData().get("profilephoto");

            Log.d("FCMservice", "onMessageReceived: title: " + title + " message: " + messgae
                    + " imageurl: " + imageurl);

            if (imageurl == null) {
                imageurl = "";
            }
            if (title == null) {
                return;
            }
            if (title.isEmpty()) {
                return;
            }

            if (title.equals("chatnoti") && messgae.equals("chatnoti")) {

                chatnoti(this, profilephoto, displayname, extraid);

                return;
            }

            if (title.equals("notificationUsers") && messgae.equals("notificationUsers")) {

                postsNotification(this, remoteMessage);

                return;
            }
            if (title.equals("setwallpapers") && messgae.equals("setwallpapers")) {

                setWallpapers(this, remoteMessage);

                return;
            }
            if (title.equals(messgae)) {
                Log.d("", "onMessageReceived: is same");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("POSTS NOTIFICATION", "Wallpo Posts", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "POSTS NOTIFICATION");
                builder.setSmallIcon(R.drawable.logo);
                builder.setContentTitle("Please update wallpo.");
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(14587, builder.build());
                return;
            }


            if (imageurl == null) {
                createnotificationwithouturl(this, title, messgae);
            } else {
                if (imageurl.isEmpty()) {
                    createnotificationwithouturl(this, title, messgae);
                } else {
                    createnotification(this, title, messgae, imageurl);
                }
            }

            updatecode.analyticsFirebase(this, "notification_received", "notification_received");

        }

        if (remoteMessage.getNotification() != null) {

            String title = remoteMessage.getData().get("title");
            String messgae = remoteMessage.getData().get("message");
            String imageurl = remoteMessage.getData().get("imageurl");
            String extraid = remoteMessage.getData().get("extraid");
            String displayname = remoteMessage.getData().get("displayname");
            String profilephoto = remoteMessage.getData().get("profilephoto");


            if (imageurl == null) {
                imageurl = "";
            }
            if (title == null) {
                return;
            }
            if (title.isEmpty()) {
                return;
            }

            if (title.equals("chatnoti") && messgae.equals("chatnoti")) {

                chatnoti(this, profilephoto, displayname, extraid);

                return;
            }

            if (title.equals("notificationUsers") && messgae.equals("notificationUsers")) {

                postsNotification(this, remoteMessage);

                return;
            }

            if (title.equals("setwallpapers") && messgae.equals("setwallpapers")) {

                setWallpapers(this, remoteMessage);

                return;
            }

            if (title.equals(messgae)) {
                Log.d("", "onMessageReceived: is same");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("POSTS NOTIFICATION", "Wallpo Posts", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "POSTS NOTIFICATION");
                builder.setSmallIcon(R.drawable.logo);
                builder.setContentTitle("Please update wallpo.");
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(14587, builder.build());
                return;
            }


            if (imageurl == null) {

                createnotificationwithouturl(this, title, messgae);
            } else {
                if (imageurl.isEmpty()) {
                    createnotificationwithouturl(this, title, messgae);
                } else {
                    createnotification(this, title, messgae, imageurl);
                }
            }


            updatecode.analyticsFirebase(this, "notification_received", "notification_received");

        }
    }

    private void postsNotification(Context context, RemoteMessage remoteMessage) {

        String username = remoteMessage.getData().get("username");
        String displayname = remoteMessage.getData().get("displayname");
        String postid = remoteMessage.getData().get("postid");
        String caption = remoteMessage.getData().get("caption");
        String imagepath = remoteMessage.getData().get("imagepath");
        String theuserid = remoteMessage.getData().get("theuserid");
        String type = remoteMessage.getData().get("type");
        String timestamp = remoteMessage.getData().get("timestamp");

        if (type == null) {
            return;
        }
        if (type.isEmpty()) {
            return;
        }

        Roomdb db = Roomdb.getInstance(context.getApplicationContext());
        Notificationdb notifications = new Notificationdb(0, username, displayname, postid,
                caption, imagepath, type, theuserid, timestamp);

        db.mainDao().insert(notifications);

        String notData = "https://thewallpo.com/posts/R12pQ" + postid + "Wz1P";

        if (type.equals("uploadsposts")) {
            notData = "https://thewallpo.com/posts/R12pQ" + postid + "Wz1P";
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("POSTS NOTIFICATION", "Wallpo Posts", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "POSTS NOTIFICATION");
            builder.setSmallIcon(R.drawable.logo);
            builder.setContentTitle("New Notification type, Please update wallpo.");
            builder.setAutoCancel(true);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(Integer.parseInt(postid), builder.build());
            return;
        }

        Intent clickintent = new Intent(context, DeepLinkActivity.class);
        clickintent.setData(Uri.parse(notData));

        clickintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(clickintent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("POSTS NOTIFICATION", "Wallpo Posts", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "POSTS NOTIFICATION");
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("New Post from " + displayname);
        // builder.setLargeIcon(bitmap);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1452, builder.build());


    }

    private void chatnoti(Context context, String profilephoto, String displaynamest, String id) {

        Picasso.get().load(profilephoto).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Intent clickintent = new Intent(context, NotificationReceiver.class);
                     /*   clickintent.putExtra("sendtype", "chatactivity");
                        clickintent.putExtra("useridprofile", snapshot.getKey());
                        clickintent.putExtra("usernameprofile", usernamestring);
                        clickintent.putExtra("displaynameprofile", displaynamest);
                        clickintent.putExtra("verifiedprofile", verified);
                        clickintent.putExtra("descriptionprofile", description);
                        clickintent.putExtra("categoryprofile", category);
                        clickintent.putExtra("backphotoprofile", backphoto);
                        clickintent.putExtra("descriptionprofile", description);
                        clickintent.putExtra("websitesprofile", websites);
                        clickintent.putExtra("subscribedprofile", subscribed);
                        clickintent.putExtra("subscriberprofile", subscribers);*/

                PendingIntent clickPendingintent = PendingIntent.getBroadcast(context, 0, clickintent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("CHAT NOTIFICATION", "Wallpo Message", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHAT NOTIFICATION");
                builder.setLargeIcon(bitmap);
                builder.setContentTitle(displaynamest);
                builder.setContentText(getString(R.string.newmessage));
                builder.setContentIntent(clickPendingintent);
                builder.setSmallIcon(R.drawable.logo);
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(Integer.parseInt(id), builder.build());

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

    }

    private void createnotification(Context context, String title, String message, String imageurl) {

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(() -> Picasso.get().load(imageurl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Intent clickintent = new Intent(context, MainActivity.class);
                clickintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(clickintent);
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("OFFICIAL NOTIFICATION", "Wallpo official", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "OFFICIAL NOTIFICATION")
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setLargeIcon(bitmap)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigLargeIcon(bitmap).bigPicture(bitmap))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(3, builder.build());

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                Intent clickintent = new Intent(context, MainActivity.class);
                clickintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(clickintent);
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("OFFICIAL NOTIFICATION", "Wallpo official", NotificationManager.IMPORTANCE_HIGH);

                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "OFFICIAL NOTIFICATION")
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(3, builder.build());

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        }));

    }

    private void createnotificationwithouturl(Context context, String title, String message) {

        Intent clickintent = new Intent(context, MainActivity.class);
        clickintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(clickintent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("OFFICIAL NOTIFICATION", "Wallpo official", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "OFFICIAL NOTIFICATION")
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(3, builder.build());

    }

    private void setWallpapers(Context context, RemoteMessage remoteMessage) {
        String posturl = remoteMessage.getData().get("posturl");
        String type = remoteMessage.getData().get("type");
        String photoid = remoteMessage.getData().get("photoid");

        Intent i = new Intent(context, NotificationReceiver.class);
        i.putExtra("sendtype", "wallaperset");
        i.putExtra("posturl", posturl);
        i.putExtra("type", type);
        i.putExtra("photoid", photoid);
        sendBroadcast(i);



    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

}
