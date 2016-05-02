package com.irshu.editor.Components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.irshu.editor.BaseClass;
import com.irshu.editor.MapsActivity;
import com.irshu.editor.R;
import com.irshu.editor.models.EditorControl;
import com.irshu.editor.models.EditorType;
import com.squareup.picasso.Picasso;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class MapExtensions {
    private Context _Context;
    BaseClass _Base;
    public MapExtensions(BaseClass baseClass){
        this._Base= baseClass;
        this._Context= baseClass._Context;
    }

    public void insertMap(String cords,boolean insertEditText) {
//        String image="http://maps.googleapis.com/maps/api/staticmap?center=43.137022,13.067162&zoom=16&size=600x400&maptype=roadmap&sensor=true&markers=color:blue|43.137022,13.067162";
        String[] x= cords.split(",");
        String lat = x[0];
        String lng = x[1];
        int[]size= _Base.utilitiles.GetScreenDimension();
        int width=size[0];
        StringBuilder builder = new StringBuilder();
        builder.append("http://maps.google.com/maps/api/staticmap?");
        builder.append("size="+String.valueOf(width)+"x400&zoom=15&sensor=true&markers="+ lat + "," + lng);
//        ImageView imageView = new ImageView(_Context);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);
//        params.bottomMargin=12;
//        imageView.setLayoutParams(params);
//        _ParentView.addView(imageView);
//        Picasso.with(this._Context).load(builder.toString()).into(imageView);

        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        Picasso.with(this._Context).load(builder.toString()).into(_ImageView);

        final View btn =  childLayout.findViewById(R.id.btn_remove);
        _ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setVisibility(View.VISIBLE);
            }
        });
        _ImageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                btn.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _Base._ParentView.removeView(childLayout);
            }
        });
        EditorControl _control = _Base.CreateTag(EditorType.map);
        _control.Cords= cords;
        childLayout.setTag(_control);
        int Index= _Base.determineIndex(EditorType.map);
        _Base._ParentView.addView(childLayout, Index);
        if(insertEditText){
            if(_Base.GetChildCount()==2){
                insertEditText=false;
            }
        }
        if(insertEditText){
          _Base.inputExtensions.InsertEditText(Index + 1, "", "");
        }
    }

    public void loadMapActivity(){
                Intent intent=new Intent(_Context, MapsActivity.class);
                ((Activity) _Context).startActivityForResult(intent, _Base.MAP_MARKER_REQUEST);
    }

}
