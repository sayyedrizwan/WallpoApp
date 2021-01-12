package com.wallpo.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wallpo.android.R;
import com.wallpo.android.getset.User;
import com.wallpo.android.profile.ProfileActivity;

import java.util.List;

import static android.content.ContentValues.TAG;

public class subsaccountsadapter extends RecyclerView.Adapter<subsaccountsadapter.ViewHolder> {
    private Context context;
    private List<User> users;

    public subsaccountsadapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sub_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = users.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");


        try {

            Glide.with(context).load(user.getProfilephoto()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).into(holder.profilepic);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        holder.displayname.setText(user.getDisplayname());
        holder.username.setText("@ " + user.getUsername());

        holder.profilego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);

                sharedPreferences.edit().putString("useridprofile", String.valueOf(user.getUserid())).apply();
                sharedPreferences.edit().putString("usernameprofile", user.getUsername()).apply();
                sharedPreferences.edit().putString("displaynameprofile", user.getDisplayname()).apply();
                sharedPreferences.edit().putString("profilephotoprofile", user.getProfilephoto()).apply();
                sharedPreferences.edit().putString("verifiedprofile", user.getVerified()).apply();
                sharedPreferences.edit().putString("descriptionprofile", user.getDescription()).apply();
                sharedPreferences.edit().putString("websitesprofile", user.getWebsites()).apply();
                sharedPreferences.edit().putString("categoryprofile", user.getCategory()).apply();
                sharedPreferences.edit().putString("backphotoprofile", user.getBackphoto()).apply();
                sharedPreferences.edit().putString("subscribedprofile", user.getSubscribed()).apply();
                sharedPreferences.edit().putString("subscriberprofile", user.getSubscribers()).apply();

                context.startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout profilego;
        ImageView profilepic;
        AppCompatTextView displayname, username ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            displayname = itemView.findViewById(R.id.displayname);
            profilepic = itemView.findViewById(R.id.profilepic);
            profilego = itemView.findViewById(R.id.profilego);

        }
    }

}
