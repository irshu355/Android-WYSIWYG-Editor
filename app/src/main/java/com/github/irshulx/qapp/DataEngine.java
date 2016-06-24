package com.github.irshulx.qapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by mkallingal on 12/18/2015.
 */
public class DataEngine {
    private final String SharedPreference="QA";
    public Context _Context;
    private Gson gson;
    public String AccessToken;
    public DataEngine(){ }
    public  DataEngine(Context _Context){
        this._Context= _Context;
        gson = new Gson();
        this.AccessToken= GetValue("access_token","");
      //  this.host=getLocalIp();

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