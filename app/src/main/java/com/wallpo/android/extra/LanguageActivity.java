package com.wallpo.android.extra;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.transition.TransitionManager;

import com.google.android.material.snackbar.Snackbar;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    LinearLayout firstlay, frenchlayout;
    String lang = "";
    AppCompatRadioButton english, french;
    Context context = this;
    RelativeLayout mainlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        mainlay = findViewById(R.id.mainlay);
        firstlay = findViewById(R.id.firstlay);
        frenchlayout = findViewById(R.id.frenchlayout);


        english = findViewById(R.id.english);
        french = findViewById(R.id.french);


        firstlay.setOnClickListener(view -> {

            TransitionManager.beginDelayedTransition(mainlay);

            english.setChecked(true);
            french.setChecked(false);
            lang = "en";
            firstlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));
            frenchlayout.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            setAppLocale("en");

        });

        frenchlayout.setOnClickListener(view -> {

            TransitionManager.beginDelayedTransition(mainlay);

            english.setChecked(false);
            french.setChecked(true);
            lang = "fr";
            firstlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));
            frenchlayout.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            setAppLocale("fr");

        });


    }

    private void setAppLocale(String localeCode) {
    /*    Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);*/

    Locale locale = new Locale(localeCode);
    Locale.setDefault(locale);
    Configuration configuration = new Configuration();
    configuration.locale = locale;
    getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());



        Snackbar.make(mainlay, getResources().getString(R.string.languagechnage), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.restart), view -> {

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                });

    }
}