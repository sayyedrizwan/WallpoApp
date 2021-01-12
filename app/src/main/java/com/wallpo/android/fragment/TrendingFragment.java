package com.wallpo.android.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.adapter.homepostsadapter;
import com.wallpo.android.getset.Photos;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import static android.content.ContentValues.TAG;

public class TrendingFragment extends Fragment {

    private com.wallpo.android.adapter.homepostsadapter homepostsadapter;
    private List<Photos> photolist;
    private RecyclerView postsrecyclerview;
    LinearLayoutManager linearLayoutManager;
    SharedPreferences sharedPreferences;
    SpinKitView progressbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_trending, container, false);

        photolist = new ArrayList<>();

        postsrecyclerview = view.findViewById(R.id.postsrecyclerview);
        progressbar = view.findViewById(R.id.progressbar);

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        postsrecyclerview.setLayoutManager(linearLayoutManager);
        homepostsadapter = new homepostsadapter(getActivity(), photolist);
        postsrecyclerview.setAdapter(homepostsadapter);


        updatecode.analyticsFirebase(getContext(), "trending_fragment", "trending_fragment");


        if (getActivity() != null) {

            sharedPreferences = getActivity().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        }
        final String userid = sharedPreferences.getString("wallpouserid", "");
        final String trendingtime = sharedPreferences.getString("trendingtime", "");


        loadoldtrends();

        if (!trendingtime.equals(getTimestamphour())) {

            loadTrendingList();
        }

        return view;
    }

    private void loadoldtrends() {
        final String data = sharedPreferences.getString("trendingpostslist", "");

        if (data.isEmpty() || data.equals("[]")){
            loadTrendingList();
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
                String type = "trending";

                Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                        dateshowed, trendingcount, likes, type);
                photolist.add(photo);

            }

            homepostsadapter.notifyDataSetChanged();
            progressbar.setVisibility(View.GONE);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }


    private void loadTrendingList() {

        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.trendingview)
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TrendingFragment", "onFailure: ", e);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressbar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(() -> {

                    sharedPreferences.edit().putString("trendingpostslist", data).apply();
                    sharedPreferences.edit().putString("trendingtime", getTimestamphour()).apply();
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
                            String type = "trending";

                            Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                    dateshowed, trendingcount, likes, type);
                            photolist.add(photo);

                        }

                        homepostsadapter.notifyDataSetChanged();
                        progressbar.setVisibility(View.GONE);

                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }
                });


            }
        });
    }

    public String getTimestamphour() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.Trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));


        } catch (NullPointerException e) {
            Log.d("ProfileFragment", "onStart: ");
        }

    }
}
