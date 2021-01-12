package com.wallpo.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.florent37.shapeofview.shapes.CircleView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wallpo.android.R;
import com.wallpo.android.getset.Comment;
import com.wallpo.android.profile.ProfileActivity;
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
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class commentsadapter extends RecyclerView.Adapter<commentsadapter.ViewHolder> {
    private Context context;
    private List<Comment> comments;

    public commentsadapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.comment_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Comment comment = comments.get(position);

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String datauserid = wallpouserdata.getString(comment.getUserid() + "id", "");

        if (!datauserid.isEmpty()) {

            holder.usernamestring = wallpouserdata.getString(comment.getUserid() + "username", "");
            holder.displaynamest = wallpouserdata.getString(comment.getUserid() + "displayname", "");
            holder.profilephotost = wallpouserdata.getString(comment.getUserid() + "profilephoto", "");

            holder.verified = wallpouserdata.getString(comment.getUserid() + "verified", "");
            holder.description = wallpouserdata.getString(comment.getUserid() + "description", "");
            holder.category = wallpouserdata.getString(comment.getUserid() + "category", "");
            holder.websites = wallpouserdata.getString(comment.getUserid() + "websites", "");
            holder.backphoto = wallpouserdata.getString(comment.getUserid() + "backphoto", "");
            holder.subscribed = wallpouserdata.getString(comment.getUserid() + "subscribed", "");
            holder.subscribers = wallpouserdata.getString(comment.getUserid() + "subscribers", "");


            try {

                Glide.with(context).load(holder.profilephotost).centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.profilepic)
                        .apply(new RequestOptions().override(290, 290))
                        .skipMemoryCache(false).into(holder.profilepic);

            } catch (IllegalArgumentException e) {
                Log.e("searchadapter", "onBindViewHolder: ", e);
            } catch (IllegalStateException e) {

                Log.e("act", "onBindViewHolder: ", e);
            }

            holder.name.setText(holder.displaynamest.replace(" ", "\n"));

        } else {
            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(comment.getUserid())).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.userinfo)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    Log.e(TAG, "onFailure: ", e);

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

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
                                                .skipMemoryCache(false).into(holder.profilepic);

                                    } catch (IllegalArgumentException e) {
                                        Log.e("searchadapter", "onBindViewHolder: ", e);
                                    } catch (IllegalStateException e) {

                                        Log.e("act", "onBindViewHolder: ", e);
                                    }
                                    holder.name.setText(holder.displaynamest);

                                    wallpouserdata.edit().putString(comment.getUserid() + "id", comment.getUserid()).apply();
                                    wallpouserdata.edit().putString(comment.getUserid() + "username", holder.usernamestring).apply();
                                    wallpouserdata.edit().putString(comment.getUserid() + "displayname", holder.displaynamest).apply();
                                    wallpouserdata.edit().putString(comment.getUserid() + "profilephoto", holder.profilephotost).apply();

                                    wallpouserdata.edit().putString(comment.getUserid() + "verified", holder.verified).apply();
                                    wallpouserdata.edit().putString(comment.getUserid() + "description", holder.description).apply();
                                    wallpouserdata.edit().putString(comment.getUserid() + "category", holder.category).apply();
                                    wallpouserdata.edit().putString(comment.getUserid() + "websites", holder.websites).apply();
                                    wallpouserdata.edit().putString(comment.getUserid() + "backphoto", holder.backphoto).apply();
                                    wallpouserdata.edit().putString(comment.getUserid() + "subscribed", holder.subscribed).apply();
                                    wallpouserdata.edit().putString(comment.getUserid() + "subscribers", holder.subscribers).apply();


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


        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);

                sharedPreferences.edit().putString("useridprofile", String.valueOf(comment.getUserid())).apply();
                sharedPreferences.edit().putString("usernameprofile", holder.usernamestring).apply();
                sharedPreferences.edit().putString("displaynameprofile", holder.displaynamest).apply();
                sharedPreferences.edit().putString("profilephotoprofile", holder.profilephotost).apply();
                sharedPreferences.edit().putString("verifiedprofile", holder.verified).apply();
                sharedPreferences.edit().putString("descriptionprofile", holder.description).apply();
                sharedPreferences.edit().putString("websitesprofile", holder.websites).apply();
                sharedPreferences.edit().putString("categoryprofile", holder.category).apply();
                sharedPreferences.edit().putString("backphotoprofile", holder.backphoto).apply();
                sharedPreferences.edit().putString("subscribedprofile", holder.subscribed).apply();
                sharedPreferences.edit().putString("subscriberprofile", holder.subscribers).apply();

                context.startActivity(i);

            }
        });

        holder.circleview.setOnClickListener(v -> {
            Intent i = new Intent(context, ProfileActivity.class);

            sharedPreferences.edit().putString("useridprofile", String.valueOf(comment.getUserid())).apply();
            sharedPreferences.edit().putString("usernameprofile", holder.usernamestring).apply();
            sharedPreferences.edit().putString("displaynameprofile", holder.displaynamest).apply();
            sharedPreferences.edit().putString("profilephotoprofile", holder.profilephotost).apply();
            sharedPreferences.edit().putString("verifiedprofile", holder.verified).apply();
            sharedPreferences.edit().putString("descriptionprofile", holder.description).apply();
            sharedPreferences.edit().putString("websitesprofile", holder.websites).apply();
            sharedPreferences.edit().putString("categoryprofile", holder.category).apply();
            sharedPreferences.edit().putString("backphotoprofile", holder.backphoto).apply();
            sharedPreferences.edit().putString("subscribedprofile", holder.subscribed).apply();
            sharedPreferences.edit().putString("subscriberprofile", holder.subscribers).apply();

            context.startActivity(i);

        });

        holder.comment.setText(comment.getComment());
        //  holder.date.setText(comment.getDatecreated());

        if (comment.getUserid().equals(id) || comment.getPhotosuserid().equals(id)) {
            holder.menu.setVisibility(View.VISIBLE);
            holder.menu.setOnClickListener(v -> {

                PopupMenu popupMenu = new PopupMenu(context, holder.menu);
                popupMenu.getMenuInflater().inflate(R.menu.story_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete) {
                        new MaterialAlertDialogBuilder(context)
                                .setTitle(context.getResources().getString(R.string.deletecomment))
                                .setMessage(context.getResources().getString(R.string.deletecommentnotice))
                                .setNegativeButton(context.getResources().getString(R.string.cancelbig), (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                })
                                .setPositiveButton(context.getResources().getString(R.string.joinbig), (dialog, i) -> {

                                    Toast.makeText(context, context.getResources().getString(R.string.pleasewait), Toast.LENGTH_SHORT).show();
                                    holder.mainlay.getLayoutParams().height = 0;
                                    holder.mainlay.getLayoutParams().width = 0;
                                    holder.mainlay.requestLayout();
                                    dialog.dismiss();

                                    final OkHttpClient client = new OkHttpClient.Builder().build();

                                    if (context.toString().contains("StoriesCommentActivity")) {

                                        RequestBody postData = new FormBody.Builder().add("id", String.valueOf(comment.getId()))
                                                .add("userid", comment.getUserid()).add("comment", comment.getComment())
                                                .add("photoid", comment.getPhotoid()).add("fcm", sharedPreferences.getString("fcmtoken", ""))
                                                .build();

                                        Request request = new Request.Builder()
                                                .url(URLS.deletestorycomment)
                                                .post(postData)
                                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                .build();

                                        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                ((Activity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(context, context.getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                final String data = response.body().string().replaceAll(",\\[]", "");

                                            }
                                        });

                                    } else {

                                        RequestBody postData = new FormBody.Builder()
                                                .add("id", String.valueOf(comment.getId()))
                                                .add("userid", comment.getUserid()).add("comment", comment.getComment())
                                                .add("photoid", comment.getPhotoid())
                                                .add("fcm", sharedPreferences.getString("fcmtoken", "")).build();

                                        Request request = new Request.Builder()
                                                .url(URLS.deletecomment)
                                                .post(postData)
                                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                .build();

                                        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                ((Activity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(context, context.getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                final String data = response.body().string().replaceAll(",\\[]", "");

                                                ((Activity) context).runOnUiThread(() -> {

                                                    if (data.trim().contains("notfound")) {
                                                        Toast.makeText(context, context.getResources().getString(R.string.errordeletingcomment), Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }

                                                    if (data.trim().contains("unauth")) {
                                                        Toast.makeText(context, context.getResources().getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                                                        updatecode.updateFCM(context);
                                                        dialog.dismiss();
                                                    }

                                                    if (data.trim().contains("successfully")) {
                                                        holder.mainlay.getLayoutParams().height = 0;
                                                        holder.mainlay.getLayoutParams().width = 0;
                                                        holder.mainlay.requestLayout();
                                                        dialog.dismiss();
                                                    }


                                                });

                                            }
                                        });

                                    }
                                })
                                .show();

                        return true;
                    }
                    return true;
                });
                popupMenu.show();

            });

        } else {
            holder.menu.setVisibility(View.GONE);
        }

        try {
            String month = updatecode.getDate(Long.parseLong(comment.getDatecreated()), "dd MMM yyyy HH:ss");
            String monthsno = "";

            if (month.contains("Jan") || month.contains("Jan")) {
                monthsno = "01";
            } else if (month.contains("Feb") || month.contains("feb")) {
                monthsno = "02";
            } else if (month.contains("Mar") || month.contains("mar")) {
                monthsno = "03";
            } else if (month.contains("Apr") || month.contains("apr")) {
                monthsno = "04";
            } else if (month.contains("May") || month.contains("may")) {
                monthsno = "05";
            } else if (month.contains("Jun") || month.contains("jun")) {
                monthsno = "06";
            } else if (month.contains("Jul") || month.contains("jul")) {
                monthsno = "07";
            } else if (month.contains("Aug") || month.contains("aug")) {
                monthsno = "08";
            } else if (month.contains("Sept") || month.contains("sept")) {
                monthsno = "09";
            } else if (month.contains("Oct") || month.contains("oct")) {
                monthsno = "10";
            } else if (month.contains("Nov") || month.contains("nov")) {
                monthsno = "11";
            } else if (month.contains("Dec") || month.contains("dec")) {
                monthsno = "12";
            } else {
                monthsno = "";
            }

            String date = updatecode.getDate(Long.parseLong(comment.getDatecreated()), "dd MMM yyyy HH:ss").substring(0, 2);
            String year = updatecode.getDate(Long.parseLong(comment.getDatecreated()), "dd MMM yyyy HH:ss").substring(7, 11);

            int timecal = Integer.parseInt(year + monthsno + date);

            int timecaal = Integer.parseInt(getTimestampfull()) - timecal;

            if (timecaal <= 1) {

                holder.date.setText(context.getResources().getString(R.string.today));

            } else if (timecaal > 1 && timecaal < 100) {

                holder.date.setText(timecaal + context.getResources().getString(R.string.dayshort));

            } else if (timecaal > 100 && timecaal < 10000) {

                String time = String.valueOf(timecaal).substring(0, 1);

                holder.date.setText(time + context.getResources().getString(R.string.monthshort));

            } else {

                holder.date.setText(comment.getDatecreated());
            }


        } catch (StringIndexOutOfBoundsException e) {

            holder.date.setText(comment.getDatecreated());

        }


        /*

        String photogen = comment.getDatecreated().replaceAll(" ", "");
        int timecaal = Integer.parseInt(getTimestampfull()) - timecal;

        if (timecaal <= 1) {

            holder.date.setText("Today");

        } else if (timecaal > 1 && timecaal < 100) {

            holder.date.setText(timecaal + " Days ago");

        } else if (timecaal > 100 && timecaal < 10000) {

            String time = String.valueOf(timecaal).substring(0, 1);

            holder.date.setText(time + " Months ago");

        } else {

            holder.date.setText(comment.getDatecreated());
        }
        */

        if (position == (getItemCount() - 1)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.progressbar.setVisibility(View.GONE);
                }
            }, 3000);
        } else {
            holder.progressbar.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, comment, date;
        public String usernamestring, displaynamest, profilephotost, verified, description, category, websites, backphoto, subscribed, subscribers;
        CircleView circleview;
        RelativeLayout mainlay;
        ImageView profilepic, menu;
        SpinKitView progressbar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            mainlay = itemView.findViewById(R.id.mainlay);
            circleview = itemView.findViewById(R.id.circleview);
            profilepic = itemView.findViewById(R.id.profilepic);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date);
            progressbar = itemView.findViewById(R.id.progressbar);
            menu = itemView.findViewById(R.id.menu);
            menu.setVisibility(View.GONE);

        }
    }

    public String getTimestampfull() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }


}
