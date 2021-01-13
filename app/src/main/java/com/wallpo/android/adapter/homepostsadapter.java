package com.wallpo.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;
import com.bachors.wordtospan.WordToSpan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;
import com.wallpo.android.R;
import com.wallpo.android.SplashActivity;
import com.wallpo.android.activity.CommentActivity;
import com.wallpo.android.activity.HashtagActivity;
import com.wallpo.android.extra.WallpaperSettingActivity;
import com.wallpo.android.getset.Photos;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.customListeners;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.wallpo.android.utils.updatecode.addfav;
import static com.wallpo.android.utils.updatecode.addlike;
import static com.wallpo.android.utils.updatecode.getusernameid;
import static com.wallpo.android.utils.updatecode.increaseno;
import static com.wallpo.android.utils.updatecode.trendingcounts;
import static com.wallpo.android.utils.updatecode.unfav;
import static com.wallpo.android.utils.updatecode.unlike;
import static com.wallpo.android.utils.updatecode.updateFCM;

public class homepostsadapter extends RecyclerView.Adapter<homepostsadapter.ViewHolder> {
    private Context context;
    private List<Photos> photos;

    public homepostsadapter(Context context, List<Photos> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_posts_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        try {
            final Photos Photo = photos.get(position);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            final int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;


            holder.imageView.setImageBitmap(null);

            holder.imageViewbg.setImageBitmap(null);

            if (Photo.getType().equals("trending")) {

                holder.cardview.getLayoutParams().height = height / 2 / 2 * 3;
                holder.cardview.getLayoutParams().width = width - 160;
                holder.cardview.requestLayout();

                holder.relllay.getLayoutParams().width = width - 160;
                holder.relllay.requestLayout();

                int trendno = position + 1;

                holder.trendingno.setText("# " + trendno + context.getResources().getString(R.string.ontrending));
                holder.trendingno.setVisibility(View.VISIBLE);

            } else {

                holder.trendingno.setVisibility(View.GONE);
                holder.cardview.getLayoutParams().height = height / 2 / 2 * 3;
                holder.cardview.getLayoutParams().width = width - 160;
                holder.cardview.requestLayout();

                holder.relllay.getLayoutParams().width = width - 160;
                holder.relllay.requestLayout();
            }

            final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
            final String id = sharedPreferences.getString("wallpouserid", "");

            final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

            String datauserid = wallpouserdata.getString(Photo.getUserid() + "id", "");

            if (!datauserid.isEmpty()) {

                holder.usernamestring = wallpouserdata.getString(Photo.getUserid() + "username", "");
                holder.displaynamest = wallpouserdata.getString(Photo.getUserid() + "displayname", "");
                holder.profilephotost = wallpouserdata.getString(Photo.getUserid() + "profilephoto", "");

                holder.verified = wallpouserdata.getString(Photo.getUserid() + "verified", "");
                holder.description = wallpouserdata.getString(Photo.getUserid() + "description", "");
                holder.category = wallpouserdata.getString(Photo.getUserid() + "category", "");
                holder.websites = wallpouserdata.getString(Photo.getUserid() + "websites", "");
                holder.backphoto = wallpouserdata.getString(Photo.getUserid() + "backphoto", "");
                holder.subscribed = wallpouserdata.getString(Photo.getUserid() + "subscribed", "");
                holder.subscribers = wallpouserdata.getString(Photo.getUserid() + "subscribers", "");


                try {

                    Glide.with(context).load(holder.profilephotost).centerInside()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.mipmap.profilepic)
                            .apply(new RequestOptions().override(290, 290))
                            .skipMemoryCache(false).into(holder.profilepic);

                } catch (IllegalArgumentException e) {
                    Log.e("searchadapter", "onBindViewHolder: ", e);
                } catch (IllegalStateException e) {

                    Log.e("act", "onBindViewHolder: ", e);
                }

                holder.dislayname.setText(holder.displaynamest);
                holder.username.setText("@ " + holder.usernamestring);


            } else {
                final OkHttpClient client = new OkHttpClient();
                RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(Photo.getUserid())).build();
  /*
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .add("userid", String.valueOf(Photo.getUserid()))
                    .build();
*/
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

                                    } catch (IllegalArgumentException e) {
                                        Log.e("searchadapter", "onBindViewHolder: ", e);
                                    } catch (IllegalStateException e) {

                                        Log.e("act", "onBindViewHolder: ", e);
                                    }
                                    holder.dislayname.setText(holder.displaynamest);
                                    holder.username.setText("@ " + holder.usernamestring);

