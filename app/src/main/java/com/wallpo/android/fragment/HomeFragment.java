package com.wallpo.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.adapter.addstoriesadapter;
import com.wallpo.android.adapter.homepostsadapter;
import com.wallpo.android.adapter.nosubpostsadapter;
import com.wallpo.android.adapter.storiesadapter;
import com.wallpo.android.getset.Photos;
import com.wallpo.android.getset.Stories;
import com.wallpo.android.swiperefresh.SwipeRefreshLayout;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.customListeners;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.wallpo.android.utils.updatecode.updateFCM;

public class HomeFragment extends Fragment {

    private com.wallpo.android.adapter.homepostsadapter homepostsadapter;
    private com.wallpo.android.adapter.nosubpostsadapter nosubpostadapter;
    private com.wallpo.android.adapter.homepostsadapter adsdapter;
    private ConcatAdapter merged;
    private List<Photos> nosublist = new ArrayList<>();
    private List<Photos> photolist = new ArrayList<>();
    private List<Photos> adslist = new ArrayList<>();

    private RecyclerView postsrecyclerview;
    LinearLayoutManager linearLayoutManager;
    CoordinatorLayout swipelayout;

    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = 0;
    private int previousTotal;
    private boolean load = true;
    private int totalItemCounts, firstVisibleItems, visibleItemConts;
    private int pages = 1;
    private int loadnos = 0;
    private int previousTotals;
    private boolean loads = true;
    SharedPreferences sharedPreferences;
    SpinKitView progressbar;
    NestedScrollView bottomsheet;
    BottomSheetBehavior bottomSheetBehavior;
    RelativeLayout background;
    Bundle savedState = null;
    RecyclerView storyrecyclerview;
    private List<Stories> storylist = new ArrayList<>();
    SpinKitView storiesloading;
    RelativeLayout swipeup;
    Boolean checkuser = true;
    SwipeRefreshLayout swiperefreshlayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postsrecyclerview = view.findViewById(R.id.postsrecyclerview);
        progressbar = view.findViewById(R.id.progressbar);
        bottomsheet = view.findViewById(R.id.bottomsheet);
        background = view.findViewById(R.id.background);
        background.setVisibility(View.GONE);
        storyrecyclerview = view.findViewById(R.id.storyrecyclerview);
        storiesloading = view.findViewById(R.id.storiesloading);
        swipeup = view.findViewById(R.id.swipeup);
        swipelayout = view.findViewById(R.id.swipelayout);
        swiperefreshlayout = view.findViewById(R.id.swiperefreshlayout);

