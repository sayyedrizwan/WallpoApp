package com.wallpo.android.subscription;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wallpo.android.R;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PremiumActivity1 extends AppCompatActivity implements PurchasesUpdatedListener {

    NestedScrollView renewscrollview, vaildscrollview, scrollview;
    RelativeLayout continues, defualtlay, onemonthslay, oneyearslay;
    SpinKitView spin_kit;
    AppCompatTextView sixprice, oneprice, oneyrsprice, expirydate, expirydateend, redeemcoupon;
    Context context = this;
    SharedPreferences sharedPreferences;
    String subid = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        renewscrollview = findViewById(R.id.renewscrollview);
        renewscrollview.setVisibility(View.GONE);

        vaildscrollview = findViewById(R.id.vaildscrollview);
        vaildscrollview.setVisibility(View.GONE);

        scrollview = findViewById(R.id.scrollview);
        scrollview.setVisibility(View.GONE);

        continues = findViewById(R.id.continues);
        continues.setVisibility(View.GONE);

        spin_kit = findViewById(R.id.spin_kit);
        sixprice = findViewById(R.id.sixprice);
        oneprice = findViewById(R.id.oneprice);
        oneyrsprice = findViewById(R.id.oneyrsprice);
        expirydate = findViewById(R.id.expirydate);

        expirydateend = findViewById(R.id.expirydateend);
        expirydateend.setVisibility(View.GONE);

        defualtlay = findViewById(R.id.defualtlay);
        onemonthslay = findViewById(R.id.onemonthslay);
        oneyearslay = findViewById(R.id.oneyearslay);
        redeemcoupon = findViewById(R.id.redeemcoupon);

        redeemcoupon.setOnClickListener(view -> {

            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.coupondialog_layout);

            AppCompatEditText coupontext = dialog.findViewById(R.id.appCompatEditText);
            RelativeLayout redeembtn = dialog.findViewById(R.id.redeembtn);

            redeembtn.setOnClickListener(view1 -> {
                String code = coupontext.getText().toString();
                try {
                    String url = "https://play.google.com/redeem?code=" + URLEncoder.encode(code, "UTF-8");
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (android.content.ActivityNotFoundException e) {
                    // Play Store app is not installed
                    Toast.makeText(context, "Play Store not found on you device, Visit https://play.google.com/redeem and Enter the code.", Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            dialog.setOnDismissListener(dialogInterface -> {
                finish();
                Intent i = new Intent(getIntent());
                startActivity(i);
            });


            dialog.show();


        });

        sharedPreferences = context.getSharedPreferences("wallpopremium", Context.MODE_PRIVATE);

        defualtlay.setOnClickListener(view -> {

            defualtlay.setBackground(context.getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            onemonthslay.setBackground(context.getResources().getDrawable(R.drawable.roundwallpapersetting));

            oneyearslay.setBackground(context.getResources().getDrawable(R.drawable.roundwallpapersetting));

            subid = "sixmonthssub";

            continues.setVisibility(View.VISIBLE);
        });

        onemonthslay.setOnClickListener(view -> {

            defualtlay.setBackground(context.getResources().getDrawable(R.drawable.roundwallpapersetting));

            onemonthslay.setBackground(context.getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            oneyearslay.setBackground(context.getResources().getDrawable(R.drawable.roundwallpapersetting));

            subid = "onemonthssubscription";

            continues.setVisibility(View.VISIBLE);

        });

        oneyearslay.setOnClickListener(view -> {

            defualtlay.setBackground(context.getResources().getDrawable(R.drawable.roundwallpapersetting));

            onemonthslay.setBackground(context.getResources().getDrawable(R.drawable.roundwallpapersetting));

            oneyearslay.setBackground(context.getResources().getDrawable(R.drawable.roundwallpapersettingmark));

            subid = "yearlysub";

            continues.setVisibility(View.VISIBLE);

        });

        SharedPreferences sharedPreferencess = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String id = sharedPreferencess.getString("wallpouserid", "");

        PurchasesUpdatedListener purchaseUpdateListener = (billingResult, list) -> {

            try{
                for (Purchase purchase : list) {

                    String orderId = purchase.getOrderId();
                    final String productId = purchase.getSku();
                    final String purchaseTime = String.valueOf(purchase.getPurchaseTime());
                    final String purchaseToken = purchase.getPurchaseToken();

                    if (!orderId.isEmpty()) {

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

                                    spin_kit.setVisibility(View.VISIBLE);

                                    finish();
                                    Intent i = new Intent(getIntent());
                                    startActivity(i);

                                    if (data.contains("successfully")) {
                                        Log.d("TAG", "onResponse: done");
                                    } else {

                                        Log.d("TAG", "onResponse: error");
                                    }
                                });

                            }
                        });
                    }
                }
            }catch (NullPointerException e){
                Log.d("TAG", "onCreate: " + e);
            }

        };

        BillingClient billingClient = BillingClient.newBuilder(this)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    spin_kit.setVisibility(View.GONE);

                    List<Purchase> billingClient1 = billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();

                    sharedPreferences.edit().putBoolean("userIsPremium", false).apply();

                    for (Purchase purchaseHistoryRecord : billingClient1) {

                        String time = String.valueOf(purchaseHistoryRecord.getPurchaseTime());
                        String productId = purchaseHistoryRecord.getSku();

                        if (!time.isEmpty()) {
                            sharedPreferences.edit().putBoolean("userIsPremium", true).apply();
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = format.parse(updatecode.getDate(Long.parseLong(time), "dd-MM-yyyy"));
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

                                expirydate.setText("" + datelast);

                            } catch (ParseException e) {
                                Log.d("TAG", "onBillingSetupFinished: e");
                            }

                        }
                    }

                    billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, (billingResult1, list) -> {

                        for (PurchaseHistoryRecord purchaseHistoryRecord : list) {

                            String sku = purchaseHistoryRecord.getSku();

                        }
                    });

                    continues.setOnClickListener(view -> {
                        List<String> skuList = new ArrayList<>();
                        skuList.add(subid);
                        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
                        billingClient.querySkuDetailsAsync(params.build(),
                                (billingResult1, skuDetailsList) -> {
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetailsList.get(0))
                                            .build();
                                    int responseCode = billingClient.launchBillingFlow(PremiumActivity1.this, billingFlowParams).getResponseCode();


                                });

                        Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.SUBS);

                    });


                    scrollview.setVisibility(View.VISIBLE);

                    List<String> skuList = new ArrayList<>();
                    skuList.add("sixmonthssub");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

                    billingClient.querySkuDetailsAsync(params.build(), (billingResult13, list) -> {
                        for (SkuDetails skuDetails : list) {

                            String sku = skuDetails.getSku();
                            String price = skuDetails.getPrice();
                            String pricecode = skuDetails.getPriceCurrencyCode();
                            String period = skuDetails.getSubscriptionPeriod();

                            sixprice.setText(price);

                        }
                    });

                    List<String> yearlysubList = new ArrayList<>();
                    yearlysubList.add("yearlysub");
                    SkuDetailsParams.Builder yearlyparams = SkuDetailsParams.newBuilder();
                    yearlyparams.setSkusList(yearlysubList).setType(BillingClient.SkuType.SUBS);

                    billingClient.querySkuDetailsAsync(yearlyparams.build(), (billingResult1, list) -> {
                        for (SkuDetails skuDetails : list) {

                            String sku = skuDetails.getSku();
                            String price = skuDetails.getPrice();
                            String pricecode = skuDetails.getPriceCurrencyCode();
                            String period = skuDetails.getSubscriptionPeriod();

                            oneyrsprice.setText(price);

                        }
                    });

                    List<String> onemonthsList = new ArrayList<>();
                    onemonthsList.add("onemonthssubscription");
                    SkuDetailsParams.Builder onemonthparams = SkuDetailsParams.newBuilder();
                    onemonthparams.setSkusList(onemonthsList).setType(BillingClient.SkuType.SUBS);

                    billingClient.querySkuDetailsAsync(onemonthparams.build(), (billingResult12, list) -> {
                        for (SkuDetails skuDetails : list) {

                            String sku = skuDetails.getSku();
                            String price = skuDetails.getPrice();
                            String pricecode = skuDetails.getPriceCurrencyCode();
                            String period = skuDetails.getSubscriptionPeriod();

                            oneprice.setText(price);
                        }
                    });

                    if (sharedPreferences.getBoolean("userIsPremium", false)) {
                        scrollview.setVisibility(View.GONE);
                        continues.setVisibility(View.GONE);
                        vaildscrollview.setVisibility(View.VISIBLE);

                    }

                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });

    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}
