package com.wallpo.android.activity;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.wallpo.android.R;
import com.wallpo.android.cropper.CropImage;
import com.wallpo.android.cropper.CropImageView;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.updatecode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class WallpaperSetActivity extends AppCompatActivity {

    Context context = this;
    String finishs = "back";
    RelativeLayout mainlay;
    ImageView imgview;
    Bitmap bitmapimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_set);

        mainlay = findViewById(R.id.mainlay);
        imgview = findViewById(R.id.imgview);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        final int mainheight = height * 2 / 3 + 100;
        final int mainwidth = width * 2 / 3 + 100;

        finishs = "";

        CropImage.activity(getImageUri(context, Common.BITMAP))
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                //   .setAspectRatio(width, height)
                .setBackgroundColor(Color.argb(130, 177, 194, 222))
                .start(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();

            File imagefile = new File(resultUri.getPath());

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                if (Common.wallpapertype.equals("wallpaper")) {

                    WallpaperManager manager = WallpaperManager.getInstance(context.getApplicationContext());

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                        } else {

                            manager.setBitmap(bitmap);
                        }

                        Snackbar.make(mainlay, getResources().getString(R.string.homscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();

                        updatecode.updatewallpaper(context, Common.ID_SELECTED, "wallpaper");


                    } catch (NullPointerException e) {
                        e.getMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {

                        WallpaperManager manager = WallpaperManager.getInstance(context.getApplicationContext());

                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);

                                Snackbar.make(mainlay, getResources().getString(R.string.lockscreenchangedsuccessfully), Snackbar.LENGTH_LONG).show();

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        updatecode.updatewallpaper(context, Common.ID_SELECTED, "lockscreen");


                    } catch (NullPointerException e) {
                        e.getMessage();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        finish();

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}