package com.wallpo.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.getset.User;
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
import static com.facebook.FacebookSdk.getApplicationContext;

public class useraccountsadapter extends RecyclerView.Adapter<useraccountsadapter.ViewHolder> {
    private Context context;
    private List<User> users;

    public useraccountsadapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.account_list_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = users.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String datauserid = wallpouserdata.getString(user.getUserid() + "id", "");

        if (userid.equals(user.getUserid())) {
            holder.gochange.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(v -> {

            new MaterialAlertDialogBuilder(context)
                    .setTitle(context.getResources().getString(R.string.removeaccount))
                    .setMessage(context.getResources().getString(R.string.joincommunity))
                    .setNegativeButton(context.getResources().getString(R.string.cancelbig), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .setPositiveButton(context.getResources().getString(R.string.deletebig), (dialogInterface, i) -> {

                        SharedPreferences sharedaccounts = getApplicationContext().getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);
                        String addaccounts = sharedaccounts.getString("accounts", "");
                        addaccounts = addaccounts.replace(user.getUserid() + ", ", "");

                        sharedaccounts.edit().putString("accounts", addaccounts).apply();

                        if (userid.equals(user.getUserid())) {

                            if (!sharedaccounts.getString("accounts", "").trim().isEmpty()){

                                String str = sharedaccounts.getString("accounts" , "");
                                String kept = str.substring(0, str.indexOf(","));

                                SharedPreferences preferences = getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();

                                sharedPreferences.edit().putString("wallpouserid", kept.replace("," , "")).apply();

                                Intent intent = new Intent(context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                ((Activity) context).finish();

                            }else {
                                SharedPreferences preferences = getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();

                                Intent intent = new Intent(context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                ((Activity) context).finish();

                            }

                        } else {

                            holder.cardview.getLayoutParams().width = 0;
                            holder.cardview.getLayoutParams().height = 0;

                            holder.cardview.requestLayout();

                        }

                    })
                    .show();

        });

        SharedPreferences sharedaccounts = context.getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);

        final OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(user.getUserid()))
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
                                            .skipMemoryCache(false).into(holder.profileimg);

                                    Glide.with(context).load(holder.backphoto).centerInside()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .skipMemoryCache(false).into(holder.backimg);

                                } catch (IllegalArgumentException e) {
                                    Log.e("searchadapter", "onBindViewHolder: ", e);
                                } catch (IllegalStateException e) {

                                    Log.e("act", "onBindViewHolder: ", e);
                                }
                                holder.name.setText(holder.displaynamest);
                                holder.email.setText(holder.emailstring);

                                wallpouserdata.edit().putString(user.getUserid() + "id", user.getUserid()).apply();
                                wallpouserdata.edit().putString(user.getUserid() + "username", holder.usernamestring).apply();
                                wallpouserdata.edit().putString(user.getUserid() + "displayname", holder.displaynamest).apply();
                                wallpouserdata.edit().putString(user.getUserid() + "profilephoto", holder.profilephotost).apply();

                                wallpouserdata.edit().putString(user.getUserid() + "verified", holder.verified).apply();
                                wallpouserdata.edit().putString(user.getUserid() + "description", holder.description).apply();
                                wallpouserdata.edit().putString(user.getUserid() + "category", holder.category).apply();
                                wallpouserdata.edit().putString(user.getUserid() + "websites", holder.websites).apply();
                                wallpouserdata.edit().putString(user.getUserid() + "backphoto", holder.backphoto).apply();
                                wallpouserdata.edit().putString(user.getUserid() + "subscribed", holder.subscribed).apply();
                                wallpouserdata.edit().putString(user.getUserid() + "subscribers", holder.subscribers).apply();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });

        holder.gochange.setOnClickListener(v -> {
            sharedPreferences.edit().putString("wallpouserid", user.getUserid().replace(",", "")).apply();
            sharedPreferences.edit().putString("usernameuser", holder.usernamestring).apply();
            sharedPreferences.edit().putString("displaynameuser", holder.displaynamest).apply();
            sharedPreferences.edit().putString("profilephotouser", holder.profilephotost).apply();
            sharedPreferences.edit().putString("verifieduser", holder.verified).apply();

            sharedPreferences.edit().putString("descriptionuser", holder.description).apply();
            sharedPreferences.edit().putString("categoryuser", holder.category).apply();
            sharedPreferences.edit().putString("websitesuser", holder.websites).apply();
            sharedPreferences.edit().putString("backphotouser", holder.backphoto).apply();
            sharedPreferences.edit().putString("subscribeduser", holder.subscribed).apply();
            sharedPreferences.edit().putString("subscribersuser", holder.subscribers).apply();
            sharedPreferences.edit().putString("emailid", holder.emailstring).apply();
            sharedPreferences.edit().putString("profilepragdata", "").apply();

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            ((Activity) context).finish();

        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public String emailstring, usernamestring, displaynamest, profilephotost, verified, description, category, websites, backphoto, subscribed, subscribers;
        ImageView profileimg, backimg;
        TextView name, email;
        FloatingActionButton gochange;
        CardView delete, cardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileimg = itemView.findViewById(R.id.profileimg);
            name = itemView.findViewById(R.id.name);
            backimg = itemView.findViewById(R.id.backimg);
            gochange = itemView.findViewById(R.id.gochange);
            email = itemView.findViewById(R.id.email);
            delete = itemView.findViewById(R.id.delete);
            cardview = itemView.findViewById(R.id.cardview);

        }
    }

}
