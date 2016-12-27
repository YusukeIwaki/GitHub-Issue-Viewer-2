package io.github.yusukeiwaki.githubviewer.main;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.yusukeiwaki.githubviewer.LaunchUtil;
import io.github.yusukeiwaki.githubviewer.R;
import io.github.yusukeiwaki.githubviewer.model.Issue;
import io.github.yusukeiwaki.githubviewer.renderer.IssueRenderer;

/**
 */
public class IssueViewHolder extends RecyclerView.ViewHolder {

    private final TextView titleView;
    private final TextView iconView;
    private final ImageView avatarView;
    private final TextView usernameView;
    private final TextView repoTitleView;
    private final TextView numberView;
    private final TextView commentIconView;
    private final TextView numCommentView;

    public IssueViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.title);
        iconView = (TextView) itemView.findViewById(R.id.icon);
        avatarView = (ImageView) itemView.findViewById(R.id.user_avatar);
        usernameView = (TextView) itemView.findViewById(R.id.user_name);
        repoTitleView = (TextView) itemView.findViewById(R.id.repo_title);
        numberView = (TextView) itemView.findViewById(R.id.issue_number);
        commentIconView = (TextView) itemView.findViewById(R.id.comment_icon);
        numCommentView = (TextView) itemView.findViewById(R.id.num_comment);
    }

    public void bind(Issue issue) {
        new IssueRenderer(itemView.getContext(), issue)
                .titleInto(titleView)
                .iconInto(iconView)
                .userAvatarInto(avatarView)
                .usernameInto(usernameView)
                .repoTitleInto(repoTitleView)
                .numberInto(numberView)
                .numCommentInto(commentIconView, numCommentView)
                .stateInto((CardView) itemView);

        if (issue != null && !TextUtils.isEmpty(issue.getHtml_url())) {
            final Uri uri = Uri.parse(issue.getHtml_url());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LaunchUtil.launchBrowser(view.getContext(), uri);
                }
            });
        } else {
            itemView.setOnClickListener(null);
            itemView.setClickable(false);
        }
    }
}
