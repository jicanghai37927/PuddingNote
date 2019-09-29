package com.haiyunshan.pudding.compose;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.compose.event.FormatColorEvent;
import com.haiyunshan.pudding.widget.ColorPlateView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorPlateFragment extends Fragment implements ColorPlateView.OnColorPlateListener {

    @BindView(R.id.color_plate_view)
    ColorPlateView mPlateView;

    int mColor;

    Unbinder mUnbinder;

    public ColorPlateFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color_plate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mUnbinder = ButterKnife.bind(this, view);
        }

        {
            this.mPlateView.setOnColorPlateListener(this);
        }

    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();

        super.onDestroyView();
    }

    public void setColor(int color) {
        this.mColor = color;

        mPlateView.setColor(color);
    }

    public boolean hasColor(int color) {
        return mPlateView.hasColor(color);
    }

    @Override
    public void onColorChanged(ColorPlateView view, int color) {

        // 通知颜色发生变化
        if (this.mColor != color) {
            int oldColor = this.mColor;
            this.mColor = color;

            notifyColorChanged(oldColor, this.mColor);
        }
    }

    protected void notifyColorChanged(int oldColor, int newColor) {
        EventBus.getDefault().post(new FormatColorEvent(FormatColorEvent.SRC_PLATE, newColor));
    }
}
