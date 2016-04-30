/*
 * Copyright (C) 2016 Muhammed Irshad
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.irshu.editor;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.irshu.editor.models.ControlStyles;
import com.irshu.editor.models.DataEngine;
import com.irshu.editor.models.EditorControl;
import com.irshu.editor.models.EditorState;
import com.irshu.editor.models.EditorType;
import com.irshu.editor.models.Op;
import com.irshu.editor.models.RenderType;
import com.irshu.editor.models.TextStyle;
import com.irshu.editor.models.state;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import mehdi.sakout.fancybuttons.FancyButton;

public class Editor extends AppCompatActivity {
    public Context _Context;
    private Utilities _utilities;
    public LinearLayout _ParentView;
    private RenderType _RenderType;
    public Resources _Resources;
    public List<View> _Views;
    private View activeView;
    private TextStyle _ActiveTextStyle = TextStyle.NORMAL;
    private DataEngine objEngine;
    private Gson gson;
    private String PlaceHolder=null;
    private int H1TextSize=20;
    private int H2TextSize=16;
    private int NormalTextSize=14;
    public Editor() {
    }

    public Editor(Context _context, LinearLayout parentView, RenderType renderType, String placeholder) {
        this._Context = _context;
        this._ParentView = parentView;
        this._Resources = _Context.getResources();
        _utilities=new Editor.Utilities();
        objEngine=new DataEngine(_context);
        this._RenderType= renderType;
        this.PlaceHolder=placeholder;
        gson=new Gson();

    }

    public void StartEditor(){
        AttachHandlers();
        CustomEditText _Text= InsertTextView(0,this.PlaceHolder,"");
        Activity _activity=(Activity)this._Context;
        final HorizontalScrollView ControlPanel=(HorizontalScrollView)_activity.findViewById(R.id.control_panel);
    }


    public String GetSerialized(){
        String _Serialized= serializeState(GetState());
        return _Serialized;
    }


    private int GetCollectionsLength() {
        return this._Views.size();
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */

    private void setText(EditText _EditText, String text){
        Spanned __ = Html.fromHtml(text);
        CharSequence toReplace = noTrailingwhiteLines(__);
        _EditText.setText(toReplace);
    }
    private TextView GetNewTextView(String text){
        final TextView _TextView = new TextView(_Context);
        _TextView.setTextColor(_Resources.getColor(R.color.darkertext));
        _TextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, _Resources.getDisplayMetrics()), 1.0f);
        _TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.NormalTextSize);
        _TextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if(!TextUtils.isEmpty(text)){
            Spanned __ = Html.fromHtml(text);
            CharSequence toReplace = noTrailingwhiteLines(__);
            _TextView.setText(toReplace);
        }
        return _TextView;
    }

    private CustomEditText GetNewEditText(String hint, String text) {
        final CustomEditText _EditText = new CustomEditText(_Context);
        _EditText.setTextColor(_Resources.getColor(R.color.darkertext));
        _EditText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, _Resources.getDisplayMetrics()), 1.0f);
        _EditText.setTextSize(TypedValue.COMPLEX_UNIT_SP,  this.NormalTextSize);
        _EditText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(hint.length()!=0){
            _EditText.setHint(hint);
        }
        if(text.length()!=0){
            setText(_EditText, text);
        }
            _EditText.setTag(CreateTag(EditorType.INPUT));
            _EditText.setBackgroundDrawable(_Context.getResources().getDrawable(R.drawable.invisible_edit_text));

            _EditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activeView = v;
                    //  toggleToolbarProperties(v,null);
                }
            });

            _EditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (_utilities.IsEditTextNull(_EditText)) {
                            deleteFocusedPrevious(_EditText);
                        }
                    }
                    return false;
                }
            });

            _EditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0) {
//                    deleteFocusedPrevious(_EditText);
//                }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = Html.toHtml(_EditText.getText());
                    if (s.length() > 0) {
                        if (s.charAt(s.length() - 1) == '\n') {
                            text = text.replaceAll("<br>", "");
                            setText(_EditText, text);
                            int index = _ParentView.indexOfChild(_EditText);
                            InsertTextView(index + 1, "", "");
                            clearActiveStyles();
                        }
                    }
                }
            });
        return _EditText;
    }

    public CustomEditText InsertTextView(int position,String hint, String text) {
        if(this._RenderType==RenderType.Editor) {
            final CustomEditText _view = GetNewEditText(hint, text);
            if (position == 0) {
                _ParentView.addView(_view);
            } else {
                _ParentView.addView(_view, position);
            }
            this.activeView = _view;
            if (_view.requestFocus()) {
                //   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                activeView = _view;
            }
            return _view;
        }else{
            final TextView _view = GetNewTextView(text);
            _ParentView.addView(_view);
            return  new CustomEditText(this._Context);
        }
    }

    private void deleteFocusedPrevious(EditText view) {
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

    private void RemoveParent(View view){
        int IndexOfDeleteItem=_ParentView.indexOfChild(view);
        View _nextItem=null;
        if(IndexOfDeleteItem==0)
            return;
        //remove hr if its on top of the delete field
        deleteHr(IndexOfDeleteItem - 1);
        for (int i=0;i<_ParentView.getChildCount();i++){
            if(i<IndexOfDeleteItem&&_utilities.GetControlType(_ParentView.getChildAt(i))==EditorType.INPUT){
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
            clearActiveStyles();
            this.activeView=_nextItem;
        }
    }

    private void deleteHr(int indexOfDeleteItem) {
        View view= _ParentView.getChildAt(indexOfDeleteItem);
        if(_utilities.GetControlType(view)==EditorType.hr){
            _ParentView.removeView(view);
        }
    }

    private void clearActiveStyles() {
        if(this._RenderType==RenderType.Editor) {
            this._ActiveTextStyle = TextStyle.NORMAL;
        }
    }


    private void toggleToolbarProperties(View view, EditorType typeOfControl){
        EditorControl _tag= (EditorControl) view.getTag();
        if(typeOfControl==null){
            typeOfControl=_tag.Type;
        }
        if(_tag.Type==EditorType.LI){

        }
        if(typeOfControl==EditorType.LI ||typeOfControl==EditorType.INPUT){
            EditText text= (EditText)view;
            float textSize= _utilities.PxtoSp(text.getTextSize());
            if(textSize!=14){
                toggleTextSize(true);
            }else{
                toggleTextSize(false);
            }
        }
        if(_tag.Type==EditorType.LI){

        }else{
        }
    }


    private void toggleTextSize(boolean isTrue){
        if(isTrue) {

        }else{
        }
    }




    public void UpdateTextStyle(ControlStyles style) {
        /// String type = GetControlType(activeView);
        EditText text;
        try {
            text = (EditText) activeView;
            EditorControl _Tag= _utilities.GetControlTag(text);
            if(style==ControlStyles.H1){
                if(_utilities.ContainsStyle(_Tag._ControlStyles,ControlStyles.H1)) {
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.NormalTextSize);
                  _Tag= _utilities.UpdateTagStyle(_Tag, ControlStyles.H1, Op.Delete);
                    _Tag= _utilities.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Delete);
                    _Tag= _utilities.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Insert);
                }else{
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.H1TextSize);
                    _Tag= _utilities.UpdateTagStyle(_Tag,ControlStyles.H1,Op.Insert);
                    _Tag= _utilities.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Delete);
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Delete);
                }

            }
            else if(style==ControlStyles.H2){
                if(_utilities.ContainsStyle(_Tag._ControlStyles,ControlStyles.H2)) {
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.NormalTextSize);
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.H1, Op.Delete);
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Delete);
                    _Tag= _utilities.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Insert);
                }else{
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.H2TextSize);
                    _Tag= _utilities.UpdateTagStyle(_Tag,ControlStyles.H1,Op.Delete);
                    _Tag= _utilities.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Insert);
                    _Tag= _utilities.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Delete);
                }
            }
            else if(style==ControlStyles.NORMAL){
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.NormalTextSize);
                _Tag= _utilities.UpdateTagStyle(_Tag, ControlStyles.H1, Op.Delete);
                _Tag=   _utilities.UpdateTagStyle(_Tag, ControlStyles.H2, Op.Delete);
                if(!_utilities.ContainsStyle(_Tag._ControlStyles,ControlStyles.NORMAL)) {
                    _Tag=   _utilities.UpdateTagStyle(_Tag, ControlStyles.NORMAL, Op.Insert);
                }
            }
            else if(style==ControlStyles.BOLD){
                if(_utilities.ContainsStyle(_Tag._ControlStyles,ControlStyles.BOLD)) {
                    _Tag=   _utilities.UpdateTagStyle(_Tag, ControlStyles.BOLD, Op.Delete);
                    text.setTypeface(null, Typeface.NORMAL);
                }else if(_utilities.ContainsStyle(_Tag._ControlStyles,ControlStyles.BOLDITALIC)){
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.BOLDITALIC, Op.Delete);
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.ITALIC, Op.Insert);
                    text.setTypeface(null, Typeface.ITALIC);
                }
                else if(_utilities.ContainsStyle(_Tag._ControlStyles,ControlStyles.ITALIC)){
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.BOLDITALIC, Op.Insert);
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.ITALIC, Op.Delete);
                    text.setTypeface(null, Typeface.BOLD_ITALIC);
                }else{
                    _Tag=   _utilities.UpdateTagStyle(_Tag, ControlStyles.BOLD, Op.Insert);
                    text.setTypeface(null, Typeface.BOLD);
                }
            }
            else if(style==ControlStyles.ITALIC){
                if(_utilities.ContainsStyle(_Tag._ControlStyles,ControlStyles.ITALIC)) {
                    _Tag=   _utilities.UpdateTagStyle(_Tag, ControlStyles.ITALIC, Op.Delete);
                    text.setTypeface(null, Typeface.NORMAL);
                }else if(_utilities.ContainsStyle(_Tag._ControlStyles,ControlStyles.BOLDITALIC)){
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.BOLDITALIC, Op.Delete);
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.BOLD, Op.Insert);
                    text.setTypeface(null, Typeface.BOLD);
                }
                else if(_utilities.ContainsStyle(_Tag._ControlStyles,ControlStyles.BOLD)){
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.BOLDITALIC, Op.Insert);
                    _Tag=  _utilities.UpdateTagStyle(_Tag, ControlStyles.BOLD, Op.Delete);
                    text.setTypeface(null, Typeface.BOLD_ITALIC);
                }else{
                    _Tag=   _utilities.UpdateTagStyle(_Tag, ControlStyles.ITALIC, Op.Insert);
                    text.setTypeface(null, Typeface.ITALIC);
                }
            }
            text.setTag(_Tag);
        }
        catch (Exception e){

        }

    }

    public void InsertLink() {
        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this._Context);
        inputAlert.setTitle("Add a link");
        final EditText userInput = new EditText(this._Context);
        //dont forget to add some margins on the left and right to match the title
        userInput.setHint("http://");
        userInput.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        inputAlert.setView(userInput);
        inputAlert.setPositiveButton("Insert", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInputValue = userInput.getText().toString();
                InsertLink(userInputValue);
            }
        });
        inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = inputAlert.create();
        alertDialog.show();
    }

    private void InsertLink(String uri) {
        EditorType type = _utilities.GetControlType(this.activeView);
        EditText editText = (EditText) this.activeView;
        if (type == EditorType.INPUT ||type==EditorType.LI) {
            String text = Html.toHtml(editText.getText());
            text=_utilities.trimLineEnding(text);
            Document _doc= Jsoup.parse(text);
            Elements x= _doc.select("p");

            if(x.size()>0){
                String existing= x.get(0).html();
                x.get(0).html(existing + " <a href='" + uri + "'>" + uri + "</a>");
                Spanned toTrim=Html.fromHtml(x.toString());
                CharSequence trimmed = noTrailingwhiteLines(toTrim);
                editText.setText(trimmed);   //"<p><a href='#'>Hello</a> There, how are you?</p>"
                editText.setSelection(editText.getText().length());
            }
        }
    }


    private CharSequence noTrailingwhiteLines(CharSequence text) {
        if(text.length()==0)
            return text;
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    public void OpenImageGallery() {
        int Index= determineIndex(EditorType.none);
        EditorState state= GetState();
        state.PendingIndex= Index;
        String serialized= serializeState(state);
        SaveState(serialized);

         int PICK_IMAGE_REQUEST = 1;
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        ((Activity)_Context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void InsertImage(Bitmap _image) {
        RenderEditor(GetStateFromStorage());
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        _ImageView.setImageBitmap(_image);
        int count=_ParentView.getChildCount();
       String uuid= objEngine.GenerateUUID();
        String _path= objEngine.SaveImageToInternalStorage(_image, uuid);
        final FancyButton btn = (FancyButton) childLayout.findViewById(R.id.btn_remove);
        _ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setVisibility(View.VISIBLE);
            }
        });
        _ImageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                btn.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ParentView.removeView(childLayout);
            }
        });
        EditorControl _control = new EditorControl();
        _control.Type = EditorType.img;
        _control.UUID= uuid;
        _control.Path=_path;
        childLayout.setTag(_control);

        int Index= determineIndex(EditorType.img);

        _ParentView.addView(childLayout, Index);
