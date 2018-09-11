package com.example.android.popularmovies2;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

final class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String REVIEWS_PATH = "reviews";
    private static final String TRAILER_PATH = "videos";
    private static final String MOVIE_DB_KEY = "9b8dcf97c30eb1d45558998eda4dddf6";
    private static final String API_KEY = "api_key";
    private static final String POSTER_BASE = "http://image.tmdb.org/t/p/w185";
    private static final String TRAILER_BASE = "https://www.youtube.com/watch?v=";
    private static final String MOVIE_RESULTS = "results";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "original_title";
    private static final String MOVIE_SUMARY = "overview";
    private static final String MOVIE_DATE = "release_date";
    private static final String MOVIE_RATING = "vote_average";
    private static final String MOVIE_POSTER_PATH = "poster_path";

    public static URL movieDbUrl(String path) {

        URL movieUrl = null;
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_KEY, MOVIE_DB_KEY)
                .build();
        try {
            movieUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "URL error");
        }
        return movieUrl;
    }

    public static URL reviewsUrl(String path) {
        URL reviewsUrl = null;
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(API_KEY, MOVIE_DB_KEY)
                .build();
        try {
            reviewsUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "URL error");
        }
        return reviewsUrl;
    }

    public static URL trailerUrl(String path) {

        URL trailerUrl = null;
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendPath(TRAILER_PATH)
                .appendQueryParameter(API_KEY, MOVIE_DB_KEY)
                .build();
        try {
            trailerUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "URL error");
        }
        return trailerUrl;
    }

    public static String getHttpUrlResponse(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = connection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            connection.disconnect();
        }

    }

    public static List<MovieList> movieJson(String jsonResponse) {

        MovieList movieList;
        List<MovieList> movies = new ArrayList<>();
        final int isFavorite = 0;

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.getJSONArray(MOVIE_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject returnedObject = jsonArray.getJSONObject(i);
                int movieId = returnedObject.getInt(MOVIE_ID);
                String movieTitle = returnedObject.getString(MOVIE_TITLE);
                String movieSummary = returnedObject.getString(MOVIE_SUMARY);
                String movieDate = returnedObject.getString(MOVIE_DATE);
                Double movieRating = returnedObject.getDouble(MOVIE_RATING);
                String moviePosterPath = returnedObject.getString(MOVIE_POSTER_PATH);
                String moviePoster = POSTER_BASE + moviePosterPath;
                movieList = new MovieList(movieId, movieTitle, movieSummary, movieDate, movieRating, moviePoster, isFavorite);
                movies.add(movieList);
            }
        } catch (JSONException e) {
            Log.v(LOG_TAG, "JSON error");
        }
        return movies;
    }

    public static List<String> reviewsJson(String jsonResponse) {

        List<String> reviewsArrayList = new ArrayList<>();
        final String CONTENT = "content";

        try {
            JSONObject baseJson = new JSONObject(jsonResponse);
            JSONArray jsonArray = baseJson.getJSONArray(MOVIE_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject reviewsObject = jsonArray.getJSONObject(i);
                String string = reviewsObject.getString(CONTENT);
                reviewsArrayList.add(string);
            }
        } catch (JSONException e) {
            Log.v(LOG_TAG, "JSON error");
        }
        return reviewsArrayList;
    }

    public static String trailerJson(String jsonResponse) {

        String trailerString = null;
        final String KEY = "key";

        try {
            JSONObject baseJson = new JSONObject(jsonResponse);
            JSONArray jsonArray = baseJson.getJSONArray(MOVIE_RESULTS);
            JSONObject trailerJsonObject = jsonArray.getJSONObject(0);
            String string = trailerJsonObject.getString(KEY);
            trailerString = TRAILER_BASE + string;
        } catch (JSONException e) {
            Log.v(LOG_TAG, "JSON error");
        }
        return trailerString;
    }



}