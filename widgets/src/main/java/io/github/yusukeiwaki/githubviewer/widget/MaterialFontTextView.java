package io.github.yusukeiwaki.githubviewer.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * TextView with font-awesome.
 */
public class MaterialFontTextView extends AbstractCustomFontTextView {
    public MaterialFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MaterialFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialFontTextView(Context context) {
        super(context);
    }

    @Override
    protected String getFont() {
        return "Material-Design-Iconic-Font.ttf";
    }
}
