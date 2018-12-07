package com.github.irshulx;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irshulx.Components.ComponentsWrapper;
import com.github.irshulx.Components.CustomEditText;
import com.github.irshulx.Components.DividerExtensions;
import com.github.irshulx.Components.HTMLExtensions;
import com.github.irshulx.Components.ImageExtensions;
import com.github.irshulx.Components.InputExtensions;
import com.github.irshulx.Components.ListItemExtensions;
import com.github.irshulx.Components.MacroExtensions;
import com.github.irshulx.Components.MapExtensions;
import com.github.irshulx.Utilities.Utilities;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorTextStyle;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.HtmlTag;
import com.github.irshulx.models.Node;
import com.github.irshulx.models.Op;
import com.github.irshulx.models.RenderType;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mkallingal on 4/30/2016.
 */
public class EditorCore extends LinearLayout {
    public static final String TAG = "EDITOR";
    private EditorListener listener;
    public final int MAP_MARKER_REQUEST = 20;
    public final int PICK_IMAGE_REQUEST = 1;
    private InputExtensions inputExtensions;
    private ImageExtensions imageExtensions;
    private ListItemExtensions listItemExtensions;
    private DividerExtensions dividerExtensions;
    private HTMLExtensions htmlExtensions;
    private MapExtensions mapExtensions;
    private MacroExtensions macroExtensions;
    private EditorSettings editorSettings;
    private ComponentsWrapper componentsWrapper;


    public EditorCore(Context _context, AttributeSet attrs) {
        super(_context, attrs);
        editorSettings = EditorSettings.init(_context, this);
        this.setOrientation(VERTICAL);
        initialize(attrs);

    }

    private void initialize(AttributeSet attrs) {
        loadStateFromAttrs(attrs);
        inputExtensions = new InputExtensions(this);
        imageExtensions = new ImageExtensions(this);
        listItemExtensions = new ListItemExtensions(this);
        dividerExtensions = new DividerExtensions(this);
        mapExtensions = new MapExtensions(this);
        htmlExtensions = new HTMLExtensions(this);
        macroExtensions = new MacroExtensions(this);
        componentsWrapper =new ComponentsWrapper.Builder()
                .inputExtensions(inputExtensions)
                .htmlExtensions(htmlExtensions)
                .dividerExtensions(dividerExtensions)
                .imageExtensions(imageExtensions)
                .listItemExtensions(listItemExtensions)
                .macroExtensions(macroExtensions)
                .mapExtensions(mapExtensions)
                .build();
        macroExtensions.init(componentsWrapper);
        dividerExtensions.init(componentsWrapper);
        inputExtensions.init(componentsWrapper);
        imageExtensions.init(componentsWrapper);
        listItemExtensions.init(componentsWrapper);
        mapExtensions.init(componentsWrapper);

    }

    //region Getters_and_Setters

    /**
     *
     *
     * Exposed
     */

    /**
     * returns activity
     *
     * @return
     */
    public Activity getActivity() {
        return (Activity) this.editorSettings.context;
    }

    /**
     * used to get the editor node
     *
     * @return
     */
    public LinearLayout getParentView() {
        return this.editorSettings.parentView;
    }

    /**
     * Get number of childs in the editor
     *
     * @return
     */
    public int getParentChildCount() {
        return this.editorSettings.parentView.getChildCount();
    }

    /**
     * returns whether editor is set as Editor or Rendeder
     *
     * @return
     */
    public RenderType getRenderType() {
        return this.editorSettings.renderType;
    }

    /**
     * no idea what this is
     *
     * @return
     */
    public Resources getResources() {
        return this.editorSettings.resources;
    }

    /**
     * The current active view on the editor
     *
     * @return
     */
    public View getActiveView() {
        return this.editorSettings.activeView;
    }

    public void setActiveView(View view) {
        this.editorSettings.activeView = view;
    }


