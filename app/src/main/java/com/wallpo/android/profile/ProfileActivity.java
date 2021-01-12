package com.wallpo.android.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bachors.wordtospan.WordToSpan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.wallpo.android.LoginActivity;
import com.wallpo.android.R;
import com.wallpo.android.activity.ChatActivity;
import com.wallpo.android.activity.EditProfileActivity;
import com.wallpo.android.activity.HashtagActivity;
import com.wallpo.android.activity.MessageActivity;
import com.wallpo.android.activity.SubscribeAcivity;
import com.wallpo.android.adapter.addstoriesadapter;
import com.wallpo.android.adapter.storiesadapter;
import com.wallpo.android.explorefragment.albumsadapter;
import com.wallpo.android.explorefragment.risingartistsadapter;
import com.wallpo.android.getset.Albums;
import com.wallpo.android.getset.Category;
import com.wallpo.android.getset.Photos;
import com.wallpo.android.getset.Stories;
import com.wallpo.android.uploads.addalbumadapter;
import com.wallpo.android.uploads.addpostsadapter;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;
import static com.wallpo.android.utils.updatecode.getusernameid;

public class ProfileActivity extends AppCompatActivity {

    public static RelativeLayout toplay, mainlay, subscriberslay;
    public static SharedPreferences sharedPreferences;
    CardView menu, message, share;
    public static ImageView menuimg, verified, profilepic, backimg;
    public static String userid;
    public static TextView name, username, category, websites, bio, subscribed, subscribers, editprofile, subscribebt, subscribedbt, noalbums, nofirewall;
    public static TextView noposts, trendsposts, poststxt, firewalltxt;
    Context context = this;
    RecyclerView trendingposts, posts, albums, firewall;
    private List<Photos> trendslist = new ArrayList<>();
    com.wallpo.android.explorefragment.risingartistsadapter trendingadapter;
    private List<Photos> postslist = new ArrayList<>();
    private List<Photos> addpostslist = new ArrayList<>();
    com.wallpo.android.explorefragment.risingartistsadapter poststsadapter;
    private List<Albums> albumslist = new ArrayList<>();
    private List<Category> addalbumslist = new ArrayList<>();
    com.wallpo.android.explorefragment.albumsadapter albumsadapter;
    private List<Stories> firewallslist = new ArrayList<>();
    private List<Stories> addfirewallslist = new ArrayList<>();
    com.wallpo.android.adapter.storiesadapter storiesadapter;
    SpinKitView trendingloading;
    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = 0;
    private int previousTotal;
    private boolean load = true;
    private int totalItemCountst, firstVisibleItemst, visibleItemContst;
    private int pagest = 1;
    private int loadnost = 0;
    private int previousTotalst;
    private boolean loadst = true;
    private int totalItemCountal, firstVisibleItemal, visibleItemContal;
    private int pageal = 1;
    private int loadnoal = 0;
    private int previousTotalal;
    private boolean loadal = true;
    private LinearLayoutManager linearLayoutalbum, linearLayoutposts, linearLayoutfirewall;
    CardView subscribedlay;

    public static Boolean chattime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        setContentView(R.layout.activity_profile);


        updatecode.loadAds(this, "profile");

     /*   new Handler().postDelayed(() -> {
            updatecode.loadAds(this);
        }, 15000);
*/
        sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        userid = sharedPreferences.getString("wallpouserid", "");

