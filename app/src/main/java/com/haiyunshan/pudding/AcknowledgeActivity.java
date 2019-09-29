package com.haiyunshan.pudding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haiyunshan.pudding.acknowledge.Acknowledge;
import com.haiyunshan.pudding.utils.PackageUtils;

import java.util.ArrayList;
import java.util.List;

public class AcknowledgeActivity extends AppCompatActivity {

    static final int ITEM_TITLE         = 1;
    static final int ITEM_APP           = 2;
    static final int ITEM_OPEN_SOURCE   = 3;

    RecyclerView mRecyclerView;
    AcknowledgeAdapter mAdapter;

    Toolbar mToolbar;

    public static void start(Fragment fragment) {
        Activity context = fragment.getActivity();
        Intent intent = new Intent(context, AcknowledgeActivity.class);

        fragment.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledge);

        {
            this.mToolbar = findViewById(R.id.toolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        {
            this.mRecyclerView = findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layout);

            DividerItemDecoration decor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            decor.setDrawable(getDrawable(R.drawable.shape_list_divider));
            mRecyclerView.addItemDecoration(decor);
        }

        {
            ArrayList<AcknowledgeItem> list = new ArrayList<>();
            {
                Acknowledge.AppDataset ds = Acknowledge.getApps();
                if (!ds.mList.isEmpty()) {
                    String title = getString(R.string.acknowledge_title_apps);
                    list.add(new AcknowledgeItem(ITEM_TITLE, title));

                    for (Acknowledge.AppEntry e : ds.mList) {
                        list.add(new AcknowledgeItem(ITEM_APP, e));
                    }
                }
            }
            {
                Acknowledge.OpenSourceDataset ds = Acknowledge.getOpenSource();
                if (!ds.mList.isEmpty()) {
                    String title = getString(R.string.acknowledge_title_opensource);
                    list.add(new AcknowledgeItem(ITEM_TITLE, title));

                    for (Acknowledge.OpenSourceEntry e : ds.mList) {
                        list.add(new AcknowledgeItem(ITEM_OPEN_SOURCE, e));
                    }
                }
            }

            this.mAdapter = new AcknowledgeAdapter(this, list);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class AcknowledgeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<AcknowledgeItem> mList;

        Activity mContext;

        public AcknowledgeAdapter(Activity context, List<AcknowledgeItem> list) {
            this.mContext = context;
            this.mList = list;
        }

        @Override
        public int getItemViewType(int position) {
            AcknowledgeItem item = mList.get(position);

            return item.mType;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder h = null;
            switch (viewType) {
                case ITEM_TITLE: {
                    int resource = R.layout.layout_acknowledge_title_item;
                    View view = mContext.getLayoutInflater().inflate(resource, parent, false);
                    TitleHolder holder = new TitleHolder(view);

                    h = holder;
                    break;
                }
                case ITEM_APP: {
                    int resource = R.layout.layout_acknowledge_item;
                    View view = mContext.getLayoutInflater().inflate(resource, parent, false);
                    AppHolder holder = new AppHolder(view);

                    h = holder;

                    break;
                }
                case ITEM_OPEN_SOURCE: {
                    int resource = R.layout.layout_acknowledge_item;
                    View view = mContext.getLayoutInflater().inflate(resource, parent, false);
                    OpenSourceHolder holder = new OpenSourceHolder(view);

                    h = holder;

                    break;
                }
            }

            return h;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            AcknowledgeItem item = mList.get(position);
            int viewType = this.getItemViewType(position);

            switch (viewType) {
                case ITEM_TITLE: {

                    String entry = (String)(item.mObject);

                    TitleHolder holder = (TitleHolder)h;
                    holder.bind(position, item, entry);

                    break;
                }
                case ITEM_APP: {

                    Acknowledge.AppEntry entry = (Acknowledge.AppEntry) (item.mObject);

                    AppHolder holder = (AppHolder)h;
                    holder.bind(position, item, entry);

                    break;
                }
                case ITEM_OPEN_SOURCE: {

                    Acknowledge.OpenSourceEntry entry = (Acknowledge.OpenSourceEntry) (item.mObject);

                    OpenSourceHolder holder = (OpenSourceHolder)h;
                    holder.bind(position, item, entry);

                    break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class TitleHolder extends RecyclerView.ViewHolder {

        TextView mTitleView;

        public TitleHolder(View itemView) {
            super(itemView);

            this.mTitleView = (TextView)itemView;
        }

        void bind(int position, AcknowledgeItem item, String entry) {
            mTitleView.setText(entry);
        }
    }

    private class AppHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTitleView;
        TextView mSubtitleView;
        ImageView mIconView;

        Acknowledge.AppEntry mEntry;

        public AppHolder(View itemView) {
            super(itemView);

            this.mTitleView = itemView.findViewById(R.id.tv_title);
            this.mSubtitleView = itemView.findViewById(R.id.tv_subtitle);
            this.mIconView = itemView.findViewById(R.id.iv_icon);
            mIconView.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        void bind(int position, AcknowledgeItem item, Acknowledge.AppEntry entry) {
            this.mEntry = entry;

            Context context = itemView.getContext();

            if (item.mExist == null) {
                item.mExist = PackageUtils.exist(context, entry.mId);
                if (item.mExist) {
                    item.mIcon = PackageUtils.getIcon(context, entry.mId);
                }
            }

            mTitleView.setText(entry.mName);
            mSubtitleView.setText(entry.mSlogan);

            mIconView.setImageDrawable(item.mIcon);
            mIconView.setVisibility(item.mIcon == null? View.GONE: View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                Context context = itemView.getContext();
                String pkgName = mEntry.mId;

                boolean exist = PackageUtils.exist(context, pkgName);
//                exist = false;
                if (exist) { // 存在，启动之
                    exist = PackageUtils.launch(context, pkgName);
                    if (!exist) {
                        PackageUtils.start(context, pkgName);
                    }
                } else { // 不存在，尝试在应用商店开发，否则使用浏览器打开
                    if (existStore(context, pkgName)) {
                        showDetail(context, pkgName);
                    } else {
                        startBrowse(context, pkgName);
                    }
                }
            }
        }

        boolean existStore(Context context, String pkgName) {
            Uri uri = Uri.parse("market://details?id=" + pkgName);

            boolean result = PackageUtils.canExecute(context, Intent.ACTION_VIEW, uri);
//            result = false;

            return result;
        }

        void showDetail(Context context, String pkgName) {
            Uri uri = Uri.parse("market://details?id=" + pkgName);

            PackageUtils.executeAction(context, Intent.ACTION_VIEW, uri);
        }

        void startBrowse(Context context, String pkgName) {
            Uri uri = Uri.parse("http://sj.qq.com/myapp/detail.htm?apkName=" + pkgName);

            PackageUtils.executeAction(context, Intent.ACTION_VIEW, uri);
        }

    }

    private class OpenSourceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTitleView;
        TextView mSubtitleView;
        ImageView mIconView;

        Acknowledge.OpenSourceEntry mEntry;

        public OpenSourceHolder(View itemView) {
            super(itemView);

            this.mTitleView = itemView.findViewById(R.id.tv_title);
            this.mSubtitleView = itemView.findViewById(R.id.tv_subtitle);
            this.mIconView = itemView.findViewById(R.id.iv_icon);
            mIconView.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        void bind(int position, AcknowledgeItem item, Acknowledge.OpenSourceEntry entry) {
            this.mEntry = entry;

            if (TextUtils.isEmpty(entry.mDeveloper)) {
                mTitleView.setText(entry.mName);
            } else {
                mTitleView.setText(entry.mName + " - " + entry.mDeveloper);
            }

            mSubtitleView.setText(entry.mDesc);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                Context context = itemView.getContext();
                String url = mEntry.mUri;

                startBrowse(context, url);
            }
        }

        void startBrowse(Context context, String url) {
            Uri uri = Uri.parse(url);

            PackageUtils.executeAction(context, Intent.ACTION_VIEW, uri);
        }
    }

    private class AcknowledgeItem {

        int mType;
        Object mObject;

        Boolean mExist;
        Drawable mIcon;

        public AcknowledgeItem(int type, Object obj) {
            this.mType = type;
            this.mObject = obj;
        }
    }
}
