package com.github.irshulx.Components;

/**
 * Created by mkallingal on 4/25/2016.
 */
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
/**
 * Created by mkallingal on 4/25/2016.
 */
public class CustomEditText extends TextInputEditText {
    public static final int KEYCODE_REMOVE=100;
    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context) {
        super(context);
    }



    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new CustomInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    private class CustomInputConnection extends InputConnectionWrapper {

        public CustomInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
           if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                return super.sendKeyEvent(event);
            }else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
               return super.sendKeyEvent(event);
           }
            return false;
        }


        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                int len = getText().length();
                if(len==0){
                    boolean isBackspace = sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                            && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                    return isBackspace;
                }
                int selection= getSelectionStart();
                if(selection==0)
                    return false;
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }

    }
}

