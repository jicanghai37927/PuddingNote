package com.haiyunshan.pudding.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.pudding.AcknowledgeActivity;
import com.haiyunshan.pudding.ComposeActivity;
import com.haiyunshan.pudding.HelpActivity;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.TypefaceActivity;
import com.haiyunshan.pudding.note.dataset.NoteEntry;
import com.haiyunshan.pudding.note.dataset.NoteManager;
import com.haiyunshan.pudding.utils.PrettyTimeUtils;
import com.haiyunshan.pudding.utils.UUIDUtils;

import java.util.List;

/**
 *
 */
public class NoteFragment extends Fragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    static final int REQUEST_COMPOSE = 1001;

    RecyclerView mRecyclerView;
    SortedList<NoteEntry> mSortedList;
    NoteAdapter mAdapter;

    Toolbar mToolbar;
    TextView mNewNoteBtn;
    View mNewCameraBtn;
    View mNewPhotoBtn;

    public static final NoteFragment newInstance(Bundle args) {
        NoteFragment f = new NoteFragment();

        if (args != null) {
            f.setArguments(args);
        }

        return f;
    }

    public NoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layout);
        }

        {
            this.mToolbar = view.findViewById(R.id.toolbar);
            mToolbar.inflateMenu(R.menu.menu_note);
            mToolbar.setOnMenuItemClickListener(this);
        }

        {
            this.mNewNoteBtn = view.findViewById(R.id.tv_new_note);
            mNewNoteBtn.setOnClickListener(this);

            this.mNewCameraBtn = view.findViewById(R.id.new_camera_note);
            mNewCameraBtn.setOnClickListener(this);

            this.mNewPhotoBtn = view.findViewById(R.id.new_photo_note);
            mNewPhotoBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            NoteManager mgr = NoteManager.instance();
            List<NoteEntry> list = mgr.getList();

            this.mAdapter = new NoteAdapter();
            NoteCallback callback = new NoteCallback(mAdapter);
            this.mSortedList = new SortedList<>(NoteEntry.class, callback);
            mSortedList.addAll(list);

            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COMPOSE) {
            if (resultCode == Activity.RESULT_OK) {
                String id = (data == null)? null: data.getStringExtra("id");
                if (!TextUtils.isEmpty(id)) {
                    int index = indexOf(id);
                    if (index >= 0) {
                        mAdapter.notifyItemChanged(index);
                    }
                }

                int pos = Integer.MAX_VALUE;

                List<NoteEntry> list = NoteManager.instance().getList();
                for (NoteEntry e : list) {
                    int index = indexOf(e.getId());
                    if (index < 0) {
                        index = mSortedList.add(e);
                        pos = (index < pos)? index: pos;
                    }
                }

                if (pos != Integer.MAX_VALUE) {
                    mRecyclerView.scrollToPosition(pos);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mNewNoteBtn) {
            String id = UUIDUtils.next();
            ComposeActivity.startForResult(this, REQUEST_COMPOSE, id, ComposeActivity.ACTION_NOTE);
        } else if (v == mNewCameraBtn) {
            String id = UUIDUtils.next();
            ComposeActivity.startForResult(this, REQUEST_COMPOSE, id, ComposeActivity.ACTION_CAMERA);
        } else if (v == mNewPhotoBtn) {
            String id = UUIDUtils.next();
            ComposeActivity.startForResult(this, REQUEST_COMPOSE, id, ComposeActivity.ACTION_PHOTO);
        }
    }

    int indexOf(String id) {
        int size = mSortedList.size();
        for (int i = 0; i < size; i++) {
            NoteEntry e = mSortedList.get(i);
            if (e.getId().equalsIgnoreCase(id)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_acknowledge: {
//                AcknowledgeActivity.start(this);
//                TypefaceActivity.start(this);
                HelpActivity.start(this);

                break;
            }
        }

        return false;
    }

    /**
     *
     */
    private class NoteCallback extends SortedListAdapterCallback<NoteEntry> {

        /**
         *
         */
        public NoteCallback(RecyclerView.Adapter adapter) {
            super(adapter);
        }

        @Override
        public int compare(NoteEntry o1, NoteEntry o2) {
            long t1 = o1.getCreated();
            long t2 = o2.getCreated();

            int v = 0;
            if (t1 > t2) {
                v = -1;
            } else if (t1 < t2) {
                v = 1;
            }

            return v;
        }

        @Override
        public boolean areContentsTheSame(NoteEntry oldItem, NoteEntry newItem) {
            return false;
        }

        @Override
        public boolean areItemsTheSame(NoteEntry item1, NoteEntry item2) {
            return item1.getId().equals(item2.getId());
        }
    }

    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {

        @NonNull
        @Override
        public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int resource = R.layout.layout_note_item;
            View view = getActivity().getLayoutInflater().inflate(resource, parent, false);
            NoteHolder holder = new NoteHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
            NoteEntry entry = mSortedList.get(position);
            holder.bind(position, entry);
        }

        @Override
        public int getItemCount() {
            return mSortedList.size();
        }
    }

    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTitleView;
        TextView mSubtitleView;
        TextView mTimeView;
        View mCardView;

        NoteEntry mEntry;

        public NoteHolder(View itemView) {
            super(itemView);

            this.mTitleView = itemView.findViewById(R.id.tv_title);
            this.mSubtitleView = itemView.findViewById(R.id.tv_subtitle);
            this.mTimeView = itemView.findViewById(R.id.tv_time);

            this.mCardView = itemView.findViewById(R.id.card_note);
            mCardView.setOnClickListener(this);
        }

        void bind(int position, NoteEntry entry) {
            this.mEntry = entry;

            mTitleView.setText(entry.getTitle());
            if (TextUtils.isEmpty(entry.getTitle())) {
                mTitleView.setText(R.string.note_title);
            }

            mSubtitleView.setText(entry.getSubtitle());
            if (TextUtils.isEmpty(entry.getSubtitle())) {
                mSubtitleView.setText(R.string.note_subtitle);
            }

            String time = PrettyTimeUtils.format(entry.getCreated());
            mTimeView.setText(time);
        }

        @Override
        public void onClick(View v) {
            if (v == mCardView) {
                String id = mEntry.getId();
                ComposeActivity.startForResult(NoteFragment.this, REQUEST_COMPOSE, id, ComposeActivity.ACTION_NOTE);
            }
        }
    }
}
