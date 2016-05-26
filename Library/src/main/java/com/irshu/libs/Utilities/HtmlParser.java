package com.irshu.libs.Utilities;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.irshu.libs.models.HtmlTag;

/**
 * Created by mkallingal on 5/22/2016.
 */

/*

<h1>I was born an Indian</h1>
<h1>sfdddsdvsdvsdv</h1>
<h1 style="text-align: center;">
    <span style="color: rgb(107, 113, 122); font-size: 13px; text-align: left; line-height: 1.42857;">
         <br>
    </span>
</h1>
<h1 style="text-align: center;">
    <span style="color: rgb(107, 113, 122); font-size: 13px; text-align: left; line-height: 1.42857;">
        asdasdfghj
    </span>
</h1>
<ul>
   <li style="text-align: left;">asdfghjk</li>
   <li style="text-align: left;">asdfghyujijytgrfedwsedrtyujtrfgeds</li>
   <li style="text-align: left;">derftgyujtrgefwdswderty</li>
</ul>
<p style="text-align: left;">tryyyyyyyyyyyyyyyyyyyyyyyy</p>
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

