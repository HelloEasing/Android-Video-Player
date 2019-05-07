package com.easing.commons.android.ui.control.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class LayoutPlaceholder extends View {

    public LayoutPlaceholder(Context context) {
        this(context, null, 0);
    }

    public LayoutPlaceholder(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayoutPlaceholder(Context context, AttributeSet attrSet, int style) {
        super(context, attrSet, style);
        init(context, attrSet);
    }

    private void init(Context context, AttributeSet attrSet) {
    }
}
