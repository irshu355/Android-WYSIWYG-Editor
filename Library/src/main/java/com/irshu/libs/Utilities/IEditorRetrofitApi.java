package com.irshu.libs.Utilities;
import com.irshu.libs.models.ImageResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by mkallingal on 5/18/2016.
 */
public interface IEditorRetrofitApi {
    @Multipart
    @POST
    Call<ImageResponse> upload(@Url String url,@Part("description") RequestBody description,
                              @Part MultipartBody.Part file);
}
