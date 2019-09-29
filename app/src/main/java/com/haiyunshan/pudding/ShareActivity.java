package com.haiyunshan.pudding;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.haiyunshan.pudding.compose.ShareFragment;
import com.haiyunshan.pudding.compose.document.Document;
import com.haiyunshan.pudding.compose.document.DocumentManager;

public class ShareActivity extends AppCompatActivity {

    FrameLayout mRootLayout;

    public static final void start(Fragment fragment) {
        Activity context = fragment.getActivity();
        Intent intent = new Intent(context, ShareActivity.class);

        fragment.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        this.mRootLayout = findViewById(R.id.root_layout);

        Document document = DocumentManager.instance().getDocument();
        if (document == null) {
            this.finish();
        } else {
            FragmentManager fm = this.getSupportFragmentManager();
            FragmentTransaction t = fm.beginTransaction();

            ShareFragment f = new ShareFragment();
            t.replace(mRootLayout.getId(), f, "share");

            t.commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
