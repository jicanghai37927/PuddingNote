package com.haiyunshan.pudding.font.dataset;

import com.haiyunshan.pudding.dataset.BaseDataset;

public class FontDataset extends BaseDataset<FontEntry> {

    public void setEditable(boolean value) {
        for (FontEntry entry : mList) {
            entry.setEditable(value);
        }
    }

    public void setSort(int value) {
        for (FontEntry entry : mList) {
            entry.setSort(value);
        }
    }

    public FontEntry obtainBySource(String source) {
        for (FontEntry item : mList) {
            if (item.mSource.equalsIgnoreCase(source)) {
                return item;
            }
        }

        return null;
    }

    public FontEntry obtainByUri(String uri) {
        for (FontEntry item : mList) {
            if (item.mUri.equalsIgnoreCase(uri)) {
                return item;
            }
        }

        return null;
    }
}
