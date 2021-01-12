package com.wallpo.android.cropper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wallpo.android.R;

import java.io.File;
import java.io.IOException;


public class CropImageActivity extends AppCompatActivity
        implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener {

    private CropImageView mCropImageView;

    /** Persist URI image to crop URI if specific permissions are required */
    private Uri mCropImageUri;

    /** the options that were set for the crop image */
    private CropImageOptions mOptions;

    private ConstraintLayout footer;
    private FloatingActionButton fabCrop;
    private View ivRotateRight, ivRotateLeft, ivFlipHorizontal, ivFlipVertical, ivBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image_activity);

        mCropImageView   = findViewById(R.id.cropImageView);
        footer           = findViewById(R.id.footer);
        fabCrop          = findViewById(R.id.fab_crop);
        ivRotateRight    = findViewById(R.id.iv_rotate_right);
        ivRotateLeft     = findViewById(R.id.iv_rotate_left);
        ivFlipHorizontal = findViewById(R.id.iv_flip_horizontal);
        ivFlipVertical   = findViewById(R.id.iv_flip_vertical);
        ivBack           = findViewById(R.id.iv_back);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            footer.setElevation(24f);
        }

        Bundle bundle = getIntent().getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE);
        if (bundle != null) {
            mCropImageUri = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE);
            mOptions = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS);
        }

        if (savedInstanceState == null) {
            if (mCropImageUri == null || mCropImageUri.equals(Uri.EMPTY)) {
                if (CropImage.isExplicitCameraPermissionRequired(this)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(
                            new String[] {Manifest.permission.CAMERA},
                            CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    CropImage.startPickImageActivity(this);
                }
            } else if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                ActivityCompat.requestPermissions(
                        this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                mCropImageView.setImageUriAsync(mCropImageUri);
            }
        }


        if (mOptions != null && !mOptions.allowRotation) {
            ivRotateLeft.setVisibility(View.GONE);
            ivRotateRight.setVisibility(View.GONE);
        }

        if (mOptions != null && !mOptions.allowFlipping) {
            ivFlipVertical  .setVisibility(View.GONE);
            ivFlipHorizontal.setVisibility(View.GONE);
        }


        fabCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultCancel();
            }
        });


        fabCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        ivRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(mOptions.rotationDegrees);
            }
        });

        ivRotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(-mOptions.rotationDegrees);
            }
        });

        ivFlipHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCropImageView.flipImageHorizontally();
            }
        });

        ivFlipVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCropImageView.flipImageVertically();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnCropImageCompleteListener(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultCancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the picker. We don't have anything to crop
                setResultCancel();
            }

            if (resultCode == Activity.RESULT_OK) {
                mCropImageUri = CropImage.getPickImageResultUri(this, data);

                // For API >= 23 we need to check specifically that we have permissions to read external
                // storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    mCropImageView.setImageUriAsync(mCropImageUri);
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null
                    && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                mCropImageView.setImageUriAsync(mCropImageUri);
            } else {
                Toast.makeText(this, R.string.crop_image_activity_no_permissions, Toast.LENGTH_LONG).show();
                setResultCancel();
            }
        }

        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            // Irrespective of whether camera permission was given or not, we show the picker
            // The picker will not add the camera intent if permission is not available
            CropImage.startPickImageActivity(this);
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            if (mOptions.initialCropWindowRectangle != null) {
                mCropImageView.setCropRect(mOptions.initialCropWindowRectangle);
            }
            if (mOptions.initialRotation > -1) {
                mCropImageView.setRotatedDegrees(mOptions.initialRotation);
            }
        } else {
            setResult(null, error, 1);
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        setResult(result.getUri(), result.getError(), result.getSampleSize());
    }

    // region: Private methods

    /** Execute crop image and save the result tou output uri. */
    protected void cropImage() {
        if (mOptions.noOutputImage) {
            setResult(null, null, 1);
        } else {
            Uri outputUri = getOutputUri();
            mCropImageView.saveCroppedImageAsync(
                    outputUri,
                    mOptions.outputCompressFormat,
                    mOptions.outputCompressQuality,
                    mOptions.outputRequestWidth,
                    mOptions.outputRequestHeight,
                    mOptions.outputRequestSizeOptions);
        }
    }

    /** Rotate the image in the crop image view. */
    protected void rotateImage(int degrees) {
        mCropImageView.rotateImage(degrees);
    }

    /**
     * Get Android uri to save the cropped image into.<br>
     * Use the given in options or create a temp file.
     */
    protected Uri getOutputUri() {
        Uri outputUri = mOptions.outputUri;
        if (outputUri == null || outputUri.equals(Uri.EMPTY)) {
            try {
                String ext =
                        mOptions.outputCompressFormat == Bitmap.CompressFormat.JPEG
                                ? ".jpg"
                                : mOptions.outputCompressFormat == Bitmap.CompressFormat.PNG ? ".png" : ".webp";
                outputUri = Uri.fromFile(File.createTempFile("cropped", ext, getCacheDir()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to create temp file for output image", e);
            }
        }
        return outputUri;
    }

    /** Result with cropped image data or error if failed. */
    protected void setResult(Uri uri, Exception error, int sampleSize) {
        int resultCode = error == null ? RESULT_OK : CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE;
        setResult(resultCode, getResultIntent(uri, error, sampleSize));
        finish();
    }

    /** Cancel of cropping activity. */
    protected void setResultCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /** Get intent instance to be used for the result of this activity. */
    protected Intent getResultIntent(Uri uri, Exception error, int sampleSize) {
        CropImage.ActivityResult result =
                new CropImage.ActivityResult(
                        mCropImageView.getImageUri(),
                        uri,
                        error,
                        mCropImageView.getCropPoints(),
                        mCropImageView.getCropRect(),
                        mCropImageView.getRotatedDegrees(),
                        mCropImageView.getWholeImageRect(),
                        sampleSize);
        Intent intent = new Intent();
        intent.putExtras(getIntent());
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result);
        return intent;
    }

    /** Update the color of a specific menu item to the given color. */
    private void updateMenuItemIconColor(Menu menu, int itemId, int color) {
        MenuItem menuItem = menu.findItem(itemId);
        if (menuItem != null) {
            Drawable menuItemIcon = menuItem.getIcon();
            if (menuItemIcon != null) {
                try {
                    menuItemIcon.mutate();
                    menuItemIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    menuItem.setIcon(menuItemIcon);
                } catch (Exception e) {
                    Log.w("AIC", "Failed to update menu item color", e);
                }
            }
        }
    }
    // endregion
}
