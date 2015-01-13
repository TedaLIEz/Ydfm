package com.hustascii.ydfm.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by wei on 15-1-13.
 */
public class CustomTextView extends TextView{

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomTextView(Context context) {
        super(context);
    }
    public void setTypeface(Typeface tf, int style) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "/fonts/custom.ttf"));
    }
}
