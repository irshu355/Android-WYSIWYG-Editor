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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.github.irshulx.EditorComponent;
import com.github.irshulx.R;
import com.github.irshulx.EditorCore;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.Node;
import com.github.irshulx.models.RenderType;

import org.jsoup.nodes.Element;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class DividerExtensions extends EditorComponent {
    private int dividerLayout = R.layout.tmpl_divider_layout;
    EditorCore editorCore;

    @Override
    public Node getContent(View view) {
        Node node = this.getNodeInstance(view);
        return node;
    }

    @Override
    public String getContentAsHTML(Node node, EditorContent content) {
       return componentsWrapper.getHtmlExtensions().getTemplateHtml(EditorType.hr);
    }

    @Override
    public void renderEditorFromState(Node node, EditorContent content) {
        insertDivider(content.nodes.indexOf(node));
    }

    @Override
    public Node buildNodeFromHTML(Element element) {
        int count = editorCore.getChildCount();
        insertDivider(count);
        return null;
    }

    @Override
    public void init(ComponentsWrapper componentsWrapper) {
        this.componentsWrapper = componentsWrapper;
    }


    public DividerExtensions(EditorCore editorCore) {
        super(editorCore);
        this.editorCore = editorCore;
    }

    public void setDividerLayout(int layout) {
        this.dividerLayout = layout;
    }

    public void insertDivider(int index) {
        View view = ((Activity) editorCore.getContext()).getLayoutInflater().inflate(this.dividerLayout, null);
        view.setTag(editorCore.createTag(EditorType.hr));
        if (index == -1) {
            index = editorCore.determineIndex(EditorType.hr);
        }
        if (index == 0) {
            Toast.makeText(editorCore.getContext(), "divider cannot be inserted on line zero", Toast.LENGTH_SHORT).show();
            return;
        }
        editorCore.getParentView().addView(view, index);

        if (editorCore.getRenderType() == RenderType.Editor) {

            if (editorCore.getControlType(editorCore.getParentView().getChildAt(index + 1 )) == EditorType.INPUT) {
                CustomEditText customEditText = (CustomEditText) editorCore.getChildAt(index + 1 );
                componentsWrapper.getInputExtensions().removeFocus(customEditText);
            }
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        int paddingTop = view.getPaddingTop();
                        int paddingBottom = view.getPaddingBottom();
                        int height = view.getHeight();
                        if (event.getY() < paddingTop) {
                            editorCore.___onViewTouched(0, editorCore.getParentView().indexOfChild(view));
                        } else if (event.getY() > height - paddingBottom) {
                            editorCore.___onViewTouched(1, editorCore.getParentView().indexOfChild(view));
                        }
                        return false;
                    }
                    return true;
                }
            });
        }
    }

    public boolean deleteHr(int indexOfDeleteItem) {
        View view = editorCore.getParentView().getChildAt(indexOfDeleteItem);
        if (view == null ||editorCore.getControlType(view) == EditorType.hr) {
            editorCore.getParentView().removeView(view);
            return true;
        }
        return false;
    }

    public void removeAllDividersBetweenDeletedAndFocusNext(int indexOfDeleteItem, int nextFocusIndex) {
        for(int i = nextFocusIndex; i <indexOfDeleteItem;i++){
            if (editorCore.getControlType(editorCore.getParentView().getChildAt(i)) == EditorType.hr){
                editorCore.getParentView().removeViewAt(i);
            }
        }
    }
}