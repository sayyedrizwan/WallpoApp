package com.wallpo.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.wallpo.android.R;
import com.wallpo.android.activity.ViewPostsActivity;
import com.wallpo.android.roomdbs.Notificationdb;
import com.wallpo.android.roomdbs.Roomdb;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


public class notificationadapter extends RecyclerView.Adapter<notificationadapter.PhotoViewHolder> {

    private static final String TAG = "accountadapter";
    private Context context;
    private List<Notificationdb> notificationList;
    private Roomdb database;

    public notificationadapter(Context context, List<Notificationdb> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_list, null);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder holder, int position) {
        Notificationdb notification = notificationList.get(position);
        database = Roomdb.getInstance(context);

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String datauserid = wallpouserdata.getString(notification.theuserid + "id", "");

        if (!datauserid.isEmpty()) {

            holder.usernamestring = wallpouserdata.getString(notification.theuserid + "username", "");
            holder.displaynamest = wallpouserdata.getString(notification.theuserid + "displayname", "");
            holder.profilephotost = wallpouserdata.getString(notification.theuserid + "profilephoto", "");

            holder.verified = wallpouserdata.getString(notification.theuserid + "verified", "");
            holder.description = wallpouserdata.getString(notification.theuserid + "description", "");
            holder.category = wallpouserdata.getString(notification.theuserid + "category", "");
            holder.websites = wallpouserdata.getString(notification.theuserid + "websites", "");
            holder.backphoto = wallpouserdata.getString(notification.theuserid + "backphoto", "");
            holder.subscribed = wallpouserdata.getString(notification.theuserid + "subscribed", "");
            holder.subscribers = wallpouserdata.getString(notification.theuserid + "subscribers", "");


            try {

                Glide.with(context).load(holder.profilephotost).centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.profilepic)
                        .apply(new RequestOptions().override(290, 290))
                        .skipMemoryCache(false).into(holder.userpics);

            } catch (IllegalArgumentException e) {
                Log.e("searchadapter", "onBindViewHolder: ", e);
            } catch (IllegalStateException e) {

                Log.e("act", "onBindViewHolder: ", e);
            }

            holder.newposts.setText("New Post from " + holder.displaynamest);

        } else {
            final OkHttpClient client = new OkHttpClient();
            RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(notification.theuserid)).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.userinfo)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    Log.e(TAG, "onFailure: ", e);

                }

                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    ((Activity) context).runOnUiThread(() -> {

                        try {
                            JSONArray array = new JSONArray(data);


                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                holder.usernamestring = object.getString("username").trim();
                                holder.displaynamest = object.getString("displayname").trim();
                                holder.profilephotost = object.getString("profilephoto").trim();

                                holder.verified = object.getString("verified").trim();
                                holder.description = object.getString("description").trim();
                                holder.category = object.getString("category").trim();
                                holder.websites = object.getString("websites").trim();
                                holder.backphoto = object.getString("backphoto").trim();
                                holder.subscribed = object.getString("subscribed").trim();
                                holder.subscribers = object.getString("subscribers").trim();


                                try {

                                    Glide.with(context).load(holder.profilephotost).centerInside()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .placeholder(R.mipmap.profilepic)
                                            .apply(new RequestOptions().override(290, 290))
                                            .skipMemoryCache(false).into(holder.userpics);

                                } catch (IllegalArgumentException e) {
                                    Log.e("searchadapter", "onBindViewHolder: ", e);
                                } catch (IllegalStateException e) {

                                    Log.e("act", "onBindViewHolder: ", e);
                                }

                                holder.newposts.setText("New Post from " + holder.displaynamest);

                                wallpouserdata.edit().putString(notification.theuserid + "id", notification.theuserid).apply();
                                wallpouserdata.edit().putString(notification.theuserid + "username", holder.usernamestring).apply();
                                wallpouserdata.edit().putString(notification.theuserid + "displayname", holder.displaynamest).apply();
                                wallpouserdata.edit().putString(notification.theuserid + "profilephoto", holder.profilephotost).apply();

                                wallpouserdata.edit().putString(notification.theuserid + "verified", holder.verified).apply();
                                wallpouserdata.edit().putString(notification.theuserid + "description", holder.description).apply();
                                wallpouserdata.edit().putString(notification.theuserid + "category", holder.category).apply();
                                wallpouserdata.edit().putString(notification.theuserid + "websites", holder.websites).apply();
                                wallpouserdata.edit().putString(notification.theuserid + "backphoto", holder.backphoto).apply();
                                wallpouserdata.edit().putString(notification.theuserid + "subscribed", holder.subscribed).apply();
                                wallpouserdata.edit().putString(notification.theuserid + "subscribers", holder.subscribers).apply();


                            }

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    });

                }
            });
        }

        holder.captions.setText(notification.caption);


        try {

            Glide.with(context).load(notification.imagepath).centerInside()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(new RequestOptions().override(290, 290))
                    .skipMemoryCache(false).into(holder.userpost);

        } catch (IllegalArgumentException e) {
            Log.e("searchadapter", "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e("act", "onBindViewHolder: ", e);
        }

        String date = updatecode.getDate(Long.parseLong(notification.timestamp), "YYYY-MM-dd hh:mm:ss");

        Log.d(TAG, "onBindViewHolder: notification " + date);

        String photogen = date.replaceAll("-", "");
        String photogen2 = photogen.substring(0, 8);
        int timecal = Integer.parseInt(photogen2);
        int timecaal = Integer.parseInt(getTimestamp()) - timecal;

        if (timecaal <= 1) {

            holder.date.setText(context.getResources().getString(R.string.Today));

        } else if (timecaal > 1 && timecaal < 100) {

            holder.date.setText(timecaal + context.getResources().getString(R.string.daysago));

        } else if (timecaal > 100 && timecaal < 10000) {

            String time = String.valueOf(timecaal).substring(0, 1);

            holder.date.setText(time + context.getResources().getString(R.string.monthsago));

        } else {

            holder.date.setText(notification.timestamp);
        }

        holder.mainlay.setOnLongClickListener(view -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(context.getResources().getString(R.string.deletenotification))
                    .setMessage(context.getResources().getString(R.string.deletenotification))
                    .setNeutralButton(context.getResources().getString(R.string.cancelbig), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    }).setPositiveButton(context.getResources().getString(R.string.deletebig), (dialogInterface, i) -> {
                Roomdb db = Roomdb.getInstance(context.getApplicationContext());
                db.mainDao().deletePosts(notification.uid, notification.postid);
                Toast.makeText(context, "" + context.getResources().getString(R.string.notificationdeleted), Toast.LENGTH_SHORT).show();

                dialogInterface.dismiss();
                holder.mainlay.getLayoutParams().height = 0;
                holder.mainlay.getLayoutParams().width = 0;
                holder.mainlay.requestLayout();

            }).show();

            return false;
        });

        holder.mainlay.setOnClickListener(view -> {

            if (!holder.clickOn) {
                return;
            }

            holder.loginbar.setVisibility(View.VISIBLE);

            holder.clickOn = false;

            OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(notification.postid)).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.photoinfo)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ((Activity) context).runOnUiThread(() -> {

                        holder.clickOn = true;

                        holder.loginbar.setVisibility(View.GONE);
                        Snackbar.make(holder.mainlay, context.getResources().getString(R.string.connectionerrortryagain), Snackbar.LENGTH_LONG)
                                .show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");
                    ((Activity) context).runOnUiThread(() -> {
                        holder.clickOn = true;

                        holder.loginbar.setVisibility(View.GONE);
                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject Object = array.getJSONObject(i);

                                String photoid = Object.getString("photoid");
                                String caption = Object.getString("caption");
                                String categoryid = Object.getString("categoryid");
                                String albumid = Object.getString("albumid");
                                String datecreated = Object.getString("datecreated");
                                String dateshowed = Object.getString("dateshowed");
                                String links = Object.getString("link");
                                String userid = Object.getString("userid");
                                String imagepath = Object.getString("imagepath");
                                String likes = Object.getString("likes");
                                String trendingcount = Object.getString("trendingcount");
                                String SEO = Object.getString("SEO");


                                Intent intent = new Intent(context, ViewPostsActivity.class);
                                SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);
                                SharedPreferences.Editor editor = sharepref.edit();

                                editor.putString("photoid", String.valueOf(photoid));
                                editor.putString("useridprofile", String.valueOf(userid));
                                editor.putString("caption", caption);
                                editor.putString("categoryid", categoryid);
                                editor.putString("albumid", albumid);
                                editor.putString("datecreated", datecreated);
                                editor.putString("dateshowed", dateshowed);
                                editor.putString("link", links);
                                editor.putString("userid", userid);
                                editor.putString("imagepath", imagepath);
                                editor.putString("trendingcount", String.valueOf(trendingcount));
                                editor.putString("likes", likes);
                                editor.apply();

                                context.startActivity(intent);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        });

    }


    @Override
    public int getItemCount() {
        return this.notificationList.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        public String usernamestring, displaynamest, profilephotost, verified, description, category, websites, backphoto, subscribed, subscribers;
        AppCompatImageView userpics, userpost;
        AppCompatTextView newposts, captions, date;
        SpinKitView loginbar;
        CardView mainlay;
        Boolean clickOn = true;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            userpics = itemView.findViewById(R.id.userpics);
            newposts = itemView.findViewById(R.id.newposts);
            captions = itemView.findViewById(R.id.captions);
            userpost = itemView.findViewById(R.id.userpost);
            date = itemView.findViewById(R.id.date);
            mainlay = itemView.findViewById(R.id.mainlay);
            loginbar = itemView.findViewById(R.id.loginbar);
            loginbar.setVisibility(View.GONE);

        }

    }


    public String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