    public EditorListener getEditorListener() {
        return this.listener;
    }

    public void setEditorListener(EditorListener _listener) {
        this.listener = _listener;
    }

    /*
     *
     * Getters and setters for  extensions
     *
     */
    protected InputExtensions getInputExtensions() {
        return this.inputExtensions;
    }

    protected ImageExtensions getImageExtensions() {
        return this.imageExtensions;
    }

    protected MapExtensions getMapExtensions() {
        return this.mapExtensions;
    }

    protected HTMLExtensions getHtmlExtensions() {
        return this.htmlExtensions;
    }

    protected ListItemExtensions getListItemExtensions() {
        return this.listItemExtensions;
    }

    protected DividerExtensions getDividerExtensions() {
        return this.dividerExtensions;
    }
    protected MacroExtensions getMacroExtensions() {
        return macroExtensions;
    }
/*
 *
 *
 *
 */

    //endregion


    /*
    Used by Editor
     */
    protected String getContentAsSerialized() {
        EditorContent state = getContent();
        return serializeContent(state);
    }

    protected String getContentAsSerialized(EditorContent state) {
        return serializeContent(state);
    }

    protected EditorContent getContentDeserialized(String EditorContentSerialized) {
        EditorContent Deserialized = this.editorSettings.gson.fromJson(EditorContentSerialized, EditorContent.class);
        return Deserialized;
    }

    protected String serializeContent(EditorContent _state) {
        String serialized = this.editorSettings.gson.toJson(_state);
        return serialized;
    }


