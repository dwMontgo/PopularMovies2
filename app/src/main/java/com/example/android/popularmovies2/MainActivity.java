package com.example.android.popularmovies2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies2.data.MovieContract;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MovieAdapter.AdapterClickHandler{

//    private static final int FAVORITE_LOADER = 0;
    private static final String SORT_POPULARITY = "popular";
    private static final String SORT_RATING = "top_rated";

    @BindView(R.id.recycler_view_main)
    RecyclerView movieRv;

    private MovieAdapter movieAdapter;
    private List<MovieList> movieList = new ArrayList<>();


    // method used by the FetchMovies class (which is used by moviesByPopularity and moviesByRating methods)
    private static void fetchMovies(List<MovieList> movieList, Context context) {
        ContentValues[] movieArray = new ContentValues[movieList.size()];

        for (int i = 0; i < movieList.size(); i++) {
            MovieList movieItem = movieList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieItem.getMovieId());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieItem.getMovieTitle());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_SUMMARY, movieItem.getMovieSummary());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_DATE, movieItem.getMovieDate());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, movieItem.getMovieRating());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, movieItem.getMoviePosterPath());
            movieArray[i] = cv;
        }

        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieArray);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        movieRv.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this);


        movieRv.setAdapter(movieAdapter);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            movieRv.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            movieRv.setLayoutManager(new GridLayoutManager(this, 4));
        }

        if (savedInstanceState == null) {
            moviesByPopularity();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("movieArrayList", (ArrayList<? extends Parcelable>) movieList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movieList = savedInstanceState.getParcelableArrayList("movieArrayList");
        movieAdapter.mapMovies(movieList);
    }

    @Override
    public void onClick(MovieList movies) {
        Context context = this;
        Class detail = DetailActivity.class;
        Intent intent = new Intent(context, detail);
        Uri selectedUri = MovieContract.MovieEntry.buildMovieUriWithId(movies.getMovieId());
        intent.setData(selectedUri);
        startActivity(intent);
    }


//    //part of loader for favorites
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        final int isFavorite = 1;
//        String[] argsString = new String[]{String.valueOf(isFavorite)};
//        final String[] MOVIE_PROJECTION = {
//                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
//                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
//                MovieContract.MovieEntry.COLUMN_MOVIE_SUMMARY,
//                MovieContract.MovieEntry.COLUMN_MOVIE_DATE,
//                MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
//                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
//                MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE
//
//        };
//        switch (id) {
//            case FAVORITE_LOADER:
//
//                return new CursorLoader(this,
//                        MovieContract.MovieEntry.CONTENT_URI,
//                        MOVIE_PROJECTION,
//                        MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE + " =? ",
//                        argsString,
//                        null);
//            default:
//                throw new RuntimeException("Loader Error");
//
//        }
//    }

//    // part of loader for favorites
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//
//        if (data != null) {
//            while (data.moveToNext()) {
//                int id = data.getInt(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_ID);
//                String title = data.getString(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_TITLE);
//                String summary = data.getString(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_SUMMARY);
//                String date = data.getString(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_DATE);
//                Double rating = data.getDouble(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_RATING);
//                String posterPath = data.getString(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_POSTER);
//                int favorite = data.getInt(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_FAVORITE);
//                MovieList movies = new MovieList(id, title, summary, date, rating, posterPath, favorite);
//                movieList.add(movies);
//            }
//            movieAdapter.mapMovies(movieList);
//        }
//    }

    private void moviesByPopularity() {
        FetchMovies fetch = new FetchMovies();
        fetch.execute(SORT_POPULARITY);
    }

//    private void moviesByRating() {
//        FetchMovies fetch = new FetchMovies();
//        fetch.execute(SORT_RATING);
//    }

//    private void getFavorites() {
//        movieList.clear();
//        getSupportLoaderManager().restartLoader(FAVORITE_LOADER, null, this);
//    }

    // part of loader for favorites
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.popularity_menu_item:
//                moviesByPopularity();
//                return true;
//
//            case R.id.rating_menu_item:
//                moviesByRating();
//                return true;
//
////            case R.id.favorite_menu_item:
////                movieList.clear();
////                getFavorites();
////                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//
//    }

    // used by moviesByPopularity and moviesByRating methods
    class FetchMovies extends AsyncTask<String, Void, List<MovieList>> {
        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected List<MovieList> doInBackground(String... strings) {
            String httpResponse = null;

            URL url = Utils.movieDbUrl(strings[0]);
            try {
                httpResponse = Utils.getHttpUrlResponse(url);
            } catch (IOException e) {
                Log.v(LOG_TAG, "getHttpUrlResponse error");
            }

            movieList = Utils.movieJson(httpResponse);
            fetchMovies(movieList, getBaseContext());
            return movieList;
        }

        @Override
        protected void onPostExecute(List<MovieList> movies) {
            movieAdapter.mapMovies(movies);
            movieRv.setAdapter(movieAdapter);
            movieAdapter.notifyDataSetChanged();
        }
    }

}