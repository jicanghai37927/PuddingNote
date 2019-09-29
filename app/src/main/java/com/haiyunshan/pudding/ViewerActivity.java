package com.haiyunshan.pudding;

import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.haiyunshan.pudding.browse.CodeViewerFragment;
import com.haiyunshan.pudding.browse.HtmlViewerFragment;
import com.haiyunshan.pudding.browse.LatexViewerFragment;
import com.haiyunshan.pudding.browse.TextViewerFragment;

import java.io.File;

public class ViewerActivity extends AppCompatActivity {

    FrameLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        this.mRootLayout = findViewById(R.id.root_layout);

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();

        Fragment f;
        Uri uri = getIntent().getData();

        // 测试用
        if (true && uri == null) {

            if (false) {
                File file = Environment.getExternalStorageDirectory();
                file = new File(file, "测试/tt/tt.html");
                uri = Uri.fromFile(file);
            }

            if (false) {
                File file = Environment.getExternalStorageDirectory();
                file = new File(file, "测试/代码/TabViewPager.java");
                uri = Uri.fromFile(file);
            }

            if (true) {
                File file = Environment.getExternalStorageDirectory();
//                file = new File(file, "测试/jlatexmath/xzz/example_03.tex");
                file = new File(file, "测试/mathml/example_02.mml");
                uri = Uri.fromFile(file);
            }
        }

        //
        if (uri == null) {
            f = TextViewerFragment.newInstance(uri);
        } else {
            String uriString = uri.toString().toLowerCase();
            if (uriString.endsWith(".htm") || uriString.endsWith(".html")) {
                f = HtmlViewerFragment.newInstance(uri);
            } else if (uriString.endsWith(".txt")) {
                f = TextViewerFragment.newInstance(uri);
            } else if (uriString.endsWith(".tex") || uriString.endsWith(".mml") || uriString.endsWith(".mathml")){
                f = LatexViewerFragment.newInstance(uri);
            } else {
                f = CodeViewerFragment.newInstance(uri);
            }
        }

        t.replace(mRootLayout.getId(), f);
        t.commit();
    }
}
