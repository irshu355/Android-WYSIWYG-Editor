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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mkallingal on 4/30/2016.
 */
public class EditorCore extends LinearLayout {
    public static final String TAG = "EDITOR";
    /*
    * EditText initializors
    */
    public String placeHolder = null;
    public boolean autoFocus = true;
    private boolean serialRenderInProgress = false;
    /*
    * Divider initializors
    */
    private final String SHAREDPREFERENCE = "QA";
    private Context context;
    protected LinearLayout parentView;
    private RenderType renderType;
    private Resources resources;
    private View activeView;
    private Gson gson;
    private Utilities utilities;
    private EditorListener listener;
    public final int MAP_MARKER_REQUEST = 20;
    public final int PICK_IMAGE_REQUEST = 1;
    private InputExtensions inputExtensions;
    private ImageExtensions imageExtensions;
    private ListItemExtensions listItemExtensions;
    private DividerExtensions dividerExtensions;
    private HTMLExtensions htmlExtensions;
    private MapExtensions mapExtensions;
    private boolean stateFresh;

    public EditorCore(Context _context, AttributeSet attrs) {
        super(_context, attrs);
        this.context = _context;
        this.setOrientation(VERTICAL);
        initialize(_context, attrs);

    }

    private void initialize(Context context, AttributeSet attrs) {
        loadStateFromAttrs(attrs);
        utilities = new Utilities();
        this.stateFresh = true;
        this.resources = context.getResources();
        gson = new Gson();
        inputExtensions = new InputExtensions(this);
        imageExtensions = new ImageExtensions(this);
        listItemExtensions = new ListItemExtensions(this);
        dividerExtensions = new DividerExtensions(this);
        mapExtensions = new MapExtensions(this);
        htmlExtensions = new HTMLExtensions(this);
        this.parentView = this;
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
        return (Activity) this.context;
    }

    /**
     * used to get the editor node
     *
     * @return
     */
    public LinearLayout getParentView() {
        return this.parentView;
    }

    /**
     * Get number of childs in the editor
     *
     * @return
     */
    public int getParentChildCount() {
        return this.parentView.getChildCount();
    }

    /**
     * returns whether editor is set as Editor or Rendeder
     *
     * @return
     */
    public RenderType getRenderType() {
        return this.renderType;
    }

    /**
     * no idea what this is
     *
     * @return
     */
    public Resources getResources() {
        return this.resources;
    }

    /**
     * The current active view on the editor
     *
     * @return
     */
    public View getActiveView() {
        return this.activeView;
    }

    public void setActiveView(View view) {
        this.activeView = view;
    }

