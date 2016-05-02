package com.irshu.editor.models;

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
import com.irshu.editor.R;

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
   //   public final String host="http://192.168.1.84/Social";
      public final String host="http://192.168.1.21/Social";
  //  public final String host="http://cityquery.azurewebsites.net";// =>Home
   // public final String host="http://192.168.43.239/Social";// =>UMobile
 //   public final String host="http://192.168.182.10/Social";// =>StarBucks, Centrepoint
 //public final String host="http://192.168.0.138/Social"; // =>Papparich, Centrepoint
 // public final String host="http://192.168.9.175/Social"; // =>Oldtown, Centrepoint
   // public final String host="http://10.0.0.122/Social"; // =>Coffee Bean, 1Utama

    private final String SharedPreference="QA";
    public Context _Context;
    private Gson gson;
    public String AccessToken;
    private ProgressDialog progressDialog;
    public DataEngine(){ }
    public DataEngine(Context _Context){
        this._Context= _Context;
        gson = new Gson();
        this.AccessToken= GetValue("access_token","");
      //  this.host=getLocalIp();

    }

    public String getLocalIp(){
        InetAddress iAddress = null;
        try {
            iAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String currentIp = iAddress.getHostAddress();
        return currentIp;
    }


    public  void ToastItOut(String Error){
        Toast.makeText(this._Context, Error, Toast.LENGTH_SHORT).show();
        return;
    }
    public String GetValue(String Key, String defaultVal){
        SharedPreferences _Preferences= _Context.getSharedPreferences(SharedPreference, 0);
        return   _Preferences.getString(Key, defaultVal);

    }

    public long GetValue (String Key, int defaultVal){
        SharedPreferences _Preferences = _Context.getSharedPreferences(SharedPreference, 0);
        return  _Preferences.getLong(Key, defaultVal);
    }

    public boolean GetValue (String Key, boolean defaultVal){
        SharedPreferences _Preferences = _Context.getSharedPreferences(SharedPreference, 0);
        return  _Preferences.getBoolean(Key, defaultVal);
    }

    public float GetValue (String Key, float defaultVal){
        SharedPreferences _Preferences = _Context.getSharedPreferences(SharedPreference, 0);
        return  _Preferences.getFloat(Key, defaultVal);
    }

    public void putValue(String Key, String Value){
        SharedPreferences _Preferences = _Context.getSharedPreferences(SharedPreference, 0);
        SharedPreferences.Editor editor = _Preferences.edit();
        editor.putString(Key, Value);
        editor.apply();
    }
    public void putValue(String Key, Long Value){
        SharedPreferences _Preferences = _Context.getSharedPreferences(SharedPreference, 0);
        SharedPreferences.Editor editor = _Preferences.edit();
        editor.putLong(Key, Value);
        editor.apply();
    }
    public void putValue(String Key, boolean Value){
        SharedPreferences _Preferences = _Context.getSharedPreferences(SharedPreference, 0);
        SharedPreferences.Editor editor = _Preferences.edit();
        editor.putBoolean(Key, Value);
        editor.apply();
    }

    public void putValue(String Key, float Value){
        SharedPreferences _Preferences= _Context.getSharedPreferences(SharedPreference, 0);
        SharedPreferences.Editor editor = _Preferences.edit();
        editor.putFloat(Key, Value);
        editor.apply();
    }

    public void SetCustomText(TextView _TextView, String Text)
    {
        _TextView.setText(Text == null || Text.length() == 0 ? "--" : Text);
    }

    public String FormatCurrencyToLocale(double Amount){
        Locale locale=new Locale("ms","MY");
        NumberFormat currencyFormatter= NumberFormat.getCurrencyInstance(locale);
        String FormattedAmount= currencyFormatter.format(Amount);
        return FormattedAmount;
    }

    public String FormatCurrencyToLocale(double Amount, boolean PrefixRequired){
        String FormattedAmount;
        if(PrefixRequired) {
            NumberFormat currencyFormatter;
            Locale locale = new Locale("ms", "MY");
            currencyFormatter = NumberFormat.getCurrencyInstance(locale);
            FormattedAmount = currencyFormatter.format(Amount);
            return FormattedAmount;
        }else{
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.setMaximumFractionDigits(2);
            FormattedAmount=formatter.format(Amount);
            return  FormattedAmount;
        }
    }

    public String FormatJSONISOToDayMonth(String DateString){
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        String[] Parts= DateString.split("-");

        String[] DayPart= Parts[2].split("T");

        return Parts[1]+"-"+DayPart[0];

    }

    public  void ShowProgressDialog(String Message,Context context){

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(Message);
        progressDialog.show();
    }

    public void HideProgressDialog(){
        this.progressDialog.dismiss();
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

    public boolean RemoveImageFromStorage(String path,String fileName){
        ContextWrapper cw = new ContextWrapper(this._Context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File file=new File(directory,path+fileName);
        try{
            return file.delete();
        }catch (Exception e){
            return false;
        }
    }


    public Bitmap LoadImageFromInternalStorage(String path, String UUID)
    {
        try {
            File f=new File(path, UUID+".png");
           // b = BitmapFactory.decodeStream(new FileInputStream(f));
         //   return  b;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

           return  bitmap;

            // ImageView img=(ImageView)_Context.(R.id.imgPicker);
            // img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            String x= e.getMessage();
            return  BitmapFactory.decodeResource(_Context.getResources(),
                  R.drawable.placeholder);
        }
    }


    public String GetUserProfilePicUrl(String UserId) {
       return  "https://panelqblobs.blob.core.windows.net/profile/" + UserId +
                "/images/sml/profilepic.png";
    }
    public String GetQImage(String fileName) {
        return  "https://panelqblobs.blob.core.windows.net/quploads/"+fileName+".png";
    }
}