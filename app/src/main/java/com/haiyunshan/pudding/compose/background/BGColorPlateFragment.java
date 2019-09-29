package com.haiyunshan.pudding.compose.background;

import android.graphics.Color;

import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.ColorPlateFragment;
import com.haiyunshan.pudding.compose.event.FormatBackgroundEvent;

import org.greenrobot.eventbus.EventBus;

public class BGColorPlateFragment extends ColorPlateFragment {

    @Override
    protected void notifyColorChanged(int oldColor, int newColor) {

        int fgColor = Color.BLACK;
        int bgColor = newColor;

        // 采用算法来指定文本色
        if (bgColor != Color.TRANSPARENT) {
            fgColor = BackgroundManager.getForeground(bgColor);
        }

        FormatBackgroundEvent event = new FormatBackgroundEvent(FormatBackgroundEvent.SRC_PLATE, fgColor, bgColor);
        EventBus.getDefault().post(event);
    }
}
