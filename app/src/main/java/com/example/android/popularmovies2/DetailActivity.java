package com.example.android.popularmovies2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies2.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION = {
        MovieContract.MovieEntry.COLUMN_MOVIE_ID,
        MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
        MovieContract.MovieEntry.COLUMN_MOVIE_SUMMARY,
        MovieContract.MovieEntry.COLUMN_MOVIE_DATE,
        MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
        MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
        MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE
    };
    private static final int LOADER_INT = 200;
    @BindView(R.id.detail_scroll_view)
    ScrollView detailSv;

    @BindView(R.id.title_text_view)
    TextView title;

    @BindView(R.id.summary_text_view)
    TextView summary;

    @BindView(R.id.date_text_view)
    TextView date;

    @BindView(R.id.rating_text_view)
    TextView rating;

    @BindView(R.id.image_view_detail)
    ImageView posterPath;

    @BindView(R.id.favorite_button)
    Button favoriteButton;

    @BindView(R.id.reviews_label_text_view)
    TextView reviewsHeader;

    @BindView(R.id.reviews_text_view)
    TextView reviews;

    @BindView(R.id.trailers_button)
    Button trailerButton;

    private ArrayList<MovieList> movieList = new ArrayList<>();
    private Uri movieUri;
    private String trailerPath = "";
    private String reviewHolder = "";
    private int favorite;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        final Intent receivedMovieIntent = getIntent();
        movieUri = receivedMovieIntent.getData();
        final String movieId = movieUri.getLastPathSegment();

        new ReviewsAsyncTask().execute(movieId);
        new TrailerAsyncTask().execute(movieId);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FavoriteAsyncTask().execute();
                Toast.makeText(getApplicationContext(), R.string.favorite_toast, Toast.LENGTH_SHORT).show();
            }
        });
        trailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchTrailer(trailerPath);
            }
        });

        getSupportLoaderManager().initLoader(LOADER_INT, null, this).forceLoad();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("MovieList", movieList);
        outState.putString("Reviews", reviewHolder);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_INT:
                return new CursorLoader(this,
                        movieUri,
                        PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("CursorLoader Error");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        cursor.moveToFirst();
        int mId = cursor.getInt(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_ID);
        String mTitle = cursor.getString(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_TITLE);
        String mSummary = cursor.getString(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_SUMMARY);
        String mDate = cursor.getString(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_DATE);
        Double mRating = cursor.getDouble(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_RATING);
        String mPosterPath = cursor.getString(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_POSTER);
        favorite = cursor.getInt(MovieContract.MovieEntry.INDEX_COLUMN_MOVIE_FAVORITE);

        MovieList movies = new MovieList(mId, mTitle, mSummary, mDate, mRating, mPosterPath, favorite);
        movieList.add(movies);
        showData(movieList);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void launchTrailer(String string) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(string));
        Intent chooser = Intent.createChooser(intent, "Choose App to Watch");
        startActivity(chooser);
        }

    private void showData(ArrayList<MovieList> movies) {
        for (int i = 0; i < movies.size(); i++) {
            MovieList selection = movies.get(i);
            Uri uri = Uri.parse(selection.getMoviePosterPath());

            title.setText(selection.getMovieTitle());
            rating.setText(String.valueOf(selection.getMovieRating()));
            summary.setText(selection.getMovieSummary());
            date.setText(selection.getMovieDate());

            Picasso.with(this)
                    .load(uri)
                    .placeholder(R.drawable.loading)
                    .into(posterPath);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        movieList = savedInstanceState.getParcelableArrayList("MovieList");
        reviewHolder = savedInstanceState.getString("Reviews");
        originalState(movieList, reviewHolder);

    }

    private void originalState(ArrayList<MovieList> movies, String reviewsString) {

        for (int i = 0; i < movies.size(); i++) {
            MovieList selection = movies.get(i);
            Uri posterPath = Uri.parse(selection.getMoviePosterPath());

            title.setText(selection.getMovieTitle());
            rating.setText(String.valueOf(selection.getMovieRating()));
            summary.setText(selection.getMovieSummary());
            date.setText(selection.getMovieDate());
            Picasso.with(this)
                    .load(posterPath)
                    .placeholder(R.drawable.loading)
                    .into(this.posterPath);

            reviews.setText(reviewsString);
        }
    }

    private static void updateFavorites(Context context, Uri uri, int favorite) {

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, favorite);
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.update(uri, cv, null, null);
    }

    class FavoriteAsyncTask extends AsyncTask<Void, Void, Void> {

        private int favoriteInt = 0;


        @Override
        protected Void doInBackground(Void... voids) {
            Context context = getApplicationContext();

            if (favorite > 0) {
                updateFavorites(context, movieUri, favoriteInt);

            } else {
                favoriteInt = 1;
                updateFavorites(context, movieUri, favoriteInt);
            }
            return null;
        }
    }

    class TrailerAsyncTask extends AsyncTask<String, Void, URL> {

        @Override
        protected URL doInBackground(String... strings) {
            String response = null;
            URL url = Utils.trailerUrl(strings[0]);
            try {
                response = Utils.getHttpUrlResponse(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            trailerPath = Utils.trailerJson(response);
            return null;
        }
    }

    class ReviewsAsyncTask extends AsyncTask<String, Void, List<String>> {

        List<String> reviewsList = new ArrayList<>();

        @Override
        protected List<String> doInBackground(String... strings) {
            String response = null;
            URL url = Utils.reviewsUrl(strings[0]);
            try {
                response = Utils.getHttpUrlResponse(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            reviewsList = Utils.reviewsJson(response);
            return reviewsList;
        }

        @Override
        protected void onPostExecute(List<String> reviewStrings) {

            String reviews = "No reviews available";
            StringBuilder reviewBuilder = new StringBuilder();
            if (reviewStrings.size() > 0) {
                for (int i = 0; reviewStrings.size() > i; i++) {
                    reviews = reviewStrings.get(i);
                    reviewBuilder.append(reviews);


                }
                reviewHolder = reviewBuilder.toString();
                DetailActivity.this.reviews.setText(reviewBuilder);
                DetailActivity.this.reviews.setVisibility(View.VISIBLE);
            } else {
                reviewHolder = reviews;
                DetailActivity.this.reviews.setText(reviews);
                DetailActivity.this.reviews.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(reviewStrings);
        }
    }


}