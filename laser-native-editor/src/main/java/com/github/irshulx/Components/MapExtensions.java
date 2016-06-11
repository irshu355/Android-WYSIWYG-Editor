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
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.github.irshulx.BaseClass;
import com.github.irshulx.MapsActivity;
import com.github.irshulx.R;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorType;
import com.squareup.picasso.Picasso;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class MapExtensions {
    private Context context;
    BaseClass base;
    private int mapExtensionTemplate=R.layout.editor_image_view;

    public MapExtensions(BaseClass baseClass, Context context){
        this.base = baseClass;
        this.context = context;
    }

    public void setMapViewTemplate(int drawable)
    {
        this.mapExtensionTemplate= drawable;
    }

    public void insertMap(String cords,boolean insertEditText) {
//        String image="http://maps.googleapis.com/maps/api/staticmap?center=43.137022,13.067162&zoom=16&size=600x400&maptype=roadmap&sensor=true&markers=color:blue|43.137022,13.067162";
        String[] x= cords.split(",");
        String lat = x[0];
        String lng = x[1];
        int[]size= base.getUtilitiles().GetScreenDimension();
        int width=size[0];
        StringBuilder builder = new StringBuilder();
        builder.append("http://maps.google.com/maps/api/staticmap?");
        builder.append("size="+String.valueOf(width)+"x400&zoom=15&sensor=true&markers="+ lat + "," + lng);
//        ImageView imageView = new ImageView(context);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);
//        params.bottomMargin=12;
//        imageView.setLayoutParams(params);
//        parentView.addView(imageView);
//        Picasso.with(this.context).load(builder.toString()).into(imageView);

        final View childLayout = ((Activity) context).getLayoutInflater().inflate(this.mapExtensionTemplate, null);
        ImageView imageView = (ImageView) childLayout.findViewById(R.id.imageView);
        Picasso.with(this.context).load(builder.toString()).into(imageView);

        final View btn =  childLayout.findViewById(R.id.btn_remove);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setVisibility(View.VISIBLE);
            }
        });
        imageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                btn.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base.getParentView().removeView(childLayout);
            }
        });
        EditorControl control = base.CreateTag(EditorType.map);
        control.Cords= cords;
        childLayout.setTag(control);
        int Index= base.determineIndex(EditorType.map);
        base.getParentView().addView(childLayout, Index);
        if(insertEditText){
          base.getInputExtensions().InsertEditText(Index + 1, null, null);
        }
    }

    public void loadMapActivity(){
                Intent intent=new Intent(context, MapsActivity.class);
                ((Activity) context).startActivityForResult(intent, base.MAP_MARKER_REQUEST);
    }

}
