package com.VitaBit.VitaBit.logic.UI.personal.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;


public class SelectPicPopupWindow extends PopupWindow {

	private Button takePhotoBtn, pickPhotoBtn, cancelBtn;
	private View mMenuView;

	@SuppressLint("InflateParams")
	public SelectPicPopupWindow(Context context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(com.VitaBit.VitaBit.R.layout.layout_popupwindow_photo, null);
		takePhotoBtn = (Button) mMenuView.findViewById(com.VitaBit.VitaBit.R.id.takePhotoBtn);
		pickPhotoBtn = (Button) mMenuView.findViewById(com.VitaBit.VitaBit.R.id.pickPhotoBtn);
		cancelBtn = (Button) mMenuView.findViewById(com.VitaBit.VitaBit.R.id.cancelBtn);

		cancelBtn.setOnClickListener(itemsOnClick);
		pickPhotoBtn.setOnClickListener(itemsOnClick);
		takePhotoBtn.setOnClickListener(itemsOnClick);
		

		this.setContentView(mMenuView);

		this.setWidth(LayoutParams.MATCH_PARENT);

		this.setHeight(LayoutParams.WRAP_CONTENT);

		this.setFocusable(true);

		this.setAnimationStyle(com.VitaBit.VitaBit.R.style.PopupAnimation);

		ColorDrawable dw = new ColorDrawable(0x80000000);

		this.setBackgroundDrawable(dw);

		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(com.VitaBit.VitaBit.R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

}
