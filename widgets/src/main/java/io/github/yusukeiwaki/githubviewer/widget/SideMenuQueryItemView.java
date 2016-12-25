package io.github.yusukeiwaki.githubviewer.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 */
public class SideMenuQueryItemView extends LinearLayout {
    private long id;

    public SideMenuQueryItemView(Context context) {
        super(context);
        initialize(context);
    }

    public SideMenuQueryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SideMenuQueryItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SideMenuQueryItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    private void initialize(Context context) {
        setOrientation(HORIZONTAL);

        TypedArray array2 = context.getTheme().obtainStyledAttributes(new int[]{
                R.attr.selectableItemBackground,
                R.attr.listPreferredItemPaddingLeft,
                R.attr.listPreferredItemPaddingRight
        });
        setBackground(array2.getDrawable(0));
        setPadding(array2.getDimensionPixelSize(1, 0), getPaddingTop(), array2.getDimensionPixelSize(2, 0), getPaddingBottom());
        array2.recycle();

        View.inflate(context, R.layout.query_item, this);
        setUnreadCount(0);
    }

    public SideMenuQueryItemView setItemId(long id) {
        this.id = id;
        return this;
    }

    public long getItemId() {
        return id;
    }

    public SideMenuQueryItemView setTitle(CharSequence title) {
        TextView titleTextView = (TextView) findViewById(android.R.id.text1);
        titleTextView.setText(title);

        return this;
    }

    public SideMenuQueryItemView setUnreadCount(int count) {
        View unreadCountContainer = findViewById(R.id.unread_count_container);
        TextView unreadCount = (TextView) findViewById(R.id.unread_count);
        if (count > 0) {
            unreadCount.setText(Integer.toString(count));
            unreadCountContainer.setVisibility(View.VISIBLE);
        } else {
            unreadCountContainer.setVisibility(View.GONE);
        }

        return this;
    }
}
