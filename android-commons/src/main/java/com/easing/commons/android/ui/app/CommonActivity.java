package com.easing.commons.android.ui.app;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.easing.commons.android.format.TimeUtil;
import com.easing.commons.android.helper.callback.Action;
import com.easing.commons.android.manager.PermissionUtil;
import com.easing.commons.android.manager.SystemUtil;
import com.easing.commons.android.view.ViewManager;
import com.easing.commons.android.value.identity.Values;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

//通过泛型实现ctx的自动转型
public class CommonActivity<T extends CommonActivity> extends AppCompatActivity {

    public T ctx;
    public Handler handler;

    //记录Activity创建时间，用于判断是否首次启动
    @Getter
    private long firstLauchTime;

    //固定屏幕方向
    @Setter
    private  Values.ORIENTATION requestedOrientation = Values.ORIENTATION.ORIENTATION_PORTRAIT;

    private Runnable customBarAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //保存全局环境
        this.ctx = (T) this;
        this.handler = CommonApplication.handler;
        this.firstLauchTime = TimeUtil.millisOfNow();
        //加入任务栈
        this.myApplication().addToStack(this);
        //在onCreate之前执行的代码
        beforeCreate();
        //屏幕始终打开
        super.getWindow().setFlags(LayoutParams.FLAG_KEEP_SCREEN_ON, LayoutParams.FLAG_KEEP_SCREEN_ON);
        //不自动弹出键盘
        super.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //总是竖屏显示
        if (requestedOrientation == Values.ORIENTATION.ORIENTATION_PORTRAIT)
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //总是横屏显示
        if (requestedOrientation == Values.ORIENTATION.ORIENTATION_LANDSCAPE)
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //onCreate生命周期
        super.onCreate(savedInstanceState);
        create();
    }

    @Override
    protected void onDestroy() {
        this.myApplication().removeFromStack(this);
        super.onDestroy();
        destroy();
    }

    //onCreate之前要执行的代码，由子类自己定义
    protected void beforeCreate() {
    }

    //onCreate代码，由子类自己定义
    protected void create() {
    }

    //onDestroy代码，由子类自己定义
    protected void destroy() {
    }

    //申请权限
    public void requestPermission(PermissionUtil.PermissionGroup... groups) {
        PermissionUtil.requestPermissionGroup(this, groups);
    }

    //申请权限，带成功和失败回调
    public void requestPermissionWithCallback(PermissionUtil.PermissionGroup... groups) {
        //先进行权限申请，拿到权限后，才进行操作
        Action okAction = new Action() {
            @Override
            public void act() {
                onPermissionOk();
            }
        };
        //获取权限失败，重启应用
        Action noAction = new Action() {
            @Override
            public void act() {
                onPermissionFail();
            }
        };
        //获取权限
        PermissionUtil.requestPermissionGroup(this, okAction, noAction, groups);
    }

    //申请全部权限
    public void requestAllPermission() {
        requestPermission(
                PermissionUtil.PermissionGroup.STORAGE,
                PermissionUtil.PermissionGroup.LOCATION,
                PermissionUtil.PermissionGroup.PHONE,
                PermissionUtil.PermissionGroup.SMS,
                PermissionUtil.PermissionGroup.CONTACTS,
                PermissionUtil.PermissionGroup.CALENDAR,
                PermissionUtil.PermissionGroup.CAMERA,
                PermissionUtil.PermissionGroup.MICROPHONE,
                PermissionUtil.PermissionGroup.SENSORS
        );
    }

    //申请全部权限
    public void requestAllPermissionWithCallback() {
        requestPermissionWithCallback(
                PermissionUtil.PermissionGroup.STORAGE,
                PermissionUtil.PermissionGroup.LOCATION,
                PermissionUtil.PermissionGroup.PHONE,
                PermissionUtil.PermissionGroup.SMS,
                PermissionUtil.PermissionGroup.CONTACTS,
                PermissionUtil.PermissionGroup.CALENDAR,
                PermissionUtil.PermissionGroup.CAMERA,
                PermissionUtil.PermissionGroup.MICROPHONE,
                PermissionUtil.PermissionGroup.SENSORS
        );
    }

    //申请权限成功
    protected void onPermissionOk() {
    }

    //申请权限失败
    protected void onPermissionFail() {
    }

    //解析布局
    public <T extends View> T inflate(int layoutId) {
        return (T) LayoutInflater.from(ctx).inflate(layoutId, null);
    }

    //获取所在的APP
    public <T extends CommonApplication> T myApplication() {
        return (T) super.getApplication();
    }

    //隐藏窗口
    public void hide() {
        super.moveTaskToBack(true);
    }

    //携带数据跳转，没有则为空
    public void start(Class<? extends Activity> clazz) {
        start(clazz, null);
    }

    //跳转并结束自己
    public void startAndFinish(Class<? extends Activity> clazz) {
        startAndFinish(clazz, null);
    }

    //携带数据跳转，没有则为空
    public void start(Class<? extends Activity> clazz, Map<String, Serializable> params) {
        Intent intent = new Intent(this, clazz);
        if (params != null)
            for (String key : params.keySet())
                intent.putExtra(key, params.get(key));
        super.startActivity(intent);
    }

    //跳转并结束自己
    public void startAndFinish(Class<? extends Activity> clazz, Map<String, Serializable> params) {
        start(clazz, params);
        this.finish();
    }

    //停止服务
    public void stopService(Class<? extends Service> clazz) {
        Intent start_activity = new Intent(this, clazz);
        super.stopService(start_activity);
    }

    public void hideStatuBar() {
        super.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void showStatuBar() {
        super.getWindow().clearFlags(LayoutParams.FLAG_FULLSCREEN);
    }

    public void hideActionBar() {
        super.getSupportActionBar().hide();
    }

    public void showActionBar() {
        super.getSupportActionBar().show();
    }

    public void changeBarColor(int statuBarColorId, int actionBarDrawableId, int avigationBarColorId) {
        super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        super.getWindow().setStatusBarColor(getResources().getColor(statuBarColorId));
        super.getSupportActionBar().setBackgroundDrawable(getDrawable(actionBarDrawableId));
        super.getWindow().setNavigationBarColor(getResources().getColor(avigationBarColorId));
    }

    //这个模式下，StatuBar，ActionBar，NavigationBar都会被视为浮动状态
    //不占据空间，悬浮在布局上方
    public void translucentMode() {
        super.getWindow().addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.getWindow().addFlags(LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    public void immersiveMode() {
        super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void exitImmersiveMode(boolean showStatuBar) {
        super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        if (showStatuBar)
            showStatuBar();
    }

    //空白占位View
    public void showStatuBarPlaceholder(int vid) {
        View view = super.findViewById(vid);
        int height = SystemUtil.getStatuBarHeight(this);
        ViewManager.size(view, null, height);
    }

    //空白占位View
    public void showNavigationBarPlaceholder(int vid) {
        View view = super.findViewById(vid);
        boolean hasNavigationBar = hasNavigationBar();
        ViewManager.size(view, null, SystemUtil.getNavigationBarHeight(ctx));
    }

    //空白占位View
    public void showNavigationBarPlaceholder(int vid, int drawable) {
        View view = super.findViewById(vid);
        int height = SystemUtil.getNavigationBarHeight(this);
        view.setBackgroundResource(drawable);
        ViewManager.size(view, null, height);
    }

    //隐藏空白占位View
    public void hideStatuBarPlaceholder(int vid) {
        View view = super.findViewById(vid);
        ViewManager.size(view, null, 0);
    }

    //隐藏空白占位View
    public void hideNavigationBarPlaceholder(int vid) {
        View view = super.findViewById(vid);
        ViewManager.size(view, null, 0);
    }

    //自定义顶部状态栏和底部导航栏颜色
    public void customBar(int statuBarColor, int navigationBarColor, boolean useBlackNavigationIcon, Integer statuPlaceholder, Integer navigationPlaceholder) {
        int flag = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        if (useBlackNavigationIcon)
            flag = flag | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        super.getWindow().getDecorView().setSystemUiVisibility(flag);

        super.getWindow().setStatusBarColor(getResources().getColor(statuBarColor));
        super.getWindow().setNavigationBarColor(getResources().getColor(navigationBarColor));

        //显示顶部占位符
        if (statuPlaceholder != null)
            showStatuBarPlaceholder(statuPlaceholder);

        customBarAction = () -> {
            if (navigationPlaceholder == null)
                return;
            //显示底部占位符
            boolean hasNavigationBar = hasNavigationBar();
            if (hasNavigationBar)
                showNavigationBarPlaceholder(navigationPlaceholder);
            else
                hideNavigationBarPlaceholder(navigationPlaceholder);

            //去除全面屏底部黑边
            if (!hasNavigationBar) {
                LinkedList<View> list = ViewManager.getAllChildViews(ctx);
                ViewManager.size(list.get(2), null, list.get(0).getMeasuredHeight());
            }
        };
        handler.postDelayed(customBarAction, 500);
    }

    //判断下方虚拟导航栏是否显示
    //这个方法必须在控件解析完毕，即onWindowFocusChanged调用结束后才有效
    public boolean hasNavigationBar() {
        int navigationBarHeight = SystemUtil.getNavigationBarHeight(ctx);
        LinkedList<View> list = ViewManager.getAllChildViews(ctx);
        for (View v : list)
            if (v.getClass() == View.class && v.getMeasuredHeight() == navigationBarHeight)
                return true;
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && customBarAction != null)
            customBarAction.run();
    }

    //判断是否首次进入界面
    public boolean isFirstLauch() {
        return TimeUtil.millisOfNow() - firstLauchTime < 1000;
    }
}
