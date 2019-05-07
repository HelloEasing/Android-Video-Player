package com.easing.commons.android.ui.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class CommonFragment extends Fragment {

  public CommonActivity ctx;
  public Handler handler;
  protected View root;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    ctx = (CommonActivity) getActivity();
    handler = ctx.handler;
    if (root != null) {
      ViewGroup parent = (ViewGroup) root.getParent();
      if (parent != null)
        parent.removeView(root);
    } else {
      root = inflateView();
      createView();
    }
    return root;
  }

  public abstract View inflateView();

  public abstract void createView();
}
