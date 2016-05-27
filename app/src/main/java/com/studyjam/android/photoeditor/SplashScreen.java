package com.studyjam.android.photoeditor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Vijayalakshmi on 27-05-2016.
 */
public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT_TASK_ID = 100;
    private static final int SPLASH_SCREEN_BUFFER_TIME = 2000;
    private boolean mIsInForeground = true;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SPLASH_TIME_OUT_TASK_ID && !isFinishing() && mIsInForeground) {
                launchPhotoEditorScreen();
            }
        };
    };

    private void launchPhotoEditorScreen() {
        Intent intent = new Intent(this, PhotoEditorActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
    }

    /* (non-Javadoc)
   * @see android.app.Activity#onResume()
   */
    @Override
    protected void onResume() {

        mHandler.removeMessages(SPLASH_TIME_OUT_TASK_ID);
        mHandler.sendEmptyMessageDelayed(SPLASH_TIME_OUT_TASK_ID, SPLASH_SCREEN_BUFFER_TIME);
        mIsInForeground = true;

        super.onResume();
    }

    /* (non-Javadoc)
     * @see com.housejoy.consumer.activity.AbstractBaseActivity#onPause()
     */
    @Override
    protected void onPause() {
        mIsInForeground = false;

        super.onPause();
    }

}
