package com.example.android.popularmovies2.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies2";
    public static final String PATH = "movies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH)
                .build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_SUMMARY = "summary";
        public static final String COLUMN_MOVIE_DATE = "date";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_POSTER = "poster";
        public static final String COLUMN_MOVIE_FAVORITE = "favorite";

        public static Uri buildMovieUriWithId(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }

        public static final int INDEX_COLUMN_MOVIE_ID = 0;
        public static final int INDEX_COLUMN_MOVIE_TITLE = 1;
        public static final int INDEX_COLUMN_MOVIE_SUMMARY = 2;
        public static final int INDEX_COLUMN_MOVIE_DATE = 3;
        public static final int INDEX_COLUMN_MOVIE_RATING = 4;
        public static final int INDEX_COLUMN_MOVIE_POSTER = 5;
        public static final int INDEX_COLUMN_MOVIE_FAVORITE = 6;
    }
}
