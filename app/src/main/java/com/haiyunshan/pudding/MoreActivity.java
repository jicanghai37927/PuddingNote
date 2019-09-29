package com.haiyunshan.pudding;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.haiyunshan.pudding.compose.MoreFragment;

/**
 *
 */
public class MoreActivity extends AppCompatActivity {

    FrameLayout mRootLayout;

    public static final void startForResult(Fragment fragment, int requestCode) {
        Activity context = fragment.getActivity();
        Intent intent = new Intent(context, MoreActivity.class);

        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        this.mRootLayout = findViewById(R.id.root_layout);

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();

        MoreFragment f = new MoreFragment();
        t.replace(mRootLayout.getId(), f, "more");

        t.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
