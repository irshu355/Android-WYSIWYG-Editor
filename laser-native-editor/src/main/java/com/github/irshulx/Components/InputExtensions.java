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
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.irshulx.EditorCore;
import com.github.irshulx.R;
import com.github.irshulx.models.EditorTextStyle;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.Op;
import com.github.irshulx.models.RenderType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by mkallingal on 4/30/2016.
 */
public class InputExtensions{
    private int H1TEXTSIZE =23;
    private int H2TEXTSIZE =20;
    private int H3TEXTSIZE =18;
    private int NORMALTEXTSIZE =16;
    private int fontFace=R.string.fontFamily__serif;
    private float lineSpacing=9.0f;
    EditorCore editorCore;

    public int getH1TextSize(){
        return this.H1TEXTSIZE;
    }
    public void setH1TextSize(int size)
    {
        this.H1TEXTSIZE=size;
    }
    public int getH2TextSize(){
        return this.H2TEXTSIZE;
    }
    public void setH2TextSize(int size)
    {
        this.H2TEXTSIZE=size;
    }

    public int getH3TextSize(){
        return this.H3TEXTSIZE;
    }
    public void setH3TextSize(int size)
    {
        this.H3TEXTSIZE=size;
    }

    public int getNormtalTextSize(){
        return this.NORMALTEXTSIZE;
    }

    public String getFontFace(){
        return editorCore.getContext().getResources().getString(fontFace);
    }
    public void setFontFace(int fontFace){
        this.fontFace=fontFace;
    }

    public float getLineSpacing(){
        return this.lineSpacing;
    }
    public void setLineSpacing(float value){
        this.lineSpacing=value;
    }

    public InputExtensions(EditorCore editorCore){
        this.editorCore = editorCore;
    }
    CharSequence GetSanitizedHtml(String text){
        Spanned __ = Html.fromHtml(text);
        CharSequence toReplace = noTrailingwhiteLines(__);
        return toReplace;
    }

