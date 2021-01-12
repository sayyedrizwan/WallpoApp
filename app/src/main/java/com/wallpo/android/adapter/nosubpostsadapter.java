package com.wallpo.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.wallpo.android.R;
import com.wallpo.android.explorefragment.exploretopidsadapter;
import com.wallpo.android.getset.Photos;
import com.wallpo.android.getset.Stories;
import com.wallpo.android.utils.customListeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class nosubpostsadapter extends RecyclerView.Adapter<nosubpostsadapter.ViewHolder> {
    private Context context;
    private List<Photos> photos;

    public nosubpostsadapter(Context context, List<Photos> photos) {
        this.context = context;
        this.photos = photos;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.no_subs_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Photos Photo = photos.get(position);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        holder.cardview.getLayoutParams().height = height / 2 / 2 * 3;
        holder.cardview.getLayoutParams().width = width - 160;
        holder.cardview.requestLayout();

        holder.relllay.getLayoutParams().width = width - 160;
        holder.relllay.requestLayout();

        holder.recyclerview.getLayoutParams().height = height / 2 / 2 * 3;
        holder.recyclerview.getLayoutParams().width = width - 160;
        holder.recyclerview.requestLayout();


        holder.redirecttoexplore.setOnClickListener(view -> {
            customListeners.listener.onObjectReady("explore");
        });

        holder.searchmore.setOnClickListener(view -> {
            customListeners.listener.onObjectReady("search");
        });

        final Handler ha = new Handler();
        ha.postDelayed(() -> holder.loadingbar.setVisibility(View.GONE), 2000);

        if (Photo.getPhotoid() == 1) {

            holder.idlay.setVisibility(View.VISIBLE);
            holder.bottomtext.setText(context.getResources().getString(R.string.trendingaccounts));
            holder.mainsearch.setVisibility(View.GONE);

            Stories storys = new Stories(0, 0, "userid", "link", "caption",
                    "type", "dateshowed", "datecreated");

            holder.idList.add(storys);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            holder.recyclerview.setLayoutManager(linearLayoutManager);
            holder.exploretopidsadapter = new exploretopidsadapter(context, holder.idList);
            holder.exploretopidsadapter.notifyDataSetChanged();
            holder.recyclerview.setAdapter(holder.exploretopidsadapter);

        } else {

            holder.bottomtext.setText(context.getResources().getString(R.string.justforyou));
            holder.mainsearch.setVisibility(View.VISIBLE);
            holder.idlay.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relllay, mainsearch, idlay;
        CardView cardview, redirecttoexplore, searchmore;
        List<Stories> idList = new ArrayList<>();
        RecyclerView recyclerview;
        TextView bottomtext;
        com.wallpo.android.explorefragment.exploretopidsadapter exploretopidsadapter;
        SpinKitView loadingbar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            redirecttoexplore = itemView.findViewById(R.id.redirecttoexplore);
            cardview = itemView.findViewById(R.id.cardview);
            searchmore = itemView.findViewById(R.id.searchmore);
            relllay = itemView.findViewById(R.id.relllay);
            mainsearch = itemView.findViewById(R.id.mainsearch);
            bottomtext = itemView.findViewById(R.id.bottomtext);
            recyclerview = itemView.findViewById(R.id.recyclerview);
            loadingbar = itemView.findViewById(R.id.loadingbar);
            idlay = itemView.findViewById(R.id.idlay);
            idlay.setVisibility(View.GONE);

        }
    }

    public String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getTimestamphour() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh", Locale.getDefault());
        return sdf.format(new Date());
    }


}
