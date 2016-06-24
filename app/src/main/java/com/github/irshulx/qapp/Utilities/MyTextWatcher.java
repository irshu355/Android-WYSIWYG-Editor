package com.github.irshulx.qapp.Utilities;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by mkallingal on 1/31/2016.
 */
public class MyTextWatcher implements TextWatcher {
    public EditText editText;
    //constructor
    public MyTextWatcher(EditText et){
        super();
        editText = et;
//Code for monitoring keystrokes
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DEL){
                    editText.setText("yippe k");
                }
                return false;
            }
        });
    }
    //Some manipulation with text
    public void afterTextChanged(Editable s) {
        if(editText.getText().length() == 12){
            editText.setText(editText.getText().delete(editText.getText().length() - 1, editText.getText().length()));
            editText.setSelection(editText.getText().toString().length());
        }
        if (editText.getText().length()==2||editText.getText().length()==5||editText.getText().length()==8){
            editText.setText(editText.getText()+"/");
            editText.setSelection(editText.getText().toString().length());
        }
    }
    public void beforeTextChanged(CharSequence s, int start, int count, int after){
    }
    public void onTextChanged(CharSequence s, int start, int before, int count) {



    }
}