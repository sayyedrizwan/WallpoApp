package com.wallpo.android.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AndroidRuntimeException;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;
import com.wallpo.android.R;
import com.wallpo.android.activity.WallpaperSetActivity;
import com.wallpo.android.adapter.messageshareadapter;
import com.wallpo.android.getset.Category;
import com.wallpo.android.getset.ChatUsers;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.subscription.PremiumActivity1;
import com.wallpo.android.uploads.uploadcategoryadapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.google.firebase.database.ServerValue.TIMESTAMP;
import static com.wallpo.android.MainActivity.initializeSSLContext;

public class updatecode {

    public static String tz = TimeZone.getDefault().getID();

    public static void updateFCM(final Context context) {

        initializeSSLContext(context);

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        if (id.isEmpty()){
            Log.e(TAG, "updateFCM: nouser");
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("MainActivity", "getInstanceId failed", task.getException());

                        return;
                    }
                    final String token = task.getResult().getToken();

                    OkHttpClient client = new OkHttpClient();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.getipaddress)
                            .get()
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                            updateseen(context, "0.0", token);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");
                            ((Activity) context).runOnUiThread(() -> {

                                updateseen(context, data.trim(), token);
                                sharedPreferences.edit().putString("useripaddress", data.trim()).apply();

                            });
                        }
                    });

                });
    }

    public static void updateseen(Context context, String ip, String token) {

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");


        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("date", tz)
                    .add("userid", id).add("ip", ip).add("fcmtoken", token).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.lastseenuser)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                ((Activity) context).runOnUiThread(() -> {

                    Log.d(TAG, "onResponse: fcmupdate " + data);
                    sharedPreferences.edit().putString("lastseenactive", getTimestamp()).apply();
                    sharedPreferences.edit().putString("fcmtoken", token).apply();


                });

            }
        });

    }

    public static void sendNotificationToUsers(final Context context, String type, String downloadLink) {

        initializeSSLContext(context);

        updatecode.analyticsFirebase(context, "sub_notification", "sub_notification");

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");
        final String fcmtoken = sharedPreferences.getString("fcmtoken", "");

        if(id.isEmpty()){
            return;
        }

        final OkHttpClient clientfav = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(id))
                .add("downloadlink", downloadLink).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.sendpostsnotification)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientfav.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                Log.d(TAG, "onResponse: av of " + data);

            }
        });

    }


    public static void addfav(final Context context, int photoid) {

        initializeSSLContext(context);

        updatecode.analyticsFirebase(context, "added_fav", "added_fav");

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");
        final String fcmtoken = sharedPreferences.getString("fcmtoken", "");

        final OkHttpClient clientfav = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(photoid))
                .add("usersid", id).add("timezone", tz).add("fcm", fcmtoken)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.favbutton)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientfav.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                Log.d(TAG, "onResponse: av of " + data);

            }
        });

    }


    public static void unfav(final Context context, int photoid) {

        initializeSSLContext(context);

        updatecode.analyticsFirebase(context, "remove_fav", "remove_fav");

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");
        final String fcmtoken = sharedPreferences.getString("fcmtoken", "");

        final OkHttpClient clientunfav = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(photoid))
                .add("usersid", id).add("timezone", tz).add("fcm", fcmtoken)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.unfavbutton)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientunfav.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

            }
        });

    }


    public static void addlike(final Context context, int photoid) {

        initializeSSLContext(context);

        updatecode.analyticsFirebase(context, "liked_posts", "liked_posts");

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        final OkHttpClient clientli = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("usersid", id)
                .add("photoid", String.valueOf(photoid)).add("timezone", tz)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.likebutton)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientli.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                Log.d(TAG, "onResponse: " + data);
            }
        });

    }


    public static void shareintent(final Context context, String text) {

        Common.SEARCHSTART = "";

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.share_layout);

        CardView copylink = dialog.findViewById(R.id.copylink);
        AppCompatTextView textMain = dialog.findViewById(R.id.text);
        Button done = dialog.findViewById(R.id.done);

        updatecode.analyticsFirebase(context, "share_intent", "share_intent");

        if (text.contains("user")) {
            textMain.setText(context.getResources().getString(R.string.shareprofile));
        }
        if (text.contains("album")) {
            textMain.setText(context.getResources().getString(R.string.sharealbums));
        }

        copylink.setOnClickListener(v -> {

            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = null;
            String url = "";

            if (text.contains("posts")) {
                int ch = Integer.parseInt(text.substring(text.lastIndexOf("/") + 1));
                //   int no = ch * 17147 * 877;
                url = "https://thewallpo.com/posts/" + "R12pQ" + ch + "Wz1P";

            } else if (text.contains("user")) {
                url = "https://thewallpo.com/" + Common.MESSAGE_DATA;
            } else if (text.contains("album")) {
                String ch = text.substring(text.lastIndexOf("/") + 1);
                int ch1 = Integer.parseInt(ch);
                url = "https://thewallpo.com/album/" + "AL_1DR" + ch1 + "SO1ML";
            }

            updatecode.analyticsFirebase(context, "copy_link", "copy_link");

            clip = ClipData.newPlainText("wallpolink", url);
            clipboard.setPrimaryClip(clip);

            TransitionManager.beginDelayedTransition(copylink);

            done.setText(context.getResources().getString(R.string.linkcopied));

            new Handler().postDelayed(() -> done.setText(context.getResources().getString(R.string.donebig)), 2000);
        });

        CardView shareexternal = dialog.findViewById(R.id.shareexternal);
        shareexternal.setOnClickListener(v -> {

            String url = "";

            if (text.contains("posts")) {
                String ch = text.substring(text.lastIndexOf("/") + 1);
             //   int no = Integer.parseInt(ch) * 17147 * 877;
                url = "https://thewallpo.com/posts/" + "R12pQ" + ch + "Wz1P";
            } else if (text.contains("user")) {
                url = "https://thewallpo.com/" + Common.MESSAGE_DATA;
            } else if (text.contains("album")) {
                String ch = text.substring(text.lastIndexOf("/") + 1);
                int ch1 = Integer.parseInt(ch);
                url = "https://thewallpo.com/album/" + "AL_1DR" + ch1 + "SO1ML";
            }

            updatecode.analyticsFirebase(context, "share_link", "share_link");

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, url);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, text);
            context.startActivity(shareIntent);

        });


        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");
        TextInputEditText sharetext = dialog.findViewById(R.id.sharetext);
        sharetext.setVisibility(View.GONE);
        RelativeLayout mainlay = dialog.findViewById(R.id.mainlay);
        CardView find = dialog.findViewById(R.id.find);

        SpinKitView loadingbar = dialog.findViewById(R.id.loadingbar);

        loadingbar.setVisibility(View.GONE);
        if (userid.isEmpty()) {

            sharetext.setVisibility(View.GONE);
            find.setVisibility(View.GONE);

            loadingbar.setVisibility(View.GONE);
        }else {

            List<ChatUsers> chatsList = new ArrayList<>();
            RecyclerView messagerecyclerview = dialog.findViewById(R.id.messagerecyclerview);
            messagerecyclerview.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            messagerecyclerview.setLayoutManager(linearLayoutManager);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userschat").child(userid).child("olduser");
            reference.orderByChild("time").limitToLast(15).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatUsers chats = snapshot.getValue(ChatUsers.class);
                        chatsList.add(chats);

                    }
                    Collections.reverse(chatsList);
                    messageshareadapter messageshareadapter = new messageshareadapter(context, chatsList);
                    messagerecyclerview.setAdapter(messageshareadapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            find.setOnClickListener(v -> {
                Common.SEARCHSTART = "searched";

                updatecode.analyticsFirebase(context, "search_link_user", "search_link_user");

                TransitionManager.beginDelayedTransition(mainlay);
                if (sharetext.getVisibility() == View.GONE) {
                    sharetext.setVisibility(View.VISIBLE);
                } else {
                    sharetext.setVisibility(View.GONE);
                }
            });

            sharetext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    new Handler().postDelayed(() -> {
                        if (sharetext.getText().toString().equals(s.toString())) {
                            if (!sharetext.getText().toString().isEmpty()) {
                                loadingbar.setVisibility(View.VISIBLE);
                                OkHttpClient client = new OkHttpClient();

                                RequestBody postData = new FormBody.Builder().add("sharetext", String.valueOf(sharetext.getText().toString())).build();

                                okhttp3.Request request = new okhttp3.Request.Builder()
                                        .url(URLS.chatsusers)
                                        .post(postData)
                                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        ((Activity) context).runOnUiThread(() -> Toast.makeText(context, context.getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final String data = response.body().string().replaceAll(",\\[]", "");

                                        ((Activity) context).runOnUiThread(() -> {

                                            chatsList.clear();
                                            try {
                                                JSONArray array = new JSONArray(data);

                                                for (int i = 0; i < array.length(); i++) {

                                                    JSONObject Object = array.getJSONObject(i);

                                                    String userid1 = Object.getString("userid");

                                                    ChatUsers chats = new ChatUsers(userid1, "", 0);
                                                    chatsList.add(chats);


                                                }

                                                messageshareadapter messageshareadapter = new messageshareadapter(context, chatsList);
                                                messagerecyclerview.setAdapter(messageshareadapter);

                                                loadingbar.setVisibility(View.GONE);

                                            } catch (
                                                    JSONException e) {
                                                e.printStackTrace();
                                            }

                                        });
                                    }
                                });
                            }
                        }
                    }, 1000);
                }
            });

        }
        done.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    public static void unlike(final Context context, int photoid) {

        initializeSSLContext(context);

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        final OkHttpClient clientwe = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(photoid))
                .add("usersid", id).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.unliked)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientwe.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                Log.d(TAG, "onResponse: " + data);

            }
        });

    }

    public static void trendingcounts(final Context context, int photoid) {

        final OkHttpClient clienttrend = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(photoid)).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.trendingcount)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clienttrend.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                Log.d(TAG, "onResponse: " + data);

            }
        });
    }


    public static void updatewallpaper(final Context context, int photoid, String type) {

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        if (id.isEmpty()) {
            return;
        }

        final OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(photoid))
                .add("userid", id).add("timezone", tz).add("type", type).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.usedupdate)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");


            }
        });
    }

    public static void increaseno(String photoid, String type) {

        final OkHttpClient clientdel = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("uid", String.valueOf(photoid))
                .add("type", type).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://thewallpo.com/wallpoappphp@1234/adsconsole/increaseno")
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientdel.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

            }
        });
    }

