package com.github.irshulx.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Node {
    public EditorType type;
    public ArrayList<String> content;
    public List<EditorTextStyle> contentStyles;
    public TextSettings textSettings;
    public ArrayList<Node> childs;
    public Map<String, Object> macroSettings;
}
