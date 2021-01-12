package com.wallpo.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.varunest.sparkbutton.SparkButton;
import com.wallpo.android.R;
import com.wallpo.android.activity.ViewPostsActivity;
import com.wallpo.android.getset.Photos;

import java.util.List;

import static android.content.ContentValues.TAG;

public class favadapter extends RecyclerView.Adapter<favadapter.ViewHolder> {
    private Context context;
    private List<Photos> photos;

    public favadapter(Context context, List<Photos> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fav_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Photos Photo = photos.get(position);

        try {

            Glide.with(context).load(Photo.getImagepath()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).into(holder.imageView);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        if (Photo.getImagepath().contains(".mp4")) {
            holder.play = true;

            if (holder.play) {
                holder.playbtn.setVisibility(View.VISIBLE);
            }else {
                holder.playbtn.setVisibility(View.GONE);
            }
        }

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewPostsActivity.class);
                SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);
                SharedPreferences.Editor editor = sharepref.edit();

                editor.putString("photoid", String.valueOf(Photo.getPhotoid()));
                editor.putString("caption", Photo.getCaption());
                editor.putString("categoryid", Photo.getCategoryid());
                editor.putString("albumid", Photo.getAlbumid());
                editor.putString("datecreated", Photo.getDatecreated());
                editor.putString("dateshowed", Photo.getDateshowed());
                editor.putString("link", Photo.getLink());
                editor.putString("userid", Photo.getUserid());
                editor.putString("imagepath", Photo.getImagepath());
                editor.putString("trendingcount", String.valueOf(Photo.getTrendingcount()));
                editor.putString("likes", Photo.getLikes());
                editor.apply();

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, playbtn;
        CardView cardview;
        SparkButton favbutton;
        Boolean play = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            cardview = itemView.findViewById(R.id.cardview);
            favbutton = itemView.findViewById(R.id.favbutton);
            playbtn = itemView.findViewById(R.id.playbtn);
            playbtn.setVisibility(View.GONE);

            SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);

            cardview.getLayoutParams().height = Integer.parseInt(sharedPreferences.getString("mobileheight", "")) / 2 + 210;
            cardview.getLayoutParams().width = Integer.parseInt(sharedPreferences.getString("mobilewidth", "")) / 2 + 210;
            cardview.requestLayout();

        }
    }

}
