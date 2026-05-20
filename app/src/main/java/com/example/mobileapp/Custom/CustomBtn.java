package com.example.mobileapp.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mobileapp.R;

public class CustomBtn extends FrameLayout {

    private AppCompatButton btn;

    public CustomBtn(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CustomBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_button, this, true);

        btn = findViewById(R.id.btnCustom);

        if (attrs != null) {
            android.content.res.TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomBtn);
            String text = a.getString(R.styleable.CustomBtn_android_text);
            if (text != null) {
                setText(text);
            }
            a.recycle();
        }

        setClickable(true);
        setFocusable(true);

        btn.setClickable(true);
    }

    public void setText(String text) {
        btn.setText(text);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);   
        btn.setOnClickListener(l);
    }
}