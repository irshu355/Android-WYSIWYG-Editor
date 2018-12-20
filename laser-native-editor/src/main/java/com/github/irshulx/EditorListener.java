package com.github.irshulx;

import android.graphics.Bitmap;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import java.util.Map;

/**
 * Created by IRSHU on 27/2/2017.
 */

public interface EditorListener{
    void onTextChanged(EditText editText, Editable text);
    void onUpload(Bitmap image, String uuid);
    View onRenderMacro(String name, Map<String, Object> props, int index);
}