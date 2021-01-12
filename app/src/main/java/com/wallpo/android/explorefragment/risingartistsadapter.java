package com.wallpo.android.explorefragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.wallpo.android.R;
import com.wallpo.android.activity.ViewPostsActivity;
import com.wallpo.android.getset.Photos;

import java.util.List;

import static android.content.ContentValues.TAG;

public class risingartistsadapter extends RecyclerView.Adapter<risingartistsadapter.ViewHolder> {
    private Context context;
    private List<Photos> Photos;

    public risingartistsadapter(Context context, List<Photos> Photos) {
        this.context = context;
        this.Photos = Photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.newartists_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Photos photos = Photos.get(position);

        try {

            Glide.with(context).load(photos.getImagepath()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).into(holder.mainimg);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        if (photos.getImagepath().contains(".mp4")) {
            holder.play = true;

            if (holder.play) {

                holder.playbtn.setVisibility(View.VISIBLE);

                holder.playbtn.setOnClickListener(view -> {

                    holder.mainimg.setVisibility(View.GONE);

                    holder.playbtn.setVisibility(View.GONE);

                    holder.videoview.setVisibility(View.VISIBLE);

                    try {
                        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                        holder.player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                        Uri videoUri = Uri.parse(photos.getImagepath());//
                        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("wallpo_player");
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                        MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
                        holder.videoview.setPlayer(holder.player);
                        holder.videoview.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                        holder.player.prepare(mediaSource);
                        holder.player.setVolume(0f);
                        holder.player.setPlayWhenReady(true);
                        holder.player.getPlaybackState();

                        holder.player.addListener(new Player.EventListener() {
                            @Override
                            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                                    holder.bufferingbar.setVisibility(View.VISIBLE);
                                } else {
                                    holder.bufferingbar.setVisibility(View.INVISIBLE);
                                }

                                if (playWhenReady) {
                                    //playing
                                    holder.playbtn.setVisibility(View.GONE);
                                } else {
                                    //pause

                                    holder.playbtn.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "onCreate: ", e);
                    }

                });
            }
        }

        holder.videoview.getVideoSurfaceView().setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewPostsActivity.class);
            SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);
            SharedPreferences.Editor editor = sharepref.edit();

            editor.putString("photoid", String.valueOf(photos.getPhotoid()));
            editor.putString("useridprofile", String.valueOf(photos.getUserid()));
            editor.putString("caption", photos.getCaption());
            editor.putString("categoryid", photos.getCategoryid());
            editor.putString("albumid", photos.getAlbumid());
            editor.putString("datecreated", photos.getDatecreated());
            editor.putString("dateshowed", photos.getDateshowed());
            editor.putString("link", photos.getLink());
            editor.putString("userid", photos.getUserid());
            editor.putString("imagepath", photos.getImagepath());
            editor.putString("trendingcount", String.valueOf(photos.getTrendingcount()));
            editor.putString("likes", photos.getLikes());
            editor.apply();

            context.startActivity(intent);
        });

        Zoomy.Builder builder = new Zoomy.Builder((Activity) context).target(holder.mainimg).tapListener(v -> {

            Intent intent = new Intent(context, ViewPostsActivity.class);
            SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);
            SharedPreferences.Editor editor = sharepref.edit();

            editor.putString("photoid", String.valueOf(photos.getPhotoid()));
            editor.putString("useridprofile", String.valueOf(photos.getUserid()));
            editor.putString("caption", photos.getCaption());
            editor.putString("categoryid", photos.getCategoryid());
            editor.putString("albumid", photos.getAlbumid());
            editor.putString("datecreated", photos.getDatecreated());
            editor.putString("dateshowed", photos.getDateshowed());
            editor.putString("link", photos.getLink());
            editor.putString("userid", photos.getUserid());
            editor.putString("imagepath", photos.getImagepath());
            editor.putString("trendingcount", String.valueOf(photos.getTrendingcount()));
            editor.putString("likes", photos.getLikes());
            editor.apply();

            context.startActivity(intent);
        });
        builder.register();

        if (!context.toString().contains(".ChatActivity@")) {
            if (position == (getItemCount() - 1)) {
                holder.spinkit.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.spinkit.setVisibility(View.GONE);
                    }
                }, 2000);
            } else {
                holder.spinkit.setVisibility(View.GONE);
            }
        } else {
            holder.spinkit.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return Photos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mainimg, playbtn;
        SpinKitView spinkit;
        SimpleExoPlayerView videoview;
        SimpleExoPlayer player;
        SpinKitView bufferingbar;
        Boolean play = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainimg = itemView.findViewById(R.id.mainimg);
            spinkit = itemView.findViewById(R.id.spinkit);
            spinkit.setVisibility(View.GONE);
            bufferingbar = itemView.findViewById(R.id.bufferingbar);
            bufferingbar.setVisibility(View.GONE);
            playbtn = itemView.findViewById(R.id.playbtn);
            playbtn.setVisibility(View.GONE);
            videoview = itemView.findViewById(R.id.videoview);
            videoview.setVisibility(View.GONE);

        }
    }

}