                                    wallpouserdata.edit().putString(Photo.getUserid() + "id", Photo.getUserid()).apply();
                                    wallpouserdata.edit().putString(Photo.getUserid() + "username", holder.usernamestring).apply();
                                    wallpouserdata.edit().putString(Photo.getUserid() + "displayname", holder.displaynamest).apply();
                                    wallpouserdata.edit().putString(Photo.getUserid() + "profilephoto", holder.profilephotost).apply();

                                    wallpouserdata.edit().putString(Photo.getUserid() + "verified", holder.verified).apply();
                                    wallpouserdata.edit().putString(Photo.getUserid() + "description", holder.description).apply();
                                    wallpouserdata.edit().putString(Photo.getUserid() + "category", holder.category).apply();
                                    wallpouserdata.edit().putString(Photo.getUserid() + "websites", holder.websites).apply();
                                    wallpouserdata.edit().putString(Photo.getUserid() + "backphoto", holder.backphoto).apply();
                                    wallpouserdata.edit().putString(Photo.getUserid() + "subscribed", holder.subscribed).apply();
                                    wallpouserdata.edit().putString(Photo.getUserid() + "subscribers", holder.subscribers).apply();


                                }

                            } catch (
                                    JSONException e) {
                                e.printStackTrace();
                            }
                        });

                    }
                });
            }

            holder.profilepic.setOnClickListener(v -> {
                Intent i = new Intent(context, ProfileActivity.class);

                sharedPreferences.edit().putString("postsidprofile", String.valueOf(Photo.getPhotoid())).apply();
                sharedPreferences.edit().putString("useridprofile", String.valueOf(Photo.getUserid())).apply();
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

            holder.dislayname.setOnClickListener(v -> {
                Intent i = new Intent(context, ProfileActivity.class);

                sharedPreferences.edit().putString("postsidprofile", String.valueOf(Photo.getPhotoid())).apply();
                sharedPreferences.edit().putString("useridprofile", String.valueOf(Photo.getUserid())).apply();
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


            try {
                holder.playbtn.setVisibility(View.GONE);
                holder.videoview.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.loadingwallpaperbar.setVisibility(View.VISIBLE);

                Glide.with(context).load(Photo.getImagepath())
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, final Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                ((Activity) context).runOnUiThread(() -> {
                                    holder.loadingwallpaperbar.setVisibility(View.GONE);
                                    load(Photo.getImagepath(), holder.imageViewbg);
                                });
                                return false;
                            }
                        }).into(holder.imageView);

            } catch (IllegalArgumentException e) {
                Log.e(TAG, "onBindViewHolder: ", e);
            } catch (IllegalStateException e) {

                Log.e(TAG, "onBindViewHolder: ", e);
            }

            if (Photo.getImagepath().contains(".mp4")) {
                holder.play = true;

                if (holder.play) {

                    holder.playbtn.setVisibility(View.VISIBLE);

                    holder.playbtn.setOnClickListener(view -> {
                        try {

                            customListeners.playerlistener.onObjectReady("pause");
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onBindViewHolder: ");
                        }

                        holder.imageView.setVisibility(View.GONE);

                        holder.playbtn.setVisibility(View.GONE);

                        holder.videoview.setVisibility(View.VISIBLE);

                        try {
                            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                            holder.player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                            Uri videoUri = Uri.parse(Photo.getImagepath());//
                            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("wallpo_player");
                            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                            MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
                            holder.videoview.setPlayer(holder.player);
                            holder.player.prepare(mediaSource);
                            //  holder.player.getPlayWhenReady();
                            holder.player.setPlayWhenReady(true);
                            holder.player.getPlaybackState();

                            customListeners customListeners = new customListeners();
                            customListeners.playerListeners(title -> {
                                holder.player.setPlayWhenReady(false);
                                holder.player.getPlaybackState();
                            });


                            holder.player.addListener(new Player.EventListener() {
                                @Override
                                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                                    if (!playWhenReady) {
                                        try {
                                            customListeners.playerlistener.onObjectReady("pause");
                                        } catch (NullPointerException e) {
                                            Log.d(TAG, "onBindViewHolder: ");
                                        }
                                    }

                                    if (holder.rundur) {
                                        if (playbackState == ExoPlayer.STATE_READY) {

                                            long realDurationMillis = holder.player.getDuration();
                                            int dur = (int) realDurationMillis / 1000;

                                            holder.duration.setVisibility(View.VISIBLE);
                                            holder.duration.setText(dur + context.getResources().getString(R.string.secondsshort));

                                            holder.rundur = false;


                                            new Handler().postDelayed(() -> {

                                                int minusdur = dur - 1;

                                                holder.duration.setText(minusdur + context.getResources().getString(R.string.secondsshort));

                                            }, 1000);

                                            new Handler().postDelayed(() -> {
                                                holder.duration.setVisibility(View.GONE);
                                            }, 2000);


                                        }
                                    }
                                    if (playbackState == ExoPlayer.STATE_BUFFERING) {
                                        holder.bufferingbar.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.bufferingbar.setVisibility(View.INVISIBLE);
                                    }
/*
                                if (playWhenReady) {
                                    //playing
                                    holder.playbtn.setVisibility(View.GONE);
                                } else {
                                    //pause

                                    holder.playbtn.setVisibility(View.VISIBLE);
                                }*/
                                }
                            });


                        } catch (Exception e) {
                            Log.e(TAG, "onCreate: ", e);
                        }
                    });
                } else {
                    holder.playbtn.setVisibility(View.GONE);

                }

            }

            String dateshow = Photo.getDatecreated();

            holder.date.setText(Photo.getDateshowed());

            if (!dateshow.contains("-")) {
                String CurrentDate = updatecode.getDate(Long.parseLong(Photo.getDatecreated()), "MM/dd/yyyy");

                try {
                    String FinalDate = getTimestampDayNew();
                    Date date1;
                    Date date2;
                    SimpleDateFormat dates = new SimpleDateFormat("MM/dd/yyyy");
                    date1 = dates.parse(CurrentDate);
                    date2 = dates.parse(FinalDate);
                    long difference = Math.abs(date1.getTime() - date2.getTime());
                    long differenceDates = difference / (24 * 60 * 60 * 1000);
                    String dayDifference = Long.toString(differenceDates);

                    holder.date.setText(dayDifference + context.getResources().getString(R.string.daysago));

                    if (differenceDates < 62 && differenceDates > 31) {

                        holder.date.setText("2" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 93 && differenceDates > 62) {

                        holder.date.setText("3" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 124 && differenceDates > 93) {

                        holder.date.setText("4" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 155 && differenceDates > 124) {

                        holder.date.setText("5" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 185 && differenceDates > 155) {

                        holder.date.setText("6" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 215 && differenceDates > 185) {

                        holder.date.setText("7" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 245 && differenceDates > 215) {

                        holder.date.setText("8" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 275 && differenceDates > 245) {

                        holder.date.setText("9" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 306 && differenceDates > 275) {

                        holder.date.setText("10" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 336 && differenceDates > 306) {

                        holder.date.setText("11" + context.getResources().getString(R.string.monthsago));

                    } else if (differenceDates < 366 && differenceDates > 336) {

                        holder.date.setText("1" + context.getResources().getString(R.string.oneyearsago));

                    } else if (differenceDates < 736 && differenceDates > 366) {

                        holder.date.setText("2" + context.getResources().getString(R.string.oneyear));

                    }else if (differenceDates > 736){

                        holder.date.setText(updatecode.getDate(Long.parseLong(Photo.getDatecreated()), "dd MMM YYYY HH:ss"));

                    }

                    if(differenceDates < 1){
                        holder.date.setText(context.getResources().getString(R.string.today));

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
/*
            String photogen = dateshow.replaceAll("-", "");
            String photogen2 = photogen.substring(0, 8);
            int timecal = Integer.parseInt(photogen2);
            int timecaal = Integer.parseInt(getTimestamp()) - timecal;

            if (timecaal <= 1) {

                holder.date.setText(context.getResources().getString(R.string.Today));

            } else if (timecaal > 1 && timecaal < 100) {

                holder.date.setText(timecaal + context.getResources().getString(R.string.daysago));

            } else if (timecaal > 100 && timecaal < 10000) {

                String time = String.valueOf(timecaal).substring(0, 1);

                holder.date.setText(time + context.getResources().getString(R.string.monthsago));

            } else {

                holder.date.setText(Photo.getDateshowed());
            }*/

            if (Photo.getType().equals("ads")) {
                holder.date.setText(context.getResources().getString(R.string.premiumads));
            }

            if (Photo.getType().equals("ads")) {
                if (!Photo.getLink().isEmpty())
                    holder.link.setVisibility(View.VISIBLE);
                else
                    holder.link.setVisibility(View.GONE);
            } else {
                holder.link.setVisibility(View.GONE);
            }

            holder.menu.setOnClickListener(v -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(holder.mainlayout);
                }

                if (holder.comment.getVisibility() == View.GONE) {
                    holder.menuimg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel_white));
                    holder.comment.setVisibility(View.VISIBLE);
                    holder.wallpaperlockset.setVisibility(View.VISIBLE);
                    holder.favlay.setVisibility(View.VISIBLE);
                    holder.wallpaperset.setVisibility(View.VISIBLE);
                    holder.share.setVisibility(View.VISIBLE);
                    holder.mainbackhide.setVisibility(View.VISIBLE);
                    holder.dislayname.setVisibility(View.VISIBLE);
                    holder.username.setVisibility(View.VISIBLE);

                    holder.profilecard.getLayoutParams().height = holder.profilecard.getHeight() * 2;
                    holder.profilecard.getLayoutParams().width = holder.profilecard.getWidth() * 2;
                    holder.profilecard.requestLayout();

                    if (Photo.getLink() != null) {
                        if (!Photo.getLink().isEmpty()) {
                            holder.link.setVisibility(View.VISIBLE);
                        } else {
                            holder.link.setVisibility(View.GONE);
                        }
                    } else {
                        holder.link.setVisibility(View.GONE);
                    }

                    holder.caption.setVisibility(View.VISIBLE);


                    if (Photo.getUserid().equals(id)) {
                        holder.deletevisible = true;

                        if (holder.deletevisible) {
                            holder.delete.setVisibility(View.VISIBLE);
                            holder.edit.setVisibility(View.VISIBLE);

                            holder.edit.setOnClickListener(view -> {
                                updatecode.editposts(context, Photo.getPhotoid());

                            });

                            holder.delete.setOnClickListener(v1 -> {

                                BottomSheetDialog dialog = new BottomSheetDialog(context);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setCancelable(true);
                                dialog.setContentView(R.layout.deleteposts);

                                Button delete = dialog.findViewById(R.id.delete);
                                AppCompatTextView caption = dialog.findViewById(R.id.caption);
                                ImageView imageview = dialog.findViewById(R.id.imageview);
                                SpinKitView loadingbar = dialog.findViewById(R.id.loadingbar);
                                loadingbar.setVisibility(View.GONE);
                                assert caption != null;
                                caption.setText(Photo.getCaption());

                                try {

                                    Glide.with(context).load(Photo.getImagepath()).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .skipMemoryCache(false).into(imageview);

                                } catch (IllegalArgumentException e) {
                                    Log.e("searchadapter", "onBindViewHolder: ", e);
                                } catch (IllegalStateException e) {

                                    Log.e("act", "onBindViewHolder: ", e);
                                }

                                delete.setOnClickListener(view -> {

                                    loadingbar.setVisibility(View.VISIBLE);
                                    delete.setClickable(false);

                                    final SharedPreferences sharedPreferences1 = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
                                    final String fcmtoken = sharedPreferences1.getString("fcmtoken", "");

                                    final OkHttpClient clientdel = new OkHttpClient();

                                    RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(Photo.getPhotoid()))
                                            .add("userid", Photo.getUserid())
                                            .add("fcm", fcmtoken).build();

                                    okhttp3.Request request = new okhttp3.Request.Builder()
                                            .url(URLS.deletephoto)
                                            .post(postData)
                                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                            .build();

                                    clientdel.newCall(request).enqueue(new Callback() {

                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.e(TAG, "onFailure: ", e);
                                        }

                                        @Override
                                        public void onResponse(Call call, final Response response) throws IOException {
                                            final String data = response.body().string().replaceAll(",\\[]", "");

                                            ((Activity) context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loadingbar.setVisibility(View.GONE);
                                                    delete.setClickable(false);
                                                    if (data.trim().contains("unauth")) {
                                                        updateFCM(context);
                                                        Toast.makeText(context, "" + context.getResources().getString(R.string.autherrortryagain), Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                        return;
                                                    }

                                                    if (data.trim().contains("error")) {
                                                        updateFCM(context);
                                                        Toast.makeText(context, "" + context.getResources().getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                        return;
                                                    }

                                                    if (data.trim().contains("deleted")) {
                                                        Toast.makeText(context, "Posts deleted successfully.", Toast.LENGTH_SHORT).show();
                                                        holder.mainlayout.setVisibility(View.GONE);
                                                        dialog.dismiss();

                                                    }

                                                }
                                            });


                                        }
                                    });
                                });
                                dialog.show();
                            });

                        } else {
                            holder.delete.setVisibility(View.GONE);
                            holder.edit.setVisibility(View.GONE);

                        }

                    } else {

                        holder.delete.setVisibility(View.GONE);
                        holder.edit.setVisibility(View.GONE);
                    }

                } else {
                    holder.menuimg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu_home));
                    holder.comment.setVisibility(View.GONE);
                    holder.wallpaperlockset.setVisibility(View.GONE);
                    holder.favlay.setVisibility(View.GONE);
                    holder.wallpaperset.setVisibility(View.GONE);
                    holder.share.setVisibility(View.GONE);
                    holder.mainbackhide.setVisibility(View.GONE);
                    holder.dislayname.setVisibility(View.GONE);
                    holder.username.setVisibility(View.GONE);
                    holder.link.setVisibility(View.GONE);
                    holder.caption.setVisibility(View.GONE);

                    holder.delete.setVisibility(View.GONE);
                    holder.edit.setVisibility(View.GONE);

                    if (Photo.getType().equals("ads")) {
                        if (!Photo.getLink().isEmpty())
                            holder.link.setVisibility(View.VISIBLE);
                        else
                            holder.link.setVisibility(View.GONE);
                    } else {
                        holder.link.setVisibility(View.GONE);
                    }

                    holder.profilecard.getLayoutParams().height = holder.profilecard.getHeight() / 2;
                    holder.profilecard.getLayoutParams().width = holder.profilecard.getWidth() / 2;
                    holder.profilecard.requestLayout();

                }
            });


            holder.likes.setText(updatecode.daynumber(Photo.getLikes()));

            holder.share.setOnClickListener(v -> {
                Common.SHARETYPE = "posts";
                Common.MESSAGE_DATA = String.valueOf(Photo.getPhotoid());
                updatecode.shareintent(context, "posts/" + Photo.getPhotoid());
            });


            if (id != null) {
                if (!id.isEmpty()) {

                    final OkHttpClient cliento = new OkHttpClient();

                    RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(Photo.getPhotoid()))
                            .add("usersid", id)
                            .build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.getlikes)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    cliento.newCall(request).enqueue(new okhttp3.Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "onFailure: ", e);
                        }

                        @Override
                        public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (data.trim().contains("not liked")) {
                                        //notliked

                                        holder.likebutton.setChecked(false);

                                    } else {
                                        //like

                                        holder.likebutton.setChecked(true);

                                    }

                                }
                            });


                        }
                    });


                } else {
                    Log.d(TAG, "onBindViewHolder: nonlogin");
                }
            } else {
                Log.d(TAG, "onBindViewHolder: nonlogin");
            }

            if (id != null) {
                if (!id.isEmpty()) {

                    holder.likebutton.setEventListener(new SparkEventListener() {
                        @Override
                        public void onEvent(ImageView button, boolean buttonState) {
                            if (buttonState) {
                                //like the posts
                                holder.likebutton.setChecked(true);

                                holder.likebutton.setChecked(true);

                                if (Integer.parseInt(Photo.getLikes()) <= 999 && Integer.parseInt(Photo.getLikes()) >= 0) {

                                    int add = Integer.parseInt(holder.likes.getText().toString().replace(",", "")) + 1;

                                    holder.likes.setText("" + add);
                                }

                                addlike(context, Photo.getPhotoid());


                            } else {

                                //unlike the posts

                                holder.likebutton.setChecked(false);
                                holder.likebutton.setChecked(false);

                                if (Integer.parseInt(Photo.getLikes()) <= 999 && Integer.parseInt(Photo.getLikes()) > 0) {

                                    int add = Integer.parseInt(holder.likes.getText().toString().replace(",", "")) - 1;

                                    holder.likes.setText("" + add);
                                }

                                unlike(context, Photo.getPhotoid());

                            }
                        }

                        @Override
                        public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                        }

                        @Override
                        public void onEventAnimationStart(ImageView button, boolean buttonState) {

                        }
                    });


                } else {
                    holder.likebutton.setOnClickListener(v -> {
                        Toast.makeText(context, context.getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                        holder.likebutton.setChecked(false);
                        holder.likebutton.setChecked(false);
                    });
                }
            } else {
                holder.likebutton.setOnClickListener(v -> {
                    Toast.makeText(context, context.getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                    holder.likebutton.setChecked(false);
                    holder.likebutton.setChecked(false);
                });
            }


            //fav

            if (id != null) {
                if (!id.isEmpty()) {

                    final OkHttpClient clientqq = new OkHttpClient();


                    RequestBody postData = new FormBody.Builder().add("photoid", String.valueOf(Photo.getPhotoid()))
                            .add("usersid", id)
                            .build();

                    okhttp3.Request requests = new okhttp3.Request.Builder()
                            .url(URLS.checkfav)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    clientqq.newCall(requests).enqueue(new okhttp3.Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "onFailure: ", e);
                        }

                        @Override
                        public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (data.contains("not fav")) {
                                        holder.favbutton.setChecked(false);

                                    } else {

                                        holder.favbutton.setChecked(true);
                                    }

                                }
                            });

                        }
                    });


                } else {
                    holder.favbutton.setOnClickListener(v -> {
                        Toast.makeText(context, context.getResources().getString(R.string.logintofavposts), Toast.LENGTH_SHORT).show();

                        holder.favbutton.setChecked(false);
                        holder.favbutton.setChecked(false);
                    });
                }
            } else {
                holder.favbutton.setOnClickListener(v -> {
                    Toast.makeText(context, context.getResources().getString(R.string.logintofavposts), Toast.LENGTH_SHORT).show();

                    holder.favbutton.setChecked(false);
                    holder.favbutton.setChecked(false);
                });
            }


            if (id != null) {
                if (!id.isEmpty()) {

                    holder.favbutton.setEventListener(new SparkEventListener() {
                        @Override
                        public void onEvent(ImageView button, boolean buttonState) {
                            if (buttonState) {
                                //fav the posts

                                holder.favbutton.setChecked(true);

                                holder.favbutton.setChecked(true);

                                addfav(context, Photo.getPhotoid());


                            } else {

                                //unfav the posts

                                holder.favbutton.setChecked(false);

                                holder.favbutton.setChecked(false);

                                unfav(context, Photo.getPhotoid());


                            }
                        }

                        @Override
                        public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                        }

                        @Override
                        public void onEventAnimationStart(ImageView button, boolean buttonState) {

                        }
                    });

                } else {
                    holder.favbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, context.getResources().getString(R.string.logintofavposts), Toast.LENGTH_SHORT).show();

                            holder.likebutton.setChecked(false);
                            holder.likebutton.setChecked(false);
                        }
                    });
                }
            } else {
                holder.favbutton.setOnClickListener(v -> {
                    Toast.makeText(context, context.getResources().getString(R.string.logintofavposts), Toast.LENGTH_SHORT).show();

                    holder.likebutton.setChecked(false);
                    holder.likebutton.setChecked(false);
                });
            }


            trendingcounts(context, Photo.getPhotoid());


            if (Photo.getType().equals("ads")) {

                try {
                    if (sharedPreferences.getString("adstimes", "0").equals("")) {
                        int firsttime = Integer.parseInt(sharedPreferences.getString("adstimes", "0")) + 25;

                        int timestamp = Integer.parseInt(getTimestamphour());

                        if (timestamp > firsttime) {
                            increaseno(Photo.getUid(), "");
                            updatecode.reducenumber(Photo.getUid());
                        } else {
                            Log.d(TAG, "onBindViewHolder: ");
                        }
                    } else {
                        increaseno(Photo.getUid(), "");
                    }

                } catch (NumberFormatException e) {
                    Log.e(TAG, "onBindViewHolder: ", e);

                    increaseno(Photo.getUid(), "");

                }


            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                holder.wallpaperlockset.setVisibility(View.GONE);
            }

            holder.wallpaperlockset.setOnClickListener(v -> {
                if (Photo.getImagepath().contains(".mp4")) {
                    updatecode.changelivewallpaper(context, Photo.getImagepath(), Photo.getPhotoid(), holder.view);
                } else {
                    if (holder.imageView.getDrawable() == null) {
                        Toast.makeText(context, context.getResources().getString(R.string.imagenotloadedproperly), Toast.LENGTH_SHORT).show();

                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                        Bitmap bitmapimg = ((BitmapDrawable) holder.imageView.getDrawable()).getBitmap();

                        updatecode.changelockscreen(context, holder.wallpaperlockset, bitmapimg, Photo.getPhotoid());


                        if (Photo.getType().equals("ads")) {
                            increaseno(Photo.getUid(), "wallpaper");
                        }
                    }
                }
            });

            holder.comment.setOnClickListener(v -> {
                Intent i = new Intent(context, CommentActivity.class);
                Common.PHOTO_INFO = Photo.getImagepath();
                Common.TYPE = Photo.getCaption();
                Common.ID_SELECTED = Photo.getPhotoid();
                Common.FROM = Photo.getUserid();
                context.startActivity(i);
            });

            holder.wallpaperset.setOnClickListener(v -> {

                if (Photo.getImagepath().contains(".mp4")) {
                    updatecode.changelivewallpaper(context, Photo.getImagepath(), Photo.getPhotoid(), holder.view);
                } else {

                    if (holder.imageView.getDrawable() == null) {
                        Toast.makeText(context, context.getResources().getString(R.string.imagenotloadedproperly), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Bitmap bitmapimg = ((BitmapDrawable) holder.imageView.getDrawable()).getBitmap();

                    updatecode.changehomescreen(context, holder.wallpaperset, bitmapimg, Photo.getPhotoid());
                }

                if (Photo.getType().equals("ads")) {
                    increaseno(Photo.getUid(), "wallpaper");
                }

            });
            
            holder.wallpaperset.setOnLongClickListener(view -> {
                context.startActivity(new Intent(context, WallpaperSettingActivity.class));
                return false;
            });


            holder.wallpaperlockset.setOnLongClickListener(view -> {
                context.startActivity(new Intent(context, WallpaperSettingActivity.class));
                return false;
            });


            WordToSpan link = new WordToSpan();
            link.setColorTAG(context.getResources().getColor(R.color.texthashtag))
                    .setColorURL(context.getResources().getColor(R.color.texthashtag))
                    .setColorPHONE(context.getResources().getColor(R.color.texthashtag))
                    .setColorMAIL(context.getResources().getColor(R.color.texthashtag))
                    .setColorMENTION(context.getResources().getColor(R.color.texthashtag))
                    .setColorCUSTOM(context.getResources().getColor(R.color.texthashtag))
                    .setUnderlineURL(false)
                    .setLink(Photo.getCaption())
                    .into(holder.caption)
                    .setClickListener(new WordToSpan.ClickListener() {
                        @Override
                        public void onClick(String type, String text) {
                            // type: "tag", "mail", "url", "phone", "mention" or "custom"

                            if (type.trim().equals("tag")) {
                                sharedPreferences.edit().putString("hashtagtext", text).apply();
                                context.startActivity(new Intent(context, HashtagActivity.class));

                            } else if (type.trim().equals("mention")) {

                                getusernameid(context, text.trim());

                            } else if (type.trim().equals("mail")) {
                                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                                emailIntent.setType("plain/text");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{text.trim()});

                                context.startActivity(Intent.createChooser(emailIntent, context.getResources().getString(R.string.sendmail)));

                            } else if (type.trim().equals("phone")) {
                                String phono = text;
                                if (phono.length() > 10) {
                                    phono = "+" + text;
                                }
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + phono));
                                context.startActivity(intent);

                            } else if (type.trim().equals("url")) {

                                if (text.trim().contains("https://") || text.trim().contains("http://")) {
                                    Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(text.trim()));
                                    context.startActivity(viewIntent);
                                } else {
                                    Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://" + text.trim()));
                                    context.startActivity(viewIntent);
                                }
                            } else if (type.trim().equals("custom")) {
                                Log.d(TAG, "onClick: custom");
                            } else {
                                Log.d(TAG, "onClick: else");
                            }

                        }
                    });


            holder.link.setOnClickListener(v -> {

                String link1;
                if (Photo.getLink().contains("https://") || Photo.getLink().contains("http://")) {
                    link1 = Photo.getLink();
                } else {
                    link1 = "https://" + Photo.getLink().trim();
                }

                if (!Photo.getLink().isEmpty()) {

                    Intent viewIntent = new Intent("android.intent.action.VIEW",
                            Uri.parse(link1));
                    context.startActivity(viewIntent);

                }

            });


            if (position == (getItemCount() - 1)) {
                if (Photo.getType().equals("posts")) {
                    holder.progressbar.setVisibility(View.VISIBLE);
                } else {
                    holder.progressbar.setVisibility(View.GONE);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.progressbar.setVisibility(View.GONE);
                    }
                }, 3000);
            } else {
                holder.progressbar.setVisibility(View.GONE);
            }
        } catch (IndexOutOfBoundsException e) {
            ((Activity) context).finish();
            context.startActivity(new Intent(context, SplashActivity.class));
        }
    }


    private void load(String imagepath, ImageView imgbg) {
        Glide.with(context).load(imagepath)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(imgbg);

    }


    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public String usernamestring, displaynamest, profilephotost, verified, description, category, websites, backphoto, subscribed, subscribers;
        ImageView imageView, imageViewbg, profilepic, menuimg, playbtn;
        TextView dislayname, username, likes;
        CardView cardview, comment, wallpaperset, wallpaperlockset, share, delete, edit, profilecard, link;
        SparkButton favbutton;
        TextView date, caption, trendingno;
        RelativeLayout relllay, layoutofview;
        CardView menu;
        RelativeLayout mainbackhide, favlay;
        RelativeLayout mainlayout;
        SpinKitView progressbar, loadingwallpaperbar, bufferingbar;
        SparkButton likebutton;
        SimpleExoPlayerView videoview;
        SimpleExoPlayer player;
        View view;
        Boolean play = false;
        Boolean rundur = true;
        Boolean deletevisible = false;
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            cardview = itemView.findViewById(R.id.cardview);
            favbutton = itemView.findViewById(R.id.favbutton);
            imageViewbg = itemView.findViewById(R.id.imageViewbg);
            relllay = itemView.findViewById(R.id.relllay);
            date = itemView.findViewById(R.id.premiumuser);
            profilepic = itemView.findViewById(R.id.profilepic);
            profilecard = itemView.findViewById(R.id.profilecard);
            dislayname = itemView.findViewById(R.id.dislayname);
            dislayname.setVisibility(View.GONE);
            username = itemView.findViewById(R.id.username);
            username.setVisibility(View.GONE);
            menu = itemView.findViewById(R.id.menu);
            menuimg = itemView.findViewById(R.id.menuimg);
            menuimg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu_home));
            comment = itemView.findViewById(R.id.comment);
            comment.setVisibility(View.GONE);
            wallpaperset = itemView.findViewById(R.id.wallpaperset);
            wallpaperset.setVisibility(View.GONE);
            wallpaperlockset = itemView.findViewById(R.id.wallpaperlockset);
            wallpaperlockset.setVisibility(View.GONE);
            share = itemView.findViewById(R.id.share);
            share.setVisibility(View.GONE);
            mainbackhide = itemView.findViewById(R.id.mainbackhide);
            mainbackhide.setVisibility(View.GONE);
            delete = itemView.findViewById(R.id.delete);
            delete.setVisibility(View.GONE);
            edit = itemView.findViewById(R.id.edit);
            edit.setVisibility(View.GONE);
            mainlayout = itemView.findViewById(R.id.mainlayout);
            favlay = itemView.findViewById(R.id.favlay);
            favlay.setVisibility(View.GONE);
            progressbar = itemView.findViewById(R.id.progressbar);
            progressbar.setVisibility(View.GONE);
            link = itemView.findViewById(R.id.link);
            link.setVisibility(View.GONE);
            caption = itemView.findViewById(R.id.caption);
            caption.setVisibility(View.GONE);
            trendingno = itemView.findViewById(R.id.trendingno);
            trendingno.setVisibility(View.GONE);
            likes = itemView.findViewById(R.id.likeno);
            likebutton = itemView.findViewById(R.id.likebutton);
            layoutofview = itemView.findViewById(R.id.layoutofview);
            videoview = itemView.findViewById(R.id.videoview);
            videoview.setVisibility(View.GONE);
            playbtn = itemView.findViewById(R.id.playbtn);
            playbtn.setVisibility(View.GONE);
            loadingwallpaperbar = itemView.findViewById(R.id.loadingwallpaperbar);
            loadingwallpaperbar.setVisibility(View.GONE);
            bufferingbar = itemView.findViewById(R.id.bufferingbar);
            bufferingbar.setVisibility(View.GONE);
            duration = itemView.findViewById(R.id.duration);
            duration.setVisibility(View.GONE);

            Zoomy.Builder builder = new Zoomy.Builder((Activity) context).target(imageView);
            builder.register();

        }
    }


    public String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getTimestampDayNew() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getTimestamphour() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh", Locale.getDefault());
        return sdf.format(new Date());
    }


}
