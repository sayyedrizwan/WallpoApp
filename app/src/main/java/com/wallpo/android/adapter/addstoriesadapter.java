package com.wallpo.android.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wallpo.android.R;
import com.wallpo.android.getset.Stories;
import com.wallpo.android.uploads.ImagestatusActivity;
import com.wallpo.android.uploads.StatusActivity;
import com.wallpo.android.utils.Common;
import com.wallpo.android.videoGallery.VideoFolder;

import java.util.List;

public class addstoriesadapter extends RecyclerView.Adapter<addstoriesadapter.ViewHolder> {
    private Context context;
    private List<Stories> Stories;

    public addstoriesadapter(Context context, List<Stories> Stories) {
        this.context = context;
        this.Stories = Stories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.addstories_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Stories stories = Stories.get(position);

        holder.cardview.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(holder.cardview);
            }

            if (holder.add.getVisibility() == View.VISIBLE) {
                holder.add.setVisibility(View.GONE);
                holder.addlist.setVisibility(View.VISIBLE);
            } else {
                holder.addlist.setVisibility(View.GONE);
                holder.add.setVisibility(View.VISIBLE);
            }
        });

        holder.addlist.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(holder.cardview);
            }

            holder.addlist.setVisibility(View.GONE);
            holder.add.setVisibility(View.VISIBLE);
        });


        holder.status.setOnClickListener(v -> context.startActivity(new Intent(context, StatusActivity.class)));

        holder.image.setOnClickListener(v -> context.startActivity(new Intent(context, ImagestatusActivity.class)));

        holder.video.setOnClickListener(v ->
                Dexter.withContext(context).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Dexter.withContext((Activity) context).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                        .withListener(new PermissionListener() {
                                            @Override
                                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                                Common.TYPE = "";
                                                context.startActivity(new Intent(context, VideoFolder.class));
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
        return Stories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardview, status, image, video;
        RelativeLayout add, addlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardview = itemView.findViewById(R.id.cardview);

            SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);

            int width = Integer.parseInt(sharedPreferences.getString("firewallheight", "0"));

            cardview.getLayoutParams().height = width / 2 / 2 * 3;
            cardview.getLayoutParams().width = width / 2 / 2 * 2 + 70;
            cardview.requestLayout();

            add = itemView.findViewById(R.id.add);
            addlist = itemView.findViewById(R.id.addlist);
            status = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.image);
            video = itemView.findViewById(R.id.video);

        }
    }

}
