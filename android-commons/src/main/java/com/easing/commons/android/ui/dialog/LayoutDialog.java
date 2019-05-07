package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.easing.commons.android.view.ViewManager;
import com.easing.commons.android.ui.app.CommonActivity;

//弹出任意布局
public class LayoutDialog extends DialogFragment {

    private CommonActivity ctx;
    private View root;

    //静态创建方法
    public static LayoutDialog create(CommonActivity ctx, int layoutId) {
        LayoutDialog dialog = new LayoutDialog();
        dialog.setCancelable(false);
        dialog.ctx = ctx;
        dialog.root = ViewManager.inflate(ctx, layoutId);
        return dialog;
    }

    //显示
    public void show() {
        FragmentManager manager = ctx.getSupportFragmentManager();
        super.show(manager, "LayoutDialog");
    }

    //隐藏
    public void dispose() {
        dismiss();
        ctx = null;
        root = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    //解析子控件
    public <T> T findView(int viewId) {
        return (T) root.findViewById(viewId);
    }
}
