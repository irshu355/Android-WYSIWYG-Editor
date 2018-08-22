package com.github.irshulx.wysiwyg;

import android.app.Application;


/**
 * Created by mkallingal on 12/23/2015.
 */
public class MyApplication extends Application {

    private String someVariable;

    public String getSomeVariable() {
        return someVariable;
    }

    public void setSomeVariable(String someVariable) {
        this.someVariable = someVariable;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}