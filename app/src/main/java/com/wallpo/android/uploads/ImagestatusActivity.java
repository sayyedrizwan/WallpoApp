package com.wallpo.android.uploads;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wallpo.android.LoginActivity;
import com.wallpo.android.R;
import com.wallpo.android.cropper.CropImage;
import com.wallpo.android.cropper.CropImageView;
import com.wallpo.android.utils.Common;
import com.wallpo.android.utils.URLS;
import com.wallpo.android.utils.updatecode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImagestatusActivity extends AppCompatActivity {

    TextInputEditText textInputEditText;
    TextView textcount;
    Button sharebutton;
    Context context = this;
    Uri imageUri;
    private Bitmap bitmap;
    ImageView mainimg;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagestatus);

        textInputEditText = findViewById(R.id.textInputEditText);
        textcount = findViewById(R.id.textcount);
        sharebutton = findViewById(R.id.sharebutton);
        mainimg = findViewById(R.id.mainimg);

        SharedPreferences sharedPreferences = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("wallpouserid", "");

        if (userid.isEmpty()) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }

        Intent intent = getIntent();
        imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (imageUri != null) {

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setCropMenuCropButtonTitle("NEXT")
                    .setBackgroundColor(Color.argb(130, 177, 194, 222))
                    .start(this);

        } else {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setCropMenuCropButtonTitle("NEXT")
                    .setBackgroundColor(Color.argb(130, 177, 194, 222))
                    .start(this);

        }

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                textcount.setText(s.length() + "/140");
            }
        });

        sharebutton.setOnClickListener(v -> {
            final String tz = TimeZone.getDefault().getID();

            if (mainimg.getDrawable() == null) {
                Toast.makeText(context, context.getResources().getString(R.string.imagenotfound), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("UPLOAD NOTIFICATION", "Status Upload",
                        NotificationManager.IMPORTANCE_HIGH);

                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "UPLOAD NOTIFICATION");
            builder.setContentTitle(context.getResources().getString(R.string.uploadingimageonfirewall));
            builder.setSmallIcon(R.mipmap.logo);
            builder.setAutoCancel(true);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setProgress(100, 0, true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());

            finish();

            updatecode.analyticsFirebase(context,
                    "firewall_image_start_upload", "firewall_image_start_upload");

            SharedPreferences sharedPreferences1 = getSharedPreferences("wallpo", Context.MODE_PRIVATE);
            String userid1 = sharedPreferences1.getString("wallpouserid", "");

            StorageReference storageRef = storage.getReference();

            long time = System.currentTimeMillis();
            Random r = new Random();
            double random1 = r.nextInt(999 - 10) + 10;
            double random2 = r.nextInt(999 - 10) + 10;

            String uploadfile = "wallpo/" + userid1 + "/" + time + random1 + userid1 + random2;
            StorageReference imageupload = storageRef.child(uploadfile + ".jpeg");

            mainimg.setDrawingCacheEnabled(true);
            mainimg.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) mainimg.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imageupload.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageupload.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d("EditProfileActivity", "onComplete: " + downloadUri + " - " + downloadUri.getPath());

                        OkHttpClient client = new OkHttpClient();

                        RequestBody postData = new FormBody.Builder().add("userid", userid1)
                                .add("link", String.valueOf(downloadUri))
                                .add("caption", textInputEditText.getText().toString().trim().replace("'", "\\'"))
                                .add("timezone", tz).add("type", "image").build();

                        Request request = new Request.Builder()
                                .url(URLS.uploadstoriesimage)
                                .post(postData)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        StorageReference desertRef = storageRef.child(uploadfile);

                                        desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                                        builder.setContentTitle(context.getResources().getString(R.string.erroruploadingimageonfirewall))
                                                .setProgress(0, 0, false);
                                        notificationManager.notify(1, builder.build());
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String data = response.body().string().replaceAll(",\\[]", "");

                                runOnUiThread(() -> {

                                    if (data.trim().contains("successfully")) {
                                        builder.setContentTitle(context.getResources().getString(R.string.uploadingimageonfirewalldone))
                                                .setProgress(0, 0, false);


                                        SharedPreferences sharedusersdata = getSharedPreferences("wallpouserdata", Context.MODE_PRIVATE);
                                        sharedusersdata.edit().putString(userid + "firewallprofile", "").apply();
                                        Common.ALBUM_STATUS = "changed";

                                        updatecode.analyticsFirebase(context,
                                                "firewall_image_uploaded", "firewall_image_uploaded");

                                    } else {


                                        StorageReference desertRef = storageRef.child(uploadfile);

                                        desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                                        builder.setContentTitle(context.getResources().getString(R.string.erroruploadingimageonfirewall))
                                                .setProgress(0, 0, false);
                                    }
                                    notificationManager.notify(1, builder.build());
                                });
                            }
                        });


                    } else {

                        StorageReference desertRef = storageRef.child(uploadfile);

                        desertRef.delete().addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: ")).addOnFailureListener(exception -> Log.d("TAG", "onError: err"));

                        builder.setContentTitle(context.getResources().getString(R.string.erroruploadingimageonfirewall))
                                .setProgress(0, 0, false);
                        notificationManager.notify(1, builder.build());
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, context.getResources().getString(R.string.erroruploadingimageonfirewall), Toast.LENGTH_SHORT).show();
                }
            });


        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();

            File imagefile = new File(resultUri.getPath());

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                mainimg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            finish();
        }
    }


}