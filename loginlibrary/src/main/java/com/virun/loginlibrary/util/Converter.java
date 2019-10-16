package com.virun.loginlibrary.util;

import android.content.res.Resources;
import android.util.TypedValue;

public class Converter {

    private static Resources context;
    public Converter(Resources context){
        Converter.context =context;
    }
    public int getDP(int size) {
        size = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, size,  context
                        .getDisplayMetrics());
        return size;
    }
}
