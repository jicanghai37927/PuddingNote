package com.haiyunshan.pudding.compose.state;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.compose.BottomSheetFragment;
import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.compose.FontFragment;
import com.haiyunshan.pudding.compose.TextFormatFragment;
import com.haiyunshan.pudding.compose.format.TextFormat;
import com.haiyunshan.pudding.font.dataset.FontEntry;
import com.haiyunshan.pudding.font.dataset.FontManager;

public class FormatState extends BaseState implements Toolbar.OnMenuItemClickListener {

    FormatState(ComposeFragment parent) {
        super(StateMachine.FORMAT, parent);
    }

    @Override
    public void onEnter() {
        super.onEnter();

        {
            mToolbar.getMenu().clear();
            mToolbar.inflateMenu(R.menu.menu_compose_format);

            mToolbar.setNavigationIcon(R.drawable.ic_done_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mParent.closeFormat();
                }
            });

            mToolbar.setOnMenuItemClickListener(this);
        }

        {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)(mRecyclerView.getLayoutParams());
            params.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.composer_bottom_sheet_size);

            mToolbar.setVisibility(View.GONE);

            BottomSheetFragment f = mParent.getBottomSheet();

            Fragment target = createTextFormatFragment();
            String tag = "text_format";

            f.setContent(target, tag);

            FragmentTransaction t = mParent.getChildFragmentManager().beginTransaction();
            t.show(mParent.getBottomSheet());
            t.commit();

            mParent.getView().findViewById(R.id.composer_bottom_bar_container).setVisibility(View.GONE);
        }

        {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onExit() {
        super.onExit();
    }

    @Override
    public boolean onBackPressed() {
        if (mBottomSheet.onBackPressed()) {
            return true;
        }

        return mParent.closeFormat();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {

            default: {
                break;
            }

        }

        return false;
    }

    FontFragment createFontFragment() {
        FontFragment target = new FontFragment();
        {
            TextFormat format = mDocument.getFormat().getParagraph();
            FontEntry entry = FontManager.getInstance().obtain(format.getFont());
            String font = (entry == null)? "": entry.getId();
            target.setArguments(font);
        }

        return target;
    }

    TextFormatFragment createTextFormatFragment() {
        TextFormatFragment target = new TextFormatFragment();

        return target;
    }
}
