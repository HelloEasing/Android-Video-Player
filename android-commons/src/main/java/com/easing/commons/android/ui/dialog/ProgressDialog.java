package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.ui.app.CommonActivity;

//进度框，只能弹出一次，下次要重新创建
public class ProgressDialog extends DialogFragment {

    private CommonActivity ctx;
    private String message;
    private boolean alive = true;

    //创建
    public static ProgressDialog pop(CommonActivity ctx, Object msg) {
        //创建
        ProgressDialog dialog = new ProgressDialog();
        dialog.ctx = ctx;
        dialog.message = msg.toString();
        dialog.setCancelable(false);
        //弹出
        FragmentTransaction transaction = ctx.getSupportFragmentManager().beginTransaction();
        transaction.add(dialog, "DialogFragment");
        transaction.commit();
        return dialog;
    }

    //销毁
    public void dispose() {
        ctx.handler.postDelayed(super::dismiss, 500);
        alive = false;
    }

    //销毁
    public void disposeImmediately() {
        ctx.handler.post(super::dismiss);
        alive = false;
    }

    //是否销毁
    public boolean alive() {
        return alive;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View root = getActivity().getLayoutInflater().inflate(R.layout.layout_progress_dialog, null);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        //设置图片动画
        ImageView iv = root.findViewById(R.id.iv);
        AnimationDrawable animation = (AnimationDrawable) getResources().getDrawable(R.drawable.progress_m01);
        iv.setImageDrawable(animation);
        animation.start();
        //设置提示信息
        TextView messageText = root.findViewById(R.id.text_msg);
        messageText.setText(message);
        return dialog;
    }
}
