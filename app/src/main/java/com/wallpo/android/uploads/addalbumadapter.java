package com.wallpo.android.uploads;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wallpo.android.R;
import com.wallpo.android.getset.Category;
import com.wallpo.android.utils.URLS;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.wallpo.android.profile.ProfileActivity.comebacktoprofile;

public class addalbumadapter extends RecyclerView.Adapter<addalbumadapter.ViewHolder> {
    private Context context;
    private List<Category> categoryList;

    public addalbumadapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_album_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Category category = categoryList.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("wallpo", Context.MODE_PRIVATE);

        String userid = sharedPreferences.getString("wallpouserid", "");

        holder.addalbum.setOnClickListener(view -> {

            final EditText input = new EditText(context);
            input.setHint(context.getResources().getString(R.string.enteralbumname));
            input.setInputType(InputType.TYPE_CLASS_TEXT);

            new MaterialAlertDialogBuilder(context)
                    .setTitle(context.getResources().getString(R.string.createnewalbum))
                    .setView(input)
                    .setNegativeButton(context.getResources().getString(R.string.cancelbig), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .setPositiveButton(context.getResources().getString(R.string.CREATE), (dialogInterface, i) -> {

                        if (input.getText().toString().isEmpty()) {
                            Toast.makeText(context, context.getResources().getString(R.string.enteralbumname), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        TransitionManager.beginDelayedTransition(holder.addalbum);
                        holder.img.setVisibility(View.GONE);
                        holder.loadingbar.setVisibility(View.VISIBLE);
                        holder.addalbum.setClickable(false);

                        String tz = TimeZone.getDefault().getID();

                        OkHttpClient client = new OkHttpClient();

                        RequestBody postData = new FormBody.Builder().add("userid", userid)
                                .add("albumcreated", tz).add("albumname", input.getText().toString().trim().replace("'", "\\'")).build();

                        Request request = new Request.Builder()
                                .url(URLS.addalbum)
                                .post(postData)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .build();


                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, context.getResources().getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();

                                        TransitionManager.beginDelayedTransition(holder.addalbum);
                                        holder.loadingbar.setVisibility(View.GONE);
                                        holder.img.setVisibility(View.VISIBLE);
                                        holder.addalbum.setClickable(true);

                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String data = response.body().string().replaceAll(",\\[]", "");
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!context.toString().contains("ProfileActivity")) {
                                            if (context.toString().contains("UploadVideoActivity")) {
                                                UploadVideoActivity.loadalbum(context);
                                            } else {
                                                UploadImageActivity.loadalbum(context);
                                            }
                                        }

                                        String useridprofile = sharedPreferences.getString("useridprofile", "");

                                        SharedPreferences sharedusersdata = context.getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);

                                        sharedusersdata.edit().putString(useridprofile + "albumsprofile", "").apply();

                                        TransitionManager.beginDelayedTransition(holder.addalbum);
                                        holder.loadingbar.setVisibility(View.GONE);
                                        holder.img.setVisibility(View.VISIBLE);
                                        holder.addalbum.setClickable(true);

                                        if (data.trim().contains("created")) {

                                            sharedusersdata.edit().putString(useridprofile + "albumsprofile", "").apply();
                                            Toast.makeText(context, context.getResources().getString(R.string.albumcreated), Toast.LENGTH_LONG).show();


                                            if (context.toString().contains("ProfileActivity")) {
                                                comebacktoprofile(context);
                                            }
                                        } else {

                                            Toast.makeText(context, context.getResources().getString(R.string.errorwhilecreatingalbum), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        });

                    })
                    .show();

        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView addalbum;
        SpinKitView loadingbar;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            addalbum = itemView.findViewById(R.id.addalbum);
            loadingbar = itemView.findViewById(R.id.loadingbar);
            loadingbar.setVisibility(View.GONE);
            img = itemView.findViewById(R.id.img);

        }
    }

}
