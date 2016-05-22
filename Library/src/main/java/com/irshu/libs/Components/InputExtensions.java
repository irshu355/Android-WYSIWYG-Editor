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
import com.irshu.libs.models.ControlStyles;
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
    BaseClass base;
    public InputExtensions(BaseClass baseClass){
        this.base = baseClass;
        this.context = baseClass.context;
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
        textView.setTextColor(base.resources.getColor(R.color.darkertext));
        textView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, base.resources.getDisplayMetrics()), 1.0f);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, base.NORMALTEXTSIZE);
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
        editText.setTextColor(base.resources.getColor(R.color.darkertext));
        editText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, base.resources.getDisplayMetrics()), 1.0f);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, base.NORMALTEXTSIZE);
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
                base.activeView = v;
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
                        int index = base.parentView.indexOfChild(editText);

                        /*
                        * if user has configured the listener, fire the onTextChanged event
                        */
                        /*
                        * since it was a return key, add a new editText below
                        */
                        InsertEditText(index + 1, "", "");
                    }
                }
                if (base.listener != null) {
                    base.listener.onTextChanged(editText, s);
                }
            }
        });
        return editText;
    }
    public TextView InsertEditText(int position, String hint, String text) {
        if(base.renderType == RenderType.Editor) {
            final CustomEditText view = GetNewEditText(hint, text);
            if (position == 0) {
                base.parentView.addView(view);
            } else {
                base.parentView.addView(view, position);
            }
            base.activeView = view;
            if (view.requestFocus()) {
                //   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                base.activeView = view;
            }
            return view;
        }else{
            final TextView view = GetNewTextView(text);
            view.setTag(base.CreateTag(EditorType.INPUT));
            base.parentView.addView(view);
            return view;
        }
    }


    public CustomEditText InsertEditText(int position, CustomEditText editText) {
            if (position == 0) {
                base.parentView.addView(editText);
            } else {
                base.parentView.addView(editText, position);
            }
            if (editText.requestFocus()) {
                //   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                base.activeView = editText;
            }
            return editText;
    }



    public void UpdateTextStyle(ControlStyles style,TextView editText) {
        /// String type = GetControlType(activeView);
        try {
            if(editText==null) {
                editText = (EditText) base.activeView;
            }
            EditorControl tag= base.GetControlTag(editText);
            if(style==ControlStyles.H1){
                if(base.ContainsStyle(tag._ControlStyles, ControlStyles.H1)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, base.NORMALTEXTSIZE);
                    tag= base.UpdateTagStyle(tag, ControlStyles.H1, Op.Delete);
                    tag= base.UpdateTagStyle(tag, ControlStyles.H2, Op.Delete);
                    tag= base.UpdateTagStyle(tag, ControlStyles.NORMAL, Op.Insert);
                }else{
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, base.H1TEXTSIZE);
                    tag= base.UpdateTagStyle(tag, ControlStyles.H1, Op.Insert);
                    tag= base.UpdateTagStyle(tag, ControlStyles.H2, Op.Delete);
                    tag= base.UpdateTagStyle(tag, ControlStyles.NORMAL, Op.Delete);
                }

            }
            else if(style==ControlStyles.H2){
                if(base.ContainsStyle(tag._ControlStyles, ControlStyles.H2)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, base.NORMALTEXTSIZE);
                    tag=  base.UpdateTagStyle(tag, ControlStyles.H1, Op.Delete);
                    tag= base.UpdateTagStyle(tag, ControlStyles.H2, Op.Delete);
                    tag= base.UpdateTagStyle(tag, ControlStyles.NORMAL, Op.Insert);
                }else{
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, base.H2TEXTSIZE);
                    tag= base.UpdateTagStyle(tag, ControlStyles.H1, Op.Delete);
                    tag= base.UpdateTagStyle(tag, ControlStyles.H2, Op.Insert);
                    tag= base.UpdateTagStyle(tag, ControlStyles.NORMAL, Op.Delete);
                }
            }
            else if(style==ControlStyles.NORMAL){
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, base.NORMALTEXTSIZE);
                tag= base.UpdateTagStyle(tag, ControlStyles.H1, Op.Delete);
                tag= base.UpdateTagStyle(tag, ControlStyles.H2, Op.Delete);
                if(!base.ContainsStyle(tag._ControlStyles, ControlStyles.NORMAL)) {
                    tag=   base.UpdateTagStyle(tag, ControlStyles.NORMAL, Op.Insert);
                }
            }
            else if(style==ControlStyles.BOLD){
                if(base.ContainsStyle(tag._ControlStyles, ControlStyles.BOLD)) {
                    tag=   base.UpdateTagStyle(tag, ControlStyles.BOLD, Op.Delete);
                    editText.setTypeface(null, Typeface.NORMAL);
                }else if(base.ContainsStyle(tag._ControlStyles, ControlStyles.BOLDITALIC)){
                    tag=  base.UpdateTagStyle(tag, ControlStyles.BOLDITALIC, Op.Delete);
                    tag=  base.UpdateTagStyle(tag, ControlStyles.ITALIC, Op.Insert);
                    editText.setTypeface(null, Typeface.ITALIC);
                }
                else if(base.ContainsStyle(tag._ControlStyles, ControlStyles.ITALIC)){
                    tag=  base.UpdateTagStyle(tag, ControlStyles.BOLDITALIC, Op.Insert);
                    tag=  base.UpdateTagStyle(tag, ControlStyles.ITALIC, Op.Delete);
                    editText.setTypeface(null, Typeface.BOLD_ITALIC);
                }else{
                    tag=   base.UpdateTagStyle(tag, ControlStyles.BOLD, Op.Insert);
                    editText.setTypeface(null, Typeface.BOLD);
                }
            }
            else if(style==ControlStyles.ITALIC){
                if(base.ContainsStyle(tag._ControlStyles, ControlStyles.ITALIC)) {
                    tag=   base.UpdateTagStyle(tag, ControlStyles.ITALIC, Op.Delete);
                    editText.setTypeface(null, Typeface.NORMAL);
                }else if(base.ContainsStyle(tag._ControlStyles, ControlStyles.BOLDITALIC)){
                    tag=  base.UpdateTagStyle(tag, ControlStyles.BOLDITALIC, Op.Delete);
                    tag= base.UpdateTagStyle(tag, ControlStyles.BOLD, Op.Insert);
                    editText.setTypeface(null, Typeface.BOLD);
                }
                else if(base.ContainsStyle(tag._ControlStyles, ControlStyles.BOLD)){
                    tag=  base.UpdateTagStyle(tag, ControlStyles.BOLDITALIC, Op.Insert);
                    tag=  base.UpdateTagStyle(tag, ControlStyles.BOLD, Op.Delete);
                    editText.setTypeface(null, Typeface.BOLD_ITALIC);
                }else{
                    tag=   base.UpdateTagStyle(tag, ControlStyles.ITALIC, Op.Insert);
                    editText.setTypeface(null, Typeface.ITALIC);
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
        EditorType editorType = base. GetControlType(base.activeView);
        EditText editText = (EditText) base.activeView;
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
