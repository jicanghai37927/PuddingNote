package com.haiyunshan.pudding.compose.document;

import com.haiyunshan.pudding.compose.format.Format;
import com.haiyunshan.pudding.compose.note.Note;
import com.haiyunshan.pudding.note.dataset.NoteEntry;
import com.haiyunshan.pudding.note.dataset.NoteManager;
import com.haiyunshan.pudding.utils.UUIDUtils;

public class DocumentManager {

    private static DocumentManager sInstance;

    Document mDocument;

    public static DocumentManager instance() {
        if (sInstance == null) {
            sInstance = new DocumentManager();
        }

        return sInstance;
    }

    private DocumentManager() {

    }

    public void setDocument(Document doc) {
        this.mDocument = doc;
    }

    public Document getDocument() {
        return this.mDocument;
    }

    public String create(Document doc) {
        String id = UUIDUtils.next();
        Note note = Note.create(id);

        // 添加到列表中
        {
            NoteManager mgr = NoteManager.instance();
            NoteEntry entry = mgr.obtain(id);
            if (entry == null) {
                entry = mgr.put(id);
            }

            entry.setCreated(note.getCreated());
            entry.setModified(note.getModified());
        }

        // 保存笔记
        {
            note.save();
        }

        // 保存样式
        {
            Format format = Format.create(id);
            format.setFormat(doc.getFormat());

            format.save();
        }

        return id;

    }
}
