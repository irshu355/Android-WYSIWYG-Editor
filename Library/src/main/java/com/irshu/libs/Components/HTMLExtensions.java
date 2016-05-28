package com.irshu.libs.Components;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.irshu.libs.BaseClass;
import com.irshu.libs.models.ControlStyles;
import com.irshu.libs.models.EditorControl;
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
            case h2:
                RenderHeader(tag,element);
                break;
            case p:
                text= element.html();
                editText= base.getInputExtensions().InsertEditText(count, "", text);
                break;
        }
    }

   private void RenderHeader(HtmlTag tag, Element element){
       String text= element.html();
       int count= base.getParentView().getChildCount();
       TextView  editText= base.getInputExtensions().InsertEditText(count, "", text);
       ControlStyles style= tag==HtmlTag.h1?ControlStyles.H1:tag==HtmlTag.h2?ControlStyles.H2:ControlStyles.H3;
       base.getInputExtensions().UpdateTextStyle(style,editText);
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