        toplay = findViewById(R.id.toplay);
        menu = findViewById(R.id.menu);
        message = findViewById(R.id.message);
        message.setVisibility(View.GONE);
        share = findViewById(R.id.share);
        share.setVisibility(View.GONE);
        mainlay = findViewById(R.id.mainlay);
        menuimg = findViewById(R.id.menuimg);
        verified = findViewById(R.id.verified);
        verified.setVisibility(View.GONE);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        profilepic = findViewById(R.id.profilepic);
        backimg = findViewById(R.id.backimg);
        category = findViewById(R.id.category);
        websites = findViewById(R.id.websites);
        bio = findViewById(R.id.bio);
        subscribed = findViewById(R.id.subscribed);
        subscribers = findViewById(R.id.subscribers);
        subscriberslay = findViewById(R.id.subscriberslay);
        editprofile = findViewById(R.id.editprofile);
        editprofile.setVisibility(View.GONE);
        subscribebt = findViewById(R.id.subscribebt);
        subscribebt.setVisibility(View.GONE);
        subscribedbt = findViewById(R.id.subscribedbt);
        subscribedbt.setVisibility(View.GONE);
        firewalltxt = findViewById(R.id.firewalltxt);
        trendingposts = findViewById(R.id.trendingposts);
        trendingloading = findViewById(R.id.trendingloading);
        noposts = findViewById(R.id.noposts);
        noposts.setVisibility(View.GONE);
        trendsposts = findViewById(R.id.trendsposts);
        poststxt = findViewById(R.id.poststxt);
        posts = findViewById(R.id.posts);
        albums = findViewById(R.id.albums);
        noalbums = findViewById(R.id.noalbums);
        noalbums.setVisibility(View.GONE);
        nofirewall = findViewById(R.id.nofirewall);
        nofirewall.setVisibility(View.GONE);
        firewall = findViewById(R.id.firewall);
        subscribedlay = findViewById(R.id.subscribedlay);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        toplay.getLayoutParams().height = height / 2 / 2 * 3 - 130;
        toplay.requestLayout();

        if (!userid.isEmpty()) {

            if (userid.equals(sharedPreferences.getString("useridprofile", ""))) {

                linearLayoutposts = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                posts.setLayoutManager(linearLayoutposts);
                addpostsadapter addpostsadapter = new addpostsadapter(context, addpostslist);
                poststsadapter = new risingartistsadapter(context, postslist);
                ConcatAdapter concatAdapter = new ConcatAdapter(addpostsadapter, poststsadapter);
                posts.setAdapter(concatAdapter);

                Photos photos = new Photos("caption", "datecreated", "imagepath", 0,
                        "userid", "albumid", "link", "categoryid", "dateshowed", 0, "likes", "type");


                addpostslist.add(photos);

                addpostsadapter.notifyDataSetChanged();

            } else {
                linearLayoutposts = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                posts.setLayoutManager(linearLayoutposts);
                poststsadapter = new risingartistsadapter(context, postslist);
                posts.setAdapter(poststsadapter);

            }

        } else {
            linearLayoutposts = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            posts.setLayoutManager(linearLayoutposts);
            poststsadapter = new risingartistsadapter(context, postslist);
            posts.setAdapter(poststsadapter);

        }

        if (!userid.isEmpty()) {

            if (userid.equals(sharedPreferences.getString("useridprofile", ""))) {

                linearLayoutalbum = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                albums.setLayoutManager(linearLayoutalbum);

                Category category = new Category("categoryid", "name", "imagelink", "");
                addalbumslist.add(category);

                addalbumadapter addalbumadapter = new addalbumadapter(context, addalbumslist);
                albumsadapter = new albumsadapter(context, albumslist);
                ConcatAdapter concatAdapter = new ConcatAdapter(addalbumadapter, albumsadapter);
                albums.setAdapter(concatAdapter);

            } else {

                linearLayoutalbum = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                albums.setLayoutManager(linearLayoutalbum);
                albumsadapter = new albumsadapter(context, albumslist);
                albums.setAdapter(albumsadapter);

            }

        } else {

            linearLayoutalbum = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            albums.setLayoutManager(linearLayoutalbum);
            albumsadapter = new albumsadapter(context, albumslist);
            albums.setAdapter(albumsadapter);

        }

        if (!userid.isEmpty()) {

            if (userid.equals(sharedPreferences.getString("useridprofile", ""))) {


                linearLayoutfirewall = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                firewall.setLayoutManager(linearLayoutfirewall);

                final addstoriesadapter addadapter = new addstoriesadapter(context, addfirewallslist);
                storiesadapter = new storiesadapter(context, firewallslist);

                ConcatAdapter concatAdapter = new ConcatAdapter(addadapter, storiesadapter);

                firewall.setAdapter(concatAdapter);

                Stories stories = new Stories(0, 0, "u", "l", "c",
                        "type", "dateshowed", "datecreated");

                addfirewallslist.add(stories);

                addadapter.notifyDataSetChanged();

            } else {


                linearLayoutfirewall = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                firewall.setLayoutManager(linearLayoutfirewall);
                storiesadapter = new storiesadapter(context, firewallslist);
                firewall.setAdapter(storiesadapter);

            }

        } else {
            linearLayoutfirewall = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            firewall.setLayoutManager(linearLayoutfirewall);
            storiesadapter = new storiesadapter(context, firewallslist);
            firewall.setAdapter(storiesadapter);

        }


