package com.example.mobileapp.Custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobileapp.R;

import java.util.Locale;

public class CustomInputField extends FrameLayout {

    private TextView tvLabel;
    private TextView tvError;
    private EditText edtInput;

    public CustomInputField(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CustomInputField(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomInputField(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_input, this, true);

        tvLabel = findViewById(R.id.tvLabel);
        edtInput = findViewById(R.id.edtInput);
        tvError = findViewById(R.id.tvError);

        if (attrs != null) {
            android.content.res.TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomInputField);

            String labelText = a.getString(R.styleable.CustomInputField_labelText);
            String hint = a.getString(R.styleable.CustomInputField_android_hint);
            String text = a.getString(R.styleable.CustomInputField_android_text);
            int inputType = a.getInt(R.styleable.CustomInputField_android_inputType, -1);
            int textSize = a.getDimensionPixelSize(R.styleable.CustomInputField_android_textSize, -1);

            if (labelText != null) setLabelText(labelText);
            if (hint != null) setHint(hint);
            if (text != null) setText(text);

            if (inputType != -1) {
                edtInput.setInputType(inputType);
            }

            if (textSize != -1) {
                edtInput.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            a.recycle();
        }

        if (edtInput != null) {
            // Đảm bảo bàn phím hiển thị nút "Tiếp tục" thay vì "Hoàn tất" để tránh đóng bàn phím đột ngột
            edtInput.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_NEXT);

            edtInput.setOnFocusChangeListener((v, hasFocus) -> {
                if (tvLabel == null) return;
                if (hasFocus) {
                    tvLabel.setTypeface(null, Typeface.BOLD);
                    tvLabel.setTextColor(Color.parseColor("#2196F3"));
                } else {
                    tvLabel.setTypeface(null, Typeface.NORMAL);
                    tvLabel.setTextColor(Color.parseColor("#757575"));
                }
            });
        }
    }

    public void setLabelText(String text) {
        if (tvLabel != null) tvLabel.setText(text);
    }

    public void setHint(String hint) {
        if (edtInput != null) edtInput.setHint(hint);
    }

    public void setText(String text) {
        // Quan trọng: Chỉ set text nếu giá trị mới khác giá trị cũ để tránh mất dấu khi đang gõ
        if (edtInput != null && !edtInput.getText().toString().equals(text)) {
            edtInput.setText(text);
        }
    }

    public String getEnteredText() {
        return (edtInput != null) ? edtInput.getText().toString() : "";
    }

    public void setError(String message) {
        if (message == null || message.isEmpty()) {
            tvError.setVisibility(GONE);
        } else {
            tvError.setText(message);
            tvError.setVisibility(VISIBLE);
        }
    }

    public void clearError() {
        tvError.setVisibility(GONE);
    }

    public EditText getEditText() {
        return edtInput;
    }
}
