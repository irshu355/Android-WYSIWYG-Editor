        package com.irshu.libs;
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
        import android.view.Display;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TableLayout;
        import android.widget.TableRow;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.gson.Gson;
        import com.irshu.editor.R;
        import com.irshu.libs.Components.DividerExtensions;
        import com.irshu.libs.Components.HTMLExtensions;
        import com.irshu.libs.Components.ImageExtensions;
        import com.irshu.libs.Components.InputExtensions;
        import com.irshu.libs.Components.ListItemExtensions;
        import com.irshu.libs.Components.MapExtensions;
        import com.irshu.libs.models.ControlStyles;
        import com.irshu.libs.models.EditorControl;
        import com.irshu.libs.models.EditorState;
        import com.irshu.libs.models.EditorType;
        import com.irshu.libs.models.Op;
        import com.irshu.libs.models.RenderType;
        import com.irshu.libs.models.state;
        import java.util.ArrayList;
        import java.util.List;

        /**
         * Created by mkallingal on 4/30/2016.
         */
        public class BaseClass extends LinearLayout {
            /*
            * EditText initializors
            */
            public String PlaceHolder=null;
            /*
            * Divider initializors
            */
            private final String SHAREDPREFERENCE="QA";
            private Context __context;
            private LinearLayout __parentView;
            private RenderType __renderType;
            private Resources __resources;
            private View __activeView;
            private Gson __gson;
            private Utilitiles __utilitiles;
            private EditorListener __listener;
            public final int MAP_MARKER_REQUEST =20;
            public final int PICK_IMAGE_REQUEST =1;
            private String __imageUploaderUri;
            private InputExtensions __inputExtensions;
            private ImageExtensions __imageExtensions;
            private ListItemExtensions __listItemExtensions;
            private DividerExtensions __dividerExtensions;
            private HTMLExtensions __htmlExtensions;
            private MapExtensions __mapExtensions;
                public BaseClass(Context _context, AttributeSet attrs){
                    super(_context,attrs);
                    this.__context = _context;
                    this.setOrientation(VERTICAL);
                    initialize(_context,attrs);
            }
            private void initialize(Context context, AttributeSet attrs) {
                loadStateFromAttrs(attrs);
                __utilitiles =new Utilitiles();
                this.__resources = context.getResources();
                __gson =new Gson();
                __inputExtensions =new InputExtensions(this,context);
                __imageExtensions =new ImageExtensions(this,context);
                __imageExtensions.setImageUploadUri(this.__imageUploaderUri);
                __listItemExtensions =new ListItemExtensions(this,context);
                __dividerExtensions =new DividerExtensions(this,context);
                __mapExtensions = new MapExtensions(this,context);
                __htmlExtensions = new HTMLExtensions(this,context);
                this.__parentView = this;
            }

            //region Getters_and_Setters
            public LinearLayout getParentView(){
                return this.__parentView;
            }
            public RenderType getRenderType(){
                return this.__renderType;
            }

            public Resources getResources(){
                return this.__resources;
            }

            public View getActiveView(){
                return this.__activeView;
            }
            public void setActiveView(View view){
                this.__activeView =view;
            }
            public Utilitiles getUtilitiles(){
                return this.__utilitiles;
            }
            public EditorListener getEditorListener(){
                return this.__listener;
            }
            public void setEditorListener(EditorListener _listener){
                this.__listener = _listener;
            }
            public InputExtensions getInputExtensions(){
                return this.__inputExtensions;
            }
            public ImageExtensions getImageExtensions(){
                return this.__imageExtensions;
            }
            public MapExtensions getMapExtensions(){
                return this.__mapExtensions;
            }
            public HTMLExtensions getHtmlExtensions(){
                return this.__htmlExtensions;
            }
            public ListItemExtensions getListItemExtensions(){
                return this.__listItemExtensions;
            }
            public DividerExtensions getDividerExtensions(){
                return this.__dividerExtensions;
            }
            public String getImageUploaderUri() {
                return this.__imageUploaderUri;
            }

            public void setImageUploaderUri(String imageUploaderUri) {
                this.__imageUploaderUri = imageUploaderUri;
            }

            //endregion
            public interface EditorListener{
                public void onTextChanged(EditText editText, Editable text);
            }

            private void loadStateFromAttrs(AttributeSet attributeSet) {
                if (attributeSet == null) {
                    return; // quick exit
                }

                TypedArray a = null;
                try {
                    a = getContext().obtainStyledAttributes(attributeSet, R.styleable.editor);
                    this.PlaceHolder = a.getString(R.styleable.editor_placeholder);
                    String renderType= a.getString(R.styleable.editor_render_type);
                    if(TextUtils.isEmpty(renderType)) {
                        this.__renderType = com.irshu.libs.models.RenderType.Editor;
                    }else{
                        this.__renderType = renderType.toLowerCase().equals("renderer")?RenderType.Renderer :RenderType.Editor;
                    }

                } finally {
                    if (a != null) {
                        a.recycle(); // ensure this is always called
                    }
                }
            }
            public int determineIndex(EditorType type){
                int size= this.__parentView.getChildCount();
                if(this.__renderType ==RenderType.Renderer)
                    return size;
                View _view= this.__activeView;
                if(_view==null)
                    return size;
                int currentIndex= this.__parentView.indexOfChild(_view);
                EditorType tag = GetControlType(_view);
                if(tag==EditorType.INPUT){
                    int length=((EditText)this.__activeView).getText().length();
                    if(length>0){
                        return type==EditorType.UL_LI ||type==EditorType.OL_LI?currentIndex: currentIndex+1;
                    }else{
                        return currentIndex;
                    }
                }else if (tag==EditorType.UL_LI||tag==EditorType.OL_LI){
                    EditText _text= (EditText) _view.findViewById(R.id.txtText);
                    if(_text.getText().length()>0){

                    }
                    return size;
                }
                else{
                    return size;
                }
            }
            public boolean ContainsStyle(List<ControlStyles> _Styles, ControlStyles style){
                for (ControlStyles item:_Styles){
                    if(item==style){
                        return true;
                    }
                    continue;
                }
                return  false;
            }
            public EditorControl UpdateTagStyle(EditorControl controlTag, ControlStyles style,Op _op){
                List<ControlStyles> styles= controlTag._ControlStyles;
                if(_op==Op.Delete){
                    int index= styles.indexOf(style);
                    if(index!=-1) {
                        styles.remove(index);
                        controlTag._ControlStyles = styles;
                    }
                }else{
                    int index= styles.indexOf(style);
                    if(index==-1){
                        styles.add(style);
                    }
                }
                return controlTag;
            }

            public EditorType GetControlType(View _view) {
                EditorControl _control = (EditorControl) _view.getTag();
                return _control.Type;
            }

            public EditorControl GetControlTag(View view) {
                EditorControl control = (EditorControl) view.getTag();
                return control;
            }
            public EditorControl CreateTag(EditorType type){
                EditorControl control = new EditorControl();
                control.Type=type;
                control._ControlStyles=new ArrayList<>();
                switch (type)
                {
                    case hr:
                    case img:
                    case INPUT:
                    case ul:
                    case UL_LI:
                }
                return control;
            }

            public void deleteFocusedPrevious(EditText view) {
                String parentTagName= view.getParent().getClass().getName();
                if(parentTagName.toLowerCase().equals("android.widget.tablerow")){
                    TableRow _row = (TableRow) view.getParent();
                    TableLayout _table = (TableLayout) _row.getParent();
                    int index = _table.indexOfChild(_row);
                    _table.removeView(_row);
                    if (index > 0) {
                        TableRow focusrow = (TableRow) _table.getChildAt(index - 1);
                        EditText text = (EditText) focusrow.findViewById(R.id.txtText);
                        if (text.requestFocus()) {
                            text.setSelection(text.getText().length());
                        }
                    }else{
                        RemoveParent(_table);
                    }
                }else{
                    RemoveParent(view);
                }
            }

            public void RemoveParent(View view){
                int indexOfDeleteItem= __parentView.indexOfChild(view);
                View nextItem=null;
                if(indexOfDeleteItem==0)
                    return;
                //remove hr if its on top of the delete field
              __dividerExtensions.deleteHr(indexOfDeleteItem - 1);
                for (int i=0;i< __parentView.getChildCount();i++){
                    if(i<indexOfDeleteItem&&GetControlType(__parentView.getChildAt(i))==EditorType.INPUT){
                        nextItem= __parentView.getChildAt(i);
                    }
                }
                this.__parentView.removeView(view);
                if(nextItem!=null) {
                    EditText text = (EditText) nextItem;
                    String x=text.getText().toString();
                    //   _Text.setSelection(_Text.getText().length());
                    if(text.requestFocus()){
                        text.setSelection(text.getText().length());
                    }
                    this.__activeView =nextItem;
                }
            }


            public EditorState getStateFromString(String content){
                if(content==null) {
                    content = GetValue("editorState", "");
                }
                EditorState deserialized= __gson.fromJson(content, EditorState.class);
                return deserialized;
            }

            public String GetValue(String Key, String defaultVal){
                SharedPreferences _Preferences= __context.getSharedPreferences(SHAREDPREFERENCE, 0);
                return   _Preferences.getString(Key, defaultVal);

            }
            public void putValue(String Key, String Value){
                SharedPreferences _Preferences = __context.getSharedPreferences(SHAREDPREFERENCE, 0);
                SharedPreferences.Editor editor = _Preferences.edit();
                editor.putString(Key, Value);
                editor.apply();
            }

            public String getContentAsSerialized(){
                EditorState state= getContent();
                return  serializeContent(state);
            }

            public String getContentAsSerialized(EditorState state){
                return  serializeContent(state);
            }

            public String serializeContent(EditorState _state){
                String serialized= __gson.toJson(_state);
                return serialized;
            }
            public EditorState getContent(){

                if(this.__renderType==RenderType.Renderer){
                    __utilitiles.toastItOut("This option only available in editor mode");
                    return null;
                }

                int childCount= this.__parentView.getChildCount();
                EditorState editorState=new EditorState();
                List<state> list=new ArrayList<>();
                for(int i=0;i<childCount;i++){
                    state state=new state();
                    View view= __parentView.getChildAt(i);
                    EditorType type= GetControlType(view);
                    state.type=type;
                    state.content=new ArrayList<>();
                    switch (type){
                        case INPUT:
                            EditText _text=(EditText)view;
                            EditorControl tag = (EditorControl) view.getTag();
                            state._ControlStyles=tag._ControlStyles;
                            state.content.add(Html.toHtml(_text.getText()));
                            list.add(state);
                            break;
                        case img:
                            EditorControl imgTag = (EditorControl) view.getTag();
                            state.content.add(imgTag.Path);
                            state.content.add(imgTag.scaleType.toString());
                            list.add(state);
                            //field type, content[]
                            break;
                        case hr:
                            list.add(state);
                            break;
                        case ul:
                        case ol:
                            TableLayout table=(TableLayout)view;
                            int _rowCount= table.getChildCount();
                            for (int j=0;j<_rowCount;j++){
                                View row= table.getChildAt(j);
                                EditText li= (EditText) row.findViewById(R.id.txtText);
                                state.content.add(Html.toHtml(li.getText()));
                            }
                            list.add(state);
                            break;
                        case map:
                            EditorControl mapTag = (EditorControl) view.getTag();
                            state.content.add(mapTag.Cords);
                            list.add(state);
                    }
                }
                editorState.stateList=list;
                return editorState;
            }

            public void RenderEditor(EditorState _state) {
                this.__parentView.removeAllViews();
                List<state> list = _state.stateList;
                for (state item:list){
                    switch (item.type){
                        case INPUT:
                            String text= item.content.get(0);
                           TextView view= __inputExtensions.InsertEditText(0, "", text);
                            if(item._ControlStyles!=null){
                                for (ControlStyles style:item._ControlStyles){
                                    __inputExtensions.UpdateTextStyle(style, view);
                                }
                            }
                            break;
                        case hr:
                          __dividerExtensions.InsertDivider();
                            break;
                        case img:
                            String path= item.content.get(0);
                            ImageView.ScaleType scaleType= ImageView.ScaleType.valueOf(item.content.get(1));
                           __imageExtensions.loadImage(path, scaleType);
                            break;
                        case ul:
                        case ol:
                            TableLayout _layout=null;
                            for(int i=0;i<item.content.size();i++){
                                if(i==0){
                                    _layout= __listItemExtensions.insertList(list.indexOf(item),item.type==EditorType.ol,item.content.get(i));
                                }else {
                                    __listItemExtensions.AddListItem(_layout, item.type == EditorType.ol, item.content.get(i));
                                }
                            }
                            break;
                        case map:
                            __mapExtensions.insertMap(item.content.get(0), true);
                            break;
                    }
                }
            }


            public boolean isLastRow(View view){
                int index=this.__parentView.indexOfChild(view);
                int length= this.__parentView.getChildCount();
                return length-1==index;
            }
            public int GetChildCount(){
                int Count= __parentView.getChildCount();
                return  Count;
            }
            public int GetChildCountforView(View view){
                return ((ViewGroup)view).getChildCount();
            }

            public void RenderEditorFromHtml(String content){
                __htmlExtensions.parseHtml(content);
            }

            public class Utilitiles{
                public float PxtoSp(float px){
                    float scaledDensity = __context.getResources().getDisplayMetrics().scaledDensity;
                    float sp = px / scaledDensity;
                    return sp;
                }
                public int[] GetScreenDimension(){
                    Display display =((Activity) __context).getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    int[] dimen={width,height};
                    return dimen;
                }
                public void toastItOut(String message){
                    Toast.makeText(__context, message, Toast.LENGTH_SHORT).show();
                }
            }

        }
