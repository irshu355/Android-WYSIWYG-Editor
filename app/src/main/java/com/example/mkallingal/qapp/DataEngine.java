package com.example.mkallingal.qapp;

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


    public void ClearCache() {
        putValue("cardslist", "");
        putValue("invoicelist", "");
        putValue("AcctNo", "");
        putValue("totalvolume", "");
        putValue("txntrend", "");
        putValue("topspender","");
        putValue("fuelconsumption", "");
        putValue("txnlist", "");
    }

    public  void ClearSignoutCache(){
        ClearCache();
        putValue("CorpCd","");
        putValue("PrivilegeCd", "");
        putValue("accountslist","");
        putValue("corporateinfo","");
        putValue("AcctInfo", "");
        putValue("UserName", " ");
        putValue("IsSuper", "");
        putValue("UserName","");
    }


    private  void DisplaySnackBar(){

    }

   public String GenerateUUID(){
       DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
       String sdt = df.format(new Date(System.currentTimeMillis()));
        UUID x= UUID.randomUUID();
      String[] y= x.toString().split("-");
       return y[y.length-1]+sdt;
    }


    public String SaveImageToInternalStorage(Bitmap bitmapImage, String UUID)  {
        ContextWrapper cw = new ContextWrapper(this._Context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,UUID+".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 20, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                fos.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
        return directory.getAbsolutePath();
    }


    public Bitmap LoadImageFromInternalStorage(String path, String UUID)
    {
        Bitmap b;
        try {
            File f=new File(path, UUID+".png");
           // b = BitmapFactory.decodeStream(new FileInputStream(f));
         //   return  b;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

           return  bitmap;

            // ImageView IMG=(ImageView)context.(R.id.imgPicker);
            // IMG.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            String x= e.getMessage();
            return  BitmapFactory.decodeResource(_Context.getResources(),
                    R.drawable.placeholder);
        }
    }

}