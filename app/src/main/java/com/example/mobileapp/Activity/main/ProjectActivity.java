package com.example.mobileapp.Activity.main;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.R;

public class ProjectActivity extends AppCompatActivity {

    private ImageView imgProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_project);

        imgProject = findViewById(R.id.imgProject);

        imgProject.setCameraDistance(12000);

        imgProject.setRotationY(-12f);
        imgProject.setRotationX(8f);

        Animation anim =
                AnimationUtils.loadAnimation(this, R.anim.card_float);

        imgProject.startAnimation(anim);
    }
}