    public void setText(TextView textView, String text){
        CharSequence toReplace = GetSanitizedHtml(text);
        textView.setText(toReplace);
    }
    private TextView GetNewTextView(String text){
        final TextView textView = new TextView(this.editorCore.getContext());
        textView.setTypeface(Typeface.create(getFontFace(), Typeface.NORMAL));
        textView.setGravity(Gravity.BOTTOM);
        textView.setTextColor(editorCore.getResources().getColor(R.color.darkertext));
        textView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.getLineSpacing(), editorCore.getResources().getDisplayMetrics()), 1.0f);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setPadding(textView.getPaddingLeft(),textView.getPaddingTop(),textView.getPaddingRight(),30);
        textView.setLayoutParams(params);
        if(!TextUtils.isEmpty(text)){
            Spanned __ = Html.fromHtml(text);
            CharSequence toReplace = noTrailingwhiteLines(__);
            textView.setText(toReplace);
        }
        return textView;
    }
    public CustomEditText GetNewEditTextInst(String hint, String text) {
        final CustomEditText editText = new CustomEditText(this.editorCore.getContext());
        editText.setTypeface(Typeface.create( getFontFace() ,Typeface.NORMAL));
        editText.setGravity(Gravity.BOTTOM);
        editText.setFocusableInTouchMode(true);
      //  editText.setTextColor(editorCore.getResources().getColor(R.color.darkertext));
        editText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.getLineSpacing(), editorCore.getResources().getDisplayMetrics()), 1.0f);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(hint!=null){
            editText.setHint(hint);
        }
        if(text!=null){
            setText(editText, text);
        }
        editText.setTag(editorCore.CreateTag(EditorType.INPUT));
        editText.setBackgroundDrawable(this.editorCore.getContext().getResources().getDrawable(R.drawable.invisible_edit_text));
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (IsEditTextNull(editText)) {
                        editorCore.deleteFocusedPrevious(editText);
                    }
                }
                return false;
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    editText.clearFocus();
                }else{
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
                if (s.length() > 0) {
                        /*
                        * if user had pressed enter, replace it with br
                        */
                    if (s.charAt(s.length() - 1) == '\n') {
                        text = text.replaceAll("<br>", "");
                        if(text.length()>0) {
                            setText(editText, text);
                        }else{
                           editText.getText().clear();
                        }
                        int index = editorCore.getParentView().indexOfChild(editText);
                        /* if the index was 0, set the placeholder to empty
                         */
                        if(index==0){
                            editText.setHint(null);
                        }

                        /*
                        * if user has configured the listener, fire the onTextChanged event
                        */
                        /*
                        * since it was a return key, add a new editText below
                        */
                        InsertEditText(index + 1, null, null);
                    }
                }
                if (editorCore.getEditorListener() != null) {
                    editorCore.getEditorListener().onTextChanged(editText, s);
                }
            }
        });
        return editText;
    }


    public TextView InsertEditText(int position, String hint, String text) {
        if(editorCore.getRenderType() == RenderType.Editor) {
            final CustomEditText view = GetNewEditTextInst(hint, text);
                editorCore.getParentView().addView(view, position);
            editorCore.setActiveView(view);
            final android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.requestFocus();
                    InputMethodManager mgr = (InputMethodManager) editorCore.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                    view.setSelection(view.getText().length());
                    editorCore.setActiveView(view);
                }
            }, 0);
            editorCore.setActiveView(view);
            return view;
        }else{
            final TextView view = GetNewTextView(text);
            view.setTag(editorCore.CreateTag(EditorType.INPUT));
            editorCore.getParentView().addView(view);
            return view;
        }
    }

    public void UpdateTextStyle(EditorTextStyle style,TextView editText) {
        /// String type = GetControlType(getActiveView());
        try {
            if(editText==null) {
                editText = (EditText) editorCore.getActiveView();
            }
            EditorControl tag= editorCore.GetControlTag(editText);
            if(style== EditorTextStyle.H1){
                if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.H1)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Insert);
                }else{
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, H1TEXTSIZE);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Insert);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Delete);
                }

            }
            else if(style== EditorTextStyle.H2){
                if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.H2)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
                    tag=  editorCore.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Insert);
                }else{
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, H2TEXTSIZE);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Insert);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Delete);
                }
            }
            else if(style== EditorTextStyle.H3){
                if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.H3)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
                    tag=  editorCore.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Insert);
                }else{
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, H3TEXTSIZE);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Insert);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Delete);
                }
            }
            else if(style== EditorTextStyle.NORMAL){
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
                tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                if(!editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.NORMAL)) {
                    tag=   editorCore.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Insert);
                }
            }
            else if(style== EditorTextStyle.BOLD){
                if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.BOLD)) {
                    tag=   editorCore.UpdateTagStyle(tag, EditorTextStyle.BOLD, Op.Delete);
                    editText.setTypeface(Typeface.create(getFontFace(), Typeface.NORMAL));
                }else if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.BOLDITALIC)){
                    tag=  editorCore.UpdateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Delete);
                    tag=  editorCore.UpdateTagStyle(tag, EditorTextStyle.ITALIC, Op.Insert);
                    editText.setTypeface(Typeface.create(getFontFace(), Typeface.ITALIC));
                }
                else if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.ITALIC)){
                    tag=  editorCore.UpdateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Insert);
                    tag=  editorCore.UpdateTagStyle(tag, EditorTextStyle.ITALIC, Op.Delete);
                    editText.setTypeface(Typeface.create(getFontFace(), Typeface.BOLD_ITALIC));
                }else{
                    tag=   editorCore.UpdateTagStyle(tag, EditorTextStyle.BOLD, Op.Insert);
                    editText.setTypeface(Typeface.create(getFontFace(), Typeface.BOLD));
                }
            }
            else if(style== EditorTextStyle.ITALIC){
                if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.ITALIC)) {
                    tag=   editorCore.UpdateTagStyle(tag, EditorTextStyle.ITALIC, Op.Delete);
                    editText.setTypeface(Typeface.create(getFontFace(), Typeface.NORMAL));
                }else if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.BOLDITALIC)){
                    tag=  editorCore.UpdateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Delete);
                    tag= editorCore.UpdateTagStyle(tag, EditorTextStyle.BOLD, Op.Insert);
                    editText.setTypeface(Typeface.create(getFontFace(), Typeface.BOLD));
                }
                else if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.BOLD)){
                    tag=  editorCore.UpdateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Insert);
                    tag=  editorCore.UpdateTagStyle(tag, EditorTextStyle.BOLD, Op.Delete);
                    editText.setTypeface(Typeface.create(getFontFace(), Typeface.BOLD_ITALIC));
                }else{
                    tag=   editorCore.UpdateTagStyle(tag, EditorTextStyle.ITALIC, Op.Insert);
                    editText.setTypeface(Typeface.create(getFontFace(), Typeface.ITALIC));
                }
            }
            else if(style== EditorTextStyle.INDENT){
                int pBottom= editText.getPaddingBottom();
                int pRight= editText.getPaddingRight();
                int pTop= editText.getPaddingTop();
                if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.INDENT)){
                    tag=   editorCore.UpdateTagStyle(tag, EditorTextStyle.INDENT, Op.Delete);
                    editText.setPadding(0,pTop,pRight,pBottom);
                }else{
                    tag=   editorCore.UpdateTagStyle(tag, EditorTextStyle.INDENT, Op.Insert);
                    editText.setPadding(30,pTop,pRight,pBottom);
                }
            }
            else if(style== EditorTextStyle.OUTDENT){
                int pBottom= editText.getPaddingBottom();
                int pRight= editText.getPaddingRight();
                int pTop= editText.getPaddingTop();
                if(editorCore.ContainsStyle(tag._ControlStyles, EditorTextStyle.INDENT)){
                    tag=   editorCore.UpdateTagStyle(tag, EditorTextStyle.INDENT, Op.Delete);
                    editText.setPadding(0,pTop,pRight,pBottom);
                }
            }
            editText.setTag(tag);
        }
        catch (Exception e){

        }

    }

    public void InsertLink() {
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
                InsertLink(userInputValue);
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

    private void InsertLink(String uri) {
        EditorType editorType = editorCore. GetControlType(editorCore.getActiveView());
        EditText editText = (EditText) editorCore.getActiveView();
        if (editorType == EditorType.INPUT ||editorType==EditorType.UL_LI) {
            String text = Html.toHtml(editText.getText());
            text=trimLineEnding(text);
            Document _doc= Jsoup.parse(text);
            Elements x= _doc.select("p");

            if(x.size()>0){
                String existing= x.get(0).html();
                x.get(0).html(existing + " <a href='" + uri + "'>" + uri + "</a>");
                Spanned toTrim=Html.fromHtml(x.toString());
                CharSequence trimmed = noTrailingwhiteLines(toTrim);
                editText.setText(trimmed);   //
                editText.setSelection(editText.getText().length());
            }
        }
    }

    public CharSequence noTrailingwhiteLines(CharSequence text) {
        if(text.length()==0)
            return text;
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }
    boolean IsEditTextNull(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }
    private String trimLineEnding(String s) {
        if (s.charAt(s.length() - 1) == '\n') {
            String formatted = s.toString().substring(0, s.length() - 1);
            return formatted;
        }
        return  s;
    }

}
