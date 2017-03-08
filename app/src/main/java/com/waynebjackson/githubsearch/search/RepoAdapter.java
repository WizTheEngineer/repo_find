package com.waynebjackson.githubsearch.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.waynebjackson.githubsearch.R;
import com.waynebjackson.githubsearch.data.model.Repo;
import com.waynebjackson.githubsearch.networking.PicassoFactory;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Wayne on 3/6/2017.
 */

public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.RepoViewHolder> {

    private LayoutInflater mInflater;
    private List<Repo> mRepositories;
    private RepoClickListener mRepoClickListener;
    private int mLastPosition = -1;
    private PicassoFactory mPicassoFactory;

    public RepoAdapter(@NonNull Context context, @NonNull RepoClickListener repoClickListener) {
        checkNotNull(context);
        checkNotNull(repoClickListener);
        mInflater = LayoutInflater.from(context);
        mRepositories = Lists.newArrayList();
        mRepoClickListener = repoClickListener;
        mPicassoFactory = new PicassoFactory();
    }

    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.repo_list_item, parent, false);
        return new RepoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RepoViewHolder holder, int position) {
        Repo repository = mRepositories.get(position);
        holder.bindRepo(repository);
        setAnimation(holder.itemView, position);
    }

    @Override
    public void onViewDetachedFromWindow(RepoViewHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return mRepositories.size();
    }

    public void bindRepos(@NonNull List<Repo> repositories) {
        mLastPosition = -1;
        mRepositories.clear();
        mRepositories.addAll(repositories);
        notifyDataSetChanged();
    }

    public void clearRepos() {
        mRepositories.clear();
        notifyDataSetChanged();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    public class RepoViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mOwnerAvatarView;
        private TextView mOwnerUsernameView;
        private TextView mTitleView;
        private TextView mDescriptionView;
        private TextView mStarCountView;
        private TextView mForkCountView;
        private TextView mWatcherCountView;
        private TextView mRepoCreatedAtView;
        private TextView mLanguageView;

        private SimpleDateFormat mDateFormat;


        public RepoViewHolder(View itemView) {
            super(itemView);
            mOwnerAvatarView = (CircleImageView) itemView.findViewById(R.id.repo_owner_avatar_view);
            mOwnerUsernameView = (TextView) itemView.findViewById(R.id.repo_owner_username_view);
            mTitleView = (TextView) itemView.findViewById(R.id.repo_title_view);
            mDescriptionView = (TextView) itemView.findViewById(R.id.repo_description_view);
            mStarCountView = (TextView) itemView.findViewById(R.id.star_count_view);
            mForkCountView = (TextView) itemView.findViewById(R.id.fork_count_view);
            mWatcherCountView = (TextView) itemView.findViewById(R.id.watcher_count_view);
            mRepoCreatedAtView = (TextView) itemView.findViewById(R.id.repo_created_at_view);
            mLanguageView = (TextView) itemView.findViewById(R.id.repo_language_view);

            mDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        }

        public void bindRepo(@NonNull final Repo repository) {
            mPicassoFactory.getPicasso(itemView.getContext())
                    .load(repository.getOwner().getAvatarUrl())
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(mOwnerAvatarView);
            mOwnerUsernameView.setText(repository.getOwner().getLogin());
            mTitleView.setText(repository.getName());
            mDescriptionView.setText(repository.getDescription());
            mStarCountView.setText(String.valueOf(repository.getStargazersCount()));
            mForkCountView.setText(String.valueOf(repository.getForksCount()));
            mWatcherCountView.setText(String.valueOf(repository.getWatchersCount()));

            // Format the date
            String createdDate = mDateFormat.format(repository.getCreatedAt());
            String createdString = itemView.getContext().getString(R.string.created_on,
                    createdDate);
            mRepoCreatedAtView.setText(createdString);

            mLanguageView.setText(repository.getLanguage());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRepoClickListener.onRepoClicked(repository);
                }
            });
        }

        public void clearAnimation() {
            itemView.clearAnimation();
        }
    }

    public interface RepoClickListener {
        void onRepoClicked(@NonNull Repo repository);
    }
}
