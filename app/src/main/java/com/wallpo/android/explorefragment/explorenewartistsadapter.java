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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.textfield.TextInputLayout;
import com.wallpo.android.R;
import com.wallpo.android.getset.Photos;
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
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class explorenewartistsadapter extends RecyclerView.Adapter<explorenewartistsadapter.ViewHolder> {
    private Context context;
    private List<Stories> Stories;
    public static TextView noticeexplore;
    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = -10;
    private int previousTotal;
    private boolean load = true;

    public explorenewartistsadapter(Context context, List<Stories> Stories) {
        this.context = context;
        this.Stories = Stories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.explore_posts_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Stories stories = Stories.get(position);

        holder.noticeexplore.setText(context.getResources().getString(R.string.risingartists));

        final LinearLayoutManager linearLayoutManager;
        final List<Photos> postsList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerview.setLayoutManager(linearLayoutManager);

        holder.risingartistsadapter = new risingartistsadapter(context, postsList);

        holder.recyclerview.setAdapter(holder.risingartistsadapter);

        SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);

        holder.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        loadartistsposts(holder.spin_kit, postsList, holder.risingartistsadapter, String.valueOf(loadno));
                        load = true;
                    }
                }
            }

        });


        loadartistsposts(holder.spin_kit, postsList, holder.risingartistsadapter, "");

    }

    private void loadartistsposts(final SpinKitView spin_kit, final List<Photos> postsList, final risingartistsadapter risingartistsadapter, String limit) {
        OkHttpClient client = new OkHttpClient();


        RequestBody postData = new FormBody.Builder().add("limit", limit).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.newartistsposts)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("explorepostsadapter", "onFailure: ", e);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spin_kit.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                                postsList.add(photo);
                            }

                            spin_kit.setVisibility(View.GONE);
                            risingartistsadapter.notifyDataSetChanged();


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
        TextView noticeexplore;
        private risingartistsadapter risingartistsadapter;
        SpinKitView spin_kit;
        CardView filter;
        TextInputLayout textField;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerview = itemView.findViewById(R.id.recyclerview);
            noticeexplore = itemView.findViewById(R.id.noticeexplore);
            spin_kit = itemView.findViewById(R.id.spin_kit);
            filter = itemView.findViewById(R.id.filter);
            filter.setVisibility(View.GONE);
            textField = itemView.findViewById(R.id.textField);
            textField.setVisibility(View.GONE);

        }
    }


    public String getTimestamphour() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        return sdf.format(new Date());
    }

}
