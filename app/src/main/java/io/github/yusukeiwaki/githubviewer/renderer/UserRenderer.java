package io.github.yusukeiwaki.githubviewer.renderer;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.github.yusukeiwaki.githubviewer.model.User;

/**
 */
public class UserRenderer extends AbstractRenderer<User> {
    public UserRenderer(Context context, User object) {
        super(context, object);
    }

    public UserRenderer usernameInto(TextView textView) {
        if (!shouldHandle(textView)) return this;

        textView.setText(object.getLogin());

        return this;
    }

    public UserRenderer avatarInto(final ImageView imageView) {
        if (!shouldHandle(imageView)) return this;

        final String url = object.getAvatar_url();
        if (imageView.getTag() != null) {
            String origUrl = (String) imageView.getTag();
            if (origUrl.equals(url)) return this;
        }

        Picasso.with(context)
                .load(url)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setTag(url);
                    }

                    @Override
                    public void onError() {

                    }
                });
        return this;
    }
}
