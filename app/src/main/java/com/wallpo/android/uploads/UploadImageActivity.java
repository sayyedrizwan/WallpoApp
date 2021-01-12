package com.wallpo.android.uploads;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.wallpo.android.cropper.CropImage;
import com.wallpo.android.cropper.CropImageView;
import com.wallpo.android.fragment.ProfileFragment;
import com.wallpo.android.getset.Category;
import com.wallpo.android.maps.UploadMapActivity;
import com.wallpo.android.profile.ProfileActivity;
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
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.wallpo.android.MainActivity.initializeSSLContext;

public class UploadImageActivity extends AppCompatActivity {

    Context context = this;
    String userid;
    Uri imageUri;
    AppCompatTextView no, locationtxt;
    public static SharedPreferences sharepref;
    ImageView profilepic, imagepath;
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
    RelativeLayout taglocation;
    Boolean mapRedirect = false;
    Boolean runQuery = false;
    Boolean mapViewed = false;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

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

        Intent intent = getIntent();
        imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (imageUri != null) {

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setCropMenuCropButtonTitle("NEXT")
                    .setBackgroundColor(Color.argb(130, 177, 194, 222))
                    .start(this);

        } else {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setCropMenuCropButtonTitle("NEXT")
                    .setBackgroundColor(Color.argb(130, 177, 194, 222))
                    .start(this);

        }

        no = findViewById(R.id.no);
        captions = findViewById(R.id.captions);
        profilepic = findViewById(R.id.profilepic);
        imagepath = findViewById(R.id.imagepath);
        categoryview = findViewById(R.id.categoryview);
        albumview = findViewById(R.id.albumview);
        uploadbtn = findViewById(R.id.uploadbtn);
        seo = findViewById(R.id.seo);
        link = findViewById(R.id.link);
        back = findViewById(R.id.back);
        lastcopy = findViewById(R.id.lastcopy);
        taglocation = findViewById(R.id.taglocation);
        locationtxt = findViewById(R.id.locationtxt);

        mapRedirect = false;

        taglocation.setOnClickListener(view -> {

            Dexter.withContext(UploadImageActivity.this)
                    .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            Dexter.withContext(UploadImageActivity.this)
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
                                                        fusedLocationClient.getLastLocation().addOnSuccessListener(UploadImageActivity.this, location -> {
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

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.categoryview)
                .get()
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
                Toast.makeText(context, getResources().getString(R.string.selectonecategory), Toast.LENGTH_SHORT).show();
                return;
            }

            if (seo.getText().toString().length() < 2) {

                Toast.makeText(context, getResources().getString(R.string.addonetag), Toast.LENGTH_SHORT).show();
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
        builder.setContentTitle(getResources().getString(R.string.uploadingpost));
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setProgress(100, 0, true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());

        updatecode.analyticsFirebase(context,
                "posts_image_start_upload", "posts_image_start_upload");

        finish();

        final String tz = TimeZone.getDefault().getID();

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        sharedPreferences.edit().putString("lastseo", seo.getText().toString().trim()).apply();

        StorageReference storageRef = storage.getReference();

        long time = System.currentTimeMillis();
        double random1 = Math.random() * (999 - 10 + 1) + 10;
        double random2 = Math.random() * (9999 - 10 + 1) + 10;

        String uploadfile = "wallpo/" + userid + "/" + time + random1 + userid + random2;
        StorageReference upload = storageRef.child(uploadfile.replace(".", "") + ".jpeg");


        int file_size = Integer.parseInt(String.valueOf(imagefile.length() / 1024));

        int quality = 50;


        if (file_size <= 400) {

            quality = 100;

        } else if (file_size > 400 && file_size < 600) {

            quality = 80;

        } else if (file_size > 600 && file_size < 800) {

            quality = 60;

        } else if (file_size > 800 && file_size < 1000) {

            quality = 50;

        } else if (file_size > 1000 && file_size < 1500) {

            quality = 40;

        } else if (file_size > 1500 && file_size < 2000) {
            quality = 35;

        } else if (file_size > 2000) {
            quality = 28;

        }

        imagepath.setDrawingCacheEnabled(true);
        imagepath.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imagepath.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = upload.putBytes(data);

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
                            .add("link", link.getText().toString().trim().replace("'", "\\'")).add("categoryid", catdata)
                            .add("albumid", albdata).add("caption", captions.getText().toString().trim().replace("'", "\\'"))
                            .add("timezone", tz).add("location", Common.latlongdata.trim().replace("'", "\\'")).build();

                    Request request = new Request.Builder()
                            .url(URLS.uploadposts)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> {

                                StorageReference desertRef = storageRef.child(uploadfile);

                                desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                                ProfileActivity.loaddataprofile(context);

                                builder.setContentTitle(getResources().getString(R.string.erroruploadigpost))
                                        .setProgress(0, 0, false);
                                notificationManager.notify(1, builder.build());
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data1 = response.body().string().replaceAll(",\\[]", "");

                            runOnUiThread(() -> {
                                ProfileActivity.loaddataprofile(context);

                                sharedPreferences.edit().putString("profilepragdata", "").apply();
                                ProfileFragment.loaddata(context);

                                updatecode.analyticsFirebase(context,
                                        "posts_image_upload", "posts_image_upload");

                                Log.d("UploadImageActivity", "run: " + data1);
                                if (data1.trim().contains("successfully")) {
                                    builder.setContentTitle(getResources().getString(R.string.postsuploaded))
                                            .setProgress(0, 0, false);


                                    SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);
                                    sharedusersdata.edit().putString(userid + "postsprofile", "").apply();
                                    Common.ALBUM_STATUS = "changed";

                                    updatecode.analyticsFirebase(context,
                                            "posts_image_uploaded", "posts_image_uploaded");


                                } else {

                                    StorageReference desertRef = storageRef.child(uploadfile);

                                    desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                                    ProfileActivity.loaddataprofile(context);
                                    builder.setContentTitle(getResources().getString(R.string.erroruploadigpost))
                                            .setProgress(0, 0, false);
                                }

                                notificationManager.notify(1, builder.build());
                            });

                        }
                    });

                } else {

                    StorageReference desertRef = storageRef.child(uploadfile);

                    desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                    ProfileActivity.loaddataprofile(context);

                    builder.setContentTitle(getResources().getString(R.string.erroruploadigpost))
                            .setProgress(0, 0, false);
                    notificationManager.notify(1, builder.build());
                }

            }).addOnFailureListener(e -> {

                builder.setContentTitle(getResources().getString(R.string.erroruploadigpost))
                        .setProgress(0, 0, false);
                notificationManager.notify(1, builder.build());
            });

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

        RequestBody postData = new FormBody.Builder().add("usersid", userid).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resultUri = result.getUri();

            imagefile = new File(resultUri.getPath());

            try {


                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);


                imagepath.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            finish();

        }
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