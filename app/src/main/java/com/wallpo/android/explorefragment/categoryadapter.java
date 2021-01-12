package com.wallpo.android.explorefragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wallpo.android.R;
import com.wallpo.android.getset.Category;
import com.wallpo.android.utils.Common;

import java.util.List;

import static android.content.ContentValues.TAG;

public class categoryadapter extends RecyclerView.Adapter<categoryadapter.ViewHolder> {
    private Context context;
    private List<Category> Categories;

    public categoryadapter(Context context, List<Category> Categories) {
        this.context = context;
        this.Categories = Categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.category_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Category category = Categories.get(position);

        try {

            Glide.with(context).load(category.getImagelink()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).into(holder.categoryimage);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        } catch (IllegalStateException e) {

            Log.e(TAG, "onBindViewHolder: ", e);
        }

        holder.name.setText(category.getName());

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        holder.card.setOnClickListener(view -> {
            Intent i = new Intent(context, ViewCategoryActivity.class);
            sharedPreferences.edit().putString("categoryname", category.getName()).apply();
            sharedPreferences.edit().putString("categoryid", category.getCategoryid()).apply();
            sharedPreferences.edit().putString("categoryimg", category.getImagelink()).apply();
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return Categories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryimage;
        TextView name;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryimage = itemView.findViewById(R.id.categoryimage);

            name = itemView.findViewById(R.id.name);

            card = itemView.findViewById(R.id.card);


        }
    }

}
