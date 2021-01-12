package com.wallpo.android.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.adapter.notificationadapter;
import com.wallpo.android.database.NotificationDatabase;
import com.wallpo.android.roomdbs.Notificationdb;
import com.wallpo.android.roomdbs.Roomdb;
import com.wallpo.android.utils.updatecode;

import java.util.ArrayList;
import java.util.List;

import static com.wallpo.android.database.NotificationDatabase.COL_MAINUSERID;
import static com.wallpo.android.database.NotificationDatabase.NOTI_TABLE_NAME;

public class NotificationFragment extends Fragment {

    SQLiteDatabase mDatabase;
    NotificationDatabase databaseHelper;
    private RecyclerView recyclerview, recyclerviewsuggestion;
    com.wallpo.android.adapter.notificationadapter notificationadapter;
    TextView notice;
    RelativeLayout notilay;
    Roomdb database;
    List<Notificationdb> notificationdbList = new ArrayList<>();
    private int totalItemCount, firstVisibleItem, visibleItemCont;
    private int page = 1;
    private int loadno = 0;
    private int previousTotal;
    private boolean load = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);

        notilay = view.findViewById(R.id.notilay);
        notice = view.findViewById(R.id.notice);

        recyclerview = view.findViewById(R.id.recyclerview);

        database = Roomdb.getInstance(getActivity());

        notificationdbList = database.mainDao().getAllNotifications(0);

        updatecode.analyticsFirebase(getContext(), "notification_activity", "notification_activity");

        notificationadapter = new notificationadapter(getContext(), notificationdbList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerview.addItemDecoration(dividerItemDecoration);
        recyclerview.setAdapter(notificationadapter);

        if (notificationadapter.getItemCount() > 1){
            notice.setVisibility(View.GONE);
        }

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dx:" + dx + " dy:" + dy);
                visibleItemCont = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findLastVisibleItemPosition();


                if (dy > 0) {
                    if (load) {
                        if (totalItemCount > previousTotal) {
                            previousTotal = totalItemCount;
                            load = false;
                            page++;
                        }
                    }

                    if (!load && (firstVisibleItem + visibleItemCont) >= totalItemCount) {
                        loadno = loadno + 25;

                        notificationdbList = database.mainDao().getAllNotifications(loadno);

                        notificationadapter.notifyDataSetChanged();
                        load = true;
                    }
                }
            }

        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity.home.setText(getResources().getString(R.string.home));
        MainActivity.trending.setText(getResources().getString(R.string.trending));
        MainActivity.profile.setText(getResources().getString(R.string.profile));
        MainActivity.explore.setText(getResources().getString(R.string.explore));
        MainActivity.monetize.setText(getResources().getString(R.string.monetization));
        MainActivity.search.setText(getResources().getString(R.string.search));
        MainActivity.notification.setText(getResources().getString(R.string.Notification));
    }
}
