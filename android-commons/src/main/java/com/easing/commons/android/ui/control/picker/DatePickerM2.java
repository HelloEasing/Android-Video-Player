package com.easing.commons.android.ui.control.picker;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatButton;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.format.TimeUtil;
import com.easing.commons.android.value.time.YMD;
import com.easing.commons.android.manager.DimenUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lombok.Setter;

//用Calendar管理日期，兼容低版本系统
public class DatePickerM2 {
	
	private TextView parent;
	private Context ctx;
	private Button selectedItem;
	
	@Setter
	private Calendar date;
	private Calendar popDate;
	
	private static final String[] WEEK_TEXTS = {"日", "一", "二", "三", "四", "五", "六"};
	private int startWeek = 1;
	
	@Setter
	private Settings settings;
	@Setter
	private String title;
	@Setter
	private int itemWidth = 50;
	@Setter
	private int itemHeight = 40;
	@Setter
	private int titleFontSize = 16;
	@Setter
	private int itemFontSize = 16;
	
	private static final Settings DEFAULT_SETTING = new Settings() {
		@Override
		public Calendar parentTextToDate(String text) {
			try {
				int y = Integer.valueOf(text.substring(0, 4));
				int m = Integer.valueOf(text.substring(5, 7));
				int d = Integer.valueOf(text.substring(8, 10));
				Calendar calendar = Calendar.getInstance();
				calendar.set(y, m, d);
				return calendar;
			} catch (Exception e) {
				return Calendar.getInstance();
			}
		}
		
		@Override
		public String dateToParentText(Calendar date) {
			return new SimpleDateFormat("yyyy年MM月dd日").format(date.getTime());
		}
		
		@Override
		public String dateToPopupText(Calendar date) {
			return new SimpleDateFormat("yyyy年MM月").format(date.getTime());
		}
		
		@Override
		public void paint(Button item, boolean isTitle, boolean isEnabled, boolean isPressed, boolean isSelected, YMD ymd) {
			Context ctx = item.getContext();
			
			if (isTitle) {
				item.setBackgroundResource(R.color.color_transparent);
				item.setTextColor(ctx.getResources().getColor(R.color.color_light_blue));
				return;
			}
			
			if (!isEnabled) {
				item.setBackgroundResource(R.color.color_transparent);
				item.setTextColor(ctx.getResources().getColor(R.color.color_black_40));
				return;
			}
			
			if (isSelected) {
				item.setBackgroundResource(R.color.color_light_blue);
				item.setTextColor(ctx.getResources().getColor(R.color.color_white));
				return;
			}
			
			YMD nowYmd = TimeUtil.getYmd();
			if (ymd.year == nowYmd.year)
				if (ymd.month == nowYmd.month)
					if (ymd.day == nowYmd.day) {
						item.setBackgroundResource(R.color.color_transparent);
						item.setTextColor(ctx.getResources().getColor(R.color.color_orange));
						return;
					}
			
			item.setBackgroundResource(R.color.color_transparent);
			item.setTextColor(ctx.getResources().getColor(R.color.color_black_70));
		}
		
		@Override
		public void onClickEvent(Calendar date, YMD ymd) {
		}
	};
	
	private DatePickerM2() {
	}
	
	public static DatePickerM2 bind(TextView parent, Settings settings) {
		DatePickerM2 picker = new DatePickerM2();
		picker.parent = parent;
		picker.ctx = parent.getContext();
		picker.settings = settings;
		picker.parent.setOnClickListener((v) -> picker.pick());
		picker.parent.setFocusable(false);
		picker.date = DEFAULT_SETTING.parentTextToDate(parent.getText().toString());
		if (settings != null)
			if (settings.parentTextToDate(parent.getText().toString()) != null)
				picker.date = settings.parentTextToDate(parent.getText().toString());
		return picker;
	}
	
