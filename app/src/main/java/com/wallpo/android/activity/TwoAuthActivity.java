package com.wallpo.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.wallpo.android.LoginActivity;
import com.wallpo.android.R;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TwoAuthActivity extends AppCompatActivity {

    Button enable, resendcode, submit, disable;
    ConstraintLayout mainlay, checkverification, disablelay;
    SpinKitView loadingbar;
    Context context = this;
    MaterialCheckBox emailauth, phoneauth;
    String auth = "";
    String email, displayname, useridenc;
    AppCompatTextView noticeup, sec, recovercode;
    int random1;
    int seconds = 0;
    TextInputEditText codefeild;
    String wholeCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        enable = findViewById(R.id.enable);
        mainlay = findViewById(R.id.mainlay);
        mainlay.setVisibility(View.GONE);
        loadingbar = findViewById(R.id.loadingbar);
        loadingbar.setVisibility(View.VISIBLE);
        emailauth = findViewById(R.id.emailauth);
        phoneauth = findViewById(R.id.phoneauth);
        noticeup = findViewById(R.id.noticeup);
        sec = findViewById(R.id.sec);
        resendcode = findViewById(R.id.resendcode);
        resendcode.setVisibility(View.GONE);
        submit = findViewById(R.id.submit);
        codefeild = findViewById(R.id.codefeild);
        checkverification = findViewById(R.id.checkverification);
        disablelay = findViewById(R.id.disablelay);
        disablelay.setVisibility(View.GONE);
        disable = findViewById(R.id.disable);
        recovercode = findViewById(R.id.recovercode);


        final SharedPreferences sharepref = getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharepref.getString("wallpouserid", "");

        if (userid.isEmpty()) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }


        updatecode.analyticsFirebase(context, "twoauth_activity", "twoauth_activity");


        phoneauth.setOnClickListener(v -> {

            phoneauth.setChecked(false);

            Snackbar.make(phoneauth, getResources().getString(R.string.phoneauthavailable), Snackbar.LENGTH_LONG).show();
        });

        enable.setOnClickListener(v -> {

            if (!emailauth.isChecked()) {

                Snackbar.make(phoneauth, getResources().getString(R.string.authtype), Snackbar.LENGTH_LONG).show();
                return;
            }

            checkverification.setVisibility(View.VISIBLE);
            Random r = new Random();
            random1 = r.nextInt(9999 - 1111) + 1111;

            sendverification(context, random1, email, displayname);

            runquery();

        });


        resendcode.setOnClickListener(v -> {
            Random r = new Random();
            random1 = r.nextInt(9999 - 1111) + 1111;

            sendverification(context, random1, email, displayname);
            runquery();
        });


        submit.setOnClickListener(v -> {

            if (codefeild.getText().toString().isEmpty()) {
                Toast.makeText(context, getResources().getString(R.string.entercode), Toast.LENGTH_SHORT).show();
                return;
            }

            if (String.valueOf(random1).equals(codefeild.getText().toString())) {

                loadingbar.setVisibility(View.VISIBLE);

                submit.setClickable(false);

                OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder()
                        .add("email", email)
                        .add("verifycode", codefeild.getText().toString())
                        .add("fcm", sharepref.getString("fcmtoken", "")).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.authverifylogin)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();

                                loadingbar.setVisibility(View.GONE);

                                submit.setClickable(true);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");

                        runOnUiThread(() -> {
                            if (data.trim().contains("verified")) {
                                Toast.makeText(context, getResources().getString(R.string.emailauthenabled), Toast.LENGTH_SHORT).show();

                                loadingbar.setVisibility(View.GONE);

                                submit.setClickable(true);

                                disablelay.setVisibility(View.VISIBLE);
                                checkverification.setVisibility(View.GONE);
                                mainlay.setVisibility(View.GONE);

                            } else {

                                loadingbar.setVisibility(View.GONE);

                                submit.setClickable(true);

                                Toast.makeText(context, getResources().getString(R.string.errorupdating), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            } else {
                Toast.makeText(context, getResources().getString(R.string.pleasecheckcode), Toast.LENGTH_SHORT).show();
            }

        });

        disable.setOnClickListener(v -> {
            loadingbar.setVisibility(View.VISIBLE);
            disable.setClickable(false);

            OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder()
                    .add("email", email).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.disabletwoauth)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        loadingbar.setVisibility(View.GONE);
                        disable.setClickable(true);

                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(() -> {
                        if (data.trim().contains("done")) {
                            loadingbar.setVisibility(View.GONE);
                            disable.setClickable(true);

                            mainlay.setVisibility(View.VISIBLE);
                            loadingbar.setVisibility(View.GONE);
                            disablelay.setVisibility(View.GONE);

                        } else {
                            loadingbar.setVisibility(View.GONE);
                            disable.setClickable(true);

                            Toast.makeText(context, getResources().getString(R.string.errorupdating), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        });

        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder()
                .add("userid", userid)
                .add("fcm", sharepref.getString("fcmtoken", "")).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.userdiableinfo)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    mainlay.setVisibility(View.VISIBLE);
                    enable.setText(getResources().getString(R.string.connectionerrorbig));
                    enable.setClickable(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(() -> {

                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            email = object.getString("email").trim();
                            displayname = object.getString("displayname").trim();
                            useridenc = object.getString("useridenc").trim();
                            String auth = object.getString("auth").trim();

                            String[] items = useridenc.split("");
                            wholeCode = "";
                            for (String item : items) {
                                try {
                                    int datas = Integer.parseInt(String.valueOf(items));
                                    wholeCode = wholeCode + datas;
                                } catch (NumberFormatException e) {
                                    char ch = item.charAt(0);
                                    int pos = ch - 'a' + 1;
                                    wholeCode = wholeCode + pos;
                                }
                            }

                            if (wholeCode.length() > 6) {
                                wholeCode = wholeCode.replace("-", "")
                                        .substring(0, 6);
                            } else {
                                wholeCode = wholeCode.replace("-", "");
                            }

                            recovercode.setText(getResources().getString(R.string.recoverycodeis) + " - " + wholeCode + " \n " +
                                    getResources().getString(R.string.recoverycodeisusethis));

                            noticeup.setText(getResources().getString(R.string.verficationemailsend1) + email + getResources().getString(R.string.verficationemailsend2));

                            if (auth.contains("email") && auth.contains("true")) {
                                disablelay.setVisibility(View.VISIBLE);

                                mainlay.setVisibility(View.GONE);
                                loadingbar.setVisibility(View.GONE);

                            } else if (auth.contains("email") && auth.contains("false")) {
                                mainlay.setVisibility(View.VISIBLE);
                                loadingbar.setVisibility(View.GONE);
                                disablelay.setVisibility(View.GONE);

                            }

                        }

                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }

                });

            }
        });

    }

    private void runquery() {

        resendcode.setVisibility(View.GONE);
        seconds = 60;
        sec.setText(seconds + getResources().getString(R.string.seconds));

        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                seconds = seconds - 1;

                if (seconds > 1) {
                    ha.postDelayed(this, 1000);
                    sec.setVisibility(View.VISIBLE);
                    sec.setText(seconds + getResources().getString(R.string.seconds));
                } else {
                    sec.setVisibility(View.GONE);
                    resendcode.setVisibility(View.VISIBLE);
                }

            }
        }, 1000);

    }

    public static void sendverification(Context context, int random, String email, String displayname) {

        OkHttpClient client = new OkHttpClient();

        RequestBody postData = new FormBody.Builder()
                .add("email", email)
                .add("displayname", displayname)
                .add("token", String.valueOf(random))
                .add("deviceinfo", Build.MANUFACTURER + " " + Build.MODEL).build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.sendauthmail)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                ((Activity)context).runOnUiThread(() -> {
                    if (data.trim().contains("done")) {
                        Toast.makeText(context, context.getResources().getString(R.string.verificationmailsend), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.errorwhilsending), Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

    }
}