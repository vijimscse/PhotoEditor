package com.studyjam.android.photoeditor;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoEditorActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_FILE = 100;
    private static final int REQUEST_CAMERA = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private ImageView mCanvasImageView;
    private String mUserChoosenTask;
    private View mEmptyPhotoViewContainer;
    private boolean mImageChoosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initialiseViews();
    }

    private void initialiseViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.photo_editor);
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

        mEmptyPhotoViewContainer.setOnClickListener(this);
        findViewById(R.id.image_effects).setOnClickListener(this);
        findViewById(R.id.rotate_acw).setOnClickListener(this);
        findViewById(R.id.rotate_cw).setOnClickListener(this);
    }

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
                break;

            case R.id.rotate_acw:
                break;

            case R.id.rotate_cw:
                break;
        }
    }

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
                    if(result)
                        launchCamera();
                } else if (items[item].equals(getString(R.string.choose_frm_gallery))) {
                    mUserChoosenTask = items[item];
                    if(result)
                        launchGallery();
                }
            }
        });
        builder.show();
    }

    private void launchGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

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
            mEmptyPhotoViewContainer.setVisibility(View.GONE);
            mCanvasImageView.setVisibility(View.GONE);
            mImageChoosen = false;
        }
    }

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
            mEmptyPhotoViewContainer.setVisibility(View.GONE);
            mCanvasImageView.setVisibility(View.VISIBLE);
            mImageChoosen = true;
            mCanvasImageView.setImageBitmap(thumbnail);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                mEmptyPhotoViewContainer.setVisibility(View.GONE);
                mCanvasImageView.setVisibility(View.VISIBLE);
                mImageChoosen = true;
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
