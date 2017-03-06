package com.waynebjackson.githubsearch.data.service;

import com.google.gson.Gson;
import com.waynebjackson.githubsearch.networking.parsing.GsonFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Wayne on 3/5/2017.
 * Factory for generating an instance of the {@link GithubService}.
 */
public class GithubServiceFactory {
    public GithubService getGithubService() {
        Gson gson = new GsonFactory().getGson();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(GithubService.class);
    }
}
