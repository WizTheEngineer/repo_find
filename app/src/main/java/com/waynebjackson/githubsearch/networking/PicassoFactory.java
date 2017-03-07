package com.waynebjackson.githubsearch.networking;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Wayne on 3/6/2017.
 * Factory responsible for providing configured instance of a {@link Picasso} object.
 */
public class PicassoFactory {

    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    private Picasso mPicasso;

    public Picasso getPicasso(Context context) {
        if (mPicasso == null) {
            mPicasso = new Picasso.Builder(context)
                           .downloader(new OkHttp3Downloader(context, CACHE_SIZE))
                           .build();
        }
        return mPicasso;
    }
}
