package com.wallpo.android.explorefragment;

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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wallpo.android.R;
import com.wallpo.android.getset.User;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.utils.updatecode;

import java.util.List;

import static android.content.ContentValues.TAG;

public class idsadapter extends RecyclerView.Adapter<idsadapter.ViewHolder> {
    private Context context;
    private List<User> Users;

    public idsadapter(Context context, List<User> Users) {
        this.context = context;
        this.Users = Users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ids_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User users = Users.get(position);

        try {

            Glide.with(context).load(users.getProfilephoto()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).into(holder.profileimg);

            Glide.with(context).load(users.getBackphoto()).centerInside()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).into(holder.backimg);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        holder.subscribers.setText(updatecode.daynumber(users.getSubscribers()));

        holder.username.setText(users.getDisplayname());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);

                sharedPreferences.edit().putString("useridprofile", String.valueOf(users.getUserid())).apply();
                sharedPreferences.edit().putString("usernameprofile", users.getUsername()).apply();
                sharedPreferences.edit().putString("displaynameprofile", users.getDisplayname()).apply();
                sharedPreferences.edit().putString("profilephotoprofile", users.getProfilephoto()).apply();
                sharedPreferences.edit().putString("verifiedprofile", users.getVerified()).apply();
                sharedPreferences.edit().putString("descriptionprofile", users.getDescription()).apply();
                sharedPreferences.edit().putString("websitesprofile", users.getWebsites()).apply();
                sharedPreferences.edit().putString("categoryprofile", users.getCategory()).apply();
                sharedPreferences.edit().putString("backphotoprofile", users.getBackphoto()).apply();
                sharedPreferences.edit().putString("subscribedprofile", users.getSubscribed()).apply();
                sharedPreferences.edit().putString("subscriberprofile", users.getSubscribers()).apply();

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileimg, backimg;
        TextView subscribers;
        CardView card;
        AppCompatTextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileimg = itemView.findViewById(R.id.profileimg);
            backimg = itemView.findViewById(R.id.backimg);
            subscribers = itemView.findViewById(R.id.subscribers);
            card = itemView.findViewById(R.id.card);
            username = itemView.findViewById(R.id.username);


        }
    }

}
