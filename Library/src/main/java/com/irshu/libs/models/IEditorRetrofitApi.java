package com.irshu.libs.models;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by mkallingal on 5/18/2016.
 */
public interface IEditorRetrofitApi {

    @Multipart
    @POST("/api/DiscussionsApi/PostImage")
    void uploadImage(@Part("file") TypedFile file, Callback<Response> callback);
}
