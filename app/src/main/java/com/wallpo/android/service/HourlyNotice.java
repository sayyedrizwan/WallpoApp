package com.wallpo.android.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.wallpo.android.R;
import com.wallpo.android.extra.DeepLinkActivity;
import com.wallpo.android.utils.URLS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HourlyNotice extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.getrecdata)
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HomeFragment", "onFailure: ", e);

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");
                try {
                    JSONArray array = new JSONArray(data);

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject Object = array.getJSONObject(i);

                        int photoid = Object.getInt("photoid");
                        String caption = Object.getString("caption");
                        String categoryid = Object.getString("categoryid");
                        String albumid = Object.getString("albumid");
                        String datecreated = Object.getString("datecreated");
                        String dateshowed = Object.getString("dateshowed");
                        String link = Object.getString("link");
                        String userid = Object.getString("userid");
                        String imagepath = Object.getString("imagepath");
                        String likes = Object.getString("likes");
                        int trendingcount = Object.getInt("trendingcount");

                        Bitmap bitmap = getBitmapfromUrl(imagepath);

                        String notData = "https://thewallpo.com/posts/R12pQ" + photoid + "Wz1P";

                        Intent clickintent = new Intent(context, DeepLinkActivity.class);
                        clickintent.setData(Uri.parse(notData));

                        clickintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(clickintent);
                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("SUGGESTIONS NOTIFICATION", "Post suggestions",
                                    NotificationManager.IMPORTANCE_HIGH);

                            NotificationManager manager = context.getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(channel);
                        }
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "SUGGESTIONS NOTIFICATION");
                        builder.setContentTitle("Suggestions:- " + caption);
                        builder.setSmallIcon(R.mipmap.logo);
                        builder.setLargeIcon(bitmap);
                        builder.setContentIntent(pendingIntent);
                        builder.setAutoCancel(true);
                        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(747, builder.build());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }

}
