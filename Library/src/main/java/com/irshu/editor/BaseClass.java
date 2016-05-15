        package com.irshu.editor;
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
        import android.widget.LinearLayout;
        import android.widget.TableLayout;
        import android.widget.TableRow;
        import com.google.gson.Gson;
        import com.irshu.editor.Components.DividerExtensions;
        import com.irshu.editor.Components.ImageExtensions;
        import com.irshu.editor.Components.InputExtensions;
        import com.irshu.editor.Components.ListItemExtensions;
        import com.irshu.editor.Components.MapExtensions;
        import com.irshu.editor.models.ControlStyles;
        import com.irshu.editor.models.DataEngine;
        import com.irshu.editor.models.EditorControl;
        import com.irshu.editor.models.EditorState;
        import com.irshu.editor.models.EditorType;
        import com.irshu.editor.models.Op;
        import com.irshu.editor.models.RenderType;
        import com.irshu.editor.models.state;
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
            public Context _Context;
            public LinearLayout _ParentView;
            public RenderType _RenderType;
            public Resources _Resources;
            public View activeView;
            public DataEngine objEngine;
            public Gson gson;
            public Utilitiles utilitiles;
            public EditorListener listener;
            public final int MAP_MARKER_REQUEST =20;
            public final int PICK_IMAGE_REQUEST =1;
            public InputExtensions inputExtensions;
            public ImageExtensions imageExtensions;
            public ListItemExtensions listItemExtensions;
            public DividerExtensions dividerExtensions;
            public MapExtensions mapExtensions;

                public BaseClass(Context _context, AttributeSet attrs){
                    super(_context,attrs);
                    this._Context= _context;

                    loadStateFromAttrs(attrs);
                utilitiles=new Utilitiles();
                this._Resources = _Context.getResources();
                objEngine=new DataEngine(_context);
                gson=new Gson();
                inputExtensions=new InputExtensions(this);
                imageExtensions=new ImageExtensions(this);
                listItemExtensions=new ListItemExtensions(this);
                dividerExtensions =new DividerExtensions(this);
                mapExtensions= new MapExtensions(this);
                this._ParentView = this;
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
                        this._RenderType = com.irshu.editor.models.RenderType.Editor;
                    }else{
                        this._RenderType= renderType.toLowerCase().equals("readonly")?RenderType.ReadOnly:RenderType.Editor;
                    }

                } finally {
                    if (a != null) {
                        a.recycle(); // ensure this is always called
                    }
                }
            }


            public int determineIndex(EditorType type){
                int size= this._ParentView.getChildCount();
                if(this._RenderType==RenderType.ReadOnly)
                    return size;
                View _view= this.activeView;
                if(_view==null)
                    return size;
                int currentIndex= this._ParentView.indexOfChild(_view);
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
            public EditorControl UpdateTagStyle(EditorControl _controlTag, ControlStyles style,Op _op){
                List<ControlStyles> _Styles= _controlTag._ControlStyles;
                if(_op==Op.Delete){
                    int index= _Styles.indexOf(style);
                    if(index!=-1) {
                        _Styles.remove(index);
                        _controlTag._ControlStyles = _Styles;
                    }
                }else{
                    int index= _Styles.indexOf(style);
                    if(index==-1){
                        _Styles.add(style);
                    }
                }
                return _controlTag;
            }

            public EditorType GetControlType(View _view) {
                EditorControl _control = (EditorControl) _view.getTag();
                return _control.Type;
            }

            public EditorControl GetControlTag(View _view) {
                EditorControl _control = (EditorControl) _view.getTag();
                return _control;
            }
            public EditorControl CreateTag(EditorType type){
                EditorControl _control = new EditorControl();
                _control.Type=type;
                _control._ControlStyles=new ArrayList<>();
                switch (type)
                {
                    case hr:
                    case img:
                    case INPUT:
                    case ul:
                    case UL_LI:
                }
                return _control;
            }

            public void deleteFocusedPrevious(EditText view) {
                String _ParentTagName= view.getParent().getClass().getName();
                if(_ParentTagName.toLowerCase().equals("android.widget.tablerow")){
                    TableRow _row = (TableRow) view.getParent();
                    TableLayout _table = (TableLayout) _row.getParent();
                    int index = _table.indexOfChild(_row);
                    _table.removeView(_row);
                    if (index > 0) {
                        TableRow _Focusrow = (TableRow) _table.getChildAt(index - 1);
                        EditText _Text = (EditText) _Focusrow.findViewById(R.id.txtText);
                        if (_Text.requestFocus()) {
                            _Text.setSelection(_Text.getText().length());
                        }
                    }else{
                        RemoveParent(_table);
                    }
                }else{
                    RemoveParent(view);
                }
            }

            public void RemoveParent(View view){
                int IndexOfDeleteItem=_ParentView.indexOfChild(view);
                View _nextItem=null;
                if(IndexOfDeleteItem==0)
                    return;
                //remove hr if its on top of the delete field
              dividerExtensions.deleteHr(IndexOfDeleteItem - 1);
                for (int i=0;i<_ParentView.getChildCount();i++){
                    if(i<IndexOfDeleteItem&&GetControlType(_ParentView.getChildAt(i))==EditorType.INPUT){
                        _nextItem=_ParentView.getChildAt(i);
                    }
                }
                this._ParentView.removeView(view);
                if(_nextItem!=null) {
                    EditText _Text = (EditText) _nextItem;
                    String x=_Text.getText().toString();
                    //   _Text.setSelection(_Text.getText().length());
                    if(_Text.requestFocus()){
                        _Text.setSelection(_Text.getText().length());
                    }
                    this.activeView=_nextItem;
                }
            }


            public  boolean SaveState(String serialized){
                if(TextUtils.isEmpty(serialized)){
                    serialized=serializeState(GetState());
                }
                objEngine.putValue("editorState", serialized);
                return true;
            }


            public EditorState GetStateFromStorage(){
                String serialized= objEngine.GetValue("editorState", "");
                EditorState Deserialized= gson.fromJson(serialized, EditorState.class);
                return Deserialized;
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
                int _count= this._ParentView.getChildCount();
                EditorState _editorState=new EditorState();
                List<state> _list=new ArrayList<>();
                for(int i=0;i<_count;i++){
                    state _state=new state();
                    View _view= _ParentView.getChildAt(i);
                    EditorType type= GetControlType(_view);
                    _state.type=type;
                    _state.content=new ArrayList<>();
                    switch (type){
                        case INPUT:
                            EditText _text=(EditText)_view;
                            _state.content.add(Html.toHtml(_text.getText()));
                            _list.add(_state);
                            break;
                        case img:
                            EditorControl imgTag = (EditorControl) _view.getTag();
                            _state.content.add(imgTag.Path);
                            _state.content.add(imgTag.UUID);
                            _list.add(_state);
                            //field type, content[]
                            break;
                        case hr:
                            _list.add(_state);
                            break;
                        case ul:
                        case ol:
                            TableLayout _table=(TableLayout)_view;
                            int _rowCount= _table.getChildCount();
                            for (int j=0;j<_rowCount;j++){
                                View row= _table.getChildAt(j);
                                EditText _li= (EditText) row.findViewById(R.id.txtText);
                                _state.content.add(Html.toHtml(_li.getText()));
                            }
                            _list.add(_state);
                            break;
                        case map:
                            EditorControl mapTag = (EditorControl) _view.getTag();
                            _state.content.add(mapTag.Cords);
                            _list.add(_state);
                    }
                }
                _editorState.stateList=_list;
                return _editorState;
            }
            public boolean isLastRow(View view){
                int index=this._ParentView.indexOfChild(view);
                int length= this._ParentView.getChildCount();
                return length-1==index;
            }
            public int GetChildCount(){
                int Count= _ParentView.getChildCount();
                return  Count;
            }
            public int GetChildCountforView(View view){
                return ((ViewGroup)view).getChildCount();
            }


            public class Utilitiles{
                public float PxtoSp(float px){
                    float scaledDensity = _Context.getResources().getDisplayMetrics().scaledDensity;
                    float sp = px / scaledDensity;
                    return sp;
                }
                public int[] GetScreenDimension(){
                    Display display =((Activity)_Context).getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    int[] dimen={width,height};
                    return dimen;
                }
            }

        }
