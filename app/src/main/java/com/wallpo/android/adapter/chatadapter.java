package com.wallpo.android.adapter;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bachors.wordtospan.WordToSpan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wallpo.android.R;
import com.wallpo.android.activity.HashtagActivity;
import com.wallpo.android.explorefragment.albumsadapter;
import com.wallpo.android.explorefragment.idsadapter;
import com.wallpo.android.explorefragment.risingartistsadapter;
import com.wallpo.android.getset.Albums;
import com.wallpo.android.getset.Chats;
import com.wallpo.android.getset.Photos;
import com.wallpo.android.getset.User;
import com.wallpo.android.profile.ProfileActivity;
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
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.google.firebase.database.ServerValue.TIMESTAMP;
import static com.wallpo.android.activity.MessageActivity.messagetype;
import static com.wallpo.android.utils.updatecode.getusernameid;

public class chatadapter extends RecyclerView.Adapter<chatadapter.ViewHolder> {
    private Context context;
    private List<Chats> chatsList;
    public static final int MSG_RIGHT_MESSAGE = 0;
    public static final int MSG_LEFT_MESSAGE = 1;

    public chatadapter(Context context, List<Chats> chatsList) {
        this.context = context;
        this.chatsList = chatsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == MSG_LEFT_MESSAGE) {
            view = inflater.inflate(R.layout.chat_left, null);
        } else {
            view = inflater.inflate(R.layout.chat_right, null);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        String useridprofile = sharedPreferences.getString("useridprofile", "");

        if (position == (getItemCount() - 1)) {
            switch (holder.getItemViewType()) {
                case MSG_RIGHT_MESSAGE:
                    if (messagetype.equals("olduser")) {
                        DatabaseReference refs = FirebaseDatabase.getInstance().getReference("userschat");
                        refs.child(useridprofile).child(messagetype).child(userid).child("status").setValue("unseen");
                    }

                    Log.d(TAG, "onBindViewHolder: right");
                    break;
                case MSG_LEFT_MESSAGE:

                    if (messagetype.equals("olduser")) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userschat");
                        ref.child(useridprofile).child(messagetype).child(userid).child("status").setValue("seen");
                        ref.child(useridprofile).child(messagetype).child(userid).child("time").setValue(TIMESTAMP);
                    }
                    Log.d(TAG, "onBindViewHolder: left");
                    break;
            }
        } else {

        }

        final Chats chats = chatsList.get(position);

        WordToSpan link = new WordToSpan();
        link.setColorTAG(context.getResources().getColor(R.color.texthashtag))
                .setColorURL(context.getResources().getColor(R.color.white))
                .setColorPHONE(context.getResources().getColor(R.color.white))
                .setColorMAIL(context.getResources().getColor(R.color.white))
                .setColorMENTION(context.getResources().getColor(R.color.texthashtag))
                .setColorCUSTOM(context.getResources().getColor(R.color.white))
                .setUnderlineURL(false)
                .setLink(chats.getMessage().trim())
                .into(holder.message)
                .setClickListener(new WordToSpan.ClickListener() {
                    @Override
                    public void onClick(String type, String text) {
                        // type: "tag", "mail", "url", "phone", "mention" or "custom"

                        if (type.trim().equals("tag")) {

                            sharedPreferences.edit().putString("hashtagtext", text).apply();
                            context.startActivity(new Intent(context, HashtagActivity.class));

                        } else if (type.trim().equals("mention")) {

                            getusernameid(context, text.trim());

                        } else if (type.trim().equals("mail")) {

                            Log.d(TAG, "onClick: mail");
                        } else if (type.trim().equals("url")) {

                            Log.d(TAG, "onClick: custom");
                        } else if (type.trim().equals("custom")) {
                            Log.d(TAG, "onClick: custom");
                        } else {
                            Log.d(TAG, "onClick: else");
                        }

                    }
                });


        final String id = sharedPreferences.getString("wallpouserid", "");

        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String datauserid = wallpouserdata.getString(chats.getSender() + "id", "");

