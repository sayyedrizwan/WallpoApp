package com.wallpo.android.videoGallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wallpo.android.R;
import com.wallpo.android.uploads.VideoTrimmerActivity;
import com.wallpo.android.uploads.VideoTrimmerUploadActivity;
import com.wallpo.android.utils.Common;

import java.util.ArrayList;


public class Adapter_VideoFolder extends RecyclerView.Adapter<Adapter_VideoFolder.ViewHolder> {

    ArrayList<Model_Video> al_video;
    Context context;
    Activity activity;


    public Adapter_VideoFolder(Context context, ArrayList<Model_Video> al_video, Activity activity) {

        this.al_video = al_video;
        this.context = context;
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;
        RelativeLayout rl_select;

        public ViewHolder(View v) {

            super(v);

            iv_image = (ImageView) v.findViewById(R.id.iv_image);
            rl_select = (RelativeLayout) v.findViewById(R.id.rl_select);

        }
    }

    @Override
    public Adapter_VideoFolder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_videos, parent, false);

        ViewHolder viewHolder1 = new ViewHolder(view);


        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder Vholder, final int position) {


        try {
            Glide.with(context).load("file://" + al_video.get(position).getStr_thumb())
                    .skipMemoryCache(false).into(Vholder.iv_image);

        } catch (IllegalArgumentException e) {
            Log.e("VideoFolder", "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e("act", "onBindViewHolder: ", e);
        }

        Vholder.rl_select.setBackgroundColor(Color.parseColor("#FFFFFF"));
        Vholder.rl_select.setAlpha(0);


        Vholder.rl_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Common.IMAGE_SELECTED = "finish";
                if (Common.TYPE.equals("videoupload")){

                    Intent intent_gallery = new Intent(context, VideoTrimmerUploadActivity.class);
                    Common.VIDEO_PATH = al_video.get(position).getStr_path();
                    activity.startActivity(intent_gallery);

                    Common.TYPE ="";

                }else {

                    Intent intent_gallery = new Intent(context, VideoTrimmerActivity.class);
                    Common.VIDEO_PATH = al_video.get(position).getStr_path();
                    activity.startActivity(intent_gallery);

                }

            }
        });

    }

    @Override
    public int getItemCount() {

        return al_video.size();
    }

}

