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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.wallpo.android.R;
import com.wallpo.android.adapter.commentsadapter;
import com.wallpo.android.getset.Comment;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StoriesCommentActivity extends AppCompatActivity {


    private List<Comment> commentlist = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerview;
    String id = "";
    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = 0;
    private int previousTotal;
    private boolean load = true;
    Context context = this;
    TextView caption;
    com.wallpo.android.adapter.commentsadapter commentsadapter;
    CardView logincomment, addcomment;
    TextInputEditText commenttext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_comment);

        recyclerview = findViewById(R.id.recyclerview);
        logincomment = findViewById(R.id.logincomment);
        logincomment.setVisibility(View.GONE);
        commenttext = findViewById(R.id.commenttext);
        addcomment = findViewById(R.id.addcomment);
        caption = findViewById(R.id.caption);

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("wallpouserid", "");

        updatecode.analyticsFirebase(context, "stories_comment_activity", "stories_comment_activity");

        if (id.isEmpty()){
            logincomment.setVisibility(View.VISIBLE);
            commenttext.setVisibility(View.GONE);
            addcomment.setVisibility(View.GONE);
        }

        caption.setText(Common.PHOTO_INFO);

        linearLayoutManager = new LinearLayoutManager(context);
        recyclerview.setLayoutManager(linearLayoutManager);

        commentsadapter = new commentsadapter(context, commentlist);
        recyclerview.setAdapter(commentsadapter);

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


                if (dy > 0) {
                    if (load) {
                        if (totalItemCount > previousTotal) {
                            previousTotal = totalItemCount;
                            load = false;
                            page++;
                        }
                    }

                    if (!load && (firstVisibleItem + visibleItemCont) >= totalItemCount - 4) {
                        loadno = loadno + 20;
                        loadstoriescomment(String.valueOf(loadno));
                        load = true;
                    }
                }
            }

        });

        loadstoriescomment("");


        addcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                long timeMilli = date.getTime();
                Comment comments = new Comment(0, id, commenttext.getText().toString(), String.valueOf(Common.PHOTO_ID), Common.USER_ID, String.valueOf(timeMilli));

                commentlist.add(0, comments);

                commentsadapter.notifyDataSetChanged();

                String comment = commenttext.getText().toString();
                commenttext.setText("");

                OkHttpClient client = new OkHttpClient();

                final String tz = TimeZone.getDefault().getID();

                RequestBody postData = new FormBody.Builder()
                        .add("userid", id)
                        .add("comment", comment)
                        .add("storyid", String.valueOf(Common.PHOTO_ID))
                        .add("storyuserid", Common.USER_ID).add("timezone", tz).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.addstorycomment)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Connection Error..", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");

                    }
                });


            }
        });

    }

    private void loadstoriescomment(String limit) {

        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder()
                .add("storyid", String.valueOf(Common.PHOTO_ID))
                .add("userid", String.valueOf(id))
                .add("limit", String.valueOf(limit)).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.storyshowcomment)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("StoryCommentActivity", "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                int id = object.getInt("id");
                                String userid = object.getString("userid");
                                String storycomment = object.getString("comment");
                                String storyid = object.getString("storyid");
                                String storyuserid = object.getString("storyuserid");
                                String date = object.getString("date");

                                Comment comments = new Comment(id, userid, storycomment, storyid, storyuserid, date);
                                commentlist.add(comments);

                            }

                            commentsadapter.notifyDataSetChanged();

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }


    public String getTimestampdate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}