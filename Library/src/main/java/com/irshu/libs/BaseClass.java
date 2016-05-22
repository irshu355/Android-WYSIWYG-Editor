        package com.irshu.libs;
        import android.app.Activity;
        import android.content.Context;
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

        import com.google.gson.Gson;
        import com.irshu.editor.R;
        import com.irshu.libs.Components.DividerExtensions;
        import com.irshu.libs.Components.ImageExtensions;
        import com.irshu.libs.Components.InputExtensions;
        import com.irshu.libs.Components.ListItemExtensions;
        import com.irshu.libs.Components.MapExtensions;
        import com.irshu.libs.models.ControlStyles;
        import com.irshu.libs.models.DataEngine;
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
            public int H1TEXTSIZE =20;
            public int H2TEXTSIZE =16;
            public int NORMALTEXTSIZE =14;
                        /*
            * Divider initializors
            */
            public int dividerBackground=R.drawable.divider_background_light;
            public Context context;
            public LinearLayout parentView;
            public RenderType renderType;
            public Resources resources;
            public View activeView;
            public DataEngine objEngine;
            public Gson gson;
            public Utilitiles utilitiles;
            public EditorListener listener;
            public final int MAP_MARKER_REQUEST =20;
            public final int PICK_IMAGE_REQUEST =1;
            public String ImageUploaderUri;
            public InputExtensions inputExtensions;
            public ImageExtensions imageExtensions;
            public ListItemExtensions listItemExtensions;
            public DividerExtensions dividerExtensions;
            public MapExtensions mapExtensions;

                public BaseClass(Context _context, AttributeSet attrs){
                    super(_context,attrs);
                    this.context = _context;
                    this.setOrientation(VERTICAL);
                    loadStateFromAttrs(attrs);
                utilitiles=new Utilitiles();
                this.resources = context.getResources();
                objEngine=new DataEngine(_context);
                gson=new Gson();
                inputExtensions=new InputExtensions(this);
                imageExtensions=new ImageExtensions(this);
                listItemExtensions=new ListItemExtensions(this);
                dividerExtensions =new DividerExtensions(this);
                mapExtensions= new MapExtensions(this);
                this.parentView = this;
            }
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
                        this.renderType = com.irshu.libs.models.RenderType.Editor;
                    }else{
                        this.renderType = renderType.toLowerCase().equals("renderer")?RenderType.Renderer :RenderType.Editor;
                    }

                } finally {
                    if (a != null) {
                        a.recycle(); // ensure this is always called
                    }
                }
            }


            public int determineIndex(EditorType type){
                int size= this.parentView.getChildCount();
                if(this.renderType ==RenderType.Renderer)
                    return size;
                View _view= this.activeView;
                if(_view==null)
                    return size;
                int currentIndex= this.parentView.indexOfChild(_view);
                EditorType tag = GetControlType(_view);
                if(tag==EditorType.INPUT){
                    int length=((EditText)this.activeView).getText().length();
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
                int indexOfDeleteItem= parentView.indexOfChild(view);
                View nextItem=null;
                if(indexOfDeleteItem==0)
                    return;
                //remove hr if its on top of the delete field
              dividerExtensions.deleteHr(indexOfDeleteItem - 1);
                for (int i=0;i< parentView.getChildCount();i++){
                    if(i<indexOfDeleteItem&&GetControlType(parentView.getChildAt(i))==EditorType.INPUT){
                        nextItem= parentView.getChildAt(i);
                    }
                }
                this.parentView.removeView(view);
                if(nextItem!=null) {
                    EditText text = (EditText) nextItem;
                    String x=text.getText().toString();
                    //   _Text.setSelection(_Text.getText().length());
                    if(text.requestFocus()){
                        text.setSelection(text.getText().length());
                    }
                    this.activeView=nextItem;
                }
            }


            public  boolean SaveState(String serialized){
                if(TextUtils.isEmpty(serialized)){
                    serialized=serializeState(GetState());
                }
                objEngine.putValue("editorState", serialized);
                return true;
            }


            public EditorState getStateFromString(String content){
                if(content==null) {
                    content = objEngine.GetValue("editorState", "");
                }
                EditorState deserialized= gson.fromJson(content, EditorState.class);
                return deserialized;
            }

            public String GetStateAsSerialized(){
                EditorState state= GetState();
                return  serializeState(state);
            }

            public String serializeState(EditorState _state){
                String serialized= gson.toJson(_state);
                return serialized;
            }
            public EditorState GetState(){
                int childCount= this.parentView.getChildCount();
                EditorState editorState=new EditorState();
                List<state> list=new ArrayList<>();
                for(int i=0;i<childCount;i++){
                    state state=new state();
                    View view= parentView.getChildAt(i);
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
                this.parentView.removeAllViews();
                List<state> list = _state.stateList;
                for (state item:list){
                    switch (item.type){
                        case INPUT:
                            String text= item.content.get(0);
                           TextView view= inputExtensions.InsertEditText(0, "", text);
                            if(item._ControlStyles!=null){
                                for (ControlStyles style:item._ControlStyles){
                                    inputExtensions.UpdateTextStyle(style,view);
                                }
                            }
                            break;
                        case hr:
                          dividerExtensions.InsertDivider();
                            break;
                        case img:
                            String path= item.content.get(0);
                            ImageView.ScaleType scaleType= ImageView.ScaleType.valueOf(item.content.get(1));
                           imageExtensions.loadImage(path,scaleType);
                            break;
                        case ul:
                        case ol:
                            TableLayout _layout=null;
                            for(int i=0;i<item.content.size();i++){
                                if(i==0){
                                    _layout= listItemExtensions.insertList(list.indexOf(item),item.type==EditorType.ol,item.content.get(i));
                                }else {
                                    listItemExtensions.AddListItem(_layout, item.type==EditorType.ol, item.content.get(i));
                                }
                            }
                            break;
                        case map:
                            mapExtensions.insertMap(item.content.get(0),true);
                            break;
                    }
                }
            }


            public boolean isLastRow(View view){
                int index=this.parentView.indexOfChild(view);
                int length= this.parentView.getChildCount();
                return length-1==index;
            }
            public int GetChildCount(){
                int Count= parentView.getChildCount();
                return  Count;
            }
            public int GetChildCountforView(View view){
                return ((ViewGroup)view).getChildCount();
            }


            public class Utilitiles{
                public float PxtoSp(float px){
                    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
                    float sp = px / scaledDensity;
                    return sp;
                }
                public int[] GetScreenDimension(){
                    Display display =((Activity) context).getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    int[] dimen={width,height};
                    return dimen;
                }
            }

        }
