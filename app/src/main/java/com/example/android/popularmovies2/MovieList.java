package com.example.android.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

public final class MovieList implements Parcelable {

    public static final Parcelable.Creator<MovieList> CREATOR = new Parcelable.Creator<MovieList>() {
        @Override
        public MovieList createFromParcel(Parcel source) {
            return new MovieList(source);
        }

        @Override
        public MovieList[] newArray(int size) {
            return new MovieList[size];
        }
    };
    private final int movieId;
    private final String movieTitle;
    private final String movieSummary;
    private final String movieDate;
    private final Double movieRating;
    private final String moviePosterPath;
    private final int isFavorite;


    public MovieList(int id, String title, String summary, String date, double rating, String posterPath, int favorite) {
        movieId = id;
        movieTitle = title;
        movieSummary = summary;
        movieDate = date;
        movieRating = rating;
        moviePosterPath = posterPath;
        isFavorite = favorite;
    }

    private MovieList(Parcel in) {
        this.movieId = in.readInt();
        this.movieTitle = in.readString();
        this.movieSummary = in.readString();
        this.movieDate = in.readString();
        this.movieRating = (Double) in.readValue(Double.class.getClassLoader());
        this.moviePosterPath = in.readString();
        this.isFavorite = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.movieId);
        dest.writeString(this.movieTitle);
        dest.writeString(this.movieSummary);
        dest.writeValue(this.movieRating);
        dest.writeString(this.moviePosterPath);
        dest.writeString(this.movieDate);
        dest.writeInt(this.isFavorite);
    }

    public int getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMovieSummary() {
        return movieSummary;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public Double getMovieRating() {
        return movieRating;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

}