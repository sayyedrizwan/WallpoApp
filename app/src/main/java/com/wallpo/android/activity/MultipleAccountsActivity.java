package com.wallpo.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallpo.android.LoginActivity;
import com.wallpo.android.R;
import com.wallpo.android.adapter.useraccountsadapter;
import com.wallpo.android.getset.User;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.updatecode;

import java.util.ArrayList;
import java.util.List;

public class MultipleAccountsActivity extends AppCompatActivity {

    com.wallpo.android.adapter.useraccountsadapter useraccountsadapter;
    RecyclerView recyclerview;
    CardView addaccount;
    Context context = this;
    private List<User> userslist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_accounts);

        SharedPreferences sharedPreferences = getSharedPreferences("wallpoaccounts", Context.MODE_PRIVATE);

        recyclerview = findViewById(R.id.recyclerview);
        addaccount = findViewById(R.id.addaccount);

        addaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.logintype = "";
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        String accounts = sharedPreferences.getString("accounts", "").replace(" ", "");


        updatecode.analyticsFirebase(context, "multiple_accounts_activity", "multiple_accounts_activity");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MultipleAccountsActivity.this);
        recyclerview.setLayoutManager(linearLayoutManager);
        useraccountsadapter = new useraccountsadapter(this, userslist);
        recyclerview.setAdapter(useraccountsadapter);

        if (!accounts.equals("")) {

            String[] acc = accounts.split(",");
            for (String account : acc) {
                String id = account.trim();

                User user = new User(id);
                userslist.add(user);

                useraccountsadapter.notifyDataSetChanged();
            }


        }

    }
}