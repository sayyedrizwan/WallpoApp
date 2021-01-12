package com.wallpo.android.extra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.activity.AlbumsActivity;
import com.wallpo.android.activity.ViewPostsActivity;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.wallpo.android.utils.updatecode.getusernameid;

public class DeepLinkActivity extends AppCompatActivity {

    private static final String TAG = "DeepLinking";
    String link;
    Context context = this;
    private String id, email;
    SpinKitView loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);

        Intent intent = getIntent();

        if(intent == null){
            startActivity(new Intent(context, MainActivity.class));
            return;
        }
        link = intent.getData().toString();

        if (link.contains("://wallpotestdata")) {
            link = link.replace("://wallpotestdata", "");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("wallpouserid", "");

        loadingbar = findViewById(R.id.loadingbar);
        loadingbar.setVisibility(View.VISIBLE);

        if (intent == null || intent.getData() == null) {
            finish();
            startActivity(new Intent(context, MainActivity.class));
            Toast.makeText(this, context.getResources().getString(R.string.linkerror), Toast.LENGTH_SHORT).show();
            return;
        }

        if (link.isEmpty()) {
            // loadingbar.setVisibility(View.GONE);
            finish();
            startActivity(new Intent(context, MainActivity.class));
            return;
        }
        if (link.equals("https://thewallpo.com") || link.equals("https://thewallpo.com/")
                || link.equals("thewallpo.com/") || link.equals("thewallpo.com")
                || link.equals("http://thewallpo.com/") || link.equals("http://thewallpo.com")) {
            // loadingbar.setVisibility(View.GONE);
            finish();
            startActivity(new Intent(context, MainActivity.class));
            return;
        }

        if (link.contains("posts/")) {

            updatecode.analyticsFirebase(context, "posts_share_link", "posts_share_link");


            String strs = link;
            String remainder = strs.substring(strs.indexOf("/posts/") + 1);

            String getit = remainder.replace("posts/", "")
                    .replace("R12pQ", "").replace("Wz1P", "");

            if (getit.isEmpty()) {

                // loadingbar.setVisibility(View.GONE);
                finish();
                startActivity(new Intent(context, MainActivity.class));
                return;
            }

            OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("id", String.valueOf(getit)).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.getphotoid)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: ", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(() -> {

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                int photoid = object.getInt("photoid");
                                String userid = object.getString("userid").trim();
                                String likes = object.getString("likes").trim();

                                final String caption = object.getString("caption").trim();
                                final String categoryid = object.getString("categoryid").trim();
                                final String albumid = object.getString("albumid").trim();
                                String imagepath = object.getString("imagepath").trim();
                                final String datecreated = object.getString("datecreated").trim();
                                final String dateshowed = object.getString("dateshowed").trim();
                                final String link = object.getString("link").trim();
                                final String trendingcount = object.getString("trendingcount").trim();

                                if (data.equals("[]")) {

                                    // loadingbar.setVisibility(View.GONE);
                                    Toast.makeText(context, context.getResources().getString(R.string.postnotfound), Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(context, MainActivity.class));

                                    return;
                                } else if (String.valueOf(photoid).isEmpty()) {

                                    // loadingbar.setVisibility(View.GONE);
                                    Toast.makeText(context, context.getResources().getString(R.string.postnotfound), Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(context, MainActivity.class));

                                    return;
                                } else {

                                    // loadingbar.setVisibility(View.GONE);
                                    finish();
                                    startActivity(new Intent(context, MainActivity.class));
                                    Intent intent = new Intent(context, ViewPostsActivity.class);

                                    SharedPreferences sharepref = context.getApplicationContext().getSharedPreferences("wallpo", 0);
                                    SharedPreferences.Editor editor = sharepref.edit();

                                    editor.putString("photoid", String.valueOf(photoid));
                                    editor.putString("useridprofile", userid);
                                    editor.putString("caption", caption);
                                    editor.putString("categoryid", categoryid);
                                    editor.putString("albumid", albumid);
                                    editor.putString("datecreated", datecreated);
                                    editor.putString("dateshowed", dateshowed);
                                    editor.putString("link", link);
                                    editor.putString("userid", userid);
                                    editor.putString("imagepath", imagepath);
                                    editor.putString("trendingcount", trendingcount);
                                    editor.putString("likes", likes);
                                    editor.apply();

                                    context.startActivity(intent);

                                    return;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                }
            });


        } else if (link.contains("album/")) {

            updatecode.analyticsFirebase(context, "album_share_link", "album_share_link");

            String strs = link;
            String remainder = strs.substring(strs.indexOf("/album/") + 1);

            String getit = remainder.replace("album/", "").replace("AL_1DR", " ").replace("SO1ML", " ");

            OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("albumid", getit).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.albuminfo)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: ", e);

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(() -> {

                        try {
                            JSONArray array = new JSONArray(data);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                String albumid = object.getString("albumid").trim();
                                String userid = object.getString("userid").trim();
                                final String albumname = object.getString("albumname").trim();
                                final String albumdesc = object.getString("albumdesc").trim();
                                final String albumcreated = object.getString("albumcreated").trim();
                                String albumurl = object.getString("albumurl").trim();

                                if (data.equals("[]")) {

                                    // loadingbar.setVisibility(View.GONE);

                                    Toast.makeText(context, context.getResources().getString(R.string.albumnotfound), Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(context, MainActivity.class));

                                    return;
                                } else if (String.valueOf(albumid).isEmpty()) {

                                    // loadingbar.setVisibility(View.GONE);

                                    Toast.makeText(context, context.getResources().getString(R.string.albumnotfound), Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(context, MainActivity.class));

                                    return;
                                } else {

                                    // loadingbar.setVisibility(View.GONE);
                                    finish();

                                    Intent intent = new Intent(context, AlbumsActivity.class);

                                    sharedPreferences.edit().putString("albumid", albumid).apply();
                                    sharedPreferences.edit().putString("albumname", albumname).apply();
                                    sharedPreferences.edit().putString("albumdesc", albumdesc).apply();
                                    sharedPreferences.edit().putString("albumurl", albumurl).apply();
                                    sharedPreferences.edit().putString("albumuserid", userid).apply();
                                    sharedPreferences.edit().putString("albumcreated", albumcreated).apply();

                                    context.startActivity(intent);

                                    return;
                                }
                            }

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

        } else {

            if (link.contains("B1P") && link.contains("R_S")) {

                updatecode.analyticsFirebase(context, "user_share_link", "user_share_link");

                // loadingbar.setVisibility(View.GONE);

                String ch = link.substring(link.lastIndexOf("/") + 1);

                String userid = ch.replace("B1P", "").replace("R_S", "");

                final OkHttpClient client = new OkHttpClient();

                RequestBody postData = new FormBody.Builder().add("userid", userid).build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URLS.userinfo)
                        .post(postData)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        Log.e(TAG, "onFailure: ", e);

                    }

                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        final String data = response.body().string().replaceAll(",\\[]", "");

                        runOnUiThread(() -> {

                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    String usernametxt = object.getString("username").trim();
                                    String displayname = object.getString("displayname").trim();
                                    String profilepictxt = object.getString("profilephoto").trim();

                                    String verifiedtxt = object.getString("verified").trim();
                                    String description = object.getString("description").trim();
                                    String categorytxt = object.getString("category").trim();
                                    String websitestxt = object.getString("websites").trim();
                                    String backphoto = object.getString("backphoto").trim();
                                    String subscribed = object.getString("subscribed").trim();
                                    String subscribers = object.getString("subscribers").trim();

                                    sharedPreferences.edit().putString("useridprofile", userid).apply();
                                    sharedPreferences.edit().putString("usernameprofile", usernametxt).apply();
                                    sharedPreferences.edit().putString("displaynameprofile", displayname).apply();
                                    sharedPreferences.edit().putString("profilephotoprofile", profilepictxt).apply();
                                    sharedPreferences.edit().putString("verifiedprofile", verifiedtxt).apply();
                                    sharedPreferences.edit().putString("descriptionprofile", description).apply();
                                    sharedPreferences.edit().putString("websitesprofile", websitestxt).apply();
                                    sharedPreferences.edit().putString("categoryprofile", categorytxt).apply();
                                    sharedPreferences.edit().putString("backphotoprofile", backphoto).apply();
                                    sharedPreferences.edit().putString("subscribedprofile", subscribed).apply();
                                    sharedPreferences.edit().putString("subscriberprofile", subscribers).apply();

                                    // loadingbar.setVisibility(View.GONE);

                                    finish();
                                    startActivity(new Intent(context, ProfileActivity.class));

                                    return;
                                }

                            } catch (
                                    JSONException e) {
                                e.printStackTrace();
                            }
                        });


                    }
                });

                return;
            }

            String s = link;
            int counter = s.split("/", -1).length - 1;

            if (counter > 3) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                // loadingbar.setVisibility(View.GONE);
                Toast.makeText(context, context.getResources().getString(R.string.updateyourapp), Toast.LENGTH_SHORT).show();
            }

            String ch = link.substring(link.lastIndexOf("/") + 1);

            getusernameid(context, " " + ch);
            new Handler().postDelayed(this::finish, 2000);
        }

    }
}