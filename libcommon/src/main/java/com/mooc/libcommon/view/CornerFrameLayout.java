package com.mooc.libcommon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 可以自定义圆角的FrameLayout
 */
public class CornerFrameLayout extends FrameLayout {

    public CornerFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public CornerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CornerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        ViewHelper.setViewOutline(this, attrs, defStyleAttr, defStyleRes);
    }

    public void setViewOutline(int radius, int radiusSide) {
        ViewHelper.setViewOutline(this, radius, radiusSide);
    }
}
