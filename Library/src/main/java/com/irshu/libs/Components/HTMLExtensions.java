package com.irshu.libs.Components;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.irshu.libs.BaseClass;
import com.irshu.libs.models.ControlStyles;
import com.irshu.libs.models.HtmlTag;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by mkallingal on 5/25/2016.
 */
public class HTMLExtensions {
    private Context context;
    BaseClass base;

    public HTMLExtensions(BaseClass baseClass, Context context){
        this.base = baseClass;
        this.context = context;
    }
    public void parseHtml(String htmlString){
        Document doc= Jsoup.parse(htmlString);
        for (Element element:doc.body().children()){
            if(!matchesTag(element.tagName()))
                continue;
            buildNode(element);
        }
    }
    private void buildNode(Element element) {
        String text;
        TextView editText;
        HtmlTag tag= HtmlTag.valueOf(element.tagName().toLowerCase());
        int count= base.getParentView().getChildCount();
        if("<br>".equals(element.html().replaceAll("\\s+", ""))||"<br/>".equals(element.html().replaceAll("\\s+", ""))){
            base.getInputExtensions().InsertEditText(count, "", "");
            return;
        }
        switch (tag){
            case h1:
                text= element.html();
                 editText= base.getInputExtensions().InsertEditText(count, "", text);
                base.getInputExtensions().UpdateTextStyle(ControlStyles.H1,editText);
                break;
            case h2:
                text= element.html();
                 editText= base.getInputExtensions().InsertEditText(count, "", text);
                base.getInputExtensions().UpdateTextStyle(ControlStyles.H2,editText);
                break;
            case p:
                text= element.html();
                editText= base.getInputExtensions().InsertEditText(count, "", text);
                break;
        }
    }
    public static boolean matchesTag(String test) {
        for (HtmlTag tag : HtmlTag.values()) {
            if (tag.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
