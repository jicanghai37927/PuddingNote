package com.haiyunshan.pudding.utils;

import android.content.Context;
import android.os.Environment;

import com.haiyunshan.pudding.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class FileHelper {

    public static String getName(File file) {
        String name = file.getName();

        int pos = name.lastIndexOf('.');
        if (pos > 0) {
            name = name.substring(0, pos);
        }

        return name;
    }

    public static String[] getPrettyPath(Context context, File file) {
        String[] array = getPath(context, file);
        if (array.length != 2) {
            return array;
        }

        int resId = R.string.path_phone;
        String p = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!(array[0].equalsIgnoreCase(p))) {
            resId = R.string.path_sdcard;
        }

        String prefix = context.getString(resId);
        return new String[] { prefix, array[1] };
    }


    public static String[] getPath(Context context, File file) {

        String path = file.getAbsolutePath();

        // 遍历所有的
        {
            List<String> list = StorageManagerHack.getMountedVolume(context);

            for (String prefix : list) {
                if (path.startsWith(prefix)) {
                    String remain = path.substring(prefix.length());
                    return new String[]{prefix, remain};
                }
            }
        }

        // 获取外置
        {
            String prefix = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (path.startsWith(prefix)) {
                String remain = path.substring(prefix.length());
                return new String[]{prefix, remain};
            }
        }

        return new String[] { path };
    }

    public static void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                final String message =
                        "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        {
            cleanDirectory(directory);
        }

        if (!directory.delete()) {
            final String message =
                    "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = verifiedListFiles(directory);

        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    private static File[] verifiedListFiles(final File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }
}
