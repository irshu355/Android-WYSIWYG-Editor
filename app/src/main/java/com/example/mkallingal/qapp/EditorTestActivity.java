package com.example.mkallingal.qapp;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.irshu.editor.Editor;
import com.irshu.editor.models.ControlStyles;
import com.irshu.editor.models.RenderType;

import java.io.IOException;

public class EditorTestActivity extends AppCompatActivity {
    Editor _editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
     //   Edit _LinearLayout= (LinearLayout)findViewById(R.id.formHolder);
     //   _editor =new Editor(EditorTestActivity.this,_LinearLayout, RenderType.Editor, "Editor Placeholder goes here...");
        _editor= (Editor) findViewById(R.id.editor);
        
        findViewById(R.id.action_header_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.UpdateTextStyle(ControlStyles.H1);
            }
        });
        findViewById(R.id.action_header_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.UpdateTextStyle(ControlStyles.H2);
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.UpdateTextStyle(ControlStyles.BOLD);
            }
        });

        findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.UpdateTextStyle(ControlStyles.ITALIC);
            }
        });


        findViewById(R.id.action_bulleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertUnorderedList();
            }
        });
        findViewById(R.id.action_unordered_numbered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertUnorderedList();
            }
        });
        findViewById(R.id.action_hr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertDivider();
            }
        });
        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.OpenImagePicker();
            }
        });
        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertLink();
            }
        });
        findViewById(R.id.action_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertMap();
            }
        });
        _editor.StartEditor();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == _editor.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK&& data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                _editor.InsertImage(bitmap);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            Toast.makeText(getApplicationContext(), "It was canccelled", Toast.LENGTH_SHORT).show();
            _editor.RestoreState();
        }
        else if(requestCode== _editor.MAP_MARKER_REQUEST){
            _editor.InsertMap(data.getStringExtra("cords"), true);
        }
    }
}