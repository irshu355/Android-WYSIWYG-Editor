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
package com.irshu.libs.Components;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.irshu.libs.BaseClass;
import com.irshu.editor.R;
import com.irshu.libs.models.EditorTextStyle;
import com.irshu.libs.models.EditorControl;
import com.irshu.libs.models.EditorType;
import com.irshu.libs.models.Op;
import com.irshu.libs.models.RenderType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
/**
 * Created by mkallingal on 4/30/2016.
 */
public class InputExtensions{
    private Context context;
    private int H1TEXTSIZE =22;
    private int H2TEXTSIZE =18;
    private int H3TEXTSIZE =16;
    private int NORMALTEXTSIZE =14;
    BaseClass base;
    public InputExtensions(BaseClass baseClass,Context context){
        this.base = baseClass;
        this.context = context;
    }
    CharSequence GetSanitizedHtml(String text){
        Spanned __ = Html.fromHtml(text);
        CharSequence toReplace = noTrailingwhiteLines(__);
        return toReplace;
    }
    public void setText(EditText editText, String text){
        CharSequence toReplace = GetSanitizedHtml(text);
        editText.setText(toReplace);
    }
    public void setText(TextView textView, String text){
        CharSequence toReplace = GetSanitizedHtml(text);
        textView.setText(toReplace);
    }
    private TextView GetNewTextView(String text){
        final TextView textView = new TextView(context);
        textView.setGravity(Gravity.BOTTOM);
    //    textView.setTextColor(base.getResources().getColor(R.color.darkertext));
        textView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, base.getResources().getDisplayMetrics()), 1.0f);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 30);
        textView.setLayoutParams(params);
        if(!TextUtils.isEmpty(text)){
            Spanned __ = Html.fromHtml(text);
            CharSequence toReplace = noTrailingwhiteLines(__);
            textView.setText(toReplace);
        }
        return textView;
    }
    public CustomEditText GetNewEditText(String hint, String text) {
        final CustomEditText editText = new CustomEditText(context);
        editText.setGravity(Gravity.BOTTOM);
      //  editText.setTextColor(base.getResources().getColor(R.color.darkertext));
        editText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, base.getResources().getDisplayMetrics()), 1.0f);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(hint.length()!=0){
            editText.setHint(hint);
        }
        if(text.length()!=0){
            setText(editText, text);
        }
        editText.setTag(base.CreateTag(EditorType.INPUT));
        editText.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.invisible_edit_text));
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base.setActiveView(v);
                //  toggleToolbarProperties(v,null);
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (IsEditTextNull(editText)) {
                        base.deleteFocusedPrevious(editText);
                    }
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0) {
//                    deleteFocusedPrevious(editText);
//                }
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
                        * if user had pressed enter, replace it with <br/>
                        */
                    if (s.charAt(s.length() - 1) == '\n') {
                        text = text.replaceAll("<br>", "");
                        setText(editText, text);
                        int index = base.getParentView().indexOfChild(editText);

                        /*
                        * if user has configured the listener, fire the onTextChanged event
                        */
                        /*
                        * since it was a return key, add a new editText below
                        */
                        InsertEditText(index + 1, "", "");
                    }
                }
                if (base.getEditorListener() != null) {
                    base.getEditorListener().onTextChanged(editText, s);
                }
            }
        });
        return editText;
    }
    public TextView InsertEditText(int position, String hint, String text) {
        if(base.getRenderType() == RenderType.Editor) {
            final CustomEditText view = GetNewEditText(hint, text);
                base.getParentView().addView(view, position);
            base.setActiveView(view);
            if (view.requestFocus()) {
                //   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                base.setActiveView(view);
            }
            return view;
        }else{
            final TextView view = GetNewTextView(text);
            view.setTag(base.CreateTag(EditorType.INPUT));
            base.getParentView().addView(view);
            return view;
        }
    }


    public CustomEditText InsertEditText(int position, CustomEditText editText) {
            if (position == 0) {
                base.getParentView().addView(editText);
            } else {
                base.getParentView().addView(editText, position);
            }
            if (editText.requestFocus()) {
                //   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                base.setActiveView(editText);
            }
            return editText;
    }



    public void UpdateTextStyle(EditorTextStyle style,TextView editText) {
        /// String type = GetControlType(getActiveView());
        try {
            if(editText==null) {
                editText = (EditText) base.getActiveView();
            }
            EditorControl tag= base.GetControlTag(editText);
            if(style== EditorTextStyle.H1){
                if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.H1)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Insert);
                }else{
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, H1TEXTSIZE);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Insert);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Delete);
                }

            }
            else if(style== EditorTextStyle.H2){
                if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.H2)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
                    tag=  base.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Insert);
                }else{
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, H2TEXTSIZE);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Insert);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Delete);
                }
            }
            else if(style== EditorTextStyle.H3){
                if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.H3)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
                    tag=  base.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Insert);
                }else{
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, H3TEXTSIZE);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H3, Op.Insert);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Delete);
                }
            }
            else if(style== EditorTextStyle.NORMAL){
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMALTEXTSIZE);
                tag= base.UpdateTagStyle(tag, EditorTextStyle.H1, Op.Delete);
                tag= base.UpdateTagStyle(tag, EditorTextStyle.H2, Op.Delete);
                if(!base.ContainsStyle(tag._ControlStyles, EditorTextStyle.NORMAL)) {
                    tag=   base.UpdateTagStyle(tag, EditorTextStyle.NORMAL, Op.Insert);
                }
            }
            else if(style== EditorTextStyle.BOLD){
                if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.BOLD)) {
                    tag=   base.UpdateTagStyle(tag, EditorTextStyle.BOLD, Op.Delete);
                    editText.setTypeface(null, Typeface.NORMAL);
                }else if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.BOLDITALIC)){
                    tag=  base.UpdateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Delete);
                    tag=  base.UpdateTagStyle(tag, EditorTextStyle.ITALIC, Op.Insert);
                    editText.setTypeface(null, Typeface.ITALIC);
                }
                else if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.ITALIC)){
                    tag=  base.UpdateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Insert);
                    tag=  base.UpdateTagStyle(tag, EditorTextStyle.ITALIC, Op.Delete);
                    editText.setTypeface(null, Typeface.BOLD_ITALIC);
                }else{
                    tag=   base.UpdateTagStyle(tag, EditorTextStyle.BOLD, Op.Insert);
                    editText.setTypeface(null, Typeface.BOLD);
                }
            }
            else if(style== EditorTextStyle.ITALIC){
                if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.ITALIC)) {
                    tag=   base.UpdateTagStyle(tag, EditorTextStyle.ITALIC, Op.Delete);
                    editText.setTypeface(null, Typeface.NORMAL);
                }else if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.BOLDITALIC)){
                    tag=  base.UpdateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Delete);
                    tag= base.UpdateTagStyle(tag, EditorTextStyle.BOLD, Op.Insert);
                    editText.setTypeface(null, Typeface.BOLD);
                }
                else if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.BOLD)){
                    tag=  base.UpdateTagStyle(tag, EditorTextStyle.BOLDITALIC, Op.Insert);
                    tag=  base.UpdateTagStyle(tag, EditorTextStyle.BOLD, Op.Delete);
                    editText.setTypeface(null, Typeface.BOLD_ITALIC);
                }else{
                    tag=   base.UpdateTagStyle(tag, EditorTextStyle.ITALIC, Op.Insert);
                    editText.setTypeface(null, Typeface.ITALIC);
                }
            }
            else if(style== EditorTextStyle.INDENT){
                int pBottom= editText.getPaddingBottom();
                int pRight= editText.getPaddingRight();
                int pTop= editText.getPaddingTop();
                if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.INDENT)){
                    tag=   base.UpdateTagStyle(tag, EditorTextStyle.INDENT, Op.Delete);
                    editText.setPadding(0,pTop,pRight,pBottom);
                }else{
                    tag=   base.UpdateTagStyle(tag, EditorTextStyle.INDENT, Op.Insert);
                    editText.setPadding(30,pTop,pRight,pBottom);
                }
            }
            else if(style== EditorTextStyle.OUTDENT){
                int pBottom= editText.getPaddingBottom();
                int pRight= editText.getPaddingRight();
                int pTop= editText.getPaddingTop();
                if(base.ContainsStyle(tag._ControlStyles, EditorTextStyle.INDENT)){
                    tag=   base.UpdateTagStyle(tag, EditorTextStyle.INDENT, Op.Delete);
                    editText.setPadding(0,pTop,pRight,pBottom);
                }
            }
            editText.setTag(tag);
        }
        catch (Exception e){

        }

    }

    public void InsertLink() {
        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this.context);
        inputAlert.setTitle("Add a Link");
        final EditText userInput = new EditText(this.context);
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
        EditorType editorType = base. GetControlType(base.getActiveView());
        EditText editText = (EditText) base.getActiveView();
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
                editText.setText(trimmed);   //"<p><a href='#'>Hello</a> There, how are you?</p>"
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
