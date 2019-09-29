package com.haiyunshan.pudding.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.providers.contacts.HanziToPinyin;
import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.utils.FileHelper;
import com.haiyunshan.pudding.utils.Utils;

import java.io.File;
import java.util.ArrayList;

import static com.haiyunshan.pudding.compose.export.ExportHexoMarkdown.getName;

public class TestMiscActivity extends AppCompatActivity {

    Uri mPictureUri;

    static final int TAKE_PHOTO_REQUEST_ONE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_misc);

        findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        findViewById(R.id.btn_pinyin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hanziToPinyin();
            }
        });
        findViewById(R.id.btn_path).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                testPath();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO_REQUEST_ONE) {

            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Log.w("AA", data.toString());
                } else {
                    Log.w("AA", "null");
                }

                Log.w("AA", mPictureUri.toString());

                String path = Utils.getRealPathFromURI(this, mPictureUri);
                Log.w("AA", path);

            } else {
                if (mPictureUri != null) {
                    Utils.deleteImageUri(this, mPictureUri);

                    mPictureUri = null;
                }
            }
        }
    }

    void takePhoto() {
        Uri imageUri = Utils.createImageUri(this);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//如果不设置EXTRA_OUTPUT getData()  获取的是bitmap数据  是压缩后的
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_ONE);

        this.mPictureUri = imageUri;
    }

    void hanziToPinyin() {
        String text = "远看山有色";
        text = "Hello world! i love china. 远看山有色";
        text = "Hello world! i love china. 　远　看　山　有　色";
        text = "Hello world! i love china. 　\n远　\r看　山　有　色01020304";
//        text = "天狗与美惠";
        text = "天狗与美惠 01";

        if (false) {
            HanziToPinyin instance = HanziToPinyin.getInstance();
            ArrayList<HanziToPinyin.Token> list = instance.get(text);
            for (HanziToPinyin.Token t : list) {
                Log.w("AA", t.type + " , " + t.source + " = " + t.target);
            }
        }

        {
            String str = String.format("IMG_%1$04d", (0 + 1));
            Log.w("AA", str);
            Log.w("AA", getName(text));
        }
    }

    void testPath() {
        File file = Environment.getExternalStorageDirectory();
        file = new File(file, "asb/adqe/adbad");

        String[] array = FileHelper.getPrettyPath(this, file);
        for (String str : array) {
            Log.w("AA", str);
        }
    }

}
