package com.irshu.editor.Components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.irshu.editor.BaseClass;
import com.irshu.editor.R;
import com.irshu.editor.models.EditorType;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class DividerExtensions {
    private Context _Context;
    BaseClass _Base;
    public DividerExtensions(BaseClass baseClass){
        this._Base= baseClass;
        this._Context= baseClass._Context;
    }

    public void InsertDivider(){

        View view=new View(_Context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 3);
        params.setMargins(10, 10, 10, 10);
        view.setLayoutParams(params);
        view.setBackgroundDrawable(_Context.getResources().getDrawable(R.drawable.border_bottom));
        view.setTag(_Base.CreateTag(EditorType.hr));
        int Index=_Base.determineIndex(EditorType.hr);
        _Base._ParentView.addView(view, Index);
        if(_Base.isLastRow(view)) {
            //check if ul is active
           _Base.inputExtensions.InsertEditText(Index + 1, "", "");
        }
    }
    public void deleteHr(int indexOfDeleteItem) {
        View view= _Base._ParentView.getChildAt(indexOfDeleteItem);
        if(_Base.GetControlType(view)==EditorType.hr){
            _Base._ParentView.removeView(view);
        }
    }


}
