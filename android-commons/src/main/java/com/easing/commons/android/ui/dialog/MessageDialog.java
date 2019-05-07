package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.ui.control.button.ImageButtonM2;

//消息框
public class MessageDialog extends DialogFragment {

    private String message = "";

    private AppCompatActivity ctx;

    private TextView messageText;

    //静态创建方法
    public static MessageDialog create() {
        MessageDialog dialog = new MessageDialog();
        dialog.setCancelable(false);
        return dialog;
    }

    //静态创建方法
    public static MessageDialog create(Object msg) {
        MessageDialog dialog = new MessageDialog();
        dialog.setCancelable(false);
        dialog.message = msg.toString();
        return dialog;
    }

    //设置消息
    public MessageDialog message(Object msg) {
        message = msg.toString();
        return this;
    }

    //显示
    public void show(AppCompatActivity ctx) {
        this.ctx = ctx;
        FragmentManager manager = ctx.getSupportFragmentManager();
        super.show(manager, "MessageDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View root = getActivity().getLayoutInflater().inflate(R.layout.layout_message_dialog, null);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        //解析控件
        messageText = root.findViewById(R.id.text_msg);
        ImageButtonM2 okButton = root.findViewById(R.id.bt_ok);
        messageText.setText(message);
        okButton.setOnClickListener(v -> {
            dismiss();
        });
        return dialog;
    }
}
