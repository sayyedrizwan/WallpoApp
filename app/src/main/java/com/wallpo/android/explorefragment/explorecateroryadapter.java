package com.wallpo.android.explorefragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallpo.android.R;
import com.wallpo.android.getset.Category;
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

public class explorecateroryadapter extends RecyclerView.Adapter<explorecateroryadapter.ViewHolder> {
    private Context context;
    private List<Stories> Stories;

    public explorecateroryadapter(Context context, List<Stories> Stories) {
        this.context = context;
        this.Stories = Stories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.explore_adapter_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Stories stories = Stories.get(position);

        LinearLayoutManager linearLayoutManager;
        final List<Category> categoryList = new ArrayList<>();


        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerview.setLayoutManager(linearLayoutManager);

        holder.categoryadapter = new categoryadapter(context, categoryList);

        holder.recyclerview.setAdapter(holder.categoryadapter);

        final SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);

        try {
            JSONArray array = new JSONArray(sharepref.getString("category", ""));

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                String categoryid = object.getString("categoryid");
                String name = object.getString("name");
                String imagelink = object.getString("imagelink");

                Category categoryItem = new Category(categoryid, name, imagelink);
                categoryList.add(categoryItem);

                holder.categoryadapter.notifyDataSetChanged();

            }


        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        if (!sharepref.getString("categorytime", "").equals(getTimestamphour())) {

            OkHttpClient client = new OkHttpClient();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.categoryview)
                    .get()
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
                            sharepref.edit().putString("category", data).apply();
                            sharepref.edit().putString("categorytime", getTimestamphour()).apply();
                            categoryList.clear();

                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    String categoryid = object.getString("categoryid");
                                    String name = object.getString("name");
                                    String imagelink = object.getString("imagelink");

                                    Category categoryItem = new Category(categoryid, name, imagelink);
                                    categoryList.add(categoryItem);

                                    holder.categoryadapter.notifyDataSetChanged();

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
        private com.wallpo.android.explorefragment.categoryadapter categoryadapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerview = itemView.findViewById(R.id.recyclerview);

        }
    }


    public String getTimestamphour() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        return sdf.format(new Date());
    }

}
