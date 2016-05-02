package com.irshu.editor.Components;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.irshu.editor.BaseClass;
import com.irshu.editor.R;
import com.irshu.editor.models.EditorType;
import com.irshu.editor.models.RenderType;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class ListItemExtensions {
    private Context _Context;
    BaseClass _Base;
    public ListItemExtensions(BaseClass baseClass){
        this._Base= baseClass;
        this._Context= baseClass._Context;
    }

    public TableLayout insertList(int Index, boolean isOrdered, String text){
        TableLayout _table=CreateTable();

        _Base._ParentView.addView(_table, Index);
        AddListItem(_table,isOrdered,text);
        if(_Base._RenderType== RenderType.Editor) {

        }
        _table.setTag(_Base.CreateTag(isOrdered ? EditorType.ol : EditorType.ul));
        return _table;
    }

    public TableLayout CreateTable(){
        TableLayout _table = new TableLayout(_Context);
        _table.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return _table;
    }

    public View AddListItem(TableLayout layout, boolean isOrdered, String text){
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.tmpl_unordered_list_item, null);
        final CustomEditText _EditText= (CustomEditText) childLayout.findViewById(R.id.txtText);
        _EditText.setTextColor(_Base._Resources.getColor(R.color.darkertext));
        _EditText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, _Base._Resources.getDisplayMetrics()), 1.0f);
        _EditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        _EditText.setTag(_Base.CreateTag(EditorType.LI));
        childLayout.setTag(_Base.CreateTag(EditorType.LI));
        _Base.activeView= _EditText;
        if(isOrdered){
            final TextView _order= (TextView) childLayout.findViewById(R.id.lblOrder);
            _order.setText("1.");
        }

        if(!TextUtils.isEmpty(text)){
            _Base.inputExtensions.setText(_EditText, text);
        }



        if(_Base._RenderType==RenderType.Editor) {
            _EditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _Base.activeView = v;
                    //   toggleToolbarProperties(v,null);
                }
            });

            _EditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if(_Base.inputExtensions.IsEditTextNull(_EditText)){
                            _Base.deleteFocusedPrevious(_EditText);
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
                            EditorType type = _Base.GetControlType(_table);
                            if (s.length() == 0 || s.toString().equals("\n")) {
                                int index = _Base._ParentView.indexOfChild(_table);
                                _table.removeView(_row);
                                _Base.inputExtensions.InsertEditText(index + 1, "", "");
                            } else {
                                Spanned __ = Html.fromHtml(text);
                                CharSequence toReplace =_Base.inputExtensions.noTrailingwhiteLines(__);
                                _EditText.setText(toReplace);
                                int index = _table.indexOfChild(_row);
                                //  InsertEditText(index + 1, "");
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

    public void InsertUnorderedList(){
        View ActiveView= _Base.activeView;
        if(_Base.GetControlType(ActiveView)==EditorType.LI){

            TableRow _row = (TableRow) ActiveView.getParent();
            TableLayout _table = (TableLayout) _row.getParent();
            //loop through each child of the ul and convert them into normal edittext
            for(int i= _table.indexOfChild(_row); i<_table.getChildCount();i++) {
                View _childRow=_table.getChildAt(i);
                _table.removeView(_childRow);
                EditText _EditText = (EditText)_childRow.findViewById(R.id.txtText);
                String text = Html.toHtml(_EditText.getText());
                int index = _Base._ParentView.indexOfChild(_table);
                _Base.inputExtensions.InsertEditText(index + 1, "", text);
            }
        }else{
            int Index = _Base.determineIndex(EditorType.LI);
            //check if the active view has content
            View view= _Base._ParentView.getChildAt(Index);
            if(view!=null) {
                EditorType type =_Base.GetControlType(view); //if then, get the type of that view
                if(type==EditorType.INPUT){
                    String text= ((EditText)view).getText().toString();  //get the text, if not null, replace it with list item
                    _Base._ParentView.removeView(view);
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
