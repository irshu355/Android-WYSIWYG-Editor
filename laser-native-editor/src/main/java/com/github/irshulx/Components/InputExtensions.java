/*
 * Copyright (C) 2016 Muhammed Irshad
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.irshulx.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.irshulx.EditorCore;
import com.github.irshulx.R;
import com.github.irshulx.Utilities.FontCache;
import com.github.irshulx.models.EditorTextStyle;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.Op;
import com.github.irshulx.models.RenderType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Map;

/**
 * Created by mkallingal on 4/30/2016.
 */
public class InputExtensions {
    public static final int HEADING = 0;
    public static final int CONTENT = 1;
    private int H1TEXTSIZE = 23;
    private int H2TEXTSIZE = 20;
    private int H3TEXTSIZE = 18;
    private int NORMALTEXTSIZE = 16;
    private int fontFace = R.string.fontFamily__serif;
    EditorCore editorCore;
    private Map<Integer, String> contentTypeface;
    private Map<Integer, String> headingTypeface;

    public int getH1TextSize() {
        return this.H1TEXTSIZE;
    }

    public void setH1TextSize(int size) {
        this.H1TEXTSIZE = size;
    }

    public int getH2TextSize() {
        return this.H2TEXTSIZE;
    }

    public void setH2TextSize(int size) {
        this.H2TEXTSIZE = size;
    }

    public int getH3TextSize() {
        return this.H3TEXTSIZE;
    }

    public void setH3TextSize(int size) {
        this.H3TEXTSIZE = size;
    }

    public int getNormalTextSize() {
        return this.NORMALTEXTSIZE;
    }

    public void setNormalTextSize(int size){
        this.NORMALTEXTSIZE = size;
    }

    public String getFontFace() {
        return editorCore.getContext().getResources().getString(fontFace);
    }

    public void setFontFace(int fontFace) {
        this.fontFace = fontFace;
    }


    public Map<Integer, String> getContentTypeface() {
        return contentTypeface;
    }

    public void setContentTypeface(Map<Integer, String> contentTypeface) {
        this.contentTypeface = contentTypeface;
    }

    public Map<Integer, String> getHeadingTypeface() {
        return headingTypeface;
    }

    public void setHeadingTypeface(Map<Integer, String> headingTypeface) {
        this.headingTypeface = headingTypeface;
    }


    public InputExtensions(EditorCore editorCore) {
        this.editorCore = editorCore;
    }

    CharSequence GetSanitizedHtml(String text) {
        Spanned __ = Html.fromHtml(text);
        CharSequence toReplace = noTrailingwhiteLines(__);
        return toReplace;
    }

    public void setText(TextView textView, String text) {
        CharSequence toReplace = GetSanitizedHtml(text);
        textView.setText(toReplace);
    }


    private TextView getNewTextView(String text) {
        final TextView textView = new TextView(new ContextThemeWrapper(this.editorCore.getContext(), R.style.WysiwygEditText));
        addEditableStyling(textView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, (int) editorCore.getContext().getResources().getDimension(R.dimen.edittext_margin_bottom));
        textView.setLayoutParams(params);
        if (!TextUtils.isEmpty(text)) {
            Spanned __ = Html.fromHtml(text);
            CharSequence toReplace = noTrailingwhiteLines(__);
            textView.setText(toReplace);
        }
        return textView;
    }

