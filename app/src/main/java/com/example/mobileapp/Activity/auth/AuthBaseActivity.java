package com.example.mobileapp.Activity.auth;

import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.example.mobileapp.Activity.auth.BaseActivity;
import com.example.mobileapp.R;

public class AuthBaseActivity extends BaseActivity {

    protected VideoView videoView;
    protected FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth_base);

        videoView = findViewById(R.id.videoBackground);
        container = findViewById(R.id.authContainer);

        setupVideo();
    }

    private void setupVideo() {

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background);
        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);

            videoView.start();

            videoView.post(() -> {

                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();

                int viewWidth = videoView.getWidth();
                int viewHeight = videoView.getHeight();

                if (videoWidth == 0 || videoHeight == 0 || viewWidth == 0 || viewHeight == 0) return;

                float videoRatio = (float) videoWidth / videoHeight;
                float viewRatio = (float) viewWidth / viewHeight;

                float scale = (videoRatio > viewRatio)
                        ? (float) viewHeight / videoHeight
                        : (float) viewWidth / videoWidth;

                videoView.setScaleX(scale);
                videoView.setScaleY(scale);

                videoView.setTranslationX((viewWidth - videoWidth * scale) / 2f);
                videoView.setTranslationY((viewHeight - videoHeight * scale) / 2f);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) videoView.pause();
    }
}