package com.haiyunshan.pudding;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public class AuthorActivity extends AppCompatActivity {

    EditText mEditAuthor;

    /**
     *
     * @param fragment
     * @param requestCode
     * @param text
     */
    public static final void startForResult(Fragment fragment, int requestCode, String text) {
        Activity context = fragment.getActivity();

        Intent intent = new Intent(context, AuthorActivity.class);
        intent.putExtra("text", text == null? "": text);

        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        {
            this.mEditAuthor = findViewById(R.id.edit_author);
        }

        {
            String text = getIntent().getStringExtra("text");
            text = (text == null)? "": text;
            text.trim();

            mEditAuthor.setText(text);
            mEditAuthor.setSelection(text.length());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();

        {
            Editable editable = mEditAuthor.getText();

            String text = (editable == null)? "": editable.toString();
            text = text.trim();

            intent.putExtra("text", text);
        }

        this.setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}
