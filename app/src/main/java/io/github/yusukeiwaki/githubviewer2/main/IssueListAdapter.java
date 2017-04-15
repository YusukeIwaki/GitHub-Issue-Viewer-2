package io.github.yusukeiwaki.githubviewer2.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.github.yusukeiwaki.githubviewer2.databinding.ListItemIssueBinding;
import io.github.yusukeiwaki.githubviewer2.model.Issue;
import io.github.yusukeiwaki.realm_java_helpers_bolts.RealmHelper;
import io.github.yusukeiwaki.realm_java_helpers_bolts.RealmRecyclerViewAdapter;

public class IssueListAdapter extends RealmRecyclerViewAdapter<Issue, IssueViewHolder> {
    public IssueListAdapter(RealmHelper.Query<Issue> query) {
        super(query);
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
