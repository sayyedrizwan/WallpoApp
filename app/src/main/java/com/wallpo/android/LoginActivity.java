package com.wallpo.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.wallpo.android.activity.TwoAuthActivity;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.wallpo.android.MainActivity.initializeSSLContext;
import static com.wallpo.android.utils.Cache.deleteCache;

public class LoginActivity extends AppCompatActivity {

    CardView facebooksignin, googlesignin;
    CallbackManager callbackManager;
    LoginButton fbbutton;
    Context context = this;
    RelativeLayout signin, login, mainid, loginbutton;
    ImageView close;
    TextView signup, username, loginnot, signintext, forgetpassword;
    SpinKitView spinkit, fbloadding, googleloadding;
    TextInputEditText emailid, password;
    SQLiteDatabase mDatabase;
    String name, photo;
    SwitchMaterial passswitvh;
    int seconds = 0;
    int random;
    AppCompatTextView time, resend, termandcondition;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeSSLContext(context);

        facebooksignin = findViewById(R.id.facebooksignin);
        googlesignin = findViewById(R.id.googlesignin);
        fbbutton = findViewById(R.id.fbbutton);
        signin = findViewById(R.id.signin);
        signin.setVisibility(View.VISIBLE);
        mainid = findViewById(R.id.mainid);
        close = findViewById(R.id.close);
        login = findViewById(R.id.login);
        login.setVisibility(GONE);
        signup = findViewById(R.id.signup);
        username = findViewById(R.id.username);
        loginnot = findViewById(R.id.loginnot);
        signintext = findViewById(R.id.signintext);
        spinkit = findViewById(R.id.spinkit);
        spinkit.setVisibility(GONE);
        fbloadding = findViewById(R.id.fbloadding);
        fbloadding.setVisibility(GONE);
        googleloadding = findViewById(R.id.googleloadding);
        googleloadding.setVisibility(GONE);
        loginbutton = findViewById(R.id.loginbutton);
        emailid = findViewById(R.id.emailid);
        password = findViewById(R.id.password);
        passswitvh = findViewById(R.id.passswitvh);
        forgetpassword = findViewById(R.id.forgetpassword);
        termandcondition = findViewById(R.id.termandcondition);

        sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        MainActivity.getlocation(context);
        termandcondition.setOnClickListener(view -> {
            Intent viewIntent =
                    new Intent("android.intent.action.VIEW",
                            Uri.parse("https://thewallpo.com/tandc"));
            startActivity(viewIntent);
        });

        switch (Common.logintype) {
            case "fblogin":

                fbbutton.performClick();

                updatecode.analyticsFirebase(context, "fb_login", "fb_login");

                break;
            case "googlelogin":

                deleteCache(context);

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 102);

