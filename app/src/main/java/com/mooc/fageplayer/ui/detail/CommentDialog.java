package com.mooc.fageplayer.ui.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.Observer;

import com.mooc.fageplayer.R;
import com.mooc.fageplayer.databinding.LayoutCommentDialogBinding;
import com.mooc.fageplayer.model.Comment;
import com.mooc.fageplayer.ui.login.UserManager;
import com.mooc.fageplayer.ui.publish.CaptureActivity;
import com.mooc.libcommon.AppGlobals;
import com.mooc.libcommon.dialog.LoadingDialog;
import com.mooc.libcommon.utils.FileUploadManager;
import com.mooc.libcommon.utils.FileUtils;
import com.mooc.libcommon.utils.PixUtils;
import com.mooc.libcommon.view.ViewHelper;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;

import java.util.concurrent.atomic.AtomicInteger;

public class CommentDialog extends AppCompatDialogFragment implements View.OnClickListener {

    private static final String KEY_ITEM_ID = "key_item_id";
    private LayoutCommentDialogBinding mBinding;
    private long itemId;
    private boolean isVideo;

    private int width, height;
    private String coverUrl;
    private String fileUrl;
    private CommentAddListener mListener;
    private String filePath;
    private boolean isOnDestroy = false;
    private LoadingDialog loadingDialog;


    public static CommentDialog newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(KEY_ITEM_ID, itemId);
        CommentDialog fragment = new CommentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Window window = getDialog().getWindow();
        window.setWindowAnimations(0);

        isOnDestroy = false;

        mBinding = LayoutCommentDialogBinding.inflate(inflater, ((ViewGroup) window.findViewById(android.R.id.content)), false);
        mBinding.commentVideo.setOnClickListener(this);
        mBinding.commentDelete.setOnClickListener(this);
        mBinding.commentSend.setOnClickListener(this);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        this.itemId = getArguments().getLong(KEY_ITEM_ID);
        ViewHelper.setViewOutline(mBinding.getRoot(), PixUtils.dp2px(10), ViewHelper.RADIUS_TOP);

