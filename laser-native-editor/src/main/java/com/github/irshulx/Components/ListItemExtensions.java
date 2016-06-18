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

package com.github.irshulx.Components;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.github.irshulx.BaseClass;
import com.github.irshulx.R;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.RenderType;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class ListItemExtensions {
    private Context context;
    BaseClass base;
    private int listItemTemplate=R.layout.tmpl_unordered_list_item;

    public ListItemExtensions(BaseClass baseClass, Context context){
        this.base = baseClass;
        this.context =context;
    }

    public void setListItemTemplate(int drawable){
        this.listItemTemplate=drawable;
    }

    public TableLayout insertList(int Index, boolean isOrdered, String text){

        TableLayout table=CreateTable();
        base.getParentView().addView(table, Index);
        table.setTag(base.CreateTag(isOrdered ? EditorType.ol : EditorType.ul));
        AddListItem(table, isOrdered, text);
        return table;
    }

    public TableLayout CreateTable(){
        TableLayout table = new TableLayout(base.getContext());
        table.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        table.setPadding(30, 10, 10, 10);
        return table;
    }



    public View AddListItem(TableLayout layout, boolean isOrdered, String text){
        final View childLayout = ((Activity) base.getContext()).getLayoutInflater().inflate(this.listItemTemplate, null);
        final CustomEditText editText= (CustomEditText) childLayout.findViewById(R.id.txtText);
        editText.setTypeface(Typeface.create(base.getInputExtensions().getFontFace(),Typeface.NORMAL));
               if(isOrdered){
            final TextView _order= (TextView) childLayout.findViewById(R.id.lblOrder);
            int count= layout.getChildCount();
            _order.setText(String.valueOf(count+1)+".");
        }
         if(base.getRenderType() ==RenderType.Editor) {
            editText.setTextColor(base.getResources().getColor(R.color.darkertext));
            editText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, base.getInputExtensions().getLineSpacing(), base.getResources().getDisplayMetrics()), 1.0f);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, base.getInputExtensions().getNormtalTextSize());
            editText.setTag(base.CreateTag(isOrdered ? EditorType.OL_LI : EditorType.UL_LI));
            childLayout.setTag(base.CreateTag(isOrdered ? EditorType.OL_LI : EditorType.UL_LI));
            base.setActiveView(editText);
            base.getInputExtensions().setText(editText, text);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    base.setActiveView(v);
                    //   toggleToolbarProperties(v,null);
                }
            });
             editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                 @Override
                 public void onFocusChange(View v, boolean hasFocus) {
                     if (hasFocus) {
                         base.setActiveView(v);
                     }
                 }
             });
            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (base.getInputExtensions().IsEditTextNull(editText)) {
                            base.deleteFocusedPrevious(editText);
                        }
                    }
                    return false;
                }
            });

            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //                if (s.length() == 0) {
                    //                    deleteFocusedPrevious(editText);
                    //                }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = Html.toHtml(editText.getText());
                    if (s.length() > 0) {
                        if (s.charAt(s.length() - 1) == '\n') {
                            text = text.replaceAll("<br>", "");
                            TableRow _row = (TableRow) editText.getParent();
                            TableLayout _table = (TableLayout) _row.getParent();
                            EditorType type = base.GetControlType(_table);
                            if (s.length() == 0 || s.toString().equals("\n")) {
                                int index = base.getParentView().indexOfChild(_table);
                                _table.removeView(_row);
                                base.getInputExtensions().InsertEditText(index + 1, "", "");
                            } else {
                                Spanned __ = Html.fromHtml(text);
                                CharSequence toReplace = base.getInputExtensions().noTrailingwhiteLines(__);

                                if (toReplace.length() > 0) {
                                    editText.setText(toReplace);
                                } else {
                                    editText.getText().clear();
                                }


                                int index = _table.indexOfChild(_row);
                                //  InsertEditText(index + 1, "");
                                AddListItem(_table, type == EditorType.ol, "");
                            }

                        }
                    }
                }
            });

             final Handler handler = new Handler();
             handler.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     editText.requestFocus();
                     InputMethodManager mgr = (InputMethodManager) base.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                     mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                     editText.setSelection(editText.getText().length());
                 }
             },0);
        }
        else{
            final TextView textView= (TextView) childLayout.findViewById(R.id.lblText);
             textView.setTypeface(Typeface.create(base.getInputExtensions().getFontFace(),Typeface.NORMAL));
            /*
            It's a renderer, so instead of EditText,render TextView
             */
            if(!TextUtils.isEmpty(text)){
              base.getInputExtensions().setText(textView, text);
            }
             textView.setTextColor(base.getResources().getColor(R.color.darkertext));
             textView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, base.getInputExtensions().getLineSpacing(), base.getResources().getDisplayMetrics()), 1.0f);
             textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
             textView.setVisibility(View.VISIBLE);
             editText.setVisibility(View.GONE);
        }
        layout.addView(childLayout);
        return childLayout;
    }

    public void ConvertListToNormalText(TableLayout _table,int startIndex){
        int tableChildCount=_table.getChildCount();
        for(int i=startIndex;i<tableChildCount;i++) {
            View _childRow = _table.getChildAt(i);
            _table.removeView(_childRow);
            String text = getTextFromListItem(_childRow);
            int Index= base.getParentView().indexOfChild(_table);
            base.getInputExtensions().InsertEditText(Index+1,"",text);
            i -= 1;
            tableChildCount-=1;
        }
        //if item is the last in the table, remove the table from parent

        if(_table.getChildCount()==0){
            base.getParentView().removeView(_table);
        }
    }

    public void ConvertListToOrdered(TableLayout _table){
        EditorControl type= base.CreateTag(EditorType.ol);
        _table.setTag(type);
        for(int i=0;i<_table.getChildCount();i++){
            View _childRow = _table.getChildAt(i);
            CustomEditText editText = (CustomEditText) _childRow.findViewById(R.id.txtText);
            editText.setTag(base.CreateTag(EditorType.OL_LI));
            _childRow.setTag(base.CreateTag(EditorType.OL_LI));
            TextView _bullet= (TextView) _childRow.findViewById(R.id.lblOrder);
            _bullet.setText(String.valueOf(i + 1) + ".");
        }
    }


    public void ConvertListToUnordered(TableLayout _table){
        EditorControl type= base.CreateTag(EditorType.ul);
        _table.setTag(type);
        for(int i=0;i<_table.getChildCount();i++){
            View _childRow = _table.getChildAt(i);
            CustomEditText _EditText = (CustomEditText) _childRow.findViewById(R.id.txtText);
            _EditText.setTag(base.CreateTag(EditorType.UL_LI));
            _childRow.setTag(base.CreateTag(EditorType.UL_LI));
            TextView _bullet= (TextView) _childRow.findViewById(R.id.lblOrder);
            _bullet.setText("•");
        }
    }


    public String getTextFromListItem(View row){
        CustomEditText _text= (CustomEditText) row.findViewById(R.id.txtText);
       return _text.getText().toString();
    }

    public void Insertlist(boolean isOrdered){
        View ActiveView= base.getActiveView();
        EditorType currentFocus= base.GetControlType(ActiveView);
        if(currentFocus==EditorType.UL_LI&&!isOrdered) {
                 /* this means, current focus is on n unordered list item, since user clicked
                 on unordered list icon, loop through the parents childs and convert each list item into normal edittext
                 *
                 */
            TableRow _row = (TableRow) ActiveView.getParent();
            TableLayout _table = (TableLayout) _row.getParent();
            ConvertListToNormalText(_table,_table.indexOfChild(_row));
                    /* this means, current focus is on n unordered list item, since user clicked
                 on unordered list icon, loop through the parents childs and convert each list item into normal edittext
                 *
                 */

        }else if(currentFocus==EditorType.UL_LI&&isOrdered){

                                    /*
                    * user clicked on ordered list item. since it's an unordered list, you need to loop through each and convert each
                    * item into an ordered list.
                    * */
            TableRow _row = (TableRow) ActiveView.getParent();
            TableLayout _table = (TableLayout) _row.getParent();
            ConvertListToOrdered(_table);
                                 /*
                    * user clicked on ordered list item. since it's an unordered list, you need to loop through each and convert each
                    * item into an ordered list.
                    * */
        }
        else if(currentFocus==EditorType.OL_LI&&isOrdered) {
                /*
                *
                * this means the item was an ordered list, you need to convert the item into a normal EditText
                *
                * */
            TableRow _row = (TableRow) ActiveView.getParent();
            TableLayout _table = (TableLayout) _row.getParent();
            ConvertListToNormalText(_table,_table.indexOfChild(_row));
                /*
                *
                * this means the item was an ordered list, you need to convert the item into a normal EditText
                *
                * */
        }
        else if(currentFocus==EditorType.OL_LI&&!isOrdered){
                 /*
                *
                * this means the item was an ordered list, you need to convert the item into an unordered list
                *
                * */

            TableRow _row = (TableRow) ActiveView.getParent();
            TableLayout _table = (TableLayout) _row.getParent();
            ConvertListToUnordered(_table);
                  /*
                *
                * this means the item was an ordered list, you need to convert the item into an unordered list
                *
                * */
        }
        else if(isOrdered){
                 /*
                *
                * it's a normal edit text, convert it into an ordered list. but first check index-1, if it's ordered, should follow the order no.
                * if it's unordered, convert all of em to ordered.
                *
                * */
            int index_of_activeView= base.getParentView().indexOfChild(base.getActiveView());
            int Index = base.determineIndex(EditorType.OL_LI);
            //check if the active view has content
            View view= base.getParentView().getChildAt(Index);
            if(view!=null) {
                EditorType type = base.GetControlType(view); //if then, get the type of that view, this behaviour is so, if that line has text,
                // it needs to be converted to list item
                if(type==EditorType.INPUT){
                    String text= ((CustomEditText)view).getText().toString();  //get the text, if not null, replace it with list item
                    base.getParentView().removeView(view);

                    if(Index==0) {
                        insertList(Index, isOrdered, text);
                    }else if(base.GetControlType(base.getParentView().getChildAt(index_of_activeView - 1))==EditorType.ol){
                        TableLayout _table= (TableLayout) base.getParentView().getChildAt(index_of_activeView - 1);
                        AddListItem(_table, isOrdered, text);
                    }else{
                        insertList(Index,isOrdered,text);
                    }
                }else{
                    insertList(Index,isOrdered,"");    //otherwise
                }
            }else{
                insertList(Index,isOrdered,"");
            }


        }else{
                 /*
                *
                * it's a normal edit text, convert it into an un-ordered list
                *
                * */

            int Index = base.determineIndex(EditorType.UL_LI);
            //check if the active view has content
            View view= base.getParentView().getChildAt(Index);
            if(view!=null) {
                EditorType type = base.GetControlType(view); //if then, get the type of that view, this behaviour is so, if that line has text,
                // it needs to be converted to list item
                if(type==EditorType.INPUT){
                    String text= ((EditText)view).getText().toString();  //get the text, if not null, replace it with list item
                    base.getParentView().removeView(view);
                    insertList(Index,false,text);
                }else{
                    insertList(Index,false,"");    //otherwise
                }
            }else{
                insertList(Index,false,"");
            }
        }

    }
}
