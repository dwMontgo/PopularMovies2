package com.example.android.popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies2.data.MovieContract.MovieEntry;

class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 3;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "( "
                + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"
                + MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL,"
                + MovieEntry.COLUMN_MOVIE_SUMMARY + " TEXT NOT NULL,"
                + MovieEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL,"
                + MovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL,"
                + MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL,"
                + MovieEntry.COLUMN_MOVIE_FAVORITE + " INTEGER NOT NULL DEFAULT 0, "
                + "UNIQUE ( " + MovieEntry.COLUMN_MOVIE_ID + " ) ON CONFLICT IGNORE"
                + ");";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
