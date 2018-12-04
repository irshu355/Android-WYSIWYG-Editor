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

    public void parseHtml(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        for (Element element : doc.body().children()) {
            if (!matchesTag(element.tagName().toLowerCase()))
                continue;
            buildNode(element);
        }
    }

    private void buildNode(Element element) {
        String text;
        HtmlTag tag = HtmlTag.valueOf(element.tagName().toLowerCase());
        int count = editorCore.getParentView().getChildCount();

        if ("<br>".equals(element.html().replaceAll("\\s+", "")) || "<br/>".equals(element.html().replaceAll("\\s+", ""))) {
            editorCore.getInputExtensions().insertEditText(count, null, null);
            return;
        } else if ("hr".equals(tag.name()) || "<hr>".equals(element.html().replaceAll("\\s+", "")) || "<hr/>".equals(element.html().replaceAll("\\s+", ""))) {
            editorCore.getDividerExtensions().insertDivider(count);
            return;
        }

        switch (tag) {
            case h1:
            case h2:
            case h3:
                RenderHeader(tag, element);
                break;
            case p:
                text = element.html();
                TextView textView = editorCore.getInputExtensions().insertEditText(count, null, text);
                editorCore.getInputExtensions().applyStyles(textView, element);
                break;
            case ul:
            case ol:
                RenderList(tag == HtmlTag.ol, element);
                break;
            case img:
                RenderImageFromHtml(element);
                break;
            case div:
                renderDiv(element);
                break;
        }
    }

    private void renderDiv(Element element) {
        String tag = element.attr("data-tag");
        if (tag.equals("img")) {
            RenderImage(element);
        }
    }

    private void RenderImage(Element element) {
        Element img = element.child(0);
        Element descTag = element.child(1);
        String src = img.attr("src");
        editorCore.getImageExtensions().loadImage(src, descTag);
    }

    private void RenderImageFromHtml(Element element) {
        String src = element.attr("src");
        Element descTag = element.child(1);
        editorCore.getImageExtensions().loadImage(src, descTag);
    }

    private void RenderList(boolean isOrdered, Element element) {
        if (element.children().size() > 0) {
            Element li = element.child(0);
            String text = getHtmlSpan(li);
            TableLayout layout = editorCore.getListItemExtensions().insertList(editorCore.getParentChildCount(), isOrdered, text);
            for (int i = 1; i < element.children().size(); i++) {
                li = element.child(i);
                text = getHtmlSpan(li);
                View view = editorCore.getListItemExtensions().addListItem(layout, isOrdered, text);
                editorCore.getListItemExtensions().applyStyles(view, li);
            }
        }
    }

    private void RenderHeader(HtmlTag tag, Element element) {
        int count = editorCore.getParentView().getChildCount();
        String text = getHtmlSpan(element);
        TextView editText = editorCore.getInputExtensions().insertEditText(count, null, text);
        EditorTextStyle style = tag == HtmlTag.h1 ? EditorTextStyle.H1 : tag == HtmlTag.h2 ? EditorTextStyle.H2 : EditorTextStyle.H3;
        editorCore.getInputExtensions().UpdateTextStyle(style, editText);
        editorCore.getInputExtensions().applyStyles(editText, element);
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

    private String getHtmlSpan(Element element) {
        Element el = new Element(Tag.valueOf("span"), "");
        el.attributes().put("style", element.attr("style"));
        el.html(element.html());
        return el.toString();
    }

    private boolean hasChildren(Element element) {
        return element.getAllElements().size() > 0;
    }


    private static boolean matchesTag(String test) {
        for (HtmlTag tag : HtmlTag.values()) {
            if (tag.name().equals(test)) {
                return true;
            }
        }
        return false;
    }

    private String getTemplateHtml(EditorType child) {
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

    private String createStyleTag(Map<Enum, String> styles) {
        String tmpl = " style=\"{{builder}}\"";
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Enum, String> style : styles.entrySet()) {
            builder.append(style.getValue()).append(";");
        }
        tmpl = tmpl.replace("{{builder}}", builder);
        return tmpl;
    }

    private String getInputHtml(Node item) {
        boolean isParagraph = true;
        String tmpl = getTemplateHtml(item.type);
        //  CharSequence content= android.text.Html.fromHtml(item.content.get(0)).toString();
        //  CharSequence trimmed= editorCore.getInputExtensions().noTrailingwhiteLines(content);
        String trimmed = Jsoup.parse(item.content.get(0)).body().select("p").html();
        Map<Enum, String> styles = new HashMap<>();
        if (item.contentStyles.size() > 0) {
            for (EditorTextStyle style : item.contentStyles) {
                switch (style) {
                    case BOLD:
                        tmpl = tmpl.replace("{{$content}}", "<b>{{$content}}</b>");
                        break;
                    case BOLDITALIC:
                        tmpl = tmpl.replace("{{$content}}", "<b><i>{{$content}}</i></b>");
                        break;
                    case ITALIC:
                        tmpl = tmpl.replace("{{$content}}", "<i>{{$content}}</i>");
                        break;
                    case INDENT:
                        styles.put(style, "margin-left:25px");
                        break;
                    case OUTDENT:
                        styles.put(style, "margin-left:0");
                        break;
                    case H1:
                        tmpl = tmpl.replace("{{$tag}}", "h1");
                        isParagraph = false;
                        break;
                    case H2:
                        tmpl = tmpl.replace("{{$tag}}", "h2");
                        isParagraph = false;
                        break;
                    case H3:
                        tmpl = tmpl.replace("{{$tag}}", "h3");
                        isParagraph = false;
                        break;
                    case NORMAL:
                        tmpl = tmpl.replace("{{$tag}}", "p");
                        isParagraph = true;
                        break;
                }
            }
        }

        styles.put(TEXT_COLOR, "color:" + item.textSettings.getTextColor());

        if (item.type == EditorType.OL_LI || item.type == EditorType.UL_LI) {
            tmpl = tmpl.replace("{{$tag}}", "span");
        } else if (isParagraph) {
            tmpl = tmpl.replace("{{$tag}}", "p");
        }
        tmpl = tmpl.replace("{{$content}}", trimmed);
        tmpl = tmpl.replace(" {{$style}}", createStyleTag(styles));
        return tmpl;
    }

    public String getContentAsHTML() {
        StringBuilder htmlBlock = new StringBuilder();
        String html;
        EditorContent content = editorCore.getContent();
        return getContentAsHTML(content);
    }

    public String getContentAsHTML(EditorContent content) {
        StringBuilder htmlBlock = new StringBuilder();
        String html;
        for (Node item : content.nodes) {
            switch (item.type) {
                case INPUT:
                    html = getInputHtml(item);
                    htmlBlock.append(html);
                    break;
                case img:
                    String subHtml = getInputHtml(item.childs.get(0));
                    html = getTemplateHtml(item.type);
                    html = html.replace("{{$url}}", item.content.get(0));
                    html = html.replace("{{$img-sub}}", subHtml);
                    htmlBlock.append(html);
                    break;
                case hr:
                    htmlBlock.append(getTemplateHtml(item.type));
                    break;
                case map:
                    htmlBlock.append(getTemplateHtml(item.type).replace("{{$content}}", editorCore.getMapExtensions().getCordsAsUri(item.content.get(0))).replace("{{$desc}}", item.content.get(1)));
                    break;
                case ul:
                case ol:
                    htmlBlock.append(getListAsHtml(item));
                    break;
            }
        }
        return htmlBlock.toString();
    }

    public String getContentAsHTML(String editorContentAsSerialized) {
        EditorContent content = editorCore.getContentDeserialized(editorContentAsSerialized);
        return getContentAsHTML(content);
    }


    private String getListAsHtml(Node item) {
        int count = item.childs.size();
        String tmpl_parent = getTemplateHtml(item.type);
        StringBuilder childBlock = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String html = getInputHtml(item.childs.get(i));
            childBlock.append(html);
        }
        tmpl_parent = tmpl_parent.replace("{{$content}}", childBlock.toString());
        return tmpl_parent;
    }
}