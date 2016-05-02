package com.irshu.editor.Components;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irshu.editor.BaseClass;
import com.irshu.editor.R;
import com.irshu.editor.models.ControlStyles;
import com.irshu.editor.models.EditorControl;
import com.irshu.editor.models.EditorType;
import com.irshu.editor.models.Op;
import com.irshu.editor.models.RenderType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by mkallingal on 4/30/2016.
 */
public class InputExtensions{
    private Context _Context;
    BaseClass _Base;
    public InputExtensions(BaseClass baseClass){
        this._Base= baseClass;
        this._Context= baseClass._Context;
    }

    public void setText(EditText _EditText, String text){
        Spanned __ = Html.fromHtml(text);
        CharSequence toReplace = noTrailingwhiteLines(__);
        _EditText.setText(toReplace);
    }
    private TextView GetNewTextView(String text){
        final TextView _TextView = new TextView(_Context);
        _TextView.setTextColor(_Base._Resources.getColor(R.color.darkertext));
        _TextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, _Base._Resources.getDisplayMetrics()), 1.0f);
        _TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, _Base.NORMALTEXTSIZE);
        _TextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if(!TextUtils.isEmpty(text)){
            Spanned __ = Html.fromHtml(text);
            CharSequence toReplace = noTrailingwhiteLines(__);
            _TextView.setText(toReplace);
        }
        return _TextView;
    }

    private CustomEditText GetNewEditText(String hint, String text) {
        final CustomEditText _EditText = new CustomEditText(_Context);
        _EditText.setTextColor(_Base._Resources.getColor(R.color.darkertext));
        _EditText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, _Base._Resources.getDisplayMetrics()), 1.0f);
        _EditText.setTextSize(TypedValue.COMPLEX_UNIT_SP,  _Base.NORMALTEXTSIZE);
        _EditText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(hint.length()!=0){
            _EditText.setHint(hint);
        }
        if(text.length()!=0){
            setText(_EditText, text);
        }
        _EditText.setTag(_Base.CreateTag(EditorType.INPUT));
        _EditText.setBackgroundDrawable(_Context.getResources().getDrawable(R.drawable.invisible_edit_text));

        _EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _Base.activeView = v;
                //  toggleToolbarProperties(v,null);
            }
        });

        _EditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (IsEditTextNull(_EditText)) {
                        _Base.deleteFocusedPrevious(_EditText);
                    }
                }
                return false;
            }
        });

        _EditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0) {
//                    deleteFocusedPrevious(_EditText);
//                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = Html.toHtml(_EditText.getText());
                if (s.length() > 0) {
                    if (s.charAt(s.length() - 1) == '\n') {
                        text = text.replaceAll("<br>", "");
                        setText(_EditText, text);
                        int index = _Base._ParentView.indexOfChild(_EditText);
                        InsertEditText(index + 1, "", "");
                    }
                }
            }
        });
        return _EditText;
    }

    public CustomEditText InsertEditText(int position, String hint, String text) {
        if(_Base._RenderType== RenderType.Editor) {
            final CustomEditText _view = GetNewEditText(hint, text);
            if (position == 0) {
                _Base._ParentView.addView(_view);
            } else {
                _Base._ParentView.addView(_view, position);
            }
            _Base.activeView = _view;
            if (_view.requestFocus()) {
                //   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                _Base.activeView = _view;
            }
            return _view;
        }else{
            final TextView _view = GetNewTextView(text);
            _Base._ParentView.addView(_view);
            return  new CustomEditText(this._Context);
        }
    }

    public void UpdateTextStyle(ControlStyles style) {
        /// String type = GetControlType(activeView);
        EditText text;
        try {
            text = (EditText) _Base.activeView;
            EditorControl _Tag= _Base.GetControlTag(text);
            if(style==ControlStyles.H1){
                if(_Base.ContainsStyle(_Tag._ControlStyles, ControlStyles.H1)) {
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, _Base.NORMALTEXTSIZE);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.H1, Op.Delete);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Delete);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Insert);
                }else{
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, _Base.H1TEXTSIZE);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.H1, Op.Insert);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Delete);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Delete);
                }

            }
            else if(style==ControlStyles.H2){
                if(_Base.ContainsStyle(_Tag._ControlStyles, ControlStyles.H2)) {
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, _Base.NORMALTEXTSIZE);
                    _Tag=  _Base.UpdateTagStyle(_Tag, ControlStyles.H1, Op.Delete);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Delete);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Insert);
                }else{
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, _Base.H2TEXTSIZE);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.H1, Op.Delete);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Insert);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Delete);
                }
            }
            else if(style==ControlStyles.NORMAL){
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, _Base.NORMALTEXTSIZE);
                _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.H1, Op.Delete);
                _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Delete);
                if(!_Base.ContainsStyle(_Tag._ControlStyles, ControlStyles.NORMAL)) {
                    _Tag=   _Base.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Insert);
                }
            }
            else if(style==ControlStyles.BOLD){
                if(_Base.ContainsStyle(_Tag._ControlStyles, ControlStyles.BOLD)) {
                    _Tag=   _Base.UpdateTagStyle(_Tag, ControlStyles.BOLD, Op.Delete);
                    text.setTypeface(null, Typeface.NORMAL);
                }else if(_Base.ContainsStyle(_Tag._ControlStyles, ControlStyles.BOLDITALIC)){
                    _Tag=  _Base.UpdateTagStyle(_Tag, ControlStyles.BOLDITALIC, Op.Delete);
                    _Tag=  _Base.UpdateTagStyle(_Tag, ControlStyles.ITALIC, Op.Insert);
                    text.setTypeface(null, Typeface.ITALIC);
                }
                else if(_Base.ContainsStyle(_Tag._ControlStyles, ControlStyles.ITALIC)){
                    _Tag=  _Base.UpdateTagStyle(_Tag, ControlStyles.BOLDITALIC, Op.Insert);
                    _Tag=  _Base.UpdateTagStyle(_Tag, ControlStyles.ITALIC, Op.Delete);
                    text.setTypeface(null, Typeface.BOLD_ITALIC);
                }else{
                    _Tag=   _Base.UpdateTagStyle(_Tag, ControlStyles.BOLD, Op.Insert);
                    text.setTypeface(null, Typeface.BOLD);
                }
            }
            else if(style==ControlStyles.ITALIC){
                if(_Base.ContainsStyle(_Tag._ControlStyles, ControlStyles.ITALIC)) {
                    _Tag=   _Base.UpdateTagStyle(_Tag, ControlStyles.ITALIC, Op.Delete);
                    text.setTypeface(null, Typeface.NORMAL);
                }else if(_Base.ContainsStyle(_Tag._ControlStyles, ControlStyles.BOLDITALIC)){
                    _Tag=  _Base.UpdateTagStyle(_Tag, ControlStyles.BOLDITALIC, Op.Delete);
                    _Tag= _Base.UpdateTagStyle(_Tag, ControlStyles.BOLD, Op.Insert);
                    text.setTypeface(null, Typeface.BOLD);
                }
                else if(_Base.ContainsStyle(_Tag._ControlStyles, ControlStyles.BOLD)){
                    _Tag=  _Base.UpdateTagStyle(_Tag, ControlStyles.BOLDITALIC, Op.Insert);
                    _Tag=  _Base.UpdateTagStyle(_Tag, ControlStyles.BOLD, Op.Delete);
                    text.setTypeface(null, Typeface.BOLD_ITALIC);
                }else{
                    _Tag=   _Base.UpdateTagStyle(_Tag, ControlStyles.ITALIC, Op.Insert);
                    text.setTypeface(null, Typeface.ITALIC);
                }
            }
            text.setTag(_Tag);
        }
        catch (Exception e){

        }

    }

    public void InsertLink() {
        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this._Context);
        inputAlert.setTitle("Add a link");
        final EditText userInput = new EditText(this._Context);
        //dont forget to add some margins on the left and right to match the title
        userInput.setHint("http://");
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
        EditorType type =_Base. GetControlType(_Base.activeView);
        EditText editText = (EditText) _Base.activeView;
        if (type == EditorType.INPUT ||type==EditorType.LI) {
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
    boolean IsEditTextNull(EditText _editText){
        return _editText.getText().toString().trim().length() == 0;
    }
    private String trimLineEnding(String s) {
        if (s.charAt(s.length() - 1) == '\n') {
            String formatted = s.toString().substring(0, s.length() - 1);
            return formatted;
        }
        return  s;
    }

}
