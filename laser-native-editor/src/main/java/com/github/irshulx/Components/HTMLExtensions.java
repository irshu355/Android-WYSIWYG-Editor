package com.github.irshulx.Components;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.github.irshulx.EditorCore;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorTextStyle;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.HtmlTag;
import com.github.irshulx.models.Node;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import java.util.HashMap;
import java.util.Map;

import static com.github.irshulx.models.TextSetting.TEXT_COLOR;

/**
 * Created by mkallingal on 5/25/2016.
 */
public class HTMLExtensions {
    EditorCore editorCore;

    public HTMLExtensions(EditorCore editorCore) {
        this.editorCore = editorCore;
    }


    public Map<String, String> getStyleMap(Element element) {
        Map<String, String> keymaps = new HashMap<>();
        if (!element.hasAttr("style")) {
            return keymaps;
        }
        String styleStr = element.attr("style"); // => margin-top:10px;color:#fcc;border-bottom:1px solid #ccc; background-color: #333; text-align:center
        String[] keys = styleStr.split(":");
        String[] split;
        if (keys.length > 1) {
            for (int i = 0; i < keys.length; i++) {
                if (i % 2 != 0) {
                    split = keys[i].split(";");
                    if (split.length == 1) break;
                    keymaps.put(split[1].trim(), keys[i + 1].split(";")[0].trim());
                } else {
                    split = keys[i].split(";");
                    if (i + 1 == keys.length) break;
                    keymaps.put(keys[i].split(";")[split.length - 1].trim(), keys[i + 1].split(";")[0].trim());
                }
            }
        }
        return keymaps;
    }

    public String getHtmlSpan(Element element) {
        Element el = new Element(Tag.valueOf("span"), "");
        el.attributes().put("style", element.attr("style"));
        el.html(element.html());
        return el.toString();
    }

    private boolean hasChildren(Element element) {
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

    public String getTemplateHtml(EditorType child) {
        String template = null;
        switch (child) {
            case INPUT:
                template = "<{{$tag}} data-tag=\"input\" {{$style}}>{{$content}}</{{$tag}}>";
                break;
            case hr:
                template = "<hr data-tag=\"hr\"/>";
                break;
            case img:
                template = "<div data-tag=\"img\"><img src=\"{{$url}}\" />{{$img-sub}}</div>";
                break;
            case IMG_SUB:
                template = "<{{$tag}} data-tag=\"img-sub\" {{$style}} class=\"editor-image-subtitle\">{{$content}}</{{$tag}}>";
                break;
            case map:
                template = "<div data-tag=\"map\"><img src=\"{{$content}}\" /><span text-align:'center' {{$style}}>{{$desc}}</span></div>";
                break;
            case ol:
                template = "<ol data-tag=\"ol\">{{$content}}</ol>";
                break;
            case ul:
                template = "<ul data-tag=\"ul\">{{$content}}</ul>";
                break;
            case OL_LI:
            case UL_LI:
                String dataTag = child == EditorType.OL_LI ? "data-tag=\"list-item-ol\"" : "data-tag=\"list-item-ul\"";
                template = "<li " + dataTag + "><{{$tag}} {{$style}}>{{$content}}</{{$tag}}></li>";
                break;
        }
        return template;
    }

}