    protected EditorContent getContent() {

        if (this.editorSettings.renderType == RenderType.Renderer) {
            Utilities.toastItOut(this.getContext(),"This option only available in editor mode");
            return null;
        }

        int childCount =this.editorSettings.parentView.getChildCount();
        EditorContent editorState = new EditorContent();
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View view = this.editorSettings.parentView.getChildAt(i);
            Node node = getNodeInstance(view);
            switch (node.type) {
                case INPUT:
                    node = getInputExtensions().getContent(view);
                    list.add(node);
                    break;
                case img:
                    node = getImageExtensions().getContent(view);
                    list.add(node);
                    //field type, content[]
                    break;
                case hr:
                    node = getDividerExtensions().getContent(view);
                    list.add(node);
                    break;
                case ul:
                case ol:
                    node =getListItemExtensions().getContent(view);
                    list.add(node);
                    break;
                case map:
                    node = getMapExtensions().getContent(view);
                    list.add(node);
                    break;
                case macro:
                    node = getMacroExtensions().getContent(view);
                    list.add(node);
                    break;
            }
        }
        editorState.nodes = list;
        return editorState;
    }


    protected void renderEditor(EditorContent _state) {
        this.editorSettings.parentView.removeAllViews();
        this.editorSettings.serialRenderInProgress  = true;
        for (Node item : _state.nodes) {
            switch (item.type) {
                case INPUT:
                    inputExtensions.renderEditorFromState(item, _state);
                    break;
                case hr:
                    dividerExtensions.renderEditorFromState(item, _state);
                    break;
                case img:
                    imageExtensions.renderEditorFromState(item, _state);
                    break;
                case ul:
                case ol:
                    getListItemExtensions().renderEditorFromState(item, _state);
                    break;
                case map:
                    mapExtensions.renderEditorFromState(item, _state);
                    break;
                case macro:
                    macroExtensions.renderEditorFromState(item, _state);
                    break;
            }
        }
        this.editorSettings.serialRenderInProgress = false;
    }

    protected void parseHtml(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        for (Element element : doc.body().children()) {
            if (!HTMLExtensions.matchesTag(element.tagName().toLowerCase())){
                String tag = element.attr("data-tag");
                if(!tag.equals("macro")){
                    continue;
                }
            }
            buildNodeFromHTML(element);
        }
    }

    private void buildNodeFromHTML(Element element) {
        String text;

        String macroTag = element.attr("data-tag");
        if(macroTag.equals("macro")){
            macroExtensions.buildNodeFromHTML(element);
            return;
        }

        HtmlTag tag = HtmlTag.valueOf(element.tagName().toLowerCase());
        int count = getParentView().getChildCount();

        if ("<br>".equals(element.html().replaceAll("\\s+", "")) || "<br/>".equals(element.html().replaceAll("\\s+", ""))) {
            inputExtensions.insertEditText(count, null, null);
            return;
        } else if ("hr".equals(tag.name()) || "<hr>".equals(element.html().replaceAll("\\s+", "")) || "<hr/>".equals(element.html().replaceAll("\\s+", ""))) {
            getDividerExtensions().buildNodeFromHTML(element);
            return;
        }

        switch (tag) {
            case h1:
            case h2:
            case h3:
            case p:
                inputExtensions.buildNodeFromHTML(element);
                break;
            case ul:
            case ol:
                listItemExtensions.buildNodeFromHTML(element);
                break;
            case img:
                imageExtensions.buildNodeFromHTML(element);
                break;
            case div:
                String dataTag = element.attr("data-tag");
                if (dataTag.equals("img")) {
                    imageExtensions.buildNodeFromHTML(element);
                }
                break;

        }
    }

    protected String getHTMLContent() {
        EditorContent content = getContent();
        return getHTMLContent(content);
    }

    protected String getHTMLContent(EditorContent content) {
        StringBuilder htmlBlock = new StringBuilder();
        String html;
        for (Node item : content.nodes) {
            switch (item.type) {
                case INPUT:
                    html = inputExtensions.getContentAsHTML(item, content);
                    htmlBlock.append(html);
                    break;
                case img:
                    String imgHtml = getImageExtensions().getContentAsHTML(item, content);
                    htmlBlock.append(imgHtml);
                    break;
                case hr:
                    htmlBlock.append(dividerExtensions.getContentAsHTML(item, content));
                    break;
                case map:
                    String htmlMap = mapExtensions.getContentAsHTML(item,content);
                    htmlBlock.append(htmlMap);
                    break;
                case ul:
                case ol:
                    htmlBlock.append(listItemExtensions.getContentAsHTML(item, content));
                    break;
                case macro:
                    htmlBlock.append(macroExtensions.getContentAsHTML(item, content));
                    break;
            }
        }
        return htmlBlock.toString();
    }

    protected String getHTMLContent(String editorContentAsSerialized) {
        EditorContent content = getContentDeserialized(editorContentAsSerialized);
        return getHTMLContent(content);
    }


    protected void renderEditorFromHtml(String content) {
        this.editorSettings.serialRenderInProgress = true;
        parseHtml(content);
        this.editorSettings.serialRenderInProgress = false;
    }

    protected void clearAllContents() {
        this.editorSettings.parentView.removeAllViews();

    }




    private void loadStateFromAttrs(AttributeSet attributeSet) {
        if (attributeSet == null) {
            return; // quick exit
        }

        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attributeSet, R.styleable.editor);
            this.editorSettings.placeHolder = a.getString(R.styleable.editor_placeholder);
            this.editorSettings.autoFocus = a.getBoolean(R.styleable.editor_auto_focus, true);
            String renderType = a.getString(R.styleable.editor_render_type);
            if (TextUtils.isEmpty(renderType)) {
                this.editorSettings.renderType = com.github.irshulx.models.RenderType.Editor;
            } else {
                this.editorSettings.renderType = renderType.toLowerCase().equals("renderer") ? RenderType.Renderer : RenderType.Editor;
            }

        } finally {
            if (a != null) {
                a.recycle(); // ensure this is always called
            }
        }
    }

    /**
     * determine target index for the next insert,
     *
     * @param type
     * @return
     */
    public int determineIndex(EditorType type) {
        int size = this.editorSettings.parentView.getChildCount();
        if (this.editorSettings.renderType == RenderType.Renderer)
            return size;
        View _view = this.editorSettings.activeView;
        if (_view == null)
            return size;
        int currentIndex = this.editorSettings.parentView.indexOfChild(_view);
        EditorType tag = getControlType(_view);
        if (tag == EditorType.INPUT) {
            int length = ((EditText) this.editorSettings.activeView).getText().length();
            if (length > 0) {
                return type == EditorType.UL_LI || type == EditorType.OL_LI ? currentIndex : currentIndex;
            } else {
                return currentIndex;
            }
        } else if (tag == EditorType.UL_LI || tag == EditorType.OL_LI) {
            EditText _text = _view.findViewById(R.id.txtText);
            if (_text.getText().length() > 0) {

            }
            return size;
        } else {
            return size;
        }
    }

    public boolean containsStyle(List<EditorTextStyle> _Styles, EditorTextStyle style) {
        for (EditorTextStyle item : _Styles) {
            if (item == style) {
                return true;
            }
            continue;
        }
        return false;
    }

    public EditorControl updateTagStyle(EditorControl controlTag, EditorTextStyle style, Op _op) {
        List<EditorTextStyle> styles = controlTag.editorTextStyles;
        if (_op == Op.Delete) {
            int index = styles.indexOf(style);
            if (index != -1) {
                styles.remove(index);
                controlTag.editorTextStyles = styles;
            }
        } else {
            int index = styles.indexOf(style);
            if (index == -1) {
                styles.add(style);
            }
        }
        return controlTag;
    }

    public EditorType getControlType(View _view) {
        if (_view == null)
            return null;

        EditorControl _control = (EditorControl) _view.getTag();
        return _control.Type;
    }

    public EditorControl getControlTag(View view) {
        if (view == null)
            return null;
        EditorControl control = (EditorControl) view.getTag();
        return control;
    }

    public EditorControl createTag(EditorType type) {
        EditorControl control = new EditorControl();
        control.Type = type;
        control.editorTextStyles = new ArrayList<>();
        switch (type) {
            case hr:
            case img:
            case INPUT:
            case ul:
            case UL_LI:
        }
        return control;
    }

    public void deleteFocusedPrevious(EditText view) {
        int index = this.editorSettings.parentView.indexOfChild(view);
        if (index == 0)
            return;
        EditorControl contentType = (EditorControl) ((View) view.getParent()).getTag();
          /*
         *
         * If the person was on an active ul|li, move him to the previous node
         *
         */


        if (contentType != null && (contentType.Type == EditorType.OL_LI || contentType.Type == EditorType.UL_LI)) {
            listItemExtensions.validateAndRemoveLisNode(view, contentType);
            return;
        }

        View toFocus = this.editorSettings.parentView.getChildAt(index - 1);
        EditorControl control = (EditorControl) toFocus.getTag();

        /**
         * If its an image or map, do not delete edittext, as there is nothing to focus on after image
         */
        if (control.Type == EditorType.img || control.Type == EditorType.map) {
            return;
        }
        /*
         *
         * If the person was on edittext,  had removed the whole text, we need to move into the previous line
         *
         */

        if (control.Type == EditorType.ol || control.Type == EditorType.ul) {
         /*
         *
         * previous node on the editor is a list, set focus to its inside
         *
         */
            this.editorSettings.parentView.removeView(view);
            listItemExtensions.setFocusToList(toFocus, ListItemExtensions.POSITION_END);
        } else {
            removeParent(view);
        }
    }


    public int removeParent(View view) {
        int indexOfDeleteItem = this.editorSettings.parentView.indexOfChild(view);
        View nextItem = null;
        //remove hr if its on top of the delete field
        this.editorSettings.parentView.removeView(view);
        Log.d("indexOfDeleteItem", "indexOfDeleteItem : " + indexOfDeleteItem);
        if (dividerExtensions.deleteHr(Math.max(0, indexOfDeleteItem - 1)))
            indexOfDeleteItem -= 1;
        for (int i = 0; i < indexOfDeleteItem; i++) {
            if (getControlType(this.editorSettings.parentView.getChildAt(i)) == EditorType.INPUT) {
                nextItem = this.editorSettings.parentView.getChildAt(i);
                continue;
            }
        }
        if (nextItem != null) {
            CustomEditText text = (CustomEditText) nextItem;
            if (text.requestFocus()) {
                text.setSelection(text.getText().length());
            }
            this.editorSettings.activeView = nextItem;
        }
        return indexOfDeleteItem;
    }


    public EditorContent getStateFromString(String content) {
        if (content == null) {
            content = getValue("editorState", "");
        }
        EditorContent deserialized = this.editorSettings.gson.fromJson(content, EditorContent.class);
        return deserialized;
    }

    private String getValue(String Key, String defaultVal) {
        SharedPreferences _Preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return _Preferences.getString(Key, defaultVal);

    }

    protected void putValue(String Key, String Value) {
        SharedPreferences _Preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor editor = _Preferences.edit();
        editor.putString(Key, Value);
        editor.apply();
    }


    private Node getNodeInstance(View view){
        Node node = new Node();
        EditorType type = getControlType(view);
        node.type = type;
        node.content = new ArrayList<>();
        return node;
    }




    public boolean isLastRow(View view) {
        int index = this.editorSettings.parentView.indexOfChild(view);
        int length = this.editorSettings.parentView.getChildCount();
        return length - 1 == index;
    }



    public boolean onKey(View v, int keyCode, KeyEvent event, CustomEditText editText) {
        if (keyCode != KeyEvent.KEYCODE_DEL) {
            return false;
        }
        if (inputExtensions.isEditTextEmpty(editText)) {
            deleteFocusedPrevious(editText);
            int controlCount = getParentChildCount();
            if (controlCount == 1)
                return checkLastControl();
            return false;
        }
        int length = editText.getText().length();
        int selectionStart = editText.getSelectionStart();

        EditorType editorType = getControlType(this.editorSettings.activeView);
        CustomEditText nextFocus;
        if (selectionStart == 0 && length > 0) {
            if ((editorType == EditorType.UL_LI || editorType == EditorType.OL_LI)) {
                //now that we are inside the edittext, focus inside it
                int index = listItemExtensions.getIndexOnEditorByEditText(editText);
                if (index == 0) {
                    deleteFocusedPrevious(editText);
                }
            } else {
                int index = getParentView().indexOfChild(editText);
                if (index == 0)
                    return false;
                nextFocus = inputExtensions.getEditTextPrevious(index);

                if (nextFocus != null) {
                    deleteFocusedPrevious(editText);
                    nextFocus.setText(nextFocus.getText().toString() + editText.getText().toString());
                    nextFocus.setSelection(nextFocus.getText().length());
                }
            }
        }
        return false;
    }

    private boolean checkLastControl() {
        EditorControl control = getControlTag(getParentView().getChildAt(0));
        if (control == null)
            return false;
        switch (control.Type) {
            case ul:
            case ol:
                this.editorSettings.parentView.removeAllViews();
                break;
        }

        return false;
    }

    public boolean isStateFresh() {
        return this.editorSettings.stateFresh;
    }

    public void setStateFresh(boolean stateFresh) {
        this.editorSettings.stateFresh = stateFresh;
    }

    public boolean isSerialRenderInProgress() {
        return this.editorSettings.serialRenderInProgress;
    }

    public void setSerialRenderInProgress(boolean serialRenderInProgress) {
        this.editorSettings.serialRenderInProgress = serialRenderInProgress;
    }


    public String getPlaceHolder() {
        return editorSettings.placeHolder;
    }

    public boolean getAutoFucus() {
        return editorSettings.autoFocus;
    }
}
