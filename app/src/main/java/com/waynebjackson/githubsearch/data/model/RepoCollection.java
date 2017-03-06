package com.waynebjackson.githubsearch.data.model;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Wayne on 3/5/2017.
 * Contains the collection of {@link Repo} objects obtained from the Github search API.
 */
public class RepoCollection {

    @SerializedName("items")
    private List<Repo> repositories;

    public RepoCollection() {
        repositories = Lists.newArrayList();
    }

    public List<Repo> getRepositories() {
        return repositories;
    }
}
