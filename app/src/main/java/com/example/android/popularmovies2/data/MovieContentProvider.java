package com.example.android.popularmovies2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MovieContentProvider extends ContentProvider {

    private static final int MOVIE_DATA = 100;
    private static final int MOVIE_DATA_WITH_ID = 101;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String contentAuthority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(contentAuthority, MovieContract.PATH, MOVIE_DATA);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH + "/#", MOVIE_DATA_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {

            case MOVIE_DATA: {
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case MOVIE_DATA_WITH_ID: {

                String movieId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieId};
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Curor error");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] cv) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case MOVIE_DATA:
                db.beginTransaction();
                int i = 0;
                try {
                    for (ContentValues value : cv) {
                        long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                                null,
                                value);
                        if (id != -1) {
                            i++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (i > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return i;
            default:
                return super.bulkInsert(uri, cv);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues cv) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues cv, String selection,
                      String[] selectionArgs) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        String movie = uri.getLastPathSegment();
        String[] selectionArguments = new String[]{movie};
        int i = 0;

        switch (uriMatcher.match(uri)) {
            case MOVIE_DATA_WITH_ID:
                db.beginTransaction();
                try {
                    i = db.update(MovieContract.MovieEntry.TABLE_NAME,
                            cv,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =? ",
                            selectionArguments);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (i != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
        }
        return i;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }


}