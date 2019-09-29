package com.haiyunshan.pudding.compose.event;

public class FormatSpacingEvent {

    public final int mLineSpacing;
    public final int mLetterSpacing;

    public FormatSpacingEvent(int lineMult, int letterSpacing) {
        this.mLineSpacing = lineMult;
        this.mLetterSpacing = letterSpacing;
    }

}
