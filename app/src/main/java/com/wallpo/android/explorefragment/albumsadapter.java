package com.wallpo.android.explorefragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wallpo.android.R;
import com.wallpo.android.activity.AlbumsActivity;
import com.wallpo.android.getset.Albums;
import com.wallpo.android.utils.URLS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;

public class albumsadapter extends RecyclerView.Adapter<albumsadapter.ViewHolder> {
    private Context context;
    private List<Albums> Albums;
    private String userid, albumid;

    public albumsadapter(Context context, List<Albums> Albums) {
        this.context = context;
        this.Albums = Albums;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.album_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Albums albums = Albums.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        if (context.toString().contains(".ProfileActivity@")) {

            holder.displayname.setVisibility(View.GONE);

        }

        try {

            Glide.with(context).load(albums.getAlbumurl()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).into(holder.image);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        userid = albums.getUserid();
        albumid = albums.getAlbumid();

        holder.albumname.setText(albums.getAlbumname());

        if (!context.toString().contains(".ProfileActivity@")) {
            getuserinfo(albums.getUserid(), holder.displayname);
        }

        trendingimg(albums.getAlbumid(), holder.img1, holder.img2, holder.img3);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AlbumsActivity.class);

                sharedPreferences.edit().putString("albumid", albums.getAlbumid()).apply();
                sharedPreferences.edit().putString("albumname", albums.getAlbumname()).apply();
                sharedPreferences.edit().putString("albumdesc", albums.getAlbumdesc()).apply();
                sharedPreferences.edit().putString("albumurl", albums.getAlbumurl()).apply();
                sharedPreferences.edit().putString("albumuserid", albums.getUserid()).apply();
                sharedPreferences.edit().putString("albumcreated", albums.getAlbumcreated()).apply();

                context.startActivity(i);

            }
        });

    }

    private void trendingimg(final String albumid, final ImageView img1, final ImageView img2, final ImageView img3) {

        if (!String.valueOf(albumid).isEmpty()) {

            final SharedPreferences sharedPreferences = context.getSharedPreferences("albumwallpo", Context.MODE_PRIVATE);

            int type = 0;
            try {
                JSONArray array = new JSONArray(sharedPreferences.getString(albumid, ""));

                for (int i = 0; i < array.length(); i++) {

                    JSONObject Object = array.getJSONObject(i);

                    String imagepath = Object.getString("imagepath");

                    type = type + 1;

                    if (type < 4) {
                        if (type == 1) {
                            try {

                                Glide.with(context).load(imagepath).centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .apply(new RequestOptions().override(190, 190))
                                        .skipMemoryCache(false).into(img1);

                            } catch (IllegalArgumentException e) {
                                Log.e(TAG, "onBindViewHolder: ", e);
                            } catch (IllegalStateException e) {

                                Log.e(TAG, "onBindViewHolder: ", e);
                            }

                        } else if (type == 2) {
                            try {

                                Glide.with(context).load(imagepath).centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .apply(new RequestOptions().override(190, 190))
                                        .skipMemoryCache(false).into(img2);

                            } catch (IllegalArgumentException e) {
                                Log.e(TAG, "onBindViewHolder: ", e);
                            } catch (IllegalStateException e) {

                                Log.e(TAG, "onBindViewHolder: ", e);
                            }

                        } else if (type == 3) {
                            try {

                                Glide.with(context).load(imagepath).centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .apply(new RequestOptions().override(190, 190))
                                        .skipMemoryCache(false).into(img3);

                            } catch (IllegalArgumentException e) {
                                Log.e(TAG, "onBindViewHolder: ", e);
                            } catch (IllegalStateException e) {

                                Log.e(TAG, "onBindViewHolder: ", e);
                            }

                        }
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (sharedPreferences.getString(albumid, "").isEmpty()) {

                final OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder().add("albumid", String.valueOf(albumid)).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.trendingalbumimg)
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

                            sharedPreferences.edit().putString(albumid, data).apply();

                            int type1 = 0;

                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject Object = array.getJSONObject(i);

                                    String imagepath = Object.getString("imagepath");

                                    type1 = type1 + 1;

                                    if (type1 < 4) {
                                        if (type1 == 1) {
                                            try {

                                                Glide.with(context).load(imagepath).centerCrop()
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                        .skipMemoryCache(false).into(img1);

                                            } catch (IllegalArgumentException e) {
                                                Log.e(TAG, "onBindViewHolder: ", e);
                                            } catch (IllegalStateException e) {

                                                Log.e(TAG, "onBindViewHolder: ", e);
                                            }

                                        } else if (type1 == 2) {
                                            try {

                                                Glide.with(context).load(imagepath).centerCrop()
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                        .skipMemoryCache(false).into(img2);

                                            } catch (IllegalArgumentException e) {
                                                Log.e(TAG, "onBindViewHolder: ", e);
                                            } catch (IllegalStateException e) {

                                                Log.e(TAG, "onBindViewHolder: ", e);
                                            }

                                        } else if (type1 == 3) {
                                            try {

                                                Glide.with(context).load(imagepath).centerCrop()
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                        .skipMemoryCache(false).into(img3);

                                            } catch (IllegalArgumentException e) {
                                                Log.e(TAG, "onBindViewHolder: ", e);
                                            } catch (IllegalStateException e) {

                                                Log.e(TAG, "onBindViewHolder: ", e);
                                            }

                                        }
                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        });


                    }
                });
            }
        }



    }


    @Override
    public int getItemCount() {
        return Albums.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView albumname, displayname;
        CardView card;
        ImageView img1, img2, img3;
        String userid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            albumname = itemView.findViewById(R.id.albumname);
            displayname = itemView.findViewById(R.id.username);
            card = itemView.findViewById(R.id.card);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);


        }
    }

    private void getuserinfo(final String userid, final TextView displayname) {

        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String datauserid = wallpouserdata.getString(userid + "id", "");

        if (!datauserid.isEmpty()) {

            String usernamestring = wallpouserdata.getString(userid + "username", "");
            String displaynamest = wallpouserdata.getString(userid + "displayname", "");
            String profilephotost = wallpouserdata.getString(userid + "profilephoto", "");

            String verified = wallpouserdata.getString(userid + "verified", "");
            String description = wallpouserdata.getString(userid + "description", "");
            String category = wallpouserdata.getString(userid + "category", "");
            String websites = wallpouserdata.getString(userid + "websites", "");
            String backphoto = wallpouserdata.getString(userid + "backphoto", "");
            String subscribed = wallpouserdata.getString(userid + "subscribed", "");
            String subscribers = wallpouserdata.getString(userid + "subscribers", "");


            displayname.setText(displaynamest);


        } else {
            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(userid)).build();

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

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    String usernamestring = object.getString("username").trim();
                                    String displaynamest = object.getString("displayname").trim();
                                    String profilephotost = object.getString("profilephoto").trim();

                                    String verified = object.getString("verified").trim();
                                    String description = object.getString("description").trim();
                                    String category = object.getString("category").trim();
                                    String websites = object.getString("websites").trim();
                                    String backphoto = object.getString("backphoto").trim();
                                    String subscribed = object.getString("subscribed").trim();
                                    String subscribers = object.getString("subscribers").trim();

                                    displayname.setText(displaynamest);

                                    wallpouserdata.edit().putString(userid + "id", userid).apply();
                                    wallpouserdata.edit().putString(userid + "username", usernamestring).apply();
                                    wallpouserdata.edit().putString(userid + "displayname", displaynamest).apply();
                                    wallpouserdata.edit().putString(userid + "profilephoto", profilephotost).apply();

                                    wallpouserdata.edit().putString(userid + "verified", verified).apply();
                                    wallpouserdata.edit().putString(userid + "description", description).apply();
                                    wallpouserdata.edit().putString(userid + "category", category).apply();
                                    wallpouserdata.edit().putString(userid + "websites", websites).apply();
                                    wallpouserdata.edit().putString(userid + "backphoto", backphoto).apply();
                                    wallpouserdata.edit().putString(userid + "subscribed", subscribed).apply();
                                    wallpouserdata.edit().putString(userid + "subscribers", subscribers).apply();


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

}
