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

public class LikedPostsActivity extends AppCompatActivity {

    private Context context = this;
    private RecyclerView recyclerView;
    private favadapter adapter;
    private List<Photos> photolist;
    private String TAG = "LikePostsActivity";

    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = 0;
    private int previousTotal;
    private boolean load = true;
    SpinKitView progressbar;
    TextView textfav;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_posts);

        textfav = findViewById(R.id.textfav);
        textfav.setVisibility(View.GONE);
        progressbar = findViewById(R.id.progressbar);

        photolist = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new favadapter(context, photolist);
        recyclerView.setAdapter(adapter);

        loadmoreliked("");

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
                        loadno = loadno + 20;
                        loadmoreliked(String.valueOf(loadno));
                        load = true;
                    }
                }
            }

        });

        updatecode.analyticsFirebase(context, "hashtag_activity", "hashtag_activity");

    }

    private void loadmoreliked(String limit) {

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        OkHttpClient client = new OkHttpClient();

        if (!userid.isEmpty()) {

            RequestBody postData = new FormBody.Builder().add("userid", userid)
                    .add("limit", String.valueOf(limit)).add("fcm", sharedPreferences.getString("fcmtoken", "")).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.likesphoto)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("FavActivity", "onFailure: ", e);
                    runOnUiThread(() -> progressbar.setVisibility(View.GONE));
                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(() -> {

                        Log.d(TAG, "onResponse: likedposts " + data);
                        if (data.trim().equals("autherror")){
                            Toast.makeText(context, "" + getResources().getString(R.string.autherrortryagain), Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject Object = array.getJSONObject(i);

//                                    int id = Object.getInt("id");
                                int photoid = Object.getInt("photoid");
                                String caption = Object.getString("caption");
                                String categoryid = Object.getString("categoryid");
                                String albumid = Object.getString("albumid");
                                String datecreated = Object.getString("datecreated");
                                String dateshowed = Object.getString("dateshowed");
                                String link = Object.getString("link");
                                String userid1 = Object.getString("userid");
                                String imagepath = Object.getString("imagepath");
                                String likes = Object.getString("likes");
                                int trendingcount = Object.getInt("trendingcount");

                                Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid1, albumid, link, categoryid,
                                        dateshowed, trendingcount, likes);
                                photolist.add(photo);
                            }

                            adapter.notifyDataSetChanged();

                            progressbar.setVisibility(View.GONE);

                            if (limit.equals("")){
                                if (adapter.getItemCount() < 1){
                                    textfav.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    });


                }
            });
        }

    }
}