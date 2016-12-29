package io.github.yusukeiwaki.githubviewer.renderer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.model.Issue;

/**
 */
public class IssueRenderer extends AbstractRenderer<Issue> {
    private final UserRenderer userRenderer;
    public IssueRenderer(Context context, Issue issue) {
        super(context, issue);
        userRenderer = new UserRenderer(context, issue != null ? issue.getUser() : null);
    }

    public IssueRenderer titleInto(TextView textView) {
        if (!shouldHandle(textView)) return this;

        textView.setText(object.getTitle());

        return this;
    }

    public IssueRenderer iconInto(TextView textView) {
        if (!shouldHandle(textView)) return this;

        if (TextUtils.isEmpty(object.getPull_request())) {
            textView.setText(R.string.my_font_issue);
        } else {
            textView.setText(R.string.my_font_pull_request);
        }

        return this;
    }

    public IssueRenderer userAvatarInto(ImageView avatarView) {
        if (!shouldHandle(avatarView)) return this;

        userRenderer.avatarInto(avatarView);

        return this;
    }

    public IssueRenderer usernameInto(TextView textView) {
        if (!shouldHandle(textView)) return this;

        userRenderer.usernameInto(textView);

        return this;
    }

    public IssueRenderer repoTitleInto(TextView textView) {
        if (!shouldHandle(textView)) return this;

        textView.setText(object.getRepositoryName());

        return this;
    }

    public IssueRenderer numberInto(TextView textView) {
        if (!shouldHandle(textView)) return this;

        textView.setText("#" + object.getNumber());

        return this;
    }

    public IssueRenderer numCommentInto(View icon, TextView textView) {
        if (!shouldHandle(textView)) return this;

        long numComments = object.getComments();
        if (numComments > 0) {
            icon.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(Long.toString(numComments));
        } else {
            icon.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }

        return this;
    }

    public IssueRenderer stateInto(CardView cardView) {
        if (!shouldHandle(cardView)) return this;

        if ("closed".equals(object.getState())) {
            cardView.setCardBackgroundColor(0xffC6C6C6);
        } else {
            cardView.setCardBackgroundColor(0xffE7F6DF);
        }

        return this;
    }
}
