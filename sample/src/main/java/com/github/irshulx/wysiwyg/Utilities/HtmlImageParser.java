package com.github.irshulx.wysiwyg.Utilities;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by mkallingal on 12/30/2015.
 */
public class HtmlImageParser implements Html.ImageGetter {
    public static final String LOG = HtmlImageParser.class.getName();
    Context c;
    View container;

    public HtmlImageParser(View t, Context c) {
        this.c = c;
        this.container = t;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Drawable getDrawable(String source) {
        try {
            HTMLImageDrawable urlDrawable = new HTMLImageDrawable();
            ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(
                    urlDrawable);

            if (Build.VERSION.SDK_INT < 11) {
                asyncTask.execute(source);
            } else {
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, source);
            }
            return urlDrawable;
        } catch (Exception e) {
            Log.e(LOG, e.getMessage());
        }
        return null;

    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        HTMLImageDrawable urlDrawable;

        public ImageGetterAsyncTask(HTMLImageDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            try {
                if (urlDrawable != null) {
                    urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(),
                            0 + result.getIntrinsicHeight());

                    urlDrawable.drawable = result;
                    HtmlImageParser.this.container.invalidate();
                }

            } catch (Exception e) {
                Log.e(LOG, e.getMessage());
            }
        }


        public Drawable fetchDrawable(String urlString) {
            try {
//                InputStream is = fetch(urlString);
                URL imageURL = new URL(urlString);
                InputStream inputStream = imageURL.openStream();
                Drawable drawable = Drawable.createFromStream(inputStream, "src");
                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(),
                        0 + drawable.getIntrinsicHeight());
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }


    }
}
