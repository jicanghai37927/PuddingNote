package com.haiyunshan.pudding.browse;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.chapter.Chapter;
import com.haiyunshan.pudding.chapter.ChapterBook;
import com.haiyunshan.pudding.divider.LeadingMarginDividerItemDecoration;

public class ChapterDialogFragment extends AppCompatDialogFragment {

    RecyclerView mRecyclerView;
    ChapterAdapter mAdapter;

    Toolbar mToolbar;
    TextView mInfoView;

    ChapterBook mBook;
    TextViewerFragment mParentFragment;

    int mCurrentItem;

    public ChapterDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_Chapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chapter, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layout);

            int margin = getResources().getDimensionPixelSize(R.dimen.list_margin_horizontal);
            LeadingMarginDividerItemDecoration decor = new LeadingMarginDividerItemDecoration(getActivity());
            decor.setDrawable(getResources().getDrawable(R.drawable.shape_list_divider, null));
            decor.setMargin(margin);

            mRecyclerView.addItemDecoration(decor);
        }

        {
            this.mToolbar = view.findViewById(R.id.toolbar);
            mToolbar.setNavigationIcon(R.drawable.chips_ic_close_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        {
            this.mInfoView = view.findViewById(R.id.tv_info);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        {
            this.mBook = mParentFragment.mBook;

            this.mAdapter = new ChapterAdapter();
            mRecyclerView.setAdapter(mAdapter);

            int pos = mParentFragment.getCurrentItem();
            if (pos > 0) {
                mRecyclerView.scrollToPosition(pos);
            }

            this.mCurrentItem = pos;
        }

        {
            int strId = (mBook.isArbitrary())? R.string.chapter_current_fmt_1: R.string.chapter_current_fmt_2;
            String str = getString(strId, (mCurrentItem + 1), mBook.size());
            mInfoView.setText(str);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment parent = this.getParentFragment();
        while (true) {
            if (parent == null) {
                break;
            }

            if (parent instanceof TextViewerFragment) {
                this.mParentFragment = (TextViewerFragment)parent;
                break;
            }

            parent = parent.getParentFragment();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        mParentFragment.mChapterDialogFragment = null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        mParentFragment.mChapterDialogFragment = null;
    }

    public void notifyChapterChanged() {

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        if (mBook != null) {
            int strId = (mBook.isArbitrary())? R.string.chapter_current_fmt_1: R.string.chapter_current_fmt_2;
            String str = getString(strId, (mCurrentItem + 1), mBook.size());
            mInfoView.setText(str);
        }
    }

    private class ChapterAdapter extends RecyclerView.Adapter<ChapterHolder> {

        @NonNull
        @Override
        public ChapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int resource = R.layout.layout_chapter_item;
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(resource, parent, false);

            ChapterHolder holder = new ChapterHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ChapterHolder holder, int position) {
            Chapter entry = mBook.get(position);
            holder.bind(position, entry);
        }

        @Override
        public int getItemCount() {
            return (mBook == null)? 0: mBook.size();
        }
    }

    private class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameView;

        int mPosition;
        Chapter mEntry;

        public ChapterHolder(View itemView) {
            super(itemView);

            this.mNameView = itemView.findViewById(R.id.tv_name);
            mNameView.setOnClickListener(this);
        }

        void bind(int position, Chapter entry) {
            this.mPosition = position;
            this.mEntry = entry;

            if (mBook.isArbitrary()) {
                if (mPosition == 0) {
                    mNameView.setText(R.string.chapter_begin);
                } else {
                    mNameView.setText(getString(R.string.chapter_name_fmt, position));
                }
            } else {

                if (mPosition == 0 && !entry.hasTitle()) {
                    mNameView.setText(R.string.chapter_begin);
                } else {
                    String title = entry.getTitle();
                    String subtitle = entry.getSubtitle();

                    mNameView.setText(getTitle(title, subtitle));
                }
            }


            mNameView.getPaint().setFakeBoldText((position == mCurrentItem));


        }

        @Override
        public void onClick(View v) {
            if (v == mNameView) {
                mParentFragment.showChapter(mPosition, mEntry);
                dismiss();
            }
        }

        CharSequence getTitle(String title, String subtitle) {

            if (TextUtils.isEmpty(title)) {
                return subtitle;
            }

            SpannableStringBuilder ssb = new SpannableStringBuilder();

            {
                SpannableString ss = new SpannableString(subtitle);

//                int start = 0;
//                int end = ss.length();
//                int flags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

//                ss.setSpan(new RelativeSizeSpan(0.72f), start, end, flags);
//                ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, flags);

                ssb.append(ss);
            }

            {
                ssb.append('\n');
            }

            {
                SpannableString ss = new SpannableString(title);

                int start = 0;
                int end = ss.length();
                int flags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

                ss.setSpan(new RelativeSizeSpan(0.9f), start, end, flags);
//                ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, flags);

                ssb.append(ss);
            }

            return ssb;
        }
    }
}
