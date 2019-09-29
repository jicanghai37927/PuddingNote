package com.haiyunshan.pudding.compose.state;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.compose.ComposeFragment;

public class ReadState extends BaseState implements Toolbar.OnMenuItemClickListener {

    ReadState(ComposeFragment parent) {
        super(StateMachine.READ, parent);
    }

    @Override
    public void onEnter() {
        super.onEnter();

        {
            mToolbar.getMenu().clear();
            mToolbar.inflateMenu(R.menu.menu_compose_read);

            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.onBackPressed();
                }
            });

            mToolbar.setOnMenuItemClickListener(this);
        }

        {
            {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)(mRecyclerView.getLayoutParams());
//                params.topMargin = getIntAttr(mContext, R.attr.actionBarSize);
//                params.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.composer_bottom_bar_size);
                params.bottomMargin = 0;

                mToolbar.setVisibility(View.VISIBLE);

                FragmentTransaction t = mParent.getChildFragmentManager().beginTransaction();
                t.hide(mParent.getBottomSheet());
                t.commit();

                mParent.getView().findViewById(R.id.composer_bottom_bar_container).setVisibility(View.GONE);
            }
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
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_edit: {
                mParent.startEdit();
                break;
            }
            case R.id.menu_format: {
                mParent.startFormat();
                break;
            }
            case R.id.menu_more: {
                mParent.startMore();
                break;
            }
            case R.id.menu_share: {
                mParent.startShare();
                break;
            }

        }

        return false;
    }

    int getIntAttr(Context context, int attr) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, outValue, true);
        int data = outValue.data;

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        data = (int)outValue.getDimension(outMetrics);

        return data;
    }
}
