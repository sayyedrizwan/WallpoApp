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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallpo.android.R;
import com.wallpo.android.adapter.storiesadapter;
import com.wallpo.android.getset.Stories;
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
import okhttp3.OkHttpClient;

import static android.content.ContentValues.TAG;

public class explorestoriessadapter extends RecyclerView.Adapter<explorestoriessadapter.ViewHolder> {
    private Context context;
    private List<Stories> Stories;

    public explorestoriessadapter(Context context, List<Stories> Stories) {
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

        holder.noticeexplore.setText(context.getResources().getString(R.string.trendingonfirewall));

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerview.setLayoutManager(linearLayoutManager);

        final List<Stories> storylists = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final storiesadapter adapter = new storiesadapter(context, storylists);

        holder.recyclerview.setAdapter(adapter);

        String storydata = sharedPreferences.getString("storiesdatatrends", "");

        storylists.clear();

        try {
            JSONArray array = new JSONArray(storydata);

            for (int i = 0; i < array.length(); i++) {

                JSONObject Object = array.getJSONObject(i);

                int storyid = Object.getInt("id");
                String userids = Object.getString("userid");
                String caption = Object.getString("caption");
                String type = Object.getString("type");
                int likes = Object.getInt("likes");
                String link = Object.getString("link");
                String dateshowed = Object.getString("dateshowed");
                String datecreated = Object.getString("datecreated");

                Stories story = new Stories(storyid, likes, userids, link, caption, type, dateshowed, datecreated);
                storylists.add(story);
            }

            adapter.notifyDataSetChanged();

            if (adapter.getItemCount() < 1){

                getstorydata(storylists, adapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sharedPreferences.edit().putString("storiesdatatrends", "").apply();

        final String userids = sharedPreferences.getString("wallpouserid", "");

        if (!sharedPreferences.getString("storiesdatatrends", "").equals(getTimestamphour())){

            getstorydata(storylists, adapter);
        }


        getstorydata(storylists, adapter);

    }

    private void getstorydata(final List<Stories> storylists, final storiesadapter adapter) {

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.trendingfirewall)
                .get()
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

                        try {
                            JSONArray array = new JSONArray(data);

                            sharedPreferences.edit().putString("storiesdatatrends", data).apply();
                            sharedPreferences.edit().putString("storiestrendstime", getTimestamphour()).apply();

                            storylists.clear();

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject Object = array.getJSONObject(i);

                                int storyid = Object.getInt("id");
                                String userid = Object.getString("userid");
                                String link = Object.getString("link");
                                String caption = Object.getString("caption");
                                int likes = Object.getInt("likes");
                                String dateshowed = Object.getString("dateshowed");
                                String datecreated = Object.getString("datecreated");
                                String type = Object.getString("type");

                                Stories story = new Stories(storyid, likes, userid, link, caption, type, dateshowed, datecreated);
                                storylists.add(story);
                            }

                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });

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
