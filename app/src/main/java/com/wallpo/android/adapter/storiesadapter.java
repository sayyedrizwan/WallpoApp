package com.wallpo.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;
import com.wallpo.android.LoginActivity;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.SplashActivity;
import com.wallpo.android.activity.StoriesCommentActivity;
import com.wallpo.android.getset.Stories;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class storiesadapter extends RecyclerView.Adapter<storiesadapter.ViewHolder> {
    private Context context;
    private List<Stories> Stories;

    public storiesadapter(Context context, List<Stories> Stories) {
        this.context = context;
        this.Stories = Stories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.stories_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        try{
            

        final Stories stories = Stories.get(position);

        final SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        if (id.equals(stories.getUserid())) {
            holder.options.setVisibility(View.VISIBLE);
        } else {
            holder.options.setVisibility(View.GONE);
        }


        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String datauserid = wallpouserdata.getString(stories.getUserid() + "id", "");

        if (!datauserid.isEmpty()) {

            holder.usernamestring = wallpouserdata.getString(stories.getUserid() + "username", "");
            holder.displaynamest = wallpouserdata.getString(stories.getUserid() + "displayname", "");
            holder.profilephotost = wallpouserdata.getString(stories.getUserid() + "profilephoto", "");

            holder.verified = wallpouserdata.getString(stories.getUserid() + "verified", "");
            holder.description = wallpouserdata.getString(stories.getUserid() + "description", "");
            holder.category = wallpouserdata.getString(stories.getUserid() + "category", "");
            holder.websites = wallpouserdata.getString(stories.getUserid() + "websites", "");
            holder.backphoto = wallpouserdata.getString(stories.getUserid() + "backphoto", "");
            holder.subscribed = wallpouserdata.getString(stories.getUserid() + "subscribed", "");
            holder.subscribers = wallpouserdata.getString(stories.getUserid() + "subscribers", "");

            try {

                Glide.with(context).load(holder.profilephotost).centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.profilepic)
                        .skipMemoryCache(false).into(holder.profilepic);

            } catch (IllegalArgumentException e) {
                Log.e("searchadapter", "onBindViewHolder: ", e);
            } catch (IllegalStateException e) {

                Log.e("act", "onBindViewHolder: ", e);
            }

            holder.displayname.setText(holder.displaynamest);

        } else {
            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(stories.getUserid())).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.userinfo)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    Log.e(TAG, "onFailure: ", e);
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
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
                                            .skipMemoryCache(false).into(holder.profilepic);

                                } catch (IllegalArgumentException e) {
                                    Log.e("searchadapter", "onBindViewHolder: ", e);
                                } catch (IllegalStateException e) {

                                    Log.e("act", "onBindViewHolder: ", e);
                                }

                                holder.displayname.setText(holder.displaynamest);


                                wallpouserdata.edit().putString(stories.getUserid() + "id", stories.getUserid()).apply();
                                wallpouserdata.edit().putString(stories.getUserid() + "username", holder.usernamestring).apply();
                                wallpouserdata.edit().putString(stories.getUserid() + "displayname", holder.displaynamest).apply();
                                wallpouserdata.edit().putString(stories.getUserid() + "profilephoto", holder.profilephotost).apply();

                                wallpouserdata.edit().putString(stories.getUserid() + "verified", holder.verified).apply();
                                wallpouserdata.edit().putString(stories.getUserid() + "description", holder.description).apply();
                                wallpouserdata.edit().putString(stories.getUserid() + "category", holder.category).apply();
                                wallpouserdata.edit().putString(stories.getUserid() + "websites", holder.websites).apply();
                                wallpouserdata.edit().putString(stories.getUserid() + "backphoto", holder.backphoto).apply();
                                wallpouserdata.edit().putString(stories.getUserid() + "subscribed", holder.subscribed).apply();
                                wallpouserdata.edit().putString(stories.getUserid() + "subscribers", holder.subscribers).apply();


                            }

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    });


                }
            });
        }

        holder.caption.setText(stories.getCaption().replace("\n", ""));

        if (stories.getType().equals("video")) {
            holder.golay.setVisibility(View.VISIBLE);
            holder.golay.setText(context.getResources().getString(R.string.viewvideo));
        } else if (stories.getType().equals("image")) {
            holder.golay.setVisibility(View.VISIBLE);
            holder.golay.setText(context.getResources().getString(R.string.viewimage));
        } else {
            holder.golay.getLayoutParams().height = 0;
            holder.golay.getLayoutParams().width = 0;
            holder.golay.requestLayout();
        }


        holder.profilepic.setOnClickListener(v -> {
            Intent i = new Intent(context, ProfileActivity.class);

            sharedPreferences.edit().putString("storyidprofile", String.valueOf(stories.getId())).apply();
            sharedPreferences.edit().putString("useridprofile", String.valueOf(stories.getUserid())).apply();
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

        holder.displayname.setOnClickListener(v -> {
            Intent i = new Intent(context, ProfileActivity.class);

            sharedPreferences.edit().putString("storyidprofile", String.valueOf(stories.getId())).apply();
            sharedPreferences.edit().putString("useridprofile", String.valueOf(stories.getUserid())).apply();
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

        holder.golay.setOnClickListener(view -> {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;

            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.getBehavior().setPeekHeight(height);
            dialog.setContentView(R.layout.openstories);

            CardView cancel = dialog.findViewById(R.id.cancel);

            SpinKitView loadingbar = dialog.findViewById(R.id.loadingbar);
            AppCompatTextView duration = dialog.findViewById(R.id.duration);
            duration.setVisibility(View.GONE);
            ImageView imageview = dialog.findViewById(R.id.imageview);

            imageview.getLayoutParams().height = height - 300;
            imageview.requestLayout();

            holder.videoview = dialog.findViewById(R.id.videoview);
            holder.videoview.setVisibility(View.GONE);

            holder.videoview.getLayoutParams().height = height - 200;
            holder.videoview.requestLayout();

            if (stories.getLink().contains(".mp4")) {
                imageview.setVisibility(View.GONE);
                holder.videoview.setVisibility(View.VISIBLE);
              //  duration.setVisibility(View.VISIBLE);

                try {
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    holder.player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                    Uri videoUri = Uri.parse(stories.getLink());//
                    DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("wallpo_player");
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
                    holder.videoview.setPlayer(holder.player);
                    holder.player.prepare(mediaSource);
                    holder.player.setPlayWhenReady(true);
                    holder.player.getPlaybackState();

                    holder.player.addListener(new Player.EventListener() {
                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                            if (holder.rundur) {
                                if (playbackState == ExoPlayer.STATE_READY) {
                                    long realDurationMillis = holder.player.getDuration();
                                    int dur = (int) realDurationMillis / 1000;

                                    duration.setVisibility(View.VISIBLE);
                                    duration.setText(dur + context.getResources().getString(R.string.secondsshort));

                                    holder.rundur = false;

                                    new Handler().postDelayed(() -> {

                                        int minusdur = dur - 1;

                                        duration.setText(minusdur + context.getResources().getString(R.string.secondsshort));

                                    }, 1000);

                                    new Handler().postDelayed(() -> {
                                        duration.setVisibility(View.GONE);
                                    }, 2000);

                                }
                            }

                        }
                    });


                } catch (Exception e) {
                    Log.e(TAG, "onCreate: ", e);
                }

                dialog.setOnCancelListener(dialogInterface -> {
                    holder.player.setPlayWhenReady(false);
                    holder.player.getPlaybackState();
                });
                cancel.setOnClickListener(view1 -> {
                    dialog.dismiss();
                    holder.player.setPlayWhenReady(false);
                    holder.player.getPlaybackState();
                });


            } else {

                try {
                    Glide.with(context).load(stories.getLink())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingbar.setVisibility(View.GONE);
                                        }
                                    });
                                    return false;
                                }
                            })
                            .into(imageview);

                } catch (IllegalArgumentException e) {
                    Log.e("searchadapter", "onBindViewHolder: ", e);
                } catch (IllegalStateException e) {

                    Log.e("act", "onBindViewHolder: ", e);
                }

                cancel.setOnClickListener(view1 -> {
                    dialog.dismiss();
                });

            }

            dialog.show();

        });

        holder.options.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(context, holder.options);
            popupMenu.getMenuInflater().inflate(R.menu.story_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.delete:

                        new MaterialAlertDialogBuilder(context)
                                .setTitle(context.getResources().getString(R.string.deletefirewall))
                                .setMessage(context.getResources().getString(R.string.deletefirewallnotice))
                                .setNegativeButton(context.getResources().getString(R.string.cancelbig), (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                })
                                .setPositiveButton(context.getResources().getString(R.string.deletebig), (dialog, i) -> {

                                    holder.mainlay.getLayoutParams().height = 0;
                                    holder.mainlay.getLayoutParams().width = 0;
                                    holder.mainlay.requestLayout();

                                    Toast.makeText(context, context.getResources().getString(R.string.pleasewait), Toast.LENGTH_SHORT).show();

                                    final OkHttpClient client = new OkHttpClient.Builder()
                                            .connectTimeout(3, TimeUnit.MINUTES).writeTimeout(3, TimeUnit.MINUTES)
                                            .readTimeout(3, TimeUnit.MINUTES).retryOnConnectionFailure(false)
                                            .build();

                                    RequestBody postData = new FormBody.Builder().add("id", String.valueOf(stories.getId()))
                                            .add("url", stories.getLink())
                                            .add("type", stories.getType()).add("fcm", sharedPreferences.getString("fcmtoken", "")).build();

                                    okhttp3.Request request = new okhttp3.Request.Builder()
                                            .url(URLS.deletestory)
                                            .post(postData)
                                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                            .build();

                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, context.getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show());
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            final String data = response.body().string().replaceAll(",\\[]", "");

                                            ((Activity) context).runOnUiThread(() -> {

                                                Log.d(TAG, "run: storiesdelete " + data);

                                                if (data.trim().contains("notfound")) {
                                                    Toast.makeText(context, context.getResources().getString(R.string.errordeletingfirewall), Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }

                                                if (data.trim().contains("unauth")) {
                                                    Toast.makeText(context, context.getResources().getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                                                    updatecode.updateFCM(context);
                                                    dialog.dismiss();
                                                }

                                                if (data.trim().contains("Successfully")) {
                                                    Toast.makeText(context, context.getResources().getString(R.string.deletefirewallsuccessfully), Toast.LENGTH_SHORT).show();
                                                    holder.mainlay.getLayoutParams().height = 0;
                                                    holder.mainlay.getLayoutParams().width = 0;
                                                    holder.mainlay.requestLayout();
                                                    dialog.dismiss();
                                                }


                                            });

                                        }
                                    });

                                })
                                .show();

                        return true;
                }
                return true;
            });
            popupMenu.show();
        });


        if (id.equals(stories.getUserid())) {

            holder.options.setVisibility(View.VISIBLE);
        } else {
            holder.options.setVisibility(View.GONE);
        }
        Log.d(TAG, "onBindViewHolder: " + id + stories.getUserid());

        holder.likeno.setText(updatecode.daynumber(String.valueOf(stories.getLikes())));

        if (id != null) {
            if (!id.isEmpty()) {

                final OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder().add("usersid", id)
                        .add("photoid", String.valueOf(stories.getId())).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.checkstorielikes)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {

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
                                if (data.contains("liked")) {
                                    holder.likebutton.setChecked(true);
                                } else {
                                    holder.likebutton.setChecked(false);
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

                OkHttpClient client = new OkHttpClient();

                final String tz = TimeZone.getDefault().getID();

                holder.likebutton.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if (buttonState) {
                            //like the posts
                            holder.likebutton.setChecked(true);

                            holder.likebutton.setChecked(true);

                            if (stories.getLikes() <= 999 && stories.getLikes() >= 0) {

                                int add = Integer.parseInt(holder.likeno.getText().toString().replace(",", "")) + 1;

                                holder.likeno.setText("" + add);
                            }

                            RequestBody postData = new FormBody.Builder().add("usersid", id)
                                    .add("photoid", String.valueOf(stories.getId()))
                                    .add("timezone", tz).build();

                            okhttp3.Request request = new okhttp3.Request.Builder()
                                    .url(URLS.storylikebutton)
                                    .post(postData)
                                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e(TAG, "onFailure: ", e);
                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    final String data = response.body().string().replaceAll(",\\[]", "");
                                    Log.i(TAG, "onResponse: " + data);

                                }
                            });


                        } else {

                            //unlike the posts

                            holder.likebutton.setChecked(false);
                            holder.likebutton.setChecked(false);

                            if (stories.getLikes() <= 999 && stories.getLikes() > 0) {

                                int add = Integer.parseInt(holder.likeno.getText().toString().replace(",", "")) - 1;

                                holder.likeno.setText("" + add);
                            }

                            RequestBody postData = new FormBody.Builder().add("usersid", id)
                                    .add("photoid", String.valueOf(stories.getId()))
                                    .add("timezone", tz).build();

                            okhttp3.Request request = new okhttp3.Request.Builder()
                                    .url(URLS.storyunlikebutton)
                                    .post(postData)
                                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                    .build();

                            client.newCall(request).enqueue(new Callback() {

                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e(TAG, "onFailure: ", e);
                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    final String data = response.body().string().replaceAll(",\\[]", "");
                                    Log.i(TAG, "onResponse: " + data);

                                }
                            });

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
                holder.likebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, context.getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                        holder.likebutton.setChecked(false);
                        holder.likebutton.setChecked(false);
                    }
                });
            }
        } else {
            holder.likebutton.setOnClickListener(v -> {
                Toast.makeText(context, context.getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                holder.likebutton.setChecked(false);
                holder.likebutton.setChecked(false);
            });
        }

        holder.comment.setOnClickListener(v -> {
            Intent i = new Intent(context, StoriesCommentActivity.class);
            Common.PHOTO_INFO = stories.getCaption();
            Common.USER_ID = stories.getUserid();
            Common.PHOTO_ID = String.valueOf(stories.getId());
            context.startActivity(i);
        });


        try {
            String month = stories.getDateshowed();
            String monthsno = "";

            if (month.contains("Jan") || month.contains("Jan")) {
                monthsno = "01";
            } else if (month.contains("Feb") || month.contains("feb")) {
                monthsno = "02";
            } else if (month.contains("Mar") || month.contains("mar")) {
                monthsno = "03";
            } else if (month.contains("Apr") || month.contains("apr")) {
                monthsno = "04";
            } else if (month.contains("May") || month.contains("may")) {
                monthsno = "05";
            } else if (month.contains("Jun") || month.contains("jun")) {
                monthsno = "06";
            } else if (month.contains("Jul") || month.contains("jul")) {
                monthsno = "07";
            } else if (month.contains("Aug") || month.contains("aug")) {
                monthsno = "08";
            } else if (month.contains("Sept") || month.contains("sept")) {
                monthsno = "09";
            } else if (month.contains("Oct") || month.contains("oct")) {
                monthsno = "10";
            } else if (month.contains("Nov") || month.contains("nov")) {
                monthsno = "11";
            } else if (month.contains("Dec") || month.contains("dec")) {
                monthsno = "12";
            } else {
                monthsno = "";
            }

            String date = stories.getDateshowed().substring(0, 2);
            String year = stories.getDateshowed().substring(7, 11);

            try{
                int timecal = Integer.parseInt(year.replace("-", "") + monthsno.replace("-", "") + date.replace("-", ""));

                int timecaal = Integer.parseInt(getTimestampfull()) - timecal;

                if (timecaal <= 1) {

                    holder.date.setText(context.getResources().getString(R.string.today));

                } else if (timecaal > 1 && timecaal < 100) {

                    holder.date.setText(timecaal + context.getResources().getString(R.string.dayshortago));

                } else if (timecaal > 100 && timecaal < 10000) {

                    String time = String.valueOf(timecaal).substring(0, 1);

                    holder.date.setText(time + context.getResources().getString(R.string.monthshortago));


                } else {
                    String dates;
                    if (stories.getDateshowed().length() > 10) {
                        dates = stories.getDateshowed().substring(0, 11);
                    }else {
                        dates = stories.getDateshowed();
                    }
                    holder.date.setText(dates);
                }
            }catch (NumberFormatException e){

                holder.date.setText(stories.getDateshowed());
            }

        } catch (StringIndexOutOfBoundsException e) {

            holder.date.setText(stories.getDateshowed());

        }

        if (position == (getItemCount() - 1)) {
            holder.progressbar.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> holder.progressbar.setVisibility(View.GONE), 3000);

        } else {
            holder.progressbar.setVisibility(View.GONE);
        }
        }catch (IndexOutOfBoundsException e){

            ((Activity) context).finish();
            context.startActivity(new Intent(context, SplashActivity.class));
        }

    }

    @Override
    public int getItemCount() {
        return Stories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public String usernamestring, displaynamest, profilephotost, verified, description, category, websites, backphoto, subscribed, subscribers;
        CardView cardview;
        ImageView profilepic, comment, options;
        SpinKitView progressbar;
        TextView caption, likeno, displayname;
        AppCompatButton golay;
        SparkButton likebutton;
        RelativeLayout mainlay;
        AppCompatTextView date;
        SimpleExoPlayerView videoview;
        SimpleExoPlayer player;
        Boolean rundur = true;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardview = itemView.findViewById(R.id.cardview);
            profilepic = itemView.findViewById(R.id.profilepic);
            progressbar = itemView.findViewById(R.id.progressbar);
            progressbar.setVisibility(View.GONE);
            caption = itemView.findViewById(R.id.caption);
            golay = itemView.findViewById(R.id.golay);
            options = itemView.findViewById(R.id.options);
            options.setVisibility(View.GONE);
            likebutton = itemView.findViewById(R.id.likebutton);
            likeno = itemView.findViewById(R.id.likeno);
            comment = itemView.findViewById(R.id.comment);
            mainlay = itemView.findViewById(R.id.mainlay);
            displayname = itemView.findViewById(R.id.displayname);
            date = itemView.findViewById(R.id.date);

            SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);

            int width = Integer.parseInt(sharedPreferences.getString("firewallheight", "0"));

            cardview.getLayoutParams().height = width / 2 / 2 * 3;
            cardview.getLayoutParams().width = width / 2 / 2 * 2 + 70;
            cardview.requestLayout();

        }
    }


    public String getTimestampfull() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
