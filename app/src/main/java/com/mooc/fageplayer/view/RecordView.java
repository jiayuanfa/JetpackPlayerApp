package com.mooc.fageplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mooc.fageplayer.R;
import com.mooc.libcommon.utils.PixUtils;

public class RecordView extends View implements View.OnLongClickListener, View.OnClickListener{

    private static final int PROGRESS_INTERVAL = 100;
    private final Paint fillPaint;
    private final Paint progressPaint;
    private int progressMaxValue;
    private final int radius;
    private final int progressWidth;
    private final int progressColor;
    private final int fillColor;
    private final int maxDuration;
    private int progressValue;
    private boolean isRecording;
    private long startRecordTime;
    private OnRecordListener mListener;

    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.RecordView,
                defStyleAttr,
                defStyleRes
                );
        radius = typedArray.getDimensionPixelOffset(R.styleable.RecordView_radius, 0);
        progressWidth = typedArray.getDimensionPixelOffset(R.styleable.RecordView_progress_width, PixUtils.dp2px(3));
        progressColor = typedArray.getColor(R.styleable.RecordView_progress_color, Color.RED);
        fillColor = typedArray.getColor(R.styleable.RecordView_fill_color, Color.WHITE);
        maxDuration = typedArray.getInteger(R.styleable.RecordView_duration, 10);
        setMaxDuration(maxDuration);
        typedArray.recycle();

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(fillColor);
        fillPaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressWidth);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                progressValue++;
                postInvalidate();
                if (progressValue <= progressMaxValue) {
                    sendEmptyMessageDelayed(0, PROGRESS_INTERVAL);
                } else {
                    finishRecord();
                }
            }
        };

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    isRecording = true;
                    startRecordTime = System.currentTimeMillis();
                    handler.sendEmptyMessage(0);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    long now = System.currentTimeMillis();
                    if (now - startRecordTime > ViewConfiguration.getLongPressTimeout()) {
                        finishRecord();
                    }
                    handler.removeCallbacksAndMessages(null);
                    isRecording = false;
                    startRecordTime = 0;
                    progressValue = 0;
                    postInvalidate();
                }
                return false;
            }
        });

        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    private void finishRecord() {
        if (mListener != null) {
            mListener.onFinish();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (isRecording) {
            canvas.drawCircle(width / 2, height / 2, width / 2, fillPaint);

            /**
             * 绘制原理： RectF绘制一个矩形，此虚拟矩形内切入一个椭圆，以矩形的中心为圆心，
             * 以时钟方向旋转startAngle度，和椭圆相交得到一条直线和一个交点；从这点直线开
             * 始，正方向旋转sweepAngle度，得到另一条直线和交点，这样得到一个两个交点的圆
             * 弧；
             */
            int left = progressWidth / 2;
            int top = progressWidth / 2;
            int right = width - progressWidth / 2;
            int bottom = height - progressWidth / 2;

            /**
             * 指定绘制的角度大小
             * 实际上就是弧线的长短
             */
            float sweepAngle = (progressValue * 1.0f / progressMaxValue) * 360;

            /**
             * 绘制外圈的弧线
             * useCenter为false就是弧线，否则就是扇形
             */
            canvas.drawArc(left, top, right, bottom, -90, sweepAngle, false, progressPaint);
        } else {
            canvas.drawCircle(width / 2, height / 2, radius, fillPaint);
        }
    }

    private void setMaxDuration(int maxDuration) {
        this.progressMaxValue = maxDuration * 1000 / PROGRESS_INTERVAL;
    }

    public void setOnRecordListener(OnRecordListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onClick();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mListener != null) {
            mListener.onLongClick();
        }
        return true;
    }

    public interface OnRecordListener {
        void onClick();
        void onLongClick();
        void onFinish();
    }
}
