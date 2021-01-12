package com.wallpo.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.florent37.shapeofview.shapes.CircleView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wallpo.android.R;
import com.wallpo.android.adapter.chatadapter;
import com.wallpo.android.getset.ChatUsers;
import com.wallpo.android.getset.Chats;
import com.wallpo.android.profile.ProfileActivity;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.google.firebase.database.ServerValue.TIMESTAMP;
import static com.wallpo.android.activity.MessageActivity.messagetype;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    ImageView profileimg, notificationmenu, profileseenimg;
    String userid;
    String useridprofile;
    SharedPreferences sharedPreferences;
    AppCompatTextView displayname, username;
    TextInputEditText commenttext;
    TextView time;
    CardView sendmessage, back, cardview;
    com.wallpo.android.adapter.chatadapter chatadapter;
    List<Chats> chatsList = new ArrayList<>();
    RecyclerView recyclerview;
    RelativeLayout acceptbutton;
    TextView notice;
    String getType = "";
    String key = "";
    CircleView seenlayout;
    LinearLayout bottomlay;
    LinearLayoutManager linearLayoutManager;
    SpinKitView loadingbar, chatloading;
    private int totalItemCounts, firstVisibleItems, visibleItemConts;
    private int pages = 1;
    private int loadnos = 30;
    private int previousTotals;
    private boolean loads = true;
    String usernameprofile, displaynameprofile, profilephotoprofile, verifiedprofile, descriptionprofile, websitesprofile, categoryprofile, backphotoprofile,
            subscribedprofile, subscriberprofile;

    Boolean chattime = true;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        userid = sharedPreferences.getString("wallpouserid", "");

        usernameprofile = sharedPreferences.getString("usernameprofile", "");
        useridprofile = sharedPreferences.getString("useridprofile", "");
        displaynameprofile = sharedPreferences.getString("displaynameprofile", "");
        profilephotoprofile = sharedPreferences.getString("profilephotoprofile", "");
        verifiedprofile = sharedPreferences.getString("verifiedprofile", "");
        descriptionprofile = sharedPreferences.getString("descriptionprofile", "");
        websitesprofile = sharedPreferences.getString("websitesprofile", "");
        categoryprofile = sharedPreferences.getString("categoryprofile", "");
        backphotoprofile = sharedPreferences.getString("backphotoprofile", "");
        subscribedprofile = sharedPreferences.getString("subscribedprofile", "");
        subscriberprofile = sharedPreferences.getString("subscriberprofile", "");

        profileimg = findViewById(R.id.profileimg);
        displayname = findViewById(R.id.displayname);
        commenttext = findViewById(R.id.commenttext);
        sendmessage = findViewById(R.id.sendmessage);
        username = findViewById(R.id.username);
        back = findViewById(R.id.back);
        acceptbutton = findViewById(R.id.acceptbutton);
        acceptbutton.setVisibility(View.GONE);
        notice = findViewById(R.id.notice);
        notice.setVisibility(View.GONE);
        seenlayout = findViewById(R.id.seenlayout);
        seenlayout.setVisibility(View.GONE);
        time = findViewById(R.id.time);
        time.setVisibility(View.GONE);
        bottomlay = findViewById(R.id.bottomlay);
        bottomlay.setVisibility(View.GONE);
        notificationmenu = findViewById(R.id.notificationmenu);
        loadingbar = findViewById(R.id.loadingbar);
        profileseenimg = findViewById(R.id.profileseenimg);
        chatloading = findViewById(R.id.chatloading);
        chatloading.setVisibility(View.GONE);
        cardview = findViewById(R.id.cardview);

        if (userid.equals(useridprofile)) {
            finish();
        }
        //   messagetype = "olduser";
        //  messagetype = "newuser";

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (usernameprofile.isEmpty()) {
            loaddetail();
        }


        updatecode.analyticsFirebase(context, "chat_user_per_minute", "chat_user_per_minute");
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                updatecode.analyticsFirebase(context, "chat_user_per_minute", "chat_user_per_minute");
                if (chattime) {
                    handler.postDelayed(this, 60000);
                }
            }
        };
        handler.postDelayed(runnable, 60000);

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerview.setLayoutManager(linearLayoutManager);

        chatadapter = new chatadapter(ChatActivity.this, chatsList);
        recyclerview.setAdapter(chatadapter);
        seenview();
        loaduser();
        loadmessage(30);
        recyclerviewload();

        try {
            Glide.with(getApplicationContext()).load(profilephotoprofile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                    .placeholder(R.mipmap.profilepic)
                    .into(profileimg);

            Glide.with(getApplicationContext()).load(profilephotoprofile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                    .into(profileseenimg);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        displayname.setText(displaynameprofile);
        username.setText("@ " + usernameprofile);

        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, ProfileActivity.class);

                sharedPreferences.edit().putString("useridprofile", useridprofile).apply();
                sharedPreferences.edit().putString("usernameprofile", usernameprofile).apply();
                sharedPreferences.edit().putString("displaynameprofile", displaynameprofile).apply();
                sharedPreferences.edit().putString("profilephotoprofile", profilephotoprofile).apply();
                sharedPreferences.edit().putString("verifiedprofile", verifiedprofile).apply();
                sharedPreferences.edit().putString("descriptionprofile", descriptionprofile).apply();
                sharedPreferences.edit().putString("websitesprofile", websitesprofile).apply();
                sharedPreferences.edit().putString("categoryprofile", categoryprofile).apply();
                sharedPreferences.edit().putString("backphotoprofile", backphotoprofile).apply();
                sharedPreferences.edit().putString("subscribedprofile", subscribedprofile).apply();
                sharedPreferences.edit().putString("subscriberprofile", subscriberprofile).apply();

                startActivity(i);
            }
        });

        notificationmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ChatActivity.this, notificationmenu);
                popupMenu.getMenuInflater().inflate(R.menu.chat_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mute:

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("userschat").child(userid).child("olduser").child(useridprofile).removeValue();

                                DatabaseReference references = FirebaseDatabase.getInstance().getReference();
                                HashMap<String, Object> hashMaps = new HashMap<>();
                                hashMaps.put("chatuser", useridprofile);
                                hashMaps.put("status", "unseen");
                                hashMaps.put("time", TIMESTAMP);

                                references.child("userschat/" + userid + "/newuser/" + useridprofile).setValue(hashMaps);

                                finish();
                                Intent i = new Intent(getIntent());
                                startActivity(i);

                                break;
                            case R.id.clearchat:

                                new MaterialAlertDialogBuilder(context)
                                        .setTitle(getResources().getString(R.string.deleteallmsg))
                                        .setMessage(getResources().getString(R.string.deleteallmsgnotice))
                                        .setNeutralButton(getResources().getString(R.string.cancelbig), (dialogInterface, ii) -> {
                                            dialogInterface.dismiss();
                                        })
                                        .setPositiveButton(getResources().getString(R.string.deletebig), (dialogInterface, ii) -> {

                                            dialogInterface.dismiss();

                                            String value = "";
                                            try {
                                                if (Integer.parseInt(userid) > Integer.parseInt(useridprofile)) {
                                                    value = userid + "_" + useridprofile;
                                                } else {
                                                    value = useridprofile + "_" + userid;
                                                }
                                            } catch (NumberFormatException e) {
                                                finish();
                                                Toast.makeText(ChatActivity.this, getResources().getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                                            }


                                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                                            //   ref.child("userschat").child(userid).child(messagetype).child(useridprofile).removeValue();
                                            Query deleteqery = ref1.child("chats").child(value)
                                                    .orderByChild("sender").equalTo(userid);

                                            deleteqery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot deletesnp : dataSnapshot.getChildren()) {
                                                        deletesnp.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.e(TAG, "onCancelled", databaseError.toException());
                                                }
                                            });
                                        })
                                        .show();


                                break;
                            case R.id.makeshortcut:
                                DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                                refs.child("userschat").child(userid).child("newuser").child(useridprofile).removeValue();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("chatuser", useridprofile);
                                hashMap.put("status", "unseen");
                                hashMap.put("time", TIMESTAMP);

                                refs.child("userschat/" + userid + "/olduser/" + useridprofile).setValue(hashMap);

                                messagetype = "newuser";
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        messagetype = "olduser";
                                    }
                                }, 2000);
                                loaduser();


                                break;
                        }

                        return true;
                    }
                });
                popupMenu.show();

            }
        });

        sendmessage.setOnClickListener(v -> {

            if (commenttext.getText().toString().isEmpty()) {
                Toast.makeText(ChatActivity.this, getResources().getString(R.string.entermessage), Toast.LENGTH_SHORT).show();
                return;
            }

            String value = "";
            try {
                if (Integer.parseInt(userid) > Integer.parseInt(useridprofile)) {
                    value = userid + "_" + useridprofile;
                } else {
                    value = useridprofile + "_" + userid;
                }
            } catch (NumberFormatException e) {
                finish();
                Toast.makeText(ChatActivity.this, getResources().getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
            }

            time.setVisibility(View.GONE);
            seenlayout.setVisibility(View.GONE);

            updatecode.checkchatgroup(ChatActivity.this);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", userid);
            hashMap.put("reciver", useridprofile);
            hashMap.put("message", commenttext.getText().toString().trim());
            hashMap.put("time", TIMESTAMP);
            hashMap.put("share", "");

            commenttext.setText("");
            double random1 = Math.random() * (999 - 10 + 1) + 10;
            double random2 = Math.random() * (9999 - 10 + 1) + 10;

            String valuestamp = String.valueOf(random1).replace(".", "") + System.currentTimeMillis()
                    + String.valueOf(random2).replace(".", "");

            Log.d(TAG, "onClick: dygdd " + valuestamp);


            reference.child("chats").child(value).child(valuestamp).setValue(hashMap);

            DatabaseReference referencesq = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> hashMaps = new HashMap<>();
            hashMaps.put("chatuser", useridprofile);
            hashMaps.put("status", "unseen");
            hashMaps.put("time", TIMESTAMP);

            referencesq.child("userschat/" + userid + "/olduser/" + useridprofile).setValue(hashMaps);


        });

    }

    private void loaddetail() {
        final SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("wallpouserid", "");

        final SharedPreferences wallpouserdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

        String useridprofilemain = sharedPreferences.getString("useridprofile", "");

        String useridprofile = wallpouserdata.getString("useridprofile", "");

        String datauserid = wallpouserdata.getString(useridprofile + "id", "");

        if (!datauserid.isEmpty()) {

            usernameprofile = wallpouserdata.getString(useridprofile + "username", "");
            displaynameprofile = wallpouserdata.getString(useridprofile + "displayname", "");
            profilephotoprofile = wallpouserdata.getString(useridprofile + "profilephoto", "");

            verifiedprofile = wallpouserdata.getString(useridprofile + "verified", "");
            descriptionprofile = wallpouserdata.getString(useridprofile + "description", "");
            categoryprofile = wallpouserdata.getString(useridprofile + "category", "");
            websitesprofile = wallpouserdata.getString(useridprofile + "websites", "");
            backphotoprofile = wallpouserdata.getString(useridprofile + "backphoto", "");
            subscribedprofile = wallpouserdata.getString(useridprofile + "subscribed", "");
            subscriberprofile = wallpouserdata.getString(useridprofile + "subscribers", "");


            try {

                Glide.with(this).load(profilephotoprofile).centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.profilepic)
                        .apply(new RequestOptions().override(290, 290))
                        .skipMemoryCache(false).into(profileimg);

                Glide.with(this).load(profilephotoprofile).centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.profilepic)
                        .apply(new RequestOptions().override(290, 290))
                        .skipMemoryCache(false).into(profileseenimg);

            } catch (IllegalArgumentException e) {
                Log.e("searchadapter", "onBindViewHolder: ", e);
            } catch (IllegalStateException e) {

                Log.e("act", "onBindViewHolder: ", e);
            }

            username.setText("@ " + usernameprofile);
            displayname.setText(displaynameprofile);


        } else {

            final OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("userid", String.valueOf(useridprofilemain)).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URLS.userinfo)
                    .post(postData)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    Log.e(TAG, "onFailure: ", e);

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String data = response.body().string().replaceAll(",\\[]", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray array = new JSONArray(data);

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    usernameprofile = object.getString("username").trim();
                                    displaynameprofile = object.getString("displayname").trim();
                                    profilephotoprofile = object.getString("profilephoto").trim();

                                    verifiedprofile = object.getString("verified").trim();
                                    descriptionprofile = object.getString("description").trim();
                                    categoryprofile = object.getString("category").trim();
                                    websitesprofile = object.getString("websites").trim();
                                    backphotoprofile = object.getString("backphoto").trim();
                                    subscribedprofile = object.getString("subscribed").trim();
                                    subscriberprofile = object.getString("subscribers").trim();


                                    try {

                                        Glide.with(ChatActivity.this).load(profilephotoprofile).centerInside()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .placeholder(R.mipmap.profilepic)
                                                .apply(new RequestOptions().override(290, 290))
                                                .skipMemoryCache(false).into(profileimg);


                                        Glide.with(ChatActivity.this).load(profilephotoprofile).centerInside()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .placeholder(R.mipmap.profilepic)
                                                .apply(new RequestOptions().override(290, 290))
                                                .skipMemoryCache(false).into(profileseenimg);

                                    } catch (IllegalArgumentException e) {
                                        Log.e("searchadapter", "onBindViewHolder: ", e);
                                    } catch (IllegalStateException e) {

                                        Log.e("act", "onBindViewHolder: ", e);
                                    }
                                    displayname.setText(displaynameprofile);
                                    username.setText("@ " + usernameprofile);

                                    wallpouserdata.edit().putString(useridprofile + "id", useridprofilemain).apply();
                                    wallpouserdata.edit().putString(useridprofile + "username", usernameprofile).apply();
                                    wallpouserdata.edit().putString(useridprofile + "displayname", displaynameprofile).apply();
                                    wallpouserdata.edit().putString(useridprofile + "profilephoto", profilephotoprofile).apply();

                                    wallpouserdata.edit().putString(useridprofile + "verified", verifiedprofile).apply();
                                    wallpouserdata.edit().putString(useridprofile + "description", descriptionprofile).apply();
                                    wallpouserdata.edit().putString(useridprofile + "category", categoryprofile).apply();
                                    wallpouserdata.edit().putString(useridprofile + "websites", websitesprofile).apply();
                                    wallpouserdata.edit().putString(useridprofile + "backphoto", backphotoprofile).apply();
                                    wallpouserdata.edit().putString(useridprofile + "subscribed", subscribedprofile).apply();
                                    wallpouserdata.edit().putString(useridprofile + "subscribers", subscriberprofile).apply();


                                }

                            } catch (
                                    JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });
        }

    }

    private void recyclerviewload() {

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemConts = linearLayoutManager.getChildCount();
                totalItemCounts = linearLayoutManager.getItemCount();
                firstVisibleItems = linearLayoutManager.findLastVisibleItemPosition();

                if (dy < 0) {
                    if (loads) {
                        if (totalItemCounts > previousTotals) {
                            previousTotals = totalItemCounts;
                            loads = false;
                        }
                    }

                    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        if (chatadapter.getItemCount() > 27) {
                            chatloading.setVisibility(View.VISIBLE);
                            loadnos = loadnos + 30;
                            loadmessage(loadnos);
                            loads = true;
                        }
                    }

                }
            }

        });

    }

    private void seenview() {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("userschat").child(userid)
                .child(messagetype).child(useridprofile);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String status = dataSnapshot.child("status").getValue(String.class);
                    long timestamp = dataSnapshot.child("time").getValue(Long.class);
                    if (status.equals("seen")) {
                        seenlayout.setVisibility(View.VISIBLE);
                        time.setVisibility(View.VISIBLE);

                        if (updatecode.getTimestampday().contains(updatecode.getDate(timestamp, "dd"))) {
                            time.setText(updatecode.getDate(timestamp, "hh:mm"));
                        } else {
                            time.setText(updatecode.getDate(timestamp, "dd/MM/yyyy hh:mm"));
                        }

                    } else {
                        time.setVisibility(View.GONE);
                        seenlayout.setVisibility(View.GONE);
                    }
                } catch (NullPointerException e) {
                    time.setVisibility(View.GONE);
                    seenlayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadmessage(int limit) {
        String value = "";
        try {
            if (Integer.parseInt(userid) > Integer.parseInt(useridprofile)) {
                value = userid + "_" + useridprofile;
            } else {
                value = useridprofile + "_" + userid;
            }
        } catch (NumberFormatException e) {
            finish();
            Toast.makeText(ChatActivity.this, getResources().getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
        }

        Query reference;
        reference = FirebaseDatabase.getInstance().getReference("chats").child(value).limitToLast(limit);
        reference.orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);
                    if (chats.getReciver().equals(userid) &&
                            chats.getSender().equals(useridprofile) ||
                            chats.getReciver().equals(useridprofile)
                                    &&
                                    chats.getSender().equals(userid)) {

                        chatsList.add(chats);
                    }

                    chatadapter.notifyDataSetChanged();
                    if (limit < 35) {
                        recyclerview.scrollToPosition(chatsList.size() - 1);
                    } else {
                        int lik = limit - 30;
                        recyclerview.scrollToPosition(chatsList.size() - lik);
                    }
                }
                //time.setVisibility(View.GONE);
                //  seenlayout.setVisibility(View.GONE);
                chatloading.setVisibility(View.GONE);
                loadingbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loaduser() {

        if (messagetype.equals("newuser")) {

            bottomlay.setVisibility(View.VISIBLE);
            DatabaseReference reference;
            reference = FirebaseDatabase.getInstance().getReference("userschat").child(userid).child("newuser");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatUsers chats = snapshot.getValue(ChatUsers.class);

                        messagetype = "olduser";
                        if (chats.getChatuser().equals(useridprofile)) {
                            acceptbutton.setVisibility(View.VISIBLE);
                            notice.setVisibility(View.VISIBLE);
                            messagetype = "newuser";
                            bottomlay.setVisibility(View.GONE);
                            acceptbutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    acceptbutton.setVisibility(View.GONE);
                                    notice.setVisibility(View.GONE);
                                    bottomlay.setVisibility(View.VISIBLE);
                                    notificationmenu.setVisibility(View.VISIBLE);
                                    adddata();
                                    messagetype = "olduser";
                                }
                            });
                        } else {
                            bottomlay.setVisibility(View.VISIBLE);
                            messagetype = "olduser";
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            messagetype = "olduser";

            bottomlay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        chattime = false;
    }

    private void adddata() {


        new Handler().postDelayed(() -> {

            if (profileseenimg.getVisibility() == View.GONE){
                if (chatsList.toString().contains(", sender='" + useridprofile + "',")){


                    final OkHttpClient client = new OkHttpClient();

                    RequestBody postData = new FormBody.Builder().add("mainuserid", useridprofile)
                            .add("userid", userid)
                            .build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(URLS.sendmsgfcm)
                            .post(postData)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                        }
                    });

                }
            }

        }, 5000);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("chatuser", useridprofile);
        hashMap.put("status", "seen");
        hashMap.put("time", TIMESTAMP);

        reference.child("userschat/" + userid + "/olduser/" + useridprofile).setValue(hashMap);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query deleteqery = ref.child("userschat").child(userid).child("newuser").orderByChild("chatuser").equalTo(useridprofile);

        deleteqery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot deletesnp : dataSnapshot.getChildren()) {
                    deletesnp.getRef().removeValue();
                    loadmessage(30);
                    loaduser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        messagetype = "olduser";

    }


}