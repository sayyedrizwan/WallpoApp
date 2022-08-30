package com.wallpo.android.subscription;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import androidx.transition.TransitionManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.internal.model.ImmutableList;
import com.wallpo.android.R;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PremiumActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjwSEVRE8R/a61oLgpK1Zh8vqJ+okH9WN2ElbIoBUaknAt/DnQm7U8AyY4pM7FatPsTczJ8FQdpiuQbu1bLOtFygH1i4Hn/N0IGFZQXagIV4pS0efAKQa0MTGKjzd8VCDkhcZg8irIImu88JGFFUh8tEerhMIdatMtwJOXjsedpZhyCLYx8kTM6sSPdrn0ozqjk5NBZuyhhFWRsO0LDSUd5pr+4la458JWhshopBx+Wctm0nG4W+wt1/tAA+YamGnlamlVhYs/rygxZGRvaS25hC2S8D4J1xDKvkKVF2jKop6zbC2DGbfDRQlptvDxnY/x1LpotpAdP1IlggiXgv/RQIDAQAB";

    BillingClient mBillingClient;

    Context context = this;
    NestedScrollView scrollview, vaildscrollview, renewscrollview;
    RelativeLayout continues, defualtlay, onemonthslay, oneyearslay;
    AppCompatTextView sixprice, oneprice, oneyrsprice, expirydate, expirydateend;
    String subid = "";
    SpinKitView spin_kit;
    SharedPreferences sharedPreferences;
    String mainTime;
    Button changepackage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        scrollview = findViewById(R.id.scrollview);
        scrollview.setVisibility(View.GONE);
        continues = findViewById(R.id.continues);
        continues.setVisibility(View.GONE);

        sixprice = findViewById(R.id.sixprice);
        oneprice = findViewById(R.id.oneprice);
        oneyrsprice = findViewById(R.id.oneyrsprice);
        defualtlay = findViewById(R.id.defualtlay);
        onemonthslay = findViewById(R.id.onemonthslay);
        oneyearslay = findViewById(R.id.oneyearslay);
        spin_kit = findViewById(R.id.spin_kit);
        vaildscrollview = findViewById(R.id.vaildscrollview);
        vaildscrollview.setVisibility(View.GONE);
        expirydate = findViewById(R.id.expirydate);
        expirydateend = findViewById(R.id.expirydateend);
        expirydateend.setVisibility(View.GONE);
        renewscrollview = findViewById(R.id.renewscrollview);
        renewscrollview.setVisibility(View.GONE);
        changepackage = findViewById(R.id.changepackage);

        sharedPreferences = context.getSharedPreferences("wallpopremium", Context.MODE_PRIVATE);

        sharedPreferences.edit().putString("checkpremium", "check").apply();
        sharedPreferences.edit().putString("datecheck", "").apply();

        SharedPreferences sharedPreferencess = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String id = sharedPreferencess.getString("wallpouserid", "");
        PurchasesUpdatedListener purchaseUpdateListeners = (billingResult, purchases) -> {

        };

        PurchasesUpdatedListener purchaseUpdateListener = (billingResult, purchases) -> {
            // To be implemented in a later section.
            try {
                if (billingResult.getResponseCode() == 0) {
                    if (purchases.toString() != null) {

                        if (!purchases.toString().isEmpty()) {
                            try {
                                JSONArray array = new JSONArray(purchases.toString().replace("Purchase. Json:", ""));
                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    final String orderId = object.getString("orderId").trim();
                                    final String productId = object.getString("productId").trim();
                                    final String purchaseTime = object.getString("purchaseTime").trim();
                                    final String purchaseToken = object.getString("purchaseToken").trim();

                                    if (orderId.isEmpty()) {

                                        return;
                                    }

                                    sharedPreferences.edit().putString("orderId", orderId).apply();
                                    sharedPreferences.edit().putString("productId", productId).apply();
                                    sharedPreferences.edit().putString("purchaseTime", purchaseTime).apply();
                                    sharedPreferences.edit().putString("purchaseToken", purchaseToken).apply();

                                    spin_kit.setVisibility(View.VISIBLE);

                                    final OkHttpClient tokenClient = new OkHttpClient();

                                    RequestBody postData = new FormBody.Builder().add("userid", id)
                                            .add("couponused", productId).add("orderid", orderId)
                                            .add("subdate", purchaseTime).add("purchasetoken", purchaseToken).build();

                                    okhttp3.Request requestToken = new okhttp3.Request.Builder()
                                            .url(URLS.updatepremiumdata)
                                            .post(postData)
                                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                            .build();

                                    tokenClient.newCall(requestToken).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.d("TAG", "onFailure: ");
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            final String data = response.body().string().replaceAll(",\\[]", "");

                                            runOnUiThread(() -> {

                                                spin_kit.setVisibility(View.GONE);

                                                finish();

                                                startActivity(getIntent());

                                                if (data.contains("successfully")) {
                                                    Log.d("TAG", "onResponse: done");
                                                } else {

                                                    Log.d("TAG", "onResponse: error");
                                                }
                                            });

                                        }
                                    });

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }

            } catch (NullPointerException e) {
                Log.e("TAG", "onCreate: ", e);
            }
        };

        changepackage.setOnClickListener(view -> {

            TransitionManager.beginDelayedTransition(defualtlay);

            renewscrollview.setVisibility(View.GONE);
            vaildscrollview.setVisibility(View.GONE);
            continues.setVisibility(View.GONE);
            scrollview.setVisibility(View.VISIBLE);


        });

        defualtlay.setOnClickListener(view -> {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            onemonthslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            oneyearslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            subid = "sixmonthssub";

            continues.setVisibility(View.VISIBLE);

        });

        onemonthslay.setOnClickListener(view -> {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            onemonthslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            oneyearslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            subid = "onemonthssubscription";

            continues.setVisibility(View.VISIBLE);

        });

        oneyearslay.setOnClickListener(view -> {

            defualtlay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            onemonthslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersetting));

            oneyearslay.setBackground(getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            subid = "yearlysub";

            continues.setVisibility(View.VISIBLE);

        });

        String timezoneSaved = sharedPreferencess.getString("timezone", "");

        if (timezoneSaved.isEmpty()) {
            timezoneSaved = TimeZone.getDefault().getID();
        }

        Log.d("TAG", "onCreate: " + timezoneSaved);

        final OkHttpClient clientTime = new OkHttpClient();

        RequestBody postData = new FormBody.Builder().add("timezone", timezoneSaved).build();

        okhttp3.Request requestTime = new okhttp3.Request.Builder()
                .url(URLS.gettime)
                .post(postData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientTime.newCall(requestTime).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");
                runOnUiThread(() -> {

                    mainTime = data.replace(" ", "");
                });
            }
        });


        final OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URLS.getsubsammount)
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {

                    spin_kit.setVisibility(View.GONE);
                    new MaterialAlertDialogBuilder(context)
                            .setTitle(context.getResources().getString(R.string.servererror))
                            .setCancelable(false)
                            .setMessage(context.getResources().getString(R.string.connectionerrortryagain))
                            .setNegativeButton(context.getResources().getString(R.string.cancelbig), (dialogInterface, i) -> {
                                finish();
                            })
                            .setPositiveButton(context.getResources().getString(R.string.tryagainbig), (dialogInterface, i) -> {
                                finish();
                                startActivity(getIntent());
                            })
                            .show();

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

                            final String id = object.getString("id").trim();
                            final String onemonths = object.getString("onemonths").trim();
                            final String sixmonths = object.getString("sixmonths").trim();
                            final String oneyears = object.getString("oneyears").trim();

                            if (!id.isEmpty()) {
                                sixprice.setText(getString(R.string.symbol) + " " + sixmonths);
                                oneprice.setText(getString(R.string.symbol) + " " + onemonths);
                                oneyrsprice.setText(getString(R.string.symbol) + " " + oneyears);


                            } else {

                                new MaterialAlertDialogBuilder(context)
                                        .setTitle(context.getResources().getString(R.string.servererror))
                                        .setMessage(context.getResources().getString(R.string.connectionerrortryagain))
                                        .setNegativeButton(context.getResources().getString(R.string.cancelbig), (dialogInterface, ii) -> {
                                            finish();
                                        })
                                        .setPositiveButton(context.getResources().getString(R.string.tryagainbig), (dialogInterface, ii) -> {
                                            finish();
                                            startActivity(getIntent());
                                        })
                                        .show();

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        final OkHttpClient clientCheck = new OkHttpClient();

        RequestBody postData1 = new FormBody.Builder().add("usersid", id).build();

        okhttp3.Request requestCheck = new okhttp3.Request.Builder()
                .url(URLS.checksubuser)
                .post(postData1)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        clientCheck.newCall(requestCheck).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string().replaceAll(",\\[]", "");

                runOnUiThread(() -> {
                    spin_kit.setVisibility(View.GONE);

                    Log.d("TAG", "onResponse: premiumdata " + data);
                    if (data.trim().equals("[]")) {
                        sharedPreferences.edit().putBoolean("userIsPremium", false).apply();
                        scrollview.setVisibility(View.VISIBLE);
                        return;
                    }

                    try {


                        JSONArray array = new JSONArray(data);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject Object = array.getJSONObject(i);

                            String productId = Object.getString("couponused");
                            String orderId = Object.getString("orderid");
                            String purchaseTime = Object.getString("subdate");
                            String purchaseToken = Object.getString("purchasetoken");

                            sharedPreferences.edit().putString("orderId", orderId).apply();
                            sharedPreferences.edit().putString("productId", productId).apply();
                            sharedPreferences.edit().putString("purchaseTime", purchaseTime).apply();
                            sharedPreferences.edit().putString("purchaseToken", purchaseToken).apply();

                            continues.setVisibility(View.GONE);

                            if (mainTime == null) {
                                finish();
                                startActivity(getIntent());
                            } else {

                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = format.parse(updatecode.getDate(Long.parseLong(purchaseTime), "dd-MM-yyyy"));
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                switch (productId) {
                                    case "sixmonthssub":
                                        calendar.add(Calendar.MONTH, 6);
                                        break;
                                    case "onemonthssubscription":
                                        calendar.add(Calendar.MONTH, 1);
                                        break;
                                    case "yearlysub":
                                        calendar.add(Calendar.MONTH, 12);
                                        break;
                                    default:
                                        calendar.add(Calendar.MONTH, 0);
                                        break;
                                }


                                Date newDate = calendar.getTime();
                                String sixmonthsdate = format.format(calendar.getTime());

                                SimpleDateFormat formats = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                                String datelast = formats.format(calendar.getTime());

                                switch (CheckDates(mainTime, sixmonthsdate)) {
                                    case "before":
                                        scrollview.setVisibility(View.GONE);
                                        continues.setVisibility(View.GONE);
                                        vaildscrollview.setVisibility(View.VISIBLE);

                                        renewscrollview.setVisibility(View.GONE);
                                        spin_kit.setVisibility(View.GONE);

                                        expirydate.setText("" + datelast);

                                        sharedPreferences.edit().putBoolean("userIsPremium", true).apply();

                                        break;
                                    case "sameday":
                                        scrollview.setVisibility(View.GONE);
                                        continues.setVisibility(View.GONE);
                                        vaildscrollview.setVisibility(View.VISIBLE);
                                        expirydateend.setVisibility(View.VISIBLE);
                                        renewscrollview.setVisibility(View.GONE);

                                        expirydate.setText("" + datelast);


                                        sharedPreferences.edit().putBoolean("userIsPremium", true).apply();


                                        break;
                                    case "expired":
                                        renewscrollview.setVisibility(View.VISIBLE);
                                        spin_kit.setVisibility(View.GONE);
                                        scrollview.setVisibility(View.GONE);
                                        continues.setVisibility(View.GONE);
                                        vaildscrollview.setVisibility(View.GONE);
                                        continues.setVisibility(View.VISIBLE);

                                        if (!productId.isEmpty()) {
                                            subid = productId;
                                        } else {
                                            continues.setVisibility(View.GONE);
                                        }

                                        sharedPreferences.edit().putBoolean("userIsPremium", false).apply();

                                        break;
                                    default:
                                        vaildscrollview.setVisibility(View.GONE);
                                        continues.setVisibility(View.GONE);
                                        scrollview.setVisibility(View.VISIBLE);
                                        renewscrollview.setVisibility(View.GONE);

                                        sharedPreferences.edit().putBoolean("userIsPremium", false).apply();


                                        break;
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                });

            }
        });


        BillingClient billingClient = BillingClient.newBuilder(this)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build();

        continues.setOnClickListener(view -> {

            if (subid == null) {
                return;
            }
            if (subid.isEmpty()) {
                return;
            }
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NonNull BillingResult result) {
                    if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    }

                }

                @Override
                public void onBillingServiceDisconnected() {

                }
            });
        });


    }

    public String CheckDates(String startDate, String endDate) {

        SimpleDateFormat dfDate = new SimpleDateFormat("dd-MM-yyyy");

        String b = "";

        try {
            if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                b = "before";  // If start date is before end date.
            } else if (dfDate.parse(startDate).equals(dfDate.parse(endDate))) {
                b = "sameday";  // If two dates are equal.
            } else {
                b = "expired"; // If start date is after the end date.
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            finish();
            startActivity(getIntent());
        }

        return b;
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }

    }

    private void handlePurchase(Purchase purchase) {

        Log.d("TAG", "handlePurchase: " + purchase.getPurchaseToken());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}