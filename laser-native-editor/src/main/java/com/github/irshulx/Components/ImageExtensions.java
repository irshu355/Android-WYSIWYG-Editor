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

import com.github.irshulx.BaseClass;
import com.github.irshulx.R;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.Utilities.IEditorRetrofitApi;
import com.github.irshulx.models.ImageResponse;
import com.github.irshulx.Utilities.ServiceGenerator;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class ImageExtensions {
    private Context context;
    private BaseClass base;
    private String imageUploadUri;
    private int editorImageLayout=R.layout.editor_image_view;
    public ImageExtensions(BaseClass baseClass, Context context){
        this.context = context;
        this.base = baseClass;
    }

    public void setEditorImageLayout(int drawable){
        this.editorImageLayout= drawable;
    }

    public void executeDownloadImageTask(String url, int index){
        new DownloadImageTask(index).execute(url);
    }

    public void setImageUploadUri(String uri){
        this.imageUploadUri= uri;
    }

    public void OpenImageGallery() {
        int Index=this.base.determineIndex(EditorType.none);
        EditorContent state= base.getContent();
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    // Always show the chooser (if there are multiple options available)
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select an image"), base.PICK_IMAGE_REQUEST);
    }

    public void InsertImage(Bitmap _image,int Index) {
       // Render(getStateFromString());
        final View childLayout = ((Activity) context).getLayoutInflater().inflate(this.editorImageLayout, null);
        ImageView imageView = (ImageView) childLayout.findViewById(R.id.imageView);
        imageView.setImageBitmap(_image);
        final String uuid= GenerateUUID();
        BindEvents(childLayout);
        if(Index==-1) {
             Index = base.determineIndex(EditorType.img);
        }
        base.getParentView().addView(childLayout, Index);
        //      _Views.add(childLayout);
        if(base.isLastRow(childLayout)) {
            base.getInputExtensions().InsertEditText(Index + 1, null, null);
        }
        EditorControl control= base.CreateTag(EditorType.img);
        childLayout.setTag(control);
        if(TextUtils.isEmpty(base.getImageUploaderUri())) {
            String error="You must configure a valid remote URI to be able to upload the image.This image is not persisted";
            base.getUtilitiles().toastItOut(error);
            TextView sts=(TextView) childLayout.findViewById(R.id.lblStatus);
            sts.setBackgroundDrawable(base.getResources().getDrawable(R.drawable.error_background));
            sts.setVisibility(View.VISIBLE);
            sts.setText(error);
            return;
        }
        UploadImageToServer(_image, childLayout, uuid);
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
        ImageView imageView = new ImageView(this.context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,25,0,30);
        imageView.setLayoutParams(params);

        Picasso.with(this.context).load(_path).into(imageView);
        base.getParentView().addView(imageView);
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


    /*
         /used to upload the image to remote
       */
    private  void UploadImageToServer(Bitmap bitmap, final View view, String uuid){
        File f = new File(context.getCacheDir(), uuid+".png");
        final TextView lblStatus= (TextView) view.findViewById(R.id.lblStatus);
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            IEditorRetrofitApi service =
                    ServiceGenerator.createService(IEditorRetrofitApi.class);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), f);
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("picture", f.getName(), requestFile);
            // add another part within the multipart request
            RequestBody description =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), "");
            // finally, execute the request
            view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            lblStatus.setVisibility(View.VISIBLE);
            if(base.getEditorListener()!=null){
            }
            Call<ImageResponse> call = service.upload(base.getImageUploaderUri(), description, body);
            call.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, final retrofit2.Response<ImageResponse> response) {
                    ((TextView) view.findViewById(R.id.lblStatus)).setText("Upload complete");
                    EditorControl control= base.CreateTag(EditorType.img);
                    control.Path= response.body().Uri;
                    view.setTag(control);
                    new java.util.Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                    lblStatus.setVisibility(View.GONE);
                                }
                            });
                        }
                    },3000);
                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    lblStatus.setText(t.getMessage());
                    lblStatus.setBackgroundDrawable(base.getResources().getDrawable(R.drawable.error_background));
                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void BindEvents(final View layout){
        final ImageView imageView= (ImageView) layout.findViewById(R.id.imageView);
        final View btn_remove=layout.findViewById(R.id.btn_remove);

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base.getParentView().removeView(layout);
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