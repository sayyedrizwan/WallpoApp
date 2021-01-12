package com.wallpo.android.uploads;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wallpo.android.LoginActivity;
import com.wallpo.android.R;
import com.wallpo.android.fragment.ProfileFragment;
import com.wallpo.android.getset.Category;
import com.wallpo.android.maps.UploadMapActivity;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;
import com.wallpo.android.videoTrimmer.compressor.VideoCompress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.wallpo.android.MainActivity.initializeSSLContext;

public class UploadVideoActivity extends AppCompatActivity {

    Context context = this;
    String userid;
    Uri imageUri;
    AppCompatTextView no, locationtxt;
    public static SharedPreferences sharepref;
    ImageView profilepic;
    VideoView videoview;
    AppCompatEditText captions, seo, link;
    public static RecyclerView categoryview, albumview;
    List<Category> categoryList = new ArrayList<>();
    public static List<Category> albumList = new ArrayList<>();
    public static List<Category> albumListadd = new ArrayList<>();
    Button uploadbtn;
    Uri resultUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    File imagefile;
    CardView back;
    AppCompatTextView lastcopy;
    Boolean mapViewed = false;
    private FusedLocationProviderClient fusedLocationClient;
    Boolean mapRedirect = false;
    Boolean runQuery = false;
    RelativeLayout taglocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        initializeSSLContext(context);

        Common.categorydata.clear();
        Common.albumdata.clear();

        Common.latlongdata = "";

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        sharepref = getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        userid = sharepref.getString("wallpouserid", "");

        if (userid.isEmpty()) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        no = findViewById(R.id.no);
        captions = findViewById(R.id.captions);
        profilepic = findViewById(R.id.profilepic);
        videoview = findViewById(R.id.videoview);
        categoryview = findViewById(R.id.categoryview);
        albumview = findViewById(R.id.albumview);
        uploadbtn = findViewById(R.id.uploadbtn);
        seo = findViewById(R.id.seo);
        link = findViewById(R.id.link);
        back = findViewById(R.id.back);
        taglocation = findViewById(R.id.taglocation);
        lastcopy = findViewById(R.id.lastcopy);
        locationtxt = findViewById(R.id.locationtxt);

        mapRedirect = false;

