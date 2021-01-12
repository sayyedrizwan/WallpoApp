package com.wallpo.android.activity;

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
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bachors.wordtospan.WordToSpan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wallpo.android.R;
import com.wallpo.android.cropper.CropImage;
import com.wallpo.android.cropper.CropImageView;
import com.wallpo.android.explorefragment.risingartistsadapter;
import com.wallpo.android.getset.Photos;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.wallpo.android.utils.updatecode.getusernameid;

public class AlbumsActivity extends AppCompatActivity {

    AppBarLayout appbar;
    Toolbar toolbar;
    Context context = this;
    TextView abouttext;
    SharedPreferences sharedPreferences;
    ImageView albumimg, albumpic;
    SpinKitView trendingloading;
    String albumid;
    RecyclerView recyclerview;
    private List<Photos> photolist = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    com.wallpo.android.explorefragment.risingartistsadapter risingartistsadapter;
    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = 0;
    private int previousTotal;
    private boolean load = true;
    FloatingActionButton edit;
    Bitmap bitmap;
    SpinKitView loadingbar;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    CollapsingToolbarLayout toolBarLayout;
    FloatingActionButton delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_albums);

        appbar = findViewById(R.id.app_bar);
        abouttext = findViewById(R.id.abouttext);
        albumimg = findViewById(R.id.albumimg);
        trendingloading = findViewById(R.id.trendingloading);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerview = findViewById(R.id.recyclerview);
        edit = findViewById(R.id.edit);
        edit.setVisibility(View.GONE);

        updatecode.analyticsFirebase(context, "album_activity", "albumactivity");

        sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        albumid = sharedPreferences.getString("albumid", "");
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(sharedPreferences.getString("albumname", ""));
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.TextAppearance_MyApp_Title_Collapsed);
        toolBarLayout.setExpandedTitleTextAppearance(R.style.TextAppearance_MyApp_Title_Expanded);

        loadablumsdata();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        appbar.getLayoutParams().height = height - 200;
        appbar.requestLayout();

        loadcaption();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.share);
        fab.setOnClickListener(view -> {
            Common.SHARETYPE = "album";
            Common.MESSAGE_DATA = String.valueOf(albumid);
            updatecode.shareintent(context, "album/" + albumid);
        });

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        recyclerview.setLayoutManager(linearLayoutManager);
        risingartistsadapter = new risingartistsadapter(context, photolist);
        recyclerview.setAdapter(risingartistsadapter);

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemCont = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (dx > 0) {
                    if (load) {
                        if (totalItemCount > previousTotal) {
                            previousTotal = totalItemCount;
                            load = false;
                            page++;
                        }
                    }

                    if (!load && (firstVisibleItem + visibleItemCont) >= totalItemCount - 2) {
                        loadno = loadno + 25;
                        loaddata(String.valueOf(loadno));
                        load = true;
                    }
                }
            }

        });

        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String albumdata = sharedusersdata.getString(albumid + "albummdata", "");

        if (!albumdata.isEmpty()) {
            try {
                JSONArray array = new JSONArray(albumdata);

                for (int i = 0; i < array.length(); i++) {

                    JSONObject Object = array.getJSONObject(i);

                    int photoid = Object.getInt("photoid");
                    String caption = Object.getString("caption");
                    String categoryid = Object.getString("categoryid");
                    String albumid = Object.getString("albumid");
                    String datecreated = Object.getString("datecreated");
                    String dateshowed = Object.getString("dateshowed");
                    String links = Object.getString("link");
                    String userid = Object.getString("userid");
                    String imagepath = Object.getString("imagepath");
                    String likes = Object.getString("likes");
                    int trendingcount = Object.getInt("trendingcount");

                    Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, links, categoryid,
                            dateshowed, trendingcount, likes);
                    photolist.add(photo);

                }

                trendingloading.setVisibility(View.GONE);
                risingartistsadapter.notifyDataSetChanged();


            } catch (
                    JSONException e) {
                e.printStackTrace();
            }
        } else {
            loaddata("");
        }
    }

    private void loadablumsdata() {
        if (sharedPreferences.getString("albumcreated", "").isEmpty()) {

            OkHttpClient client = new OkHttpClient();
            RequestBody postData = new FormBody.Builder().add("albumid", String.valueOf(albumid)).build();

            Request request = new Request.Builder()
                    .url(URLS.albuminfo)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(context, "Connection Error.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject Object = array.getJSONObject(i);

                                    String albumid = Object.getString("albumid");
                                    String userid = Object.getString("userid");
                                    String albumname = Object.getString("albumname");
                                    String albumdesc = Object.getString("albumdesc");
                                    String albumcreated = Object.getString("albumcreated");
                                    String albumurl = Object.getString("albumurl");

                                    sharedPreferences.edit().putString("albumid", albumid).apply();
                                    sharedPreferences.edit().putString("albumname", albumname).apply();
                                    sharedPreferences.edit().putString("albumdesc", albumdesc).apply();
                                    sharedPreferences.edit().putString("albumurl", albumurl).apply();
                                    sharedPreferences.edit().putString("albumuserid", userid).apply();
                                    sharedPreferences.edit().putString("albumcreated", albumcreated).apply();

                                    loadcaption();
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

    private void loadcaption() {

        String userids = sharedPreferences.getString("wallpouserid", "");

        String albumuserid = sharedPreferences.getString("albumuserid", "");

        if (userids.equals(albumuserid)) {
            edit.setVisibility(View.VISIBLE);

            edit.setOnClickListener(v -> {

                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.editalbum);

                Button update = dialog.findViewById(R.id.update);
                TextInputEditText albumabout = dialog.findViewById(R.id.albumabout);
                TextInputEditText albumname = dialog.findViewById(R.id.albumname);
                FloatingActionButton delete = dialog.findViewById(R.id.delete);

                albumpic = dialog.findViewById(R.id.albumpic);

                try {
                    Glide.with(context).load(sharedPreferences.getString("albumurl", ""))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false).into(albumpic);

                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "onBindViewHolder: ", e);
                } catch (IllegalStateException e) {

                    Log.e(TAG, "onBindViewHolder: ", e);
                }

                albumname.setText(sharedPreferences.getString("albumname", ""));
                albumabout.setText(sharedPreferences.getString("albumdesc", ""));

                TextView changealbumpic = dialog.findViewById(R.id.changealbumpic);
                loadingbar = dialog.findViewById(R.id.loadingbar);
                loadingbar.setVisibility(View.GONE);

                changealbumpic.setOnClickListener(v1 -> {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 479);
                });


                delete.setOnClickListener(view -> new MaterialAlertDialogBuilder(context)
                        .setTitle(getResources().getString(R.string.suredeletealbum))
                        .setMessage(getResources().getString(R.string.deletealbummsg))
                        .setNegativeButton(getResources().getString(R.string.cancelbig), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setPositiveButton(getResources().getString(R.string.deletebig), (dialogInterface, i) -> {

                            dialogInterface.dismiss();
                            loadingbar.setVisibility(View.VISIBLE);
                            OkHttpClient client = new OkHttpClient();

                            RequestBody postData = new FormBody.Builder().add("albumid", String.valueOf(albumid))
                                    .build();

                            Request request = new Request.Builder()
                                    .url(URLS.deletealbum)
                                    .post(postData)
                                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingbar.setVisibility(View.GONE);
                                            Toast.makeText(context, "Connection Error.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String data = response.body().string().replaceAll(",\\[]", "");

                                    runOnUiThread(() -> {

                                        Common.ALBUM_STATUS = "changed";

                                        if (data.trim().contains("deleted")) {

                                            loadingbar.setVisibility(View.GONE);
                                            Toast.makeText(context, "Album deleted successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Error while deleting the album.", Toast.LENGTH_SHORT).show();
                                        }

                                        finish();

                                    });

                                }
                            });
                        })
                        .show());


                update.setOnClickListener(v1 -> {

                    loadingbar.setVisibility(View.VISIBLE);

                    OkHttpClient client = new OkHttpClient();

                    RequestBody postData = new FormBody.Builder().add("albumid", String.valueOf(albumid))
                            .add("albumname", String.valueOf(albumname.getText().toString().trim().replace("'", "\\'")))
                            .add("albumdesc", String.valueOf(albumabout.getText().toString().replace("'", "\\'")))
                            .build();

                    Request request = new Request.Builder()
                            .url(URLS.albumupdate)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingbar.setVisibility(View.GONE);
                                    Toast.makeText(context, "Connection Error..", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");

                            runOnUiThread(() -> {
                                if (data.trim().contains("successfully")) {


                                    sharedPreferences.edit().putString("albumcreated", "").apply();

                                    loadablumsdata();


                                    Common.ALBUM_STATUS = "changed";

                                    loadingbar.setVisibility(View.GONE);
                                    Toast.makeText(context, "Updated successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    loadingbar.setVisibility(View.GONE);
                                    Toast.makeText(context, "Error while updating value.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                });

                dialog.show();

            });

        } else {
            edit.setVisibility(View.GONE);
        }

        toolBarLayout.setTitle(sharedPreferences.getString("albumname", ""));

        try {

            Glide.with(context).load(sharedPreferences.getString("albumurl", ""))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).into(albumimg);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        WordToSpan link = new WordToSpan();
        link.setColorTAG(context.getResources().getColor(R.color.texthashtag))
                .setColorURL(context.getResources().getColor(R.color.texthashtag))
                .setColorPHONE(context.getResources().getColor(R.color.texthashtag))
                .setColorMAIL(context.getResources().getColor(R.color.texthashtag))
                .setColorMENTION(context.getResources().getColor(R.color.texthashtag))
                .setColorCUSTOM(context.getResources().getColor(R.color.texthashtag))
                .setUnderlineURL(false)
                .setLink(sharedPreferences.getString("albumdesc", ""))
                .into(abouttext)
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

                            context.startActivity(Intent.createChooser(emailIntent, "Send Mail.."));

                        } else if (type.trim().equals("phone")) {
                            String phono = text;
                            if (phono.length() > 10) {
                                phono = "+" + text;
                            }
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phono));
                            startActivity(intent);

                        } else if (type.trim().equals("url")) {

                            if (text.trim().contains("https://") || text.trim().contains("http://")) {
                                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(text.trim()));
                                startActivity(viewIntent);
                            } else {
                                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://" + text.trim()));
                                startActivity(viewIntent);
                            }
                        } else if (type.trim().equals("custom")) {
                            Log.d(TAG, "onClick: custom");
                        } else {
                            Log.d(TAG, "onClick: else");
                        }

                    }
                });

    }

    private void loaddata(String limit) {
        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("albumid", String.valueOf(albumid))
                .add("limit", String.valueOf(limit)).build();

        Request request = new Request.Builder()
                .url(URLS.albumphotos)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        trendingloading.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (limit.isEmpty()) {

                            SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

                            sharedusersdata.edit().putString(albumid + "albummdata", data).apply();
                        }

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject Object = array.getJSONObject(i);

                                int photoid = Object.getInt("photoid");
                                String caption = Object.getString("caption");
                                String categoryid = Object.getString("categoryid");
                                String albumid = Object.getString("albumid");
                                String datecreated = Object.getString("datecreated");
                                String dateshowed = Object.getString("dateshowed");
                                String link = Object.getString("link");
                                String userid = Object.getString("userid");
                                String imagepath = Object.getString("imagepath");
                                String likes = Object.getString("likes");
                                int trendingcount = Object.getInt("trendingcount");

                                Photos photo = new Photos(caption, datecreated, imagepath, photoid, userid, albumid, link, categoryid,
                                        dateshowed, trendingcount, likes);
                                photolist.add(photo);

                            }

                            trendingloading.setVisibility(View.GONE);
                            risingartistsadapter.notifyDataSetChanged();


                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == 479) {
                Uri ImageUri = data.getData();

                CropImage.activity(ImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1080, 1820)
                        .setCropMenuCropButtonTitle("UPLOAD")
                        .setBackgroundColor(Color.argb(130, 177, 194, 222))
                        .start(this);

            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                File imagefile = new File(resultUri.getPath());

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    albumpic.setImageBitmap(bitmap);

                    changealbumcover(imagefile);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    private void changealbumcover(File albumcover) {

        final String tz = TimeZone.getDefault().getID();

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        StorageReference storageRef = storage.getReference();

        long time = System.currentTimeMillis();
        double random1 = Math.random() * (999 - 10 + 1) + 10;
        double random2 = Math.random() * (9999 - 10 + 1) + 10;

        String uploadfile = "wallpo/" + userid + "/" + time + random1 + userid + random2;
        StorageReference albumupload = storageRef.child(uploadfile + "albums.jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("UPLOAD NOTIFICATION", "Albums", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "UPLOAD NOTIFICATION");
        builder.setContentTitle("Changing your album cover.");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setProgress(100, 0, true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());

        loadingbar.setVisibility(View.VISIBLE);


        albumpic.setDrawingCacheEnabled(true);
        albumpic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) albumpic.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = albumupload.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return albumupload.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    OkHttpClient client = new OkHttpClient();

                    RequestBody postData = new FormBody.Builder().add("urlphoto", String.valueOf(downloadUri))
                            .add("albumid", String.valueOf(albumid)).build();

                    Request request = new Request.Builder()
                            .url(URLS.albumurl)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    builder.setContentTitle(context.getResources().getString(R.string.erroruploadingcover))
                                            .setProgress(0, 0, false);
                                    notificationManager.notify(1, builder.build());

                                    loadingbar.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");

                            runOnUiThread(() -> {
                                Log.d(TAG, "run: albumupdate " + data.trim());
                                if (data.trim().contains("successfully")) {

                                    albumimg.setImageBitmap(bitmap);

                                    Common.ALBUM_STATUS = "changed";

                                    sharedPreferences.edit().putString("albumcreated", "").apply();

                                    loadablumsdata();

                                    builder.setContentTitle(context.getResources().getString(R.string.albumchangessuccesfully))
                                            .setProgress(0, 0, false);


                                    loadingbar.setVisibility(View.GONE);
                                } else {

                                    loadingbar.setVisibility(View.GONE);
                                    builder.setContentTitle(context.getResources().getString(R.string.erroruploadingcover))
                                            .setProgress(0, 0, false);
                                }
                                notificationManager.notify(1, builder.build());
                            });
                        }
                    });

                } else {

                    loadingbar.setVisibility(View.GONE);

                    builder.setContentTitle(context.getResources().getString(R.string.erroruploadingcover))
                            .setProgress(0, 0, false);
                    notificationManager.notify(1, builder.build());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                loadingbar.setVisibility(View.GONE);

                builder.setContentTitle(context.getResources().getString(R.string.erroruploadingcover))
                        .setProgress(0, 0, false);
                notificationManager.notify(1, builder.build());

            }
        });


    }
}