package io.github.yusukeiwaki.githubviewer2.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 */
public class SideMenuItemView extends LinearLayout {
    public SideMenuItemView(Context context) {
        super(context);
        initialize(context, null);
    }

    public SideMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public SideMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SideMenuItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);

        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{
                R.attr.selectableItemBackground,
                R.attr.listPreferredItemHeightSmall,
                R.attr.listPreferredItemPaddingLeft,
                R.attr.listPreferredItemPaddingRight
        });
        setBackground(array.getDrawable(0));
        setMinimumHeight(array.getDimensionPixelSize(1, getMinimumHeight()));
        setPadding(array.getDimensionPixelSize(2, 0), getPaddingTop(), array.getDimensionPixelSize(3, 0), getPaddingBottom());
        array.recycle();

        View.inflate(context, R.layout.nav_item, this);

        if (attrs != null) {
            TypedArray array2 =
                    context.getTheme().obtainStyledAttributes(attrs, R.styleable.SideMenuItemView, 0, 0);
            setTitle(array2.getString(R.styleable.SideMenuItemView_android_title));
            setIcon(array2.getString(R.styleable.SideMenuItemView_materialIcon));

            array2.recycle();
        }
    }

    public SideMenuItemView setTitle(CharSequence title) {
        TextView titleTextView = (TextView) findViewById(R.id.text);
        titleTextView.setText(title);

        return this;
    }

    public SideMenuItemView setIcon(CharSequence icon) {
        TextView iconTextView = (TextView) findViewById(R.id.icon);
        if (TextUtils.isEmpty(icon)) {
            iconTextView.setVisibility(View.GONE);
        } else {
            iconTextView.setText(icon);
            iconTextView.setVisibility(View.VISIBLE);
        }

        return this;
    }
}
