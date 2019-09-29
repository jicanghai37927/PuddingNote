package com.haiyunshan.pudding.compose.document;

import android.text.SpannableStringBuilder;

import com.haiyunshan.pudding.compose.format.Format;
import com.haiyunshan.pudding.compose.helper.PictureHelper;
import com.haiyunshan.pudding.compose.note.BaseEntity;
import com.haiyunshan.pudding.compose.note.Note;
import com.haiyunshan.pudding.compose.note.ParagraphEntity;
import com.haiyunshan.pudding.compose.note.PictureEntity;

import java.util.ArrayList;
import java.util.List;

public class Document {

    Note mNote;
    Format mFormat;

    ArrayList<BaseItem> mBody;

    long mSavedTime;    // 最近一次保存时间

    PictureHelper mPictureHelper;

    public Document(Note note) {
        this.mNote = note;
        this.mFormat = Format.create(note.getId());

        List<BaseEntity> list = note.getBody();

        this.mBody = new ArrayList<>(list.size() + 1);
        for (BaseEntity en : list) {
            BaseItem item = create(en);
            mBody.add(item);
        }

        if (mBody.isEmpty()) {
            mBody.add(ParagraphItem.create(this, ""));
        }

        this.mSavedTime = System.currentTimeMillis();

        this.mPictureHelper = new PictureHelper();
    }

    public String getId() {
        return mNote.getId();
    }

    public long getCreated() {
        return mNote.getCreated();
    }

    public long getModified() {
        return mNote.getModified();
    }

    public void setModified(long modified) {
        mNote.setModified(modified);
    }

    public void save() {
        Note note = this.getNote();
        note.save();

        this.mSavedTime = System.currentTimeMillis();
    }

    Note getNote() {
        mNote.clear();

        List<BaseItem> list = this.getBody();
        for (BaseItem item: list) {
            BaseEntity en = item.getEntity();
            mNote.add(en);
        }

        return mNote;
    }

    public Format getFormat() {
        return mFormat;
    }

    public List<BaseItem> getBody() {
        return mBody;
    }

    public CharSequence getText() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        for (BaseItem item : mBody) {
            if ((item instanceof ParagraphItem)) {
                ParagraphItem pItem = (ParagraphItem)item;
                ssb.append(pItem.getText());
            }

            ssb.append('\n');
        }

        if (ssb.length() != 0) {
            ssb.delete(ssb.length() - 1, ssb.length());
        }

        return ssb;
    }

    public void add(BaseItem item) {
        this.add(size(), item);
    }

    public void add(int index, BaseItem item) {
        mBody.add(index, item);

        // 同时添加图片
        if (item instanceof PictureItem) {
            mPictureHelper.add((PictureItem)item);
        }
    }

    public int indexOf(BaseItem item) {
        return mBody.indexOf(item);
    }

    public int remove(BaseItem item) {
        int index = this.indexOf(item);
        if (index >= 0) {
            mBody.remove(index);

            // 同时删除图片
            if (item instanceof PictureItem) {
                mPictureHelper.remove((PictureItem)item);
            }
        }

        return index;
    }

    public int size() {
        return mBody.size();
    }

    public BaseItem get(int index) {
        return mBody.get(index);
    }

    public String[] getTitle() {
        String title = null;
        String subtitle = null;

        List<BaseItem> list = this.getBody();
        for (BaseItem item : list) {
            if (!(item instanceof ParagraphItem)) {
                continue;
            }

            String text = ((ParagraphItem) item).getText().toString();
            if (text.length() > 200) {
                text = text.substring(0, 200);
            }

            String[] array = text.split("\n");
            for (String str : array) {
                str = str.trim();
                if (str.isEmpty()) {
                    continue;
                }

                if (title == null) {
                    title = str;
                } else if (subtitle == null) {
                    subtitle = str;
                }

                if (subtitle != null) {
                    break;
                }
            }

            if (subtitle != null) {
                break;
            }
        }

        return new String[] { title, subtitle };
    }

    public boolean isEmpty() {

        for (BaseItem item : mBody) {
            if (!item.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public long getSavedTime() {
        return mSavedTime;
    }

    private BaseItem create(BaseEntity en) {
        BaseItem item = null;

        if (en instanceof ParagraphEntity) {
            item = new ParagraphItem(this, (ParagraphEntity)en);
        } else if (en instanceof PictureEntity) {
            item = new PictureItem(this, (PictureEntity)en);
        }

        return item;
    }
}
