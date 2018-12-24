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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.irshulx.EditorComponent;
import com.github.irshulx.EditorCore;
import com.github.irshulx.GlideApp;
import com.github.irshulx.R;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorControl;
import com.github.irshulx.models.EditorType;
import com.github.irshulx.models.HtmlTag;
import com.github.irshulx.models.Node;
import com.github.irshulx.models.RenderType;
import com.github.irshulx.models.TextSettings;
import org.jsoup.nodes.Element;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by mkallingal on 5/1/2016.
 */
public class ImageExtensions extends EditorComponent {
    private EditorCore editorCore;
    private int editorImageLayout = R.layout.tmpl_image_view;
    public RequestListener requestListener;
    public RequestOptions requestOptions;
    public DrawableTransitionOptions transition;

    @DrawableRes
    public int placeholder = -1;
    @DrawableRes
    public int errorBackground = -1;

    @Override
    public Node getContent(View view) {
        Node node = getNodeInstance(view);
        EditorControl imgTag = (EditorControl) view.getTag();
        if (!TextUtils.isEmpty(imgTag.path)) {
            node.content.add(imgTag.path);

            /**
             * for subtitle
             */
            EditText textView =  view.findViewById(R.id.desc);
            Node subTitleNode = getNodeInstance(textView);
            EditorControl descTag = (EditorControl) textView.getTag();
            subTitleNode.contentStyles = descTag.editorTextStyles;
            subTitleNode.textSettings = descTag.textSettings;
            Editable desc = textView.getText();
            subTitleNode.content.add(Html.toHtml(desc));
            node.childs = new ArrayList<>();
            node.childs.add(subTitleNode);
        }
        return  node;
    }

    @Override
    public String getContentAsHTML(Node node, EditorContent content) {
        String subHtml = componentsWrapper.getInputExtensions().getInputHtml(node.childs.get(0));
        String html = componentsWrapper.getHtmlExtensions().getTemplateHtml(node.type);
        html = html.replace("{{$url}}", node.content.get(0));
        html = html.replace("{{$img-sub}}", subHtml);
        return html;
    }

    @Override
    public void renderEditorFromState(Node node, EditorContent content) {
        String path = node.content.get(0);
        if(editorCore.getRenderType() == RenderType.Renderer) {
            loadImage(path, node.childs.get(0));
        }else{
            View layout = insertImage(null,path,editorCore.getChildCount(),node.childs.get(0).content.get(0), false);
            componentsWrapper. getInputExtensions().applyTextSettings(node.childs.get(0), (TextView) layout.findViewById(R.id.desc));
        }
    }

    @Override
    public Node buildNodeFromHTML(Element element) {
        HtmlTag tag = HtmlTag.valueOf(element.tagName().toLowerCase());
        if(tag == HtmlTag.div){
            String dataTag = element.attr("data-tag");
            if (dataTag.equals("img")) {
                Element img = element.child(0);
                Element descTag = element.child(1);
                String src = img.attr("src");
                loadImage(src, descTag);
            }
        }else {
            String src = element.attr("src");
            if(element.children().size() > 0) {
                Element descTag = element.child(1);
                loadImage(src, descTag);
            }else{
                loadImageRemote(src, null);
            }
        }
        return null;
    }

    @Override
    public void init(ComponentsWrapper componentsWrapper) {
        this.componentsWrapper = componentsWrapper;
    }

    public ImageExtensions(EditorCore editorCore) {
        super(editorCore);
        this.editorCore = editorCore;
    }

    public void setEditorImageLayout(int drawable) {
        this.editorImageLayout = drawable;
    }

