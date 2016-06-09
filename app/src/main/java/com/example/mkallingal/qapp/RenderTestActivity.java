package com.example.mkallingal.qapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.github.irshulx.Editor;
import com.github.irshulx.models.EditorState;

public class RenderTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson=new Gson();
        setContentView(R.layout.activity_render_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Editor renderer= (Editor) findViewById(R.id.renderer);
        String content= getIntent().getStringExtra("content");
        EditorState Deserialized= gson.fromJson(content, EditorState.class);
        renderer.Render(Deserialized);
    }
}
