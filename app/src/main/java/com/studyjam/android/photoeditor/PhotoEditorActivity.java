package com.studyjam.android.photoeditor;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.studyjam.android.photoeditor.utils.ImageFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoEditorActivity extends AppCompatActivity implements View.OnClickListener,
        PopupMenu.OnMenuItemClickListener {

    private static final int SELECT_FILE = 100;
    private static final int REQUEST_CAMERA = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final int MAX_ANGLE = 360;
    private static final int MIN_ANGLE = 0;
    private ImageView mCanvasImageView;
    private String mUserChoosenTask;
    private View mEmptyPhotoViewContainer;
    private View mPhotoOptionsContainer;
    private boolean mImageChoosen;
    private int mCWRotationAngle;
    private static final String MIME_TYPE = "image/*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initialiseViews();
    }

    /**
     * Initialises the views
     */
    private void initialiseViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.photo_editor);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                if (mImageChoosen) {
                    showAlertOnNewPic();
                } else {
                    showImageSourceDialog();
                }
            }
        });
        mCanvasImageView = (ImageView) findViewById(R.id.edit_image);
        mEmptyPhotoViewContainer = findViewById(R.id.emtpy_photo_view_container);
        mPhotoOptionsContainer = findViewById(R.id.photo_edit_options_container);

        mEmptyPhotoViewContainer.setOnClickListener(this);
        findViewById(R.id.image_effects).setOnClickListener(this);
        findViewById(R.id.rotate_acw).setOnClickListener(this);
        findViewById(R.id.rotate_cw).setOnClickListener(this);
    }

    /**
     * Displays the alert dialog when there are some changes left without saving
     */
    private void showAlertOnNewPic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView titleView = (TextView) getLayoutInflater().inflate(R.layout.custom_dialog_title_view, null);
        titleView.setText(R.string.alert);
        builder.setCustomTitle(titleView);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showImageSourceDialog();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage(R.string.new_image_confirmation);
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emtpy_photo_view_container:
                showImageSourceDialog();
                break;

            case R.id.image_effects:
                PopupMenu popupMenu = new PopupMenu(this, v);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.image_effects_popup_menu);
                popupMenu.show();
                break;

            case R.id.rotate_acw:
                mCWRotationAngle = 360 - mCWRotationAngle - 90;
                if (mCWRotationAngle < MIN_ANGLE) {
                    mCWRotationAngle = 0;
                }
                mCanvasImageView.setRotation(mCWRotationAngle);
                break;

            case R.id.rotate_cw:
                mCWRotationAngle += 90;
                if (mCWRotationAngle > MAX_ANGLE) {
                    mCWRotationAngle = 0;
                }
                mCanvasImageView.setRotation(mCWRotationAngle);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        BitmapDrawable abmp = (BitmapDrawable) mCanvasImageView.getDrawable();
        boolean handled = true;
        Bitmap outputBmp = null;
        ImageFilter imageFilter = new ImageFilter();

        switch (item.getItemId()) {
            case R.id.image_effect_highlight:
                outputBmp = imageFilter.applyHighlightEffect(abmp.getBitmap());
                break;

            case R.id.image_effect_invert:
                outputBmp = imageFilter.applyInvertEffect(abmp.getBitmap());
                break;

            case R.id.image_effect_gray:
                outputBmp = imageFilter.applyGreyscaleEffect(abmp.getBitmap());
                break;

            case R.id.image_effect_gama:
                outputBmp = imageFilter.applyGammaEffect(abmp.getBitmap(), 255, 255, 255);
                break;

            case R.id.image_effect_color_filter:
                outputBmp = imageFilter.applyColorFilterEffect(abmp.getBitmap(), 255, 255, 255);
                break;

            case R.id.image_effect_sepia:
                outputBmp = imageFilter.applySepiaToningEffect(abmp.getBitmap(), 1, 255, 255, 255);
                break;

            case R.id.image_effect_decrease_color_depth:
                outputBmp = imageFilter.applyDecreaseColorDepthEffect(abmp.getBitmap(), 1);
                break;

            case R.id.image_effect_contrast:
                outputBmp = imageFilter.applyContrastEffect(abmp.getBitmap(), 1);
                break;

            case R.id.image_effect_gaussian_blur:
                outputBmp = imageFilter.applyGaussianBlurEffect(abmp.getBitmap());
                break;

            case R.id.image_effect_bright:
                outputBmp = imageFilter.applyBrightnessEffect(abmp.getBitmap(), 1);
                break;

            case R.id.image_effect_share_pen:
                outputBmp = imageFilter.applySharpenEffect(abmp.getBitmap(), 1);
                break;

            case R.id.image_effect_mean_removal:
                outputBmp = imageFilter.applyMeanRemovalEffect(abmp.getBitmap());
                break;

            case R.id.image_effect_mean_smooth:
                outputBmp = imageFilter.applySmoothEffect(abmp.getBitmap(), 1);
                break;

            case R.id.image_effect_mean_emboss:
                outputBmp = imageFilter.applyEmbossEffect(abmp.getBitmap());
                break;

            case R.id.image_effect_mean_engrave:
                outputBmp = imageFilter.applyEngraveEffect(abmp.getBitmap());
                break;

            case R.id.image_effect_mean_round_corner:
                outputBmp = imageFilter.applyRoundCornerEffect(abmp.getBitmap(), 1);
                break;

            default:
                handled = false;
                break;
        }

        if (outputBmp != null) {
            mCanvasImageView.setImageBitmap(outputBmp);
        }

        return handled;
    }

    /**
     * Displays the dialog on new addition of photo whether from Camera/Gallery
     */
    private void showImageSourceDialog() {
        final String[] items = getResources().getStringArray(R.array.photo_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(getLayoutInflater().inflate(R.layout.custom_dialog_title_view, null));
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = checkExternalStoragePermission(PhotoEditorActivity.this);

                if (items[item].equals(getString(R.string.take_photo))) {
                    mUserChoosenTask = items[item];
                    if (result) {
                        launchCamera();
                    }
                } else if (items[item].equals(getString(R.string.choose_frm_gallery))) {
                    mUserChoosenTask = items[item];
                    if (result) {
                        launchGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    /**
     * Launches the gallery screen
     */
    private void launchGallery() {
        Intent intent = new Intent();
        intent.setType(MIME_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), SELECT_FILE);
    }

    /**
     * Launches the camera
     */
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        } else {
            resetUI();
        }
    }

    /**
     * Resets the UI to fresh state
     */
    private void resetUI() {
        mEmptyPhotoViewContainer.setVisibility(View.VISIBLE);
        mCanvasImageView.setVisibility(View.GONE);
        mPhotoOptionsContainer.setVisibility(View.GONE);
        mImageChoosen = false;
    }

    /**
     * Called when the control comes back from camera screen
     * @param data
     */
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (thumbnail != null) {
            updateUI();
            mCanvasImageView.setImageBitmap(thumbnail);
        }
    }

    /**
     * Updates the UI when the image is choosen from gallery or camera
     */
    private void updateUI() {
        mEmptyPhotoViewContainer.setVisibility(View.GONE);
        mCanvasImageView.setVisibility(View.VISIBLE);
        mImageChoosen = true;
        mPhotoOptionsContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Called when the control comes back from gallery
     * @param data
     */
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;

        if (data != null) {
            try {
                updateUI();
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mCanvasImageView.setImageBitmap(bm);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkExternalStoragePermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permission_required);
                    alertBuilder.setMessage(R.string.external_storage_permission_required);
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mUserChoosenTask.equals(getString(R.string.take_photo))) {
                        launchCamera();
                    } else if (mUserChoosenTask.equals(getString(R.string.choose_frm_gallery))) {
                        launchGallery();
                    }
                }
                break;
        }
    }
}
