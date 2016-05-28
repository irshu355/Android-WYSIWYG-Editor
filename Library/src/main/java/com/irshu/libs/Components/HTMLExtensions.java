package com.irshu.libs.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.irshu.libs.BaseClass;
import com.irshu.libs.models.ControlStyles;
import com.irshu.libs.models.EditorControl;
import com.irshu.libs.models.HtmlTag;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

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
        else if("<hr>".equals(element.html().replaceAll("\\s+", ""))||"<hr/>".equals(element.html().replaceAll("\\s+", ""))){
            base.getDividerExtensions().InsertDivider();
            return;
        }
        switch (tag){
            case h1:
            case h2:
            case h3:
                RenderHeader(tag,element);
                break;
            case p:
                text= element.html();
                editText= base.getInputExtensions().InsertEditText(count, "", text);
                break;
            case ul:
            case ol:
                RenderList(tag==HtmlTag.ol, element);
                break;
            case img:
                RenderImage(element);
                break;
        }
    }

    private void RenderImage(Element element) {
        String src= element.attr("src");
        Bitmap bitmap= base.getImageExtensions().getBitmapFromURL(src);
        base.getImageExtensions().InsertImage(bitmap);
    }

    private void RenderList(boolean isOrdered, Element element) {
        if(element.children().size()>0){
            Element li=element.child(0);
            String text=getHtmlSpan(li);
            TableLayout layout= base.getListItemExtensions().insertList(base.getParentChildCount(),isOrdered,text);
            for (int i=1;i<element.children().size();i++){
                 text=getHtmlSpan(li);
                base.getListItemExtensions().AddListItem(layout,isOrdered,text);
            }
        }
    }

    private void RenderHeader(HtmlTag tag, Element element){
       int count= base.getParentView().getChildCount();
       String text=getHtmlSpan(element);
       TextView  editText= base.getInputExtensions().InsertEditText(count, "", text);
       ControlStyles style= tag==HtmlTag.h1?ControlStyles.H1:tag==HtmlTag.h2?ControlStyles.H2:ControlStyles.H3;
       base.getInputExtensions().UpdateTextStyle(style,editText);
    }

    private String getHtmlSpan(Element element) {
        Element el = new Element(Tag.valueOf("span"), "");
        el.attributes().put("style",element.attr("style"));
        el.html(element.html());
        return el.toString();
    }

    private boolean hasChildren(Element element){
        return element.getAllElements().size() > 0;
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
