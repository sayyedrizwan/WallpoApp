package com.wallpo.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;
import com.wallpo.android.R;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;
import static com.wallpo.android.utils.updatecode.addfav;
import static com.wallpo.android.utils.updatecode.addlike;
import static com.wallpo.android.utils.updatecode.getusernameid;
import static com.wallpo.android.utils.updatecode.trendingcounts;
import static com.wallpo.android.utils.updatecode.unfav;
import static com.wallpo.android.utils.updatecode.unlike;
import static com.wallpo.android.utils.updatecode.updateFCM;

public class ViewPostsActivity extends AppCompatActivity {

    BottomSheetBehavior bottomSheetBehavior;
    CardView bottomup, linkbutton, delete, wallpaperset, wallpaperlockset, edit, back, share, comment;
    ImageView imagview, imageView1, profilepic;
    Context context = this;
    CoordinatorLayout mainlay;
    View bottomsheet;
    boolean run;
    RelativeLayout viewlayout, background, rellay;
    TextView caption, date, likeno, dislayname, username;
    SparkButton favbutton, likebutton;
    SpinKitView bufferingbar;
    SimpleExoPlayerView videoview;
    SimpleExoPlayer player;
    String imagepath, photoid;
    AppCompatTextView duration;
    Boolean rundur = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_view_posts);
        new Handler().postDelayed(() -> runfullscreen(), 1500);

        bottomsheet = findViewById(R.id.bottomsheet);
        bottomup = findViewById(R.id.bottomup);
        imagview = findViewById(R.id.imagview);
        imageView1 = findViewById(R.id.imagview1);
        mainlay = findViewById(R.id.mainlay);
        viewlayout = findViewById(R.id.viewlayout);
        background = findViewById(R.id.background);
        background.setVisibility(View.GONE);
        caption = findViewById(R.id.caption);
        rellay = findViewById(R.id.rellay);
        rellay.setVisibility(View.GONE);
        date = findViewById(R.id.date);
        likeno = findViewById(R.id.likeno);
        favbutton = findViewById(R.id.favbutton);
        linkbutton = findViewById(R.id.link);
        likebutton = findViewById(R.id.likebutton);
        delete = findViewById(R.id.delete);
        delete.setVisibility(View.GONE);
        wallpaperset = findViewById(R.id.wallpaperset);
        wallpaperlockset = findViewById(R.id.wallpaperlockset);
        edit = findViewById(R.id.edit);
        edit.setVisibility(View.GONE);
        profilepic = findViewById(R.id.profilepic);
        dislayname = findViewById(R.id.dislayname);
        username = findViewById(R.id.username);
        back = findViewById(R.id.back);
        bufferingbar = findViewById(R.id.bufferingbar);
        bufferingbar.setVisibility(View.GONE);
        videoview = findViewById(R.id.videoview);
        videoview.setVisibility(View.GONE);
        share = findViewById(R.id.share);
        duration = findViewById(R.id.duration);
        duration.setVisibility(View.GONE);
        comment = findViewById(R.id.comment);

        Zoomy.Builder builder = new Zoomy.Builder((Activity) context).target(imagview);
        builder.register();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        userinfos();

        updatecode.analyticsFirebase(context, "view_posts_activity", "view_posts_activity");

        bottomsheet.getLayoutParams().height = height / 2 / 2 / 2 * 3;

        bottomsheet.requestLayout();

        back.setOnClickListener(view -> {
            finish();
        });

        imageView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                run = true;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //visible

                    run = false;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition(mainlay);
                    }
                    bottomsheet.setVisibility(View.VISIBLE);

                    viewlayout.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (run) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    TransitionManager.beginDelayedTransition(mainlay);
                                }
                                bottomsheet.setVisibility(View.VISIBLE);
                                viewlayout.setVisibility(View.VISIBLE);

                            }
                        }
                    }, 400);


                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //gone

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (run) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    TransitionManager.beginDelayedTransition(mainlay);
                                }
                                bottomsheet.setVisibility(View.GONE);
                                viewlayout.setVisibility(View.GONE);
                            }
                        }
                    }, 700);
                }


                return true;
            }
        });

        final SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        imagepath = sharedPreferences.getString("imagepath", "");
        photoid = sharedPreferences.getString("photoid", "");

        share.setOnClickListener(view -> {
            Common.SHARETYPE = "posts";
            Common.MESSAGE_DATA = String.valueOf(photoid);
            updatecode.shareintent(context, "posts/" + photoid);
        });

        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);

        bottomup.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //collapsed
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runfullscreen();
                            }
                        }, 2500);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(mainlay);
                        }
                        background.setVisibility(View.GONE);
                        rellay.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //dragging
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runfullscreen();
                            }
                        }, 2500);

                        background.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runfullscreen();
                            }
                        }, 2500);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        //fullexpanded
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runfullscreen();
                            }
                        }, 2500);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(mainlay);
                        }
                        background.setVisibility(View.VISIBLE);
                        rellay.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        //hidden
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runfullscreen();
                            }
                        }, 2500);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(mainlay);
                        }
                        background.setVisibility(View.GONE);
                        rellay.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runfullscreen();
                            }
                        }, 2500);
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d("ViewPostsActivity", "onSlide: slidding....");
            }
        });

        SharedPreferences sharepref = getApplicationContext().getSharedPreferences("wallpo", 0);
        try {

            Glide.with(context).load(sharepref.getString("imagepath", ""))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            runOnUiThread(() -> Toast.makeText(context, getResources().getString(R.string.errorloadingimage), Toast.LENGTH_SHORT).show());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, final Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            runOnUiThread(() -> imageView1.setImageDrawable(resource));
                            return false;
                        }
                    }).into(imagview);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        if (sharepref.getString("imagepath", "").contains(".mp4")) {
            videoview.setVisibility(View.VISIBLE);
            try {
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                Uri videoUri = Uri.parse(sharepref.getString("imagepath", ""));//
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("wallpo_player");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
                videoview.setPlayer(player);
                player.prepare(mediaSource);

                player.setPlayWhenReady(true);
                player.getPlaybackState();

                player.addListener(new Player.EventListener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (rundur) {
                            if (playbackState == ExoPlayer.STATE_READY) {
                                long realDurationMillis = player.getDuration();
                                int dur = (int) realDurationMillis / 1000;

                                duration.setVisibility(View.VISIBLE);
                                duration.setText(dur + "s");


                                rundur = false;

                                new Handler().postDelayed(() -> {

                                    int minusdur = dur - 1;

                                    duration.setText(minusdur + "s");

                                }, 1000);

                                new Handler().postDelayed(() -> {
                                    duration.setVisibility(View.GONE);
                                }, 2000);

                            }
                        }

                        if (playbackState == ExoPlayer.STATE_BUFFERING) {
                            bufferingbar.setVisibility(View.VISIBLE);
                        } else {
                            bufferingbar.setVisibility(View.INVISIBLE);
                        }

                    }
                });


            } catch (Exception e) {
                Log.e(TAG, "onCreate: ", e);
            }
        }


        date.setText(sharedPreferences.getString("dateshowed", ""));


        final int likk = Integer.parseInt(sharedPreferences.getString("likes", "0"));

        if (likk < 1) {
            likeno.setText("0");
        } else if (likk < 999 && likk > 1) {
            likeno.setText("" + likk);
        } else if (likk < 9999 && likk > 999) {
            String no = String.valueOf(likk).substring(0, 1);
            likeno.setText(no + "K");
        } else if (likk < 99999 && likk > 9999) {
            String no = String.valueOf(likk).substring(0, 2);
            likeno.setText(no + "K");
        } else if (likk < 999999 && likk > 99999) {
            String no = String.valueOf(likk).substring(0, 3);
            likeno.setText(no + "K");
        } else if (likk < 9999999 && likk > 999999) {
            String no = String.valueOf(likk).substring(0, 1);
            likeno.setText(no + "M ");
        } else if (likk < 99999999 && likk > 9999999) {
            String no = String.valueOf(likk).substring(0, 2);
            likeno.setText(no + "M ");
        } else if (likk < 999999999 && likk > 99999999) {
            String no = String.valueOf(likk).substring(0, 3);
            likeno.setText(no + "M ");
        } else {

            try {
                // The comma in the format specifier does the trick
                String s = String.format("%,d", Long.parseLong(likk + ""));

                likeno.setText(s);

            } catch (
                    NumberFormatException e) {
                Log.i(TAG, "onResponse: error");
            }
        }

        WordToSpan link = new WordToSpan();
        link.setColorTAG(context.getResources().getColor(R.color.texthashtag))
                .setColorURL(context.getResources().getColor(R.color.white))
                .setColorPHONE(context.getResources().getColor(R.color.white))
                .setColorMAIL(context.getResources().getColor(R.color.white))
                .setColorMENTION(context.getResources().getColor(R.color.texthashtag))
                .setColorCUSTOM(context.getResources().getColor(R.color.white))
                .setUnderlineURL(false)
                .setLink(sharedPreferences.getString("caption", ""))
                .into(caption)
                .setClickListener(new WordToSpan.ClickListener() {
                    @Override
                    public void onClick(String type, String text) {
                        if (type.trim().equals("tag")) {

                            sharedPreferences.edit().putString("hashtagtext", text).apply();
                            context.startActivity(new Intent(context, HashtagActivity.class));

                        } else if (type.trim().equals("mention")) {

                            getusernameid(context, text.trim());

                        } else if (type.trim().equals("mail")) {

                            Log.d(TAG, "onClick: mail");
                        } else if (type.trim().equals("url")) {

                            Log.d(TAG, "onClick: custom");
                        } else if (type.trim().equals("url")) {

                            Log.d(TAG, "onClick: custom");
                        } else if (type.trim().equals("custom")) {
                            Log.d(TAG, "onClick: custom");
                        } else {
                            Log.d(TAG, "onClick: else");
                        }

                    }
                });

        final String id = sharedPreferences.getString("wallpouserid", "");


        comment.setOnClickListener(v -> {
            Intent i = new Intent(context, CommentActivity.class);
            Common.PHOTO_INFO = sharedPreferences.getString("imagepath", "");
            Common.TYPE = sharedPreferences.getString("caption", "");
            Common.ID_SELECTED = Integer.parseInt(photoid);
            Common.FROM = sharedPreferences.getString("photoid", "useridprofile");
            context.startActivity(i);
        });

        if (id != null) {
            if (!id.isEmpty()) {

                final OkHttpClient clientqq = new OkHttpClient();

                RequestBody postData = new FormBody.Builder()
                        .add("photoid", String.valueOf(sharedPreferences.getString("photoid", "")))
                        .add("usersid", id).build();

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
                                    favbutton.setChecked(false);
                                } else {

                                    favbutton.setChecked(true);
                                }

                            }
                        });

                    }
                });


            } else {
                favbutton.setOnClickListener(v -> {
                    Toast.makeText(context, getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                    favbutton.setChecked(false);
                    favbutton.setChecked(false);
                });
            }
        } else {
            favbutton.setOnClickListener(v -> {
                Toast.makeText(context, getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                favbutton.setChecked(false);
                favbutton.setChecked(false);
            });
        }


        trendingcounts(context, Integer.parseInt(sharedPreferences.getString("photoid", "0")));


        if (id != null) {
            if (!id.isEmpty()) {

                favbutton.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if (buttonState) {
                            //fav the posts

                            favbutton.setChecked(true);

                            favbutton.setChecked(true);

                            addfav(context, Integer.parseInt(sharedPreferences.getString("photoid", "0")));


                        } else {

                            //unfav the posts

                            favbutton.setChecked(false);

                            favbutton.setChecked(false);

                            unfav(context, Integer.parseInt(sharedPreferences.getString("photoid", "0")));

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
                favbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(context, getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                        favbutton.setChecked(false);
                        favbutton.setChecked(false);
                    }
                });
            }
        } else {
            favbutton.setOnClickListener(v -> {
                Toast.makeText(context, getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                favbutton.setChecked(false);
                favbutton.setChecked(false);
            });
        }


        final SQLiteDatabase mDatabase = null;

        if (id != null) {
            if (!id.isEmpty()) {

                final OkHttpClient cliento = new OkHttpClient();

                RequestBody postData = new FormBody.Builder()
                        .add("photoid", String.valueOf(photoid))
                        .add("usersid", id).build();

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

                        ((Activity) context).runOnUiThread(() -> {
                            if (data.trim().contains("not liked")) {
                                //notliked

                                likebutton.setChecked(false);

                            } else {
                                //like

                                likebutton.setChecked(true);

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

                likebutton.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if (buttonState) {
                            //like the posts
                            likebutton.setChecked(true);

                            likebutton.setChecked(true);

                            if (Integer.parseInt(sharedPreferences.getString("likes", "0")) <= 999 && Integer.parseInt(sharedPreferences.getString("likes", "0")) >= 0) {

                                int add = Integer.parseInt(likeno.getText().toString().replace(",", "")) + 1;

                                likeno.setText("" + add);
                            }

                            addlike(context, Integer.parseInt(photoid));


                        } else {

                            //unlike the posts

                            likebutton.setChecked(false);
                            likebutton.setChecked(false);

                            if (Integer.parseInt(sharedPreferences.getString("likes", "0")) <= 999 && Integer.parseInt(sharedPreferences.getString("likes", "0")) > 0) {

                                int add = Integer.parseInt(likeno.getText().toString().replace(",", "")) - 1;

                                likeno.setText("" + add);
                            }

                            unlike(context, Integer.parseInt(photoid));

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
                likebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                        likebutton.setChecked(false);
                        likebutton.setChecked(false);
                    }
                });
            }
        } else {
            likebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, getResources().getString(R.string.logintolikeposts), Toast.LENGTH_SHORT).show();

                    likebutton.setChecked(false);
                    likebutton.setChecked(false);
                }
            });
        }

        final String linkstxt = sharedPreferences.getString("link", "");

        if (linkstxt.isEmpty()) {
            linkbutton.setVisibility(View.GONE);
        }
        linkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String link;
                if (linkstxt.contains("https://") || linkstxt.contains("http://")) {
                    link = linkstxt;
                } else {
                    link = "https://" + linkstxt.trim();
                }

                if (!linkstxt.isEmpty()) {

                    Intent viewIntent = new Intent("android.intent.action.VIEW",
                            Uri.parse(link));
                    context.startActivity(viewIntent);

                }

            }
        });

        if (id.equals(sharedPreferences.getString("userid", ""))) {
            delete.setVisibility(View.VISIBLE);

            delete.setOnClickListener(v -> {
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
                caption.setText(sharedPreferences.getString("caption", ""));

                try {

                    Glide.with(context).load(sharedPreferences.getString("imagepath", "")).centerCrop()
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


                    final OkHttpClient clientdel = new OkHttpClient();

                    final SharedPreferences sharedPreferences1 = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
                    final String fcmtoken = sharedPreferences1.getString("fcmtoken", "");

                    RequestBody postData = new FormBody.Builder()
                            .add("photoid", photoid)
                            .add("userid", id).add("fcm", fcmtoken).build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.deletephoto)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    clientdel.newCall(request).enqueue(new okhttp3.Callback() {

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
                                        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);
                                        sharedusersdata.edit().putString(id + "postsprofile", "").apply();

                                        Common.ALBUM_STATUS = "changed";
                                        finish();
                                        dialog.dismiss();

                                    }

                                }
                            });


                        }
                    });
                });
                dialog.show();
            });


            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(v -> {
                //editview
                updatecode.editposts(context, Integer.parseInt(photoid));

            });


        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            wallpaperlockset.setVisibility(View.GONE);
        }

        wallpaperlockset.setOnClickListener(v -> {
            if (imagepath.contains(".mp4")) {
                updatecode.changelivewallpaper(context, imagepath, Integer.parseInt(photoid), mainlay);
            } else {
                if (imagview.getDrawable() == null) {
                    Toast.makeText(context, "Image is not loaded properly. ", Toast.LENGTH_SHORT).show();

                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    Bitmap bitmapimg = ((BitmapDrawable) imagview.getDrawable()).getBitmap();

                    updatecode.changelockscreen(context, wallpaperlockset, bitmapimg, Integer.parseInt(photoid));

                }
            }

        });


        wallpaperset.setOnClickListener(v -> {

            if (imagepath.contains(".mp4")) {
                updatecode.changelivewallpaper(context, imagepath, Integer.parseInt(photoid), wallpaperset);
            } else {

                if (imagview.getDrawable() == null) {
                    Toast.makeText(context, "Image is not loaded properly. ", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bitmap bitmapimg = ((BitmapDrawable) imagview.getDrawable()).getBitmap();

                updatecode.changehomescreen(context, wallpaperset, bitmapimg, Integer.parseInt(photoid));
            }
        });


    }

    private void userinfos() {

        SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);
        String userid = sharepref.getString("useridprofile", "");


        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String datauserid = wallpouserdata.getString(userid + "id", "");

        if (!datauserid.isEmpty()) {

            String usernamestring = wallpouserdata.getString(userid + "username", "");
            String displaynamest = wallpouserdata.getString(userid + "displayname", "");
            String profilephotost = wallpouserdata.getString(userid + "profilephoto", "");

            String verified = wallpouserdata.getString(userid + "verified", "");
            String description = wallpouserdata.getString(userid + "description", "");
            String category = wallpouserdata.getString(userid + "category", "");
            String websites = wallpouserdata.getString(userid + "websites", "");
            String backphoto = wallpouserdata.getString(userid + "backphoto", "");
            String subscribed = wallpouserdata.getString(userid + "subscribed", "");
            String subscribers = wallpouserdata.getString(userid + "subscribers", "");


            try {

                Glide.with(context).load(profilephotost).centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.profilepic)
                        .apply(new RequestOptions().override(290, 290))
                        .skipMemoryCache(false).into(profilepic);

            } catch (IllegalArgumentException e) {
                Log.e("searchadapter", "onBindViewHolder: ", e);
            } catch (IllegalStateException e) {

                Log.e("act", "onBindViewHolder: ", e);
            }

            dislayname.setText(displaynamest);
            username.setText("@ " + usernamestring);

            profilepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ProfileActivity.class);

                    sharepref.edit().putString("postsidprofile", "").apply();
                    sharepref.edit().putString("useridprofile", String.valueOf(userid)).apply();
                    sharepref.edit().putString("usernameprofile", usernamestring).apply();
                    sharepref.edit().putString("displaynameprofile", displaynamest).apply();
                    sharepref.edit().putString("profilephotoprofile", profilephotost).apply();
                    sharepref.edit().putString("verifiedprofile", verified).apply();
                    sharepref.edit().putString("descriptionprofile", description).apply();
                    sharepref.edit().putString("websitesprofile", websites).apply();
                    sharepref.edit().putString("categoryprofile", category).apply();
                    sharepref.edit().putString("backphotoprofile", backphoto).apply();
                    sharepref.edit().putString("subscribedprofile", subscribed).apply();
                    sharepref.edit().putString("subscriberprofile", subscribers).apply();

                    context.startActivity(i);

                }
            });

            dislayname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ProfileActivity.class);

                    sharepref.edit().putString("useridprofile", String.valueOf(userid)).apply();
                    sharepref.edit().putString("usernameprofile", usernamestring).apply();
                    sharepref.edit().putString("displaynameprofile", displaynamest).apply();
                    sharepref.edit().putString("profilephotoprofile", profilephotost).apply();
                    sharepref.edit().putString("verifiedprofile", verified).apply();
                    sharepref.edit().putString("descriptionprofile", description).apply();
                    sharepref.edit().putString("websitesprofile", websites).apply();
                    sharepref.edit().putString("categoryprofile", category).apply();
                    sharepref.edit().putString("backphotoprofile", backphoto).apply();
                    sharepref.edit().putString("subscribedprofile", subscribed).apply();
                    sharepref.edit().putString("subscriberprofile", subscribers).apply();

                    context.startActivity(i);

                }
            });


        } else {
            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder()
                    .add("userid", String.valueOf(userid)).build();

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

                                    String usernamestring = object.getString("username").trim();
                                    String displaynamest = object.getString("displayname").trim();
                                    String profilephotost = object.getString("profilephoto").trim();

                                    String verified = object.getString("verified").trim();
                                    String description = object.getString("description").trim();
                                    String category = object.getString("category").trim();
                                    String websites = object.getString("websites").trim();
                                    String backphoto = object.getString("backphoto").trim();
                                    String subscribed = object.getString("subscribed").trim();
                                    String subscribers = object.getString("subscribers").trim();


                                    try {

                                        Glide.with(context).load(profilephotost).centerInside()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .placeholder(R.mipmap.profilepic)
                                                .skipMemoryCache(false).into(profilepic);

                                    } catch (IllegalArgumentException e) {
                                        Log.e("searchadapter", "onBindViewHolder: ", e);
                                    } catch (IllegalStateException e) {

                                        Log.e("act", "onBindViewHolder: ", e);
                                    }
                                    dislayname.setText(displaynamest);
                                    username.setText("@ " + usernamestring);

                                    wallpouserdata.edit().putString(userid + "id", userid).apply();
                                    wallpouserdata.edit().putString(userid + "username", usernamestring).apply();
                                    wallpouserdata.edit().putString(userid + "displayname", displaynamest).apply();
                                    wallpouserdata.edit().putString(userid + "profilephoto", profilephotost).apply();

                                    wallpouserdata.edit().putString(userid + "verified", verified).apply();
                                    wallpouserdata.edit().putString(userid + "description", description).apply();
                                    wallpouserdata.edit().putString(userid + "category", category).apply();
                                    wallpouserdata.edit().putString(userid + "websites", websites).apply();
                                    wallpouserdata.edit().putString(userid + "backphoto", backphoto).apply();
                                    wallpouserdata.edit().putString(userid + "subscribed", subscribed).apply();
                                    wallpouserdata.edit().putString(userid + "subscribers", subscribers).apply();


                                    profilepic.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(context, ProfileActivity.class);

                                            sharepref.edit().putString("useridprofile", String.valueOf(userid)).apply();
                                            sharepref.edit().putString("usernameprofile", usernamestring).apply();
                                            sharepref.edit().putString("displaynameprofile", displaynamest).apply();
                                            sharepref.edit().putString("profilephotoprofile", profilephotost).apply();
                                            sharepref.edit().putString("verifiedprofile", verified).apply();
                                            sharepref.edit().putString("descriptionprofile", description).apply();
                                            sharepref.edit().putString("websitesprofile", websites).apply();
                                            sharepref.edit().putString("categoryprofile", category).apply();
                                            sharepref.edit().putString("backphotoprofile", backphoto).apply();
                                            sharepref.edit().putString("subscribedprofile", subscribed).apply();
                                            sharepref.edit().putString("subscriberprofile", subscribers).apply();

                                            context.startActivity(i);

                                        }
                                    });

                                    dislayname.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(context, ProfileActivity.class);

                                            sharepref.edit().putString("useridprofile", String.valueOf(userid)).apply();
                                            sharepref.edit().putString("usernameprofile", usernamestring).apply();
                                            sharepref.edit().putString("displaynameprofile", displaynamest).apply();
                                            sharepref.edit().putString("profilephotoprofile", profilephotost).apply();
                                            sharepref.edit().putString("verifiedprofile", verified).apply();
                                            sharepref.edit().putString("descriptionprofile", description).apply();
                                            sharepref.edit().putString("websitesprofile", websites).apply();
                                            sharepref.edit().putString("categoryprofile", category).apply();
                                            sharepref.edit().putString("backphotoprofile", backphoto).apply();
                                            sharepref.edit().putString("subscribedprofile", subscribed).apply();
                                            sharepref.edit().putString("subscriberprofile", subscribers).apply();

                                            context.startActivity(i);

                                        }
                                    });


                                }

                            } catch (
                                    JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runfullscreen();
            }
        }, 1500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runfullscreen();
            }
        }, 1500);
    }

    private void runfullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }
}