        /**
         * 软键盘弹出必要条件
         * 1：必须是EditText或者子类
         * 2：必须先获取焦点
         * 3：如果是在onCreateView中，必须设置50秒以上的延迟以保证View加载完毕
         */
        mBinding.getRoot().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSoftInputMethod();
            }
        }, 100);

        dismissWhenPressBack();
        return mBinding.getRoot();
    }

    private void dismissWhenPressBack() {
        mBinding.inputView.setOnBackKeyEventListener(() -> {
            mBinding.inputView.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            hideSoftInputMethod();
                            dismiss();
                        }
                    }
            , 200);
            return true;
        });
    }

    private void showSoftInputMethod() {
        mBinding.inputView.setFocusable(true);
        mBinding.inputView.setFocusableInTouchMode(true);
        mBinding.inputView.requestFocus();
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(mBinding.inputView, InputMethodManager.SHOW_FORCED);
    }

    private void hideSoftInputMethod() {
        if (isOnDestroy) return;
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(mBinding.inputView.getWindowToken(), 0);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comment_send:
                publishComment();
                break;
            case R.id.comment_video:
                hideSoftInputMethod();
                CaptureActivity.startActivityForResult(getActivity());
                break;
            case R.id.comment_delete:
                filePath = null;
                isVideo = false;
                width = 0;
                height = 0;
                mBinding.commentCover.setImageDrawable(null);
                mBinding.commentExtLayout.setVisibility(View.GONE);

                // 允许再次录制视频
                mBinding.commentVideo.setEnabled(true);
                mBinding.commentVideo.setAlpha(255);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_CAPTURE && resultCode == Activity.RESULT_OK) {
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);

            if (!TextUtils.isEmpty(filePath)) {
                mBinding.commentExtLayout.setVisibility(View.VISIBLE);
                mBinding.commentCover.setImageUrl(filePath);
                if (isVideo) {
                    mBinding.commentIconVideo.setVisibility(View.VISIBLE);
                }
            }

            // 禁止再次录制视频 除非删除
            mBinding.commentVideo.setEnabled(false);
            mBinding.commentVideo.setAlpha(80);
        }
    }

    /**
     * 文件上传、发布
     */
    private void publishComment() {
        if (TextUtils.isEmpty(mBinding.inputView.getText())) {
            return;
        }
        if (isVideo && !TextUtils.isEmpty(filePath)) {
            // 视频
            FileUtils.generateVideoCover(filePath).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String coverPath) {
                    uploadFile(coverPath, filePath);
                }
            });
        } else if (!TextUtils.isEmpty(filePath)) {
            // 图片
            uploadFile(null, filePath);
        } else {
            publish();
        }
    }

    /**
     * 上传封面图、视频文件
     * @param coverPath
     * @param filePath
     */
    @SuppressLint("RestrictedApi")
    private void uploadFile(String coverPath, String filePath) {
        showLoadingDialog();

        AtomicInteger count = new AtomicInteger(1);

        /**
         * 视频
         * 封面图+文件path
         */
        if (!TextUtils.isEmpty(coverPath)) {
            count.set(2);
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    int remain = count.decrementAndGet();
                    coverUrl = FileUploadManager.upload(coverPath);
                    if (remain <= 0) {
                        if (!TextUtils.isEmpty(fileUrl) && !TextUtils.isEmpty(coverUrl)) {
                            publish();
                        } else {
                            dismissLoadingDialog();
                            showToast(getString(R.string.file_upload_failed));
                        }
                    }
                }
            });
        }

        /**
         * 图片只有文件，没有封面图
         */
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int remain = count.decrementAndGet();
                fileUrl = FileUploadManager.upload(filePath);
                if (remain <= 0) {
                    if (!TextUtils.isEmpty(fileUrl) || !TextUtils.isEmpty(coverPath) && !TextUtils.isEmpty(coverUrl)) {
                        publish();
                    } else {
                        dismissLoadingDialog();
                        showToast(getString(R.string.file_upload_failed));
                    }
                }
            }
        });
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
            loadingDialog.setLoadingText(getString(R.string.upload_text));
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(false);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @SuppressLint("RestrictedApi")
    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            } else {
                ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });
            }
        }
    }

    /**
     * 评论发布
     */
    private void publish() {
        String commentText = mBinding.inputView.getText().toString();
        ApiService.post("/comment/addComment")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", itemId)
                .addParam("commentText", commentText)
                .addParam("image_url", isVideo ? coverUrl : fileUrl)
                .addParam("video_url", isVideo ? fileUrl : null)
                .addParam("width", width)
                .addParam("height", height)
                .execute(new JsonCallback<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommentSuccess(response.body);
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(ApiResponse<Comment> response) {
                        showToast("评论失败:" + response.message);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 多线程调用Toast的处理
     * @param s
     */
    @SuppressLint("RestrictedApi")
    private void showToast(String s) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(AppGlobals.getApplication(), s, Toast.LENGTH_SHORT).show();
        } else {
            ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
                Toast.makeText(AppGlobals.getApplication(), s, Toast.LENGTH_SHORT).show();
            });
        }
    }

    @SuppressLint("RestrictedApi")
    private void onCommentSuccess(Comment body) {
        showToast("评论发布成功");
        ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
            if (mListener != null) {
                mListener.onAddComment(body);
            }
            hideSoftInputMethod();
            dismiss();
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissLoadingDialog();

        filePath = null;
        fileUrl = null;
        coverUrl = null;
        isVideo = false;
        width = 0;
        height = 0;
    }

    public interface CommentAddListener {
        void onAddComment(Comment comment);
    }

    public void setCommentAddListener(CommentAddListener listener) {
        mListener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isOnDestroy = true;
    }
}
