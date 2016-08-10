package com.evontech.VideoPlugin;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by amitrai on 11/7/16.
 */
public class InputFilterMinMax implements InputFilter {

    private float min, max;

    public InputFilterMinMax(String min, String max) {
        this.min = Float.parseFloat(min);
        this.max = Float.parseFloat(max);
    }
    private boolean isInRange(float a, float b, float c, int length) {
        if (length>5)
            return false;
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            if(source.toString().equalsIgnoreCase("."))
                return null;
            float input = Float.parseFloat(dest.toString() + source.toString());
            if (isInRange(min, max, input, dstart)){
                int dotPos = -1;
                int len = dest.length();
                for (int i = 0; i < len; i++) {
                    char c = dest.charAt(i);
                    if (c == '.' || c == ',') {
                        dotPos = i;
                        break;
                    }
                }
                if (dotPos >= 0) {

                    // protects against many dots
                    if (source.equals(".") || source.equals(","))
                    {
                        return "";
                    }
                    // if the text is entered before the dot
                    if (dend <= dotPos) {
                        return null;
                    }
                    if (len - dotPos > 3) {
                        return "";
                    }
                }

                return null;
            }
        } catch (NumberFormatException nfe) { }
        return "";
    }
}
