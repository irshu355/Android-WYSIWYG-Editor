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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.irshulx.EditorCore;
import com.github.irshulx.R;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorType;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class ImageExtensions {
    private EditorCore editorCore;
    private int editorImageLayout=R.layout.tmpl_image_view;
    public ImageExtensions(EditorCore editorCore){
        this.editorCore = editorCore;
    }

    public void setEditorImageLayout(int drawable){
        this.editorImageLayout= drawable;
    }

    public void executeDownloadImageTask(String url, int index){
        new DownloadImageTask(index).execute(url);
    }


    public void OpenImageGallery() {
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    // Always show the chooser (if there are multiple options available)
        ((Activity) editorCore.getContext()).startActivityForResult(Intent.createChooser(intent, "Select an image"), editorCore.PICK_IMAGE_REQUEST);
    }

    public void InsertImage(Bitmap _image,int Index) {
       // Render(getStateFromString());
        final View childLayout = ((Activity) editorCore.getContext()).getLayoutInflater().inflate(this.editorImageLayout, null);
        ImageView imageView = (ImageView) childLayout.findViewById(R.id.imageView);
        final TextView lblStatus= (TextView) childLayout.findViewById(R.id.lblStatus);
        imageView.setImageBitmap(_image);
        final String uuid= GenerateUUID();
        BindEvents(childLayout);
        if(Index==-1) {
             Index = editorCore.determineIndex(EditorType.img);
        }
        editorCore.getParentView().addView(childLayout, Index);
        //      _Views.add(childLayout);
        if(editorCore.isLastRow(childLayout)) {
            editorCore.getInputExtensions().insertEditText(Index + 1, null, null);
        }
        EditorControl control= editorCore.CreateTag(EditorType.img);
        control.Path=uuid; // set the imageId,so we can recognize later after upload
        childLayout.setTag(control);
        childLayout.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        lblStatus.setVisibility(View.VISIBLE);
        editorCore.getEditorListener().onUpload(_image,uuid);
    }
    public String GenerateUUID(){
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String sdt = df.format(new Date(System.currentTimeMillis()));
        UUID x= UUID.randomUUID();
        String[] y= x.toString().split("-");
        return y[y.length-1]+sdt;
    }

    /*
      /used by the renderer to render the image from the state
    */
    public  void loadImage(String _path){
        ImageView imageView = new ImageView(this.editorCore.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 25, 0, 30);
        imageView.setLayoutParams(params);

        Picasso.with(this.editorCore.getContext()).load(_path).into(imageView);
        editorCore.getParentView().addView(imageView);
    }


    public View findImageById(String imageId){
        for(int i=0;i<editorCore.getParentChildCount();i++){
            View view = editorCore.getParentView().getChildAt(i);
            EditorControl control = editorCore.GetControlTag(view);
            if(!TextUtils.isEmpty(control.Path)&&control.Path.equals(imageId))
                return view;
        }
        return null;
    }

    public void onPostUpload(String url, String imageId) {
        View view=findImageById(imageId);
        final TextView lblStatus = (TextView) view.findViewById(R.id.lblStatus);
        lblStatus.setText(!TextUtils.isEmpty(url)?"Upload complete":"Upload failed");
        if(!TextUtils.isEmpty(url)) {
            EditorControl control = editorCore.CreateTag(EditorType.img);
            control.Path = url;
            view.setTag(control);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    ((Activity) editorCore.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on th UI thread, therefore is safe to modify UI elements.
                            lblStatus.setVisibility(View.GONE);
                        }
                    });
                }
            };
            new java.util.Timer().schedule(timerTask, 3000);
        }
        view.findViewById(R.id.progress).setVisibility(View.GONE);
    }

    /*
      /used to fetch an image from internet and return a Bitmap equivalent
    */
   private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private int InsertIndex;
        public DownloadImageTask(int index) {
            this.InsertIndex=index;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            InsertImage(result,this.InsertIndex);
        }
    }



    private void BindEvents(final View layout){
        final ImageView imageView= (ImageView) layout.findViewById(R.id.imageView);
        final View btn_remove=layout.findViewById(R.id.btn_remove);

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorCore.getParentView().removeView(layout);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imageView.setColorFilter(Color.argb(50, 0, 0, 0));
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    imageView.setColorFilter(Color.argb(0, 0, 0, 0));
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                        imageView.setColorFilter(Color.argb(0, 0, 0, 0));
                    }
                }
                return false;
            }
        });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_remove.setVisibility(View.VISIBLE);
                }
            });
            imageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    btn_remove.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                }
            });
    }
}