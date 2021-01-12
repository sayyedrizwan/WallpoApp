package com.wallpo.android.uploads;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.transition.TransitionManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wallpo.android.R;
import com.wallpo.android.getset.Photos;
import com.wallpo.android.utils.Common;
import com.wallpo.android.videoGallery.VideoFolder;

import java.util.List;

public class addpostsadapter extends RecyclerView.Adapter<addpostsadapter.ViewHolder> {
    private Context context;
    private List<Photos> categoryList;

    public addpostsadapter(Context context, List<Photos> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_posts_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Photos category = categoryList.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        String userid = sharedPreferences.getString("wallpouserid", "");

        holder.card.setOnClickListener(view -> {

            TransitionManager.beginDelayedTransition(holder.card);

            if (holder.uploadimg.getVisibility() == View.VISIBLE) {

                holder.uploadimg.setVisibility(View.GONE);
                holder.uploadlay.setVisibility(View.VISIBLE);


            } else {

                holder.uploadlay.setVisibility(View.GONE);
                holder.uploadimg.setVisibility(View.VISIBLE);

            }

        });

        holder.image.setOnClickListener(view -> context.startActivity(new Intent(context, UploadImageActivity.class)));

        holder.video.setOnClickListener(view -> Dexter.withContext(context).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Dexter.withContext((Activity) context).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {

                                        Intent i = new Intent(context, VideoFolder.class);
                                        Common.TYPE = "videoupload";
                                        context.startActivity(i);
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        Toast.makeText(context, context.getResources().getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(context, context.getResources().getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check());


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textv;
        CardView card, image, video;
        RelativeLayout uploadlay;
        ImageView uploadimg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textv = itemView.findViewById(R.id.textv);
            card = itemView.findViewById(R.id.card);
            uploadlay = itemView.findViewById(R.id.uploadlay);
            uploadlay.setVisibility(View.GONE);
            uploadimg = itemView.findViewById(R.id.uploadimg);
            image = itemView.findViewById(R.id.image);
            video = itemView.findViewById(R.id.video);

        }
    }

}
