package com.wallpo.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.adapter.storiesadapter;
import com.wallpo.android.explorefragment.idsadapter;
import com.wallpo.android.explorefragment.risingartistsadapter;
import com.wallpo.android.getset.Photos;
import com.wallpo.android.getset.Stories;
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
import okhttp3.Response;

public class SearchFragment extends Fragment {

    RecyclerView recyclerviewids, recyclerviewposts, recyclerviewstories;
    CardView searchbar;
    EditText searchtext;
    private List<Photos> photolist = new ArrayList<>();
    private List<User> userlist = new ArrayList<>();
    private List<Stories> storielist = new ArrayList<>();
    LinearLayoutManager linearLayoutids, linearLayoutposts, linearLayoutstories;
    com.wallpo.android.explorefragment.idsadapter idsadapter;
    com.wallpo.android.explorefragment.risingartistsadapter risingartistsadapter;
    com.wallpo.android.adapter.storiesadapter storiesadapter;
    private ConcatAdapter merged;
    SpinKitView idloading, postsloading, storiesloading;
    RelativeLayout idrel, postslay, storieslay;

    private int totalItemCountid, firstVisibleItemid, visibleItemContid;
    private int page = 1;
    private int loadids = 0;
    private int previousTotalid;
    private boolean loadid = true;

    private int totalItemCountpt, firstVisibleItempt, visibleItemContpt;
    private int loadpt = 0;
    private int previousTotalpt;
    private boolean loadp = true;

    private int totalItemCountst, firstVisibleItemst, visibleItemContst;
    private int loadst = 0;
    private int previousTotalst;
    private boolean loads = true;

    AppCompatTextView nouserfoundtxt, nopoststxt, nostoriestxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerviewids = view.findViewById(R.id.recyclerviewids);
        recyclerviewposts = view.findViewById(R.id.recyclerviewposts);
        recyclerviewstories = view.findViewById(R.id.recyclerviewstories);
        searchbar = view.findViewById(R.id.searchbar);
        searchtext = view.findViewById(R.id.searchtext);
        idloading = view.findViewById(R.id.idloading);
        postsloading = view.findViewById(R.id.postsloading);
        storiesloading = view.findViewById(R.id.storiesloading);
        idrel = view.findViewById(R.id.idrel);
        idrel.setVisibility(View.GONE);
        postslay = view.findViewById(R.id.postslay);
        postslay.setVisibility(View.GONE);
        storieslay = view.findViewById(R.id.storieslay);
        storieslay.setVisibility(View.GONE);
        nouserfoundtxt = view.findViewById(R.id.nouserfoundtxt);
        nouserfoundtxt.setVisibility(View.GONE);
        nopoststxt = view.findViewById(R.id.nopoststxt);
        nopoststxt.setVisibility(View.GONE);
        nostoriestxt = view.findViewById(R.id.nostoriestxt);
        nostoriestxt.setVisibility(View.GONE);

        linearLayoutids = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutposts = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutstories = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerviewids.setLayoutManager(linearLayoutids);
        recyclerviewposts.setLayoutManager(linearLayoutposts);
        recyclerviewstories.setLayoutManager(linearLayoutstories);

        idsadapter = new idsadapter(getActivity(), userlist);
        risingartistsadapter = new risingartistsadapter(getActivity(), photolist);
        storiesadapter = new storiesadapter(getActivity(), storielist);

        recyclerviewids.setAdapter(idsadapter);
        recyclerviewposts.setAdapter(risingartistsadapter);
        recyclerviewstories.setAdapter(storiesadapter);


        updatecode.analyticsFirebase(getContext(), "visit_for_search", "visit_for_search");

        searchbar.setOnClickListener(v -> {
            startloadingSearch();

        });

        searchtext.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                startloadingSearch();

