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

import com.irshu.editor.EditorLayout;
import com.irshu.editor.models.ControlStyles;
import com.irshu.editor.models.RenderType;

import java.io.IOException;

public class EditorTestActivity extends AppCompatActivity {
    EditorLayout _layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout _LinearLayout= (LinearLayout)findViewById(R.id.parentHolder);
        _layout=new EditorLayout(EditorTestActivity.this,_LinearLayout, RenderType.Editor, "Editor Placeholder goes here...");
        findViewById(R.id.action_header_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.UpdateTextStyle(ControlStyles.H1);
            }
        });
        findViewById(R.id.action_header_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.UpdateTextStyle(ControlStyles.H2);
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.UpdateTextStyle(ControlStyles.BOLD);
            }
        });

        findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.UpdateTextStyle(ControlStyles.ITALIC);
            }
        });


        findViewById(R.id.action_bulleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.InsertUnorderedList();
            }
        });
        findViewById(R.id.action_unordered_numbered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.InsertUnorderedList();
            }
        });
        findViewById(R.id.action_hr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.InsertDivider();
            }
        });
        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.OpenImagePicker();
            }
        });
        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.InsertLink();
            }
        });
        findViewById(R.id.action_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.InsertMap();
            }
        });
        _layout.startEditor();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int PICK_IMAGE_REQUEST = 1;

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK&& data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                _layout.InsertImage(bitmap);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            Toast.makeText(getApplicationContext(), "It was canccelled", Toast.LENGTH_SHORT).show();
            _layout.RestoreState();
        }
        else if(requestCode==20){
            _layout.InsertMap(data.getStringExtra("result"),true);
        }
    }
}