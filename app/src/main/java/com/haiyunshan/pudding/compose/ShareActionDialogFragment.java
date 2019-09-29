package com.haiyunshan.pudding.compose;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.utils.PackageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class ShareActionDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    public static final int ACTION_SAVE_PICTURE = 0x301;

    RecyclerView mRecyclerView;
    ActionAdapter mAdapter;

    RecyclerView mResolveListView;
    ResolveAdapter mResolveAdapter;

    View mCancelBtn;

    SharePriority mSharePriority;

    OnShareActionListener mOnShareActionListener;

    public ShareActionDialogFragment() {
        this.mSharePriority = new SharePriority();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_Activity_Dim);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dialog_share_action, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(layout);
        }

        {
            this.mResolveListView = view.findViewById(R.id.resolve_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mResolveListView.setLayoutManager(layout);
        }

        {
            this.mCancelBtn = view.findViewById(R.id.btn_cancel);
            mCancelBtn.setOnClickListener(this);

            view.setOnClickListener(this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        {
            this.mAdapter = new ActionAdapter(getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }

        {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_SEND);

            List<ResolveInfo> list = PackageUtils.queryActivities(getActivity(), intent);
            if (list == null || list.isEmpty()) {
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);

                list = PackageUtils.queryActivities(getActivity(), intent);
            }

            if (list == null || list.isEmpty()) {
                mResolveListView.setVisibility(View.GONE);

                getView().findViewById(R.id.line_separator_2).setVisibility(View.GONE);
            } else {
                this.mResolveAdapter = new ResolveAdapter(list);
                mResolveListView.setAdapter(mResolveAdapter);
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(View v) {
        if (v == this.getView()) {
            this.dismiss();
        } else if (v == mCancelBtn) {
            this.dismiss();
        }
    }

    public void setOnShareActionListener(OnShareActionListener listener) {
        this.mOnShareActionListener = listener;
    }

    /**
     *
     */
    private class ActionAdapter extends RecyclerView.Adapter {

        ArrayList<ActionItem> mList;

        public ActionAdapter(Activity context) {

            ArrayList<ActionItem> list = new ArrayList<>();

            {
                list.add(new ActionItem(ACTION_SAVE_PICTURE, getString(R.string.btn_save_pic), R.drawable.ic_save_picture));
            }

            this.mList = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int resource = R.layout.layout_share_action_item;

            View view = inflater.inflate(resource, parent, false);
            ActionHolder holder = new ActionHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            ActionHolder holder = (ActionHolder)h;

            ActionItem item = mList.get(position);
            holder.bind(position, item);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    /**
     *
     */
    private class ActionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mIconView;
        TextView mNameView;

        ActionItem mEntry;

        public ActionHolder(View itemView) {
            super(itemView);

            this.mIconView = itemView.findViewById(R.id.iv_icon);
            this.mNameView = itemView.findViewById(R.id.tv_name);

            mIconView.setBackgroundResource(R.drawable.shape_round_rect_white);
            mIconView.setElevation(1.6f);

            mIconView.setOnClickListener(this);
            mNameView.setOnClickListener(this);
        }

        void bind(int postiion, ActionItem item) {
            this.mEntry = item;

            mIconView.setImageResource(item.mIcon);
            mNameView.setText(item.mName);
        }

        @Override
        public void onClick(View v) {
            dismiss();

            if (mOnShareActionListener != null) {
                mOnShareActionListener.onAction(ShareActionDialogFragment.this, mEntry.mId);
            }
        }
    }

    /**
     *
     */
    private class ActionItem {

        int mId;
        CharSequence mName;
        int mIcon;

        ActionItem(int id, CharSequence name, int iconResid) {
            this.mId = id;
            this.mName = name;
            this.mIcon = iconResid;
        }
    }

    /**
     *
     */
    private class ResolveAdapter extends RecyclerView.Adapter {

        ArrayList<ResolveItem> mList;

        ResolveAdapter(List<ResolveInfo> list) {

            this.mList = new ArrayList<>(list.size());

            for (ResolveInfo entry : list) {
                mList.add(new ResolveItem(entry));
            }

            Collections.sort(mList, new Comparator<ResolveItem>() {
                @Override
                public int compare(ResolveItem o1, ResolveItem o2) {
                    return o1.getPriority() - o2.getPriority();
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int resource = R.layout.layout_share_action_item;

            View view = inflater.inflate(resource, parent, false);
            ResolveHolder holder = new ResolveHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            ResolveHolder holder = (ResolveHolder)h;

            ResolveItem item = mList.get(position);
            holder.bind(position, item);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    /**
     *
     */
    private class ResolveHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mIconView;
        TextView mNameView;

        ResolveItem mEntry;

        public ResolveHolder(View itemView) {
            super(itemView);

            this.mIconView = itemView.findViewById(R.id.iv_icon);
            this.mNameView = itemView.findViewById(R.id.tv_name);

            mIconView.setOnClickListener(this);
            mNameView.setOnClickListener(this);
        }

        void bind(int postiion, ResolveItem item) {
            mEntry = item;

            mIconView.setImageDrawable(item.getIcon(getActivity()));
            mNameView.setText(item.getName(getActivity()));
        }

        @Override
        public void onClick(View v) {
            dismiss();

            if (mOnShareActionListener != null) {
                mOnShareActionListener.onAction(ShareActionDialogFragment.this, mEntry.mEntry);
            }
        }
    }

    private class ResolveItem {

        CharSequence mName;
        Drawable mIcon;

        ResolveInfo mEntry;

        int mPriority;

        ResolveItem(ResolveInfo entry) {
            this.mEntry = entry;

            this.mName = null;
            this.mIcon = null;

            this.mPriority = -1;
        }

        CharSequence getName(Context context) {
            if (mName != null) {
                return mName;
            }

            PackageManager pm = context.getPackageManager();
            this.mName = mEntry.loadLabel(pm);

            return mName;
        }

        Drawable getIcon(Context context) {
            if (mIcon != null) {
                return mIcon;
            }

            PackageManager pm = context.getPackageManager();
            this.mIcon = mEntry.loadIcon(pm);

            return mIcon;
        }

        int getPriority() {
            if (mPriority >= 0) {
                return mPriority;
            }

            int index = mSharePriority.indexOf(mEntry);
            index = (index < 0)? Integer.MAX_VALUE: index;

            this.mPriority = index;
            return mPriority;
        }
    }

    private class SharePriority {

        ArrayList<String> mList;

        SharePriority() {
            ArrayList<String> list = new ArrayList<>();

            {
                list.add("com.tencent.mm.ui.tools.ShareImgUI"); // 发送给朋友
                list.add("com.tencent.mm.ui.tools.ShareToTimeLineUI"); // 发送到朋友圈
                list.add("com.sina.weibo.composerinde.ComposerDispatchActivity"); // 微博
                list.add("com.weico.international.activity.compose.SeaComposeActivity"); // 国际微博

                list.add("com.tencent.mobileqq.activity.JumpActivity"); // 发送给好友
                list.add("com.alibaba.android.rimet.biz.BokuiActivity"); // 钉钉

                list.add("com.sina.weibo.weiyou.share.WeiyouShareDispatcher"); // 私信

                list.add("com.tencent.mm.ui.tools.AddFavoriteUI"); // 添加到微信收藏
                list.add("cooperation.qqfav.widget.QfavJumpActivity"); // 保存到QQ收藏
                list.add("com.tencent.mobileqq.activity.qfileJumpActivity"); // 发送到我的电脑
                list.add("cooperation.qlink.QlinkShareJumpActivity"); // 面对面快传（免流量）

            }

            this.mList = list;
        }

        int indexOf(ResolveInfo entry) {
            String name = entry.activityInfo.name;

            int index = mList.indexOf(name);
            return index;
        }
    }

    public static interface OnShareActionListener {

        void onAction(ShareActionDialogFragment fragment, int action);

        void onAction(ShareActionDialogFragment fragment, ResolveInfo resolveInfo);

    }
}
