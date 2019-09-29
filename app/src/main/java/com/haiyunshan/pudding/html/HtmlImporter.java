package com.haiyunshan.pudding.html;

import android.net.Uri;

import com.haiyunshan.pudding.compose.document.PictureItem;
import com.haiyunshan.pudding.compose.note.BaseEntity;
import com.haiyunshan.pudding.compose.note.Note;
import com.haiyunshan.pudding.compose.note.ParagraphEntity;
import com.haiyunshan.pudding.compose.note.PictureEntity;
import com.haiyunshan.pudding.dataset.FileStorage;
import com.haiyunshan.pudding.note.dataset.NoteEntry;
import com.haiyunshan.pudding.note.dataset.NoteManager;
import com.haiyunshan.pudding.utils.UUIDUtils;
import com.haiyunshan.pudding.utils.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class HtmlImporter implements ObservableOnSubscribe<HtmlImporter> {

    HtmlPage mPage;
    NoteEntry mEntry;

    Note mNote;

    boolean mIsDone;

    public HtmlImporter(HtmlPage page, NoteEntry entry) {
        this.mPage = page;
        this.mEntry = entry;

        mIsDone = false;
    }

    public boolean isDone() {
        return mIsDone;
    }

    @Override
    public void subscribe(ObservableEmitter<HtmlImporter> emitter) {

        // 创建NOTE
        {
            this.removeNote(mEntry);

            this.mNote = Note.create(mEntry.getId());
        }

        // 导入数据
        {
            int size = mPage.size();
            for (int i = 0; i < size; i++) {
                BaseEntity entity = null;

                BaseDiv div = mPage.get(i);
                if (div instanceof ParagraphDiv) {
                    entity = toParagraph((ParagraphDiv)div);
                } else if (div instanceof PictureDiv) {
                    entity = toPicture((PictureDiv)div);

                    // 拷贝图片
                    if (entity != null) {
                        copyPicture((PictureEntity)entity, (PictureDiv)div);
                    }
                }

                if (entity != null) {
                    mNote.add(entity);
                }

                emitter.onNext(this);
            }
        }

        // 更新Entry
        {
            mNote.save();

            this.updateNote();
            NoteManager.instance().save();
        }

        this.mIsDone = true;
        emitter.onComplete();
    }

    ParagraphEntity toParagraph(ParagraphDiv div) {
        String id = UUIDUtils.next();

        ParagraphEntity entity = new ParagraphEntity(id);
        entity.setText(div.getPrettyContent(false, false).toString());

        return entity;
    }

    PictureEntity toPicture(PictureDiv div) {
        Uri uri = div.getUri();
        if (uri == null) {
            return null;
        }

        String id = UUIDUtils.next();

        PictureEntity entity = new PictureEntity(id);

        if (uri.getScheme().equalsIgnoreCase("file")) {
            File file = new File(uri.getPath());
            int[] size = PictureItem.getImageWidthHeight(file);

            String suffix = Utils.getSuffix(file);
            suffix = suffix.toLowerCase();

            entity.setUri(entity.getId() + suffix);
            entity.setWidth(size[0]);
            entity.setHeight(size[1]);
            entity.setDesc("");
            entity.setSource(Uri.fromFile(file).toString());

        } else {

            int[] size = new int[] { -1, -1 };

            String suffix = "";

            entity.setUri(entity.getId() + suffix);
            entity.setWidth(size[0]);
            entity.setHeight(size[1]);
            entity.setDesc("");
            entity.setSource(uri.toString());

        }

        return entity;
    }

    void copyPicture(PictureEntity entity, PictureDiv div) {

        String source = entity.getSource();
        if (source.startsWith("http")) {

        } else {
            String noteId = mEntry.getId();
            String uri = entity.getUri();

            File src = new File(div.getUri().getPath());
            File dest = FileStorage.getNotePicture(noteId, uri);
            File tmp = new File(dest.getAbsolutePath() + ".tmp");

            try {
                FileUtils.copyFile(src, tmp);
                tmp.renameTo(dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    void removeNote(NoteEntry entry) {
        try {
            Note.remove(entry.getId());
        } catch (IOException e) {
        }
    }

    void updateNote() {
        NoteEntry entry = this.mEntry;

        {
            String source = Uri.fromFile(mPage.mFile).toString();
            mEntry.setSource(source);
            mNote.setSource(source);
        }

        {
            entry.setCreated(mNote.getCreated());
            entry.setModified(mNote.getModified());
        }

        {
            String[] array = mNote.getTitle();
            String title = array[0];
            String subtitle = array[1];

            entry.setTitle(title);
            entry.setSubtitle(subtitle);
        }
    }
}
