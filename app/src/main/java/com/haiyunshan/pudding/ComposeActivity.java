package com.haiyunshan.pudding;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.compose.document.Document;
import com.haiyunshan.pudding.compose.document.DocumentManager;
import com.haiyunshan.pudding.seamless.Verifier;

public class ComposeActivity extends AppCompatActivity {

    public static final String ACTION_NOTE      = "note";
    public static final String ACTION_CAMERA    = "camera";
    public static final String ACTION_PHOTO     = "photo";

    FrameLayout mRootLayout;

    public static final void startForResult(Fragment fragment, int requestCode, String id, String action) {
        Activity context = fragment.getActivity();
        Intent intent = new Intent(context, ComposeActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("action", action);

        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        this.mRootLayout = findViewById(R.id.root_layout);

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();

        ComposeFragment f = ComposeFragment.newInstance(getIntent().getExtras());

        t.replace(mRootLayout.getId(), f, "compose");
        t.commit();
    }

    @Override
    protected void onDestroy() {

        {
            DocumentManager.instance().setDocument(null);
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag("compose");
        if (f != null) {
            ComposeFragment fragment = (ComposeFragment)f;
            if (fragment.onBackPressed()) {
                return;
            }
        }

        Intent intent = this.getIntent();
        String id = (intent == null)? null: intent.getStringExtra("id");
        if (TextUtils.isEmpty(id)) {
            this.setResult(RESULT_CANCELED, null);
        } else {
            intent = new Intent();
            intent.putExtra("id", id);
            this.setResult(RESULT_OK, intent);
        }

        super.onBackPressed();
    }

    public void createNote(Document doc) {
        String id = DocumentManager.instance().create(doc);

        {
            FragmentManager fm = this.getSupportFragmentManager();

            FragmentTransaction t = fm.beginTransaction();
            t.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);

            Bundle args = new Bundle();
            args.putString("id", id);
            args.putString("action", ACTION_NOTE);

            ComposeFragment f = ComposeFragment.newInstance(args);

            t.replace(mRootLayout.getId(), f, "compose");
            t.commit();
        }
    }
}