        if (!datauserid.isEmpty()) {

            holder.usernamestring = wallpouserdata.getString(chats.getSender() + "username", "");
            holder.displaynamest = wallpouserdata.getString(chats.getSender() + "displayname", "");
            holder.profilephotost = wallpouserdata.getString(chats.getSender() + "profilephoto", "");

            holder.verified = wallpouserdata.getString(chats.getSender() + "verified", "");
            holder.description = wallpouserdata.getString(chats.getSender() + "description", "");
            holder.category = wallpouserdata.getString(chats.getSender() + "category", "");
            holder.websites = wallpouserdata.getString(chats.getSender() + "websites", "");
            holder.backphoto = wallpouserdata.getString(chats.getSender() + "backphoto", "");
            holder.subscribed = wallpouserdata.getString(chats.getSender() + "subscribed", "");
            holder.subscribers = wallpouserdata.getString(chats.getSender() + "subscribers", "");


            try {

                Glide.with(context).load(holder.profilephotost).centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.profilepic)
                        .apply(new RequestOptions().override(290, 290))
                        .skipMemoryCache(false).into(holder.profileimg);

            } catch (IllegalArgumentException e) {
                Log.e("searchadapter", "onBindViewHolder: ", e);
            } catch (IllegalStateException e) {

                Log.e("act", "onBindViewHolder: ", e);
            }


        } else {
            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(chats.getSender()))
                    .build();

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
                                            .skipMemoryCache(false).into(holder.profileimg);

                                } catch (IllegalArgumentException e) {
                                    Log.e("searchadapter", "onBindViewHolder: ", e);
                                } catch (IllegalStateException e) {

                                    Log.e("act", "onBindViewHolder: ", e);
                                }

                                wallpouserdata.edit().putString(chats.getSender() + "id", chats.getSender()).apply();
                                wallpouserdata.edit().putString(chats.getSender() + "username", holder.usernamestring).apply();
                                wallpouserdata.edit().putString(chats.getSender() + "displayname", holder.displaynamest).apply();
                                wallpouserdata.edit().putString(chats.getSender() + "profilephoto", holder.profilephotost).apply();

                                wallpouserdata.edit().putString(chats.getSender() + "verified", holder.verified).apply();
                                wallpouserdata.edit().putString(chats.getSender() + "description", holder.description).apply();
                                wallpouserdata.edit().putString(chats.getSender() + "category", holder.category).apply();
                                wallpouserdata.edit().putString(chats.getSender() + "websites", holder.websites).apply();
                                wallpouserdata.edit().putString(chats.getSender() + "backphoto", holder.backphoto).apply();
                                wallpouserdata.edit().putString(chats.getSender() + "subscribed", holder.subscribed).apply();
                                wallpouserdata.edit().putString(chats.getSender() + "subscribers", holder.subscribers).apply();


                            }

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    });


                }
            });
        }

        if (!chats.getShare().isEmpty()) {

            holder.setIsRecyclable(false);

            String ch = chats.getShare().substring(chats.getShare().lastIndexOf("/") + 1);
            if (chats.getShare().contains("posts")) {
                holder.message.setVisibility(View.GONE);

                List<Photos> photolist = new ArrayList<>();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                holder.recyclerview.setLayoutManager(linearLayoutManager);
                risingartistsadapter risingartistsadapter = new risingartistsadapter(context, photolist);
                holder.recyclerview.setAdapter(risingartistsadapter);

                OkHttpClient client = new OkHttpClient();


                RequestBody postData = new FormBody.Builder().add("photoid", ch)
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.photoinfo)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.message.setVisibility(View.VISIBLE);
                                holder.message.setText(context.getResources().getString(R.string.errorloadingpost));
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
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


                                        Photos photos = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                                dateshowed, trendingcount, likes);
                                        photolist.add(photos);

                                    }
                                    holder.recyclerview.setVisibility(View.VISIBLE);
                                    risingartistsadapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

            } else if (chats.getShare().contains("user")) {
                holder.message.setVisibility(View.GONE);
                String chs = chats.getShare().substring(chats.getShare().lastIndexOf("/") + 1);

                List<User> userList = new ArrayList<>();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                holder.recyclerview.setLayoutManager(linearLayoutManager);
                idsadapter idsadapter = new idsadapter(context, userList);
                holder.recyclerview.setAdapter(idsadapter);

                OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder()
                        .add("username", String.valueOf(chs)).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.getuserid)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.message.setVisibility(View.VISIBLE);

                                holder.message.setText(context.getResources().getString(R.string.errorloadinguserprofile));
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    JSONArray array = new JSONArray(data);

                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);
                                        String userid = object.getString("userid");
                                        String username = object.getString("username");
                                        String verified = object.getString("verified");
                                        String description = object.getString("description");
                                        String displayname = object.getString("displayname");
                                        String category = object.getString("category");
                                        String websites = object.getString("websites");
                                        String profilephoto = object.getString("profilephoto");
                                        String backphoto = object.getString("backphoto");
                                        String subscribed = object.getString("subscribed");
                                        String subscribers = object.getString("subscribers");

                                        User users = new User(userid, "", "", username, "", verified, "", description, displayname, category, profilephoto,
                                                backphoto, websites, subscribed, subscribers);
                                        userList.add(users);

                                    }

                                    holder.recyclerview.setVisibility(View.VISIBLE);
                                    idsadapter.notifyDataSetChanged();
                                } catch (
                                        JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                });

            } else if (chats.getShare().contains("album")) {

                holder.message.setVisibility(View.GONE);
                String chs = chats.getShare().substring(chats.getShare().lastIndexOf("/") + 1);

                final List<Albums> albumsList = new ArrayList<>();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                holder.recyclerview.setLayoutManager(linearLayoutManager);

                albumsadapter albumsadapter = new albumsadapter(context, albumsList);

                holder.recyclerview.setAdapter(albumsadapter);

                OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder().add("albumid", String.valueOf(chs)).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.albuminfo)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.message.setVisibility(View.VISIBLE);
                                holder.message.setText(context.getResources().getString(R.string.errorloadingpost));
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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
                                    }

                                    holder.recyclerview.setVisibility(View.VISIBLE);
                                    albumsadapter.notifyDataSetChanged();

                                } catch (
                                        JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                });


            } else {
                holder.message.setVisibility(View.VISIBLE);
                holder.message.setText(context.getResources().getString(R.string.updateyourapp));
            }
        }

        holder.profilecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);

                sharedPreferences.edit().putString("useridprofile", String.valueOf(chats.getSender())).apply();
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

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public String usernamestring, displaynamest, profilephotost, verified, description, category, websites, backphoto, subscribed, subscribers;
        ImageView profileimg;
        ImageView profileseenimg;
        TextView message;
        CardView profilecard;
        RecyclerView recyclerview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileimg = itemView.findViewById(R.id.profileimg);
            message = itemView.findViewById(R.id.message);
            profilecard = itemView.findViewById(R.id.cardView2);
            recyclerview = itemView.findViewById(R.id.recyclerview);
            recyclerview.setVisibility(View.GONE);
        }
    }

    public String getTimestampfull() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public int getItemViewType(int position) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        if (chatsList.get(position).getSender().equals(id)) {
            return MSG_RIGHT_MESSAGE;
        } else {
            return MSG_LEFT_MESSAGE;
        }

    }
}
