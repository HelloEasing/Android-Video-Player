package com.easing.commons.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;

import com.easing.commons.android.ui.adapter.ImageAdapter.Holder;
import com.easing.commons.android.R;
import com.easing.commons.android.view.ViewManager;

import java.util.List;

public class ImageAdapter extends RecyclerAdapter<Integer, Holder> {
  
  public ImageAdapter(RecyclerView recyclerView) {
    super(recyclerView, R.layout.item_image_list);
  }
  
  public ImageAdapter(RecyclerView recyclerView, List<Integer> datas) {
    super(recyclerView, R.layout.item_image_list, datas);
  }
  
  @Override
  public Holder createHolder(View root) {
    ImageAdapter.Holder holder = new ImageAdapter.Holder(root);
    root.setTag(holder);
    return holder;
  }
  
  @Override
  public void bindHolder(Holder holder, Integer resId) {
    holder.iv.setImageResource(resId);
  }
  
  public static class Holder extends ViewHolder {
    public ImageView iv;
    
    public Holder(View root) {
      super(root);
      iv = ViewManager.findView(root, R.id.iv);
    }
  }
  
}
