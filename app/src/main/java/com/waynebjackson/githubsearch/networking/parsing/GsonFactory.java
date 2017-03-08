package com.waynebjackson.githubsearch.networking.parsing;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Wayne on 3/5/2017.
 * Factory for retrieving a properly configured {@link Gson} instance.
 */
public class GsonFactory {

    public Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Gson gson = gsonBuilder.create();
        return gson;
    }
}
