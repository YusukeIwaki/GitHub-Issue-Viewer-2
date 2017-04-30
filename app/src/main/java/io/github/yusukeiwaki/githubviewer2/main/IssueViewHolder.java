package io.github.yusukeiwaki.githubviewer2.main;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import io.github.yusukeiwaki.githubviewer2.LaunchUtil;
import io.github.yusukeiwaki.githubviewer2.databinding.ListItemIssueBinding;
import io.github.yusukeiwaki.githubviewer2.model.Issue;

/**
 */
public class IssueViewHolder extends RecyclerView.ViewHolder {

    private ListItemIssueBinding binding;

    public IssueViewHolder(ListItemIssueBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Issue issue) {
        binding.setIssue(issue);

        if (issue != null && !TextUtils.isEmpty(issue.html_url)) {
            final Uri uri = Uri.parse(issue.html_url);
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
