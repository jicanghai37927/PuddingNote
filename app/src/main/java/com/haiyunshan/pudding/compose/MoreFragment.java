package com.haiyunshan.pudding.compose;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.haiyunshan.pudding.AuthorActivity;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.divider.SectionDividerItemDecoration;
import com.haiyunshan.pudding.setting.Setting;

import java.util.ArrayList;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 *
 */
public class MoreFragment extends Fragment implements View.OnClickListener {

    public static final int EVENT_PAGE      = 0x101;    // 页面设置
    public static final int EVENT_CREATE    = 0x102;    // 新建笔记

    static final int ITEM_SEPARATE          = 102;

    static final int ITEM_PAGE              = 201;  // 页面设置
    static final int ITEM_AUTHOR            = 202;  // 作者姓名
    static final int ITEM_CREATE            = 203;  // 新建笔记

    static final int REQUEST_AUTHOR         = 0x1001;   //

    RecyclerView mRecyclerView;
    MoreAdapter mAdapter;

    View mDoneBtn;

    public MoreFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layout);

            SectionDividerItemDecoration decor = new SectionDividerItemDecoration(getActivity());
            decor.setDrawable(getResources().getDrawable(R.drawable.shape_list_divider, null));

            mRecyclerView.addItemDecoration(decor);
        }

        {
            this.mDoneBtn = view.findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.mAdapter = new MoreAdapter(getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_AUTHOR) {
            if (resultCode == RESULT_OK && data != null) {
                String text = data.getStringExtra("text");
                text = (text == null)? "": text;
                text = text.trim();

                if (!text.isEmpty()) {
                    Setting.instance().setAuthor(text);
                    Setting.instance().save();
                }
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

        Fragment parent = this.getParentFragment();
        while (true) {
            if (parent == null) {
                break;
            }

            parent = parent.getParentFragment();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mDoneBtn) {
            getActivity().onBackPressed();
        }
    }

    void onEvent(int event) {
        Activity context = getActivity();

        {
            Intent intent = new Intent();
            intent.putExtra("event", event);

            context.setResult(RESULT_OK, intent);
        }

        context.onBackPressed();
    }

    /**
     *
     */
    private class MoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<MoreItem> mList;

        Activity mContext;

        public MoreAdapter(Activity context) {
            this.mContext = context;

            this.mList = new ArrayList<>();
            mList.add(new MoreItem(ITEM_PAGE,
                    R.drawable.ic_page_setting,
                    getString(R.string.more_page_setting), false));

            mList.add(new MoreItem(ITEM_SEPARATE));

            mList.add(new MoreItem(ITEM_AUTHOR,
                    R.drawable.ic_sign,
                    getString(R.string.more_author), true));

            mList.add(new MoreItem(ITEM_SEPARATE));

            mList.add(new MoreItem(ITEM_CREATE,
                    0, getString(R.string.more_create_note), false));

        }

        @Override
        public int getItemViewType(int position) {
            int type = mList.get(position).mType;
            return type;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            LayoutInflater inflater = mContext.getLayoutInflater();

            switch (viewType) {
                case ITEM_SEPARATE: {
                    int resource = R.layout.layout_paragraph_format_separate_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new SeparateHolder(view);

                    break;
                }

                case ITEM_PAGE: {
                    int resource = R.layout.layout_list_more_icon_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new PageHolder(view);

                    break;
                }

                case ITEM_AUTHOR: {
                    int resource = R.layout.layout_list_more_icon_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new SignHolder(view);

                    break;
                }

                case ITEM_CREATE: {
                    int resource = R.layout.layout_list_more_create_item;
                    View view = inflater.inflate(resource, parent, false);
                    holder = new CreateHolder(view);

                    break;
                }
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            int viewType = getItemViewType(position);
            MoreItem item = mList.get(position);

            switch (viewType) {
                case ITEM_SEPARATE: {

                    break;
                }

                case ITEM_PAGE: {
                    PageHolder holder = (PageHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_AUTHOR: {
                    SignHolder holder = (SignHolder)h;
                    holder.bind(position, item);

                    break;
                }

                case ITEM_CREATE: {
                    CreateHolder holder = (CreateHolder)h;
                    holder.bind(position, item);

                    break;
                }
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    /**
     * 页面设置
     *
     */
    private class PageHolder extends IconHolder {

        public PageHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {

            onEvent(EVENT_PAGE);

        }
    }

    /**
     * 作者姓名
     *
     */
    private class SignHolder extends IconHolder {

        public SignHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {

            Fragment fragment = MoreFragment.this;
            String text = Setting.instance().getAuthor();
            AuthorActivity.startForResult(fragment, REQUEST_AUTHOR, text);

        }
    }

    /**
     * 新建笔记
     *
     */
    private class CreateHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameView;

        public CreateHolder(View itemView) {
            super(itemView);


            this.mNameView = itemView.findViewById(R.id.tv_name);


            itemView.setOnClickListener(this);
        }

        void bind(int position, MoreItem item) {

            mNameView.setText(item.mName);

        }

        @Override
        public void onClick(View v) {

            onEvent(EVENT_CREATE);

        }
    }

    /**
     *
     */
    private class IconHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mIconView;
        TextView mNameView;
        View mArrowView;

        public IconHolder(View itemView) {
            super(itemView);

            this.mIconView = itemView.findViewById(R.id.iv_icon);
            this.mNameView = itemView.findViewById(R.id.tv_name);
            this.mArrowView = itemView.findViewById(R.id.iv_arrow);

            itemView.setOnClickListener(this);
        }

        void bind(int position, MoreItem item) {

            mIconView.setImageResource(item.mIcon);
            mNameView.setText(item.mName);
            mArrowView.setVisibility(item.mArrow? View.VISIBLE: View.INVISIBLE);

        }

        @Override
        public void onClick(View v) {

        }
    }

    /**
     *
     */
    private class SeparateHolder extends RecyclerView.ViewHolder {

        public SeparateHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     *
     */
    private class MoreItem {

        int mType;

        int mIcon;
        CharSequence mName;
        boolean mArrow;

        MoreItem(int type) {
            this(type, 0, null, false);
        }

        MoreItem(int type, int icon, CharSequence name, boolean arrow) {
            this.mType = type;

            this.mIcon = icon;
            this.mName = name;
            this.mArrow = arrow;
        }
    }

}