    public CustomEditText getNewEditTextInst(final String hint, String text) {
        final CustomEditText editText = new CustomEditText(new ContextThemeWrapper(this.editorCore.getContext(), R.style.WysiwygEditText));
        addEditableStyling(editText);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (hint != null) {
            editText.setHint(hint);
        }
        if (text != null) {
            setText(editText, text);
        }
        editText.setTag(editorCore.createTag(EditorType.INPUT));
        editText.setBackgroundDrawable(ContextCompat.getDrawable(this.editorCore.getContext(), R.drawable.invisible_edit_text));
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return editorCore.onKey(v, keyCode, event, editText);
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    editText.clearFocus();
                } else {
                    editorCore.setActiveView(v);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = Html.toHtml(editText.getText());
                Object tag = editText.getTag(R.id.control_tag);
                if (s.length() == 0 && tag != null)
                    editText.setHint(tag.toString());
                if (s.length() > 0) {
                /*
                * if user had pressed enter, replace it with br
                */
                    for (int i = 0; i < s.length(); i++) {
                        if (s.charAt(i) == '\n') {
                            CharSequence subChars = s.subSequence(0, i);
                            SpannableStringBuilder ssb = new SpannableStringBuilder(subChars);
                            text = Html.toHtml(ssb);
                            if (text.length() > 0)
                                setText(editText, text);
                            else
                                s.clear();
                            int index = editorCore.getParentView().indexOfChild(editText);
                    /* if the index was 0, set the placeholder to empty, behaviour happens when the user just press enter
                     */
                            if (index == 0) {
                                editText.setHint(null);
                                editText.setTag(R.id.control_tag, hint);
                            }
                            int position = index + 1;
                            String newText = null;
                            int lastIndex = s.length() - 1;
                            int nextIndex = i + 1;
                            if (nextIndex < lastIndex)
                                newText = s.subSequence(nextIndex, lastIndex).toString();
                            insertEditText(position, hint, newText);
                        }
                    }
                }
                if (editorCore.getEditorListener() != null) {
                    editorCore.getEditorListener().onTextChanged(editText, s);
                }
            }
        });
        return editText;
    }

    private boolean isLastText(int index) {
        if (index == 0)
            return false;
        View view = editorCore.getParentView().getChildAt(index - 1);
        EditorType type = editorCore.getControlType(view);
        return type == EditorType.INPUT;
    }

    private void addEditableStyling(TextView editText) {
        editText.setTypeface(getTypeface(CONTENT, Typeface.NORMAL));
        editText.setFocusableInTouchMode(true);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);

    }


    public TextView insertEditText(int position, String hint, String text) {
        String nextHint = isLastText(position) ? null : editorCore.placeHolder;
        if (editorCore.getRenderType() == RenderType.Editor) {
            final CustomEditText view = getNewEditTextInst(nextHint, text);
            editorCore.getParentView().addView(view, position);
            editorCore.setActiveView(view);
            final android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setFocus(view);
                }
            }, 0);
            editorCore.setActiveView(view);
            return view;
        } else {
            final TextView view = getNewTextView(text);
            view.setTag(editorCore.createTag(EditorType.INPUT));
            editorCore.getParentView().addView(view);
            return view;
        }
    }


    private EditorControl reWriteTags(EditorControl tag, EditorTextStyle styleToAdd) {
        tag = editorCore.updateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
        tag = editorCore.updateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
        tag = editorCore.updateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
        tag = editorCore.updateTagStyle(tag, EditorTextStyle.NORMAL, Op.Delete);
        tag = editorCore.updateTagStyle(tag, styleToAdd, Op.Insert);
        return tag;
    }

    public boolean isEditorTextStyleHeaders(EditorTextStyle editorTextStyle) {
        return editorTextStyle == EditorTextStyle.H1 || editorTextStyle == EditorTextStyle.H2 || editorTextStyle == EditorTextStyle.H3;
    }

    public boolean isEditorTextStyleContentStyles(EditorTextStyle editorTextStyle) {
        return editorTextStyle == EditorTextStyle.BOLD || editorTextStyle == EditorTextStyle.BOLDITALIC || editorTextStyle == EditorTextStyle.ITALIC;
    }


    public int getTextStyleFromStyle(EditorTextStyle editorTextStyle) {
        if (editorTextStyle == EditorTextStyle.H1)
            return H1TEXTSIZE;
        if (editorTextStyle == EditorTextStyle.H2)
            return H2TEXTSIZE;
        if (editorTextStyle == EditorTextStyle.H3)
            return H3TEXTSIZE;
        return NORMALTEXTSIZE;
    }

    private void updateTextStyle(TextView editText, EditorTextStyle editorTextStyle) {
        EditorControl tag;
        if (editText == null) {
            editText = (EditText) editorCore.getActiveView();
        }
        EditorControl editorControl = editorCore.getControlTag(editText);
        if (isEditorTextStyleHeaders(editorTextStyle)) {
            if (editorCore.containsStyle(editorControl._ControlStyles, editorTextStyle)) {
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
                editText.setTypeface(getTypeface(CONTENT, Typeface.NORMAL));
                tag = reWriteTags(editorControl, EditorTextStyle.NORMAL);
            } else {
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, getTextStyleFromStyle(editorTextStyle));
                editText.setTypeface(getTypeface(HEADING, Typeface.BOLD));
                tag = reWriteTags(editorControl, editorTextStyle);
            }
            editText.setTag(tag);
        }
    }

    private boolean containsHeaderTextStyle(EditorControl tag) {
        for (EditorTextStyle item : tag._ControlStyles) {
            if (isEditorTextStyleHeaders(item)) {
                return true;
            }
            continue;
        }
        return false;
    }


    public void boldifyText(EditorControl tag, TextView editText, int textMode) {
        if (editorCore.containsStyle(tag._ControlStyles, EditorTextStyle.BOLD)) {
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.BOLD, Op.Delete);
            editText.setTypeface(getTypeface(textMode, Typeface.NORMAL));
        } else if (editorCore.containsStyle(tag._ControlStyles, EditorTextStyle.BOLDITALIC)) {
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Delete);
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.ITALIC, Op.Insert);
            editText.setTypeface(getTypeface(textMode, Typeface.ITALIC));
        } else if (editorCore.containsStyle(tag._ControlStyles, EditorTextStyle.ITALIC)) {
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Insert);
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.ITALIC, Op.Delete);
            editText.setTypeface(getTypeface(textMode, Typeface.BOLD_ITALIC));
        } else {
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.BOLD, Op.Insert);
            editText.setTypeface(getTypeface(textMode, Typeface.BOLD));
        }
        editText.setTag(tag);
    }

    public void italicizeText(EditorControl tag, TextView editText, int textMode) {

        if (editorCore.containsStyle(tag._ControlStyles, EditorTextStyle.ITALIC)) {
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.ITALIC, Op.Delete);
            editText.setTypeface(getTypeface(textMode, Typeface.NORMAL));
        } else if (editorCore.containsStyle(tag._ControlStyles, EditorTextStyle.BOLDITALIC)) {
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Delete);
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.BOLD, Op.Insert);
            editText.setTypeface(getTypeface(textMode, Typeface.BOLD));
        } else if (editorCore.containsStyle(tag._ControlStyles, EditorTextStyle.BOLD)) {
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Insert);
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.BOLD, Op.Delete);
            editText.setTypeface(getTypeface(textMode, Typeface.BOLD_ITALIC));
        } else {
            tag = editorCore.updateTagStyle(tag, EditorTextStyle.ITALIC, Op.Insert);
            editText.setTypeface(getTypeface(textMode, Typeface.ITALIC));
        }
        editText.setTag(tag);
    }

    public void UpdateTextStyle(EditorTextStyle style, TextView editText) {
        /// String type = getControlType(getActiveView());
        try {
            if (editText == null) {
                editText = (EditText) editorCore.getActiveView();
            }
            EditorControl tag = editorCore.getControlTag(editText);

            if (isEditorTextStyleHeaders(style)) {
                updateTextStyle(editText, style);
                return;
            }
            if (isEditorTextStyleContentStyles(style)) {
                boolean containsHeadertextStyle = containsHeaderTextStyle(tag);
                if (style == EditorTextStyle.BOLD) {
                    boldifyText(tag, editText, containsHeadertextStyle ? HEADING : CONTENT);
                } else if (style == EditorTextStyle.ITALIC) {
                    italicizeText(tag, editText, containsHeadertextStyle ? HEADING : CONTENT);
                }
                return;
            }
            if (style == EditorTextStyle.INDENT) {
                int pBottom = editText.getPaddingBottom();
                int pRight = editText.getPaddingRight();
                int pTop = editText.getPaddingTop();
                if (editorCore.containsStyle(tag._ControlStyles, EditorTextStyle.INDENT)) {
                    tag = editorCore.updateTagStyle(tag, EditorTextStyle.INDENT, Op.Delete);
                    editText.setPadding(0, pTop, pRight, pBottom);
                    editText.setTag(tag);
                } else {
                    tag = editorCore.updateTagStyle(tag, EditorTextStyle.INDENT, Op.Insert);
                    editText.setPadding(30, pTop, pRight, pBottom);
                    editText.setTag(tag);
                }
            } else if (style == EditorTextStyle.OUTDENT) {
                int pBottom = editText.getPaddingBottom();
                int pRight = editText.getPaddingRight();
                int pTop = editText.getPaddingTop();
                if (editorCore.containsStyle(tag._ControlStyles, EditorTextStyle.INDENT)) {
                    tag = editorCore.updateTagStyle(tag, EditorTextStyle.INDENT, Op.Delete);
                    editText.setPadding(0, pTop, pRight, pBottom);
                    editText.setTag(tag);
                }
            }
        } catch (Exception e) {

        }

    }

    public void insertLink() {
        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this.editorCore.getContext());
        inputAlert.setTitle("Add a Link");
        final EditText userInput = new EditText(this.editorCore.getContext());
        //dont forget to add some margins on the left and right to match the title
        userInput.setHint("type the URL here");
        userInput.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        inputAlert.setView(userInput);
        inputAlert.setPositiveButton("Insert", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInputValue = userInput.getText().toString();
                insertLink(userInputValue);
            }
        });
        inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = inputAlert.create();
        alertDialog.show();
    }

    public void insertLink(String uri) {
        EditorType editorType = editorCore.getControlType(editorCore.getActiveView());
        EditText editText = (EditText) editorCore.getActiveView();
        if (editorType == EditorType.INPUT || editorType == EditorType.UL_LI) {
            String text = Html.toHtml(editText.getText());
            if (TextUtils.isEmpty(text))
                text = "<p dir=\"ltr\"></p>";
            text = trimLineEnding(text);
            Document _doc = Jsoup.parse(text);
            Elements x = _doc.select("p");
            String existing = x.get(0).html();
            x.get(0).html(existing + " <a href='" + uri + "'>" + uri + "</a>");
            Spanned toTrim = Html.fromHtml(x.toString());
            CharSequence trimmed = noTrailingwhiteLines(toTrim);
            editText.setText(trimmed);   //
            editText.setSelection(editText.getText().length());
        }
    }

    public CharSequence noTrailingwhiteLines(CharSequence text) {
        if (text.length() == 0)
            return text;
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    public boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    private String trimLineEnding(String s) {
        if (s.charAt(s.length() - 1) == '\n') {
            String formatted = s.toString().substring(0, s.length() - 1);
            return formatted;
        }
        return s;
    }

    /**
     * returns the appropriate typeface
     *
     * @param mode  => whether heading (0) or content(1)
     * @param style => NORMAL, BOLD, BOLDITALIC, ITALIC
     * @return typeface
     */
    public Typeface getTypeface(int mode, int style) {
        if (mode == HEADING && headingTypeface == null) {
            return Typeface.create(getFontFace(), style);
        } else if (mode == CONTENT && contentTypeface == null) {
            return Typeface.create(getFontFace(), style);
        }
        if (mode == HEADING && !headingTypeface.containsKey(style)) {
            throw new IllegalArgumentException("the provided fonts for heading is missing the varient for this style. Please checkout the documentation on adding custom fonts.");
        } else if (mode == CONTENT && !headingTypeface.containsKey(style)) {
            throw new IllegalArgumentException("the provided fonts for content is missing the varient for this style. Please checkout the documentation on adding custom fonts.");
        }
        if (mode == HEADING) {
            return FontCache.get(headingTypeface.get(style), editorCore.getContext());
        } else {
            return FontCache.get(contentTypeface.get(style), editorCore.getContext());
        }
    }

    public void setFocus(CustomEditText view) {
        view.requestFocus();
        InputMethodManager mgr = (InputMethodManager) editorCore.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        view.setSelection(view.getText().length());
        editorCore.setActiveView(view);
    }


    public void setFocusToNext(int startIndex) {
        for (int i = startIndex; i < editorCore.getParentView().getChildCount(); i++) {
            View view = editorCore.getParentView().getChildAt(i);
            EditorType editorType = editorCore.getControlType(view);
            if (editorType == EditorType.hr || editorType == EditorType.img || editorType == EditorType.map || editorType == EditorType.none)
                continue;
            if (editorType == EditorType.INPUT) {
                setFocus((CustomEditText) view);
                break;
            }
            if (editorType == EditorType.ol || editorType == EditorType.ul) {
                editorCore.getListItemExtensions().setFocusToList(view, ListItemExtensions.POSITION_START);
                editorCore.setActiveView(view);
            }
        }
    }

    public CustomEditText getEditTextPrevious(int startIndex) {
        CustomEditText customEditText = null;
        for (int i = 0; i < startIndex; i++) {
            View view = editorCore.getParentView().getChildAt(i);
            EditorType editorType = editorCore.getControlType(view);
            if (editorType == EditorType.hr || editorType == EditorType.img || editorType == EditorType.map || editorType == EditorType.none)
                continue;
            if (editorType == EditorType.INPUT) {
                customEditText = (CustomEditText) view;
                continue;
            }
            if (editorType == EditorType.ol || editorType == EditorType.ul) {
                editorCore.getListItemExtensions().setFocusToList(view, ListItemExtensions.POSITION_START);
                editorCore.setActiveView(view);
            }
        }
        return customEditText;
    }

    public void setFocusToPrevious(int startIndex) {
        for (int i = startIndex; i > 0; i--) {
            View view = editorCore.getParentView().getChildAt(i);
            EditorType editorType = editorCore.getControlType(view);
            if (editorType == EditorType.hr || editorType == EditorType.img || editorType == EditorType.map || editorType == EditorType.none)
                continue;
            if (editorType == EditorType.INPUT) {
                setFocus((CustomEditText) view);
                break;
            }
            if (editorType == EditorType.ol || editorType == EditorType.ul) {
                editorCore.getListItemExtensions().setFocusToList(view, ListItemExtensions.POSITION_START);
                editorCore.setActiveView(view);
            }
        }
    }

}
