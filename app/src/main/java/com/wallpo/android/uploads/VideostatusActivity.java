package com.wallpo.android.uploads;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wallpo.android.R;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;
import com.wallpo.android.videoTrimmer.compressor.VideoCompress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideostatusActivity extends AppCompatActivity {

    VideoView videoView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Button sharebutton;
    Context context = this;
    TextInputEditText textInputEditText;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    TextView textcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videostatus);

        videoView = findViewById(R.id.videoview);
        sharebutton = findViewById(R.id.sharebutton);
        textInputEditText = findViewById(R.id.textInputEditText);
        textcount = findViewById(R.id.textcount);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        Uri uri = Uri.parse(Common.FINAL_VIDEO_RESULT);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoView.pause();
            }
        }, 2000);

        FileInputStream files = null;
        try {
            files = new FileInputStream(Common.FINAL_VIDEO_RESULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int file_size = 0;
        try {
            file_size = Integer.parseInt(String.valueOf(files.available() / 1024));
        } catch (IOException e) {
            e.printStackTrace();
        }

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                textcount.setText(s.length() + " / 140");
            }
        });


        sharebutton.setOnClickListener(v -> {
            FileInputStream file = null;
            try {
                file = new FileInputStream(new File(Common.FINAL_VIDEO_RESULT));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            int file_size1 = 0;
            try {
                file_size1 = Integer.parseInt(String.valueOf(file.available() / 1024));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (file_size1 > 13000) {

                Random random = new Random();
                int randomNumbers = random.nextInt(4200 - 465) + 465;
                long time = System.currentTimeMillis();
                String name = "/data/user/0/com.wallpo.android/cache/compress" + time + randomNumbers + ".mp4";

                VideoCompress.compressVideoLow(Common.FINAL_VIDEO_RESULT, name, new VideoCompress.CompressListener() {
                    @Override
                    public void onStart() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("UPLOAD NOTIFICATION", "Status Upload",
                                    NotificationManager.IMPORTANCE_HIGH);

                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(channel);
                        }
                        builder = new NotificationCompat.Builder(context, "UPLOAD NOTIFICATION");
                        builder.setContentTitle(context.getResources().getString(R.string.uploadingvideoonfirewall));
                        builder.setSmallIcon(R.mipmap.logo);
                        builder.setAutoCancel(true);
                        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                        builder.setProgress(100, 0, true);
                        notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(1, builder.build());
                        VideoTrimmerActivity.status = "back";

                        updatecode.analyticsFirebase(context,
                                "firewall_video_start_compress_upload", "firewall_video_start_compress_upload");

                        finish();

                    }

                    @Override
                    public void onSuccess() {
                        Common.FINAL_VIDEO_RESULT = name;
                        uploadvideo();
                    }

                    @Override
                    public void onFail() {
                        builder.setContentTitle(context.getResources().getString(R.string.erroruploadingvideofirewall))
                                .setProgress(0, 0, false);
                        notificationManager.notify(1, builder.build());
                    }

                    @Override
                    public void onProgress(float percent) {

                    }
                });

            } else {

                uploadvideo();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("UPLOAD NOTIFICATION", "Status Upload",
                            NotificationManager.IMPORTANCE_HIGH);

                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }
                builder = new NotificationCompat.Builder(context, "UPLOAD NOTIFICATION");
                builder.setContentTitle(context.getResources().getString(R.string.uploadingvideoonfirewall));
                builder.setSmallIcon(R.mipmap.logo);
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setProgress(100, 0, true);
                notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(1, builder.build());
                VideoTrimmerActivity.status = "back";

                updatecode.analyticsFirebase(context,
                        "firewall_video_start_upload", "firewall_video_start_upload");

                finish();

            }


        });

    }

    private void uploadvideo() {

        final String tz = TimeZone.getDefault().getID();

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        StorageReference storageRef = storage.getReference();

        long time = System.currentTimeMillis();
        double random1 = Math.random() * (999 - 10 + 1) + 10;
        double random2 = Math.random() * (9999 - 10 + 1) + 10;

        String uploadfile = "wallpo/" + userid + "/" + time + random1 + userid + random2;
        StorageReference videoupload = storageRef.child(uploadfile.replace(".", "") + ".mp4");

        try {
            InputStream stream = new FileInputStream(new File(Common.FINAL_VIDEO_RESULT));
            UploadTask uploadTask = videoupload.putStream(stream);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return videoupload.getDownloadUrl();

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        OkHttpClient client = new OkHttpClient();

                        RequestBody postData = new FormBody.Builder().add("userid", userid)
                                .add("link", String.valueOf(downloadUri)).add("caption", textInputEditText.getText().toString().trim().replace("'", "\\'"))
                                .add("timezone", tz).add("type", "video").build();

                        Request request = new Request.Builder()
                                .url(URLS.uploadstoriesimage)
                                .post(postData)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(() -> {

                                    StorageReference desertRef = storageRef.child(uploadfile);

                                    desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                                    builder.setContentTitle(context.getResources().getString(R.string.erroruploadigvideopost))
                                            .setProgress(0, 0, false);
                                    notificationManager.notify(1, builder.build());
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String data = response.body().string().replaceAll(",\\[]", "");

                                runOnUiThread(() -> {
                                    Log.d("TAG", "onResponse: Uploaded on " + data);
                                    updatecode.analyticsFirebase(context,
                                            "firewall_video_upload", "firewall_video_upload");

                                    if (data.trim().contains("successfull")) {
                                        builder.setContentTitle(context.getResources().getString(R.string.uploadedonfirewall))
                                                .setProgress(0, 0, false);


                                        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);
                                        sharedusersdata.edit().putString(userid + "firewallprofile", "").apply();
                                        Common.ALBUM_STATUS = "changed";

                                    } else {


                                        StorageReference desertRef = storageRef.child(uploadfile);

                                        desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                                        builder.setContentTitle(context.getResources().getString(R.string.erroruploadigvideopost))
                                                .setProgress(0, 0, false);
                                    }

                                    notificationManager.notify(1, builder.build());
                                });

                            }
                        });


                    } else {

                        StorageReference desertRef = storageRef.child(uploadfile);

                        desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                        builder.setContentTitle(context.getResources().getString(R.string.erroruploadigvideopost))
                                .setProgress(0, 0, false);
                        notificationManager.notify(1, builder.build());
                    }

                }
            }).addOnFailureListener(e -> {

                builder.setContentTitle(context.getResources().getString(R.string.erroruploadigvideopost))
                        .setProgress(0, 0, false);
                notificationManager.notify(1, builder.build());
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}