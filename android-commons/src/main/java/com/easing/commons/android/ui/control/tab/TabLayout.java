package com.easing.commons.android.ui.control.tab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


public class TabLayout extends LinearLayout {

    protected OnSelected onSelected;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrSet) {
        super.setOrientation(LinearLayout.HORIZONTAL);
    }

    public void select(int pos) {
        View v = super.getChildAt(pos);
        v.performClick();
    }

    public void onSelected(OnSelected listener) {
        this.onSelected = listener;
    }

    public interface OnSelected {
        void onSelect(View v, int pos);
    }
}
