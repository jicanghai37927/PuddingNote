package com.haiyunshan.pudding;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.haiyunshan.pudding.note.NoteFragment;
import com.haiyunshan.pudding.seamless.Verifier;

public class NoteActivity extends AppCompatActivity {

    FrameLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        this.mRootLayout = findViewById(R.id.root_layout);

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();

        NoteFragment f = NoteFragment.newInstance(null);

        t.replace(mRootLayout.getId(), f);
        t.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 校验完整性
        Verifier.isValid(this);
    }
}
