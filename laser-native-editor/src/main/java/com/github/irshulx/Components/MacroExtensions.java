package com.github.irshulx.Components;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.github.irshulx.EditorCore;
import com.github.irshulx.EditorComponent;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.HtmlTag;
import com.github.irshulx.models.Node;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MacroExtensions extends EditorComponent {
    private final EditorCore editorCore;
    public MacroExtensions(EditorCore editorCore) {
        super(editorCore);
        this.editorCore = editorCore;
    }

    public void insertMacro(String name, View view, Map<String,Object> settings, int index){
        EditorControl control = editorCore.createTag(EditorType.macro);
        control.macroSettings = settings;
        control.macroName = name;
        if(index == -1) {
             index = editorCore.determineIndex(EditorType.macro);
        }
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
        String template = "<{{$tag}} data-tag=\"macro\" {{$settings}}></{{$tag}}>";
        template = template.replace("{{$tag}}", node.content.get(0));
        StringBuilder dataTags = new StringBuilder();
        for(Map.Entry<String, Object> item: node.macroSettings.entrySet()){
            dataTags.append(" ");
            dataTags.append("data-"+item.getKey()).append("=\"").append(String.valueOf(item.getValue())).append("\"");
        }
        if(TextUtils.isEmpty(dataTags)){
            template = template.replace("{{$settings}}", "");
        }else {
            template = template.replace("{{$settings}}", dataTags.toString());
        }
        return template;
    }

    @Override
    public void renderEditorFromState(Node node, EditorContent content) {
        int index = editorCore.getChildCount();
        View view = editorCore.getEditorListener().onRenderMacro(node.content.get(0), node.macroSettings, editorCore.getChildCount());
        if(view != null) {
            insertMacro(node.content.get(0), view, node.macroSettings, index);
        }
    }

    @Override
    public Node buildNodeFromHTML(Element element) {
        String tag = element.tagName().toLowerCase();
        Node node = getNodeInstance(EditorType.macro);
        node.content.add(tag);

        List<Attribute> attrs = element.attributes().asList();

        if(!attrs.isEmpty()){
            node.macroSettings = new HashMap<>();
            for(Attribute attr: attrs){
                node.macroSettings.put(attr.getKey(), attr.getValue());
            }
        }
        int index = editorCore.getChildCount();
        View view = editorCore.getEditorListener().onRenderMacro(tag, node.macroSettings, editorCore.getChildCount());
        if(view != null) {
            insertMacro(tag, view, node.macroSettings, index);
        }
        return null;
    }

    @Override
    public void init(ComponentsWrapper componentsWrapper) {
        this.componentsWrapper = componentsWrapper;
    }

}
