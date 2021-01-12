package com.wallpo.android.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wallpo.android.R;
import com.wallpo.android.adapter.usermessageadapter;
import com.wallpo.android.getset.ChatUsers;
import com.wallpo.android.utils.updatecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    com.wallpo.android.adapter.usermessageadapter usermessageadapter;
    Context context = this;
    SharedPreferences sharedPreferences;
    String userid;
    DatabaseReference reference;
    private List<String> userList = new ArrayList<>();
    // List<Chats> chatsList = new ArrayList<>();
    List<ChatUsers> chatsList = new ArrayList<>();
    public static String messagetype = "";
    public static String redirectto = "";
    SpinKitView loadingbar;
    TextView text, messagerequest, titletext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        text = findViewById(R.id.text);
        text.setVisibility(View.GONE);
        loadingbar = findViewById(R.id.loadingbar);
        messagerequest = findViewById(R.id.messagerequest);
        messagerequest.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recyclerview);
        titletext = findViewById(R.id.titletext);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        userid = sharedPreferences.getString("wallpouserid", "");

        updatecode.analyticsFirebase(context, "message_activity", "message_activity");

        MessageActivity.messagetype = "olduser";

        reference = FirebaseDatabase.getInstance().getReference("userschat").child(userid).child("newuser");

        reference.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    int count = Integer.parseInt(String.valueOf(snap.getChildrenCount())) / 3;
                    if (count > 1) {
                        messagerequest.setText(count + getResources().getString(R.string.newmsgrequest));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messagerequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chatsList.clear();
                titletext.setText(getResources().getString(R.string.msgrequest));
                loadingbar.setVisibility(View.VISIBLE);
                reference = FirebaseDatabase.getInstance().getReference("userschat").child(userid).child("newuser");
                reference.orderByChild("time").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatsList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ChatUsers chats = snapshot.getValue(ChatUsers.class);
                            chatsList.add(chats);
                        }
                        Collections.reverse(chatsList);

                        MessageActivity.messagetype = "newuser";
                        usermessageadapter = new usermessageadapter(context, chatsList);
                        recyclerView.setAdapter(usermessageadapter);

                        loadingbar.setVisibility(View.GONE);

                        text.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("userschat").child(userid).child("olduser");

        reference.orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatUsers chats = snapshot.getValue(ChatUsers.class);
                    chatsList.add(chats);
                }
                Collections.reverse(chatsList);
                usermessageadapter = new usermessageadapter(context, chatsList);
                recyclerView.setAdapter(usermessageadapter);

                loadingbar.setVisibility(View.GONE);

                if (usermessageadapter.getItemCount() < 1) {
                    text.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}