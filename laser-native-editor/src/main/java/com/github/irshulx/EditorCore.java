package com.github.irshulx;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irshulx.Components.CustomEditText;
import com.github.irshulx.Components.DividerExtensions;
import com.github.irshulx.Components.HTMLExtensions;
import com.github.irshulx.Components.ImageExtensions;
import com.github.irshulx.Components.InputExtensions;
import com.github.irshulx.Components.ListItemExtensions;
import com.github.irshulx.Components.MapExtensions;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorTextStyle;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.Node;
import com.github.irshulx.models.Op;
import com.github.irshulx.models.RenderType;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkallingal on 4/30/2016.
 */
public class EditorCore extends LinearLayout {
    /*
    * EditText initializors
    */
    public String placeHolder = null;
    /*
    * Divider initializors
    */
    private final String SHAREDPREFERENCE = "QA";
    private Context __context;
    private Activity __activity;
    protected LinearLayout __parentView;
    private RenderType __renderType;
    private Resources __resources;
    private View __activeView;
    private Gson __gson;
    private Utilities __utilities;
    private EditorListener __listener;
    public final int MAP_MARKER_REQUEST = 20;
    public final int PICK_IMAGE_REQUEST = 1;
    private InputExtensions __inputExtensions;
    private ImageExtensions __imageExtensions;
    private ListItemExtensions __listItemExtensions;
    private DividerExtensions __dividerExtensions;
    private HTMLExtensions __htmlExtensions;
    private MapExtensions __mapExtensions;

