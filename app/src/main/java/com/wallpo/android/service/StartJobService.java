package com.wallpo.android.service;

import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.wallpo.android.utils.VideoWallpaperMuteService;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class StartJobService extends JobService {

    private static final String TAG = "StartJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        doBackgroundWork(jobParameters);
        return true;
    }
    private void doBackgroundWork(final JobParameters params) {
        new Thread(() -> {
            Toast.makeText(this, "cccccc", Toast.LENGTH_SHORT).show();
            Intent intents = new Intent(
                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intents.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this,
                    VideoWallpaperMuteService.class));
            intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intents);
            
            jobFinished(params, false);
        }).start();
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