        taglocation.setOnClickListener(view -> {

            Dexter.withContext(UploadVideoActivity.this)
                    .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            Dexter.withContext(UploadVideoActivity.this)
                                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {

                                            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                            assert locationManager != null;
                                            boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                            if (GpsStatus) {

                                                Toast.makeText(context, getResources().getString(R.string.gettingyourloc), Toast.LENGTH_LONG).show();

                                                Handler handler = new Handler();
                                                Runnable runnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                                            return;
                                                        }
                                                        fusedLocationClient.getLastLocation().addOnSuccessListener(UploadVideoActivity.this, location -> {
                                                            if (location != null) {

                                                                runQuery = false;

                                                                mapViewed = true;

                                                                startActivity(new Intent(context, UploadMapActivity.class));

                                                            } else {
                                                                runQuery = true;
                                                            }
                                                        });

                                                        if (runQuery) {
                                                            handler.postDelayed(this, 2500);
                                                        }
                                                    }
                                                };
                                                handler.postDelayed(runnable, 500);


                                            } else {

                                                new MaterialAlertDialogBuilder(context)
                                                        .setTitle(getResources().getString(R.string.welcometowallpomap))
                                                        .setMessage(getResources().getString(R.string.mapnotice))
                                                        .setNeutralButton(getResources().getString(R.string.cancelbig), (dialogInterface, i) -> {
                                                            dialogInterface.dismiss();
                                                        })
                                                        .setPositiveButton(getResources().getString(R.string.enablebig), (dialogInterface, i) -> {
                                                            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                                                            startActivity(intent1);
                                                        })
                                                        .show();

                                            }
                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response) {
                                            Toast.makeText(context, getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                            token.continuePermissionRequest();
                                        }
                                    }).check();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(context, getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();


        });

        back.setOnClickListener(view -> {
            finish();
        });


        Uri uri = Uri.parse(String.valueOf(new File(Common.FINAL_VIDEO_RESULT)));
        videoview.setVideoURI(uri);
        videoview.start();

        MediaController mediaController = new MediaController(this);

        mediaController.setAnchorView(videoview);

        videoview.setMediaController(mediaController);

        new Handler().postDelayed(() -> {
            videoview.pause();
        }, 1000);


        lastcopy.setOnClickListener(view -> {

            String lastseo = sharepref.getString("lastseo", "");

            if (lastseo.isEmpty()) {
                Toast.makeText(context, getResources().getString(R.string.seoisempty), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, getResources().getString(R.string.seocopied), Toast.LENGTH_SHORT).show();
                seo.setText(lastseo);
            }

        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        categoryview.setLayoutManager(linearLayoutManager);
        uploadcategoryadapter uploadcategoryadapter = new uploadcategoryadapter(context, categoryList);
        categoryview.setAdapter(uploadcategoryadapter);

        captions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                no.setText(s.length() + " / 160");
            }
        });

        try {

            Glide.with(getApplicationContext()).load(sharepref.getString("profilephotouser", "")).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.profilepic)
                    .skipMemoryCache(false).into(profilepic);

        } catch (IllegalArgumentException e) {
            Log.e("searchadapter", "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e("act", "onBindViewHolder: ", e);
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.categoryview)
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(context, context.getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(() -> {
                    categoryList.clear();
                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            String categoryid = object.getString("categoryid");
                            String name = object.getString("name");
                            String imagelink = object.getString("imagelink");

                            Category categoryItem = new Category(categoryid, name, imagelink, "category");
                            categoryList.add(categoryItem);
                        }

                        uploadcategoryadapter.notifyDataSetChanged();

                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }

                });
            }
        });

        loadalbum(context);

        uploadbtn.setOnClickListener(view -> {
            if (Common.categorydata.size() < 1) {
                Toast.makeText(context, context.getResources().getString(R.string.selectonecategory), Toast.LENGTH_SHORT).show();
                return;
            }

            if (seo.getText().toString().length() < 2) {
                Toast.makeText(context, context.getResources().getString(R.string.addonetag), Toast.LENGTH_SHORT).show();
                return;
            }

            sharepref.edit().putString("lastseo", seo.getText().toString()).apply();

            uploadposts();

        });

    }

    private void uploadposts() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("UPLOAD NOTIFICATION", "Posts Upload",
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "UPLOAD NOTIFICATION");
        builder.setContentTitle(context.getResources().getString(R.string.uploadingpost));
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setProgress(100, 0, true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());

        updatecode.analyticsFirebase(context,
                "posts_video_start_upload", "posts_video_start_upload");


        finish();
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(Common.FINAL_VIDEO_RESULT));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int file_size = 0;
        try {
            file_size = Integer.parseInt(String.valueOf(file.available() / 1024));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file_size > 14000) {
            Random random = new Random();
            int randomNumbers = random.nextInt(4200 - 465) + 465;
            long time = System.currentTimeMillis();
            String name = "/data/user/0/com.wallpo.android/cache/compress" + time + randomNumbers + ".mp4";

            VideoCompress.compressVideoLow(Common.FINAL_VIDEO_RESULT, name, new VideoCompress.CompressListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    Common.FINAL_VIDEO_RESULT = name;
                    uploadposts();
                }

                @Override
                public void onFail() {

                }

                @Override
                public void onProgress(float percent) {

                }
            });

            return;
        }
        final String tz = TimeZone.getDefault().getID();

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        StorageReference storageRef = storage.getReference();

        long time = System.currentTimeMillis();
        double random1 = Math.random() * (999 - 10 + 1) + 10;
        double random2 = Math.random() * (9999 - 10 + 1) + 10;

        String uploadfile = "wallpo/" + userid + "/" + time + random1 + userid + random2;
        StorageReference upload = storageRef.child(uploadfile.replace(".", "") + ".mp4");

        try {
            InputStream stream = new FileInputStream(Common.FINAL_VIDEO_RESULT);

            UploadTask uploadTask = upload.putStream(stream);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return upload.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    String catdata = Common.categorydata.toString()
                            .replace("[", "").replace("]", "");

                    String albdata = Common.albumdata.toString()
                            .replace("[", "").replace("]", "");

                    OkHttpClient client = new OkHttpClient();

                    RequestBody postData = new FormBody.Builder().add("userid", userid)
                            .add("imagepath", String.valueOf(downloadUri)).add("seotags", seo.getText().toString().trim().replace("'", "\\'"))
                            .add("link", link.getText().toString().trim().replace("'", "\\'")).add("categoryid", catdata).add("albumid", albdata)
                            .add("caption", captions.getText().toString().trim().trim().replace("'", "\\'"))
                            .add("location", Common.latlongdata.trim().replace("'", "\\'")).add("timezone", tz)
                            .build();

                    Request request = new Request.Builder()
                            .url(URLS.uploadposts)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> {
                                StorageReference storageRef = storage.getReference();

                                StorageReference desertRef = storageRef.child(uploadfile);

                                desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                                ProfileActivity.loaddataprofile(context);
                                builder.setContentTitle(context.getResources().getString(R.string.erroruploadigpost))
                                        .setProgress(0, 0, false);
                                notificationManager.notify(1, builder.build());
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string().replaceAll(",\\[]", "");

                            runOnUiThread(() -> {

                                ProfileActivity.loaddataprofile(context);
                                sharedPreferences.edit().putString("profilepragdata", "").apply();
                                ProfileFragment.loaddata(context);

                                updatecode.analyticsFirebase(context,
                                        "posts_video_uploaded", "posts_video_uploaded");

                                Log.d("UploadVideoActivity", "onResponse: " + data);
                                if (data.trim().contains("successfully")) {
                                    builder.setContentTitle(context.getResources().getString(R.string.postsuploaded))
                                            .setProgress(0, 0, false);
                                    

                                    SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);
                                    sharedusersdata.edit().putString(userid + "postsprofile", "").apply();
                                    Common.ALBUM_STATUS = "changed";
                                    
                                    updatecode.sendNotificationToUsers(context, "posts", String.valueOf(downloadUri));


                                } else {

                                    StorageReference desertRef = storageRef.child(uploadfile);

                                    desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                                    builder.setContentTitle(context.getResources().getString(R.string.erroruploadigpost))
                                            .setProgress(0, 0, false);
                                }

                                notificationManager.notify(1, builder.build());
                            });

                        }
                    });


                } else {

                    ProfileActivity.loaddataprofile(context);

                    builder.setContentTitle(context.getResources().getString(R.string.erroruploadigpost))
                            .setProgress(0, 0, false);
                    notificationManager.notify(1, builder.build());
                }

            }).addOnFailureListener(e -> {

                builder.setContentTitle(context.getResources().getString(R.string.erroruploadigpost))
                        .setProgress(0, 0, false);
                notificationManager.notify(1, builder.build());
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getResources().getString(R.string.filenotfound), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public static void loadalbum(Context context) {

        sharepref = context.getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharepref.getString("wallpouserid", "");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        albumview.setLayoutManager(linearLayoutManager);
        uploadcategoryadapter uploadcategoryadapters = new uploadcategoryadapter(context, albumList);
        addalbumadapter addalbumadapter = new addalbumadapter(context, albumListadd);

        ConcatAdapter concatAdapter = new ConcatAdapter(addalbumadapter, uploadcategoryadapters);
        albumview.setAdapter(concatAdapter);

        albumListadd.clear();
        Category categoryItems = new Category("0", "albumname", "albumurl");
        albumListadd.add(categoryItems);

        addalbumadapter.notifyDataSetChanged();

        uploadcategoryadapters.notifyDataSetChanged();

        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("usersid", userid)
                .build();

        Request request = new Request.Builder()
                .url(URLS.spinneralbum)
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

                    albumList.clear();
                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            String albumid = object.getString("albumid");
                            String albumname = object.getString("albumname");
                            String albumurl = object.getString("albumurl");


                            Category categoryItems1 = new Category(albumid, albumname, albumurl, "album");
                            albumList.add(categoryItems1);

                            uploadcategoryadapters.notifyDataSetChanged();
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
    protected void onStart() {
        super.onStart();
        if (mapViewed) {
            if (!Common.latlongdata.isEmpty()) {
                String str = Common.latlongdata;
                if (str.contains("/")) {

                    String lats = str.substring(0, str.indexOf("/"));
                    String longs = str.substring(str.indexOf("/") + 1, str.length());

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(Double.parseDouble(lats), Double.parseDouble(longs), 1);

                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();

                        locationtxt.setText("");

                        if (knownName != null) {
                            locationtxt.setText(knownName);
                        }

                        if (city != null) {
                            locationtxt.setText(locationtxt.getText().toString() + ", " + city);
                        }

                        if (state != null) {
                            locationtxt.setText(locationtxt.getText().toString() + ", " + state);
                        }

                        if (country != null) {
                            locationtxt.setText(locationtxt.getText().toString() + ", " + country);
                        }

                        if (locationtxt.getText().toString().isEmpty()) {
                            locationtxt.setText("Unknown Location to show address, Location added to post.");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}