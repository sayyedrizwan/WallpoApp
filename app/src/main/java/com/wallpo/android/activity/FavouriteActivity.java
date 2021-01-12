package com.wallpo.android.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.wallpo.android.R;
import com.wallpo.android.adapter.favadapter;
import com.wallpo.android.getset.Photos;
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

import static com.wallpo.android.MainActivity.initializeSSLContext;
import static com.wallpo.android.utils.updatecode.updateFCM;

public class FavouriteActivity extends AppCompatActivity {

    private favadapter adapter;
    private List<Photos> photolist;

    private RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    Context context = FavouriteActivity.this;
    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = 0;
    private int previousTotal;
    private boolean load = true;
    SpinKitView progressbar;
    TextView textfav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        initializeSSLContext(context);

        recyclerView = findViewById(R.id.recyclerview);
        textfav = findViewById(R.id.textfav);
        textfav.setVisibility(View.GONE);
        progressbar = findViewById(R.id.progressbar);
        photolist = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new favadapter(context, photolist);
        recyclerView.setAdapter(adapter);

        updatecode.analyticsFirebase(context, "favourite_activity", "favourite_activity");

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        sharedPreferences.edit().putString("favviewweight", String.valueOf(recyclerView.getMeasuredWidth())).apply();

        sharedPreferences.edit().putString("favviewheight", String.valueOf(recyclerView.getMeasuredHeight())).apply();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemCont = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findLastVisibleItemPosition();


                if (dx > 0) {
                    if (load) {
                        if (totalItemCount > previousTotal) {
                            previousTotal = totalItemCount;
                            load = false;
                            page++;
                        }
                    }

                    if (!load && (firstVisibleItem + visibleItemCont) >= totalItemCount) {
                        loadmore();
                        load = true;
                    }
                }
            }

        });



        OkHttpClient client = new OkHttpClient();
        String userid = sharedPreferences.getString("wallpouserid", "");

        if (!userid.isEmpty()) {

            RequestBody postData = new FormBody.Builder().add("userid", userid)
                    .add("fcm", sharedPreferences.getString("fcmtoken", "")).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.favview)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("FavActivity", "onFailure: ", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressbar.setVisibility(View.GONE);
                            textfav.setVisibility(View.GONE);
                            Toast.makeText(context, getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (data.trim().equals("autherror")){
                                textfav.setText(getResources().getString(R.string.autherrortryagain));

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    TransitionManager.beginDelayedTransition(((LinearLayout) findViewById(R.id.mainlay)));
                                }
                                updateFCM(context);
                                progressbar.setVisibility(View.GONE);
                                textfav.setVisibility(View.VISIBLE);
                                return;
                            }
                            if (data.trim().equals("nofound")){

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    TransitionManager.beginDelayedTransition(((LinearLayout) findViewById(R.id.mainlay)));
                                }

                                progressbar.setVisibility(View.GONE);
                                textfav.setVisibility(View.VISIBLE);
                                return;
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
                                    photolist.add(photo);
                                }

                                adapter.notifyDataSetChanged();
                                if (adapter.getItemCount() > 0){
                                    progressbar.setVisibility(View.GONE);
                                }

                                if (adapter.getItemCount() < 1){

                                    progressbar.setVisibility(View.GONE);
                                    textfav.setVisibility(View.VISIBLE);


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

    private void loadmore() {

        loadno = loadno + 20;

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        OkHttpClient client = new OkHttpClient();
        String userid = sharedPreferences.getString("wallpouserid", "");

        if (!userid.isEmpty()) {


            RequestBody postData = new FormBody.Builder().add("userid", userid)
                    .add("limit", String.valueOf(loadno))
                    .add("fcm", sharedPreferences.getString("fcmtoken", ""))
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.favview)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("FavActivity", "onFailure: ", e);
                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (data.trim().equals("autherror")){
                                Toast.makeText(context, "" + getResources().getString(R.string.autherrortryagain), Toast.LENGTH_LONG).show();
                                return;
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
                                    photolist.add(photo);
                                }

                                adapter.notifyDataSetChanged();

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
}