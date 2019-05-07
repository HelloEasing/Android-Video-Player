package com.easing.commons.android.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.easing.commons.android.value.measure.Pos;
import com.easing.commons.android.value.measure.Size;

import java.util.LinkedList;

public class ViewManager {

    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;

    //解析Layout
    public static <T extends View> T inflate(Context context, int layoutId) {
        return (T) View.inflate(context, layoutId, null);
    }

    //解析View
    public static <T extends View> T findView(View v, int vid) {
        return (T) v.findViewById(vid);
    }

    //获取控件绑定的数据
    public static <T> T getTag(View v) {
        return (T) v.getTag();
    }

    //获取控件相当于屏幕的位置
    public static Pos getPositionOnScreen(View v) {
        int[] pos = new int[2];
        v.getLocationOnScreen(pos);
        return new Pos(pos[0], pos[1]);
    }

    //设置控件边距
    public static void padding(View v, int top, int bottom, int left, int right) {
        v.setPadding(left, top, right, bottom);
    }

    //获取屏幕大小
    @Deprecated
    public static Size getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        return new Size(dm.widthPixels, dm.heightPixels);
    }

    //获取控件实际宽度
    public static int width(View v) {
        return v.getMeasuredWidth();
    }

    //获取控件实际高度
    public static int height(View v) {
        return v.getMeasuredHeight();
    }

    //获取控件实际大小
    public static Size size(View v) {
        int w = v.getMeasuredWidth();
        int h = v.getMeasuredHeight();
        return new Size(w, h);
    }

    //设置控件背景
    public static void setBackground(View v, int drawableId) {
        Drawable drawable = v.getContext().getDrawable(drawableId);
        v.setBackground(drawable);
    }

    //设置控件大小
    public static void size(View v, Integer w, Integer h) {
        LayoutParams params = v.getLayoutParams();
        if (params == null)
            params = new LayoutParams(0, 0);
        if (w != null)
            params.width = w.intValue();
        if (h != null)
            params.height = h.intValue();
        v.setLayoutParams(params);
    }

    //设置线性布局中的控件大小
    public static void size(View v, int w, int h, float weight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h, weight);
        v.setLayoutParams(params);
    }

    //获取Activity的根节点View
    public static View getRootView(Activity activity) {
        return activity.findViewById(android.R.id.content);
    }

    //获取控件文本
    public static String getText(TextView tv) {
        String text = tv.getText().toString();
        return text;
    }

    //获取控件文本
    public static String getText(Spinner spinner) {
        String text = spinner.getSelectedItem().toString();
        return text;
    }

    //获取控件文本
    public static String getText(TextView tv, boolean trim, boolean ignoreCase) {
        String text = tv.getText().toString();
        if (trim)
            text = text.trim();
        if (ignoreCase)
            text = text.toLowerCase();
        return text;
    }

    //获取控件中的整数
    public static Integer getInt(TextView textView, Integer defaultValue) {
        String text = textView.getText().toString().trim().toLowerCase();
        if (text.isEmpty())
            return defaultValue;
        else
            return Integer.valueOf(text);
    }

    //获取控件中的小数
    public static Double getDouble(TextView textView, Double defaultValue) {
        String text = textView.getText().toString().trim().toLowerCase();
        if (text.isEmpty())
            return defaultValue;
        else
            return Double.valueOf(text);
    }

    //获取控件中的布尔值
    public static Boolean getBool(TextView textView, Boolean defaultValue) {
        String text = textView.getText().toString().trim().toLowerCase();
        if (text.isEmpty())
            return defaultValue;
        else
            return Boolean.valueOf(text);
    }

    //移除编辑框默认的双击事件
    public static void removeDoubleClickEvent(EditText et) {
        et.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });
    }

    //设置控件文本
    public static void text(TextView v, Object data) {
        if (data == null)
            v.setText("");
        else
            v.setText(data.toString());
    }

    //平移控件
    public static void translate(View v, int dx, int dy) {
        v.offsetLeftAndRight(dx);
        v.offsetTopAndBottom(dy);
    }

    //获取子控件
    public static LinkedList<View> getAllChildViews(Activity ctx) {
        LinkedList<View> list = getAllChildViews(ctx.getWindow().getDecorView(), true);
        return list;
    }

    //获取子控件
    public static LinkedList<View> getAllChildViews(View view, boolean recurse) {
        LinkedList<View> children = new LinkedList();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                children.add(child);
                if (recurse)
                    children.addAll(getAllChildViews(child, recurse));
            }
        }
        return children;
    }
}
