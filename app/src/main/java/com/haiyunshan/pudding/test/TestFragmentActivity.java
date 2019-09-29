package com.haiyunshan.pudding.test;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.haiyunshan.pudding.R;

public class TestFragmentActivity extends AppCompatActivity {

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);

        mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

}
