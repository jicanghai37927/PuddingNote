package com.haiyunshan.pudding.dataset;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BaseDataset<T extends BaseEntry> extends BaseEntry {

    @SerializedName("list")
    protected ArrayList<T> mList;

    public BaseDataset() {
        super(null);

        this.mList = new ArrayList<>();
    }

    public T get(int index) {
        return mList.get(index);
    }

    public List<T> getList() {
        return mList;
    }

    public void add(T e) {
        mList.add(e);
    }

    public int size() {
        return mList.size();
    }

    public boolean remove(T entry) {
        return mList.remove(entry);
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    public T obtain(String id) {
        for (T item : mList) {
            if (item.mId.equalsIgnoreCase(id)) {
                return item;
            }
        }

        return null;
    }
}
