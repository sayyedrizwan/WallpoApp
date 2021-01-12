package com.wallpo.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallpo.android.MainActivity;
import com.wallpo.android.R;
import com.wallpo.android.explorefragment.explorealbumadapter;
import com.wallpo.android.explorefragment.explorecateroryadapter;
import com.wallpo.android.explorefragment.explorenewartistsadapter;
import com.wallpo.android.explorefragment.explorepostsadapter;
import com.wallpo.android.explorefragment.explorestoriessadapter;
import com.wallpo.android.explorefragment.exploretopidsadapter;
import com.wallpo.android.extra.WallpaperSettingActivity;
import com.wallpo.android.getset.Stories;
import com.wallpo.android.utils.updatecode;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private List<Stories> albumList = new ArrayList<>();
    private List<Stories> categoryList = new ArrayList<>();
    private List<Stories> idList = new ArrayList<>();
    private List<Stories> postsList = new ArrayList<>();
    private List<Stories> postsList1 = new ArrayList<>();
    private List<Stories> postsList2 = new ArrayList<>();
    private List<Stories> storyList = new ArrayList<>();
    private List<Stories> newatistsList = new ArrayList<>();
    com.wallpo.android.explorefragment.explorenewartistsadapter explorenewartistsadapter;
    com.wallpo.android.explorefragment.explorealbumadapter explorealbumadapter;
    com.wallpo.android.explorefragment.exploretopidsadapter exploretopidsadapter;
    com.wallpo.android.explorefragment.explorecateroryadapter explorecateroryadapter;
    com.wallpo.android.explorefragment.explorepostsadapter explorepostsadapter;
    com.wallpo.android.explorefragment.explorepostsadapter explorepostsadapter1;
    com.wallpo.android.explorefragment.explorepostsadapter explorepostsadapter2;
    com.wallpo.android.explorefragment.explorestoriessadapter explorestoriessadapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ConcatAdapter merged;
    public static Boolean checkuser = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);


        updatecode.analyticsFirebase(getContext(), "explore_per_minute", "explore_per_minute");

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                updatecode.analyticsFirebase(getContext(), "explore_per_minute", "explore_per_minute");
                if (checkuser) {
                    handler.postDelayed(this, 60000);
                }
            }
        };
        handler.postDelayed(runnable, 60000);


        recyclerView = view.findViewById(R.id.recyclerview);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        explorecateroryadapter = new explorecateroryadapter(getActivity(), categoryList);
        explorealbumadapter = new explorealbumadapter(getActivity(), albumList);
        exploretopidsadapter = new exploretopidsadapter(getActivity(), idList);
        explorepostsadapter = new explorepostsadapter(getActivity(), postsList);
        explorepostsadapter1 = new explorepostsadapter(getActivity(), postsList1);
        explorepostsadapter2 = new explorepostsadapter(getActivity(), postsList2);
        explorenewartistsadapter = new explorenewartistsadapter(getActivity(), newatistsList);
        explorestoriessadapter = new explorestoriessadapter(getActivity(), storyList);

        merged = new ConcatAdapter(explorecateroryadapter, explorealbumadapter, explorenewartistsadapter, exploretopidsadapter, explorepostsadapter, explorestoriessadapter);
        recyclerView.setAdapter(merged);
        recyclerView.setHasFixedSize(true);

        Stories storys = new Stories(0, 0, "userid", "link", "caption",
                "type", "dateshowed", "datecreated");

        categoryList.clear();
        albumList.clear();
        idList.clear();
        postsList.clear();
        newatistsList.clear();
        storyList.clear();

        categoryList.add(storys);
        albumList.add(storys);
        idList.add(storys);
        postsList.add(storys);
        newatistsList.add(storys);
        storyList.add(storys);

        explorecateroryadapter.notifyDataSetChanged();
        explorealbumadapter.notifyDataSetChanged();
        exploretopidsadapter.notifyDataSetChanged();
        explorepostsadapter.notifyDataSetChanged();
        explorenewartistsadapter.notifyDataSetChanged();
        explorestoriessadapter.notifyDataSetChanged();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            MainActivity.home.setText(getResources().getString(R.string.home));
            MainActivity.trending.setText(getResources().getString(R.string.trending));
            MainActivity.profile.setText(getResources().getString(R.string.profile));
            MainActivity.explore.setText(getResources().getString(R.string.Explore));
            MainActivity.monetize.setText(getResources().getString(R.string.monetization));
            MainActivity.search.setText(getResources().getString(R.string.search));
            MainActivity.notification.setText(getResources().getString(R.string.notification));
        } catch (NullPointerException e) {
            Log.d("ProfileFragment", "onStart: ");
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        checkuser = false;
    }

}