        swiperefreshlayout.setEnabled(true);
        swiperefreshlayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.com_facebook_blue),
                getResources().getColor(R.color.signup),
                getResources().getColor(R.color.white));

        setHasOptionsMenu(true);

        updatecode.analyticsFirebase(getContext(), "home_per_minute", "home_per_minute");

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                updatecode.analyticsFirebase(getContext(), "home_per_minute", "home_per_minute");
                if (checkuser) {
                    handler.postDelayed(this, 60000);
                }
            }
        };
        handler.postDelayed(runnable, 60000);


        if (getActivity() != null) {

            sharedPreferences = getActivity().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            bottomsheet.getLayoutParams().height = height / 3 + 490;

            sharedPreferences.edit().putString("firewallheight", String.valueOf(height / 3 + 490)).apply();

            bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);

            bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            //collapsed
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                TransitionManager.beginDelayedTransition(postsrecyclerview);
                            }
                            background.setVisibility(View.GONE);
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            Log.d(TAG, "onStateChanged: dragging");
                            break;
                        case BottomSheetBehavior.STATE_HALF_EXPANDED:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            //fullexpanded
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                TransitionManager.beginDelayedTransition(postsrecyclerview);
                            }
                            background.setVisibility(View.VISIBLE);
                            break;
                        case BottomSheetBehavior.STATE_HIDDEN:
                            //hidden
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                TransitionManager.beginDelayedTransition(postsrecyclerview);
                            }
                            background.setVisibility(View.GONE);
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                TransitionManager.beginDelayedTransition(postsrecyclerview);
                            }
                            background.setVisibility(View.GONE);

                            break;

                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    Log.d("ViewPostsActivity", "onSlide: slidding....");
                }
            });

        }

        swipeup.setOnTouchListener((v, event) -> {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            return false;
        });


        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        postsrecyclerview.setLayoutManager(linearLayoutManager);

        homepostsadapter = new homepostsadapter(getActivity(), photolist);
        adsdapter = new homepostsadapter(getActivity(), adslist);

        merged = new ConcatAdapter(adsdapter, homepostsadapter);
        postsrecyclerview.setAdapter(merged);


        if (getActivity() != null) {

            sharedPreferences = getActivity().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        }
        final String userid = sharedPreferences.getString("wallpouserid", "");

        if (userid != null) {
            if (!userid.isEmpty()) {
                //loadstories
                swipeup.setVisibility(View.VISIBLE);

                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                storyrecyclerview.setLayoutManager(linearLayoutManager);

                final storiesadapter adapter = new storiesadapter(getActivity(), storylist);

                List<Stories> storylists = new ArrayList<>();

                Stories storys = new Stories(0, 0, "userid", "link", "caption",
                        "type", "dateshowed", "datecreated");
                storylists.add(storys);

                final addstoriesadapter addadapter = new addstoriesadapter(getActivity(), storylists);

                ConcatAdapter mergeAdapter = new ConcatAdapter(addadapter, adapter);

                storyrecyclerview.setAdapter(mergeAdapter);

                String storydata = sharedPreferences.getString("storieposts", "");

                storylist.clear();

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
                        storylist.add(story);
                    }

                    adapter.notifyDataSetChanged();

                    if (adapter.getItemCount() > 0) {
                        storiesloading.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loadstories(adapter);

                storyrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                        visibleItemConts = linearLayoutManager.getChildCount();
                        totalItemCounts = linearLayoutManager.getItemCount();
                        firstVisibleItems = linearLayoutManager.findLastVisibleItemPosition();

                        if (dx > 0) {
                            if (loads) {
                                if (totalItemCounts > previousTotals) {
                                    previousTotals = totalItemCounts;
                                    loads = false;
                                }
                            }

                            if (!loads && (firstVisibleItems + visibleItemConts) >= totalItemCounts - 8) {
                                loadmorestories(adapter);
                                loads = true;
                            }
                        }
                    }

                });

            } else {
                bottomsheet.setVisibility(View.GONE);
                background.setVisibility(View.GONE);
                swipeup.setVisibility(View.GONE);
            }

        } else {
            bottomsheet.setVisibility(View.GONE);
            background.setVisibility(View.GONE);
            swipeup.setVisibility(View.GONE);
        }

        postsrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        System.out.println("Scrolling now");
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        System.out.println("Scroll Settling");
                        break;

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemCont = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                Log.d(TAG, "onScrolled: scrollingtime " + dx);
                if (dx > 0) {
                    if (load) {
                        if (totalItemCount > previousTotal) {
                            previousTotal = totalItemCount;
                            load = false;
                        }
                    }

                    if (!load && (firstVisibleItem + visibleItemCont) >= totalItemCount - 3) {
                        if (userid != null) {
                            if (!userid.isEmpty()) {
                                loadmore();
                                load = true;
                            }
                        }
                    }
                }
            }

        });

        try {
            int firsttime = Integer.parseInt(sharedPreferences.getString("adstimes", "0")) + 25;

            int firsttimes = Integer.parseInt(sharedPreferences.getString("adstimes", "0")) - 25;

            int timestamp = Integer.parseInt(getTimestamphour());

            if (timestamp > firsttimes) {
                sharedPreferences.edit().putString("adstimes", "").apply();
                loadads();
            }

            if (timestamp > firsttime) {
                loadads();
            } else {
                oldads();
            }

        } catch (NumberFormatException e) {
            Log.e(TAG, "onCreateView: ", e);
            oldads();
            loadads();
        }


        swiperefreshlayout.setOnRefreshListener(() -> {
            if (userid != null) {
                if (!userid.isEmpty()) {

                    loadphoto(URLS.mainscreenphoto);

                    loadphotofirst();

                } else {

                    loadphoto(URLS.mainscreenphotorandom);

                }
            } else {

                loadphoto(URLS.mainscreenphotorandom);

            }
        });

        if (userid != null) {
            if (!userid.isEmpty()) {

                loadphoto(URLS.mainscreenphoto);

                loadphotofirst();

            } else {

                loadphoto(URLS.mainscreenphotorandom);

            }
        } else {

            loadphoto(URLS.mainscreenphotorandom);

        }


        return view;
    }

    private void loadmorestories(final storiesadapter adapter) {

        loadnos = loadnos + 50;

        OkHttpClient client = new OkHttpClient();

        final String userid = sharedPreferences.getString("wallpouserid", "");

        RequestBody postData = new FormBody.Builder().add("userid", userid)
                .add("limit", String.valueOf(loadnos)).build();

        Request request = new Request.Builder()
                .url(URLS.mainscreenstory)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(() -> {

                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject Object = array.getJSONObject(i);

                            int storyid = Object.getInt("id");
                            String userid1 = Object.getString("userid");
                            String caption = Object.getString("caption");
                            String type = Object.getString("type");
                            int likes = Object.getInt("likes");
                            String link = Object.getString("link");
                            String dateshowed = Object.getString("dateshowed");
                            String datecreated = Object.getString("datecreated");

                            Stories story = new Stories(storyid, likes, userid1, link, caption, type, dateshowed, datecreated);
                            storylist.add(story);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });


            }
        });

    }

    private void loadstories(final storiesadapter adapter) {

        final String userid = sharedPreferences.getString("wallpouserid", "");

        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", userid).build();

        Request request = new Request.Builder()
                .url(URLS.mainscreenstory)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        storiesloading.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swiperefreshlayout.setRefreshing(false);
                        sharedPreferences.edit().putString("storieposts", data).apply();
                        storylist.clear();

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject Object = array.getJSONObject(i);

                                int storyid = Object.getInt("id");
                                String userid = Object.getString("userid");
                                String caption = Object.getString("caption");
                                String type = Object.getString("type");
                                int likes = Object.getInt("likes");
                                String link = Object.getString("link");
                                String dateshowed = Object.getString("dateshowed");
                                String datecreated = Object.getString("datecreated");

                                Stories story = new Stories(storyid, likes, userid, link, caption, type, dateshowed, datecreated);
                                storylist.add(story);
                            }

                            adapter.notifyDataSetChanged();

                            if (adapter.getItemCount() > 0) {
                                storiesloading.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });

    }

    private void oldads() {

        String data = sharedPreferences.getString("adsdata", "");

        if (data.isEmpty()) {
            loadads();
        }

        try {
            JSONArray array = new JSONArray(sharedPreferences.getString("adsdata", ""));

            adslist.clear();

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
                String type = "ads";

                Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                        dateshowed, trendingcount, likes, type);
                adslist.add(photo);
            }

            adsdapter.notifyDataSetChanged();

            progressbar.setVisibility(View.GONE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadphotofirst() {

        String data = sharedPreferences.getString("photomainscreendata", "");
        if (!data.isEmpty()) {
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
                    String type = "posts";

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
    }


    private void loadads() {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.preadslist)
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded").build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HomeFragment", "onFailure: ", e);

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                if (getContext() == null) {
                    return;
                }
                ((Activity) getContext()).runOnUiThread(() -> {

                    adslist.clear();
                    try {
                        Log.d(TAG, "run: mainscreenads " + data);
                        JSONArray array = new JSONArray(data);

                        sharedPreferences = getContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("adsdata", data).apply();
                        sharedPreferences.edit().putString("adstimes", getTimestamphour()).apply();

                        adslist.clear();

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
                            String uid = Object.getString("uid");
                            String type = "ads";

                            Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                    dateshowed, trendingcount, likes, type, uid);
                            adslist.add(photo);
                        }

                        adsdapter.notifyDataSetChanged();

                        progressbar.setVisibility(View.GONE);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            }
        });

    }

    private void loadphoto(String url) {

        OkHttpClient client = new OkHttpClient();

        if (getActivity() == null) {
            return;
        }

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String userid = sharedPreferences.getString("wallpouserid", "");
        String fcm = sharedPreferences.getString("fcmtoken", "");

        RequestBody postData;

        if (userid.isEmpty()) {

            postData = new FormBody.Builder().add("userid", "0").build();

        } else {
            postData = new FormBody.Builder().add("userid", userid)
                    .add("fcm", fcm).build();
        }

        Request request = new Request.Builder().url(url)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HomeFragment", "onFailure: ", e);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {

                    swiperefreshlayout.setRefreshing(false);
                    Toast.makeText(getContext(), getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {

                    updatecode.loadAds(getContext(), "popup");

                    swiperefreshlayout.setRefreshing(false);
                    final SharedPreferences shareprefusers = getActivity().getSharedPreferences("wallpouserdata", 0);
                    shareprefusers.edit().clear().apply();

                    if (data.trim().equals("unautha")) {
                        updateFCM(getContext());
                        Toast.makeText(getContext(), getResources().getString(R.string.autherrortryagain), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (data.trim().equals("nofound")) {
                        Toast.makeText(getContext(), getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (userid != null) {
                        if (!userid.isEmpty()) {
                            sharedPreferences.edit().putString("photomainscreendata", data).apply();
                        }
                    }

                    photolist.clear();

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
                            String userid1 = Object.getString("userid");
                            String imagepath = Object.getString("imagepath");
                            String likes = Object.getString("likes");
                            int trendingcount = Object.getInt("trendingcount");
                            String type = "posts";

                            Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid1, albumid, link, categoryid,
                                    dateshowed, trendingcount, likes, type);

                            photolist.add(photo);


                        }
                        homepostsadapter.notifyDataSetChanged();

                        progressbar.setVisibility(View.GONE);

                        if (homepostsadapter.getItemCount() < 1) {

                            merged.removeAdapter(adsdapter);
                            merged.removeAdapter(homepostsadapter);

                            nosublist.clear();

                            Photos useridposts = new Photos("", 1);
                            nosublist.add(useridposts);

                            Photos nosubadd = new Photos("", 0);
                            nosublist.add(nosubadd);

                            nosubpostadapter = new nosubpostsadapter(getActivity(), nosublist);
                            merged = new ConcatAdapter(adsdapter, nosubpostadapter, homepostsadapter);
                            postsrecyclerview.setAdapter(merged);
                            loadphoto(URLS.mainscreenphotorandom);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                        photolist.clear();

                        homepostsadapter.notifyDataSetChanged();

                    }
                });


            }
        });

    }

    private void loadmore() {
        loadno = loadno + 40;

        final String userid = sharedPreferences.getString("wallpouserid", "");
        final String fcmtoken = sharedPreferences.getString("fcmtoken", "");


        OkHttpClient client = new OkHttpClient();
        if (!userid.isEmpty()) {

            RequestBody postData = new FormBody.Builder().add("userid", userid)
                    .add("limit", String.valueOf(loadno)).add("fcm", fcmtoken).build();

            Request request = new Request.Builder()
                    .url(URLS.mainscreenphoto)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("HomeFragment", "onFailure: ", e);
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getContext(), "" + getActivity().getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(() -> {

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
                                String userid1 = Object.getString("userid");
                                String imagepath = Object.getString("imagepath");
                                String likes = Object.getString("likes");
                                int trendingcount = Object.getInt("trendingcount");
                                String type = "posts";

                                Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid1, albumid, link, categoryid,
                                        dateshowed, trendingcount, likes, type);

                                photolist.add(photo);
                            }
                            homepostsadapter.notifyDataSetChanged();

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    });


                }
            });

        }

    }

    public String getTimestamphour() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddhhmm", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            MainActivity.home.setText(getResources().getString(R.string.Home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));

        } catch (NullPointerException e) {
            Log.d("ProfileFragment", "onStart: ");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        checkuser = false;
    }

}
