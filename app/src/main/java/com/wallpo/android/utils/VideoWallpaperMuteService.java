package com.wallpo.android.utils;

import android.media.MediaPlayer;
import android.net.Uri;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class VideoWallpaperMuteService extends WallpaperService {
    protected static int playheadTime = 0;

    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    class VideoEngine extends Engine {
        private final String TAG = getClass().getSimpleName();
        private final MediaPlayer mediaPlayer;

        public VideoEngine() {
            super();
            Log.i(TAG, "( VideoEngine )");
            mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(Common.VIDEO_DLD_PATH));
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0, 0);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "onSurfaceCreated");
            mediaPlayer.setSurface(holder.getSurface());
            mediaPlayer.start();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "( INativeWallpaperEngine ): onSurfaceDestroyed");
            playheadTime = mediaPlayer.getCurrentPosition();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }

}
