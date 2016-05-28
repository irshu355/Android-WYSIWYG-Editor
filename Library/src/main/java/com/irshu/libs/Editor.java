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
    package com.irshu.libs;
    import android.content.Context;
    import android.graphics.Bitmap;
    import android.util.AttributeSet;

    import com.irshu.libs.models.ControlStyles;
    import com.irshu.libs.models.EditorState;
    import com.irshu.libs.models.RenderType;

    public class Editor extends BaseClass {
        public Editor(Context context, AttributeSet attrs) {
            super(context, attrs);
            super.setEditorListener(null);
            //  initialize(context,parentView,renderType,_PlaceHolderText);
            if (getRenderType() == RenderType.Editor) {
                getInputExtensions().InsertEditText(0, this.PlaceHolder, "");
            }
        }

        public void setEditorListener(EditorListener _listener){
            super.setEditorListener(_listener);
        }
        public void StartEditor(){
        }

        public EditorState getContent(){
            return super.getContent();
        }

        public String getContentAsSerialized(){
            return super.getContentAsSerialized();
        }

        public String getContentAsSerialized(EditorState state){
            return super.getContentAsSerialized(state);
        }

        public void InsertImage(Bitmap bitmap){
            getImageExtensions().InsertImage(bitmap);
        }
        public void InsertMap(){
            getMapExtensions().loadMapActivity();
        }
        public void InsertMap(String Cords, boolean InsertEditText){
            getMapExtensions().insertMap(Cords, InsertEditText);
        }
        public void InsertList(boolean isOrdered){
            getListItemExtensions().Insertlist(isOrdered);
        }
        public void InsertDivider(){
            getDividerExtensions().InsertDivider();
        }
        public void UpdateTextStyle(ControlStyles style){
            getInputExtensions().UpdateTextStyle(style, null);
        }
        public void InsertLink() {
            getInputExtensions().InsertLink();
        }

        public void OpenImagePicker() {
            getImageExtensions().OpenImageGallery();
        }
        public void RenderEditor(EditorState _state) {
            super.RenderEditor(_state);
        }
        public void RestoreState(){
            EditorState state= getStateFromString(null);
            RenderEditor(state);
        }

        public void RenderEditor(String HtmlString){
            RenderEditorFromHtml(HtmlString);
        }
    }