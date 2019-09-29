/**
 * 文件名	: WindowUtils.java
 * 作者		: 陈振磊
 * 创建日期	: 2016年10月22日
 * 版权    	:  
 * 描述    	: 
 * 修改历史	: 
 */

package com.haiyunshan.pudding.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;

import com.haiyunshan.pudding.App;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * 窗口工具类
 *
 */
public final class WindowUtils {

    static final String TAG = "WindowUtils";

	public static final float dp2px(float dpValue) {
		DisplayMetrics metrics = App.getInstance().getResources().getDisplayMetrics();
		float value = TypedValue.applyDimension(COMPLEX_UNIT_DIP, dpValue, metrics);
		return value;
	}

	public static final int getDisplayWidth() {
        DisplayMetrics metrics = App.getInstance().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

	/**
	 * 获取整个手机屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static final int getDisplayHeight(Activity context) {
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		
		return height;
	}

    public static final int getRealHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int height = metrics.heightPixels;

        return height;
    }

    public static final int getDecorBottom(Activity context) {

        Rect r = new Rect();
        View decorView = context.getWindow().getDecorView();
        decorView.getWindowVisibleDisplayFrame(r);

        return r.bottom;
    }

    /**
	 * 底部虚拟按键栏的高度
	 * 
	 * @param context
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static final int getSoftButtonsBarHeight(Activity context) {
		DisplayMetrics metrics = new DisplayMetrics();

		// 这个方法获取可能不是真实屏幕的高度
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int usableHeight = metrics.heightPixels;
		
		// 获取当前屏幕的真实高度
		context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
		int realHeight = metrics.heightPixels;

		int height = 0;
		if (realHeight > usableHeight) {
			height = realHeight - usableHeight;
		} else {
			height = 0;
		}

		return height;
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static final int getStatusBarHeight(Context context) {
		int height = getStatusHeight1(context); 
		if (height > 0) {
			return height; 
		}
		
		height = getStatusHeight2(context); 
		if (height > 0) {
			return height; 
		}
		
		return height; 
	}
	
	/**
	 * 获取状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static final int getStatusBarHeight(Activity context) {
		int height = getStatusHeight1(context); 
		if (height > 0) {
			return height; 
		}
		
		height = getStatusHeight2(context); 
		if (height > 0) {
			return height; 
		}
		
		height = getStatusHeight3(context); 
		if (height > 0) {
			return height; 
		}
		
		return height; 
	}
	
	static final int getStatusHeight2(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	static final int getStatusHeight1(Context context) {
	 
	    int statusHeight = 0;
	    try {
	        Class<?> clazz = Class.forName("com.android.internal.R$dimen");
	        Object object = clazz.newInstance();
	        int height = Integer.parseInt(clazz.getField("status_bar_height")
	                .get(object).toString());
	        statusHeight = context.getResources().getDimensionPixelSize(height);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return statusHeight;
	}
	
	static final int getStatusHeight3(Activity context) {
		Rect rectangle = new Rect();
		Window window = context.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
		int statusBarHeight = rectangle.top;
		
		return statusBarHeight; 
	}
	

}
