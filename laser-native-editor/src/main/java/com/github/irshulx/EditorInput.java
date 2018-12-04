package com.github.irshulx;

import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.Node;

import org.jsoup.nodes.Element;

public abstract class EditorInput {
    public abstract Node getContent();
    public abstract String getContentAsHTML(Node node, EditorContent content);
    public abstract void renderEditorFromState(Node node, EditorContent content);
    public abstract Node buildNodeFromHTML(Element element);
}