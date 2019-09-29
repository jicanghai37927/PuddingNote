package com.haiyunshan.pudding;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.haiyunshan.pudding.font.StorageTypefaceFragment;

public class TypefaceActivity extends AppCompatActivity {

    FrameLayout mRootLayout;

    public static void start(Activity context) {
        Intent intent = new Intent(context, TypefaceActivity.class);
        context.startActivity(intent);
    }

    public static void start(Fragment fragment) {
        Activity context = fragment.getActivity();

        Intent intent = new Intent(context, TypefaceActivity.class);
        fragment.startActivity(intent);
    }

    public static void startForResult(Fragment fragment, int requestCode) {
        Activity context = fragment.getActivity();

        Intent intent = new Intent(context, TypefaceActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typeface);

        this.mRootLayout = findViewById(R.id.root_layout);

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();

        StorageTypefaceFragment f = StorageTypefaceFragment.newInstance(null);

        t.replace(mRootLayout.getId(), f);
        t.commit();
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_OK);

        super.onBackPressed();
    }
}
