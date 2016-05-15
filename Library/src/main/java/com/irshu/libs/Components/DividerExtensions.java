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
package com.irshu.libs.Components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.irshu.libs.BaseClass;
import com.irshu.libs.models.EditorType;

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
        view.setBackgroundDrawable(_Context.getResources().getDrawable(_Base.dividerBackground));
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
