package com.haiyunshan.pudding.compose;


import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.TypefaceActivity;
import com.haiyunshan.pudding.compose.event.FormatCompleteEvent;
import com.haiyunshan.pudding.compose.event.FormatFontEvent;
import com.haiyunshan.pudding.divider.LeadingMarginDividerItemDecoration;
import com.haiyunshan.pudding.font.dataset.FontEntry;
import com.haiyunshan.pudding.font.dataset.FontManager;
import com.haiyunshan.pudding.widget.FormatTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.text.Collator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FontFragment extends Fragment implements FormatTitleBar.OnButtonClickListener, View.OnClickListener {

    static final int REQUEST_ADD = 1001;

    @BindView(R.id.title_bar)
    FormatTitleBar mTitleBar;

    @BindView(R.id.recycler_list_view)
    RecyclerView mRecyclerView;

    SortedList<FontEntry> mSortedList;
    FontAdapter mAdapter;

    String mFontId;

    Unbinder mUnbinder;

    public FontFragment() {

    }

    public void setArguments(String font) {
        Bundle args = new Bundle();
        args.putString("font", font);

        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_font, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mUnbinder = ButterKnife.bind(this, view);

        {

        }

        {
            mTitleBar.setOnButtonClickListener(this);
            mTitleBar.setBackable(true);
        }

        {
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layout);


            LeadingMarginDividerItemDecoration decor = new LeadingMarginDividerItemDecoration(getActivity());
            decor.setDrawable(getActivity().getDrawable(R.drawable.shape_divider));
            decor.setMargin(getActivity().getResources().getDimensionPixelSize(R.dimen.font_item_check_size));
            mRecyclerView.addItemDecoration(decor);

            SimpleItemAnimator animator = (SimpleItemAnimator)(mRecyclerView.getItemAnimator());
            animator.setSupportsChangeAnimations(false);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (mTitleBar.isEdit()) {
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            resetTranslate();
                        }
                    }
                }
            });
            mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mTitleBar.isEdit()) {
                        int action = event.getActionMasked();
                        if (action == MotionEvent.ACTION_DOWN) {
                            resetTranslate();
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            if (args != null) {
                this.mFontId = args.getString("font", "");
            }
        }

        {
            this.mAdapter = new FontAdapter(getActivity());
            FontCallback callback = new FontCallback(mAdapter);
            this.mSortedList = new SortedList<>(FontEntry.class, callback);

            FontManager mgr = FontManager.getInstance();
            mSortedList.addAll(mgr.getList());

            this.mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADD: {

                List<FontEntry> list = FontManager.getInstance().getList();
                for (FontEntry entry : list) {
                    if (mSortedList.indexOf(entry) < 0) {
                        mSortedList.add(entry);
                    }
                }

                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
    }

    void setChecked(int position, String previousId) {

        int old = -1;
        if (!TextUtils.isEmpty(previousId)) {
            int size = mSortedList.size();
            for (int i = 0; i < size; i++) {
                FontEntry entry = mSortedList.get(i);
                if (entry.getId().equals(previousId)) {
                    old = i;
                    break;
                }
            }
        }

        if (old >= 0) {
            mAdapter.notifyItemChanged(old);
        }

        mAdapter.notifyItemChanged(position);
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

            case FormatTitleBar.BUTTON_EDIT: {
                mAdapter.notifyDataSetChanged();

                break;
            }

            case FormatTitleBar.BUTTON_DONE: {
                mAdapter.notifyDataSetChanged();

                break;
            }
            case FormatTitleBar.BUTTON_ADD: {

                bar.setEdit(false);
                mAdapter.notifyDataSetChanged();

                TypefaceActivity.startForResult(this, REQUEST_ADD);

                break;
            }
        }
    }

    public void resetTranslate() {
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = mRecyclerView.getChildAt(i);
            RecyclerView.ViewHolder h = mRecyclerView.findContainingViewHolder(v);
            FontHolder holder = (FontHolder)h;
            holder.setTranslate(0);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private class FontCallback extends SortedListAdapterCallback<FontEntry> {

        Collator mCollator;

        /**
         * Creates a {@link SortedList.Callback} that will forward data change events to the provided
         * Adapter.
         *
         * @param adapter The Adapter instance which should receive events from the SortedList.
         */
        public FontCallback(RecyclerView.Adapter adapter) {
            super(adapter);

            this.mCollator = Collator.getInstance();
        }

        @Override
        public int compare(FontEntry o1, FontEntry o2) {
            int s1 = o1.getSort();
            int s2 = o2.getSort();
            if (s1 < s2) {
                return -1;
            } else if (s1 > s2) {
                return 1;
            }

            String n1 = o1.getPrettyName();
            String n2 = o2.getPrettyName();

            return mCollator.compare(n1, n2);
        }

        @Override
        public boolean areContentsTheSame(FontEntry oldItem, FontEntry newItem) {
            String n1 = oldItem.getPrettyName();
            String n2 = newItem.getPrettyName();

            return n1.equalsIgnoreCase(n2);
        }

        @Override
        public boolean areItemsTheSame(FontEntry item1, FontEntry item2) {
            return item1.getId().equalsIgnoreCase(item2.getId());
        }
    }

    private class FontAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        LayoutInflater mInflater;

        FontAdapter(Activity context) {
            this.mInflater = context.getLayoutInflater();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int resource = R.layout.layout_paragraph_font_item;
            View view = mInflater.inflate(resource, parent, false);
            FontHolder holder = new FontHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            FontEntry entry = mSortedList.get(position);

            FontHolder holder = (FontHolder)h;
            holder.bind(position, entry);
        }

        @Override
        public int getItemCount() {
            return mSortedList.size();
        }

    }

    private class FontHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mItemLayout;
        View mCheckedView;
        View mDeleteView;
        TextView mNameView;

        View mDeleteBtn;

        int mPosition;
        FontEntry mEntry;

        public FontHolder(View itemView) {
            super(itemView);

            this.mItemLayout = itemView.findViewById(R.id.font_layout);
            this.mCheckedView = itemView.findViewById(R.id.iv_checked);
            this.mDeleteView = itemView.findViewById(R.id.iv_delete);
            this.mNameView = itemView.findViewById(R.id.tv_name);

            this.mDeleteBtn = itemView.findViewById(R.id.btn_delete);

            mItemLayout.setOnClickListener(this);
            mDeleteView.setOnClickListener(this);
            mDeleteBtn.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        void bind(int position, FontEntry entry) {
            this.mPosition = position;
            this.mEntry = entry;

            {
                mNameView.setText(entry.getPrettyName());

                FontManager mgr = FontManager.getInstance();
                Typeface tf = mgr.getTypeface(entry);
                mNameView.setTypeface(tf);
            }

            {
                mDeleteBtn.setEnabled(false);
                mDeleteBtn.setClickable(false);
                mDeleteBtn.setTranslationX(0);
            }

            boolean isEdit = mTitleBar.isEdit();
            if (isEdit) {
                mItemLayout.setEnabled(false);
                mItemLayout.setClickable(false);
                mItemLayout.setTranslationX(0);

                mCheckedView.setVisibility(View.GONE);

                mDeleteView.setVisibility(View.VISIBLE);
                if (!entry.isEditable()) {
                    mDeleteView.setVisibility(View.INVISIBLE);
                }

            } else {
                mItemLayout.setEnabled(true);
                mItemLayout.setClickable(true);
                mItemLayout.setTranslationX(0);

                mDeleteView.setVisibility(View.GONE);

                mCheckedView.setVisibility(View.INVISIBLE);

                String fontId = mFontId;
                if (fontId != null) {
                    if (entry.getId().equalsIgnoreCase(fontId)) {
                        mCheckedView.setVisibility(View.VISIBLE);
                    }
                }
            }

        }

        @Override
        public void onClick(View v) {
            if (v == mItemLayout) {
                if (mCheckedView.getVisibility() != View.VISIBLE) {
                    String previousId = mFontId;
                    mFontId = mEntry.getId();

                    setChecked(mPosition, previousId);

                    EventBus.getDefault().post(new FormatFontEvent(mEntry));
                }
            } else if (v == mDeleteView) {
                resetTranslate();

                mDeleteBtn.setEnabled(true);
                mDeleteBtn.setClickable(true);

                int width = mDeleteBtn.getWidth();
                mItemLayout.animate().translationXBy(-width);
            } else if (v == mDeleteBtn) {

                long duration = 200;
                int value = -itemView.getWidth();

                mItemLayout.animate().setDuration(duration).translationXBy(value);
                mDeleteBtn.animate().setDuration(duration).translationX(value).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        // 删除数据
                        FontManager.getInstance().remove(mEntry);
                        FontManager.getInstance().save();

                        // 更新UI
                        mSortedList.remove(mEntry);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });


            } else if (v == itemView) {
                resetTranslate();
            }
        }

        boolean isTranslate() {
            return mDeleteBtn.isEnabled();
        }

        void setTranslate(int x) {
            if (isTranslate()) {

                mDeleteBtn.setEnabled(false);
                mDeleteBtn.setClickable(false);

                mItemLayout.animate().translationX(x);
            }
        }
    }
}
