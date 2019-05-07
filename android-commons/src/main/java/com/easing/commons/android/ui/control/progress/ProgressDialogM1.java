package com.easing.commons.android.ui.control.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.easing.commons.android.R;

public class ProgressDialogM1 extends ProgressBar {
    public ProgressDialogM1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        customStyle();
    }

    public ProgressDialogM1(Context context, AttributeSet attrs) {
        super(context, attrs);
        customStyle();
    }

    public ProgressDialogM1(Context context) {
        super(context);
        customStyle();
    }

    private void customStyle() {
        super.setIndeterminateDrawable(super.getResources().getDrawable(R.drawable.progress_m01, null));
    }
}
