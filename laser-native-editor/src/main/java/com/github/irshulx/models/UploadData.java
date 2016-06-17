package com.github.irshulx.models;

import okhttp3.RequestBody;

/**
 * Created by mkallingal on 6/13/2016.
 */
public class UploadData {
    public String Url;
    public RequestBody body;
    public boolean doUpload;

    public UploadData(){
        doUpload=true;
    }
}
