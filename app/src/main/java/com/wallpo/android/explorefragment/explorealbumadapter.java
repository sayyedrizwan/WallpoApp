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
import com.wallpo.android.getset.Albums;
import com.wallpo.android.getset.Stories;
import com.wallpo.android.utils.URLS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import static android.content.ContentValues.TAG;

public class explorealbumadapter extends RecyclerView.Adapter<explorealbumadapter.ViewHolder> {
    private Context context;
    private List<Stories> Stories;
    public static TextView noticeexplore;

    public explorealbumadapter(Context context, List<Stories> Stories) {
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

        LinearLayoutManager linearLayoutManager;
        final List<Albums> albumsList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerview.setLayoutManager(linearLayoutManager);

        holder.albumsadapter = new albumsadapter(context, albumsList);

        holder.recyclerview.setAdapter(holder.albumsadapter);

        final SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);

     /*   if (sharepref.getString("albumexploretime", "").isEmpty()){
            OkHttpClient client = new OkHttpClient();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.trendingalbums)
                    .get()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("explorefirstadapter", "onFailure: ", e);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: dd");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: trendingalbums " + data);
                            sharepref.edit().putString("albumexplore", data).apply();
                            sharepref.edit().putString("albumexploretime", getTimestamphour()).apply();
                            albumsList.clear();
                            deletealbumdata();

                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    String albumid = object.getString("albumid");
                                    String userid = object.getString("userid");
                                    String albumname = object.getString("albumname");
                                    String albumdesc = object.getString("albumdesc");
                                    String albumcreated = object.getString("albumcreated");
                                    String albumurl = object.getString("albumurl");

                                    Albums albumItem = new Albums(albumid, userid, albumname, albumdesc, albumcreated, albumurl);
                                    albumsList.add(albumItem);


                                    HashSet<Albums> listToSet = new HashSet<Albums>(albumsList);
                                    List<Albums> listWithoutDuplicates = new ArrayList<Albums>(listToSet);
                                    albumsList.clear();
                                    albumsList.addAll(listWithoutDuplicates);
                                    holder.albumsadapter.notifyDataSetChanged();


                                }

                            } catch (
                                    JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });
        }*/

        try {
            JSONArray array = new JSONArray(sharepref.getString("albumexplore", ""));

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                String albumid = object.getString("albumid");
                String userid = object.getString("userid");
                String albumname = object.getString("albumname");
                String albumdesc = object.getString("albumdesc");
                String albumcreated = object.getString("albumcreated");
                String albumurl = object.getString("albumurl");
                String trendingcount = object.getString("trendingcount");

                Albums albumItem = new Albums(albumid, userid, albumname, albumdesc, albumcreated, albumurl);
                albumsList.add(albumItem);


                HashSet<Albums> listToSet = new HashSet<Albums>(albumsList);
                List<Albums> listWithoutDuplicates = new ArrayList<Albums>(listToSet);
                albumsList.clear();
                albumsList.addAll(listWithoutDuplicates);

                holder.albumsadapter.notifyDataSetChanged();

                if (holder.albumsadapter.getItemCount() < 1) {
                    sharepref.edit().putString("albumexploretime", "").apply();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!sharepref.getString("albumexploretime", "").equals(getTimestamphour())) {

            OkHttpClient client = new OkHttpClient();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.trendingalbums)
                    .get()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("explorefirstadapter", "onFailure: ", e);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: dd");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    ((Activity) context).runOnUiThread(() -> {
                        sharepref.edit().putString("albumexplore", data).apply();
                        sharepref.edit().putString("albumexploretime", getTimestamphour()).apply();
                        albumsList.clear();
                        deletealbumdata();

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                String albumid = object.getString("albumid");
                                String userid = object.getString("userid");
                                String albumname = object.getString("albumname");
                                String albumdesc = object.getString("albumdesc");
                                String albumcreated = object.getString("albumcreated");
                                String albumurl = object.getString("albumurl");
                                String trendingcount = object.getString("trendingcount");

                                Albums albumItem = new Albums(albumid, userid, albumname, albumdesc, albumcreated, albumurl);
                                albumsList.add(albumItem);


                                HashSet<Albums> listToSet = new HashSet<Albums>(albumsList);
                                List<Albums> listWithoutDuplicates = new ArrayList<Albums>(listToSet);
                                albumsList.clear();
                                albumsList.addAll(listWithoutDuplicates);

                                holder.albumsadapter.notifyDataSetChanged();

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

    @Override
    public int getItemCount() {
        return Stories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerview;
        private com.wallpo.android.explorefragment.albumsadapter albumsadapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerview = itemView.findViewById(R.id.recyclerview);

        }
    }


    public String getTimestamphour() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void deletealbumdata() {
        SharedPreferences preferences = context.getSharedPreferences("albumwallpo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

    }


}
