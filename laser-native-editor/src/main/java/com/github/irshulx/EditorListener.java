package com.github.irshulx;

import android.text.Editable;
import android.widget.EditText;

import retrofit2.Retrofit;

/**
 * Created by IRSHU on 27/2/2017.
 */

public interface EditorListener{
    void onTextChanged(EditText editText, Editable text);
    Retrofit.Builder onUpload(Retrofit.Builder retrofit);
}
