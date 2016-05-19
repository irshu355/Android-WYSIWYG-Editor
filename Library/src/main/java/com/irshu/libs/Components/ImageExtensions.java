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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.irshu.libs.BaseClass;
import com.irshu.editor.R;
import com.irshu.libs.models.EditorControl;
import com.irshu.libs.models.EditorState;
import com.irshu.libs.models.EditorType;
import com.irshu.libs.models.IEditorRetrofitApi;
import com.irshu.libs.models.RenderType;
import com.irshu.libs.models.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class ImageExtensions {
    private Context _Context;
    private BaseClass _Base;
    public ImageExtensions(BaseClass baseClass){
        this._Context= baseClass._Context;
        this._Base= baseClass;
    }

    public void OpenImageGallery() {
        int Index=this._Base.determineIndex(EditorType.none);
        EditorState state= _Base.GetState();
        state.PendingIndex= Index;
        String serialized=this._Base.serializeState(state);
  //      this._Base.SaveState(serialized);

        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    // Always show the chooser (if there are multiple options available)
        ((Activity)_Context).startActivityForResult(Intent.createChooser(intent, "Select an image"), _Base.PICK_IMAGE_REQUEST);
    }

    public void InsertImage(Bitmap _image) {
       // RenderEditor(GetStateFromStorage());
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        _ImageView.setImageBitmap(_image);
        final String uuid= this._Base.objEngine.GenerateUUID();
        BindEvents(childLayout);
        EditorControl _control = new EditorControl();
        _control.Type = EditorType.img;
        _control.UUID= uuid;
        childLayout.setTag(_control);
        int Index= _Base.determineIndex(EditorType.img);
        _Base._ParentView.addView(childLayout, Index);
        //      _Views.add(childLayout);
        if(_Base.isLastRow(childLayout)) {
            _Base.inputExtensions.InsertEditText(Index + 1, "", "");
        }
        UploadImageToServer(_image,childLayout);
    }

    /*
      /used by the renderer to render the image from the state
    */
    public  void loadImage(Bitmap _image, String _path, String fileName, boolean insertEditText){
        if(this._Base._RenderType== RenderType.Editor){
            SetImageBitmap(_image, _path, fileName, insertEditText);
        }else{

        }
    }

    /*
      /used to fetch an image from internet and return a Bitmap equivalent
    */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    /*
    called from loadImage
     */
    private void SetImageBitmap(Bitmap _image, String _path, String fileName, boolean insertEditText) {
         /*
         / this function only used by Editor
         */
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        _ImageView.setImageBitmap(_image);
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
        EditorControl _control = _Base.CreateTag(EditorType.img);
        _control.UUID= fileName;
        _control.Path=_path;
        childLayout.setTag(_control);
        int Index=_Base. determineIndex(EditorType.img);
        _Base._ParentView.addView(childLayout, Index);
        if(insertEditText){
            _Base.inputExtensions.InsertEditText(Index + 1, "", "");
        }
    }
    private  void UploadImageToServer(Bitmap bitmap, final View view){
        File f = new File(_Context.getCacheDir(), "xxx");
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            TypedFile typedImage = new TypedFile("application/octet-stream", f);
            RestAdapter _Adapter = _Base.objEngine.InitRestAdapter();
            IEditorRetrofitApi _Api = _Adapter.create(IEditorRetrofitApi.class);
            _Api.uploadImage(typedImage, new Callback<Response>() {
                @Override
                public void success(Response response, retrofit.client.Response response2) {
                    EditorControl _control = (EditorControl) view.getTag();
                    _control.Path = response.Url;
                    ((TextView)view.findViewById(R.id.lblStatus)).setText("Uploaded");
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here
                                    view.findViewById(R.id.lblStatus).setVisibility(View.GONE);
                                }
                            },
                            2000
                    );
                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                }
                @Override
                public void failure(RetrofitError error) {
                    ((TextView)view.findViewById(R.id.lblStatus)).setText(error.getMessage());
                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void BindEvents(final View layout){
        final ImageView imageView= (ImageView) layout.findViewById(R.id.imageView);
        final View btnFitWidth=layout.findViewById(R.id.btnFitWidth);
        final View btnCenterCrop=layout.findViewById(R.id.btnCenterCrop);
        final View btn_remove=layout.findViewById(R.id.btn_remove);
        final View btnStretch=layout.findViewById(R.id.btnStretch);
        btnFitWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setScaleType(ImageView.ScaleType.FIT_END);
            }
        });
        btnCenterCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _Base._ParentView.removeView(layout);
            }
        });
        btnStretch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
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
                    btnCenterCrop.setVisibility(View.VISIBLE);
                    btnFitWidth.setVisibility(View.VISIBLE);
                    btnStretch.setVisibility(View.VISIBLE);
                }
            });
            imageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    btn_remove.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                    btnCenterCrop.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                    btnFitWidth.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                    btnStretch.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                }
            });
    }
}