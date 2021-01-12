package com.wallpo.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputEditText;
import com.wallpo.android.LoginActivity;
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

import static android.content.ContentValues.TAG;

public class CommentActivity extends AppCompatActivity {

    Context context = this;
    ImageView image;
    TextView caption;
    CardView addcomment, logincomment, login;
    private List<Comment> commentlist = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerview;
    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = 0;
    private int previousTotal;
    private boolean load = true;
    com.wallpo.android.adapter.commentsadapter commentsadapter;
    TextInputEditText commenttext;
    String id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        image = findViewById(R.id.image);
        caption = findViewById(R.id.caption);
        recyclerview = findViewById(R.id.recyclerview);
        addcomment = findViewById(R.id.addcomment);
        commenttext = findViewById(R.id.commenttext);
        logincomment = findViewById(R.id.logincomment);
        logincomment.setVisibility(View.GONE);
        login = findViewById(R.id.login);

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("wallpouserid", "");

        if (id.isEmpty()){
            logincomment.setVisibility(View.VISIBLE);
            commenttext.setVisibility(View.GONE);
            addcomment.setVisibility(View.GONE);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        try {
            Glide.with(context).load(Common.PHOTO_INFO)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                    .into(image);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        caption.setText(Common.TYPE);

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
                        loadcomments(String.valueOf(loadno));
                        load = true;
                    }
                }
            }

        });

        loadcomments("");

        addcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (commenttext.getText().toString().isEmpty() || commenttext.getText().toString().trim().isEmpty()) {
                    Toast.makeText(context, getResources().getString(R.string.entermessage), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (commenttext.getText().toString().trim().length() < 1) {
                    Toast.makeText(context, getResources().getString(R.string.entervaildcomment), Toast.LENGTH_SHORT).show();
                    return;
                }

                updatecode.analyticsFirebase(context, "comment_added", "comment_added");
                postcomment();

            }
        });
    }

    private void postcomment() {

        Date date = new Date();
        long timeMilli = date.getTime();

        Comment comments = new Comment(0, id, commenttext.getText().toString(), String.valueOf(Common.ID_SELECTED), Common.FROM, String.valueOf(timeMilli));

        commentlist.add(0, comments);

        String comment = commenttext.getText().toString();
        commenttext.setText("");

        commentsadapter.notifyDataSetChanged();

        final String tz = TimeZone.getDefault().getID();

        OkHttpClient client = new OkHttpClient();


        RequestBody postData = new FormBody.Builder().add("comment", comment.trim().replace("'", "\\'"))
                .add("timezone", tz).add("photoid", String.valueOf(Common.ID_SELECTED))
                .add("photosuserid", String.valueOf(Common.FROM)).add("userid", id)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.pushcomment)
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!data.trim().contains("successfully")){
                            Toast.makeText(context, getResources().getString(R.string.enterwhilecomment), Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });

    }

    private void loadcomments(String limit) {
        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(id))
                .add("photoid", String.valueOf(Common.ID_SELECTED)).add("limit", String.valueOf(limit))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.commentview)
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

                runOnUiThread(() -> {

                    try {
                        JSONArray array = new JSONArray(data);


                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            int commentid = object.getInt("id");
                            String userid = object.getString("userid");
                            String photocomment = object.getString("comment");
                            String photoid = object.getString("photoid");
                            String photosuserid = object.getString("photosuserid");
                            String datecreated = object.getString("datecreated");


                            Comment comments = new Comment(commentid, userid, photocomment, photoid, photosuserid, datecreated);
                            commentlist.add(comments);

                        }
                        commentsadapter.notifyDataSetChanged();


                    } catch (
                            JSONException e) {
                        e.printStackTrace();
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