                break;
            case "wallpologin":

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(mainid);
                }

                signin.setVisibility(GONE);
                login.setVisibility(View.VISIBLE);

                ((CardView) findViewById(R.id.wallposign)).setCardBackgroundColor(getResources().getColor(R.color.white));
                break;
        }

        signup.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(mainid);
            }

            if (username.getVisibility() == VISIBLE) {

                username.setVisibility(GONE);
                loginnot.setText(context.getResources().getString(R.string.loginbig));
                signup.setText(getResources().getString(R.string.donthaveaccount));
                signintext.setText(context.getResources().getString(R.string.signin));


            } else {

                username.setVisibility(View.VISIBLE);
                loginnot.setText(context.getResources().getString(R.string.signupbig));
                signup.setText(context.getResources().getString(R.string.loginsmall));
                signintext.setText(context.getResources().getString(R.string.signin));
                signintext.setText(context.getResources().getString(R.string.signup));
            }
        });

        signin.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(mainid);
            }

            signin.setVisibility(GONE);
            login.setVisibility(View.VISIBLE);

            ((CardView) findViewById(R.id.wallposign)).setCardBackgroundColor(getResources().getColor(R.color.white));

        });

        passswitvh.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            }
        });

        loginbutton.setOnClickListener(view -> {

            final String tz = TimeZone.getDefault().getID();

            Common.logintype = "";

            if (username.getVisibility() == GONE) {
                //login

                if (emailid.getText().toString() == null) {
                    Toast.makeText(context, getResources().getString(R.string.enteremail), Toast.LENGTH_LONG).show();
                    return;
                }
                if (emailid.getText().toString().isEmpty()) {
                    Toast.makeText(context, getResources().getString(R.string.enteremail), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!emailid.getText().toString().contains("@")) {
                    Toast.makeText(context, getResources().getString(R.string.emailnotvaild), Toast.LENGTH_LONG).show();
                    return;
                }

                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(context, getResources().getString(R.string.enterpassword), Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(context, getResources().getString(R.string.enterpassword), Toast.LENGTH_LONG).show();
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(mainid);
                }

                loginnot.setVisibility(GONE);
                spinkit.setVisibility(View.VISIBLE);

                loginuser(URLS.login, "", "", "", "");

            } else {
                //signup

                Common.logintype = "";

                if (emailid.getText().toString() == null) {
                    Toast.makeText(context, getResources().getString(R.string.enteremail), Toast.LENGTH_LONG).show();
                    return;
                }
                if (emailid.getText().toString().isEmpty()) {
                    Toast.makeText(context, getResources().getString(R.string.enteremail), Toast.LENGTH_LONG).show();
                    return;
                }
                if (username.getText().toString().isEmpty()) {
                    Toast.makeText(context, getResources().getString(R.string.entername), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!emailid.getText().toString().contains("@")) {
                    Toast.makeText(context, getResources().getString(R.string.emailnotvaild), Toast.LENGTH_LONG).show();
                    return;
                }

                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(context, getResources().getString(R.string.enterpassword), Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(context, getResources().getString(R.string.entersixchar), Toast.LENGTH_LONG).show();
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(mainid);
                }
                Common.logintype = "";
                loginnot.setVisibility(GONE);
                spinkit.setVisibility(VISIBLE);

                final OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder().add("email", emailid.getText().toString().trim().replace("'", "\\'"))
                        .add("password", password.getText().toString().trim().replace("'", "\\'")).add("displayname", username.getText().toString().trim().replace("'", "\\'"))
                        .add("code", "25874566515").add("timezone", tz)
                        .add("ipadd", sharedPreferences.getString("useripaddress", ""))
                        .add("fcm", sharedPreferences.getString("fcmtoken", ""))
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.signup)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(context, getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                TransitionManager.beginDelayedTransition(mainid);
                            }

                            loginnot.setVisibility(VISIBLE);
                            spinkit.setVisibility(GONE);

                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");
                        runOnUiThread(() -> {
                            if (data.trim().contains("unauth")) {

                                Toast.makeText(context, getResources().getString(R.string.autherror), Toast.LENGTH_SHORT).show();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    TransitionManager.beginDelayedTransition(mainid);
                                }

                                loginnot.setVisibility(View.VISIBLE);
                                spinkit.setVisibility(GONE);

                                return;
                            }

                            if (data.contains("Email registered")) {

                                Toast.makeText(context, getResources().getString(R.string.emailregistered), Toast.LENGTH_SHORT).show();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    TransitionManager.beginDelayedTransition(mainid);
                                }

                                loginnot.setVisibility(View.VISIBLE);
                                spinkit.setVisibility(GONE);


                            } else if (data.contains("successfully")) {
                                Toast.makeText(context, getResources().getString(R.string.registersuccessfully), Toast.LENGTH_SHORT).show();


                                loginuser(URLS.login, "", "", "", "");
                            }

                        });

                    }
                });


            }

        });

        forgetpassword.setOnClickListener(view -> {

            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.resetpassword_layout);

            Button cancel = dialog.findViewById(R.id.cancel);
            Button done = dialog.findViewById(R.id.done);
            SpinKitView loadingbar = dialog.findViewById(R.id.loadingbar);
            loadingbar.setVisibility(GONE);
            TextInputEditText email = dialog.findViewById(R.id.email);

            cancel.setOnClickListener(view1 -> {
                dialog.dismiss();
            });

            done.setOnClickListener(view1 -> {

                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(context, context.getResources().getString(R.string.entervalidemail), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!email.getText().toString().contains("@")) {
                    Toast.makeText(context, context.getResources().getString(R.string.entervalidemail), Toast.LENGTH_SHORT).show();
                    return;
                }

                loadingbar.setVisibility(VISIBLE);

                Random r = new Random();
                random = r.nextInt(99999 - 11111) + 1111;

                OkHttpClient client = new OkHttpClient();
                RequestBody postData = new FormBody.Builder().add("email", email.getText().toString())
                        .add("code", String.valueOf(random))
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.resetpassword)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loadingbar.setVisibility(GONE);

                                Toast.makeText(context, context.getResources().getString(R.string.errorwhilesendingmail), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");

                        runOnUiThread(() -> {

                            if (data.trim().contains("nofound")) {
                                loadingbar.setVisibility(GONE);

                                Toast.makeText(context, context.getResources().getString(R.string.emailnotfound), Toast.LENGTH_SHORT).show();
                            } else if (data.trim().contains("error")) {
                                loadingbar.setVisibility(GONE);

                                Toast.makeText(context, context.getResources().getString(R.string.errorwhilsending), Toast.LENGTH_SHORT).show();
                            } else if (data.trim().contains("done")) {
                                loadingbar.setVisibility(GONE);

                                Toast.makeText(context, context.getResources().getString(R.string.resetlinksend), Toast.LENGTH_SHORT).show();

                                Toast.makeText(context, context.getResources().getString(R.string.checkemail), Toast.LENGTH_SHORT).show();

                                dialog.dismiss();

                            } else {
                                loadingbar.setVisibility(GONE);

                                Toast.makeText(context, context.getResources().getString(R.string.errorwhilesendingmail), Toast.LENGTH_SHORT).show();
                            }

                        });

                    }
                });

            });

            dialog.show();


        });

        close.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(mainid);
            }

            login.setVisibility(GONE);
            signin.setVisibility(View.VISIBLE);

        });

        facebooksignin.setOnClickListener(view -> fbbutton.performClick());

        fbbutton.setPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        fbbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {

                    try {
                        String firstname = object.getString("first_name");
                        String lastname = object.getString("last_name");
                        String email = object.getString("email");
                        String id = object.getString("id");
                        String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                        if (email.isEmpty()) {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.fbnotice), Toast.LENGTH_LONG).show();
                            return;
                        }

                        emailid.setText(email);
                        password.setText(id);
                        photo = String.valueOf(image_url);
                        name = firstname + " " + lastname;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(mainid);
                        }

                        fbloadding.setVisibility(View.VISIBLE);
                        findViewById(R.id.fbarrow).setVisibility(GONE);
                        loginuser(URLS.googlesignin, email, firstname + lastname, image_url, id);


                        LoginManager.getInstance().logOut();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name, last_name, email, id");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, getResources().getString(R.string.fbcancel), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(context, getResources().getString(R.string.fberror), Toast.LENGTH_SHORT).show();

            }
        });

        googlesignin.setOnClickListener(view -> {

            deleteCache(context);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 102);

        });

        /*

        SharedPreferences sharepref = getApplicationContext().getSharedPreferences("wallpo", 0);
        Configuration configuration = getResources().getConfiguration();
        int currentNightMode = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:

                if (!sharepref.getString("darkmode", "").equals("noactive")) {
                    ((CardView) findViewById(R.id.googlesignin)).setCardBackgroundColor(getResources().getColor(R.color.black));
                    ((CardView) findViewById(R.id.facebooksignin)).setCardBackgroundColor(getResources().getColor(R.color.black));
                    ((CardView) findViewById(R.id.wallposign)).setCardBackgroundColor(getResources().getColor(R.color.black));
                    ((SpinKitView) findViewById(R.id.googleloadding)).setColor(getResources().getColor(R.color.white));
                    ((SpinKitView) findViewById(R.id.fbloadding)).setColor(getResources().getColor(R.color.white));
                    ((TextView) findViewById(R.id.googletext)).setTextColor(getResources().getColor(R.color.white));
                    ((TextView) findViewById(R.id.fbtext)).setTextColor(getResources().getColor(R.color.white));
                    ((TextView) findViewById(R.id.twtext)).setTextColor(getResources().getColor(R.color.white));
                    ((ImageView) findViewById(R.id.googlearrow)).setImageResource(R.drawable.ic_left_arrow_white);
                    ((ImageView) findViewById(R.id.fbarrow)).setImageResource(R.drawable.ic_left_arrow_white);
                    ((ImageView) findViewById(R.id.wallpoarrow)).setImageResource(R.drawable.ic_left_arrow_white);

                }
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                if (sharepref.getString("darkmode", "").equals("active")) {
                    ((CardView) findViewById(R.id.googlesignin)).setCardBackgroundColor(getResources().getColor(R.color.black));
                    ((CardView) findViewById(R.id.facebooksignin)).setCardBackgroundColor(getResources().getColor(R.color.black));
                    ((CardView) findViewById(R.id.wallposign)).setCardBackgroundColor(getResources().getColor(R.color.black));
                    ((SpinKitView) findViewById(R.id.googleloadding)).setColor(getResources().getColor(R.color.white));
                    ((SpinKitView) findViewById(R.id.fbloadding)).setColor(getResources().getColor(R.color.white));
                    ImageView googlearrow = findViewById(R.id.googlearrow);
                    DrawableCompat.setTint(googlearrow.getDrawable(), ContextCompat.getColor(context, R.color.white));
                    ImageView fbarrow = findViewById(R.id.fbarrow);
                    DrawableCompat.setTint(fbarrow.getDrawable(), ContextCompat.getColor(context, R.color.white));

                }
                break;
        }

*/
    }

    private void loginuser(final String url, String email, String name, String imagelink, String pass) {

        final String tz = TimeZone.getDefault().getID();

        final OkHttpClient client = new OkHttpClient();

        RequestBody postData;


        Common.logintype = "";

        if (photo == null) {
            photo = "";
        }

        if (url.contains("/googlesignin")) {

            postData = new FormBody.Builder()
                    .add("email", email)
                    .add("password", pass)
                    .add("pass", "123456789900")
                    .add("timezone", tz).add("photo", imagelink).add("displayname", name)
                    .add("fcm", sharedPreferences.getString("fcmtoken", ""))
                    .add("ipadd", sharedPreferences.getString("useripaddress", "")).build();

        } else {

            postData = new FormBody.Builder()
                    .add("email", emailid.getText().toString())
                    .add("password", password.getText().toString())
                    .add("pass", "123456789900")
                    .add("fcm", sharedPreferences.getString("fcmtoken", ""))
                    .add("ipadd", sharedPreferences.getString("useripaddress", ""))
                    .add("timezone", tz).build();
        }

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition(mainid);
                    }

                    loginnot.setVisibility(View.VISIBLE);
                    spinkit.setVisibility(GONE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition(mainid);
                    }

                    fbloadding.setVisibility(GONE);
                    findViewById(R.id.fbarrow).setVisibility(VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition(mainid);
                    }

                    fbloadding.setVisibility(GONE);
                    findViewById(R.id.fbarrow).setVisibility(VISIBLE);
                    googleloadding.setVisibility(GONE);
                    findViewById(R.id.googlearrow).setVisibility(VISIBLE);
                    Toast.makeText(context, getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(() -> {

                    if (data.trim().contains("authfailed")) {
                        Toast.makeText(context, getResources().getString(R.string.autherror), Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(mainid);
                        }

                        loginnot.setVisibility(View.VISIBLE);
                        spinkit.setVisibility(GONE);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(mainid);
                        }

                        fbloadding.setVisibility(GONE);
                        findViewById(R.id.fbarrow).setVisibility(VISIBLE);
                        googleloadding.setVisibility(GONE);
                        findViewById(R.id.googlearrow).setVisibility(VISIBLE);
                        return;
                    }

                    if (data.trim().equals("[]")) {
                        Toast.makeText(context, getResources().getString(R.string.emailidpassnotmatched), Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(mainid);
                        }

                        loginnot.setVisibility(View.VISIBLE);
                        spinkit.setVisibility(GONE);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(mainid);
                        }

                        fbloadding.setVisibility(GONE);
                        findViewById(R.id.fbarrow).setVisibility(VISIBLE);
                        googleloadding.setVisibility(GONE);
                        findViewById(R.id.googlearrow).setVisibility(VISIBLE);

                        return;
                    }


                    try {
                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            final String userid = object.getString("userid").trim();
                            final String emailtext = object.getString("email").trim();
                            final String displayname = object.getString("displayname");
                            final String password = object.getString("password").trim();
                            final String useridenc = object.getString("useridenc").trim();
                            String auth = object.getString("auth").trim();


                            final SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);
                            sharepref.edit().clear().apply();

                            final SharedPreferences shareprefs = context.getApplicationContext().getSharedPreferences("albumwallpo", 0);
                            shareprefs.edit().clear().apply();

                            final SharedPreferences shareprefusers = context.getApplicationContext().getSharedPreferences("wallpouserdata", 0);
                            shareprefusers.edit().clear().apply();

                            updatecode.analyticsFirebase(context, "login_wallpo", "login_wallpo");

                            if (auth.contains("email") && auth.contains("true")) {

                                String[] items = useridenc.split("");
                                String wholeCode = "";
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

                                fbloadding.setVisibility(GONE);
                                findViewById(R.id.fbarrow).setVisibility(VISIBLE);
                                googleloadding.setVisibility(GONE);
                                findViewById(R.id.googlearrow).setVisibility(VISIBLE);

                                BottomSheetDialog dialog = new BottomSheetDialog(context);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setCancelable(false);
                                dialog.setContentView(R.layout.login_auth_layout);

                                Button cancel = dialog.findViewById(R.id.cancel);
                                time = dialog.findViewById(R.id.time);
                                resend = dialog.findViewById(R.id.resend);
                                Button done = dialog.findViewById(R.id.done);
                                TextInputEditText sharetext = dialog.findViewById(R.id.sharetext);
                                SpinKitView loadingbar = dialog.findViewById(R.id.loadingbar);
                                loadingbar.setVisibility(GONE);

                                AppCompatTextView note = dialog.findViewById(R.id.note);

                                String emails = emailtext.substring(emailtext.lastIndexOf("@") + 1);

                                note.setText(context.getResources().getString(R.string.verficationemailsend1) + emailtext.substring(0, 2) + "******@" + emails + context.getResources().getString(R.string.verficationemailsend2));

                                Random r = new Random();
                                random = r.nextInt(99999 - 11111) + 1111;

                                TwoAuthActivity.sendverification(context, random, emailtext, displayname);

                                runquery();

                                resend.setOnClickListener(v -> {
                                    Random rs = new Random();
                                    random = rs.nextInt(99999 - 11111) + 1111;

                                    TwoAuthActivity.sendverification(context, random, emailtext, displayname);

                                    runquery();
                                });


                                cancel.setOnClickListener(v -> {
                                    dialog.dismiss();
                                });

                                String finalWholeCode = wholeCode;
                                done.setOnClickListener(v -> {
                                    if (String.valueOf(random).equals(sharetext.getText().toString())
                                            || finalWholeCode.equals(sharetext.getText().toString())) {

                                        loadingbar.setVisibility(VISIBLE);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                            TransitionManager.beginDelayedTransition(mainid);
                                        }

                                        loginnot.setVisibility(View.VISIBLE);
                                        spinkit.setVisibility(GONE);


                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                            TransitionManager.beginDelayedTransition(mainid);
                                        }

                                        fbloadding.setVisibility(GONE);
                                        findViewById(R.id.fbarrow).setVisibility(VISIBLE);
                                        googleloadding.setVisibility(GONE);
                                        findViewById(R.id.googlearrow).setVisibility(VISIBLE);

                                        SharedPreferences sharedaccounts = getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);

                                        String addaccounts = sharedaccounts.getString("accounts", "");

                                        if (addaccounts.contains(userid)) {

                                            addaccounts = addaccounts.replace(userid + ",", "");

                                        }

                                        if (url.contains("/googlesignin.php")) {
                                            sharedaccounts.edit().putString(userid + "password", "auth").apply();

                                        } else {
                                            sharedaccounts.edit().putString(userid + "password", password).apply();

                                        }
                                        String accountlists = userid + ", " + addaccounts;

                                        sharedaccounts.edit().putString("accounts", accountlists).apply();

                                        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);

                                        sharedPreferences.edit().putString("displaynameuser", displayname).apply();
                                        sharedPreferences.edit().putString("wallpouserid", userid).apply();
                                        sharedPreferences.edit().putString("emailid", emailtext).apply();

                                        if (url.contains("/googlesignin.php")) {
                                            sharedPreferences.edit().putString("password", "auth").apply();
                                        } else {
                                            sharedPreferences.edit().putString("password", password).apply();
                                        }

                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(context, context.getResources().getString(R.string.pleasecheckcode), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                dialog.show();

                            } else {
                                new Handler().postDelayed(() -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        TransitionManager.beginDelayedTransition(mainid);
                                    }

                                    loginnot.setVisibility(View.VISIBLE);
                                    spinkit.setVisibility(GONE);


                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        TransitionManager.beginDelayedTransition(mainid);
                                    }

                                    fbloadding.setVisibility(GONE);
                                    findViewById(R.id.fbarrow).setVisibility(VISIBLE);
                                    googleloadding.setVisibility(GONE);
                                    findViewById(R.id.googlearrow).setVisibility(VISIBLE);

                                    SharedPreferences sharedaccounts = getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);

                                    String addaccounts = sharedaccounts.getString("accounts", "");


                                    if (addaccounts.contains(userid)) {

                                        addaccounts = addaccounts.replace(userid + ", ", "");

                                    }

                                    if (url.contains("/googlesignin.php")) {
                                        sharedaccounts.edit().putString(userid + "password", "auth").apply();

                                    } else {
                                        sharedaccounts.edit().putString(userid + "password", password).apply();

                                    }

                                    String accountlists = userid + ", " + addaccounts;

                                    sharedaccounts.edit().putString("accounts", accountlists).apply();

                                    SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
                                    sharedPreferences.edit().putString("displaynameuser", displayname).apply();
                                    sharedPreferences.edit().putString("wallpouserid", userid).apply();
                                    sharedPreferences.edit().putString("emailid", emailtext).apply();

                                    if (url.contains("/googlesignin.php")) {
                                        sharedPreferences.edit().putString("password", "auth").apply();
                                    } else {
                                        sharedPreferences.edit().putString("password", password).apply();
                                    }

                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }, 2000);

                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                });

            }
        });

    }

    private void runquery() {

        resend.setVisibility(View.GONE);
        time.setVisibility(VISIBLE);
        seconds = 60;
        time.setText(seconds + context.getResources().getString(R.string.seconds));

        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {

                seconds = seconds - 1;

                if (seconds > 1) {

                    ha.postDelayed(this, 1000);

                    time.setVisibility(View.VISIBLE);

                    time.setText(seconds + context.getResources().getString(R.string.seconds));

                } else {

                    time.setVisibility(View.GONE);

                    resend.setVisibility(View.VISIBLE);

                }

            }
        }, 1000);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 102) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            emailid.setText(personEmail);
            password.setText(personId);
            photo = String.valueOf(personPhoto);
            name = personName;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(mainid);
            }

            googleloadding.setVisibility(View.VISIBLE);
            findViewById(R.id.googlearrow).setVisibility(GONE);

            loginuser(URLS.googlesignin, personEmail, personName, String.valueOf(personPhoto), personId);

            updatecode.analyticsFirebase(context, "google_login", "google_login");

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("LoginActivity", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}