//                    _Views.add(childLayout);
        if(isLastRow(childLayout)) {
            InsertTextView(Index + 1,"","");
        }
    }

    public  void loadImage(Bitmap _image, String _path, String fileName, boolean insertEditText){

        if(this._RenderType==RenderType.Editor){
            loadImageFromInternal(_image,_path,fileName,insertEditText);
        }else{
            loadImageFromUri(_image,_path,fileName);
        }


    }

    private void loadImageFromUri(Bitmap image, String path, String fileName) {
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        Picasso.with(this._Context).load(objEngine.GetQImage(fileName)).into(_ImageView);
        _ParentView.addView(childLayout);
    }

    private void loadImageFromInternal(Bitmap _image, String _path, String fileName, boolean insertEditText) {
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        _ImageView.setImageBitmap(_image);
        int count=_ParentView.getChildCount();

        final FancyButton btn = (FancyButton) childLayout.findViewById(R.id.btn_remove);
        _ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setVisibility(View.VISIBLE);
            }
        });
        _ImageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                btn.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ParentView.removeView(childLayout);
            }
        });
        EditorControl _control = CreateTag(EditorType.img);
        _control.UUID= fileName;
        _control.Path=_path;
        childLayout.setTag(_control);
        int Index= determineIndex(EditorType.img);
        _ParentView.addView(childLayout, Index);
        if(insertEditText){
            InsertTextView(Index + 1,"","");
        }
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
            case LI:
        }
        return _control;
    }

    public void InsertDivider(){

        View view=new View(_Context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 3);
        params.setMargins(10, 10, 10, 10);
        view.setLayoutParams(params);
        view.setBackgroundDrawable(_Context.getResources().getDrawable(R.drawable.border_bottom));
        view.setTag(CreateTag(EditorType.hr));
        int Index=determineIndex(EditorType.hr);
        _ParentView.addView(view, Index);
        if(isLastRow(view)) {
            //check if ul is active
            InsertTextView(Index + 1,"","");
        }

//        <View
//        android:layout_width="match_parent"
//        android:layout_height="1dp"
//        android:id="@+id/hr"
//        android:layout_below="@+id/rvLblBody"
//        android:layout_marginBottom="8dp"
//        android:layout_marginTop="8dp"
//        android:background="@drawable/border_bottom" />
    }

    private TableLayout insertList(int Index,boolean isOrdered,String text){
        TableLayout _table=CreateTable();

        this._ParentView.addView(_table, Index);
        AddListItem(_table,isOrdered,text);
        clearActiveStyles();
        if(this._RenderType==RenderType.Editor) {

        }
        _table.setTag(CreateTag(isOrdered ? EditorType.ol : EditorType.ul));
        return _table;
    }

    private TableLayout CreateTable(){
        TableLayout _table = new TableLayout(_Context);
        _table.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return _table;
    }

    private View AddListItem(TableLayout layout,boolean isOrdered,String text){
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.tmpl_unordered_list_item, null);
        final CustomEditText _EditText= (CustomEditText) childLayout.findViewById(R.id.txtText);
        _EditText.setTextColor(_Resources.getColor(R.color.darkertext));
        _EditText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, _Resources.getDisplayMetrics()), 1.0f);
        _EditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        _EditText.setTag(CreateTag(EditorType.LI));
        childLayout.setTag(CreateTag(EditorType.LI));
        this.activeView= _EditText;
        if(isOrdered){
            final TextView _order= (TextView) childLayout.findViewById(R.id.lblOrder);
            _order.setText("1.");
        }

        if(!TextUtils.isEmpty(text)){
            setText(_EditText, text);
        }



        if(this._RenderType==RenderType.Editor) {
            _EditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activeView = v;
                 //   toggleToolbarProperties(v,null);
                }
            });

            _EditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if(_utilities.IsEditTextNull(_EditText)){
                            deleteFocusedPrevious(_EditText);
                        }
                    }
                    return false;
                }
            });

            _EditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0) {
//                    deleteFocusedPrevious(_EditText);
//                }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = Html.toHtml(_EditText.getText());
                    if (s.length() > 0) {
                        if (s.charAt(s.length() - 1) == '\n') {
                            text = text.replaceAll("<br>", "");
                            TableRow _row = (TableRow) _EditText.getParent();
                            TableLayout _table = (TableLayout) _row.getParent();
                            EditorType type = _utilities.GetControlType(_table);
                            if (s.length() == 0 || s.toString().equals("\n")) {
                                int index = _ParentView.indexOfChild(_table);
                                _table.removeView(_row);
                                InsertTextView(index + 1, "", "");
                            } else {
                                Spanned __ = Html.fromHtml(text);
                                CharSequence toReplace = noTrailingwhiteLines(__);
                                _EditText.setText(toReplace);
                                int index = _table.indexOfChild(_row);
                                //  InsertTextView(index + 1, "");
                                AddListItem(_table, type == EditorType.ol, "");
                            }

                        }
                    }
                }
            });
        }
        else{
            _EditText.setClickable(false);
            _EditText.setCursorVisible(false);
            _EditText.setEnabled(false);
        }

        layout.addView(childLayout);
        if (_EditText.requestFocus()) {
            _EditText.setSelection(_EditText.getText().length());
        }
        return childLayout;
    }
    public int determineIndex(EditorType type){

        int size= this._ParentView.getChildCount();
        if(this._RenderType==RenderType.ReadOnly)
            return size;
        View _view= this.activeView;
        if(_view==null)
            return size;
        int currentIndex= this._ParentView.indexOfChild(_view);
        EditorType tag = _utilities.GetControlType(_view);
        if(tag==EditorType.INPUT){
            int length=((EditText)this.activeView).getText().length();
            if(length>0){
              return type==EditorType.LI?currentIndex: currentIndex+1;
            }else{
                return currentIndex;
            }
        }else if (tag==EditorType.LI){
            EditText _text= (EditText) _view.findViewById(R.id.txtText);
            if(_text.getText().length()>0){

            }
            TableLayout layout= (TableLayout) _view.getParent();

         return size;
        }
        else{
            return size;
        }
    }
    private boolean isLastRow(View view){
        int index=this._ParentView.indexOfChild(view);
        int length= this._ParentView.getChildCount();
        return length-1==index;
    }

    public  boolean SaveState(String serialized){
        if(TextUtils.isEmpty(serialized)){
            serialized=serializeState(GetState());
        }
        objEngine.putValue("editorState",serialized);
        return true;
    }
    private String serializeState(EditorState _state){
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
            EditorType type= _utilities.GetControlType(_view);
            _state.type=type;
            _state.content=new ArrayList<>();
            switch (type){
                case INPUT:
                    EditText _text=(EditText)_view;
                    _state.content.add(Html.toHtml(_text.getText()));
                    _list.add(_state);
                    break;
                case img:
                    EditorControl _control = (EditorControl) _view.getTag();
                    _state.content.add(_control.Path);
                    _state.content.add(_control.UUID);
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
                    String text=null;
                    for (int j=0;j<_rowCount;j++){
                        View row= _table.getChildAt(j);
                       EditText _li= (EditText) row.findViewById(R.id.txtText);
                        _state.content.add(Html.toHtml(_li.getText()));
                    }
                    _list.add(_state);
                    break;
            }
        }
        _editorState.stateList=_list;
      return _editorState;
    }

    public void RenderEditor(EditorState _state) {
        this._ParentView.removeAllViews();
        List<state> _list = _state.stateList;
        for (state item:_list){
            switch (item.type){
                case INPUT:
                    String text= item.content.get(0);
                    InsertTextView(0, "", text);
                    break;
                case hr:
                    InsertDivider();
                    break;
                case img:
                    String path= item.content.get(0);
                    String UUID= item.content.get(1);
                    Bitmap _bitmap=objEngine.LoadImageFromInternalStorage(path,UUID);
                    loadImage(_bitmap,path,UUID,false);
                    break;
                case ul:

                   TableLayout _layout=null;
                    for(int i=0;i<item.content.size();i++){
                        if(i==0){
                           _layout= insertList(_list.indexOf(item),false,item.content.get(i));
                        }else {
                            AddListItem(_layout, false, item.content.get(i));
                        }
                    }
                    break;
                case ol:
                    TableLayout _layout2=  CreateTable();
                    for(int i=0;i<item.content.size();i++){
                        AddListItem(_layout2,true,item.content.get(i));
                    }
                    break;
            }
        }
    }

    public EditorState GetStateFromStorage(){
        String serialized= objEngine.GetValue("editorState", "");
        EditorState Deserialized= gson.fromJson(serialized, EditorState.class);
        return Deserialized;
    }
    public void RestoreState(){
        EditorState state=GetStateFromStorage();
        RenderEditor(state);
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }



    public void insertMap(String cords,boolean insertEditText) {
//        String image="http://maps.googleapis.com/maps/api/staticmap?center=43.137022,13.067162&zoom=16&size=600x400&maptype=roadmap&sensor=true&markers=color:blue|43.137022,13.067162";
        String[] x= cords.split(",");
        String lat = x[0];
        String lng = x[1];
        int[]size= _utilities.GetScreenDimension();
        int width=size[0];
        int height= size[1];
        StringBuilder builder = new StringBuilder();
        builder.append("http://maps.google.com/maps/api/staticmap?");
        builder.append("size="+String.valueOf(width)+"x400&zoom=15&sensor=true&markers="+ lat + "," + lng);
//        ImageView imageView = new ImageView(_Context);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);
//        params.bottomMargin=12;
//        imageView.setLayoutParams(params);
//        _ParentView.addView(imageView);
//        Picasso.with(this._Context).load(builder.toString()).into(imageView);

        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        Picasso.with(this._Context).load(builder.toString()).into(_ImageView);

        final FancyButton btn = (FancyButton) childLayout.findViewById(R.id.btn_remove);
        _ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setVisibility(View.VISIBLE);
            }
        });
        _ImageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                btn.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ParentView.removeView(childLayout);
            }
        });
        EditorControl _control = CreateTag(EditorType.map);
        childLayout.setTag(_control);
        int Index= determineIndex(EditorType.map);
        _ParentView.addView(childLayout, Index);
        if(insertEditText){
            InsertTextView(Index + 1,"","");
        }

    }

    public  void InsertUnorderedList(){
        View ActiveView= activeView;
        if(_utilities.GetControlType(ActiveView)==EditorType.LI){

            TableRow _row = (TableRow) ActiveView.getParent();
            TableLayout _table = (TableLayout) _row.getParent();
            //loop through each child of the ul and convert them into normal edittext
            for(int i= _table.indexOfChild(_row); i<_table.getChildCount();i++) {
                View _childRow=_table.getChildAt(i);
                _table.removeView(_childRow);
                EditText _EditText = (EditText)_childRow.findViewById(R.id.txtText);
                String text = Html.toHtml(_EditText.getText());
                int index = _ParentView.indexOfChild(_table);
                InsertTextView(index + 1, "", text);
            }
            clearActiveStyles();
        }else{
            int Index = determineIndex(EditorType.LI);
            //check if the active view has content
            View view= _ParentView.getChildAt(Index);
            if(view!=null) {
                EditorType type = _utilities.GetControlType(view); //if then, get the type of that view
                if(type==EditorType.INPUT){
                    String text= ((EditText)view).getText().toString();  //get the text, if not null, replace it with list item
                    _ParentView.removeView(view);
                    insertList(Index,false,text);
                }else{
                    insertList(Index,false,"");    //otherwise
                }
            }else{
            insertList(Index,false,"");
        }
        }
    }

    public void AttachHandlers() {
        Activity _activity = (Activity) _Context;
        _activity.findViewById(R.id.action_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     Intent intent=new Intent(_Context, MapsActivity.class);
            //    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //    ((Activity) _Context).startActivityForResult(intent,20);
                //insertMap("");
            }
        });
    }
    private class Utilities {
        private String trimLineEnding(String s) {
            if (s.charAt(s.length() - 1) == '\n') {
                String formatted = s.toString().substring(0, s.length() - 1);
                return formatted;
            }
            return  s;
        }
        private float PxtoSp(float px){
            float scaledDensity = _Context.getResources().getDisplayMetrics().scaledDensity;
            float sp = px / scaledDensity;
            return sp;
        }
        private int[] GetScreenDimension(){
            Display display =((Activity)_Context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            int[] dimen={width,height};
            return dimen;
        }

        private boolean IsEditTextNull(EditText _editText){
            return _editText.getText().toString().trim().length() == 0;
        }

        private boolean ContainsStyle(List<ControlStyles> _Styles, ControlStyles style){
            for (ControlStyles item:_Styles){
                if(item==style){
                    return true;
                }
                continue;
            }
            return  false;
        }
        private EditorControl UpdateTagStyle(EditorControl _controlTag, ControlStyles style,Op _op){
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


    }
}