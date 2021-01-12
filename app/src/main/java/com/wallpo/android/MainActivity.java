package com.wallpo.android;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.transition.TransitionManager;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wallpo.android.fragment.ExploreFragment;
import com.wallpo.android.fragment.HomeFragment;
import com.wallpo.android.fragment.MapsFragment;
import com.wallpo.android.fragment.MonetizeFragment;
import com.wallpo.android.fragment.NotificationFragment;
import com.wallpo.android.fragment.ProfileFragment;
import com.wallpo.android.fragment.SearchFragment;
import com.wallpo.android.fragment.TrendingFragment;
import com.wallpo.android.service.HourlyNoticeService;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.customListeners;
import com.wallpo.android.utils.updatecode;
import com.wallpo.android.videoGallery.VideoFolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.SSLContext;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.wallpo.android.utils.Cache.deleteCache;
import static com.wallpo.android.utils.updatecode.checkPremium;
import static com.wallpo.android.utils.updatecode.updateFCM;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity {

    Context context = this;
    int UPDATE_REQUEST_CODE = 110;
    String id;
    RelativeLayout mainlayout;
    SharedPreferences sharedPreferences;
    ImageView fblogin, googlelogin, wallpologin;
    CardView loginlayout, loginin;
    FrameLayout framelayout;
    public static TextView trending;
    public static TextView home;
    public static TextView profile;
    public static TextView explore;
    public static TextView monetize;
    public static TextView search;
    public static TextView notification;
    public static TextView maps;
    public static final Fragment homeFragment = new HomeFragment();
    final Fragment trendingFragment = new TrendingFragment();
    public static final Fragment exploreFragment = new ExploreFragment();
    final Fragment notificationFragment = new NotificationFragment();
    final Fragment monetizeFragment = new MonetizeFragment();
    public static final Fragment searchFragment = new SearchFragment();
    final Fragment profileFragment = new ProfileFragment();
    public static final Fragment mapsFragment = new MapsFragment();
    public final FragmentManager fm = getSupportFragmentManager();
    public static Fragment active = homeFragment;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainlayout = findViewById(R.id.mainlayout);
        loginlayout = findViewById(R.id.loginlayout);
        loginlayout.setVisibility(View.GONE);
        initializeSSLContext(context);
        checkPremium(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Log.d("MainActivity", "onCreate: ratio " + height + " - " + width);
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


                Document document = Jsoup.parse(data);
                Elements elements = document.select("meta");
                Log.d(TAG, "onResponse: itt" + elements);
            }
        });

        SharedPreferences sharedPreferencess = context.getSharedPreferences("wallpoonetime", Context.MODE_PRIVATE);

        if(sharedPreferencess.getBoolean("wallpotutorials", true)){
            new Handler().postDelayed(() -> {

                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.howtouse_layout);
                dialog.show();

                sharedPreferencess.edit().putBoolean("wallpotutorials", false).apply();

            }, 25000);

        }

        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> Log.d("TAG", "onComplete: subscribed"));

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("wallpouserid", "");

        if (sharedPreferences.getBoolean("notificationswitch", true)) {
            HourlyNoticeService hourlyNoticeService = new HourlyNoticeService(this);
            hourlyNoticeService.cancelHourlyService();

            new Handler().postDelayed(hourlyNoticeService::setHourlyService, 1000);
        }else {
            HourlyNoticeService hourlyNoticeService = new HourlyNoticeService(this);
            hourlyNoticeService.cancelHourlyService();

        }
        sharedPreferences.edit().putString("mobileheight", String.valueOf(height)).apply();
        sharedPreferences.edit().putString("mobilewidth", String.valueOf(width)).apply();

        if (!sharedPreferences.getString("updatetime", "").equals(updatecode.getTimestampday())) {

            new Handler().postDelayed(this::checkAppUpdate, 5000);
        }

        explore = findViewById(R.id.explore);

        trending = findViewById(R.id.trending);

        profile =findViewById(R.id.profile);

        monetize = findViewById(R.id.monetize);

        search = findViewById(R.id.search);

        notification = findViewById(R.id.notification);

        maps = findViewById(R.id.maps);

        home = findViewById(R.id.home);

        framelayout = findViewById(R.id.framlayout);

        fm.beginTransaction(). add(R.id.framlayout, homeFragment, "home").commit();

        MainActivity.home.setText(getResources().

                        getString(R.string.Home));
        MainActivity.trending.setText(getResources().

                        getString(R.string.trending));
        MainActivity.profile.setText(getResources().

                        getString(R.string.profile));
        MainActivity.explore.setText(getResources().

                        getString(R.string.explore));
        MainActivity.monetize.setText(getResources().

                        getString(R.string.monetization));
        MainActivity.search.setText(getResources().

                        getString(R.string.search));
        MainActivity.notification.setText(getResources().

                        getString(R.string.notification));
        MainActivity.notification.setText(getResources().

                        getString(R.string.maps));
        ExploreFragment.checkuser = false;

        try {
            customListeners.playerlistener.onObjectReady("pause");
        } catch (
                NullPointerException e) {
            Log.d("TAG", "onBindViewHolder: ");
        }

