package com.haiyunshan.pudding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.pudding.utils.ClipboardUtils;
import com.haiyunshan.pudding.utils.PackageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_blog)
    TextView mBlogBtn;

    @BindView(R.id.tv_version)
    TextView mVersionBtn;

    @BindView(R.id.btn_open_weixin)
    View mWeiXinBtn;

    public static final void start(Activity context) {
        Intent intent = new Intent(context, HelpActivity.class);

        context.startActivity(intent);
    }

    public static final void start(Fragment fragment) {
        Activity context = fragment.getActivity();
        Intent intent = new Intent(context, HelpActivity.class);

        fragment.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ButterKnife.bind(this);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBlogBtn.setOnClickListener(this);
        mVersionBtn.setOnClickListener(this);
        mWeiXinBtn.setOnClickListener(this);

        {
            mVersionBtn.setText(getVersionName(this));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBlogBtn) {
            String uriString = "http://andnext.club";
            openBrowser(this, uriString);

        } else if (v == mVersionBtn) {
            PackageUtils.showDetailsSettings(this, this.getPackageName());
        } else if (v == mWeiXinBtn) {
            this.openMicroMsg(this);
        }
    }

    void openMicroMsg(Context context) {

        {
            String text = getString(R.string.help_gh_title);
            ClipboardUtils.setText(this, text);
        }

        {
            String pkgName = "com.tencent.mm";
            PackageUtils.start(context, pkgName);
        }
    }

    void openBrowser(Context context, String uriString) {
        Uri uri = Uri.parse(uriString);

        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        intent.setDataAndType(uri, "text/html");
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        try {
            this.startActivity(intent);
        } catch (Exception e) {

        }

    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getVersionName(Context ctx) {
        String localVersion = "";

        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);

            localVersion = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return localVersion;
    }

}
