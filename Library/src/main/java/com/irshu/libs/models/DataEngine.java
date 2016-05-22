package com.irshu.libs.models;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by mkallingal on 12/18/2015.
 */
public class DataEngine {
    private final String SharedPreference="QA";
    public Context _Context;
    private Gson gson;
    public String AccessToken;
    public DataEngine(){ }
    public DataEngine(Context _Context){
        this._Context= _Context;
        gson = new Gson();
        this.AccessToken= GetValue("access_token","");
    }


    public String GetValue(String Key, String defaultVal){
        SharedPreferences _Preferences= _Context.getSharedPreferences(SharedPreference, 0);
        return   _Preferences.getString(Key, defaultVal);

    }
    public void putValue(String Key, String Value){
        SharedPreferences _Preferences = _Context.getSharedPreferences(SharedPreference, 0);
        SharedPreferences.Editor editor = _Preferences.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

   public String GenerateUUID(){
       DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
       String sdt = df.format(new Date(System.currentTimeMillis()));
        UUID x= UUID.randomUUID();
      String[] y= x.toString().split("-");
       return y[y.length-1]+sdt;
    }

}