/*
    public static int getNotiCount(Context context) {
        try {
            String countQuery = "SELECT  * FROM " + LIKE_TABLE_NAME;
            SQLiteOpenHelper database = new likeDatabaseHelper(context);
            SQLiteDatabase db = database.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        } catch (SQLiteException e) {
            return 0;
        }
    }
*/

    public static void getusernameid(final Context context, final String matchedText) {

        OkHttpClient client = new OkHttpClient();

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");


        if (matchedText != null || !matchedText.isEmpty()) {


            RequestBody postData = new FormBody.Builder().add("username", matchedText.substring(1))
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.usernamemention)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: ", e);
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (data.trim().equals("[]")) {
                                Toast.makeText(context, context.getResources().getString(R.string.nouserfound), Toast.LENGTH_LONG).show();
                                return;
                            }

                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    String userid = object.getString("userid").trim();
                                    String username = object.getString("username").trim();
                                    String verified = object.getString("verified").trim();
                                    String description = object.getString("description").trim();
                                    String displayname = object.getString("displayname").trim();
                                    String category = object.getString("category").trim();
                                    String websites = object.getString("websites").trim();
                                    String profilephoto = object.getString("profilephoto").trim();
                                    String backphoto = object.getString("backphoto").trim();
                                    String subscribed = object.getString("subscribed").trim();
                                    String subscribers = object.getString("subscribers").trim();

                                    Intent profile = new Intent(context, ProfileActivity.class);

                                    sharedPreferences.edit().putString("useridprofile", userid).apply();
                                    sharedPreferences.edit().putString("usernameprofile", username).apply();
                                    sharedPreferences.edit().putString("displaynameprofile", displayname).apply();
                                    sharedPreferences.edit().putString("profilephotoprofile", profilephoto).apply();
                                    sharedPreferences.edit().putString("verifiedprofile", verified).apply();
                                    sharedPreferences.edit().putString("descriptionprofile", description).apply();
                                    sharedPreferences.edit().putString("websitesprofile", websites).apply();
                                    sharedPreferences.edit().putString("categoryprofile", category).apply();
                                    sharedPreferences.edit().putString("backphotoprofile", backphoto).apply();
                                    sharedPreferences.edit().putString("subscribedprofile", subscribed).apply();
                                    sharedPreferences.edit().putString("subscriberprofile", subscribers).apply();

                                    context.startActivity(profile);

                                }

                            } catch (
                                    JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });
        }

    }


    public static String getStringImage(Bitmap bitmap, int quality) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);


        return encodeImage;

    }

    public static String checkchatgroup(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");
        String useridprofile = sharedPreferences.getString("useridprofile", "");

        String time = sharedPreferences.getString(useridprofile + "date", "");

        if (!time.equals(updatecode.getTimestampday())) {
            DatabaseReference referenceq = FirebaseDatabase.getInstance().getReference("userschat").child(useridprofile)
                    .child("newuser").child(userid);
            referenceq.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (!snapshot.exists()) {

                        DatabaseReference referenceq = FirebaseDatabase.getInstance().getReference("userschat").child(useridprofile)
                                .child("olduser").child(userid);
                        referenceq.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (!snapshot.exists()) {

                                    DatabaseReference references = FirebaseDatabase.getInstance().getReference();
                                    HashMap<String, Object> hashMaps = new HashMap<>();
                                    hashMaps.put("chatuser", userid);
                                    hashMaps.put("status", "unseen");
                                    hashMaps.put("time", TIMESTAMP);

                                    references.child("userschat/" + useridprofile + "/newuser/" + userid).setValue(hashMaps);
                                }

                                sharedPreferences.edit().putString(useridprofile + "date", updatecode.getTimestampday()).apply();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                sharedPreferences.edit().putString(useridprofile + "date", "").apply();
                            }
                        });


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    sharedPreferences.edit().putString(useridprofile + "date", "").apply();
                }
            });

            DatabaseReference references = FirebaseDatabase.getInstance().getReference("userschat").child(userid)
                    .child("olduser").child(useridprofile);
            references.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (!snapshot.exists()) {
                        DatabaseReference references = FirebaseDatabase.getInstance().getReference("userschat").child(userid)
                                .child("newuser").child(useridprofile);
                        references.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (!snapshot.exists()) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("chatuser", useridprofile);
                                    hashMap.put("status", "unseen");
                                    hashMap.put("time", TIMESTAMP);

                                    reference.child("userschat/" + userid + "/olduser/" + useridprofile).setValue(hashMap);

                                }


                                sharedPreferences.edit().putString(useridprofile + "date", updatecode.getTimestampday()).apply();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                sharedPreferences.edit().putString(useridprofile + "date", "").apply();
                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    sharedPreferences.edit().putString(useridprofile + "date", "").apply();
                }
            });

        }
        return null;
    }

    public static String daynumber(String number) {

        try {
            final int likk = Integer.parseInt(number);
            String value = "";

            if (likk < 1) {
                value = "0";
            } else if (likk < 999 && likk > 1) {
                value = "" + likk;
            } else if (likk < 9999 && likk > 999) {
                String no = String.valueOf(likk).substring(0, 1);
                value = no + "K";
            } else if (likk < 99999 && likk > 9999) {
                String no = String.valueOf(likk).substring(0, 2);
                value = no + "K";
            } else if (likk < 999999 && likk > 99999) {
                String no = String.valueOf(likk).substring(0, 3);
                value = no + "K";
            } else if (likk < 9999999 && likk > 999999) {
                String no = String.valueOf(likk).substring(0, 1);
                value = no + "M ";
            } else if (likk < 99999999 && likk > 9999999) {
                String no = String.valueOf(likk).substring(0, 2);
                value = no + "M ";
            } else if (likk < 999999999 && likk > 99999999) {
                String no = String.valueOf(likk).substring(0, 3);
                value = no + "M ";
            } else {

                try {
                    // The comma in the format specifier does the trick
                    String s = String.format("%,d", Long.parseLong(likk + ""));
                    value = s;
                } catch (NumberFormatException e) {
                    Log.i(TAG, "onResponse: error");
                }
            }

            return value;
        } catch (NumberFormatException e) {
            return "";
        }
    }


    public static String changehomescreen(Context context, View view, Bitmap bitmap, int photoid) {

        loadAds(context, "wallpapers");

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);

        String wallpapersettertype = sharedPreferences.getString("wallpapaersettertype", "");

        if (wallpapersettertype.equals("fit")) {

            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;

            Bitmap yourbitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(yourbitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                } else {

                    wallpaperManager.setBitmap(yourbitmap);
                }
                if (view != null) {
                    Snackbar.make(view, context.getResources().getString(R.string.homscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            updatewallpaper(context, photoid, "wallpaper");

        } else if (wallpapersettertype.equals("crop")) {

            Intent i = new Intent(context, WallpaperSetActivity.class);
            Common.wallpapertype = "wallpaper";

            if (view != null) {
                Common.view = view;
            }
            Common.BITMAP = bitmap;
            context.startActivity(i);

            updatewallpaper(context, photoid, "wallpaper");


        } else if (wallpapersettertype.equals("options")) {


            if (view == null) {
                WallpaperManager manager = WallpaperManager.getInstance(context.getApplicationContext());

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    } else {

                        manager.setBitmap(bitmap);
                    }

                    updatewallpaper(context, photoid, "wallpaper");

                } catch (NullPointerException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.wallpaper_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.defualt) {

                        WallpaperManager manager = WallpaperManager.getInstance(context.getApplicationContext());

                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                            } else {

                                manager.setBitmap(bitmap);
                            }

                            Snackbar.make(view, context.getResources().getString(R.string.homscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();

                            updatewallpaper(context, photoid, "wallpaper");


                        } catch (NullPointerException e) {
                            e.getMessage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else if (menuItem.getItemId() == R.id.fit) {

                        DisplayMetrics metrics = new DisplayMetrics();
                        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int height = metrics.heightPixels;
                        int width = metrics.widthPixels;

                        Bitmap yourbitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wallpaperManager.setBitmap(yourbitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                            } else {

                                wallpaperManager.setBitmap(yourbitmap);
                            }

                            Snackbar.make(view, context.getResources().getString(R.string.homscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        updatewallpaper(context, photoid, "wallpaper");

                    } else if (menuItem.getItemId() == R.id.crop) {

                        Intent i = new Intent(context, WallpaperSetActivity.class);
                        Common.wallpapertype = "wallpaper";
                        if (view != null) {
                            Common.view = view;
                        }
                        Common.ID_SELECTED = photoid;
                        Common.BITMAP = bitmap;
                        context.startActivity(i);

                        updatewallpaper(context, photoid, "wallpaper");

                    }

                    return true;
                });

                popupMenu.show();
            }
        } else {

            WallpaperManager manager = WallpaperManager.getInstance(context.getApplicationContext());

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                } else {

                    manager.setBitmap(bitmap);
                }
                if (view != null) {
                    Snackbar.make(view, context.getResources().getString(R.string.homscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();
                }
                updatewallpaper(context, photoid, "wallpaper");


            } catch (NullPointerException e) {
                e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        updatecode.analyticsFirebase(context, "setting_wallpaper_homescreen", "setting_wallpaper_homescreen");


        return null;
    }

    public static String changelivewallpaper(Context context, String imgpath, int photoid, View view) {

        loadAds(context, "wallpapers");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("WALLPAPER NOTIFICATION", "Wallpaper Setter",
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WALLPAPER NOTIFICATION");
        builder.setContentTitle(context.getResources().getString(R.string.livevideoadjusting));
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setProgress(100, 0, true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(17, builder.build());

        FetchListener fetchListener = new FetchListener() {
            @Override
            public void onAdded(@NotNull Download download) {

            }

            @Override
            public void onQueued(@NotNull Download download, boolean b) {

            }

            @Override
            public void onWaitingNetwork(@NotNull Download download) {

            }

            @Override
            public void onCompleted(@NotNull Download download) {

                builder.setContentTitle(context.getResources().getString(R.string.livevideocompleted))
                        .setProgress(0, 0, false);
                notificationManager.notify(17, builder.build());


                SharedPreferences sharedPreferences = context.getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);
                String videosaver = sharedPreferences.getString("videosaver", "");

                updatecode.analyticsFirebase(context, "setting_wallpaper_live", "setting_wallpaper_live");

                if (videosaver.isEmpty()) {
                    Common.VIDEO_DLD_PATH = download.getFile();
                    try {
                        Intent intent = new Intent(
                                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context,
                                VideoWallpaperMuteService.class));
                        context.startActivity(intent);
                    } catch (AndroidRuntimeException e) {
                        new Thread(() -> {
                            Intent intents = new Intent(
                                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                            intents.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context,
                                    VideoWallpaperMuteService.class));
                            intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intents);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if(!Settings.canDrawOverlays(context)){
                                    Intent clickintent = new Intent(context, WallpaperWebActivity.class);
                                    clickintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                                    stackBuilder.addNextIntentWithParentStack(clickintent);
                                    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                    builder.setContentIntent(pendingIntent);
                                    builder.setContentTitle("Click Here to set Live Wallpaper.")
                                            .setProgress(0, 0, false);
                                    builder.setContentText("Permission is not enabled to set live wallpaper directly, click here to set live wallpaper or give the permission");
                                    notificationManager.notify(17, builder.build());

                                }
                            }


                        }).start();

                    }

                    updatewallpaper(context, photoid, "wallpaper");
                } else {
                    Common.VIDEO_DLD_PATH = download.getFile();
                    try {
                        Intent intent = new Intent(
                                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context,
                                VideoWallpaperService.class));
                        context.startActivity(intent);
                    } catch (AndroidRuntimeException e) {
                        new Thread(() -> {

                            Intent intent = new Intent(
                                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context,
                                    VideoWallpaperService.class));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if(!Settings.canDrawOverlays(context)){
                                    Intent clickintent = new Intent(context, WallpaperWebActivity.class);
                                    clickintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                                    stackBuilder.addNextIntentWithParentStack(clickintent);
                                    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                    builder.setContentIntent(pendingIntent);
                                    builder.setContentTitle("Click Here to set Live Wallpaper.")
                                            .setProgress(0, 0, false);
                                    builder.setContentText("Permission is not enabled to set live wallpaper directly, click here to set live wallpaper or give the permission");
                                    notificationManager.notify(17, builder.build());

                                }
                            }

                        }).start();
                    }

                    updatewallpaper(context, photoid, "wallpaper");
                }


            }

            @Override
            public void onError(@NotNull Download download, @NotNull Error error, @org.jetbrains.annotations.Nullable Throwable throwable) {

                builder.setContentTitle(context.getResources().getString(R.string.errorwhileadjuctingvideo))
                        .setProgress(0, 0, false);
                notificationManager.notify(17, builder.build());

                if (view != null) {
                    Snackbar.make(view, context.getResources().getString(R.string.errorwhileadjuctingvideo), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

            }

            @Override
            public void onProgress(@NotNull Download download, long l, long l1) {

                Log.d(TAG, "onProgress: progress " + l + "-" + l1);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    int oo = Math.toIntExact(l);

                    builder.setContentTitle(context.getResources().getString(R.string.livevideoadjusting))
                            .setProgress(100, oo, false);
                    notificationManager.notify(17, builder.build());
                }

              /*  int ProgressBar = l;

                builder.setContentTitle(context.getResources().getString(R.string.videoadjustmentcompleted))
                        .setProgress(100, Math.toIntExact(l), false);
                notificationManager.notify(158, builder.build());*/
            }

            @Override
            public void onPaused(@NotNull Download download) {

            }

            @Override
            public void onResumed(@NotNull Download download) {

            }

            @Override
            public void onCancelled(@NotNull Download download) {

            }

            @Override
            public void onRemoved(@NotNull Download download) {

            }

            @Override
            public void onDeleted(@NotNull Download download) {

            }
        };

        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(8)
                .build();

        Fetch fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch.addListener(fetchListener);
        String urls = imgpath;
        Random r = new Random();
        int random = r.nextInt(99999 - 11111) + 1111;
        String file = "/data/user/0/com.wallpo.android/cache/wallpaper/vid" + random + ".mp4";
        final Request requests = new Request(urls, file);
        requests.setPriority(Priority.HIGH);
        requests.setNetworkType(NetworkType.ALL);
        requests.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG");
        fetch.enqueue(requests, updatedRequest -> {
            //Request was successfully enqueued for download.

            if (view != null) {
                Snackbar.make(view, context.getResources().getString(R.string.adjustingthevideo), Snackbar.LENGTH_LONG).show();
            }
        }, error -> {
            //An error occurred enqueuing the request.
            builder.setContentTitle(context.getResources().getString(R.string.livevideoadjusting))
                    .setProgress(100, 0, false);
            notificationManager.notify(17, builder.build());

            if (view != null) {
                Snackbar.make(view, context.getResources().getString(R.string.errorwhileadjuctingvideo), Snackbar.LENGTH_LONG).show();
            }
        });
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String changelockscreen(Context context, View view, Bitmap bitmap, int photoid) {

        loadAds(context, "wallpapers");

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);

        String wallpapersettertype = sharedPreferences.getString("wallpapaersettertype", "");

        updatecode.analyticsFirebase(context, "setting_wallpaper_lockscreen", "setting_wallpaper_lockscreen");

        if (wallpapersettertype.equals("fit")) {

            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;

            Bitmap yourbitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(yourbitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                } else {

                    wallpaperManager.setBitmap(yourbitmap);
                }
                if (view != null) {
                    Snackbar.make(view, context.getResources().getString(R.string.lockscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            updatewallpaper(context, photoid, "lockscreen");

        } else if (wallpapersettertype.equals("crop")) {

            Intent i = new Intent(context, WallpaperSetActivity.class);
            Common.wallpapertype = "lockscreen";
            if (view != null) {
                Common.view = view;
            }
            Common.BITMAP = bitmap;
            context.startActivity(i);

            updatewallpaper(context, photoid, "lockscreen");

        } else if (wallpapersettertype.equals("options")) {
            if (view == null) {
                try {

                    WallpaperManager manager = WallpaperManager.getInstance(context.getApplicationContext());

                    try {
                        manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);

                        Toast.makeText(context, context.getResources().getString(R.string.lockscreenchangedsuccessfully), Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    updatewallpaper(context, photoid, "lockscreen");

                } catch (NullPointerException e) {
                    e.getMessage();
                }

            } else {

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.wallpaper_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.defualt) {

                        try {

                            WallpaperManager manager = WallpaperManager.getInstance(context.getApplicationContext());

                            try {
                                manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);

                                Snackbar.make(view, context.getResources().getString(R.string.lockscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();


                                Toast.makeText(context, context.getResources().getString(R.string.lockscreenchangedsuccessfully), Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            updatewallpaper(context, photoid, "lockscreen");

                        } catch (NullPointerException e) {
                            e.getMessage();
                        }

                    } else if (menuItem.getItemId() == R.id.fit) {

                        DisplayMetrics metrics = new DisplayMetrics();
                        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int height = metrics.heightPixels;
                        int width = metrics.widthPixels;

                        Bitmap yourbitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                        WallpaperManager manager = WallpaperManager.getInstance(context.getApplicationContext());

                        try {
                            manager.setBitmap(yourbitmap, null, true, WallpaperManager.FLAG_LOCK);

                            Snackbar.make(view, context.getResources().getString(R.string.lockscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();

                            Toast.makeText(context, context.getResources().getString(R.string.lockscreenchangedsuccessfully), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updatewallpaper(context, photoid, "wallpaper");

                    } else if (menuItem.getItemId() == R.id.crop) {

                        Intent i = new Intent(context, WallpaperSetActivity.class);
                        Common.wallpapertype = "lockScreen";
                        Common.view = view;
                        Common.ID_SELECTED = photoid;
                        Common.BITMAP = bitmap;
                        context.startActivity(i);

                        updatewallpaper(context, photoid, "lockscreen");

                    }

                    return true;
                });

                popupMenu.show();

            }
        } else {

            try {

                WallpaperManager manager = WallpaperManager.getInstance(context.getApplicationContext());

                try {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                    if (view != null) {
                        Snackbar.make(view, context.getResources().getString(R.string.lockscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();
                    }
                    Toast.makeText(context, context.getResources().getString(R.string.lockscreenchangedsuccessfully), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                updatewallpaper(context, photoid, "lockscreen");


            } catch (NullPointerException e) {
                e.getMessage();
            }

        }

        return null;
    }


    public static String checkPremium(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpopremium", Context.MODE_PRIVATE);

        PurchasesUpdatedListener purchaseUpdateListener = (billingResult, list) -> {

        };
        
        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    List<Purchase> billingClient1 = billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();

                    sharedPreferences.edit().putBoolean("userIsPremium", false).apply();

                    for (Purchase purchaseHistoryRecord : billingClient1) {
                        String time = String.valueOf(purchaseHistoryRecord.getPurchaseTime());
                        String productId = purchaseHistoryRecord.getSku();

                        if (!time.isEmpty()) {
                            sharedPreferences.edit().putBoolean("userIsPremium", true).apply();
                        }
                    }
                    
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
/*
        if (sharedPreferences.getString("checkpremium", "check").equals("check")) {

            int add = sharedPreferences.getInt("checkhowmanytime", 0) + 1;

            sharedPreferences.edit().putInt("checkhowmanytime", add).apply();

            if (sharedPreferences.getInt("checkhowmanytime", 0) > 1) {
                sharedPreferences.edit().putString("datecheck", "").apply();
            }

            if (!sharedPreferences.getString("datecheck", "").equals(getTimestampday())) {

                SharedPreferences sharedPreferencess = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
                String id = sharedPreferencess.getString("wallpouserid", "");

                if (!id.isEmpty()) {

                    String timezoneSaved = sharedPreferencess.getString("timezone", "");

                    if (timezoneSaved.isEmpty()) {
                        timezoneSaved = TimeZone.getDefault().getID();
                    }

                    final OkHttpClient clientTime = new OkHttpClient();

                    RequestBody bodyTime = new FormBody.Builder().add("timezone", timezoneSaved).build();

                    okhttp3.Request requestTime = new okhttp3.Request.Builder()
                            .url(URLS.gettime)
                            .post(bodyTime)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    clientTime.newCall(requestTime).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");
                            ((Activity) context).runOnUiThread(() -> {

                                String mainTime = data.replace(" ", "");

                                RequestBody bodyCheck = new FormBody.Builder().add("usersid", id).build();

                                okhttp3.Request requestCheck = new okhttp3.Request.Builder()
                                        .url(URLS.checksubuser)
                                        .post(bodyCheck)
                                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                        .build();

                                clientTime.newCall(requestCheck).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.d(TAG, "onFailure: error");
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final String data = response.body().string().replaceAll(",\\[]", "");
                                        ((Activity) context).runOnUiThread(() -> {

                                            if (data.trim().equals("[]")) {
                                                sharedPreferences.edit().putBoolean("userIsPremium", false).apply();
                                                sharedPreferences.edit().putString("checkpremium", "ischecked").apply();
                                                return;
                                            }

                                            try {
                                                JSONArray array = new JSONArray(data);

                                                for (int i = 0; i < array.length(); i++) {

                                                    JSONObject Object = array.getJSONObject(i);

                                                    String productId = Object.getString("couponused");
                                                    String orderId = Object.getString("orderid");
                                                    String purchaseTime = Object.getString("subdate");
                                                    String purchaseToken = Object.getString("purchasetoken");

                                                    sharedPreferences.edit().putString("orderId", orderId).apply();
                                                    sharedPreferences.edit().putString("productId", productId).apply();
                                                    sharedPreferences.edit().putString("purchaseTime", purchaseTime).apply();
                                                    sharedPreferences.edit().putString("purchaseToken", purchaseToken).apply();


                                                    if (mainTime == null) {
                                                        checkPremium(context);

                                                    } else {

                                                        sharedPreferences.edit().putString("datecheck", getTimestampday()).apply();

                                                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                                        Date date = format.parse(updatecode.getDate(Long.parseLong(purchaseTime), "dd-MM-yyyy"));
                                                        Calendar calendar = Calendar.getInstance();
                                                        calendar.setTime(date);
                                                        switch (productId) {
                                                            case "sixmonthssub":
                                                                calendar.add(Calendar.MONTH, 6);
                                                                break;
                                                            case "onemonthssubscription":
                                                                calendar.add(Calendar.MONTH, 1);
                                                                break;
                                                            case "yearlysub":
                                                                calendar.add(Calendar.MONTH, 12);
                                                                break;
                                                            default:
                                                                calendar.add(Calendar.MONTH, 0);
                                                                break;
                                                        }


                                                        Date newDate = calendar.getTime();
                                                        String sixmonthsdate = format.format(calendar.getTime());

                                                        SimpleDateFormat formats = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                                                        String datelast = formats.format(calendar.getTime());

                                                        switch (CheckDates(context, mainTime, sixmonthsdate)) {
                                                            case "before":
                                                            case "sameday":

                                                                sharedPreferences.edit().putBoolean("userIsPremium", true).apply();
                                                                sharedPreferences.edit().putString("checkpremium", "check").apply();
                                                                break;

                                                            case "expired":
                                                                sharedPreferences.edit().putBoolean("userIsPremium", false).apply();
                                                                sharedPreferences.edit().putString("checkpremium", "").apply();
                                                                break;
                                                            default:
                                                                sharedPreferences.edit().putBoolean("userIsPremium", false).apply();
                                                                sharedPreferences.edit().putString("checkpremium", "").apply();

                                                                break;
                                                        }
                                                    }
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }


                                        });
                                    }
                                });

                            });
                        }
                    });


                    sharedPreferences.edit().putInt("checkhowmanytime", 0).apply();

                }

            }

        }
*/
        return "";

    }

    public static String CheckDates(Context context, String startDate, String endDate) {

        SimpleDateFormat dfDate = new SimpleDateFormat("dd-MM-yyyy");

        String b = "";

        try {
            if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                b = "before";  // If start date is before end date.
            } else if (dfDate.parse(startDate).equals(dfDate.parse(endDate))) {
                b = "sameday";  // If two dates are equal.
            } else {
                b = "expired"; // If start date is after the end date.
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            checkPremium(context);
        }

        return b;
    }


    public static String editposts(Context context, int photoid) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.edit_post_layout);
        dialog.getBehavior().setPeekHeight(height - 400);


        RelativeLayout mainslay = dialog.findViewById(R.id.mainslay);
        mainslay.setVisibility(View.GONE);

        SpinKitView loadingbar = dialog.findViewById(R.id.loadingbar);
        loadingbar.setVisibility(View.VISIBLE);

        ImageView imageview = dialog.findViewById(R.id.imagepath);

        TextInputEditText captions = dialog.findViewById(R.id.captions);

        TextInputEditText link = dialog.findViewById(R.id.link);
        TextInputEditText seo = dialog.findViewById(R.id.seo);

        Common.categorydata.clear();
        Common.albumdata.clear();

        List<Category> categoryList = new ArrayList<>();
        List<Category> albumList = new ArrayList<>();
        RecyclerView categoryview = dialog.findViewById(R.id.categoryview);
        RecyclerView albumview = dialog.findViewById(R.id.albumview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        categoryview.setLayoutManager(linearLayoutManager);
        uploadcategoryadapter uploadcategoryadapter = new uploadcategoryadapter(context, categoryList);
        categoryview.setAdapter(uploadcategoryadapter);

        SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharepref.getString("wallpouserid", "");

        LinearLayoutManager linearLayoutManagers = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        albumview.setLayoutManager(linearLayoutManagers);
        uploadcategoryadapter uploadcategoryadapters = new uploadcategoryadapter(context, albumList);
        albumview.setAdapter(uploadcategoryadapters);

        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(photoid)).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.photoinfo)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity) context).runOnUiThread(() -> {
                    dialog.dismiss();
                    Snackbar.make(mainslay, context.getResources().getString(R.string.connectionerror), Snackbar.LENGTH_LONG).show();
                    loadingbar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");
                ((Activity) context).runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject Object = array.getJSONObject(i);

                            String photoid = Object.getString("photoid");
                            String caption = Object.getString("caption");
                            String categoryid = Object.getString("categoryid");
                            String albumid = Object.getString("albumid");
                            String datecreated = Object.getString("datecreated");
                            String dateshowed = Object.getString("dateshowed");
                            String links = Object.getString("link");
                            String userid = Object.getString("userid");
                            String imagepath = Object.getString("imagepath");
                            String likes = Object.getString("likes");
                            String trendingcount = Object.getString("trendingcount");
                            String SEO = Object.getString("SEO");

                            mainslay.setVisibility(View.VISIBLE);
                            loadingbar.setVisibility(View.GONE);
                            captions.setText(caption);
                            link.setText(links);
                            seo.setText(SEO);

                            String[] cat = categoryid.split(",");
                            for (String catdata : cat) {
                                String catid = catdata.trim();

                                Common.categorydata.add(catid);
                            }

                            String[] alb = albumid.split(",");
                            for (String albdata : alb) {
                                String albid = albdata.trim();

                                Common.albumdata.add(albid);
                            }

                            uploadcategoryadapter.notifyDataSetChanged();
                            uploadcategoryadapters.notifyDataSetChanged();


                            try {

                                Glide.with(context).load(imagepath).centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .skipMemoryCache(false).into(imageview);

                            } catch (IllegalArgumentException e) {
                                Log.e("searchadapter", "onBindViewHolder: ", e);
                            } catch (IllegalStateException e) {

                                Log.e("act", "onBindViewHolder: ", e);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });


        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });


        Button updatebtn = dialog.findViewById(R.id.updatebtn);
        updatebtn.setOnClickListener(view -> {

            if (Common.categorydata.size() < 1) {
                Toast.makeText(context, context.getResources().getString(R.string.selectonecategory), Toast.LENGTH_SHORT).show();
                return;
            }

            if (seo.getText().toString().length() < 2) {
                Toast.makeText(context, context.getResources().getString(R.string.addonetag), Toast.LENGTH_SHORT).show();
                return;
            }

            OkHttpClient clients = new OkHttpClient();

            RequestBody bodys = new FormBody.Builder().add("photoid", String.valueOf(photoid))
                    .add("caption", captions.getText().toString().trim().replace("'", "\\'"))
                    .add("link", link.getText().toString().trim().replace("'", "\\'"))
                    .add("categoryid", String.valueOf(Common.categorydata).replace("[", "").replace("]", ""))
                    .add("albumid", String.valueOf(Common.albumdata).replace("[", "").replace("]", ""))
                    .add("seo", seo.getText().toString().replace("'", "\\'")).add("timezone", tz).build();

            okhttp3.Request requests = new okhttp3.Request.Builder()
                    .url(URLS.updatephoto)
                    .post(bodys)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            clients.newCall(requests).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ((Activity)context).runOnUiThread(() -> {
                        Toast.makeText(context, context.getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    ((Activity)context).runOnUiThread(() -> {

                        SharedPreferences sharedusersdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);
                        sharedusersdata.edit().putString(userid + "postsprofile", "").apply();
                        if (context.toString().contains("ViewPostsActivity")){
                            Common.ALBUM_STATUS = "changed";
                        }

                        if (data.contains("successfully")){
                            dialog.dismiss();

                            new Handler().postDelayed(() -> Toast.makeText(context, context.getResources().getString(R.string.updatedsuccessfullyrefresh), Toast.LENGTH_SHORT).show(), 500);
                        }else {
                            Toast.makeText(context, context.getResources().getString(R.string.errorwhileuploadingtryafter), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });


        });

        OkHttpClient clientalb = new OkHttpClient();

        RequestBody postDataalb = new FormBody.Builder().add("usersid", userid).build();

        okhttp3.Request requestalb = new okhttp3.Request.Builder()
                .url(URLS.spinneralbum)
                .post(postDataalb)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientalb.newCall(requestalb).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity) context).runOnUiThread(() -> Toast.makeText(context, context.getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                ((Activity) context).runOnUiThread(() -> {

                    albumList.clear();
                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            String albumid = object.getString("albumid");
                            String albumname = object.getString("albumname");
                            String albumurl = object.getString("albumurl");

                            Category categoryItems = new Category(albumid, albumname, albumurl, "album");
                            albumList.add(categoryItems);

                            uploadcategoryadapters.notifyDataSetChanged();
                        }


                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }

                });
            }
        });


        OkHttpClient clientct = new OkHttpClient();

        okhttp3.Request requestct = new okhttp3.Request.Builder()
                .url(URLS.categoryview)
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientct.newCall(requestct).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Connection Error..", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                ((Activity) context).runOnUiThread(() -> {
                    categoryList.clear();
                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            String categoryid = object.getString("categoryid");
                            String name = object.getString("name");
                            String imagelink = object.getString("imagelink");

                            Category categoryItem = new Category(categoryid, name, imagelink, "category");
                            categoryList.add(categoryItem);
                        }

                        uploadcategoryadapter.notifyDataSetChanged();

                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }

                });
            }
        });

        dialog.show();

        return null;
    }

    public static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getTimestampday() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getTimestampfull() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static void updatesub(String url, final String id, final String otherid) {

        final String tz = TimeZone.getDefault().getID();

        OkHttpClient client = new OkHttpClient();

        if (!id.isEmpty() && !otherid.isEmpty()) {

            RequestBody postData = new FormBody.Builder().add("usersid", id)
                    .add("otherid", otherid).add("timezone", tz)
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: ", e);
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    Log.i(TAG, "onResponse: " + data);

                }
            });
        }
    }

    public static void loadAds(Context context, String type) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpopremium", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("userIsPremium", false)) {
            return;
        }

        int adsmainscreen = sharedPreferences.getInt("adsmainscreenload", 0);

        if (type.equals("popup")) {
            if (adsmainscreen == 0) {
                Log.d(TAG, "loadAds: popup 0");
                sharedPreferences.edit().putInt("adsmainscreenload", 1).apply();
                return;
            }
            if (adsmainscreen == 1) {
                Log.d(TAG, "loadAds: popup 1");
                sharedPreferences.edit().putInt("adsmainscreenload", 2).apply();
                gads(context, type);

            }
            if (adsmainscreen >= 2) {
                Log.d(TAG, "loadAds: popup 2");
                sharedPreferences.edit().putInt("adsmainscreenload", 0).apply();
                return;
            }

        }

        int wallpapersads = sharedPreferences.getInt("wallpapersads", 0);

        if (type.equals("wallpapers")) {
            if (wallpapersads == 0) {
                Log.d(TAG, "loadAds: popup 0");
                sharedPreferences.edit().putInt("wallpapersads", 1).apply();
                return;
            }
            if (wallpapersads >= 1) {
                Log.d(TAG, "loadAds: popup 1");
                sharedPreferences.edit().putInt("wallpapersads", 0).apply();
                gads(context, type);
            }


        }
        int profileads = sharedPreferences.getInt("profileads", 0);

        if (type.equals("profile")) {
            if (profileads == 0) {
                Log.d(TAG, "loadAds: profile 1");
                sharedPreferences.edit().putInt("profileads", 1).apply();
                return;
            }
            if (profileads >= 1) {
                Log.d(TAG, "loadAds: profile 2");
                sharedPreferences.edit().putInt("profileads", 0).apply();
                gads(context, type);
            }
        }

    }

    public static void wallpoads(Context context, String type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WallpoAdsList", Context.MODE_PRIVATE);
//SELECT photos.photoid, photos.caption, photos.categoryid, photos.albumid, photos.datecreated, photos.dateshowed, photos.link, photos.userid, photos.imagepath, photos.likes, photos.trendingcount, ads.uid, ads.extradata FROM ' +
//			"photos INNER JOIN ads ON photos.photoid = ads.postid WHERE ads.adstype = 'normal' AND ads.points > 0 AND ads.posttype = 'photos' ORDER BY RAND(), ads.points DESC LIMIT 1;

        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.normaladslist)
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                sharedPreferences.edit().putString("adsdata", data).apply();

                try {
                    JSONArray array = new JSONArray(data);

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        String link = object.getString("link");

                        if(!link.isEmpty()){

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public static void gads(Context context, String type) {
        MobileAds.initialize(context, initializationStatus -> {
        });

        InterstitialAd mInterstitial = new InterstitialAd(context);
        mInterstitial.setAdUnitId("ca-app-pub-2941808068005217/4872724597");
        mInterstitial.loadAd(new AdRequest.Builder().build());
        mInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Intent clickintent = new Intent(context, PremiumActivity1.class);

                clickintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(clickintent);
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("ADS NOTIFICATION", "Ads Upload",
                            NotificationManager.IMPORTANCE_HIGH);

                    NotificationManager manager = context.getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ADS NOTIFICATION");
                builder.setContentTitle(context.getResources().getString(R.string.adsshowing));
                builder.setContentText(context.getResources().getString(R.string.adsshowingtitle));
                builder.setContentIntent(pendingIntent);
                builder.setSmallIcon(R.mipmap.logo);
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(147, builder.build());

                updatecode.analyticsFirebase(context, "loaded_google_ads", "loaded_google_ads");

                if (mInterstitial.isLoaded()) {

                    updatecode.analyticsFirebase(context, "showed_google_ads", "showed_google_ads");
                    mInterstitial.show();
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                loadAds(context, type);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("ADS NOTIFICATION", "Ads Upload",
                            NotificationManager.IMPORTANCE_HIGH);

                    NotificationManager manager = context.getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ADS NOTIFICATION");
                builder.setContentTitle(context.getResources().getString(R.string.adsshowed));
                builder.setContentText(context.getResources().getString(R.string.adsshowedtitle));
                builder.setSmallIcon(R.mipmap.logo);
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(147, builder.build());
            }

        });
    }


    public static String checkDate(Context context, String firstdate, String secdate) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();

        long todayInMillis = Long.parseLong(firstdate);

        int year = Integer.parseInt(secdate.substring(0, 4));
        int month = Integer.parseInt(secdate.substring(5, 7).replace(" ", ""));
        int dayOfMonth = Integer.parseInt(secdate.substring(8, 10).replace(" ", ""));;

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Date dateSpecified = c.getTime();
        if (dateSpecified.before(today)) {
            return "Date specified [" + dateSpecified + "] is before today [" + today + "]";
        } else {
            return "Date specified [" + dateSpecified + "] is NOT before today [" + today + "]";
        }
    }


    public static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    public static String analyticsFirebase(Context context, String name, String value) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        bundle.putString(name, value);
        mFirebaseAnalytics.logEvent(name, bundle);

        return name;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        //dd/MM/yyyy hh:mm:ss.SSS
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
