package com.example.jetpackplayerapp.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.libcommon.global.view.ViewHelper;

public class PPImageView extends AppCompatImageView {
    public PPImageView(Context context) {
        super(context);
    }

    public PPImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PPImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewHelper.setViewOutline(this, attrs, defStyleAttr, 0);
    }

}
