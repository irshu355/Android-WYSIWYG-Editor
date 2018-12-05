package com.github.irshulx.Components;

import android.view.View;

import com.github.irshulx.EditorCore;
import com.github.irshulx.EditorComponent;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.Node;

import org.jsoup.nodes.Element;

import java.util.Map;

public class MacroExtensions extends EditorComponent {
    private final EditorCore editorCore;
    public MacroExtensions(EditorCore editorCore) {
        super(editorCore);
        this.editorCore = editorCore;
    }

    public void insertMacro(String name, View view, Map<String,Object> settings){
        EditorControl control = editorCore.createTag(EditorType.macro);
        control.macroSettings = settings;
        control.macroName = name;
        int index = editorCore.determineIndex(EditorType.macro);
        view.setTag(control);
        editorCore.getParentView().addView(view, index);
    }

    @Override
    public Node getContent(View view) {
        Node node = this.getNodeInstance(view);
        EditorControl macroTag = (EditorControl) view.getTag();
        node.content.add(macroTag.macroName);
        node.macroSettings = macroTag.macroSettings;
        return node;
    }

    @Override
    public String getContentAsHTML(Node node, EditorContent content) {
        componentsWrapper.getInputExtensions().setLineSpacing(12);
        return null;
    }

    @Override
    public void renderEditorFromState(Node node, EditorContent content) {

    }

    @Override
    public Node buildNodeFromHTML(Element element) {
        return null;
    }

    @Override
    public void init(ComponentsWrapper componentsWrapper) {
        this.componentsWrapper = componentsWrapper;
    }
}
