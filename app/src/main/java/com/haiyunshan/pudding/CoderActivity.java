package com.haiyunshan.pudding;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.haiyunshan.pudding.browse.CodeViewerFragment;

import java.io.File;

public class CoderActivity extends AppCompatActivity {

    FrameLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coder);

        this.mRootLayout = findViewById(R.id.root_layout);

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();

        Fragment f;
        Uri uri = getIntent().getData();

        // 测试用
        if (true && uri == null) {

            if (true) {
                File file = Environment.getExternalStorageDirectory();
                file = new File(file, "测试/tt/tt.html");
                uri = Uri.fromFile(file);
            }

            if (false) {
                File file = Environment.getExternalStorageDirectory();
                file = new File(file, "测试/代码/TabViewPager.java");
                uri = Uri.fromFile(file);
            }
        }

        //
        {
            f = CodeViewerFragment.newInstance(uri);
        }

        t.replace(mRootLayout.getId(), f);
        t.commit();
    }
}
