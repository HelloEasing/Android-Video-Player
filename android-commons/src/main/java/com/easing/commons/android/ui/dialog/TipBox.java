package com.easing.commons.android.ui.dialog;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easing.commons.android.format.TextUtil;
import com.easing.commons.android.view.ViewManager;
import com.easing.commons.android.R;

//自定义Toast
public class TipBox {

    private static Context ctx;
    private static Handler handler;

    volatile private static Toast toast;

    //绑定应用上下文
    public static void init(Context ctx) {
        TipBox.ctx = ctx;
        TipBox.handler = new Handler();
    }

    //通过指定的Layout显示Toast
    public static void message(Object msg, int layout) {
        handler.post(() -> {
            if (toast != null)
                toast.cancel();

            View root = LayoutInflater.from(ctx).inflate(layout, null);
            TextView tv = ViewManager.findView(root, R.id.tv);
            tv.setText(msg.toString());
            toast = new Toast(ctx);
            toast.setView(root);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        });
    }

    //取消Toast
    public static void cancel() {
        handler.post(() -> {
            if (toast != null)
                toast.cancel();
        });
    }

    //M1样式toast
    public static void messageM1(Object msg) {
        TipBox.message(msg, R.layout.layout_tip_box_1);
    }

    //默认使用M1样式显示toast
    public static void tip(Object msg) {
        TipBox.messageM1(msg);
    }

    //默认使用M1样式显示toast
    public static void tip(Object... msg) {
        TipBox.messageM1(TextUtil.arrayToString(msg));
    }
}