        menu.setOnClickListener(v -> {
            if (message.getVisibility() == View.GONE) {

                TransitionManager.beginDelayedTransition(mainlay);

                share.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);

                menuimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel));

            } else {

                TransitionManager.beginDelayedTransition(mainlay);

                share.setVisibility(View.GONE);
                message.setVisibility(View.GONE);

                menuimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_menu));

            }
        });

        subscribedlay.setOnClickListener(v -> {
            startActivity(new Intent(context, SubscribeAcivity.class));
        });

        String usernametxt = sharedPreferences.getString("usernameprofile", "");
        String useridprofile = sharedPreferences.getString("useridprofile", "");
        String displaynameprofile = sharedPreferences.getString("displaynameprofile", "");

        share.setOnClickListener(v -> {

            Common.SHARETYPE = "user";
            if (usernametxt.isEmpty()) {
                //  int ids = Integer.getInteger(useridprofile) * 147 * 14;
                Common.MESSAGE_DATA = "B1P" + useridprofile + "R_S";
            } else {
                Common.MESSAGE_DATA = usernametxt;
            }
            Common.USER_ID = useridprofile;
            updatecode.shareintent(context, "user/" + Common.MESSAGE_DATA);

        });

        message.setOnClickListener(v -> {

            if (!userid.isEmpty()) {
                if (userid.equals(useridprofile)) {
                    startActivity(new Intent(context, MessageActivity.class));
                } else {
                    MessageActivity.messagetype = "newuser";
                    startActivity(new Intent(context, ChatActivity.class));
                }
            } else {
                Snackbar.make(share, "Login to send a message.!", Snackbar.LENGTH_LONG).setAction("LOGIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                }).show();
                Snackbar.make(share, "Login to send a message.!", Snackbar.LENGTH_LONG).setAction("LOGIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                }).show();

            }
        });

        SharedPreferences shareduser = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String poststry = "\\\"photoid\\\":" + sharedPreferences.getString("postsidprofile", "") + ",";

        String storystry = "\\\"id\\\":" + sharedPreferences.getString("storyidprofile", "") + ",";

        if (shareduser.getString(useridprofile + "postsprofile", "").isEmpty()) {
            if (!shareduser.getString(useridprofile + "postsprofile", "").contains(poststry.replace("\\", ""))) {
                shareduser.edit().putString(useridprofile + "postsprofile", "").apply();
            }
        }

        if (shareduser.getString(useridprofile + "firewallprofile", "").isEmpty()) {
            if (!shareduser.getString(useridprofile + "firewallprofile", "").contains(storystry.replace("\\", ""))) {
                shareduser.edit().putString(useridprofile + "firewallprofile", "").apply();
            }
        }

        loadprofile(context);
        loadtrends();
        loadallposts(linearLayoutposts);
        loadalbums(linearLayoutalbum);
        loadfirewall(linearLayoutfirewall);

    }

    private void loadfirewall(LinearLayoutManager linearLayoutfirewall) {
        String useridprofile = sharedPreferences.getString("useridprofile", "");

        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        firewall.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemContst = linearLayoutfirewall.getChildCount();
                totalItemCountst = linearLayoutfirewall.getItemCount();
                firstVisibleItemst = linearLayoutfirewall.findLastVisibleItemPosition();

                if (dx > 0) {
                    if (loadst) {
                        if (totalItemCountst > previousTotalst) {
                            previousTotalst = totalItemCountst;
                            loadst = false;
                            page++;
                        }
                    }

                    if (!loadst && (firstVisibleItemst + visibleItemContst) >= totalItemCountst) {
                        loadnost = loadnost + 20;
                        Log.d(TAG, "onScrolled: " + loadnost);
                        loadmorefirewall(String.valueOf(loadnost));
                        loadst = true;
                    }
                }
            }

        });

        if (!sharedusersdata.getString(useridprofile + "firewallprofile", "").isEmpty()) {
            try {
                JSONArray array = new JSONArray(sharedusersdata.getString(useridprofile + "firewallprofile", ""));

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    int id = object.getInt("id");
                    String userid = object.getString("userid");
                    String link = object.getString("link");
                    String caption = object.getString("caption");
                    String type = object.getString("type");
                    int likes = object.getInt("likes");
                    String dateshowed = object.getString("dateshowed");
                    String datecreated = object.getString("datecreated");

                    Stories firewall = new Stories(id, likes, userid, link, caption, type, dateshowed, datecreated);
                    firewallslist.add(firewall);
                }

                storiesadapter.notifyDataSetChanged();

                if (storiesadapter.getItemCount() < 1) {
                    nofirewall.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            loadmorefirewall("");
        }
    }


    private void loadalbums(LinearLayoutManager linearLayoutalbum) {
        String useridprofile = sharedPreferences.getString("useridprofile", "");

        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        albums.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemContal = linearLayoutalbum.getChildCount();
                totalItemCountal = linearLayoutalbum.getItemCount();
                firstVisibleItemal = linearLayoutalbum.findLastVisibleItemPosition();


                if (dx > 0) {
                    if (loadal) {
                        if (totalItemCountal > previousTotalal) {
                            previousTotalal = totalItemCountal;
                            loadal = false;
                            page++;
                        }
                    }

                    if (!loadal && (firstVisibleItemal + visibleItemContal) >= totalItemCountal - 3) {
                        loadnoal = loadnoal + 20;
                        loadmoreposts(String.valueOf(loadnoal));
                        loadal = true;
                    }
                }
            }

        });

        if (!sharedusersdata.getString(useridprofile + "albumsprofile", "").isEmpty()) {

            try {
                JSONArray array = new JSONArray(sharedusersdata.getString(useridprofile + "albumsprofile", ""));

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    String albumid = object.getString("albumid");
                    String albumname = object.getString("albumname");
                    String albumdesc = object.getString("albumdesc");
                    String albumurl = object.getString("albumurl");
                    String userid = object.getString("userid");
                    String albumcreated = object.getString("albumcreated");

                    Albums albums = new Albums(albumid, userid, albumname, albumdesc, albumcreated, albumurl);
                    albumslist.add(albums);
                }

                albumsadapter.notifyDataSetChanged();

                if (albumsadapter.getItemCount() < 1) {
                    noalbums.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            loadmorealbums("");
        }

    }

    private void loadmorefirewall(String limit) {

        String useridprofile = sharedPreferences.getString("useridprofile", "");

        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", useridprofile)
                .add("limit", limit).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.userstories)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (limit.isEmpty()) {
                            sharedusersdata.edit().putString(useridprofile + "firewallprofile", data).apply();
                        }

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                int id = object.getInt("id");
                                String userid = object.getString("userid");
                                String link = object.getString("link");
                                String caption = object.getString("caption");
                                String type = object.getString("type");
                                int likes = object.getInt("likes");
                                String dateshowed = object.getString("dateshowed");
                                String datecreated = object.getString("datecreated");

                                Stories firewall = new Stories(id, likes, userid, link, caption, type, dateshowed, datecreated);
                                firewallslist.add(firewall);
                            }

                            storiesadapter.notifyDataSetChanged();

                            if (limit.isEmpty()) {
                                if (storiesadapter.getItemCount() < 1) {
                                    nofirewall.setVisibility(View.VISIBLE);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }

    private void loadmorealbums(String limit) {

        String useridprofile = sharedPreferences.getString("useridprofile", "");

        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", useridprofile)
                .add("limit", limit).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.albumlist)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (limit.isEmpty()) {
                            sharedusersdata.edit().putString(useridprofile + "albumsprofile", data).apply();
                        }
                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                String albumid = object.getString("albumid");
                                String albumname = object.getString("albumname");
                                String albumdesc = object.getString("albumdesc");
                                String albumurl = object.getString("albumurl");
                                String userid = object.getString("userid");
                                String albumcreated = object.getString("albumcreated");

                                Albums albums = new Albums(albumid, userid, albumname, albumdesc, albumcreated, albumurl);
                                albumslist.add(albums);
                            }

                            albumsadapter.notifyDataSetChanged();

                            if (limit.isEmpty()) {
                                if (albumsadapter.getItemCount() < 1) {
                                    noalbums.setVisibility(View.VISIBLE);
                                }
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

    private void loadtrends() {

        String useridprofile = sharedPreferences.getString("useridprofile", "");

        SharedPreferences sharedPreferences = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        if (!sharedPreferences.getString(useridprofile + "trendsprofile", "").isEmpty()) {
            try {
                JSONArray array = new JSONArray(sharedPreferences.getString(useridprofile + "trendsprofile", ""));

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

                    Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                            dateshowed, trendingcount, likes);
                    trendslist.add(photo);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                trendingposts.setLayoutManager(linearLayoutManager);

                trendingadapter = new risingartistsadapter(context, trendslist);
                trendingposts.setAdapter(trendingadapter);

                trendingloading.setVisibility(View.GONE);
                if (trendingadapter.getItemCount() < 1) {
                    noposts.setVisibility(View.VISIBLE);
                    trendsposts.setVisibility(View.GONE);
                    trendingloading.setVisibility(View.GONE);
                    poststxt.setVisibility(View.GONE);
                    trendingloading.setVisibility(View.GONE);
                    posts.setVisibility(View.GONE);
                }

            } catch (
                    JSONException e) {
                e.printStackTrace();
            }
        } else {
            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("userid", useridprofile).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.userprotrends)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: ", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            trendingloading.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            sharedPreferences.edit().putString(useridprofile + "trendsprofile", data).apply();

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

                                    Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                            dateshowed, trendingcount, likes);
                                    trendslist.add(photo);
                                }
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                                trendingposts.setLayoutManager(linearLayoutManager);
                                trendingadapter = new risingartistsadapter(context, trendslist);
                                trendingposts.setAdapter(trendingadapter);

                                trendingloading.setVisibility(View.GONE);
                                if (trendingadapter.getItemCount() < 1) {
                                    noposts.setVisibility(View.VISIBLE);
                                    trendsposts.setVisibility(View.GONE);
                                    trendingloading.setVisibility(View.GONE);
                                    poststxt.setVisibility(View.GONE);
                                    trendingloading.setVisibility(View.GONE);
                                    posts.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });

        }

    }

    private void loadallposts(LinearLayoutManager linearLayoutposts) {

        posts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemCont = linearLayoutposts.getChildCount();
                totalItemCount = linearLayoutposts.getItemCount();
                firstVisibleItem = linearLayoutposts.findLastVisibleItemPosition();

                if (dx > 0) {
                    if (load) {
                        if (totalItemCount > previousTotal) {
                            previousTotal = totalItemCount;
                            load = false;
                            page++;
                        }
                    }

                    if (!load && (firstVisibleItem + visibleItemCont) >= totalItemCount - 3) {
                        loadno = loadno + 50;
                        loadmoreposts(String.valueOf(loadno));
                        load = true;
                    }
                }
            }

        });

        String useridprofile = sharedPreferences.getString("useridprofile", "");

        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        if (!sharedusersdata.getString(useridprofile + "postsprofile", "").isEmpty()) {
            try {
                JSONArray array = new JSONArray(sharedusersdata.getString(useridprofile + "postsprofile", ""));

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

                    Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                            dateshowed, trendingcount, likes);
                    postslist.add(photo);
                }

                poststsadapter.notifyDataSetChanged();


            } catch (
                    JSONException e) {
                e.printStackTrace();
            }
        } else {

            loadmoreposts("");

        }

    }

    private void loadmoreposts(String limit) {

        String useridprofile = sharedPreferences.getString("useridprofile", "");

        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        final OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", useridprofile)
                .add("limit", limit).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.usersallphotos)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        trendingloading.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (limit.isEmpty()) {
                            sharedusersdata.edit().putString(useridprofile + "postsprofile", data).apply();
                        }
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

                                Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                        dateshowed, trendingcount, likes);
                                postslist.add(photo);
                            }

                            poststsadapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }

    public static void loadprofile(Context context) {

        String useridprofile = sharedPreferences.getString("useridprofile", "");
        String displayname = sharedPreferences.getString("displaynameprofile", "");
        String usernametxt = sharedPreferences.getString("usernameprofile", "");
        String profilepictxt = sharedPreferences.getString("profilephotoprofile", "");
        String verifiedtxt = sharedPreferences.getString("verifiedprofile", "");
        String description = sharedPreferences.getString("descriptionprofile", "");
        String websitestxt = sharedPreferences.getString("websitesprofile", "");
        String categorytxt = sharedPreferences.getString("categoryprofile", "");
        String backphoto = sharedPreferences.getString("backphotoprofile", "");
        String subscribedtxt = sharedPreferences.getString("subscribedprofile", "");
        String subscribertxt = sharedPreferences.getString("subscriberprofile", "");

        if (displayname.trim().isEmpty()) {
            loaddataprofile(context);
        }

        if (displayname.isEmpty()) {
            name.setVisibility(View.GONE);
        } else {
            name.setVisibility(View.VISIBLE);
        }
        if (usernametxt.isEmpty()) {
            username.setVisibility(View.GONE);
        } else {
            username.setVisibility(View.VISIBLE);
        }
        if (verifiedtxt.isEmpty()) {
            verified.setVisibility(View.GONE);
        } else {
            verified.setVisibility(View.VISIBLE);
        }
        if (categorytxt.isEmpty()) {
            category.setVisibility(View.GONE);
        } else {
            category.setVisibility(View.VISIBLE);
        }
        if (websitestxt.isEmpty()) {
            websites.setVisibility(View.GONE);
        } else {
            websites.setVisibility(View.VISIBLE);
        }

        if (description.isEmpty()) {
            bio.setVisibility(View.GONE);
        } else {
            bio.setVisibility(View.VISIBLE);
        }

        if (verifiedtxt.equals("wallpoverified")) {
            verified.setVisibility(View.VISIBLE);
        } else {
            verified.setVisibility(View.GONE);
        }

        name.setText(displayname);
        username.setText("@ " + usernametxt.toLowerCase());
        category.setText(" " + categorytxt);
        websites.setText(" " + websitestxt);

        websites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (websitestxt.trim().contains("https://") || websitestxt.trim().contains("http://")) {
                    Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(websitestxt.trim()));
                    context.startActivity(viewIntent);
                } else {
                    Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://" + websitestxt.trim()));
                    context.startActivity(viewIntent);
                }
            }
        });


        WordToSpan link = new WordToSpan();
        link.setColorTAG(context.getResources().getColor(R.color.texthashtag))
                .setColorURL(context.getResources().getColor(R.color.texthashtag))
                .setColorPHONE(context.getResources().getColor(R.color.texthashtag))
                .setColorMAIL(context.getResources().getColor(R.color.texthashtag))
                .setColorMENTION(context.getResources().getColor(R.color.texthashtag))
                .setColorCUSTOM(context.getResources().getColor(R.color.texthashtag))
                .setUnderlineURL(false)
                .setLink(" " + description)
                .into(bio)
                .setClickListener((type, text) -> {

                    if (type.trim().equals("tag")) {

                        sharedPreferences.edit().putString("hashtagtext", text).apply();
                        context.startActivity(new Intent(context, HashtagActivity.class));

                    } else if (type.trim().equals("mention")) {

                        getusernameid(context, text.trim());

                    } else if (type.trim().equals("mail")) {
                        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{text.trim()});

                        context.startActivity(Intent.createChooser(emailIntent, "Send Mail.."));

                    } else if (type.trim().equals("phone")) {
                        String phono = text;
                        if (phono.length() > 10) {
                            phono = "+" + text;
                        }
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phono));
                        context.startActivity(intent);

                    } else if (type.trim().equals("url")) {

                        if (text.trim().contains("https://") || text.trim().contains("http://")) {
                            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(text.trim()));
                            context.startActivity(viewIntent);
                        } else {
                            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://" + text.trim()));
                            context.startActivity(viewIntent);
                        }
                    } else if (type.trim().equals("custom")) {
                        Log.d(TAG, "onClick: custom");
                    } else {
                        Log.d(TAG, "onClick: else");
                    }

                });

        try {
            Glide.with(context.getApplicationContext()).load(profilepictxt)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                    .placeholder(R.mipmap.profilepic)
                    .into(profilepic);

            Glide.with(context.getApplicationContext()).load(backphoto)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                    .into(backimg);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        subscribed.setText(updatecode.daynumber(subscribedtxt));
        subscribers.setText(updatecode.daynumber(subscribertxt));

        subscriberslay.setOnClickListener(v -> {

            if (userid.equals(useridprofile)) {
                subscribers.setText(subscribertxt);
            }

        });

        if (!userid.isEmpty()) {
            if (userid.equals(useridprofile)) {

                updatecode.analyticsFirebase(context, "view_own_profile", "view_own_profile");

                editprofile.setVisibility(View.VISIBLE);

                editprofile.setOnClickListener(v -> context.startActivity(new Intent(context, EditProfileActivity.class)));

            } else {


                updatecode.analyticsFirebase(context, "view_others_profile", "view_others_profile_" + username.getText().toString());
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        updatecode.analyticsFirebase(context, "view_others_profile", "view_others_profile_" + username.getText().toString());
                        if (chattime) {
                            handler.postDelayed(this, 60000);
                        }
                    }
                };
                handler.postDelayed(runnable, 60000);


                firewalltxt.setText(context.getResources().getString(R.string.firewall));

                OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder().add("usersid", userid)
                        .add("otherid", useridprofile).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.checksub)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: ", e);
                    }

                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");

                        ((Activity) context).runOnUiThread(() -> {

                            if (data.trim().contains("sub")) {

                                subscribebt.setVisibility(View.GONE);
                                subscribedbt.setVisibility(View.VISIBLE);

                            } else {

                                subscribebt.setVisibility(View.VISIBLE);
                                subscribedbt.setVisibility(View.GONE);

                            }
                        });


                    }
                });

                subscribebt.setOnClickListener(v -> {
                    updatecode.updatesub(URLS.subbutton, userid, useridprofile);
                    subscribebt.setVisibility(View.GONE);
                    subscribedbt.setVisibility(View.VISIBLE);

                });

                subscribedbt.setOnClickListener(v -> {
                    updatecode.updatesub(URLS.unsubbutton, userid, useridprofile);

                    subscribedbt.setVisibility(View.GONE);
                    subscribebt.setVisibility(View.VISIBLE);
                });

            }

        } else {
            subscribebt.setVisibility(View.VISIBLE);
            subscribebt.setText(context.getResources().getString(R.string.logintosubscribe));

            subscribebt.setOnClickListener(v -> context.startActivity(new Intent(context, LoginActivity.class)));
        }

    }

    public static void loaddataprofile(Context context) {

        try {

            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("userid", sharedPreferences.getString("useridprofile", "")).build();

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
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    ((Activity) context).runOnUiThread(() -> {

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                String usernametxt = object.getString("username").trim();
                                String displayname = object.getString("displayname").trim();
                                String profilepictxt = object.getString("profilephoto").trim();

                                String verifiedtxt = object.getString("verified").trim();
                                String description = object.getString("description").trim();
                                String categorytxt = object.getString("category").trim();
                                String websitestxt = object.getString("websites").trim();
                                String backphoto = object.getString("backphoto").trim();
                                String subscribed = object.getString("subscribed").trim();
                                String subscribers = object.getString("subscribers").trim();

                                sharedPreferences.edit().putString("useridprofile", sharedPreferences.getString("useridprofile", "")).apply();
                                sharedPreferences.edit().putString("usernameprofile", usernametxt).apply();
                                sharedPreferences.edit().putString("displaynameprofile", displayname).apply();
                                sharedPreferences.edit().putString("profilephotoprofile", profilepictxt).apply();
                                sharedPreferences.edit().putString("verifiedprofile", verifiedtxt).apply();
                                sharedPreferences.edit().putString("descriptionprofile", description).apply();
                                sharedPreferences.edit().putString("websitesprofile", websitestxt).apply();
                                sharedPreferences.edit().putString("categoryprofile", categorytxt).apply();
                                sharedPreferences.edit().putString("backphotoprofile", backphoto).apply();
                                sharedPreferences.edit().putString("subscribedprofile", subscribed).apply();
                                sharedPreferences.edit().putString("subscriberprofile", subscribers).apply();

                                loadprofile(context);

                            }

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    });


                }
            });

        } catch (NullPointerException e) {
            Log.d(TAG, "loaddataprofile: not opened");
        }

    }

    public static void comebacktoprofile(Context context) {

        ((Activity) context).finish();

        context.startActivity(new Intent(context, ProfileActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (Common.ALBUM_STATUS.equals("changed")) {

            String useridprofile = sharedPreferences.getString("useridprofile", "");

            SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

            sharedusersdata.edit().putString(useridprofile + "albumsprofile", "").apply();

            Intent i = getIntent();
            startActivity(i);
            finish();

            Common.ALBUM_STATUS = "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        chattime = false;
    }
}