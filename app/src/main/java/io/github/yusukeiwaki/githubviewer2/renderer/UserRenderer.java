package io.github.yusukeiwaki.githubviewer2.renderer;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 */
public class UserRenderer {
    @BindingAdapter("userAvatarUrl")
    public static void loadAvatar(final ImageView imageView, final String url) {
        if (imageView.getTag() != null) {
            String origUrl = (String) imageView.getTag();
            if (origUrl.equals(url)) return;
        }

        Picasso.with(imageView.getContext())
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
    }
}
