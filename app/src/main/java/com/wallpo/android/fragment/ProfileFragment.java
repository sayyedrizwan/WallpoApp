package com.wallpo.android.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.activity.EditProfileActivity;
import com.wallpo.android.activity.FavouriteActivity;
import com.wallpo.android.activity.LikedPostsActivity;
import com.wallpo.android.activity.MessageActivity;
import com.wallpo.android.activity.MultipleAccountsActivity;
import com.wallpo.android.activity.TwoAuthActivity;
import com.wallpo.android.activity.UsedActivity;
import com.wallpo.android.extra.LanguageActivity;
import com.wallpo.android.extra.WallpaperSettingActivity;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.service.HourlyNoticeService;
import com.wallpo.android.subscription.PremiumActivity;
import com.wallpo.android.subscription.PremiumActivity1;
import com.wallpo.android.uploads.UploadImageActivity;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;
import com.wallpo.android.videoGallery.VideoFolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends Fragment {

    CardView logout, favourite, editprofile, cardview, twoauthsignin, likedsignin, viewprofile, premium, community;
    CardView multipleaccounts, message, usedposts, wallpapersetting, upload, image, video, changelanugau, wallposhare, feedback;
    public static ImageView profilepic;
    public static TextView displayname, textPosts;
    RelativeLayout dislay;
    LinearLayout postsplace, uploadlay;
    RelativeLayout uploadoptions;
    HorizontalScrollView scrollview;
    public static SharedPreferences sharepref;
    SwitchMaterial notificationswitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilepic = view.findViewById(R.id.profilepic);
        textPosts = view.findViewById(R.id.textPosts);
        displayname = view.findViewById(R.id.displayname);
        logout = view.findViewById(R.id.logout);
        favourite = view.findViewById(R.id.favourite);
        editprofile = view.findViewById(R.id.editprofile);
        cardview = view.findViewById(R.id.cardview);
        dislay = view.findViewById(R.id.dislay);
        postsplace = view.findViewById(R.id.postsplace);
        scrollview = view.findViewById(R.id.scrollview);
        twoauthsignin = view.findViewById(R.id.twoauthsignin);
        likedsignin = view.findViewById(R.id.likedsignin);
        viewprofile = view.findViewById(R.id.viewprofile);
        multipleaccounts = view.findViewById(R.id.multipleaccounts);
        message = view.findViewById(R.id.message);
        usedposts = view.findViewById(R.id.usedposts);
        wallpapersetting = view.findViewById(R.id.wallpapersetting);
        upload = view.findViewById(R.id.upload);
        uploadlay = view.findViewById(R.id.uploadlay);
        uploadoptions = view.findViewById(R.id.uploadoptions);
        image = view.findViewById(R.id.image);
        video = view.findViewById(R.id.video);
        changelanugau = view.findViewById(R.id.changelanugau);
        changelanugau.setVisibility(View.GONE);
        wallposhare = view.findViewById(R.id.wallposhare);
        feedback = view.findViewById(R.id.feedback);
        premium = view.findViewById(R.id.premium);
        community = view.findViewById(R.id.community);
        notificationswitch = view.findViewById(R.id.notificationswitch);

        sharepref = getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharepref.getString("wallpouserid", "");

        notificationswitch.setChecked(sharepref.getBoolean("notificationswitch", true));

        notificationswitch.setOnCheckedChangeListener((compoundButton, b) -> {
            sharepref.edit().putBoolean("notificationswitch", b).apply();
            HourlyNoticeService hourlyNoticeService = new HourlyNoticeService(getActivity());
            if(b){
                hourlyNoticeService.cancelHourlyService();
                new Handler().postDelayed(hourlyNoticeService::setHourlyService, 1000);

            }else {
                hourlyNoticeService.cancelHourlyService();

            }
        });

        favourite.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavouriteActivity.class)));
        editprofile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        multipleaccounts.setOnClickListener(v -> startActivity(new Intent(getActivity(), MultipleAccountsActivity.class)));
        wallpapersetting.setOnClickListener(v -> startActivity(new Intent(getActivity(), WallpaperSettingActivity.class)));
        changelanugau.setOnClickListener(v -> startActivity(new Intent(getActivity(), LanguageActivity.class)));

        message.setOnClickListener(v -> startActivity(new Intent(getActivity(), MessageActivity.class)));

        usedposts.setOnClickListener(v -> startActivity(new Intent(getActivity(), UsedActivity.class)));

        premium.setOnClickListener(v -> startActivity(new Intent(getActivity(), PremiumActivity1.class)));


        community.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(getActivity())
                    .setTitle(getResources().getString(R.string.joinwallpocommunity))
                    .setMessage(getResources().getString(R.string.joincommunity))
                    .setNeutralButton(getResources().getString(R.string.cancelbig), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .setPositiveButton(getResources().getString(R.string.joinbig), (dialogInterface, i) -> {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse("https://www.facebook.com/groups/2870194106532938"));

                        startActivity(viewIntent);

                        dialogInterface.dismiss();
                    })
                    .show();
        });

        feedback.setOnClickListener(view1 -> {
            BottomSheetDialog dialog = new BottomSheetDialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.feedback_layout);

            TextInputEditText sharetext = dialog.findViewById(R.id.sharetext);
            Button send = dialog.findViewById(R.id.send);

            send.setOnClickListener(view2 -> {
                if (sharetext.getText().toString().length() > 8) {
                    OkHttpClient client = new OkHttpClient();

                    dialog.dismiss();

                    RequestBody postData = new FormBody.Builder().add("emailid", sharepref.getString("emailid", ""))
                            .add("feedback", sharetext.getText().toString()).build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.feedback)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), getString(R.string.thanksyouforfeedback), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });

                }
            });

            dialog.show();
        });

        wallposhare.setOnClickListener(view1 -> {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://thewallpo.com/getwallpo");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, "https://thewallpo.com/getwallpo");
            startActivity(shareIntent);
        });

        upload.setOnClickListener(v -> {
            if (uploadlay.getVisibility() == View.VISIBLE) {

                TransitionManager.beginDelayedTransition(upload);

                uploadlay.setVisibility(View.GONE);
                uploadoptions.setVisibility(View.VISIBLE);

            } else {

                TransitionManager.beginDelayedTransition(upload);

                uploadoptions.setVisibility(View.GONE);
                uploadlay.setVisibility(View.VISIBLE);
            }
        });

        image.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UploadImageActivity.class));
        });

        video.setOnClickListener(v -> {
            Dexter.withContext(getContext()).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {
                                            Intent i = new Intent(getContext(), VideoFolder.class);
                                            Common.TYPE = "videoupload";
                                            startActivity(i);
                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response) {
                                            Toast.makeText(getContext(), getResources().getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                            token.continuePermissionRequest();
                                        }
                                    }).check();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(getContext(), getResources().getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        });


        logout.setOnClickListener(view1 -> {

            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.signout_layout);

            Button cancel = dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(v -> dialog.dismiss());

            Button signout = dialog.findViewById(R.id.signout);
            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences sharedaccounts = getApplicationContext().getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);
                    String addaccounts = sharedaccounts.getString("accounts", "");
                    addaccounts = addaccounts.replace(userid + ", ", "");

                    sharedaccounts.edit().putString("accounts", addaccounts).apply();

                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                    dialog.dismiss();
                }
            });
            dialog.show();

        });

        if (sharepref.getString("wallpouserid", "").isEmpty()) {
            cardview.setVisibility(View.GONE);
            postsplace.setVisibility(View.GONE);
            dislay.setVisibility(View.GONE);
            scrollview.setVisibility(View.GONE);
            twoauthsignin.setVisibility(View.GONE);
            likedsignin.setVisibility(View.GONE);

        }


        updatecode.analyticsFirebase(getContext(), "profile_fragment", "profile_fragment");

        viewprofile.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), ProfileActivity.class);

            sharepref.edit().putString("useridprofile", String.valueOf(sharepref.getString("wallpouserid", ""))).apply();
            sharepref.edit().putString("usernameprofile", sharepref.getString("usernameuser", "")).apply();
            sharepref.edit().putString("displaynameprofile", sharepref.getString("displaynameuser", "")).apply();
            sharepref.edit().putString("profilephotoprofile", sharepref.getString("profilephotouser", "")).apply();
            sharepref.edit().putString("verifiedprofile", sharepref.getString("verifieduser", "")).apply();
            sharepref.edit().putString("descriptionprofile", sharepref.getString("descriptionuser", "")).apply();
            sharepref.edit().putString("websitesprofile", sharepref.getString("websitesuser", "")).apply();
            sharepref.edit().putString("categoryprofile", sharepref.getString("categoryuser", "")).apply();
            sharepref.edit().putString("backphotoprofile", sharepref.getString("backphotouser", "")).apply();
            sharepref.edit().putString("subscribedprofile", sharepref.getString("subscribeduser", "")).apply();
            sharepref.edit().putString("subscriberprofile", sharepref.getString("subscribersuser", "")).apply();

            startActivity(i);
        });

        likedsignin.setOnClickListener(v -> startActivity(new Intent(getContext(), LikedPostsActivity.class)));

        twoauthsignin.setOnClickListener(v -> startActivity(new Intent(getContext(), TwoAuthActivity.class)));

        loaddata(getContext());


        return view;
    }

    public static void loaddata(Context context) {
        try {

            displayname.setText(sharepref.getString("displaynameuser", ""));
            textPosts.setText(sharepref.getString("postsusers", "0"));

            Glide.with(getApplicationContext()).load(sharepref.getString("profilephotouser", "")).centerInside()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.profilepic)
                    .skipMemoryCache(false).into(profilepic);

            if (!sharepref.getString("profilepragdata", "").equals(getTimestamp())) {

                OkHttpClient client = new OkHttpClient();

                if (!sharepref.getString("wallpouserid", "").isEmpty()) {

                    RequestBody postData = new FormBody.Builder().add("userid", sharepref.getString("wallpouserid", ""))
                            .add("fcm", sharepref.getString("fcmtoken", "")).build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.userinfo)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("ProfileFragment", "onFailure: " + e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");

                            ((Activity) context).runOnUiThread(() -> {

                                try {
                                    JSONArray array = new JSONArray(data);

                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String usernametxt = object.getString("username").trim();
                                        String displaynametxt = object.getString("displayname").trim();
                                        String profilephoto = object.getString("profilephoto").trim();

                                        String verifiedtxt = object.getString("verified").trim();
                                        String description = object.getString("description").trim();
                                        String categorytxt = object.getString("category").trim();
                                        String websites = object.getString("websites").trim();
                                        String backphoto = object.getString("backphoto").trim();
                                        String phonenumber = object.getString("phonenumber").trim();
                                        String emailverified = object.getString("emailverified").trim();
                                        String poststxt = object.getString("posts").trim();
                                        String subscribed = object.getString("subscribed").trim();
                                        String subscribers = object.getString("subscribers").trim();
                                        String emailids = object.getString("email").trim();


                                        sharepref.edit().putString("emailid", emailids).apply();
                                        sharepref.edit().putString("usernameuser", usernametxt).apply();
                                        sharepref.edit().putString("displaynameuser", displaynametxt).apply();
                                        sharepref.edit().putString("profilephotouser", profilephoto).apply();
                                        sharepref.edit().putString("verifieduser", verifiedtxt).apply();

                                        sharepref.edit().putString("descriptionuser", description).apply();
                                        sharepref.edit().putString("categoryuser", categorytxt).apply();
                                        sharepref.edit().putString("websitesuser", websites).apply();
                                        sharepref.edit().putString("backphotouser", backphoto).apply();
                                        sharepref.edit().putString("subscribeduser", subscribed).apply();
                                        sharepref.edit().putString("subscribersuser", subscribers).apply();
                                        sharepref.edit().putString("phonenumberuser", phonenumber).apply();
                                        sharepref.edit().putString("emailverifieduser", emailverified).apply();
                                        sharepref.edit().putString("profilepragdata", getTimestamp()).apply();
                                        sharepref.edit().putString("postsusers", poststxt).apply();

                                        displayname.setText(displaynametxt);

                                        try {

                                            Glide.with(getApplicationContext()).load(profilephoto).centerInside()
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .placeholder(R.mipmap.profilepic)
                                                    .skipMemoryCache(false).into(profilepic);

                                        } catch (IllegalArgumentException e) {
                                            Log.e("additionalactivity", "onBindViewHolder: ", e);
                                        } catch (IllegalStateException e) {

                                            Log.e("act", "onBindViewHolder: ", e);
                                        }

                                        textPosts.setText(poststxt);

                                    }

                                } catch (
                                        JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                        }
                    });

                }

            }

        } catch (NullPointerException e) {
            Log.d(TAG, "loaddataprofile: not opened");
        }

    }

    public static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.Profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));
        } catch (NullPointerException e) {
            Log.d("ProfileFragment", "onStart: ");
        }

    }
}
