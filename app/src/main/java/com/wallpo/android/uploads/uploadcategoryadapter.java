package com.wallpo.android.uploads;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.wallpo.android.R;
import com.wallpo.android.getset.Category;
import com.wallpo.android.utils.Common;

import java.util.List;

import static android.content.ContentValues.TAG;

public class uploadcategoryadapter extends RecyclerView.Adapter<uploadcategoryadapter.ViewHolder> {
    private Context context;
    private List<Category> categoryList;

    public uploadcategoryadapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.category_uploads, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Category category = categoryList.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        try {
            Glide.with(context).load(category.getImagelink())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                    .into(holder.imgview);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        holder.name.setText(category.getName());

        if (category.getType().equals("category")) {

            if (Common.categorydata.contains("-" + category.getCategoryid() + "-")) {

                holder.mark.setVisibility(View.VISIBLE);
            } else {

                holder.mark.setVisibility(View.GONE);

            }

        } else {

            if (Common.albumdata.contains("-" + category.getCategoryid() + "-")) {

                holder.mark.setVisibility(View.VISIBLE);
            } else {

                holder.mark.setVisibility(View.GONE);

            }
        }

        holder.mainlay.setOnClickListener(view -> {
            if (category.getType().equals("category")) {


                if (Common.categorydata.contains("-" + category.getCategoryid() + "-")) {

                    Common.categorydata.remove("-" + category.getCategoryid() + "-");

                    TransitionManager.beginDelayedTransition(holder.mainlay);

                    holder.mark.setVisibility(View.GONE);
                } else {

                    if (Common.categorydata.size() > 2){
                        Snackbar.make(view, context.getResources().getString(R.string.cannotselectmorecategory), Snackbar.LENGTH_LONG).show();
                        Snackbar.make(view, context.getResources().getString(R.string.cannotselectmorecategory), Snackbar.LENGTH_LONG).show();
                        return;
                    }


                    Common.categorydata.add("-" + category.getCategoryid() + "-");

                    TransitionManager.beginDelayedTransition(holder.mainlay);

                    holder.mark.setVisibility(View.VISIBLE);


                }

            } else {

                if (Common.albumdata.contains("-" + category.getCategoryid() + "-")) {

                    Common.albumdata.remove("-" + category.getCategoryid() + "-");

                    TransitionManager.beginDelayedTransition(holder.mainlay);

                    holder.mark.setVisibility(View.GONE);

                } else {

                    Common.albumdata.add("-" + category.getCategoryid() + "-");

                    TransitionManager.beginDelayedTransition(holder.mainlay);

                    holder.mark.setVisibility(View.VISIBLE);

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgview;
        AppCompatTextView name;
        RelativeLayout mark;
        CardView mainlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgview = itemView.findViewById(R.id.imgview);
            name = itemView.findViewById(R.id.name);
            mark = itemView.findViewById(R.id.mark);
            mark.setVisibility(View.GONE);
            mainlay = itemView.findViewById(R.id.mainlay);

        }
    }

}
