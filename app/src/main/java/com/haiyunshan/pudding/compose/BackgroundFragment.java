package com.haiyunshan.pudding.compose;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.compose.event.FormatBackgroundEvent;
import com.haiyunshan.pudding.compose.event.FormatCompleteEvent;
import com.haiyunshan.pudding.compose.format.TextFormat;
import com.haiyunshan.pudding.widget.FormatTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 */
public class BackgroundFragment extends Fragment implements FormatTitleBar.OnButtonClickListener, RadioGroup.OnCheckedChangeListener {

    FormatTitleBar mTitleBar;
    RadioGroup mColorGroup;

    BGColorFragment mColorFragment;
    BGTextureFragment mTextureFragment;

    TextFormat mTextFormat;

    String mId;
    int mFGColor;
    int mBGColor;

    public BackgroundFragment() {
        this.mTextFormat = null;

        this.mId = "";
        this.mFGColor = Color.TRANSPARENT;
        this.mBGColor = Color.TRANSPARENT;
    }

    public void setArguments(String textureId, int fgColor, int bgColor) {
        Bundle args = new Bundle();

        args.putString("textureId", textureId);
        args.putInt("fgColor", fgColor);
        args.putInt("bgColor", bgColor);

        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_background, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mTitleBar = view.findViewById(R.id.title_bar);
            mTitleBar.setTitle(R.string.background_title);
            mTitleBar.setEditable(false);
            mTitleBar.setBackable(true);
            mTitleBar.setOnButtonClickListener(this);
        }

        {
            this.mColorGroup = view.findViewById(R.id.rg_background);
            mColorGroup.setOnCheckedChangeListener(this);
        }

        {
            FragmentManager fm = getChildFragmentManager();
            this.mColorFragment = (BGColorFragment)fm.findFragmentByTag("bg_color");
            this.mTextureFragment = (BGTextureFragment)fm.findFragmentByTag("bg_texture");
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            if (args != null) {
                this.mId = args.getString("textureId", "");
                this.mFGColor = args.getInt("fgColor", Color.TRANSPARENT);
                this.mBGColor = args.getInt("bgColor", Color.TRANSPARENT);

            }
        }

        if (mTextFormat != null) {
            TextFormat format = this.mTextFormat;

            this.mFGColor = format.getTextColor();
            this.mBGColor = format.getBackgroundColor();
            this.mId = format.getBackgroundTexture();
        }

        {
            mColorFragment.setColor(mId, mFGColor, mBGColor);
            mTextureFragment.setColor(mId, mFGColor, mBGColor);
        }

        {
            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.hide(mTextureFragment);

            t.show(mColorFragment);

            t.commit();
        }

        if (TextUtils.isEmpty(mId)) {
            mColorGroup.check(R.id.rb_color);
        } else {
            mColorGroup.check(R.id.rb_texture);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        {
            Fragment p = this.getParentFragment();
            while (p != null) {

                if (p instanceof BottomSheetFragment) {

                }

                if (p instanceof ComposeFragment) {
                    ComposeFragment f = (ComposeFragment)p;
                    TextFormat format = f.getDocument().getFormat().getParagraph();
                    this.mTextFormat = format;
                }

                p = p.getParentFragment();
            }
        }

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
        if (checkedId == R.id.rb_color) {

            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.hide(mTextureFragment);

            t.show(mColorFragment);

            t.commit();
        } else if (checkedId == R.id.rb_texture) {
            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.hide(mColorFragment);

            t.show(mTextureFragment);

            t.commit();
        }
    }

    @Subscribe
    public void onBackgroundEvent(FormatBackgroundEvent event) {

        String textureId = event.mTextureId;
        int fgColor = event.mFGColor;
        int bgColor = event.mBGColor;

        String source = event.mSource;
        if (source.equals(FormatBackgroundEvent.SRC_COLOR)) {
            mTextureFragment.setColor(textureId, fgColor, bgColor);
        } else if (source.equals(FormatBackgroundEvent.SRC_TEXTURE)) {
            mColorFragment.setColor(textureId, fgColor, bgColor);
        }

    }
}
