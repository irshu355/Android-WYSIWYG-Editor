package com.irshu.editor.Components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.irshu.editor.BaseClass;
import com.irshu.editor.R;
import com.irshu.editor.models.EditorControl;
import com.irshu.editor.models.EditorState;
import com.irshu.editor.models.EditorType;
import com.irshu.editor.models.RenderType;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import mehdi.sakout.fancybuttons.FancyButton;

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
        final String _path= this._Base.objEngine.SaveImageToInternalStorage(_image, uuid);

        BindEvents(childLayout,uuid,_path);
        EditorControl _control = new EditorControl();
        _control.Type = EditorType.img;
        _control.UUID= uuid;
        _control.Path=_path;
        childLayout.setTag(_control);

        int Index= _Base.determineIndex(EditorType.img);

        _Base._ParentView.addView(childLayout, Index);
//                    _Views.add(childLayout);
        if(_Base.isLastRow(childLayout)) {
            _Base.inputExtensions.InsertEditText(Index + 1, "", "");
        }
    }
    public  void loadImage(Bitmap _image, String _path, String fileName, boolean insertEditText){
        if(this._Base._RenderType== RenderType.Editor){
            loadImageFromInternal(_image,_path,fileName,insertEditText);
        }else{
            loadImageFromUri(_image,_path,fileName);
        }
    }
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
    private void loadImageFromUri(Bitmap image, String path, String fileName) {
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        Picasso.with(this._Context).load(_Base.objEngine.GetQImage(fileName)).into(_ImageView);
        _Base._ParentView.addView(childLayout);
    }

    private void loadImageFromInternal(Bitmap _image, String _path, String fileName, boolean insertEditText) {
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        _ImageView.setImageBitmap(_image);
        int count=_Base._ParentView.getChildCount();

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

    private void BindEvents(final View layout, final String uuid, final String _path){
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
                _Base.objEngine.RemoveImageFromStorage(_path, uuid + ".png");
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
                btn_remove.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
                btnCenterCrop.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
                btnFitWidth.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
                btnStretch.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        });



    }

}
