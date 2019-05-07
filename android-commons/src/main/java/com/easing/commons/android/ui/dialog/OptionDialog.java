package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.manager.DeviceUtil;
import com.easing.commons.android.manager.DimenUtil;
import com.easing.commons.android.view.ViewManager;
import com.easing.commons.android.ui.app.CommonActivity;
import com.easing.commons.android.value.color.ColorUtil;

//弹出任意布局
public class OptionDialog<K> extends DialogFragment {

    private CommonActivity ctx;
    private View root;
    private K[] options;
    private OnSelectListener<K> listener;

    private int textWidth;
    private int dx;

    //静态创建方法
    public static <K> OptionDialog<K> create(CommonActivity ctx, K[] options, OnSelectListener<K> listener) {
        OptionDialog dialog = new OptionDialog();
        dialog.setCancelable(true);
        dialog.ctx = ctx;
        dialog.root = ViewManager.inflate(ctx, R.layout.layout_option_dialog);
        dialog.options = options;
        dialog.listener = listener;
        return dialog;
    }

    //显示
    public void show() {
        FragmentManager manager = ctx.getSupportFragmentManager();
        super.show(manager, "OptionDialog");
    }

    //隐藏
    public void dispose() {
        dismiss();
        ctx = null;
        root = null;
        options = null;
        listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout container = root.findViewById(R.id.layout_items);
        for (Object option : options) {
            //添加选项文本
            TextView tv = ViewManager.inflate(ctx, R.layout.item_option_dialog);
            float length = tv.getPaint().measureText(option.toString());
            textWidth = Math.max(textWidth, (int) length);
            tv.setText(option.toString());
            tv.setTag(option);
            container.addView(tv);
            //添加分割线
            View split = new View(ctx);
            container.addView(split);
        }
        dx = DimenUtil.toPx(ctx, 150);
        for (int i = 0; i < container.getChildCount(); i++) {
            if (i % 2 != 0) {
                View v = container.getChildAt(i);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewManager.MATCH_PARENT, 1, 0));
                if (i != container.getChildCount() - 1)
                    v.setBackgroundColor(ColorUtil.LIGHT_GREY);
                continue;
            }
            TextView tv = (TextView) container.getChildAt(i);
            tv.setLayoutParams(new LinearLayout.LayoutParams(ViewManager.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
            tv.setOnClickListener(v -> {
                int index = (container.indexOfChild(v) + 1) / 2;
                if (listener != null)
                    listener.onSelect(v, (K) v.getTag(), index);
                dispose();
            });
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        int screenWidth = DeviceUtil.getScreenSize(ctx).w;
        getDialog().getWindow().setLayout((int) (screenWidth * 0.9), ViewManager.WRAP_CONTENT);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) root.getLayoutParams();
        layoutParams.width = ViewManager.MATCH_PARENT;
        layoutParams.height = ViewManager.WRAP_CONTENT;
        root.setLayoutParams(layoutParams);
    }

    //解析子控件
    public <T> T findView(int viewId) {
        return (T) root.findViewById(viewId);
    }

    public interface OnSelectListener<K> {
        void onSelect(View v, K item, int index);
    }
}
