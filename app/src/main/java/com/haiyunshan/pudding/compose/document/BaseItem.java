package com.haiyunshan.pudding.compose.document;

import com.haiyunshan.pudding.compose.note.BaseEntity;

public abstract class BaseItem<T extends BaseEntity> {

    T mEntity;

    Document mDocument;

    BaseItem(Document document, T entity) {
        this.mEntity = entity;

        this.mDocument = document;
    }

    public T getEntity() {
        return mEntity;
    }

    public boolean isEmpty() {
        return false;
    }

    public long getCreated() {
        return mEntity.getCreated();
    }

    public long getModified() {
        return mEntity.getModified();
    }

    public void setModified(long modified) {
        mEntity.setModified(modified);
    }

}