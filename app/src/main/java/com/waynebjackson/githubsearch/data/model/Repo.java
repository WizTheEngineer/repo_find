package com.waynebjackson.githubsearch.data.model;

import java.util.Date;

/**
 * Created by Wayne on 3/3/2017.
 * Model class representing a Github repository.
 */
public class Repo {
    private long id;
    private String name;
    private Owner owner;
    private String htmlUrl;
    private String description;
    private int stargazersCount;
    private int watchersCount;
    private String language;
    private int forksCount;
    private Date createdAt;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public int getWatchersCount() {
        return watchersCount;
    }

    public String getLanguage() {
        return language;
    }

    public int getForksCount() {
        return forksCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
