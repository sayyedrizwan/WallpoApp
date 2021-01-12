package com.wallpo.android.explorefragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallpo.android.R;
import com.wallpo.android.getset.Stories;
import com.wallpo.android.getset.User;
import com.wallpo.android.utils.URLS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class exploretopidsadapter extends RecyclerView.Adapter<exploretopidsadapter.ViewHolder> {
    private Context context;
    private List<Stories> Stories;

    public exploretopidsadapter(Context context, List<Stories> Stories) {
        this.context = context;
        this.Stories = Stories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.explore_albumadapter_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Stories stories = Stories.get(position);

        holder.noticeexplore.setText(context.getResources().getString(R.string.trendingaccounts));

        final List<User> userList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        holder.recyclerview.setLayoutManager(gridLayoutManager);

        //holder.recyclerview.setHasFixedSize(true);

        holder.idsadapter = new idsadapter(context, userList);

        holder.recyclerview.setAdapter(holder.idsadapter);

        final SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);

        OkHttpClient client = new OkHttpClient();

        String id = sharepref.getString("wallpouserid", "");

        String data = sharepref.getString("topids", "");

        RequestBody body;

        if (data.isEmpty()){
            if (id != null) {
                if (id.isEmpty()) {

                    body = new FormBody.Builder().add("userid", String.valueOf(id)).build();

                } else {

                    body = new FormBody.Builder().add("userid", "").build();
                }

            } else {

                body = new FormBody.Builder().add("userid", "").build();

            }

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.exploretopid)
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("explorefirstadapter", "onFailure: ", e);

                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sharepref.edit().putString("topids", data).apply();
                            sharepref.edit().putString("idstime", getTimestamphour()).apply();
                            userList.clear();

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
                                    userList.add(users);

                                    holder.idsadapter.notifyDataSetChanged();

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

                User users = new User(userid, "", "", username,"", verified,"", description, displayname, category, profilephoto,
                        backphoto, websites, subscribed, subscribers);
                userList.add(users);

                holder.idsadapter.notifyDataSetChanged();

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (!sharepref.getString("idstime", "").equals(getTimestamphour())) {
            RequestBody bodys;

            if (id != null) {
                if (id.isEmpty()) {

                    bodys = new FormBody.Builder().add("userid", String.valueOf(id)).build();
                } else {

                    bodys = new FormBody.Builder().add("userid", "").build();
                }

            } else {

                bodys = new FormBody.Builder().add("userid", "").build();

            }

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.exploretopid)
                    .post(bodys)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("explorefirstadapter", "onFailure: ", e);

                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sharepref.edit().putString("topids", data).apply();
                            sharepref.edit().putString("idstime", getTimestamphour()).apply();
                            userList.clear();

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
                                    userList.add(users);

                                    holder.idsadapter.notifyDataSetChanged();

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

    @Override
    public int getItemCount() {
        return Stories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerview;
        private idsadapter idsadapter;
        TextView noticeexplore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerview = itemView.findViewById(R.id.recyclerview);
            noticeexplore = itemView.findViewById(R.id.noticeexplore);

        }
    }


    public String getTimestamphour() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        return sdf.format(new Date());
    }

}
