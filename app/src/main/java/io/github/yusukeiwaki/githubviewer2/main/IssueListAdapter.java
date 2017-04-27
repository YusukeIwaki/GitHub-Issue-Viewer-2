package io.github.yusukeiwaki.githubviewer2.main;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.yusukeiwaki.githubviewer2.databinding.ListItemIssueBinding;
import io.github.yusukeiwaki.githubviewer2.model.Issue;

/**
 */
public class IssueListAdapter extends RecyclerView.Adapter<IssueViewHolder> {
    private final List<Issue> issueList = new ArrayList<>();

    public void updateIssueList(List<Issue> newIssueList) {
        List<Issue> sortedNewIssueList = new ArrayList<>(newIssueList);
        Collections.sort(sortedNewIssueList, new Comparator<Issue>() {
            @Override
            public int compare(Issue issue1, Issue issue2) {
                return issue2.getUpdated_at().compareTo(issue1.getUpdated_at());
            }
        });
        if (issueList.isEmpty() || newIssueList.isEmpty()) {
            issueList.clear();
            issueList.addAll(sortedNewIssueList);
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new IssueListDiffCallback(issueList, sortedNewIssueList));
            issueList.clear();
            issueList.addAll(sortedNewIssueList);
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public IssueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemIssueBinding binding = ListItemIssueBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IssueViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(IssueViewHolder holder, int position) {
        holder.bind(issueList.get(position));
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }


    private static class IssueListDiffCallback extends DiffUtil.Callback {

        private final List<Issue> oldData;
        private final List<Issue> newData;

        public IssueListDiffCallback(List<Issue> oldData, List<Issue> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        @Override
        public int getOldListSize() {
            return oldData.size();
        }

        @Override
        public int getNewListSize() {
            return newData.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldData.get(oldItemPosition).getId() == newData.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldData.get(oldItemPosition).isTheSame(newData.get(newItemPosition));
        }
    }
}
