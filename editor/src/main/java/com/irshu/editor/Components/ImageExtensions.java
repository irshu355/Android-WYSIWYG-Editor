package com.irshu.editor.Components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
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
public class ImageExtensions extends BaseClass {
    private Context _Context;
    private BaseClass _BaseClass;
    public  ImageExtensions(){}
    public ImageExtensions(BaseClass baseClass){
        this._Context= baseClass._Context;
        this._BaseClass= baseClass;
    }

    public void OpenImageGallery() {
        int Index= determineIndex(EditorType.none);
        EditorState state= GetState();
        state.PendingIndex= Index;
        String serialized= serializeState(state);
        SaveState(serialized);

        int PICK_IMAGE_REQUEST = 1;
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        ((Activity)_Context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void InsertImage(Bitmap _image) {
       // RenderEditor(GetStateFromStorage());
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        _ImageView.setImageBitmap(_image);
        int count=_ParentView.getChildCount();
        String uuid= objEngine.GenerateUUID();
        String _path= objEngine.SaveImageToInternalStorage(_image, uuid);
        final FancyButton btn = (FancyButton) childLayout.findViewById(R.id.btn_remove);
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
                _ParentView.removeView(childLayout);
            }
        });
        EditorControl _control = new EditorControl();
        _control.Type = EditorType.img;
        _control.UUID= uuid;
        _control.Path=_path;
        childLayout.setTag(_control);

        int Index= determineIndex(EditorType.img);

        _ParentView.addView(childLayout, Index);
//                    _Views.add(childLayout);
        if(isLastRow(childLayout)) {
            inputExtensions.InsertEditText(Index + 1, "", "");
        }
    }
    public  void loadImage(Bitmap _image, String _path, String fileName, boolean insertEditText){
        if(this._RenderType== RenderType.Editor){
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
        Picasso.with(this._Context).load(objEngine.GetQImage(fileName)).into(_ImageView);
        _ParentView.addView(childLayout);
    }

    private void loadImageFromInternal(Bitmap _image, String _path, String fileName, boolean insertEditText) {
        final View childLayout = ((Activity) _Context).getLayoutInflater().inflate(R.layout.editor_image_view, null);
        ImageView _ImageView = (ImageView) childLayout.findViewById(R.id.imageView);
        _ImageView.setImageBitmap(_image);
        int count=_ParentView.getChildCount();

        final FancyButton btn = (FancyButton) childLayout.findViewById(R.id.btn_remove);
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
                _ParentView.removeView(childLayout);
            }
        });
        EditorControl _control = CreateTag(EditorType.img);
        _control.UUID= fileName;
        _control.Path=_path;
        childLayout.setTag(_control);
        int Index= determineIndex(EditorType.img);
        _ParentView.addView(childLayout, Index);
        if(insertEditText){
            inputExtensions.InsertEditText(Index + 1, "", "");
        }
    }
}
