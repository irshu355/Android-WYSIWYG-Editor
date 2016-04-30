package com.example.mkallingal.qapp;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.irshu.editor.EditorLayout;
import com.irshu.editor.models.ControlStyles;
import com.irshu.editor.models.RenderType;

public class EditorTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout _LinearLayout= (LinearLayout)findViewById(R.id.parentHolder);
        final EditorLayout _layout=new EditorLayout(EditorTestActivity.this,_LinearLayout, RenderType.Editor, "Editor Placeholder goes here...");
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
                _layout.InsertImage();
            }
        });
        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout.InsertLink();
            }
        });
        _layout.startEditor();
    }
}