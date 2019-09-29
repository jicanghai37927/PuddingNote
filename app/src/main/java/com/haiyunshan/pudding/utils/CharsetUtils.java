package com.haiyunshan.pudding.utils;

import android.text.TextUtils;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;

public class CharsetUtils {

    public static final String getCharset(File file, String defaultCharset) {

        if (!file.exists()) {
            return defaultCharset;
        }

        byte[] buf = new byte[4096];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // (1)
            UniversalDetector detector = new UniversalDetector(null);

            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();

            // (4)
            String encoding = detector.getDetectedCharset();

            // (5)
            detector.reset();
            fis.close();
            return TextUtils.isEmpty(encoding)? defaultCharset: encoding;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultCharset;
    }
}
