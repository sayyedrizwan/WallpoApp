package com.wallpo.android.extra;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.transition.TransitionManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.wallpo.android.R;
import com.wallpo.android.utils.updatecode;

public class WallpaperSettingActivity extends AppCompatActivity {

    RelativeLayout defualtset, fitset, cropset, optionsset;
    AppCompatRadioButton defualt, fit, crop, options;
    RelativeLayout defualtlay, fitlay, croplay, optionslay;
    String wallpapertype = "";
    RelativeLayout savebtn;
    AppCompatTextView savetxt, powersavingtxt;
    SpinKitView loadingbar;
    SwitchCompat switchsave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_setting);

        defualtset = findViewById(R.id.defualtset);
        fitset = findViewById(R.id.fitset);
        cropset = findViewById(R.id.cropset);
        optionsset = findViewById(R.id.optionsset);
        defualt = findViewById(R.id.defualt);
        fit = findViewById(R.id.fit);
        crop = findViewById(R.id.crop);
        options = findViewById(R.id.options);
        defualtlay = findViewById(R.id.defualtlay);
        fitlay = findViewById(R.id.fitlay);
        croplay = findViewById(R.id.croplay);
        optionslay = findViewById(R.id.optionslay);
        savebtn = findViewById(R.id.savebtn);
        savetxt = findViewById(R.id.savetxt);
        loadingbar = findViewById(R.id.loadingbar);
        loadingbar.setVisibility(View.GONE);
        powersavingtxt = findViewById(R.id.powersavingtxt);
        switchsave = findViewById(R.id.switchsave);

        crop.setClickable(false);
        defualt.setClickable(false);
        fit.setClickable(false);
        options.setClickable(false);


        SharedPreferences sharedPreferences = getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);

        String videosaver = sharedPreferences.getString("videosaver", "");

        if (videosaver.isEmpty()){
            powersavingtxt.setText(getResources().getString(R.string.videopowersavingon));
            switchsave.setChecked(true);
        }else {
            powersavingtxt.setText(getResources().getString(R.string.videopowersavingoff));
            switchsave.setChecked(false);
        }

        switchsave.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                sharedPreferences.edit().putString("videosaver", "").apply();
                powersavingtxt.setText(getResources().getString(R.string.videopowersavingon));
            }else {
                sharedPreferences.edit().putString("videosaver", "off").apply();
                powersavingtxt.setText(getResources().getString(R.string.videopowersavingoff));
            }
        });


        updatecode.analyticsFirebase(this, "user_share_link", "user_share_link");


        String wallpapersettertype = sharedPreferences.getString("wallpapaersettertype", "");

        defualtset.setOnClickListener(view -> {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            fitlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            croplay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            optionslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            defualt.setChecked(true);

            fit.setChecked(false);

            crop.setChecked(false);

            options.setChecked(false);

            wallpapertype = "";


        });

        fitset.setOnClickListener(view -> {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            fitlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            croplay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            optionslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            defualt.setChecked(false);

            fit.setChecked(true);

            crop.setChecked(false);

            options.setChecked(false);

            wallpapertype = "fit";

        });

        cropset.setOnClickListener(view -> {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            fitlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            croplay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            optionslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            defualt.setChecked(false);

            fit.setChecked(false);

            crop.setChecked(true);

            options.setChecked(false);

            wallpapertype = "crop";

        });

        optionsset.setOnClickListener(view -> {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            fitlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            croplay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            optionslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            defualt.setChecked(false);

            fit.setChecked(false);

            crop.setChecked(false);

            options.setChecked(true);

            wallpapertype = "options";

        });

        savebtn.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(savebtn);

            savetxt.setVisibility(View.GONE);

            loadingbar.setVisibility(View.VISIBLE);

            sharedPreferences.edit().putString("wallpapaersettertype", wallpapertype).apply();

            new Handler().postDelayed(() -> {

                TransitionManager.beginDelayedTransition(savebtn);

                loadingbar.setVisibility(View.GONE);

                savetxt.setVisibility(View.VISIBLE);

                Snackbar.make(savebtn, getResources().getString(R.string.wallpapsetterchangedsuccess), Snackbar.LENGTH_LONG)
                        .show();


            }, 1500);


        });


        assert wallpapersettertype != null;
        if (wallpapersettertype.equals("fit")) {
            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            fitlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            croplay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            optionslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            defualt.setChecked(false);

            fit.setChecked(true);

            crop.setChecked(false);

            options.setChecked(false);

            wallpapertype = "fit";
        } else if (wallpapersettertype.equals("crop")) {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            fitlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            croplay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            optionslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            defualt.setChecked(false);

            fit.setChecked(false);

            crop.setChecked(true);

            options.setChecked(false);

            wallpapertype = "crop";

        } else if (wallpapersettertype.equals("options")) {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            fitlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            croplay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            optionslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            defualt.setChecked(false);

            fit.setChecked(false);

            crop.setChecked(false);

            options.setChecked(true);

            wallpapertype = "options";
        } else {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            fitlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            croplay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            optionslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            defualt.setChecked(true);

            fit.setChecked(false);

            crop.setChecked(false);

            options.setChecked(false);

            wallpapertype = "";
        }

    }
}