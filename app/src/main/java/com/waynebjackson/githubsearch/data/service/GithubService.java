package com.waynebjackson.githubsearch.data.service;

import com.waynebjackson.githubsearch.data.model.RepoCollection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Wayne on 3/3/2017.
 * Service for handling requests made to the GitHub API.
 */
public interface GithubService {
    String BASE_URL = "https://api.github.com/";

    @GET("search/repositories")
    Call<RepoCollection> getRepos(@Query("q") String query);
}
