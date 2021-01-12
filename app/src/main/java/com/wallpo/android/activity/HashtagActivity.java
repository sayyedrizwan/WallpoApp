package com.wallpo.android.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.wallpo.android.R;
import com.wallpo.android.adapter.homepostsadapter;
import com.wallpo.android.explorefragment.risingartistsadapter;
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

public class HashtagActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String hashtag = "";
    TextView toptext, texthashtag;
    SpinKitView trendsloading, recetsloading;
    RecyclerView trendingposts, recentsposts;
    Context context = this;
    risingartistsadapter risingartistsadapter;
    private List<Photos> photolist = new ArrayList<>();
    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = 0;
    private int previousTotal;
    private boolean load = true;
    LinearLayoutManager linearLayoutManager;
    private List<Photos> photolistdate = new ArrayList<>();
    private int totalItemCountdt, firstVisibleItemdt, visibleItemContdt;
    private int pagedt = 1;
    private int loadnodt = 0;
    private int previousTotaldt;
    private boolean loaddt = true;
    LinearLayoutManager linearLayoutManagerdt;
    homepostsadapter homepostsadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);

        sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        toptext = findViewById(R.id.toptext);
        texthashtag = findViewById(R.id.texthashtag);
        trendingposts = findViewById(R.id.trendingposts);
        recentsposts = findViewById(R.id.recentsposts);

        trendsloading = findViewById(R.id.trendsloading);
        recetsloading = findViewById(R.id.recetsloading);

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        trendingposts.setLayoutManager(linearLayoutManager);
        risingartistsadapter = new risingartistsadapter(context, photolist);
        trendingposts.setAdapter(risingartistsadapter);

        linearLayoutManagerdt = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recentsposts.setLayoutManager(linearLayoutManagerdt);
        homepostsadapter = new homepostsadapter(context, photolistdate);
        recentsposts.setAdapter(homepostsadapter);

        hashtag = sharedPreferences.getString("hashtagtext", "");

        toptext.setText("Trending on " + hashtag);

        texthashtag.setText("RECENT POSTS OF " + hashtag);

        loadtrendinghashtag();
        loaddatehashtag();


        updatecode.analyticsFirebase(context, "hashtag_activity", "hashtag_activity");


    }

    private void loadtrendinghashtag() {

        loaddata("", "trending");

        trendingposts.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                    if (!load && (firstVisibleItem + visibleItemCont) >= totalItemCount - 3) {

                        loadno = loadno + 20;
                        loaddata(String.valueOf(loadno), "trending");
                        load = true;
                    }
                }
            }

        });

    }

    private void loaddatehashtag() {

        loaddata("", "");

        recentsposts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemContdt = linearLayoutManagerdt.getChildCount();
                totalItemCountdt = linearLayoutManagerdt.getItemCount();
                firstVisibleItemdt = linearLayoutManagerdt.findLastVisibleItemPosition();


                if (dx > 0) {
                    if (loaddt) {
                        if (totalItemCountdt > previousTotaldt) {
                            previousTotaldt = totalItemCountdt;
                            loaddt = false;
                            pagedt++;
                        }
                    }

                    if (!loaddt && (firstVisibleItemdt + visibleItemContdt) >= totalItemCountdt - 3) {

                        loadnodt = loadnodt + 20;
                        loaddata(String.valueOf(loadnodt), "");
                        loaddt = true;
                    }
                }
            }

        });

    }

    private void loaddata(String limit, String type) {
        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("hashtag", hashtag.trim().replace("'", "\\'"))
                .add("type", type).add("limit", limit).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.hashtagphoto)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("explorepostsadapter", "onFailure: ", e);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (type.equals("trending"))
                            trendingposts.setVisibility(View.GONE);
                        else
                            recetsloading.setVisibility(View.GONE);
                        Toast.makeText(context, "Connection Error.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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


                                if (type.equals("trending")) {
                                    Photos photos = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                            dateshowed, trendingcount, likes);
                                    photolist.add(photos);
                                } else {
                                    Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                            dateshowed, trendingcount, likes, "");
                                    photolistdate.add(photo);
                                }
                            }

                            if (type.equals("trending")) {
                                risingartistsadapter.notifyDataSetChanged();
                                trendsloading.setVisibility(View.GONE);
                            } else {

                                homepostsadapter.notifyDataSetChanged();
                                recetsloading.setVisibility(View.GONE);

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