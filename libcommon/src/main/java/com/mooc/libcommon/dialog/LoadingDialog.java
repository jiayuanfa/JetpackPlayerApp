package com.mooc.libcommon.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mooc.libcommon.R;
import com.mooc.libcommon.utils.PixUtils;
import com.mooc.libcommon.view.ViewHelper;


/**
 * 自定义上传中Dialog
 */
public class LoadingDialog extends AlertDialog {

    private TextView loadingText;

    public LoadingDialog(Context context) {
        super(context);
    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setLoadingText(String loadingText) {
        if (this.loadingText != null) {
            this.loadingText.setText(loadingText);
        }
    }

    /**
     * 要下载show方法后面
     * 不然会出现一些奇奇怪怪的BUG
     */
    @Override
    public void show() {
        super.show();
        setContentView(R.layout.layout_loading_view);
        loadingText = findViewById(R.id.loading_text);

        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.gravity = Gravity.CENTER;
        attributes.dimAmount = 0.5f;

        // 必须设置这个背景 否则会出现对话框的宽度很宽
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 给转菊花的loading的对话框来一个圆角
        ViewHelper.setViewOutline(findViewById(R.id.loading_layout), PixUtils.dp2px(10), ViewHelper.RADIUS_ALL);
        window.setAttributes(attributes);
    }
}
