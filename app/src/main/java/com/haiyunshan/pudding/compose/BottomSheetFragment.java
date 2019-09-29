package com.haiyunshan.pudding.compose;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.haiyunshan.pudding.R;

public class BottomSheetFragment extends Fragment {

    FrameLayout mContainer;

    public BottomSheetFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_composer_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mContainer = view.findViewById(R.id.composer_bottom_sheet);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setContent(Fragment fragment, String tag) {
        FragmentManager fm = this.getChildFragmentManager();
        while (fm.getBackStackEntryCount() != 0) {
            fm.popBackStackImmediate();
        }

        FragmentTransaction t = fm.beginTransaction();
        t.replace(mContainer.getId(), fragment, tag);
        t.commit();
    }

    public void push(Fragment fragment, String tag) {
        FragmentManager fm = this.getChildFragmentManager();

        FragmentTransaction t = fm.beginTransaction();
        t.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);

        t.replace(mContainer.getId(), fragment, tag);
        t.addToBackStack(tag);

        t.commit();
    }

    public boolean onBackPressed() {
        FragmentManager fm = this.getChildFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            return false;
        }

        fm.popBackStack();
        return true;
    }

    public void pop() {
        FragmentManager fm = this.getChildFragmentManager();
        fm.popBackStack();
    }

    public void reset() {
        FragmentManager fm = this.getChildFragmentManager();
        while (fm.getBackStackEntryCount() != 0) {
            fm.popBackStackImmediate();
        }
    }
}
