package io.github.yusukeiwaki.githubviewer.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * TextView with font-awesome.
 */
public class MyFontTextView extends AbstractCustomFontTextView {
    public MyFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFontTextView(Context context) {
        super(context);
    }

    @Override
    protected String getFont() {
        return "myfont.ttf";
    }
}
