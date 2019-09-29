package com.haiyunshan.mathlatex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import org.scilab.forge.jlatexmath.AjLatexMath;
import org.scilab.forge.jlatexmath.Insets;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class MathLatex {

    public static final void init(Context context) {
        AjLatexMath.init(context);
    }

    public static final void recycle() {

    }

    public static final Bitmap getBitmap(String latexString, float textSize) {
        TeXFormula formula = TeXFormula.getPartialTeXFormula(latexString);

        return getBitmap(formula, textSize);
    }

    public static final Bitmap getBitmap(TeXFormula formula, float textSize) {

        Context context = AjLatexMath.getContext();
        int w = context.getResources().getDisplayMetrics().widthPixels;
        int h = context.getResources().getDisplayMetrics().heightPixels;

        TeXFormula.TeXIconBuilder builder = formula.new TeXIconBuilder();
        builder.setStyle(TeXConstants.STYLE_DISPLAY)
                .setSize(textSize)
                .setWidth(TeXConstants.UNIT_PIXEL, w, TeXConstants.ALIGN_LEFT)
                .setIsMaxWidth(true)
                .setInterLineSpacing(TeXConstants.UNIT_PIXEL, AjLatexMath.getLeading(textSize));

        TeXIcon icon = builder.build();
        icon.setInsets(new Insets(5, 5, 5, 5));

        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        Bitmap.Config config = Bitmap.Config.ARGB_4444;


        Log.w("AA", "width = " + iconWidth + ", height = " + iconHeight);
        Bitmap image = Bitmap.createBitmap(iconWidth, iconHeight, config);

        Canvas g2 = new Canvas(image);
        g2.drawColor(Color.TRANSPARENT);
        icon.paintIcon(g2, 0, 0);

        return image;
    }
}
