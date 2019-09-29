package com.haiyunshan.pudding.compose.background;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.compose.BottomSheetFragment;
import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.compose.event.FormatBackgroundEvent;
import com.haiyunshan.pudding.compose.event.FormatCompleteEvent;
import com.haiyunshan.pudding.widget.FormatTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomBGColorFragment extends Fragment implements View.OnClickListener, FormatTitleBar.OnButtonClickListener, RadioGroup.OnCheckedChangeListener {

    FormatTitleBar mTitleBar;
    RadioGroup mColorGroup;

    BGColorUsualFragment mUsualFragment;
    BGColorPlateFragment mPlateFragment;
    BGColorPickerFragment mPickerFragment;

    BottomSheetFragment mBottomSheetFragment;

    int mColor;

    public CustomBGColorFragment() {
        this.mColor = Color.TRANSPARENT;
    }

    public void setArguments(int color) {
        Bundle args = new Bundle();
        args.putInt("color", color);

        super.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_bgcolor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mTitleBar = view.findViewById(R.id.title_bar);
            mTitleBar.setTitle(R.string.background_color_title);
            mTitleBar.setEditable(false);
            mTitleBar.setBackable(true);
            mTitleBar.setOnButtonClickListener(this);
        }

        {
            this.mColorGroup = view.findViewById(R.id.rg_color);
            mColorGroup.setOnCheckedChangeListener(this);
        }

        {
            FragmentManager fm = this.getChildFragmentManager();
            this.mUsualFragment = (BGColorUsualFragment)(fm.findFragmentByTag("color_usual"));
            this.mPlateFragment = (BGColorPlateFragment) (fm.findFragmentByTag("color_plate"));
            this.mPickerFragment = (BGColorPickerFragment)(fm.findFragmentByTag("color_picker"));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            if (args != null) {
                this.mColor = args.getInt("color", Color.TRANSPARENT);
            }
        }

        {
            mUsualFragment.setColor(mColor);
            mPlateFragment.setColor(mColor);
            mPickerFragment.setColor(mColor);
        }

        {
            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.hide(mPlateFragment);
            t.hide(mPickerFragment);

            t.show(mUsualFragment);

            t.commit();
        }

        if (mUsualFragment.hasColor(mColor)) {
            mColorGroup.check(R.id.rb_usual);
        } else if (mPlateFragment.hasColor(mColor)) {
            mColorGroup.check(R.id.rb_plate);
        } else {
            mColorGroup.check(R.id.rb_picker);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        {
            Fragment p = this.getParentFragment();
            while (p != null) {

                if (p instanceof BottomSheetFragment) {
                    this.mBottomSheetFragment = (BottomSheetFragment)p;
                }

                if (p instanceof ComposeFragment) {

                }

                p = p.getParentFragment();
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onButtonClick(FormatTitleBar bar, int which) {
        switch (which) {
            case FormatTitleBar.BUTTON_BACK: {
                getActivity().onBackPressed();

                break;
            }
            case FormatTitleBar.BUTTON_CLOSE: {

                EventBus.getDefault().post(new FormatCompleteEvent());

                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_usual) {
            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.hide(mPlateFragment);
            t.hide(mPickerFragment);

            t.show(mUsualFragment);

            t.commit();
        } else if (checkedId == R.id.rb_plate) {
            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.hide(mUsualFragment);
            t.hide(mPickerFragment);

            t.show(mPlateFragment);

            t.commit();
        } else if (checkedId == R.id.rb_picker) {
            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.hide(mUsualFragment);
            t.hide(mPlateFragment);

            t.show(mPickerFragment);

            t.commit();
        }

    }

    @Subscribe
    public void onBackgroundEvent(FormatBackgroundEvent event) {
        int color = event.mBGColor;
        this.mColor = color;

        String source = event.mSource;
        if (source.equals(FormatBackgroundEvent.SRC_USUAL)) {
            mPlateFragment.setColor(color);
            mPickerFragment.setColor(color);
        } else if (source.equals(FormatBackgroundEvent.SRC_PLATE)) {
            mUsualFragment.setColor(color);
            mPickerFragment.setColor(color);
        } else if (source.equals(FormatBackgroundEvent.SRC_PICKER)) {
            mUsualFragment.setColor(color);
            mPlateFragment.setColor(color);
        }

    }

}
