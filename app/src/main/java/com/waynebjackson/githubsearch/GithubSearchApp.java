package com.waynebjackson.githubsearch;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Wayne on 3/3/2017.
 */

public class GithubSearchApp extends Application {

    public static GithubSearchApp getInstance(@NonNull Context context) {
        checkNotNull(context);
        return ((GithubSearchApp) context.getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
