package com.wallpo.android.fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MonetizeFragment extends Fragment {

    TextView subtxt, note;
    CardView cardview, sendmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monetize, container, false);

        subtxt = view.findViewById(R.id.subtxt);
        cardview = view.findViewById(R.id.cardview);
        cardview.setVisibility(View.GONE);
        note = view.findViewById(R.id.note);
        note.setVisibility(View.GONE);
        sendmail = view.findViewById(R.id.sendmail);
        sendmail.setVisibility(View.GONE);


        updatecode.analyticsFirebase(getContext(), "monetize_view", "monetize_view");


        final SharedPreferences sharepref = getApplicationContext().getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        OkHttpClient client = new OkHttpClient();

        if (!sharepref.getString("wallpouserid", "").isEmpty()) {

            RequestBody postData = new FormBody.Builder().add("userid", sharepref.getString("wallpouserid", "")).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.userinfo)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("ProfileFragment", "onFailure: " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    String subscribers = object.getString("subscribers").trim();
                                    String emailid = object.getString("email").trim();

                                    int subs = Integer.parseInt(subscribers);

                                    ValueAnimator animator = ValueAnimator.ofInt(0, subs);
                                    animator.setDuration(2500);
                                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            subtxt.setText(animation.getAnimatedValue().toString() + " / 20000 Subscribers");
                                        }
                                    });
                                    animator.start();

                                    cardview.setVisibility(View.VISIBLE);
                                    note.setVisibility(View.VISIBLE);

                                    if (subs < 20000) {
                                        cardview.setOnClickListener(v -> {

                                            note.setText(getResources().getString(R.string.morethannoticesubs));
                                            sendmail.setVisibility(View.VISIBLE);

                                            sendmail.setOnClickListener(view1 -> {
                                                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                                                emailIntent.setType("plain/text");
                                                emailIntent.putExtra(Intent.EXTRA_EMAIL, "ads@thewallpo.com");
                                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.enablemonetizationfor) + emailid);

                                                startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.sendmail)));


                                                updatecode.analyticsFirebase(getContext(), "monetize_clicked_noneligible", "monetize_clicked_noneligible");


                                            });
                                        });
                                    } else if (subs > 20000) {
                                        cardview.setOnClickListener(v -> {
                                            note.setText(getResources().getString(R.string.lessthanlongsubs));


                                            updatecode.analyticsFirebase(getContext(), "monetize_clicked_eligible", "monetize_clicked_eligible");


                                            note.setVisibility(View.GONE);
                                            sendmail.setVisibility(View.VISIBLE);

                                            sendmail.setOnClickListener(view1 -> {
                                                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                                                emailIntent.setType("plain/text");
                                                emailIntent.putExtra(Intent.EXTRA_EMAIL, "ads@thewallpo.com");
                                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.enablemonetizationfor)+ emailid);

                                                startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.sendmail)));

                                            });

                                        });

                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

        } else {
            subtxt.setText("0 / 20000 Subscribers");
        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.explore));
            MainActivity.monetize.setText(getResources().getString(R.string.Monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));

        } catch (NullPointerException e) {
            Log.d("ProfileFragment", "onStart: ");
        }
    }
}
