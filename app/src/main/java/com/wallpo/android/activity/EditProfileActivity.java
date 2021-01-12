package com.wallpo.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wallpo.android.R;
import com.wallpo.android.cropper.CropImage;
import com.wallpo.android.cropper.CropImageView;
import com.wallpo.android.fragment.ProfileFragment;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfileActivity extends AppCompatActivity {

    String id, email;
    ImageView backimg, profilepic;
    CardView verified;
    TextInputEditText username, name, category, website, bio, emailid, phonenumbers;
    SharedPreferences sharedPreferences;
    Context context = this;
    Boolean tc = true;
    RelativeLayout rellay, update, veriflayout;
    SpinKitView progressbar;
    TextView savetxt, changeprofilephoto, changebackphoto, txtmail, emailnot;
    CardView changepassword, verifyemailsend;
    public static String imagechange = "";
    Bitmap bitmap;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profiles);
        sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        imagechange = "";

        id = sharedPreferences.getString("wallpouserid", "");
        email = sharedPreferences.getString("emailid", "");

        backimg = findViewById(R.id.backimg);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);
        verified = findViewById(R.id.verified);
        verified.setVisibility(View.GONE);
        profilepic = findViewById(R.id.profilepic);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        category = findViewById(R.id.category);
        website = findViewById(R.id.website);
        bio = findViewById(R.id.bio);
        phonenumbers = findViewById(R.id.phonenumber);
        emailid = findViewById(R.id.emailid);
        emailid.setText(email);
        rellay = findViewById(R.id.rellay);
        savetxt = findViewById(R.id.savetxt);
        update = findViewById(R.id.signup);
        changepassword = findViewById(R.id.changepassword);
        veriflayout = findViewById(R.id.veriflayout);
        veriflayout.setVisibility(View.GONE);
        changeprofilephoto = findViewById(R.id.changeprofilephoto);
        changebackphoto = findViewById(R.id.changebackphoto);
        txtmail = findViewById(R.id.txtmail);
        verifyemailsend = findViewById(R.id.verifyemailsend);
        emailnot = findViewById(R.id.emailnot);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        rellay.getLayoutParams().height = height / 2 / 2 * 3 - 200;
        rellay.requestLayout();

        updatecode.analyticsFirebase(context, "profile_edited", "profile_edited");

        verifyemailsend.setOnClickListener(v -> {

            if (imagechange.equals("emailchanged")) {
                profiledetail();
            } else {
                profiledetail();

                verifyemail("", email);
            }

        });


        update.setOnClickListener(v -> {

            if (savetxt.getVisibility() == View.GONE) {
                return;
            }

            if (name.getText().toString().isEmpty()) {
                Snackbar.make(rellay, getResources().getString(R.string.enteryourname), Snackbar.LENGTH_LONG)
                        .show();

                return;
            }

            if (!sharedPreferences.getString("emailid", "").equals(emailid.getText().toString())) {

                Toast.makeText(context, getResources().getString(R.string.changedyouremail), Toast.LENGTH_SHORT).show();

                verifyemail("changemail", emailid.getText().toString());


                return;
            }

            TransitionManager.beginDelayedTransition(rellay);

            savetxt.setVisibility(View.GONE);
            progressbar.setVisibility(View.VISIBLE);

            if (!sharedPreferences.getString("phonenumberuser", "").equals(phonenumbers.getText().toString())) {

            }

            if (!sharedPreferences.getString("usernameuser", "").equals(username.getText().toString())) {

                TransitionManager.beginDelayedTransition(rellay);

                savetxt.setVisibility(View.GONE);
                progressbar.setVisibility(View.VISIBLE);

                OkHttpClient client = new OkHttpClient();
                final String usernametext = usernameedit(username.getText().toString().trim().replace("'", "\\'"));

                RequestBody postData = new FormBody.Builder().add("userid", id)
                        .add("useremail", email.trim().replace("'", "\\'")).add("username", usernametext).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.updateusername)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {

                            TransitionManager.beginDelayedTransition(rellay);

                            progressbar.setVisibility(View.GONE);
                            savetxt.setVisibility(View.VISIBLE);
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");

                        runOnUiThread(() -> {
                            TransitionManager.beginDelayedTransition(rellay);

                            progressbar.setVisibility(View.GONE);
                            savetxt.setVisibility(View.VISIBLE);

                            if (data.trim().contains("available")) {

                                Snackbar.make(rellay, getResources().getString(R.string.usernameisregistered), Snackbar.LENGTH_LONG)
                                        .show();

                            } else if (data.trim().contains("successfully")) {

                                sharedPreferences.edit().putString("usernameuser", username.getText().toString()).apply();
                                Snackbar.make(rellay, getResources().getString(R.string.usernamechanged), Snackbar.LENGTH_LONG)
                                        .show();

                                if (id.equals(sharedPreferences.getString("wallpouserid", ""))) {
                                    ProfileActivity.loaddataprofile(context);
                                }

                                profiledetail();
                                sharedPreferences.edit().putString("profilepragdata", "").apply();
                                ProfileFragment.loaddata(context);

                            } else {

                                Snackbar.make(rellay, getResources().getString(R.string.errorupdatingusername), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                });

                return;

            }

            updatedata();
        });

        changepassword.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.changepassword);

            SpinKitView progressbar = dialog.findViewById(R.id.progressbar);
            progressbar.setVisibility(View.GONE);
            TextInputEditText oldpass = dialog.findViewById(R.id.oldpass);
            TextInputEditText newpass = dialog.findViewById(R.id.newpass);

            Button changebutton = dialog.findViewById(R.id.changebutton);
            changebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (oldpass.getText().toString().equals(newpass.getText().toString())) {
                        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.passwordcannotbesame), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (newpass.getText().toString().length() < 6) {
                        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.passwordcannotbemorethan), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressbar.setVisibility(View.VISIBLE);

                    OkHttpClient client = new OkHttpClient();

                    RequestBody postData = new FormBody.Builder().add("userid", id)
                            .add("oldpassword", oldpass.getText().toString().trim().replace("'", "\\'"))
                            .add("newpassword", newpass.getText().toString().trim().replace("'", "\\'"))
                            .build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.updatepassword)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    progressbar.setVisibility(View.GONE);
                                    Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    progressbar.setVisibility(View.GONE);
                                    if (data.trim().contains("error")) {
                                        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.errorupdatingpassword), Toast.LENGTH_LONG).show();
                                        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.errorupdatingpassword), Toast.LENGTH_LONG).show();
                                    }

                                    if (data.trim().contains("wrong")) {
                                        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.wrongpassword), Toast.LENGTH_LONG).show();
                                        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.wrongpassword), Toast.LENGTH_LONG).show();
                                    }


                                    if (data.trim().contains("successfully")) {
                                        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.passwordchanged), Toast.LENGTH_LONG).show();
                                        Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.passwordchanged), Toast.LENGTH_LONG).show();

                                        dialog.dismiss();
                                    }


                                }
                            });

                        }
                    });
                }
            });


            Button cancel = dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();
        });


        if (sharedPreferences.getString("displaynameuser", "").isEmpty()) {
            profiledetail();
        }

        loaddataprofile();

        changeprofilephoto.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            imagechange = "changeprofilepic";
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 124);
        });

        changebackphoto.setOnClickListener(v -> {

            Intent galleryIntent = new Intent();
            imagechange = "changebackphoto";
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 154);
        });

    }

    private void verifyemail(String type, String email) {
        emailnot.setText(getResources().getString(R.string.verficationemailsendnotice));
        txtmail.setText(getResources().getString(R.string.verify));
        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("id", id)
                .add("email", email).add("displayname", sharedPreferences.getString("displaynameuser", ""))
                .add("type", type)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.sendverificationmail)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(context, getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(() -> {
                    imagechange = "emailchanged";

                    profiledetail();

                    if (data.trim().contains("Email registered")){
                        Snackbar.make(rellay, getResources().getString(R.string.emailinuse), Snackbar.LENGTH_LONG)
                                .show();

                        new Handler().postDelayed(() -> profiledetail(), 1000);
                        return;
                    }

                    if (data.trim().contains("successfully")) {
                        if (type.equals("changemail")) {

                            Snackbar.make(rellay, getResources().getString(R.string.emailchanged), Snackbar.LENGTH_LONG)
                                    .show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    profiledetail();
                                }
                            }, 1000);
                        }
                    } else {
                        Snackbar.make(rellay, getResources().getString(R.string.errorupdatingemail), Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });
    }

    private void updatedata() {
        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", id).add("useremail", email)
                .add("description", bio.getText().toString().trim().replace("'", "\\'"))
                .add("displayname", name.getText().toString().trim().replace("'", "\\'"))
                .add("websites", website.getText().toString().trim().replace("'", "\\'"))
                .add("category", category.getText().toString()).add("fcm", sharedPreferences.getString("fcmtoken", "").trim().replace("'", "\\'")).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.update)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("EditProfileActivity", "onFailure: ", e);

                runOnUiThread(() -> {

                    TransitionManager.beginDelayedTransition(rellay);

                    progressbar.setVisibility(View.GONE);
                    savetxt.setVisibility(View.VISIBLE);
                    Snackbar.make(rellay, getResources().getString(R.string.connectionerrortryagain), Snackbar.LENGTH_LONG)
                            .show();
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(() -> {

                    if (data.contains("created successfully")) {

                        TransitionManager.beginDelayedTransition(rellay);

                        progressbar.setVisibility(View.GONE);
                        savetxt.setVisibility(View.VISIBLE);
                        Snackbar.make(rellay, getResources().getString(R.string.updatedsuccessfully), Snackbar.LENGTH_LONG)
                                .show();

                        sharedPreferences.edit().putString("displaynameuser", name.getText().toString()).apply();
                        sharedPreferences.edit().putString("descriptionuser", bio.getText().toString()).apply();
                        sharedPreferences.edit().putString("categoryuser", category.getText().toString()).apply();
                        sharedPreferences.edit().putString("websitesuser", website.getText().toString()).apply();
                        profiledetail();
                        sharedPreferences.edit().putString("profilepragdata", "").apply();
                        ProfileFragment.loaddata(context);

                        if (id.equals(sharedPreferences.getString("wallpouserid", ""))) {
                            ProfileActivity.loaddataprofile(context);
                        }

                        final SharedPreferences wallpouserdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);
                        wallpouserdata.edit().putString(id + "id", "").apply();

                    } else {
                        profiledetail();
                        sharedPreferences.edit().putString("profilepragdata", "").apply();
                        ProfileFragment.loaddata(context);
                        TransitionManager.beginDelayedTransition(rellay);

                        progressbar.setVisibility(View.GONE);
                        savetxt.setVisibility(View.VISIBLE);
                        Snackbar.make(rellay, getResources().getString(R.string.errorupdating), Snackbar.LENGTH_LONG)
                                .show();
                    }
                });


            }
        });
    }

    private void loaddataprofile() {

        String usernametxt = sharedPreferences.getString("usernameuser", "");
        String displayname = sharedPreferences.getString("displaynameuser", "");
        String profilephoto = sharedPreferences.getString("profilephotouser", "");

        String verifiedtxt = sharedPreferences.getString("verifieduser", "");
        String description = sharedPreferences.getString("descriptionuser", "");
        String categorytxt = sharedPreferences.getString("categoryuser", "");
        String websites = sharedPreferences.getString("websitesuser", "");
        String backphoto = sharedPreferences.getString("backphotouser", "");
        String phonenumber = sharedPreferences.getString("phonenumberuser", "");
        String emailverified = sharedPreferences.getString("emailverifieduser", "");

        if (verifiedtxt.equals("wallpoverified")) {
            verified.setVisibility(View.VISIBLE);
        } else {
            verified.setVisibility(View.GONE);
        }

        try {

            Glide.with(getApplicationContext()).load(backphoto)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).into(backimg);


            Glide.with(getApplicationContext()).load(profilephoto).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.profilepic)
                    .skipMemoryCache(false).into(profilepic);


        } catch (IllegalArgumentException e) {
            Log.e("searchadapter", "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e("act", "onBindViewHolder: ", e);
        }


        category.setText(categorytxt);

        bio.setText(description);

        name.setText(displayname);

        username.setText(usernametxt);

        website.setText(websites);

        tc = false;
        phonenumbers.setText(phonenumber);
        tc = true;

        if (emailverified.equals("false")) {
            veriflayout.setVisibility(View.VISIBLE);
        } else {
            veriflayout.setVisibility(View.GONE);
        }

    }



    private void updateno(String id, final String no) {

        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("userid", id).add("phonenumber", no)
                .add("fcm", sharedPreferences.getString("fcmtoken", "")).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.updatephonenumber)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(() -> {

                    tc = false;
                    tc = false;
                    if (data.trim().equals("successfully")) {
                        profiledetail();
                        Snackbar.make(rellay, getResources().getString(R.string.phonenoupdated), Snackbar.LENGTH_LONG)
                                .show();
                        sharedPreferences.edit().putString("phonenumberuser", no).apply();
                    } else {

                        Snackbar.make(rellay, getResources().getString(R.string.errorupdatingphoneno), Snackbar.LENGTH_LONG)
                                .show();

                    }

                });
            }
        });


    }


    private void profiledetail() {
        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder()
                .add("userid", id).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.userinfo)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("EditProfileActivity", "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(() -> {

                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            String emailids = object.getString("email").trim();
                            String usernametxt = object.getString("username").trim();
                            String displayname = object.getString("displayname").trim();
                            String profilephoto = object.getString("profilephoto").trim();

                            String verifiedtxt = object.getString("verified").trim();
                            String description = object.getString("description").trim();
                            String categorytxt = object.getString("category").trim();
                            String websites = object.getString("websites").trim();
                            String backphoto = object.getString("backphoto").trim();
                            String phonenumber = object.getString("phonenumber").trim();
                            String emailverified = object.getString("emailverified").trim();

                            sharedPreferences.edit().putString("emailid", emailids).apply();
                            sharedPreferences.edit().putString("usernameuser", usernametxt).apply();
                            sharedPreferences.edit().putString("displaynameuser", displayname).apply();
                            sharedPreferences.edit().putString("profilephotouser", profilephoto).apply();
                            sharedPreferences.edit().putString("verifieduser", verifiedtxt).apply();

                            sharedPreferences.edit().putString("descriptionuser", description).apply();
                            sharedPreferences.edit().putString("categoryuser", categorytxt).apply();
                            sharedPreferences.edit().putString("websitesuser", websites).apply();
                            sharedPreferences.edit().putString("backphotouser", backphoto).apply();
                            sharedPreferences.edit().putString("phonenumberuser", phonenumber).apply();
                            sharedPreferences.edit().putString("emailverifieduser", emailverified).apply();

                            emailid.setText(emailids);

                            if (verifiedtxt.equals("wallpoverified")) {
                                verified.setVisibility(View.VISIBLE);
                            } else {
                                verified.setVisibility(View.GONE);
                            }

                            try {

                                Glide.with(getApplicationContext()).load(backphoto)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .skipMemoryCache(false).into(backimg);


                                Glide.with(getApplicationContext()).load(profilephoto).centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.mipmap.profilepic)
                                        .skipMemoryCache(false).into(profilepic);


                            } catch (IllegalArgumentException e) {
                                Log.e("searchadapter", "onBindViewHolder: ", e);
                            } catch (IllegalStateException e) {

                                Log.e("act", "onBindViewHolder: ", e);
                            }


                            category.setText(categorytxt);

                            bio.setText(description);

                            name.setText(displayname);

                            username.setText(usernametxt);

                            website.setText(websites);

                            tc = false;
                            phonenumbers.setText(phonenumber);
                            tc = true;

                            if (emailverified.equals("false")) {
                                veriflayout.setVisibility(View.VISIBLE);
                            } else {
                                veriflayout.setVisibility(View.GONE);
                            }

                        }

                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }
                });


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {


            if (requestCode == 124) {

                Uri ImageUri = data.getData();

                CropImage.activity(ImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(2, 2)
                        .setCropMenuCropButtonTitle("UPLOAD")
                        .setBackgroundColor(Color.argb(130, 177, 194, 222))
                        .start(this);
            }


            if (requestCode == 154) {

                Uri ImageUri = data.getData();
                CropImage.activity(ImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1080, 1920)
                        .setCropMenuCropButtonTitle("UPLOAD")
                        .setBackgroundColor(Color.argb(130, 177, 194, 222))
                        .start(this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                if (imagechange.equals("changeprofilepic")) {
                    Uri resultUri = result.getUri();


                    File imagefile = new File(resultUri.getPath());

                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        profilepic.setImageBitmap(bitmap);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    uploadProfilephoto();
                } else if (imagechange.equals("changebackphoto")) {

                    Uri resultUri = result.getUri();


                    File imagefile = new File(resultUri.getPath());

                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        backimg.setImageBitmap(bitmap);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    backphoto();
                }

            }
        }

    }

    private void uploadProfilephoto() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("UPLOAD NOTIFICATION", "Edit In-App", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "UPLOAD NOTIFICATION");

        builder.setContentTitle("Uploading Profile picture.");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setProgress(100, 0, true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());

        StorageReference storageRef = storage.getReference();

        StorageReference profilepicture = storageRef.child("wallpo/" + id + "/" + id + "profilepic.jpeg");

        profilepic.setDrawingCacheEnabled(true);
        profilepic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profilepic.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 28, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = profilepicture.putBytes(data);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return profilepicture.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d("EditProfileActivity", "onComplete: dgfshgshs" + downloadUri + " - " + downloadUri.getPath());

                    OkHttpClient client = new OkHttpClient();

                    RequestBody postData = new FormBody.Builder()
                            .add("userid", id)
                            .add("photo", String.valueOf(downloadUri))
                            .add("fcm", sharedPreferences.getString("fcmtoken", "")).build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.uploadprofilephoto)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    builder.setContentTitle(getResources().getString(R.string.erroruploadingprofilepic))
                                            .setProgress(0, 0, false);
                                    notificationManager.notify(1, builder.build());
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (data.trim().equals("successfully")) {
                                        profiledetail();
                                        builder.setContentTitle(getResources().getString(R.string.profilepicuploaded))
                                                .setProgress(0, 0, false);
                                        notificationManager.notify(1, builder.build());

                                        sharedPreferences.edit().putString("profilepragdata", "").apply();
                                        ProfileFragment.loaddata(context);

                                        if (id.equals(sharedPreferences.getString("wallpouserid", ""))){
                                            ProfileActivity.loaddataprofile(context);
                                        }

                                    } else {
                                        sharedPreferences.edit().putString("profilepragdata", "").apply();
                                        ProfileFragment.loaddata(context);

                                        Toast.makeText(context, getResources().getString(R.string.erroruploadingprofilepic), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                } else {
                    // Handle failures
                    // ...
                    builder.setContentTitle(getResources().getString(R.string.erroruploadingprofilepic))
                            .setProgress(0, 0, false);
                    notificationManager.notify(1, builder.build());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, getResources().getString(R.string.erroruploadingprofilepic), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void backphoto() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("UPLOAD NOTIFICATION", "Edit In-App", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "UPLOAD NOTIFICATION");

        builder.setContentTitle("Uploading Back photo..");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setProgress(100, 0, true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());

        StorageReference storageRef = storage.getReference();

        StorageReference backpicture = storageRef.child("wallpo/" + id + "/" + id + "backpic.jpeg");

        backimg.setDrawingCacheEnabled(true);
        backimg.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) backimg.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = backpicture.putBytes(data);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return backpicture.getDownloadUrl();

            }
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                Log.d("EditProfileActivity", "onComplete: dgfshgshs" + downloadUri + " - " + downloadUri.getPath());

                OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder()
                        .add("userid", id)
                        .add("photo", String.valueOf(downloadUri))
                        .add("fcm", sharedPreferences.getString("fcmtoken", "")).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.uploadbackphoto)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                builder.setContentTitle("Error while uploading your back photo.")
                                        .setProgress(0, 0, false);
                                notificationManager.notify(1, builder.build());
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data1 = response.body().string().replaceAll(",\\[]", "");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (data1.trim().equals("successfully")) {
                                    profiledetail();
                                    builder.setContentTitle("Back photo uploaded successfully.")
                                            .setProgress(0, 0, false);

                                    sharedPreferences.edit().putString("profilepragdata", "").apply();
                                    ProfileFragment.loaddata(context);


                                    if (id.equals(sharedPreferences.getString("wallpouserid", ""))) {
                                        ProfileActivity.loaddataprofile(context);
                                    }

                                } else {
                                    sharedPreferences.edit().putString("profilepragdata", "").apply();
                                    ProfileFragment.loaddata(context);

                                    builder.setContentTitle("Error while uploading your back photo.")
                                            .setProgress(0, 0, false);
                                }
                                notificationManager.notify(1, builder.build());
                            }
                        });
                    }
                });

            } else {
                // Handle failures
                // ...
                builder.setContentTitle("Error while uploading your back photo.")
                        .setProgress(0, 0, false);
                notificationManager.notify(1, builder.build());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error while uploading", Toast.LENGTH_SHORT).show();
            }
        });


    }
    public static String usernameedit(String username) {
        username = username.replace(" ", "_");
        username = username.replace("#", "_");
        username = username.replace("<", "_");
        username = username.replace(">", "_");
        username = username.replace("?", "_");
        username = username.replace("{", "_");
        username = username.replace("}", "_");
        username = username.replace("(", "_");
        username = username.replace(")", "_");
        username = username.replace("%", "_");
        username = username.replace("!", "_");
        username = username.replace("~", "_");
        username = username.replace("=", "_");
        username = username.replace("+", "_");
        username = username.replace("*", "_");
        username = username.replace("&", "_");
        username = username.replace(",", "_");
        username = username.replace("-", "_");
        username = username.replace("_", "_");
        username = username.replace("|", "_");
        username = username.replace("/", "_");
        username = username.replace("^", "_");
        username = username.replace("$", "_");
        username = username.replace("\"", "_");
        username = username.replace("\\", "_");
        return username;
    }

}