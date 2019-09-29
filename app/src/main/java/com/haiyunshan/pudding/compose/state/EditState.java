package com.haiyunshan.pudding.compose.state;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.utils.SoftInputUtils;

public class EditState extends BaseState implements Toolbar.OnMenuItemClickListener {

    EditState(ComposeFragment parent) {
        super(StateMachine.EDIT, parent);
    }

    @Override
    public void onEnter() {
        super.onEnter();

        {
            mToolbar.getMenu().clear();
            mToolbar.inflateMenu(R.menu.menu_compose_edit);

            mToolbar.setNavigationIcon(R.drawable.ic_done_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mParent.closeEdit();

                }
            });

            mToolbar.setOnMenuItemClickListener(this);
        }

        {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SoftInputUtils.show(mContext);
                }
            }, 100);

        }

        {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onExit() {

        {
            SoftInputUtils.hide(mContext);
        }

        super.onExit();
    }

    public boolean onBackPressed() {
        mParent.closeEdit();

        return true;

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_take_photo: {
                mParent.takePhoto();

                break;
            }
            case R.id.menu_picture: {
                mParent.selectPhoto();
                break;
            }
        }

        return true;
    }
}