//        context.startService(new Intent(context, ChatMessageService.class));

        home.setOnClickListener(view ->{

            ExploreFragment.checkuser = false;
            if (homeFragment.getTag() == null) {

                fm.beginTransaction().add(R.id.framlayout, homeFragment, "home").commit();
            } else {
                fm.beginTransaction().hide(active).show(homeFragment).commit();
            }

            active = homeFragment;

            MainActivity.home.setText(getResources().getString(R.string.Home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));
            MainActivity.maps.setText(getResources().getString(R.string.maps));

            try {
                customListeners.playerlistener.onObjectReady("pause");
            } catch (NullPointerException e) {
                Log.d("TAG", "onBindViewHolder: ");
            }

        });

        trending.setOnClickListener(view ->{

            ExploreFragment.checkuser = false;
            if (trendingFragment.getTag() == null) {

                fm.beginTransaction().add(R.id.framlayout, trendingFragment, "trending").commit();
            } else {
                fm.beginTransaction().hide(active).show(trendingFragment).commit();
            }
            active = trendingFragment;

            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.Trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));
            MainActivity.maps.setText(getResources().getString(R.string.maps));

            try {
                customListeners.playerlistener.onObjectReady("pause");
            } catch (NullPointerException e) {
                Log.d("TAG", "onBindViewHolder: ");
            }
        });

        profile.setOnClickListener(view ->{

            ExploreFragment.checkuser = false;
            if (profileFragment.getTag() == null) {

                fm.beginTransaction().add(R.id.framlayout, profileFragment, "profile").commit();
            } else {

                fm.beginTransaction().hide(active).show(profileFragment).commit();
            }
            active = profileFragment;
            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.Profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));
            MainActivity.maps.setText(getResources().getString(R.string.maps));

            try {
                customListeners.playerlistener.onObjectReady("pause");
            } catch (NullPointerException e) {
                Log.d("TAG", "onBindViewHolder: ");
            }
        });

        maps.setOnClickListener(view ->{


            ExploreFragment.checkuser = false;
            if (mapsFragment.getTag() == null) {

                fm.beginTransaction().add(R.id.framlayout, mapsFragment, "maps").commit();
            } else {

                fm.beginTransaction().hide(active).show(mapsFragment).commit();
            }
            active = mapsFragment;
            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));
            MainActivity.maps.setText(getResources().getString(R.string.Maps));

            try {
                customListeners.playerlistener.onObjectReady("pause");
            } catch (NullPointerException e) {
                Log.d("TAG", "onBindViewHolder: ");
            }

        });

        customListeners listeners = new customListeners();


        listeners.exploreFragmentListeners(title ->{
            try {
                customListeners.playerlistener.onObjectReady("pause");
            } catch (NullPointerException e) {
                Log.d("TAG", "onBindViewHolder: ");
            }
            if (title.equals("explore")) {

                ExploreFragment.checkuser = true;
                if (exploreFragment.getTag() == null) {

                    fm.beginTransaction().add(R.id.framlayout, exploreFragment, "explore").commit();
                } else {

                    fm.beginTransaction().hide(active).show(exploreFragment).commit();
                }
                active = exploreFragment;

                MainActivity.home.setText(getResources().getString(R.string.home));
                MainActivity.trending.setText(getResources().getString(R.string.trending));
                MainActivity.profile.setText(getResources().getString(R.string.profile));
                MainActivity.explore.setText(getResources().getString(R.string.Explore));
                MainActivity.monetize.setText(getResources().getString(R.string.monetization));
                MainActivity.search.setText(getResources().getString(R.string.search));
                MainActivity.notification.setText(getResources().getString(R.string.notification));
                MainActivity.maps.setText(getResources().getString(R.string.maps));

            } else if (title.equals("search")) {

                ExploreFragment.checkuser = false;

                if (searchFragment.getTag() == null) {

                    fm.beginTransaction().add(R.id.framlayout, searchFragment, "search").commit();
                } else {

                    fm.beginTransaction().hide(active).show(searchFragment).commit();
                }

                active = searchFragment;

                MainActivity.home.setText(getResources().getString(R.string.home));
                MainActivity.trending.setText(getResources().getString(R.string.trending));
                MainActivity.profile.setText(getResources().getString(R.string.profile));
                MainActivity.explore.setText(getResources().getString(R.string.explore));
                MainActivity.monetize.setText(getResources().getString(R.string.monetization));
                MainActivity.search.setText(getResources().getString(R.string.Search));
                MainActivity.notification.setText(getResources().getString(R.string.notification));
                MainActivity.maps.setText(getResources().getString(R.string.maps));
            }
        });

        explore.setOnClickListener(view -> {

            ExploreFragment.checkuser = true;
            if (exploreFragment.getTag() == null) {

                fm.beginTransaction().add(R.id.framlayout, exploreFragment, "explore").commit();
            } else {

                fm.beginTransaction().hide(active).show(exploreFragment).commit();
            }
            active = exploreFragment;

            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.Explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));
            MainActivity.maps.setText(getResources().getString(R.string.maps));
            try {
                customListeners.playerlistener.onObjectReady("pause");
            } catch (NullPointerException e) {
                Log.d("TAG", "onBindViewHolder: ");
            }

        });

        monetize.setOnClickListener(view ->{

            ExploreFragment.checkuser = false;

            if (monetizeFragment.getTag() == null) {

                fm.beginTransaction().add(R.id.framlayout, monetizeFragment, "monetize").commit();
            } else {

                fm.beginTransaction().hide(active).show(monetizeFragment).commit();
            }
            active = monetizeFragment;

            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.Monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));
            MainActivity.maps.setText(getResources().getString(R.string.maps));
            try {
                customListeners.playerlistener.onObjectReady("pause");
            } catch (NullPointerException e) {
                Log.d("TAG", "onBindViewHolder: ");
            }

        });

        search.setOnClickListener(view ->{

            ExploreFragment.checkuser = false;

            if (searchFragment.getTag() == null) {

                fm.beginTransaction().add(R.id.framlayout, searchFragment, "search").commit();
            } else {

                fm.beginTransaction().hide(active).show(searchFragment).commit();
            }

            active = searchFragment;

            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.Search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));
            MainActivity.maps.setText(getResources().getString(R.string.maps));
            try {
                customListeners.playerlistener.onObjectReady("pause");
            } catch (NullPointerException e) {
                Log.d("TAG", "onBindViewHolder: ");
            }
        });

        notification.setOnClickListener(view ->{

            ExploreFragment.checkuser = false;
            if (notificationFragment.getTag() == null) {

                fm.beginTransaction().add(R.id.framlayout, notificationFragment, "notification").commit();

            } else {

                fm.beginTransaction().hide(active).show(notificationFragment).commit();
            }

            active = notificationFragment;

            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.Notification));
            MainActivity.maps.setText(getResources().getString(R.string.maps));
            try {
                customListeners.playerlistener.onObjectReady("pause");
            } catch (NullPointerException e) {
                Log.d("TAG", "onBindViewHolder: ");
            }
        });


        if (id != null) {
            if (id.isEmpty()) {

                new Handler().postDelayed(() -> {
                    loginlayout.setVisibility(View.VISIBLE);

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottomeffect);
                    loginlayout.setAnimation(animation);
                }, 3000);


            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(mainlayout);
                }
                loginlayout.setVisibility(View.GONE);

            }

        } else {

            new Handler().postDelayed(() -> {
                loginlayout.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottomeffect);
                loginlayout.setAnimation(animation);
            }, 3000);


        }

        if (id != null) {
            if (!id.isEmpty()) {
                if (!sharedPreferences.getString("lastseenactive", "").equals(getTimestamp())) {
                    updateFCM(context);
                }

            }
        }

        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(() -> initializeCache(), delay);

        //layout work
        fblogin = findViewById(R.id.fblogin);
        fblogin.setOnClickListener(view -> {

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.imageeffect);
            fblogin.startAnimation(animation);

            Common.logintype = "fblogin";
            Intent i = new Intent(context, LoginActivity.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(fblogin, "facebooklogo");
                pairs[1] = new Pair<View, String>(googlelogin, "googlelogo");
                pairs[2] = new Pair<View, String>(wallpologin, "wallpologo");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(i, options.toBundle());
            } else {
                startActivity(i);
            }
        });


        googlelogin =findViewById(R.id.googlelogin);
        googlelogin.setOnClickListener(view ->{

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.imageeffect);
            googlelogin.startAnimation(animation);


            Common.logintype = "googlelogin";
            Intent i = new Intent(context, LoginActivity.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(fblogin, "facebooklogo");
                pairs[1] = new Pair<View, String>(googlelogin, "googlelogo");
                pairs[2] = new Pair<View, String>(wallpologin, "wallpologo");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(i, options.toBundle());
            } else {
                startActivity(i);
            }
        });

        wallpologin =findViewById(R.id.wallpologin);
        wallpologin.setOnClickListener(view ->{

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.imageeffect);
            wallpologin.startAnimation(animation);

            Common.logintype = "wallpologin";

            Intent i = new Intent(context, LoginActivity.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(fblogin, "facebooklogo");
                pairs[1] = new Pair<View, String>(googlelogin, "googlelogo");
                pairs[2] = new Pair<View, String>(wallpologin, "wallpologo");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(i, options.toBundle());
            } else {
                startActivity(i);
            }

        });

        loginin = findViewById(R.id.loginin);
        loginin.setOnClickListener(view ->{
            Intent i = new Intent(context, LoginActivity.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(fblogin, "facebooklogo");
                pairs[1] = new Pair<View, String>(googlelogin, "googlelogo");
                pairs[2] = new Pair<View, String>(wallpologin, "wallpologo");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(i, options.toBundle());
            } else {
                startActivity(i);
            }

        });


        if (id == null) {
            profile.setVisibility(View.GONE);

        } else if (id.isEmpty()) {
            profile.setVisibility(View.GONE);
            findViewById(R.id.profileempty).setVisibility(View.GONE);
        }

        if (sharedPreferences.getString("usercontinent", "").isEmpty()) {
            getlocation(context);
        }

        if (!sharedPreferences.getString("ipaddtime", "").equals(getTimestamp())) {
            getlocation(context);
        }

    }

    private void checkAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            MainActivity.this,
                            UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UPDATE_REQUEST_CODE){
            sharedPreferences.edit().putString("updatetime", updatecode.getTimestampday()).apply();
            Log.d("TAG", "onActivityResult: Update");
            if (resultCode != RESULT_OK) {
                Log.d("","Update flow failed! Result code: " + resultCode);
            }

        }
    }

    public static void getlocation(Context context) {

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(runnable -> {
                    if (!runnable.isSuccessful()) {
                        Log.w("MainActivity", "getInstanceId failed", runnable.getException());

                        return;
                    }
                    final String token = runnable.getResult().getToken();


                    sharedPreferences.edit().putString("fcmtoken", token).apply();

                });

        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.getipaddress)
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");
                ((Activity)context).runOnUiThread(() -> {

                    Log.d("TAG", "onResponse: ipadd " + data.trim());
                    sharedPreferences.edit().putString("useripaddress", data.trim()).apply();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.getipdetail + data.trim() + "?fields=status,message,continent,continentCode,country,countryCode,region,regionName,city,district,zip,lat,lon,timezone,offset,currency,isp,org,as,asname,reverse,mobile,proxy,hosting,query")
                            .get()
                            .addHeader("User-Agent", "java-ipapi-client")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");
                            ((Activity)context).runOnUiThread(() -> {

                                if (data.contains("status\":\"success")) {
                                    String dataOutput = "[" + data + "]";
                                    try {
                                        JSONArray array = new JSONArray(dataOutput);

                                        for (int i = 0; i < array.length(); i++) {

                                            JSONObject Object = array.getJSONObject(i);

                                            String status = Object.getString("status");
                                            String continent = Object.getString("continent");
                                            String continentCode = Object.getString("continentCode");
                                            String country = Object.getString("country");
                                            String countryCode = Object.getString("countryCode");

                                            String region = Object.getString("region");
                                            String regionName = Object.getString("regionName");
                                            String city = Object.getString("city");
                                            String district = Object.getString("district");
                                            String zip = Object.getString("zip");

                                            String lat = Object.getString("lat");
                                            String lon = Object.getString("lon");
                                            String timezone = Object.getString("timezone");
                                            String offset = Object.getString("offset");
                                            String currency = Object.getString("currency");

                                            String isp = Object.getString("isp");
                                            String org = Object.getString("org");
                                            String as = Object.getString("as");
                                            String asname = Object.getString("asname");
                                            String reverse = Object.getString("reverse");

                                            String mobile = Object.getString("mobile");
                                            String proxy = Object.getString("proxy");
                                            String hosting = Object.getString("hosting");
                                            String query = Object.getString("query");

                                            sharedPreferences.edit().putString("usercity", city).apply();
                                            sharedPreferences.edit().putString("userregion", regionName).apply();
                                            sharedPreferences.edit().putString("usercontinent", continent).apply();
                                            sharedPreferences.edit().putString("usercountry", country).apply();
                                            sharedPreferences.edit().putString("timezone", timezone).apply();

                                            FirebaseMessaging.getInstance().subscribeToTopic(continent)
                                                    .addOnCompleteListener(task -> Log.d("TAG", "onComplete: subscribed"));

                                            FirebaseMessaging.getInstance().subscribeToTopic(country)
                                                    .addOnCompleteListener(task -> Log.d("TAG", "onComplete: subscribed"));
                                        }

                                        sharedPreferences.edit().putString("ipaddtime", getTimestamp()).apply();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                });
            }
        });


    }


    private void deleteuserprofiledata() {

        final SharedPreferences shareprefusers = context.getApplicationContext().getSharedPreferences("wallpouserdata", 0);
        shareprefusers.edit().clear().apply();
    }

    public static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        size += getDirSize(this.getExternalCacheDir());

        if (size > 300000000) {
            deleteCache(context);
            Log.d("MainActivity", "initializeCache: cshchecleared");
        } else {

            Log.d("MainActivity", "initializeCache: working");
        }

    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press back again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    public static void initializeSSLContext(Context mContext) {
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            customListeners.playerlistener.onObjectReady("pause");
        } catch (NullPointerException e) {
            Log.d("TAG", "onBindViewHolder: ");
        }
    }
}
