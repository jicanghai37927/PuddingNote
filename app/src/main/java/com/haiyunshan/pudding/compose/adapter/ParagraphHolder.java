package com.haiyunshan.pudding.compose.adapter;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.haiyunshan.pudding.R;
import com.haiyunshan.pudding.background.dataset.BackgroundManager;
import com.haiyunshan.pudding.compose.ComposeFragment;
import com.haiyunshan.pudding.compose.document.BaseItem;
import com.haiyunshan.pudding.compose.document.Document;
import com.haiyunshan.pudding.compose.document.ParagraphItem;
import com.haiyunshan.pudding.compose.document.PictureItem;
import com.haiyunshan.pudding.compose.format.TextFormat;
import com.haiyunshan.pudding.compose.helper.ParagraphSpanHelper;
import com.haiyunshan.pudding.compose.state.BaseState;
import com.haiyunshan.pudding.compose.state.EditState;
import com.haiyunshan.pudding.compose.state.FormatState;
import com.haiyunshan.pudding.compose.state.ReadState;
import com.haiyunshan.pudding.compose.widget.ParagraphEditText;
import com.haiyunshan.pudding.font.dataset.FontManager;
import com.haiyunshan.pudding.style.HighlightSpan;
import com.haiyunshan.pudding.style.LineSpanRender;
import com.haiyunshan.pudding.utils.WindowUtils;

import java.io.File;
import java.util.ArrayList;

public class ParagraphHolder extends BaseHolder<ParagraphItem> implements TextWatcher {

    ParagraphEditText mEdit;

    ParagraphSpanHelper mSpanHelper;

    public static final ParagraphHolder create(ComposeFragment parent, ViewGroup container) {
        LayoutInflater inflater = parent.getLayoutInflater();
        int resource = R.layout.layout_compose_paragraph_item;
        View view = inflater.inflate(resource, container, false);

        ParagraphHolder holder = new ParagraphHolder(parent, view);
        return holder;
    }

    public ParagraphHolder(ComposeFragment parent, View itemView) {
        super(parent, itemView);

        this.mEdit = itemView.findViewById(R.id.edit_paragraph);
        mEdit.addRender(new LineSpanRender(mEdit, HighlightSpan.class));

        this.mSpanHelper = new ParagraphSpanHelper(this, mEdit);
    }

