package com.haiyunshan.pudding.chapter;

import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.mozilla.universalchardet.Constants.CHARSET_UTF_16BE;
import static org.mozilla.universalchardet.Constants.CHARSET_UTF_16LE;
import static org.mozilla.universalchardet.Constants.CHARSET_UTF_8;

public class PlainText {

    static final String TAG= "PlainText";

    String mText;

    public PlainText(File file) {
        this(file, 6 * 1024);
    }

    public PlainText(File file, int maxEncoding) {

        byte[] data = null;
        try {
            data = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {

        }

        if (data == null) {
            mText = "";
            return;
        }

        String encoding;

        {
            long time = System.currentTimeMillis();

            {
                encoding = getCharset(data, maxEncoding, "GB18030");
            }

            long ellapse = System.currentTimeMillis() - time;
            Log.e(TAG, "[Nothing happen]IMPORTANT: encoding = " + ellapse);
        }


        try {
            int offset = getOffset(data, encoding);
            int length = data.length - offset;

            this.mText = new String(data, offset, length, encoding);

//            Log.w("AA", "TEXT = " + mText);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(mText)) {
            mText = "";
        }
    }

    public String getText() {
        return this.mText;
    }

    int getOffset(byte[] data, String encoding) {

        if (encoding.equalsIgnoreCase(CHARSET_UTF_8)) {

            if (data.length >= 3) {
                if ((data[0] & 0xFF) == 0xEF
                        && (data[1] & 0xFF) == 0xBB
                        && (data[2] & 0xFF) == 0xBF) {
                    return 3;
                }
            }

        } else if (encoding.equalsIgnoreCase(CHARSET_UTF_16BE)) {

            if (data.length >= 2) {
                if ((data[0] & 0xFF) == 0xFE
                        && (data[1] & 0xFF) == 0xFF) {
                    return 2;
                }
            }

        } else if (encoding.equalsIgnoreCase(CHARSET_UTF_16LE)) {

            if (data.length >= 2) {
                if ((data[0] & 0xFF) == 0xFF
                        && (data[1] & 0xFF) == 0xFE) {
                    return 2;
                }
            }

        }

        return 0;
    }

    String getCharset(byte[] data, int max, String defaultCharset) {
        String encoding;

        int start = 2 * 1024;
        int step = 1 * 1024;
        while (true) {
            encoding = getCharset(data, start);
            if (!TextUtils.isEmpty(encoding)) {
                break;
            }

            if (start > max) {
                break;
            }

            start += step;
        }


        encoding = (TextUtils.isEmpty(encoding))? defaultCharset: encoding;
        return encoding;
    }

    String getCharset(byte[] data, int max) {

        UniversalDetector detector = new UniversalDetector(null);

        int length = max;
        length = (data.length > length)? length: data.length;
        detector.handleData(data, 0, length);
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();

        detector.reset();

        return encoding;
    }
}