    public EditorCore(Context _context, AttributeSet attrs) {
        super(_context, attrs);
        this.__context = _context;
        this.__activity = (Activity) _context;
        this.setOrientation(VERTICAL);
        initialize(_context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        loadStateFromAttrs(attrs);
        __utilities = new Utilities();
        this.__resources = context.getResources();
        __gson = new Gson();
        __inputExtensions = new InputExtensions(this);
        __imageExtensions = new ImageExtensions(this);
        __listItemExtensions = new ListItemExtensions(this);
        __dividerExtensions = new DividerExtensions(this);
        __mapExtensions = new MapExtensions(this);
        __htmlExtensions = new HTMLExtensions(this);
        this.__parentView = this;
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
        return this.__activity;
    }

    /**
     * used to get the editor node
     *
     * @return
     */
    public LinearLayout getParentView() {
        return this.__parentView;
    }

    /**
     * Get number of childs in the editor
     *
     * @return
     */
    public int getParentChildCount() {
        return this.__parentView.getChildCount();
    }

    /**
     * returns whether editor is set as Editor or Rendeder
     *
     * @return
     */
    public RenderType getRenderType() {
        return this.__renderType;
    }

    /**
     * no idea what this is
     *
     * @return
     */
    public Resources getResources() {
        return this.__resources;
    }

    /**
     * The current active view on the editor
     *
     * @return
     */
    public View getActiveView() {
        return this.__activeView;
    }

    public void setActiveView(View view) {
        this.__activeView = view;
    }

    public Utilities getUtilitiles() {
        return this.__utilities;
    }

    public EditorListener getEditorListener() {
        return this.__listener;
    }

    public void setEditorListener(EditorListener _listener) {
        this.__listener = _listener;
    }

    /*
     *
     * Getters and setters for  extensions
     *
     */
    public InputExtensions getInputExtensions() {
        return this.__inputExtensions;
    }

    public ImageExtensions getImageExtensions() {
        return this.__imageExtensions;
    }

    public MapExtensions getMapExtensions() {
        return this.__mapExtensions;
    }

    public HTMLExtensions getHtmlExtensions() {
        return this.__htmlExtensions;
    }

    public ListItemExtensions getListItemExtensions() {
        return this.__listItemExtensions;
    }

    public DividerExtensions getDividerExtensions() {
        return this.__dividerExtensions;
    }
/*
 *
 *
 *
 */

    //endregion

    private void loadStateFromAttrs(AttributeSet attributeSet) {
        if (attributeSet == null) {
            return; // quick exit
        }

        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attributeSet, R.styleable.editor);
            this.placeHolder = a.getString(R.styleable.editor_placeholder);
            String renderType = a.getString(R.styleable.editor_render_type);
            if (TextUtils.isEmpty(renderType)) {
                this.__renderType = com.github.irshulx.models.RenderType.Editor;
            } else {
                this.__renderType = renderType.toLowerCase().equals("renderer") ? RenderType.Renderer : RenderType.Editor;
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
        int size = this.__parentView.getChildCount();
        if (this.__renderType == RenderType.Renderer)
            return size;
        View _view = this.__activeView;
        if (_view == null)
            return size;
        int currentIndex = this.__parentView.indexOfChild(_view);
        EditorType tag = getControlType(_view);
        if (tag == EditorType.INPUT) {
            int length = ((EditText) this.__activeView).getText().length();
            if (length > 0) {
                return type == EditorType.UL_LI || type == EditorType.OL_LI ? currentIndex : currentIndex;
            } else {
                return currentIndex;
            }
        } else if (tag == EditorType.UL_LI || tag == EditorType.OL_LI) {
            EditText _text = (EditText) _view.findViewById(R.id.txtText);
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
        List<EditorTextStyle> styles = controlTag._ControlStyles;
        if (_op == Op.Delete) {
            int index = styles.indexOf(style);
            if (index != -1) {
                styles.remove(index);
                controlTag._ControlStyles = styles;
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
        control._ControlStyles = new ArrayList<>();
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
        int index = __parentView.indexOfChild(view);
        if (index == 0)
            return;
        EditorControl contentType = (EditorControl) ((View) view.getParent()).getTag();
          /*
         *
         * If the person was on an active ul|li, move him to the previous node
         *
         */


        if (contentType != null && (contentType.Type == EditorType.OL_LI || contentType.Type == EditorType.UL_LI)) {
            __listItemExtensions.validateAndRemoveLisNode(view, contentType);
            return;
        }

        View toFocus = __parentView.getChildAt(index - 1);
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
            this.__parentView.removeView(view);
            __listItemExtensions.setFocusToList(toFocus, ListItemExtensions.POSITION_END);
        } else {
            removeParent(view);
        }
    }


    public int removeParent(View view) {
        int indexOfDeleteItem = __parentView.indexOfChild(view);
        View nextItem = null;
        //remove hr if its on top of the delete field
        this.__parentView.removeView(view);
        Log.d("indexOfDeleteItem", "indexOfDeleteItem : " + indexOfDeleteItem);
        if (__dividerExtensions.deleteHr(Math.max(0, indexOfDeleteItem - 1)))
            indexOfDeleteItem -= 1;
        for (int i = 0; i < indexOfDeleteItem; i++) {
            if (getControlType(__parentView.getChildAt(i)) == EditorType.INPUT) {
                nextItem = __parentView.getChildAt(i);
                continue;
            }
        }
        if (nextItem != null) {
            CustomEditText text = (CustomEditText) nextItem;
            if (text.requestFocus()) {
                text.setSelection(text.getText().length());
            }
            this.__activeView = nextItem;
        }
        return indexOfDeleteItem;
    }


    public EditorContent getStateFromString(String content) {
        if (content == null) {
            content = getValue("editorState", "");
        }
        EditorContent deserialized = __gson.fromJson(content, EditorContent.class);
        return deserialized;
    }

    public String getValue(String Key, String defaultVal) {
        SharedPreferences _Preferences = __context.getSharedPreferences(SHAREDPREFERENCE, 0);
        return _Preferences.getString(Key, defaultVal);

    }

    public void putValue(String Key, String Value) {
        SharedPreferences _Preferences = __context.getSharedPreferences(SHAREDPREFERENCE, 0);
        SharedPreferences.Editor editor = _Preferences.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public String getContentAsSerialized() {
        EditorContent state = getContent();
        return serializeContent(state);
    }

    public String getContentAsSerialized(EditorContent state) {
        return serializeContent(state);
    }

    public EditorContent getContentDeserialized(String EditorContentSerialized) {
        EditorContent Deserialized = __gson.fromJson(EditorContentSerialized, EditorContent.class);
        return Deserialized;
    }

    public String serializeContent(EditorContent _state) {
        String serialized = __gson.toJson(_state);
        return serialized;
    }

    public EditorContent getContent() {

        if (this.__renderType == RenderType.Renderer) {
            __utilities.toastItOut("This option only available in editor mode");
            return null;
        }

        int childCount = this.__parentView.getChildCount();
        EditorContent editorState = new EditorContent();
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            Node node = new Node();
            View view = __parentView.getChildAt(i);
            EditorType type = getControlType(view);
            node.type = type;
            node.content = new ArrayList<>();
            switch (type) {
                case INPUT:
                    EditText _text = (EditText) view;
                    EditorControl tag = (EditorControl) view.getTag();
                    node.contentStyles = tag._ControlStyles;
                    node.content.add(Html.toHtml(_text.getText()));
                    list.add(node);
                    break;
                case img:
                    EditorControl imgTag = (EditorControl) view.getTag();
                    if (!TextUtils.isEmpty(imgTag.path)) {
                        node.content.add(imgTag.path);
                        Editable desc = ((EditText) view.findViewById(R.id.desc)).getText();
                        node.content.add(desc.length() > 0 ? desc.toString() : "");
                        list.add(node);
                    }
                    //field type, content[]
                    break;
                case hr:
                    list.add(node);
                    break;
                case ul:
                case ol:
                    TableLayout table = (TableLayout) view;
                    int _rowCount = table.getChildCount();
                    for (int j = 0; j < _rowCount; j++) {
                        View row = table.getChildAt(j);
                        EditText li = (EditText) row.findViewById(R.id.txtText);
                        node.content.add(Html.toHtml(li.getText()));
                    }
                    list.add(node);
                    break;
                case map:
                    EditorControl mapTag = (EditorControl) view.getTag();
                    Editable desc = ((CustomEditText) view.findViewById(R.id.desc)).getText();
                    node.content.add(mapTag.Cords);
                    node.content.add(desc.length() > 0 ? desc.toString() : "");
                    list.add(node);
            }
        }
        editorState.nodes = list;
        return editorState;
    }

    public void renderEditor(EditorContent _state) {
        this.__parentView.removeAllViews();
        for (Node item : _state.nodes) {
            switch (item.type) {
                case INPUT:
                    String text = item.content.get(0);
                    TextView view = __inputExtensions.insertEditText(0, this.placeHolder, text);
                    if (item.contentStyles != null) {
                        for (EditorTextStyle style : item.contentStyles) {
                            __inputExtensions.UpdateTextStyle(style, view);
                        }
                    }
                    break;
                case hr:
                    __dividerExtensions.insertDivider();
                    break;
                case img:
                    String path = item.content.get(0);
                    String desc = item.content.get(1);
                    __imageExtensions.loadImage(path, desc);
                    break;
                case ul:
                case ol:
                    TableLayout _layout = null;
                    for (int i = 0; i < item.content.size(); i++) {
                        if (i == 0) {
                            _layout = __listItemExtensions.insertList(_state.nodes.indexOf(item), item.type == EditorType.ol, item.content.get(i));
                        } else {
                            __listItemExtensions.AddListItem(_layout, item.type == EditorType.ol, item.content.get(i));
                        }
                    }
                    break;
                case map:
                    __mapExtensions.insertMap(item.content.get(0), item.content.get(1), true);
                    break;
            }
        }
    }


    public boolean isLastRow(View view) {
        int index = this.__parentView.indexOfChild(view);
        int length = this.__parentView.getChildCount();
        return length - 1 == index;
    }


    public void renderEditorFromHtml(String content) {
        __htmlExtensions.parseHtml(content);
    }

    public void clearAllContents() {
        this.__parentView.removeAllViews();

    }

    public void onBackspace(CustomEditText editText) {
        int len = editText.getText().length();
        int selection = editText.getSelectionStart();
        if (selection == 0)
            return;
        editText.getText().delete(selection, 1);

//                if(editText.requestFocus())
//                editText.setSelection(editText.getText().length());
    }

    public boolean onKey(View v, int keyCode, KeyEvent event, CustomEditText editText) {
        if (keyCode != KeyEvent.KEYCODE_DEL) {
            return false;
        }
        if (__inputExtensions.isEditTextEmpty(editText)) {
            deleteFocusedPrevious(editText);
            int controlCount = getParentChildCount();
            if (controlCount == 1)
                return checkLastControl();
            return false;
        }
        int length = editText.getText().length();
        int selectionStart = editText.getSelectionStart();

        EditorType editorType = getControlType(this.__activeView);
        CustomEditText nextFocus;
        if (selectionStart == 0 && length > 0) {
            if ((editorType == EditorType.UL_LI || editorType == EditorType.OL_LI)) {
                //now that we are inside the edittext, focus inside it
                int index = __listItemExtensions.getIndexOnEditorByEditText(editText);
                if (index == 0) {
                    deleteFocusedPrevious(editText);
                }
            } else {
                int index = getParentView().indexOfChild(editText);
                if (index == 0)
                    return false;
                nextFocus = __inputExtensions.getEditTextPrevious(index);
                deleteFocusedPrevious(editText);
                nextFocus.setText(nextFocus.getText().toString() + editText.getText().toString());
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
                __parentView.removeAllViews();
                break;
        }

        return false;
    }

    public class Utilities {
        public int[] getScreenDimension() {
            Display display = ((Activity) __context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            int[] dimen = {width, height};
            return dimen;
        }

        public void toastItOut(String message) {
            Toast.makeText(__context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
