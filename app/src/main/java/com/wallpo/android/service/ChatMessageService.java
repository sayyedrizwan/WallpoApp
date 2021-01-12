package com.wallpo.android.service;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallpo.android.R;
import com.wallpo.android.utils.URLS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class ChatMessageService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Context context = this;

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("wallpouserid", "");


        if (!id.isEmpty()) {
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("userschat").child(id).child("olduser");

            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    Log.d(TAG, "onChildChanged: " + snapshot.getKey());

                    final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

                    final OkHttpClient client = new OkHttpClient();

                    RequestBody postData = new FormBody.Builder().add("userid", Objects.requireNonNull(snapshot.getKey())).build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.userinfo)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            Log.e(TAG, "onFailure: ", e);

                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");

                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(() -> {
                                try {
                                    JSONArray array = new JSONArray(data);

                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String usernamestring = object.getString("username").trim();
                                        String displaynamest = object.getString("displayname").trim();
                                        String profilephotost = object.getString("profilephoto").trim();

                                        String verified = object.getString("verified").trim();
                                        String description = object.getString("description").trim();
                                        String category = object.getString("category").trim();
                                        String websites = object.getString("websites").trim();
                                        String backphoto = object.getString("backphoto").trim();
                                        String subscribed = object.getString("subscribed").trim();
                                        String subscribers = object.getString("subscribers").trim();


                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "id", Objects.requireNonNull(snapshot.getKey())).apply();
                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "username", usernamestring).apply();
                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "displayname", displaynamest).apply();
                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "profilephoto", profilephotost).apply();

                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "verified", verified).apply();
                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "description", description).apply();
                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "category", category).apply();
                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "websites", websites).apply();
                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "backphoto", backphoto).apply();
                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "subscribed", subscribed).apply();
                                        wallpouserdata.edit().putString(Objects.requireNonNull(snapshot.getKey()) + "subscribers", subscribers).apply();

                                       Picasso.get().load(profilephotost).into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                                Intent clickintent = new Intent(context, NotificationReceiver.class);
                                                clickintent.putExtra("sendtype", "chatactivity");
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
                                                clickintent.putExtra("subscriberprofile", subscribers);

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
                                                notificationManager.notify(Integer.parseInt(snapshot.getKey()), builder.build());

                                            }

                                            @Override
                                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });

                                    }

                                } catch (
                                        JSONException e) {
                                    e.printStackTrace();
                                }
                            });


                        }
                    });


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("sendtype", "startservice");
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
