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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.irshulx.R;
import com.github.irshulx.BaseClass;
import com.github.irshulx.models.EditorType;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class DividerExtensions {
    private Context context;
    private int dividerBackground= R.drawable.divider_background_light;
    BaseClass base;
    public DividerExtensions(BaseClass baseClass, Context context){
        this.base = baseClass;
        this.context = context;
    }

    public void setDividerBackground(int drawable){
        this.dividerBackground=drawable;
    }

    public void InsertDivider(){

        View view=new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 3);
        params.setMargins(10, 10, 10, 10);
        view.setLayoutParams(params);
        view.setBackgroundDrawable(context.getResources().getDrawable(dividerBackground));
        view.setTag(base.CreateTag(EditorType.hr));
        int Index= base.determineIndex(EditorType.hr);
        base.getParentView().addView(view, Index);
        if(base.isLastRow(view)) {
            //check if ul is active
           base.getInputExtensions().InsertEditText(Index + 1, null, null);
        }
    }
    public void deleteHr(int indexOfDeleteItem) {
        View view= base.getParentView().getChildAt(indexOfDeleteItem);
        if(base.GetControlType(view)==EditorType.hr){
            base.getParentView().removeView(view);
        }
    }


}
