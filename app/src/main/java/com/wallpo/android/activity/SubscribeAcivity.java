package com.wallpo.android.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.wallpo.android.R;
import com.wallpo.android.adapter.subsaccountsadapter;
import com.wallpo.android.getset.User;
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
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class SubscribeAcivity extends AppCompatActivity {

    private static final String TAG = "SubsActivity";
    private RecyclerView recyclerview;
    private Context context = this;

    private subsaccountsadapter adapter;
    private List<User> sublist = new ArrayList<>();;
    LinearLayout imageupload;
    SpinKitView loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_acivity);

        recyclerview = findViewById(R.id.recyclerview);
        loadingbar = findViewById(R.id.loadingbar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerview.setLayoutManager(linearLayoutManager);

        adapter = new subsaccountsadapter(context, sublist);
        recyclerview.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("useridprofile", "");


        updatecode.analyticsFirebase(context, "subs_activity", "subs_activity");


        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", userid).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.usersubscribers)
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
                        loadingbar.setVisibility(View.GONE);
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
                            Toast.makeText(context, "" + getResources().getString(R.string.autherrortryagain), Toast.LENGTH_LONG).show();
                            loadingbar.setVisibility(View.GONE);
                            return;
                        }

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                String userid = object.getString("userid");
                                String username = object.getString("username");
                                String verified = object.getString("verified");
                                String description = object.getString("description");
                                String displayname = object.getString("displayname");
                                String category = object.getString("category");
                                String websites = object.getString("websites");
                                String profilephoto = object.getString("profilephoto");
                                String backphoto = object.getString("backphoto");
                                String subscribed = object.getString("subscribed");
                                String subscribers = object.getString("subscribers");

                                User users = new User(userid, "", "", username, "", verified, "", description, displayname, category, profilephoto,
                                        backphoto, websites, subscribed, subscribers);
                                sublist.add(users);

                            }

                            adapter.notifyDataSetChanged();

                            loadingbar.setVisibility(View.GONE);

                            if (adapter.getItemCount() < 1){
                                Toast.makeText(context, "" + getResources().getString(R.string.nosubscribed), Toast.LENGTH_LONG).show();

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