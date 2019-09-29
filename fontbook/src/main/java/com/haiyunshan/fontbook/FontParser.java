package com.haiyunshan.fontbook;

import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;

import java.io.File;
import java.io.IOException;

public class FontParser {

    OTFParser mParser;

    public FontParser() {
        this.mParser = new OTFParser(false, true);
    }

    public FontTable parse(File file) throws IOException {
        OpenTypeFont font = mParser.parse(file);
        NamingTable table = font.getNaming();
        font.close();

        return new FontTable(table);
    }

}
