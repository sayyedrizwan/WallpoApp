package com.wallpo.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wallpo.android.R;
import com.wallpo.android.getset.ChatUsers;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;
import static com.google.firebase.database.ServerValue.TIMESTAMP;

public class messageshareadapter extends RecyclerView.Adapter<messageshareadapter.ViewHolder> {
    private Context context;
    private List<ChatUsers> users;

    public messageshareadapter(Context context, List<ChatUsers> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.share_layout_user, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ChatUsers user = users.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        if (userid.equals(user.getChatuser())) {
            holder.mainlay.setVisibility(View.GONE);
        }

        String datauserid = wallpouserdata.getString(user.getChatuser() + "id", "");

        if (!datauserid.isEmpty()) {

            holder.usernamestring = wallpouserdata.getString(user.getChatuser() + "username", "");
            holder.displaynamest = wallpouserdata.getString(user.getChatuser() + "displayname", "");
            holder.profilephotost = wallpouserdata.getString(user.getChatuser() + "profilephoto", "");

            holder.verified = wallpouserdata.getString(user.getChatuser() + "verified", "");
            holder.description = wallpouserdata.getString(user.getChatuser() + "description", "");
            holder.category = wallpouserdata.getString(user.getChatuser() + "category", "");
            holder.websites = wallpouserdata.getString(user.getChatuser() + "websites", "");
            holder.backphoto = wallpouserdata.getString(user.getChatuser() + "backphoto", "");
            holder.subscribed = wallpouserdata.getString(user.getChatuser() + "subscribed", "");
            holder.subscribers = wallpouserdata.getString(user.getChatuser() + "subscribers", "");


            try {

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

            holder.displayname.setText(holder.displaynamest);

        } else {
            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(user.getChatuser()))
                    .build();

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

                                    holder.emailstring = object.getString("email").trim();
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
                                                .skipMemoryCache(false).into(holder.profileseenimg);

                                    } catch (IllegalArgumentException e) {
                                        Log.e("searchadapter", "onBindViewHolder: ", e);
                                    } catch (IllegalStateException e) {

                                        Log.e("act", "onBindViewHolder: ", e);
                                    }

                                    holder.displayname.setText(holder.displaynamest);

                                    wallpouserdata.edit().putString(user.getChatuser() + "id", user.getChatuser()).apply();
                                    wallpouserdata.edit().putString(user.getChatuser() + "username", holder.usernamestring).apply();
                                    wallpouserdata.edit().putString(user.getChatuser() + "displayname", holder.displaynamest).apply();
                                    wallpouserdata.edit().putString(user.getChatuser() + "profilephoto", holder.profilephotost).apply();

                                    wallpouserdata.edit().putString(user.getChatuser() + "verified", holder.verified).apply();
                                    wallpouserdata.edit().putString(user.getChatuser() + "description", holder.description).apply();
                                    wallpouserdata.edit().putString(user.getChatuser() + "category", holder.category).apply();
                                    wallpouserdata.edit().putString(user.getChatuser() + "websites", holder.websites).apply();
                                    wallpouserdata.edit().putString(user.getChatuser() + "backphoto", holder.backphoto).apply();
                                    wallpouserdata.edit().putString(user.getChatuser() + "subscribed", holder.subscribed).apply();
                                    wallpouserdata.edit().putString(user.getChatuser() + "subscribers", holder.subscribers).apply();


                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });
        }

        holder.sendmessage.setOnClickListener(v -> {

            if (holder.send) {
                Toast.makeText(context, context.getResources().getString(R.string.messagehassent), Toast.LENGTH_SHORT).show();
                return;
            }

            String value = "";
            try {
                if (Integer.parseInt(userid) > Integer.parseInt(user.getChatuser())) {
                    value = userid + "_" + user.getChatuser();
                } else {
                    value = user.getChatuser() + "_" + userid;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, context.getResources().getString(R.string.pressagain), Toast.LENGTH_SHORT).show();
            }

            if (Common.SEARCHSTART.equals("searched")) {
                updatecode.checkchatgroup(context);
            }
            String share = "";
            if (Common.SHARETYPE.equals("posts")) {
                share = "posts/" + Common.MESSAGE_DATA;
            }if (Common.SHARETYPE.equals("album")) {
                share = "album/" + Common.MESSAGE_DATA;
            }else if (Common.SHARETYPE.equals("user")){
                share = "user/" + Common.USER_ID;
            }

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", userid);
            hashMap.put("reciver", user.getChatuser());
            hashMap.put("message", "");
            hashMap.put("time", TIMESTAMP);
            hashMap.put("share", share);
            double random1 = Math.random() * (999 - 10 + 1) + 10;
            double random2 = Math.random() * (9999 - 10 + 1) + 10;

            String valuestamp = String.valueOf(random1).replace(".", "") + System.currentTimeMillis()
                    + String.valueOf(random2).replace(".", "");

            Log.d(TAG, "onClick: dygdd " + valuestamp);

            reference.child("chats").child(value).child(valuestamp).setValue(hashMap);

            DatabaseReference references = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> hashMaps = new HashMap<>();
            hashMaps.put("chatuser", user.getChatuser());
            hashMaps.put("status", "unseen");
            hashMaps.put("time", TIMESTAMP);

            references.child("userschat/" + userid + "/olduser/" + user.getChatuser()).setValue(hashMaps);

            holder.send = true;
            holder.displayname.setText("Sent");

        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public String emailstring, usernamestring, displaynamest, profilephotost, verified, description, category, websites, backphoto, subscribed, subscribers;
        ImageView profileseenimg;
        AppCompatTextView displayname;
        CardView sendmessage;
        Boolean send = false;
        RelativeLayout mainlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileseenimg = itemView.findViewById(R.id.profileseenimg);
            displayname = itemView.findViewById(R.id.displayname);
            sendmessage = itemView.findViewById(R.id.sendmessage);
            mainlay = itemView.findViewById(R.id.mainlay);

        }
    }

}
