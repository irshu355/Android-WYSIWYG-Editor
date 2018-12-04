package com.github.irshulx.models;


import java.util.List;
import java.util.Map;

/**
 * Created by mkallingal on 1/15/2016.
 */
public class EditorControl {
    public EditorType Type;
    public String path;
    public String Cords;
    public TextSettings textSettings;
    public List<EditorTextStyle> editorTextStyles;
    public Map<String, Object> macroSettings;
}
