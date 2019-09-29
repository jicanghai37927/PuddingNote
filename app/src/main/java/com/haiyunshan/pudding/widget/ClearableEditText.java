package com.haiyunshan.pudding.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 可清除编辑框控件
 *
 */
public class ClearableEditText extends android.support.v7.widget.AppCompatEditText {

	private boolean mIsEmpty = true;
	
	private Drawable mDrawableLeft;
	private Drawable mDrawableRight;

	/**
	 * @param paramContext
	 * @param paramAttributeSet
	 */
	public ClearableEditText(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		
		/* 获取左右Drawable，并只显示left drawable */
		this.mDrawableLeft = getCompoundDrawables()[0];
		this.mDrawableRight = getCompoundDrawables()[2];
		
		if (this.mDrawableRight != null) {
			setCompoundDrawables(mDrawableLeft, null, null, null);
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View#dispatchTouchEvent(android.view.MotionEvent)
	 */
	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
		
		if ((this.mDrawableRight != null) && (paramMotionEvent.getAction() == 1)) {
			
//			int paddingRight = getPaddingRight(); 
			int comPaddingRight = getCompoundPaddingRight(); 
//			int drawableWidth = mDrawableRight.getBounds().width(); 
			
			float f1 = paramMotionEvent.getX();
			float f2 = getWidth() - comPaddingRight;
			if (f1 > f2) { // 判断是否点击右drawable
				setText(null);
			}
		}
		
		return super.dispatchTouchEvent(paramMotionEvent);
	}

	/* (non-Javadoc)
	 * @see android.widget.TextView#onPreDraw()
	 */
	public boolean onPreDraw() {
		boolean isEmpty = TextUtils.isEmpty(getText());
		
		if (this.mIsEmpty != isEmpty) { // 防止重复设置
			this.mIsEmpty = isEmpty;
			
			if (this.mIsEmpty) {
				Drawable localDrawable1 = this.mDrawableLeft;
				setCompoundDrawables(localDrawable1, null, null, null);
			} else {
				setCompoundDrawables(this.mDrawableLeft, null, this.mDrawableRight, null);
			}
		}

		return super.onPreDraw(); 
	}
}
