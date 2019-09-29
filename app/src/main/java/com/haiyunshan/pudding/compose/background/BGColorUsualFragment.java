package com.haiyunshan.pudding.compose.background;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.ColorUsualFragment;
import com.haiyunshan.pudding.compose.event.FormatBackgroundEvent;

import org.greenrobot.eventbus.EventBus;

public class BGColorUsualFragment extends ColorUsualFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            String text = getString(R.string.background_default);
            this.setNoColor(true, text);
        }

    }

    @Override
    protected void notifyColorChanged(int oldColor, int newColor) {

        int fgColor = Color.BLACK;
        int bgColor = newColor;

        // 采用算法来指定文本色
        if (bgColor != Color.TRANSPARENT) {
            fgColor = BackgroundManager.getForeground(bgColor);
        }

        FormatBackgroundEvent event = new FormatBackgroundEvent(FormatBackgroundEvent.SRC_USUAL, fgColor, bgColor);
        EventBus.getDefault().post(event);
    }
}
