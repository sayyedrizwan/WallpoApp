package com.wallpo.android.uploads;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.wallpo.android.LoginActivity;
import com.wallpo.android.R;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import java.io.IOException;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StatusActivity extends AppCompatActivity {

    TextInputEditText textInputEditText;
    TextView textcount;
    Button sharebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        textInputEditText = findViewById(R.id.textInputEditText);
        textcount = findViewById(R.id.textcount);
        sharebutton = findViewById(R.id.sharebutton);

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        if (userid.isEmpty()) {
            Intent intent = new Intent(StatusActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }

        Intent intent = getIntent();

        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
            textInputEditText.setText(sharedText);
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

                textcount.setText(s.length() + "/140");
            }
        });

        sharebutton.setOnClickListener(v -> {
            final String tz = TimeZone.getDefault().getID();

            if (textInputEditText.getText().toString().trim().length() < 1) {
                Toast.makeText(StatusActivity.this, getResources().getString(R.string.statuscannotbeempty), Toast.LENGTH_SHORT).show();
                return;
            }
            if (textInputEditText.getText().toString().trim().length() < 4) {
                Toast.makeText(StatusActivity.this, getResources().getString(R.string.statuscannotbeshort), Toast.LENGTH_SHORT).show();
                return;
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("UPLOAD NOTIFICATION", "Status Upload", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(StatusActivity.this, "UPLOAD NOTIFICATION");
            builder.setContentTitle(getResources().getString(R.string.uploadingstatusonfirewall));
            builder.setSmallIcon(R.mipmap.logo);
            builder.setAutoCancel(true);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setProgress(100, 0, true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(StatusActivity.this);
            notificationManager.notify(1, builder.build());


            updatecode.analyticsFirebase(StatusActivity.this,
                    "firewall_status_start_upload", "firewall_status_start_upload");

            finish();

            OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder()
                    .add("userid", userid)
                    .add("caption", textInputEditText.getText().toString().trim().replace("'", "\\'"))
                    .add("timezone", tz).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.uploadstoriespost)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        builder.setContentTitle(getResources().getString(R.string.erroruploadingstatus))
                                .setProgress(0, 0, false);
                        notificationManager.notify(1, builder.build());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(() -> {

                        updatecode.analyticsFirebase(StatusActivity.this,
                                "firewall_status_uploaded", "firewall_status_uploaded");

                        if (data.trim().contains("successfully")) {
                            builder.setContentTitle(getResources().getString(R.string.statusuploadedonfirewall))
                                    .setProgress(0, 0, false);


                            SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);
                            sharedusersdata.edit().putString(userid + "firewallprofile", "").apply();
                            Common.ALBUM_STATUS = "changed";


                        } else {
                            builder.setContentTitle(getResources().getString(R.string.erroruploadingstatus))
                                    .setProgress(0, 0, false);
                        }
                        notificationManager.notify(1, builder.build());
                    });
                }
            });


        });


    }
}