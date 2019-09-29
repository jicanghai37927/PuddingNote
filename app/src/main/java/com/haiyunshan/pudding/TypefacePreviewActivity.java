package com.haiyunshan.pudding;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.haiyunshan.pudding.font.FontBookFragment;
import com.haiyunshan.pudding.font.TypefacePreviewFragment;

import java.util.ArrayList;

public class TypefacePreviewActivity extends AppCompatActivity {

    FrameLayout mRootLayout;

    public static final void startForResult(Fragment fragment, int requestCode, String source, ArrayList<String> list) {
        Activity context = fragment.getActivity();

        Intent intent = new Intent(context, TypefacePreviewActivity.class);
        intent.putExtra("source", source);
        intent.putStringArrayListExtra("list", list);

        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typeface_preview);

        this.mRootLayout = findViewById(R.id.root_layout);

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();

        Fragment f;
        if (getIntent().getData() != null) {
            f = FontBookFragment.newInstance(getIntent().getData());
        } else {
            f = TypefacePreviewFragment.newInstance(getIntent().getExtras());
        }

        t.replace(mRootLayout.getId(), f);
        t.commit();
    }
}
