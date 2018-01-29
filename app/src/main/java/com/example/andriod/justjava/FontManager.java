package com.example.andriod.justjava;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Manage imported fonts
 *
 * Created by Cordula Gloge on 17/01/2018.
 */

public class FontManager {

    public static final String ROOT = "fonts/",
    FONTAWESOME = ROOT + "fontawesome_webfont.ttf";

    public static Typeface getTypeface(Context context, String font){
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}