    public void openImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) editorCore.getContext()).startActivityForResult(Intent.createChooser(intent, "Select an image"), editorCore.PICK_IMAGE_REQUEST);
    }

    public View insertImage(Bitmap image, String url, int index, String subTitle, boolean appendTextline) {
        boolean hasUploaded = false;
        if(!TextUtils.isEmpty(url)) hasUploaded = true;

        // Render(getStateFromString());
        final View childLayout = ((Activity) editorCore.getContext()).getLayoutInflater().inflate(this.editorImageLayout, null);
        ImageView imageView =  childLayout.findViewById(R.id.imageView);
        final TextView lblStatus =  childLayout.findViewById(R.id.lblStatus);
        final CustomEditText desc = childLayout.findViewById(R.id.desc);
        if(!TextUtils.isEmpty(url)){
            loadImageUsingLib(url, imageView);
        }else {
            imageView.setImageBitmap(image);
        }
        final String uuid = generateUUID();
        if (index == -1) {
            index = editorCore.determineIndex(EditorType.img);
        }
        showNextInputHint(index);
        editorCore.getParentView().addView(childLayout, index);

        //      _Views.add(childLayout);

        // set the imageId,so we can recognize later after upload
        childLayout.setTag(createImageTag(hasUploaded ? url : uuid));
        desc.setTag(createSubTitleTag());


        desc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    desc.clearFocus();
                } else {
                    editorCore.setActiveView(desc);
                }
            }
        });

        if (editorCore.isLastRow(childLayout) && appendTextline) {
            componentsWrapper.getInputExtensions().insertEditText(index + 1, null, null);
        }
        if(!TextUtils.isEmpty(subTitle))
            componentsWrapper.getInputExtensions().setText(desc, subTitle);
        if(editorCore.getRenderType()== RenderType.Editor) {
            BindEvents(childLayout);
            if(!hasUploaded){
                lblStatus.setVisibility(View.VISIBLE);
                childLayout.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                editorCore.getEditorListener().onUpload(image, uuid);
            }
        }else {
            desc.setEnabled(false);
            lblStatus.setVisibility(View.GONE);
        }

        return childLayout;
    }

    private void showNextInputHint(int index) {
        View view = editorCore.getParentView().getChildAt(index);
        EditorType type = editorCore.getControlType(view);
        if (type != EditorType.INPUT)
            return;
        TextView tv = (TextView) view;
        tv.setHint(editorCore.getPlaceHolder());
        Linkify.addLinks(tv,Linkify.ALL);
    }

    private void hideInputHint(int index) {
        View view = editorCore.getParentView().getChildAt(index);
        EditorType type = editorCore.getControlType(view);
        if (type != EditorType.INPUT)
            return;

        String hint = editorCore.getPlaceHolder();
        if (index > 0) {
            View prevView = editorCore.getParentView().getChildAt(index - 1);
            EditorType prevType = editorCore.getControlType(prevView);
            if (prevType == EditorType.INPUT)
                hint = null;
        }
        TextView tv = (TextView) view;
        tv.setHint(hint);
    }

    public String generateUUID() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String sdt = df.format(new Date(System.currentTimeMillis()));
        UUID x = UUID.randomUUID();
        String[] y = x.toString().split("-");
        return y[y.length - 1] + sdt;
    }

    public EditorControl createSubTitleTag(){
        EditorControl subTag = editorCore.createTag(EditorType.IMG_SUB);
        subTag.textSettings = new TextSettings("#5E5E5E");
        return subTag;
    }

    public EditorControl createImageTag(String path) {
        EditorControl control = editorCore.createTag(EditorType.img);
        control.path = path;
        return control;
    }
    /*
      /used by the renderer to render the image from the Node
    */
    public void loadImage(String _path, Node node) {
        String desc = node.content.get(0);
        final View childLayout = loadImageRemote(_path, desc);
        CustomEditText text = childLayout.findViewById(R.id.desc);
        if (!TextUtils.isEmpty(desc)) {
            componentsWrapper.getInputExtensions().applyTextSettings(node, text);
        }
    }

    public void loadImage(String _path, Element node) {
        String desc = null;
        if(node != null) {
            desc = node.html();
        }
        final View childLayout = loadImageRemote(_path, desc);
        CustomEditText text = childLayout.findViewById(R.id.desc);
        if(node != null) {
            componentsWrapper.getInputExtensions().applyStyles(text, node);
        }
    }

    public View loadImageRemote(String _path, String desc){
        final View childLayout = ((Activity) editorCore.getContext()).getLayoutInflater().inflate(this.editorImageLayout, null);
        ImageView imageView = childLayout.findViewById(R.id.imageView);
        CustomEditText text = childLayout.findViewById(R.id.desc);

        childLayout.setTag(createImageTag(_path));
        text.setTag(createSubTitleTag());
        if(!TextUtils.isEmpty(desc)) {
            componentsWrapper.getInputExtensions().setText(text, desc);
        }
        text.setEnabled(editorCore.getRenderType() == RenderType.Editor);
        loadImageUsingLib(_path, imageView);
        editorCore.getParentView().addView(childLayout);

        if(editorCore.getRenderType()== RenderType.Editor) {
            BindEvents(childLayout);
        }

        return childLayout;
    }


    public void loadImageUsingLib(String path, ImageView imageView){
        if(requestListener == null){
            requestListener = new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            };
        }


        if(placeholder == -1){
            placeholder = R.drawable.image_placeholder;
        }

        if(errorBackground == -1){
            errorBackground = R.drawable.error_background;
        }

        if(requestOptions == null) {
            requestOptions = new RequestOptions();
        }

        requestOptions.placeholder(placeholder);
        requestOptions.error(errorBackground);

        if(transition == null){
            transition = DrawableTransitionOptions.withCrossFade().crossFade(1000);
        }
        GlideApp.with(imageView.getContext())
                .load(path)
                .transition(transition)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)   //No disk cache
                .listener(requestListener)
                .apply(requestOptions)
                .into(imageView);
    }


    public View findImageById(String imageId) {
        for (int i = 0; i < editorCore.getParentChildCount(); i++) {
            View view = editorCore.getParentView().getChildAt(i);
            EditorControl control = editorCore.getControlTag(view);
            if (!TextUtils.isEmpty(control.path) && control.path.equals(imageId))
                return view;
        }
        return null;
    }

    public void onPostUpload(String url, String imageId) {
        View view = findImageById(imageId);
        final TextView lblStatus = (TextView) view.findViewById(R.id.lblStatus);
        lblStatus.setText(!TextUtils.isEmpty(url) ? "Upload complete" : "Upload failed");
        if (!TextUtils.isEmpty(url)) {
            EditorControl control = editorCore.createTag(EditorType.img);
            control.path = url;
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



    private void BindEvents(final View layout) {
        final ImageView imageView = layout.findViewById(R.id.imageView);
        final View btn_remove = layout.findViewById(R.id.btn_remove);

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = editorCore.getParentView().indexOfChild(layout);
                editorCore.getParentView().removeView(layout);
                hideInputHint(index);
                componentsWrapper.getInputExtensions().setFocusToPrevious(index);
            }
        });

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int paddingTop = view.getPaddingTop();
                    int paddingBottom = view.getPaddingBottom();
                    int height = view.getHeight();
                    if (event.getY() < paddingTop) {
                        editorCore.___onViewTouched(0, editorCore.getParentView().indexOfChild(layout));
                    }
                    else if (event.getY() > height - paddingBottom) {
                        editorCore.___onViewTouched(1,  editorCore.getParentView().indexOfChild(layout));
                    } else {

                    }
                    return false;
                }
                return true;//hmmmm....
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