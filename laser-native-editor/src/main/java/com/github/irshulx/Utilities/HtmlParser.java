package com.github.irshulx.Utilities;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.irshulx.models.HtmlTag;

/**
 * Created by mkallingal on 5/22/2016.
 */


public class HtmlParser {
    private Context context;
    LinearLayout parentView;
    public HtmlParser(Context _context){
        this.context=_context;
        parentView=new LinearLayout(this.context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentView.setLayoutParams(params);
    }



    public static boolean matchesTag(String test) {
        for (HtmlTag tag : HtmlTag.values()) {
            if (tag.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}

