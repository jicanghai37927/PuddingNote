package com.haiyunshan.pudding.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.haiyunshan.pudding.R;

import java.util.Set;

public class TestProcessTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_process_text);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Log.w("AA", intent.toString());

        Set<String> set = extras.keySet();
        for (String str : set) {
            Object obj = extras.get(str);

            Log.w("AA", str + " = " + obj.toString());
        }




    }
}