	public void pick() {
		if (settings != null)
			settings.onShowEvent();
		
		//清空旧的状态
		selectedItem = null;
		
		//设置弹窗的起始时间
		popDate = TimeUtil.copy(date);
		if (settings != null)
			if (settings.parentTextToDate(parent.getText().toString()) != null)
				popDate = settings.parentTextToDate(parent.getText().toString());
		
		
		//创建弹窗
		View popRoot = LayoutInflater.from(ctx).inflate(R.layout.layout_date_picker_dialog, null);
		PopupWindow dialog = new PopupWindow(popRoot, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		dialog.setFocusable(true);
		dialog.setTouchable(true);
		dialog.setOutsideTouchable(true);
		dialog.showAtLocation(parent, Gravity.CENTER, 0, 0);
		
		//解析View
		GridLayout calendarLayout = popRoot.findViewById(R.id.group_calendar);
		TextView titleText = popRoot.findViewById(R.id.text_title);
		TextView popDateButton = popRoot.findViewById(R.id.bt_date);
		ImageView popRreButton = popRoot.findViewById(R.id.bt_pre);
		ImageView popNextButton = popRoot.findViewById(R.id.bt_next);
		Button okButton = popRoot.findViewById(R.id.bt_ok);
		
		//显示标题
		if (title != null)
			titleText.setText(title);
		
		//显示初始时间
		popDateButton.setText(DEFAULT_SETTING.dateToPopupText(popDate));
		if (settings != null)
			if (settings.dateToPopupText(popDate) != null)
				popDateButton.setText(settings.dateToPopupText(popDate));
		
		//在CalendarLayout中创建空白Item
		createItems(calendarLayout);
		//根据日期更新数据
		repaint(calendarLayout);
		
		//月份减小事件
		popRreButton.setOnClickListener((pv) -> {
			if (selectedItem != null)
				selectedItem.setSelected(false);
			popDate.add(Calendar.MONTH, -1);
			popDateButton.setText(new SimpleDateFormat("yyyy年MM月").format(popDate.getTime()));
			if (settings != null)
				if (settings.dateToPopupText(popDate) != null)
					popDateButton.setText(settings.dateToPopupText(popDate));
			repaint(calendarLayout);
		});
		
		//月份增加事件
		popNextButton.setOnClickListener((pv) -> {
			if (selectedItem != null)
				selectedItem.setSelected(false);
			popDate.add(Calendar.MONTH, 1);
			popDateButton.setText(new SimpleDateFormat("yyyy年MM月").format(popDate.getTime()));
			if (settings != null)
				if (settings.dateToPopupText(popDate) != null)
					popDateButton.setText(settings.dateToPopupText(popDate));
			repaint(calendarLayout);
		});
		
		//确认事件
		okButton.setOnClickListener((pv) -> {
			if (selectedItem != null) {
				YMD ymd = (YMD) selectedItem.getTag();
				date = TimeUtil.getCalendar(ymd.year, ymd.month - 1, ymd.day);
				parent.setText(DEFAULT_SETTING.dateToParentText(date));
				if (settings != null)
					if (settings.dateToParentText(date) != null)
						parent.setText(settings.dateToParentText(date));
				if (settings != null)
					settings.onClickEvent(date, ymd);
			}
			dialog.dismiss();
		});
	}
	
	//创建Items
	private void createItems(GridLayout calendarLayout) {
		//添加标题栏
		for (int i = 0; i < 7; i++) {
			int week = startWeek + i;
			if (week >= 7)
				week = week - 7;
			Button item = createItem(calendarLayout, true);
			item.setText(WEEK_TEXTS[week]);
		}
		//添加日期Item
		for (int i = 7; i < 49; i++) {
			Button item = createItem(calendarLayout, false);
		}
	}
	
	//创建Item
	private Button createItem(GridLayout calendarLayout, boolean isTitle) {
		//重写绘制方法
		//将绘制方法抽离成回调，让用户自己注入，实现个性化定制
		//固定属性要一次性设置，不要放在onDraw方法里，否则影响性能
		//修复BUG：去除按钮默认的Material样式
		Button item = new AppCompatButton(ctx, null, R.attr.borderlessButtonStyle) {
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				boolean isEnabled = super.isEnabled();
				boolean isPressed = super.isPressed();
				boolean isSelected = super.isSelected();
				YMD ymd = isTitle ? null : (YMD) getTag();
				DEFAULT_SETTING.paint(this, isTitle, isEnabled, isPressed, isSelected, ymd);
				if (settings != null)
					settings.paint(this, isTitle, isEnabled, isPressed, isSelected, ymd);
			}
		};
		//设置布局，添加到容器
		calendarLayout.addView(item);
		ViewGroup.LayoutParams lp = item.getLayoutParams();
		lp.width = DimenUtil.toPx(ctx, itemWidth);
		lp.height = DimenUtil.toPx(ctx, itemHeight);
		item.setLayoutParams(lp);
		//设置对齐方式
		item.setGravity(Gravity.CENTER);
		//设置字体大小
		if (isTitle)
			item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleFontSize);
		else
			item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, itemFontSize);
		//设置字体颜色
		if (isTitle)
			item.setTextColor(ctx.getResources().getColor(R.color.color_blue_70));
		else
			item.setTextColor(ctx.getResources().getColor(R.color.color_black_70));
		//标题栏禁止点击
		if (isTitle)
			item.setEnabled(false);
		//设置监听器
		item.setOnTouchListener((v, me) -> {
			if (me.getAction() == MotionEvent.ACTION_DOWN) {
				if (selectedItem != null)
					selectedItem.setSelected(false);
				item.setSelected(true);
				selectedItem = item;
			}
			return false;
		});
		return item;
	}
	
	private void repaint(GridLayout calendarLayout) {
		Calendar tempDate = TimeUtil.getCalendar(popDate.get(Calendar.YEAR), popDate.get(Calendar.MONTH), 1);
		int currentMonth = popDate.get(Calendar.MONTH);
		//将tempDate设置到第一天的前一天
		int week = tempDate.get(Calendar.DAY_OF_WEEK) - 1;
		if (week > startWeek)
			tempDate.add(Calendar.DATE, -(week - startWeek + 1));
		else
			tempDate.add(Calendar.DATE, -(week + 7 - startWeek + 1));
		//逐个设置日期和状态
		for (int i = 7; i < 49; i++) {
			Button item = (Button) calendarLayout.getChildAt(i);
			tempDate.add(Calendar.DATE, 1);
			item.setEnabled(tempDate.get(Calendar.MONTH) == currentMonth);
			item.setText(tempDate.get(Calendar.DATE) + "");
			item.setTag(YMD.create(tempDate.get(Calendar.YEAR), tempDate.get(Calendar.MONTH) + 1, tempDate.get(Calendar.DATE)));
		}
	}
	
	//增加月
	public Calendar increaseMonth(int months) {
		date.add(Calendar.MONTH, months);
		date.set(Calendar.DATE, 1);
		parent.setText(DEFAULT_SETTING.dateToParentText(date));
		if (settings != null)
			if (settings.dateToParentText(date) != null)
				parent.setText(settings.dateToParentText(date));
		return date;
	}
	
	//减少月
	public Calendar decreaseMonth(int months) {
		date.add(Calendar.MONTH, -months);
		date.set(Calendar.DATE, 1);
		if (settings != null)
			if (settings.dateToParentText(date) != null)
				parent.setText(settings.dateToParentText(date));
		return date;
	}
	
	public void unbind() {
		this.parent.setOnClickListener(null);
		this.parent = null;
		this.ctx = null;
	}
	
	public interface Settings {
		default Calendar parentTextToDate(String text) {
			return null;
		}
		
		default String dateToParentText(Calendar date) {
			return null;
		}
		
		default String dateToPopupText(Calendar date) {
			return null;
		}
		
		default void paint(Button item, boolean isTitle, boolean isEnabled, boolean isPressed, boolean isSelected, YMD ymd) {
		}
		
		default void onClickEvent(Calendar date, YMD ymd) {
		}
		
		default void onShowEvent() {
		}
	}
}
