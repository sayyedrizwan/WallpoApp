package com.wallpo.android.utils;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wallpo.android.R;

public class WallpaperWebActivity extends AppCompatActivity {

    Button notnow, givepermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_web);

        notnow = findViewById(R.id.notnow);
        givepermission = findViewById(R.id.givepermission);

    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);
        String videosaver = sharedPreferences.getString("videosaver", "");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                        WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                if (videosaver.isEmpty()) {
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this,
                            VideoWallpaperMuteService.class));

                } else {
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this,
                            VideoWallpaperService.class));
                }
                startActivity(intent);

                return;
            }
        }
        notnow.setOnClickListener(view -> {
            Intent intent = new Intent(
                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            if (videosaver.isEmpty()) {
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this,
                        VideoWallpaperMuteService.class));

            } else {
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this,
                        VideoWallpaperService.class));
            }
            startActivity(intent);
            finish();
        });

        givepermission.setOnClickListener(view -> {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            }
            startActivityForResult(intent, 0);
        });

    }
}