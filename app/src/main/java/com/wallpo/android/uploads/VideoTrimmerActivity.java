package com.wallpo.android.uploads;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wallpo.android.LoginActivity;
import com.wallpo.android.R;
import com.wallpo.android.utils.Common;
import com.wallpo.android.videoGallery.FileUtil;
import com.wallpo.android.videoTrimmer.interfaces.OnCropVideoListener;
import com.wallpo.android.videoTrimmer.interfaces.OnTrimVideoListener;
import com.wallpo.android.videoTrimmer.videoCropper;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import static com.wallpo.android.MainActivity.initializeSSLContext;

public class VideoTrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnCropVideoListener {

    private static final String TAG = "VideoTrimmer";
    videoCropper mVideoTrimmer;
    Context context = this;
    Uri videoUri;
    RelativeLayout textcolor;
    public static String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trimmer);

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("wallpouserid", "");

        Dexter.withContext(context).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Dexter.withContext(context).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        Log.d("TAG", "onPermissionGranted: done");
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        Toast.makeText(context, getResources().getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(context, getResources().getString(R.string.permissiondeclined), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        Log.d(TAG, "onCreate: " + id);
        if (id.isEmpty()) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }

        Common.UPLOADINGSTATUS = "working";
        initializeSSLContext(context);

        Intent intent = getIntent();
        final Uri path = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        textcolor = findViewById(R.id.textcolor);
        textcolor.setVisibility(View.GONE);

        videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        mVideoTrimmer = (videoCropper) findViewById(R.id.timeLine);


        if (videoUri != null) {
            File file = null;
            try {
                file = FileUtil.from(context, videoUri);

                Log.d("file", "File...:::: uti - " + file.getPath() + " file -" + file + " : " + file.exists());

                Common.VIDEO_PATH = file.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        if (mVideoTrimmer != null) {

            mVideoTrimmer.setMaxDuration(120);

            mVideoTrimmer.setOnTrimVideoListener(VideoTrimmerActivity.this);
            long time = System.currentTimeMillis();
            try {
                mVideoTrimmer.setDestinationPath("/data/user/0/com.wallpo.android/cache/videonew");
                //mVideoTrimmer.setDestinationPath("/storage/emulated/0/Android/data/com.wallpo.android/.video/");

            } catch (IOError e) {
                Toast.makeText(context, context.getResources().getString(R.string.errorwhilecroppingvideo), Toast.LENGTH_SHORT).show();
            }

        }

        assert mVideoTrimmer != null;
        mVideoTrimmer.setVideoURI(Uri.parse(Common.VIDEO_PATH));

        Common.VIDEO_PATH = "";

    }

    @Override
    public void onVideoPrepared() {

    }

    @Override
    public void onTrimStarted() {
        runOnUiThread(() -> textcolor.setVisibility(View.VISIBLE));
    }

    @Override
    public void getResult(Uri uri) {
        runOnUiThread(() -> {

            Intent intent = new Intent(context, VideostatusActivity.class);
            Common.FINAL_VIDEO_RESULT = String.valueOf(uri);
            startActivity(intent);

            finish();

        });
    }

    @Override
    public void cancelAction() {
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(String message) {

        runOnUiThread(() -> {
            finish();
            Toast.makeText(VideoTrimmerActivity.this, context.getResources().getString(R.string.errorwhilecroppingvideo), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (status.equals("back")) {
            finish();
            status = "";
        }
    }
}