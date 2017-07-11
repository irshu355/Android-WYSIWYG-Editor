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
import android.view.View;
import android.widget.Toast;

import com.github.irshulx.R;
import com.github.irshulx.EditorCore;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.RenderType;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class DividerExtensions {
    private int dividerLayout = R.layout.tmpl_divider_layout;
    EditorCore editorCore;

    public DividerExtensions(EditorCore editorCore) {
        this.editorCore = editorCore;
    }

    public void setDividerLayout(int layout) {
        this.dividerLayout = layout;
    }

    public void insertDivider() {
        View view = ((Activity) editorCore.getContext()).getLayoutInflater().inflate(this.dividerLayout, null);
        view.setTag(editorCore.createTag(EditorType.hr));
        int index = editorCore.determineIndex(EditorType.hr);
        if (index == 0) {
            Toast.makeText(editorCore.getContext(), "divider cannot be inserted on line zero", Toast.LENGTH_SHORT).show();
            return;
        }
        editorCore.getParentView().addView(view, index);
        if (editorCore.isLastRow(view) && editorCore.getRenderType() == RenderType.Editor) {
            //check if ul is active
            editorCore.getInputExtensions().insertEditText(index + 1, null, null);
        } else if (editorCore.getRenderType() == RenderType.Editor) {
//            editorCore.getParentView().removeViewAt(index+1);
//            /**
//             * set focus to the next nearby edittext
//             */
//            setFocusToNearbyEditText(index+1);
        }
    }

    public boolean deleteHr(int indexOfDeleteItem) {
        View view = editorCore.getParentView().getChildAt(indexOfDeleteItem);
//        if(view == null)
//            return true;
        if (view == null ||editorCore.getControlType(view) == EditorType.hr) {
            editorCore.getParentView().removeView(view);
            return true;
        }
        return false;
    }
}