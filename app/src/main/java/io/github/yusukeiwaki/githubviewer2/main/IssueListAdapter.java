package io.github.yusukeiwaki.githubviewer2.main;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.github.yusukeiwaki.githubviewer2.databinding.ListItemIssueBinding;
import io.github.yusukeiwaki.githubviewer2.model.Issue;
import io.github.yusukeiwaki.githubviewer2.widget.RealmRecyclerViewAdapter2;
import io.realm.OrderedRealmCollection;

public class IssueListAdapter extends RealmRecyclerViewAdapter2<Issue, IssueViewHolder> {
    public IssueListAdapter(@Nullable OrderedRealmCollection<Issue> data) {
        super(data, true);
    }

    @Override
    public IssueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemIssueBinding binding = ListItemIssueBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IssueViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(IssueViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