    @Override
    public void onBind(int position, ParagraphItem item) {
        super.onBind(position, item);

        mEdit.removeTextChangedListener(this);

        {
            this.applyFormat(mEdit, mDocument.getFormat().getParagraph());
        }

        {
            CharSequence text = item.getText();
            mEdit.setText(text);

            mEdit.prepareCursorControllers();

//            mEdit.setCustomSelectionActionModeCallback(mCustomSelectionActionModeCallback);
        }

        mEdit.addTextChangedListener(this);

        BaseState state = getState();
        if (state != null) {
            if (state instanceof ReadState) {

                mEdit.setCursorVisible(false);
                mEdit.setShowSoftInputOnFocus(false);
                mEdit.setFocusable(false);
                mEdit.setFocusableInTouchMode(false);
                mEdit.setEnabled(false);

//                mEdit.setOnSelectionChangeListener(null);
//                mEdit.setCustomInsertionActionModeCallback(mCustomInsertionActionModeCallback);
//                mEdit.setCustomSelectionActionModeCallback(mCustomSelectionActionModeCallback);

            } else if (state instanceof EditState) {

                mEdit.setCursorVisible(true);
                mEdit.setShowSoftInputOnFocus(true);
                mEdit.setFocusable(true);
                mEdit.setFocusableInTouchMode(true);
                mEdit.setEnabled(true);

//                mEdit.setOnSelectionChangeListener(null);
//                mEdit.setCustomInsertionActionModeCallback(null);
//                mEdit.setCustomSelectionActionModeCallback(null);

            } else if (state instanceof FormatState) {
                mEdit.setCursorVisible(false);
                mEdit.setShowSoftInputOnFocus(false);
                mEdit.setFocusable(false);
                mEdit.setFocusableInTouchMode(false);
                mEdit.setEnabled(false);

//                mEdit.setOnSelectionChangeListener(mSpanHelper);
//                mEdit.setCustomInsertionActionModeCallback(null);
//                mEdit.setCustomSelectionActionModeCallback(null);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();

    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
    }

    @Override
    public void onSave() {
        super.onSave();

        Editable text = mEdit.getText();
        mItem.setText(text);
    }

    @Override
    public boolean insertPicture(String[] array) {
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (start < 0 || end < 0) {
            return false;
        }

        if (start != end) {
            return false;
        }

        boolean changed = false;

        ArrayList<BaseItem> list = new ArrayList<>(array.length * 2 + 1);

        // 创建新对象
        {
            int length = array.length;
            for (int i = 0; i < length; i++) {
                String path = array[i];

                {
                    File file = new File(path);

                    PictureItem p = PictureItem.create(mDocument, file);
                    list.add(p);
                }

                // 每张图片后，追加一个段落
                if ((i + 1) != length) {
                    ParagraphItem p = ParagraphItem.create(mDocument,"");
                    list.add(p);
                }
            }

            boolean shouldSplit = true;

            {
                int pos = start;
                if (pos == mEdit.length()) {
                    int index = mDocument.indexOf(mItem);
                    if (index + 1 < mDocument.size()) {
                        BaseItem item = mDocument.get(index + 1);
                        shouldSplit = (!(item instanceof ParagraphItem));
                    }
                }
            }

            if (shouldSplit) {

                changed = true;

                int pos = start;
                Editable text = mEdit.getText();
                CharSequence s1 = text.subSequence(0, pos);
                CharSequence s2 = text.subSequence(pos, text.length());

                mItem.setText(s1);
                mItem.setSelection(pos, pos);

                ParagraphItem item2 = ParagraphItem.create(mDocument, s2);
                item2.setText(s2);

                list.add(item2);
            }
        }

        // 更新Document
        {
            int position = mDocument.indexOf(this.mItem);
            int index = position + 1;
            for (BaseItem item : list) {
                mDocument.add(index, item);

                index++;
            }
        }

        // 更新Adapter
        {
            int position = mAdapter.indexOf(mItem);
            int index = position + 1;

            for (BaseItem p : list) {
                mAdapter.add(index, p);

                index++;
            }

            int count = list.size();

            if (changed) {
                mAdapter.notifyItemChanged(position);
            }

            mAdapter.notifyItemRangeInserted(position + 1, count);
        }

        {
            int pos = mAdapter.indexOf(list.get(0));
            if (pos >= 0) {
                mRecyclerView.scrollToPosition(pos);
            }
        }

        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        this.setModified(System.currentTimeMillis());
    }

    public int getSelectionStart() {
        int start = mEdit.getSelectionStart();
        int end = mEdit.getSelectionEnd();

        int pos = Math.min(start, end);
        return pos;
    }

    public int getSelectionEnd() {
        int start = mEdit.getSelectionStart();
        int end = mEdit.getSelectionEnd();

        int pos = Math.max(start, end);
        return pos;
    }

    public boolean isSelectedAll() {
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();

        return (start == 0 && end == mEdit.length());
    }

    static void applyFormat(ParagraphEditText edit, TextFormat format) {

        String font = format.getFont();

        Typeface tf = FontManager.getInstance().getTypeface(font);
        edit.setTypeface(tf);

        int textSize = format.getTextSize();
        edit.setTextSize(textSize);

        int textColor = format.getTextColor();
        edit.setTextColor(textColor);

        int cursorColor = BackgroundManager.getCursorColor(textColor);
        edit.setCursorDrawableColorByReflect(cursorColor);

        int selectionColor = BackgroundManager.getHighlight(textColor);
        edit.setHighlightColor(selectionColor);

        float lineSpacingMult = format.getLineSpacingMultiplier() * 1.f / 100;
        edit.setLineSpacing(0, lineSpacingMult);

        float letterMult = format.getLetterSpacingMultiplier();
        letterMult = (letterMult - 100) * 1.f / 100;
        edit.setLetterSpacing(letterMult);

        {
            int left = edit.getPaddingLeft();
            int top = edit.getPaddingTop();
            int right = edit.getPaddingRight();
            int bottom = edit.getPaddingBottom();

            left = (int)(WindowUtils.dp2px(format.getPaddingHorizontal()));
            right = (int)(WindowUtils.dp2px(format.getPaddingHorizontal()));

            edit.setPadding(left, top, right, bottom);
        }

        {
            Layout.Alignment alignment = format.getAlignment();

            int align = TextView.TEXT_ALIGNMENT_TEXT_START;
            align = (alignment == Layout.Alignment.ALIGN_CENTER) ? TextView.TEXT_ALIGNMENT_CENTER : align;
            align = (alignment == Layout.Alignment.ALIGN_OPPOSITE) ? TextView.TEXT_ALIGNMENT_TEXT_END : align;

            edit.setTextAlignment(align);
        }

        // 最后适配一下光标高度
        {
            edit.fitCursor();
        }

    }

    public static View createView(LayoutInflater inflater, ViewGroup parent, Document document, ParagraphItem item) {
        int resource = R.layout.layout_compose_paragraph_item;
        View view = inflater.inflate(resource, parent, false);

        ParagraphEditText edit = view.findViewById(R.id.edit_paragraph);

        {
            applyFormat(edit, document.getFormat().getParagraph());
        }

        {
            edit.setCursorVisible(false);
            edit.setShowSoftInputOnFocus(false);
            edit.setFocusable(false);
            edit.setFocusableInTouchMode(false);
            edit.setEnabled(false);
        }

        {
            CharSequence text = item.getText();
            edit.setText(text);
        }

        return view;
    }

    ActionMode.Callback mCustomSelectionActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.clear();

            {
                SpannableString ss = new SpannableString(mContext.getString(R.string.btn_highlight));
                menu.add(Menu.NONE, R.id.highlight, Menu.NONE, ss);
            }

            {
                menu.add(Menu.NONE, android.R.id.copy, Menu.NONE, android.R.string.copy);
            }

            if (!isSelectedAll()) {
                menu.add(Menu.NONE, android.R.id.selectAll, Menu.NONE, android.R.string.selectAll);
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            boolean result = false;

            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.highlight: {
                    mSpanHelper.toggleHighlight();

                    result = true;
                    break;
                }

                case android.R.id.copy: {
                    break;
                }
                case android.R.id.selectAll: {
                    break;
                }
            }

            return result;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };
}
