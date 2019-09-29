package com.haiyunshan.fontbook;

import android.text.TextUtils;

import org.apache.fontbox.ttf.NameRecord;
import org.apache.fontbox.ttf.NamingTable;

public class FontTable {

    static final int[][] NAMING_ARRAY = new int[][] {
            new int[] {16,                                  NameRecord.PLATFORM_WINDOWS, NameRecord.ENCODING_WINDOWS_UNICODE_BMP, 2052},
            new int[] {NameRecord.NAME_FONT_FAMILY_NAME,    NameRecord.PLATFORM_WINDOWS, NameRecord.ENCODING_WINDOWS_UNICODE_BMP, 2052},
            new int[] {NameRecord.NAME_FULL_FONT_NAME,      NameRecord.PLATFORM_WINDOWS, NameRecord.ENCODING_WINDOWS_UNICODE_BMP, 2052},

            new int[] {16,                                  NameRecord.PLATFORM_WINDOWS, NameRecord.ENCODING_WINDOWS_UNICODE_BMP, 1033},
            new int[] {NameRecord.NAME_FONT_FAMILY_NAME,    NameRecord.PLATFORM_WINDOWS, NameRecord.ENCODING_WINDOWS_UNICODE_BMP, 1033},
            new int[] {NameRecord.NAME_FULL_FONT_NAME,      NameRecord.PLATFORM_WINDOWS, NameRecord.ENCODING_WINDOWS_UNICODE_BMP, 1033},

            new int[] {NameRecord.NAME_FONT_FAMILY_NAME,    NameRecord.PLATFORM_MACINTOSH, NameRecord.ENCODING_MACINTOSH_ROMAN, NameRecord.LANGUGAE_MACINTOSH_ENGLISH}
    };

    String mName;
    int mLanguageId;

    NamingTable mTable;

    FontTable(NamingTable table) {
        this.mTable = table;

        {
            String name = null;

            int nameId;
            int platformId;
            int encodingId;
            int languageId = -1;

            int[][] array = NAMING_ARRAY;
            for (int[] s : array) {
                nameId = s[0];
                platformId = s[1];
                encodingId = s[2];
                languageId = s[3];

                name = table.getName(nameId, platformId, encodingId, languageId);
                if (!TextUtils.isEmpty(name)) {
                    break;
                }
            }

            this.mName = name;
            this.mLanguageId = languageId;
        }
    }

    public String getName() {
        return mName;
    }

    public int getLanguageId() {
        return mLanguageId;
    }
}
