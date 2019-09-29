package com.haiyunshan.pudding.compose;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.net.margaritov.preference.colorpicker.ColorPickerView;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.compose.event.FormatColorEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorPickerFragment extends Fragment implements ColorPickerView.OnColorChangedListener {

    ColorPickerView mPickerView;

    int mColor;

    public ColorPickerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_color_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mPickerView = view.findViewById(R.id.color_picker_view);
        mPickerView.setOnColorChangedListener(this);
    }

    public void setColor(int color) {
        this.mColor = color;

        mPickerView.setColor(color, false);
    }

    public boolean hasColor(int color) {
        return true;
    }

    @Override
    public void onColorChanged(int color) {
        if (this.mColor != color) {
            int oldColor = mColor;
            this.mColor = color;

            notifyColorChanged(oldColor, this.mColor);
        }
    }

    protected void notifyColorChanged(int oldColor, int newColor) {
        EventBus.getDefault().post(new FormatColorEvent(FormatColorEvent.SRC_PICKER, newColor));
    }
}
