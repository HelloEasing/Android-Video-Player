package com.easing.commons.android.ui.control.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class ScrollerM1<K, V extends View> extends HorizontalScrollView
{
  private Context context;
  private LinearLayout scroller;

  @Getter
  private final LinkedList<K> datas = new LinkedList();
  @Getter
  private final LinkedList<V> views = new LinkedList();

  private Map<K, V> data_view_map = new HashMap();
  private Map<V, K> view_data_map = new HashMap();

  @Getter
  private View leftPadder;
  @Getter
  private View rightPadder;

  @Setter
  private ViewMapper<K, V> viewMapper;

  public ScrollerM1(Context context)
  {
    super(context);
    customStyle(context, null);
  }

  public ScrollerM1(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    customStyle(context, attrs);
  }

  public ScrollerM1(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    customStyle(context, attrs);
  }

  private void customStyle(Context context, AttributeSet attrs)
  {
    this.context = context;
    this.leftPadder = new View(context);
    this.rightPadder = new View(context);
    this.scroller = new LinearLayout(context);
    this.scroller.setHorizontalGravity(Gravity.LEFT);
    this.scroller.setVerticalGravity(Gravity.CENTER);
    this.scroller.addView(leftPadder);
    this.scroller.addView(rightPadder);
    super.addView(scroller);
    super.setHorizontalScrollBarEnabled(false);
    super.setVerticalScrollBarEnabled(false);
    super.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
  }

  public void addData(K data)
  {
    if (this.datas.contains(data))
    {
      View view = this.data_view_map.get(data);
      this.datas.remove(data);
      this.views.remove(view);
      this.data_view_map.remove(data);
      this.view_data_map.remove(view);
      super.removeView(view);
    }

    V view = viewMapper.buildView(data);
    this.datas.add(data);
    this.views.add(view);
    this.data_view_map.put(data, view);
    this.view_data_map.put(view, data);
    this.scroller.addView(view, views.size());
  }

  public V dataToView(K data)
  {
    return data_view_map.get(data);
  }

  public K viewToData(V view)
  {
    return view_data_map.get(view);
  }

  public static interface ViewMapper<K, V extends View>
  {
    public V buildView(K data);
  }
}
