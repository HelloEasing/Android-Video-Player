package com.easing.commons.android.ui.control.compound;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.view.ViewManager;

import lombok.Getter;
import lombok.Setter;

public class LoginBox extends LinearLayout {

    @Getter
    private EditText userEdit;
    @Getter
    private EditText passwordEdit;
    @Getter
    private Button rememberPasswordCheckBox;
    @Getter
    private Button autoLoginCheckBox;
    @Getter
    private Button loginButton;

    private TextView rememberPasswordText;
    private TextView autoLoginText;

    private Button passwordEyeButton;

    @Setter
    private OnSelectedChangeListener onRememberPasswordChangeListener;
    @Setter
    private OnSelectedChangeListener onAutoLoginChangeListener;

    public LoginBox(Context context) {
        super(context);
        init(context, null);
    }

    public LoginBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoginBox(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrSet) {
        View root = ViewManager.inflate(context, R.layout.layout_login_box_m01);
        super.addView(root, new LayoutParams(ViewManager.MATCH_PARENT, ViewManager.MATCH_PARENT));

        userEdit = ViewManager.findView(root, R.id.user_edit);
        passwordEdit = ViewManager.findView(root, R.id.password_edit);
        rememberPasswordCheckBox = ViewManager.findView(root, R.id.bt_remember);
        autoLoginCheckBox = ViewManager.findView(root, R.id.bt_auto_login);
        loginButton = ViewManager.findView(root, R.id.bt_login);
        rememberPasswordText = ViewManager.findView(root, R.id.text_remember);
        autoLoginText = ViewManager.findView(root, R.id.text_auto_login);
        passwordEyeButton = ViewManager.findView(root, R.id.bt_eye);

        ViewManager.removeDoubleClickEvent(userEdit);
        ViewManager.removeDoubleClickEvent(passwordEdit);

        HideReturnsTransformationMethod showRealText = HideReturnsTransformationMethod.getInstance();
        PasswordTransformationMethod showEncryptText = PasswordTransformationMethod.getInstance();
        passwordEyeButton.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN)
                passwordEdit.setTransformationMethod(showRealText);
            else if (e.getAction() == MotionEvent.ACTION_UP)
                passwordEdit.setTransformationMethod(showEncryptText);
            return false;
        });

        rememberPasswordCheckBox.setOnClickListener((v) -> {
            boolean b = rememberPasswordCheckBox.isSelected();
            rememberPasswordCheckBox.setSelected(!b);
            if (onRememberPasswordChangeListener != null)
                onRememberPasswordChangeListener.action(!b);
        });
        autoLoginCheckBox.setOnClickListener((v) -> {
            boolean b = autoLoginCheckBox.isSelected();
            autoLoginCheckBox.setSelected(!b);
            if (onAutoLoginChangeListener != null)
                onAutoLoginChangeListener.action(!b);
        });
    }

    public LoginBox rememberPassword(boolean b) {
        rememberPasswordCheckBox.setSelected(b);
        return this;
    }

    public LoginBox autoLogin(boolean b) {
        autoLoginCheckBox.setSelected(b);
        return this;
    }

    public boolean rememberPassword() {
        return rememberPasswordCheckBox.isSelected();
    }

    public boolean autoLogin() {
        return autoLoginCheckBox.isSelected();
    }

    public LoginBox enableRememberPassword(boolean b) {
        rememberPasswordCheckBox.setVisibility(b ? View.VISIBLE : View.GONE);
        rememberPasswordText.setVisibility(b ? View.VISIBLE : View.GONE);
        return this;
    }

    public LoginBox enableAutoLogin(boolean b) {
        autoLoginCheckBox.setVisibility(b ? View.VISIBLE : View.GONE);
        autoLoginText.setVisibility(b ? View.VISIBLE : View.GONE);
        return this;
    }

    public LoginBox username(String username) {
        ViewManager.text(userEdit, username);
        return this;
    }

    public LoginBox password(String password) {
        ViewManager.text(passwordEdit, password);
        return this;
    }

    public String username() {
        return ViewManager.getText(userEdit, false, false);
    }

    public String password() {
        return ViewManager.getText(passwordEdit, false, false);
    }

    public interface OnSelectedChangeListener {
        void action(boolean selected);
    }
}
