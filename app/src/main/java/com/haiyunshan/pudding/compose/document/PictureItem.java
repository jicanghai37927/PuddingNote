package com.haiyunshan.pudding.compose.document;

import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.haiyunshan.pudding.compose.note.PictureEntity;
import com.haiyunshan.pudding.dataset.FileStorage;
import com.haiyunshan.pudding.utils.UUIDUtils;
import com.haiyunshan.pudding.utils.Utils;

import java.io.File;
import java.io.IOException;

public class PictureItem extends BaseItem<PictureEntity> {

    public static final PictureItem create(Document document, File file) {
        int[] size = getImageWidthHeight(file);

        PictureEntity entity = new PictureEntity(UUIDUtils.next());
        String suffix = Utils.getSuffix(file);
        suffix = suffix.toLowerCase();

        entity.setUri(entity.getId() + suffix);
        entity.setWidth(size[0]);
        entity.setHeight(size[1]);
        entity.setDesc("");
        entity.setSource(Uri.fromFile(file).toString());

        PictureItem item = new PictureItem(document, entity);

        return item;
    }

    PictureItem(Document document, PictureEntity entity) {
        super(document, entity);
    }

    @Override
    public PictureEntity getEntity() {
        return super.getEntity();
    }

    public int getWidth() {
        return mEntity.getWidth();
    }

    public int getHeight() {
        return mEntity.getHeight();
    }

    public CharSequence getDesc() {
        return mEntity.getDesc();
    }

    public void setDesc(CharSequence text) {
        mEntity.setDesc(text.toString());
    }

    public File getFile() {
        Uri uri = Uri.parse(this.getSource());
        File file = new File(uri.getPath());
        if (file.exists()) {
            Log.w("AA", "from source = " + file.getAbsolutePath());

            return file;
        }

        return getTarget();
    }

    public File getTarget() {
        String noteId = mDocument.getId();
        String uri = mEntity.getUri();

        return FileStorage.getNotePicture(noteId, uri);
    }

    public String getSuffix() {
        String uri = mEntity.getUri();
        if (TextUtils.isEmpty(uri)) {
            return "";
        }

        int pos = uri.lastIndexOf('.');
        if (pos >= 0) {
            return uri.substring(pos);
        }

        return "";
    }

    public String getSource() {
        return mEntity.getSource();
    }

    public static int[] getImageWidthHeight(File file){
        int[] values = getJpgWidthHeight(file);
        if (values != null) {
            return values;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(file.getAbsolutePath(), options); // 此时返回的bitmap为null

        values = new int[]{options.outWidth,options.outHeight};
        return values;
    }

    static int[] getJpgWidthHeight(File file){
        String suffix = Utils.getSuffix(file);
        suffix = suffix.toLowerCase();
        if (suffix.endsWith("jpg") || suffix.endsWith("jpeg")) {
            try {
                ExifInterface exif = new ExifInterface(file.getAbsolutePath());

                int width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                int height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);

                if (width > 0 && height > 0) {

                    int orient = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    // 宽、高互换
                    if (orient == ExifInterface.ORIENTATION_TRANSPOSE
                            || orient == ExifInterface.ORIENTATION_ROTATE_90
                            || orient == ExifInterface.ORIENTATION_TRANSVERSE
                            || orient == ExifInterface.ORIENTATION_ROTATE_270) {
                        int tmp = width;
                        width = height;
                        height = tmp;
                    }

                    return new int[] { width, height };
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
