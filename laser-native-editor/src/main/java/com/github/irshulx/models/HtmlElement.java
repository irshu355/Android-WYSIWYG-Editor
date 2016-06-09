package com.github.irshulx.models;


import org.jsoup.nodes.Element;

/**
 * Created by mkallingal on 1/1/2016.
 */
public class HtmlElement {
    public String TagName;
    public Element _Element;

    public  HtmlElement(String _TagName, Element _Element){
        this.TagName= _TagName;
        this._Element= _Element;
    }
}

