package com.haiyunshan.pudding.test;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.browse.ChapterDialogFragment;
import com.haiyunshan.pudding.compose.ShareActionDialogFragment;

public class TestDialogFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dialog_fragment);

        findViewById(R.id.btn_test_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDialog();
            }
        });
    }

    void testDialog() {
        FragmentManager fm = this.getSupportFragmentManager();
        ShareActionDialogFragment f = new ShareActionDialogFragment();
        f.show(fm, "share_action");
    }
}