    public Utilities getUtilitiles() {
        return this.utilities;
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
    public InputExtensions getInputExtensions() {
        return this.inputExtensions;
    }

    public ImageExtensions getImageExtensions() {
        return this.imageExtensions;
    }

    public MapExtensions getMapExtensions() {
        return this.mapExtensions;
    }

    public HTMLExtensions getHtmlExtensions() {
        return this.htmlExtensions;
    }

    public ListItemExtensions getListItemExtensions() {
        return this.listItemExtensions;
    }

    public DividerExtensions getDividerExtensions() {
        return this.dividerExtensions;
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
            this.autoFocus = a.getBoolean(R.styleable.editor_auto_focus, true);
            String renderType = a.getString(R.styleable.editor_render_type);
            if (TextUtils.isEmpty(renderType)) {
                this.renderType = com.github.irshulx.models.RenderType.Editor;
            } else {
                this.renderType = renderType.toLowerCase().equals("renderer") ? RenderType.Renderer : RenderType.Editor;
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
        int size = this.parentView.getChildCount();
        if (this.renderType == RenderType.Renderer)
            return size;
        View _view = this.activeView;
        if (_view == null)
            return size;
        int currentIndex = this.parentView.indexOfChild(_view);
        EditorType tag = getControlType(_view);
        if (tag == EditorType.INPUT) {
            int length = ((EditText) this.activeView).getText().length();
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
        int index = parentView.indexOfChild(view);
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

        View toFocus = parentView.getChildAt(index - 1);
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
            this.parentView.removeView(view);
            listItemExtensions.setFocusToList(toFocus, ListItemExtensions.POSITION_END);
        } else {
            removeParent(view);
        }
    }


    public int removeParent(View view) {
        int indexOfDeleteItem = parentView.indexOfChild(view);
        View nextItem = null;
        //remove hr if its on top of the delete field
        this.parentView.removeView(view);
        Log.d("indexOfDeleteItem", "indexOfDeleteItem : " + indexOfDeleteItem);
        if (dividerExtensions.deleteHr(Math.max(0, indexOfDeleteItem - 1)))
            indexOfDeleteItem -= 1;
        for (int i = 0; i < indexOfDeleteItem; i++) {
            if (getControlType(parentView.getChildAt(i)) == EditorType.INPUT) {
                nextItem = parentView.getChildAt(i);
                continue;
            }
        }
        if (nextItem != null) {
            CustomEditText text = (CustomEditText) nextItem;
            if (text.requestFocus()) {
                text.setSelection(text.getText().length());
            }
            this.activeView = nextItem;
        }
        return indexOfDeleteItem;
    }


    public EditorContent getStateFromString(String content) {
        if (content == null) {
            content = getValue("editorState", "");
        }
        EditorContent deserialized = gson.fromJson(content, EditorContent.class);
        return deserialized;
    }

    public String getValue(String Key, String defaultVal) {
        SharedPreferences _Preferences = context.getSharedPreferences(SHAREDPREFERENCE, 0);
        return _Preferences.getString(Key, defaultVal);

    }

    public void putValue(String Key, String Value) {
        SharedPreferences _Preferences = context.getSharedPreferences(SHAREDPREFERENCE, 0);
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
        EditorContent Deserialized = gson.fromJson(EditorContentSerialized, EditorContent.class);
        return Deserialized;
    }

    public String serializeContent(EditorContent _state) {
        String serialized = gson.toJson(_state);
        return serialized;
    }

    private Node getNodeInstance(View view){
        Node node = new Node();
        EditorType type = getControlType(view);
        node.type = type;
        node.content = new ArrayList<>();
        return node;
    }

    public EditorContent getContent() {

        if (this.renderType == RenderType.Renderer) {
            utilities.toastItOut("This option only available in editor mode");
            return null;
        }

        int childCount = this.parentView.getChildCount();
        EditorContent editorState = new EditorContent();
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View view = parentView.getChildAt(i);
            Node node = getNodeInstance(view);
            switch (node.type) {
                case INPUT:
                    EditText _text = (EditText) view;
                    EditorControl tag = (EditorControl) view.getTag();
                    node.contentStyles = tag.editorTextStyles;
                    node.content.add(Html.toHtml(_text.getText()));
                    node.textSettings = tag.textSettings;
                    list.add(node);
                    break;
                case img:
                    EditorControl imgTag = (EditorControl) view.getTag();
                    if (!TextUtils.isEmpty(imgTag.path)) {
                        node.content.add(imgTag.path);

                        /**
                         * for subtitle
                         */
                        EditText textView =  view.findViewById(R.id.desc);
                        Node subTitleNode = getNodeInstance(textView);
                        EditorControl descTag = (EditorControl) textView.getTag();
                        subTitleNode.contentStyles = descTag.editorTextStyles;
                        subTitleNode.textSettings = descTag.textSettings;
                        Editable desc = textView.getText();
                        subTitleNode.content.add(Html.toHtml(desc));
                        node.childs = new ArrayList<>();
                        node.childs.add(subTitleNode);
                        list.add(node);

                    }
                    //field type, content[]
                    break;
                case hr:
                    list.add(node);
                    break;
                case ul:
                case ol:
                    node.childs = new ArrayList<>();
                    TableLayout table = (TableLayout) view;
                    int _rowCount = table.getChildCount();
                    for (int j = 0; j < _rowCount; j++) {
                        View row = table.getChildAt(j);
                        Node node1 = getNodeInstance(row);
                        EditText li = row.findViewById(R.id.txtText);
                        EditorControl liTag = (EditorControl) li.getTag();
                        node1.contentStyles = liTag.editorTextStyles;
                        node1.content.add(Html.toHtml(li.getText()));
                        node1.textSettings = liTag.textSettings;
                        node1.content.add(Html.toHtml(li.getText()));
                        node.childs.add(node1);
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
        this.parentView.removeAllViews();
        serialRenderInProgress  = true;
        for (Node item : _state.nodes) {
            switch (item.type) {
                case INPUT:
                    String text = item.content.get(0);
                    TextView view = inputExtensions.insertEditText(getChildCount(), this.placeHolder, text);
                    getInputExtensions().applyTextSettings(item, view);
                    break;
                case hr:
                    dividerExtensions.insertDivider(_state.nodes.indexOf(item));
                    break;
                case img:
                    String path = item.content.get(0);
                    if(getRenderType() == RenderType.Renderer) {
                        imageExtensions.loadImage(path, item.childs.get(0));
                    }else{
                        View layout = imageExtensions.insertImage(null,path,getChildCount(),item.childs.get(0).content.get(0), false);
                        getInputExtensions().applyTextSettings(item.childs.get(0), (TextView) layout.findViewById(R.id.desc));
                    }
                    break;
                case ul:
                case ol:
                    getListItemExtensions().onRenderfromEditorState(_state, item);
                    break;
                case map:
                    mapExtensions.insertMap(item.content.get(0), item.content.get(1), true);
                    break;
            }
        }
        serialRenderInProgress = false;
    }


    public boolean isLastRow(View view) {
        int index = this.parentView.indexOfChild(view);
        int length = this.parentView.getChildCount();
        return length - 1 == index;
    }


    public void renderEditorFromHtml(String content) {
        serialRenderInProgress = true;
        htmlExtensions.parseHtml(content);
        serialRenderInProgress = false;
    }

    public void clearAllContents() {
        this.parentView.removeAllViews();

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
        if (inputExtensions.isEditTextEmpty(editText)) {
            deleteFocusedPrevious(editText);
            int controlCount = getParentChildCount();
            if (controlCount == 1)
                return checkLastControl();
            return false;
        }
        int length = editText.getText().length();
        int selectionStart = editText.getSelectionStart();

        EditorType editorType = getControlType(this.activeView);
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
                parentView.removeAllViews();
                break;
        }

        return false;
    }

    public boolean isStateFresh() {
        return stateFresh;
    }

    public void setStateFresh(boolean stateFresh) {
        this.stateFresh = stateFresh;
    }

    public boolean isSerialRenderInProgress() {
        return serialRenderInProgress;
    }

    public void setSerialRenderInProgress(boolean serialRenderInProgress) {
        this.serialRenderInProgress = serialRenderInProgress;
    }

    public class Utilities {
        public int[] getScreenDimension() {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            int[] dimen = {width, height};
            return dimen;
        }

        public void toastItOut(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        public boolean containsString(String text){
             String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
             Pattern pattern = Pattern.compile(HTML_PATTERN);
                Matcher matcher = pattern.matcher(text);
                return matcher.matches();
        }

        public int dpToPixel(float dp) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            return (int) px;
        }
    }
}
