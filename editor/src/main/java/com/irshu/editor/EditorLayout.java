package com.irshu.editor;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.irshu.editor.models.ControlStyles;
import com.irshu.editor.models.EditorState;
import com.irshu.editor.models.RenderType;

/**
 * Created by mkallingal on 4/22/2016.
 */
public class EditorLayout extends LinearLayout {
    Editor _Editor;
    public EditorLayout(Context _Context,LinearLayout _ParentView, RenderType _RenderType, String _PlaceHolderText){
        super(_Context);
        initialize(_Context, _ParentView, _RenderType, _PlaceHolderText);
    }
    public EditorLayout startEditor(){
        _Editor.StartEditor();
        return this;
    }
    private void initialize(Context context,LinearLayout _ParentView, RenderType _RenderType, String _PlaceHolderText) {
        _Editor=new Editor(context,_ParentView,_RenderType,_PlaceHolderText);
    }
    public void InsertImage(){
        _Editor.OpenImageGallery();
    }
    public void InsertUnorderedList(){
        _Editor.InsertUnorderedList();
    }
    public void InsertDivider(){
        _Editor.InsertDivider();
    }
    public void UpdateTextStyle(ControlStyles style){
        _Editor.UpdateTextStyle(style);
    }
    public void InsertLink() {
        _Editor.InsertLink();
    }
}