                return true;
            }
            return false;
        });

        recyclerviewids.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemContid = linearLayoutids.getChildCount();
                totalItemCountid = linearLayoutids.getItemCount();
                firstVisibleItemid = linearLayoutids.findLastVisibleItemPosition();

                if (dx > 0) {
                    if (loadid) {
                        if (totalItemCountid > previousTotalid) {
                            previousTotalid = totalItemCountid;
                            loadid = false;
                            page++;
                        }
                    }

                    if (!loadid && (firstVisibleItemid + visibleItemContid) >= totalItemCountid - 2) {
                        loadids = loadids + 20;
                        loadids(String.valueOf(loadids));
                        loadid = true;
                    }
                }
            }

        });


        recyclerviewposts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemContpt = linearLayoutposts.getChildCount();
                totalItemCountpt = linearLayoutposts.getItemCount();
                firstVisibleItempt = linearLayoutposts.findLastVisibleItemPosition();


                if (dx > 0) {
                    if (loadp) {
                        if (totalItemCountpt > previousTotalpt) {
                            previousTotalpt = totalItemCountpt;
                            loadp = false;
                            page++;
                        }
                    }

                    if (!loadp && (firstVisibleItempt + visibleItemContpt) >= totalItemCountpt - 2) {
                        loadpt = loadpt + 20;
                        loadposts(String.valueOf(loadpt));
                        loadp = true;
                    }
                }
            }

        });

        recyclerviewstories.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemContst = linearLayoutstories.getChildCount();
                totalItemCountst = linearLayoutstories.getItemCount();
                firstVisibleItemst = linearLayoutstories.findLastVisibleItemPosition();


                if (dx > 0) {
                    if (loads) {
                        if (totalItemCountst > previousTotalst) {
                            previousTotalst = totalItemCountst;
                            loads = false;
                            page++;
                        }
                    }

                    if (!loads && (firstVisibleItemst + visibleItemContst) >= totalItemCountst - 2) {
                        loadst = loadst + 20;
                        loadstories(String.valueOf(loadpt));
                        loads = true;
                    }
                }
            }

        });


        return view;
    }

    private void startloadingSearch() {
        if (searchtext.getText().toString().isEmpty()) {

            Toast.makeText(getContext(), getResources().getString(R.string.enterwhatwanttosearch), Toast.LENGTH_SHORT).show();
            return;
        }

        if (searchtext.getText().toString().length() < 3) {

            Toast.makeText(getContext(), getResources().getString(R.string.entervaildword), Toast.LENGTH_SHORT).show();
            return;
        }

        View view1 = getActivity().getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }


        updatecode.analyticsFirebase(getContext(), "searched_for", "searched_for_" + searchtext.getText().toString());

        photolist.clear();
        storielist.clear();
        userlist.clear();

        loadids("");
        loadposts("");
        loadstories("");

        idrel.setVisibility(View.VISIBLE);
        postslay.setVisibility(View.VISIBLE);
        storieslay.setVisibility(View.VISIBLE);
    }

    private void loadstories(String limit) {

        storielist.clear();

        final OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("limit", String.valueOf(limit))
                .add("search", searchtext.getText().toString()).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.storiessearch)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

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
            public void onResponse(Call call, Response response) throws IOException {
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
                            String userid = Object.getString("userid");
                            String caption = Object.getString("caption");
                            String type = Object.getString("type");
                            int likes = Object.getInt("likes");
                            String link = Object.getString("link");
                            String dateshowed = Object.getString("dateshowed");
                            String datecreated = Object.getString("datecreated");

                            Stories story = new Stories(storyid, likes, userid, link, caption, type, dateshowed, datecreated);
                            storielist.add(story);
                        }

                        storiesloading.setVisibility(View.GONE);
                        storiesadapter.notifyDataSetChanged();

                        if (storiesadapter.getItemCount() < 1){
                            recyclerviewstories.setVisibility(View.GONE);
                            nostoriestxt.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void loadposts(String limit) {

        final OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("limit", String.valueOf(limit))
                .add("search", searchtext.getText().toString()).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.photosearchlist)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        postsloading.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
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
                            String userid = Object.getString("userid");
                            String imagepath = Object.getString("imagepath");
                            String likes = Object.getString("likes");
                            int trendingcount = Object.getInt("trendingcount");

                            Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                    dateshowed, trendingcount, likes);
                            photolist.add(photo);

                        }

                        postsloading.setVisibility(View.GONE);
                        risingartistsadapter.notifyDataSetChanged();

                        if (risingartistsadapter.getItemCount() < 1){
                            recyclerviewposts.setVisibility(View.GONE);
                            nopoststxt.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });


    }

    private void loadids(String limit) {
        final OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("limit", String.valueOf(limit))
                .add("name", searchtext.getText().toString()).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.searchuser)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(() -> idloading.setVisibility(View.GONE));

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {

                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject Object = array.getJSONObject(i);

                            String userid = Object.getString("userid");
                            String username = Object.getString("username");
                            String verified = Object.getString("verified");
                            String description = Object.getString("description");
                            String displayname = Object.getString("displayname");
                            String category = Object.getString("category");

                            String websites = Object.getString("websites");
                            String profilephoto = Object.getString("profilephoto");
                            String backphoto = Object.getString("backphoto");
                            String subscribed = Object.getString("subscribed");
                            String subscribers = Object.getString("subscribers");

                            User users = new User(userid, "", "", username, "", verified, "", description, displayname, category, profilephoto,
                                    backphoto, websites, subscribed, subscribers);
                            userlist.add(users);
                        }

                        idloading.setVisibility(View.GONE);
                        idsadapter.notifyDataSetChanged();

                        if (idsadapter.getItemCount() < 1){
                            recyclerviewids.setVisibility(View.GONE);
                            nouserfoundtxt.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.Search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));


        } catch (NullPointerException e) {
            Log.d("ProfileFragment", "onStart: ");
        }

    }
}
