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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.florent37.shapeofview.shapes.CircleView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wallpo.android.R;
import com.wallpo.android.activity.ChatActivity;
import com.wallpo.android.getset.ChatUsers;
import com.wallpo.android.getset.Chats;
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
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class usermessageadapter extends RecyclerView.Adapter<usermessageadapter.ViewHolder> {
    private Context context;
    private List<ChatUsers> userList;

    public usermessageadapter(Context context, List<ChatUsers> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.message_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ChatUsers users = userList.get(position);

        if (users.getStatus().equals("seen")) {
            holder.seenlayout.setVisibility(View.VISIBLE);
        } else {
            holder.seenlayout.setVisibility(View.GONE);
        }

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        Log.d(TAG, "onBindViewHolder: " + id);

        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String datauserid = wallpouserdata.getString(users.getChatuser() + "id", "");

        if (!datauserid.isEmpty()) {

            holder.usernamestring = wallpouserdata.getString(users.getChatuser() + "username", "");
            holder.displaynamest = wallpouserdata.getString(users.getChatuser() + "displayname", "");
            holder.profilephotost = wallpouserdata.getString(users.getChatuser() + "profilephoto", "");

            holder.verified = wallpouserdata.getString(users.getChatuser() + "verified", "");
            holder.description = wallpouserdata.getString(users.getChatuser() + "description", "");
            holder.category = wallpouserdata.getString(users.getChatuser() + "category", "");
            holder.websites = wallpouserdata.getString(users.getChatuser() + "websites", "");
            holder.backphoto = wallpouserdata.getString(users.getChatuser() + "backphoto", "");
            holder.subscribed = wallpouserdata.getString(users.getChatuser() + "subscribed", "");
            holder.subscribers = wallpouserdata.getString(users.getChatuser() + "subscribers", "");


            try {

                Glide.with(context).load(holder.profilephotost).centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.profilepic)
                        .apply(new RequestOptions().override(290, 290))
                        .skipMemoryCache(false).into(holder.profilepic);

                Glide.with(context).load(holder.profilephotost).centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.profilepic)
                        .apply(new RequestOptions().override(290, 290))
                        .skipMemoryCache(false).into(holder.profileseenimg);

            } catch (IllegalArgumentException e) {
                Log.e("searchadapter", "onBindViewHolder: ", e);
            } catch (IllegalStateException e) {

                Log.e("act", "onBindViewHolder: ", e);
            }

            holder.name.setText(holder.displaynamest.replace(" ", "\n"));

        } else {
            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(users.getChatuser())).build();

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
                                            .skipMemoryCache(false).into(holder.profilepic);


                                    Glide.with(context).load(holder.profilephotost).centerInside()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .placeholder(R.mipmap.profilepic)
                                            .apply(new RequestOptions().override(290, 290))
                                            .skipMemoryCache(false).into(holder.profileseenimg);
                                } catch (IllegalArgumentException e) {
                                    Log.e("searchadapter", "onBindViewHolder: ", e);
                                } catch (IllegalStateException e) {

                                    Log.e("act", "onBindViewHolder: ", e);
                                }
                                holder.name.setText(holder.displaynamest);

                                wallpouserdata.edit().putString(users.getChatuser() + "id", users.getChatuser()).apply();
                                wallpouserdata.edit().putString(users.getChatuser() + "username", holder.usernamestring).apply();
                                wallpouserdata.edit().putString(users.getChatuser() + "displayname", holder.displaynamest).apply();
                                wallpouserdata.edit().putString(users.getChatuser() + "profilephoto", holder.profilephotost).apply();

                                wallpouserdata.edit().putString(users.getChatuser() + "verified", holder.verified).apply();
                                wallpouserdata.edit().putString(users.getChatuser() + "description", holder.description).apply();
                                wallpouserdata.edit().putString(users.getChatuser() + "category", holder.category).apply();
                                wallpouserdata.edit().putString(users.getChatuser() + "websites", holder.websites).apply();
                                wallpouserdata.edit().putString(users.getChatuser() + "backphoto", holder.backphoto).apply();
                                wallpouserdata.edit().putString(users.getChatuser() + "subscribed", holder.subscribed).apply();
                                wallpouserdata.edit().putString(users.getChatuser() + "subscribers", holder.subscribers).apply();


                            }

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    });


                }
            });
        }


        holder.mainlay.setOnClickListener(v -> {
            Intent i = new Intent(context, ChatActivity.class);

            sharedPreferences.edit().putString("useridprofile", String.valueOf(users.getChatuser())).apply();
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


        String value = "";
        try {
            if (Integer.parseInt(id) > Integer.parseInt(users.getChatuser())) {
                value = id + "_" + users.getChatuser();
            } else {
                value = users.getChatuser() + "_" + id;
            }
        } catch (NumberFormatException e) {
            ((Activity) context).finish();
            Toast.makeText(context, context.getResources().getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
        }

        Query reference = FirebaseDatabase.getInstance().getReference("chats").child(value).limitToLast(1);
        reference.orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);

                    if (chats.getReciver().equals(id) && chats.getSender().equals(users.getChatuser()) ||
                            chats.getReciver().equals(users.getChatuser()) && chats.getSender().equals(id)) {

                        if (chats.getMessage().length() > 10) {
                            holder.textmessage.setText(chats.getMessage().substring(0, 10) + "....");
                        } else {
                            holder.textmessage.setText(chats.getMessage());
                        }

                        if (chats.getMessage().isEmpty()){
                            if (!chats.getShare().isEmpty()){
                                String output = chats.getShare().substring(0, chats.getShare().indexOf('/'));
                                if (output.equals("user")) {
                                    holder.textmessage.setText(context.getResources().getString(R.string.shareduserprofile));
                                }else if (output.equals("album")){
                                    holder.textmessage.setText(context.getResources().getString(R.string.sharedalbums));
                                }else if (output.equals("posts")){
                                    holder.textmessage.setText(context.getResources().getString(R.string.sharedposts));
                                }else{
                                    holder.textmessage.setText(context.getResources().getString(R.string.sharednew));
                                }
                            }
                        }

                        if (updatecode.getTimestampday().contains(updatecode.getDate(chats.getTime(), "dd"))) {
                            holder.time.setText(updatecode.getDate(chats.getTime(), "hh:mm"));
                        } else {
                            holder.time.setText(updatecode.getDate(chats.getTime(), "dd/MM/yyyy hh:mm"));
                        }

                        if (!chats.getSender().equals(id)) {
                            Query reference = FirebaseDatabase.getInstance().getReference("userschat").child(users.getChatuser())
                                    .child("olduser").child(id);
                            reference.orderByChild("time").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        String status = dataSnapshot.child("status").getValue(String.class);
                                        long timestamp = dataSnapshot.child("time").getValue(Long.class);

                                        if (status.equals("unseen")) {
                                            holder.newlay.setVisibility(View.VISIBLE);
                                        } else {
                                            holder.newlay.setVisibility(View.GONE);
                                        }


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            holder.newlay.setVisibility(View.GONE);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        public String usernamestring, displaynamest, profilephotost, verified, description, category, websites, backphoto, subscribed, subscribers;
        CircleView circleview, seenlayout;
        CardView mainlay;
        ImageView profilepic, profileseenimg;
        AppCompatTextView textmessage;
        RelativeLayout newlay;
        AppCompatTextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            mainlay = itemView.findViewById(R.id.mainlay);
            circleview = itemView.findViewById(R.id.circleview);
            profilepic = itemView.findViewById(R.id.profilepic);
            textmessage = itemView.findViewById(R.id.textmessage);
            time = itemView.findViewById(R.id.time);
            newlay = itemView.findViewById(R.id.newlay);
            newlay.setVisibility(View.GONE);
            seenlayout = itemView.findViewById(R.id.seenlayout);
            seenlayout.setVisibility(View.GONE);
            profileseenimg = itemView.findViewById(R.id.profileseenimg);

        }
    }

    public String getTimestampfull() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }


}
