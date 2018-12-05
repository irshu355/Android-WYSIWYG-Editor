package com.github.irshulx;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;

import com.github.irshulx.models.RenderType;
import com.google.gson.Gson;

public class EditorSettings {
     String placeHolder = null;
     boolean autoFocus = true;
     boolean serialRenderInProgress = false;
     Context context;
     LinearLayout parentView;
     RenderType renderType;
     Resources resources;
     View activeView;
     Gson gson;
     boolean stateFresh;

     public EditorSettings(Context context){
         this.context = context;
         this.stateFresh = true;
         this.resources = context.getResources();
         gson = new Gson();
         this.parentView = null;
     }

    public static EditorSettings init(Context context, EditorCore editorCore) {
       EditorSettings editorSettings = new EditorSettings(context);
       editorSettings.parentView = editorCore;
       return editorSettings;
    }
}