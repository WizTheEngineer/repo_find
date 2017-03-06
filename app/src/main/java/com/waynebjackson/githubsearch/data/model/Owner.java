package com.waynebjackson.githubsearch.data.model;

/**
 * Created by Wayne on 3/5/2017.
 */

public class Owner {
    private String login;
    private long id;
    private String avatarUrl;
    private String url;

    public String getLogin() {
        return login;
    }

    public long getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUrl() {
        return url;
    }
}
