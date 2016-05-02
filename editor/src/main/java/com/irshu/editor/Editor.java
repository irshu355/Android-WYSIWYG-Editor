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
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import com.irshu.editor.models.ControlStyles;
import com.irshu.editor.models.EditorState;
import com.irshu.editor.models.RenderType;
import com.irshu.editor.models.state;
import java.util.List;
public class Editor extends BaseClass {
    public Editor(Context _context, LinearLayout parentView, RenderType renderType, String placeholder) {
      super(_context, parentView, renderType, placeholder);
    }
    public void StartEditor(){
        inputExtensions.InsertEditText(0, this.PlaceHolder, "");
    }
    public void OpenImageGallery(){
       imageExtensions.OpenImageGallery();
    }
    public void InsertImage(Bitmap bitmap){
        imageExtensions.InsertImage(bitmap);
    }

    public void InsertMap(){
        mapExtensions.loadMapActivity();
    }

    public void InsertMap(String cords, boolean insertEditText){
        mapExtensions.insertMap(cords, insertEditText);
    }

    public void UpdateTextStyle(ControlStyles style){
        inputExtensions.UpdateTextStyle(style);
    }
    public void InsertLink(){
        inputExtensions.InsertLink();
    }
    public void InsertUnorderedList(){
        listItemExtensions.InsertUnorderedList();
    }

    public void InsertDivider(){
        dividerExtensions.InsertDivider();
    }


    public void RenderEditor(EditorState _state) {
        this._ParentView.removeAllViews();
        List<state> _list = _state.stateList;
        for (state item:_list){
            switch (item.type){
                case INPUT:
                    String text= item.content.get(0);
                    inputExtensions.InsertEditText(0, "", text);
                    break;
                case hr:
                    InsertDivider();
                    break;
                case img:
                    String path= item.content.get(0);
                    String UUID= item.content.get(1);
                    Bitmap _bitmap=objEngine.LoadImageFromInternalStorage(path,UUID);
                  //  loadImage(_bitmap,path,UUID,false);
                    break;
                case ul:

                   TableLayout _layout=null;
                    for(int i=0;i<item.content.size();i++){
                        if(i==0){
                           _layout= listItemExtensions.insertList(_list.indexOf(item),false,item.content.get(i));
                        }else {
                           listItemExtensions.AddListItem(_layout, false, item.content.get(i));
                        }
                    }
                    break;
                case ol:
                    TableLayout _layout2= listItemExtensions.CreateTable();
                    for(int i=0;i<item.content.size();i++){
                        listItemExtensions.AddListItem(_layout2, true, item.content.get(i));
                    }
                    break;
            }
        }
    }
    public void RestoreState(){
        EditorState state=GetStateFromStorage();
        RenderEditor(state);
    }




//    public void AttachHandlers() {
//        Activity _activity = (Activity) _Context;
//        _activity.findViewById(R.id.action_map).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//           //     Intent intent=new Intent(_Context, MapsActivity.class);
//            //    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //    ((Activity) _Context).startActivityForResult(intent,20);
//                //insertMap("");
//            }
//        });
//    }

}