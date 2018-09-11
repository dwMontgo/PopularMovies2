package com.example.android.popularmovies2;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.AdapterViewHolder> {

    private final AdapterClickHandler onClickHandler;
    private Context context;
    private List<MovieList> movies;

    public MovieAdapter(AdapterClickHandler adapterClickHandler) {
        onClickHandler = adapterClickHandler;
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int item = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(item, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        MovieList movies = this.movies.get(position);
        Uri uri = Uri.parse(movies.getMoviePosterPath());

        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.loading)
                .into(holder.posterHolder);
    }

    @Override
    public int getItemCount() {
        if (movies == null) return 0;
        return movies.size();
    }

    public void mapMovies(List<MovieList> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public interface AdapterClickHandler {
        void onClick(MovieList movies);
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView posterHolder;

        AdapterViewHolder(View view) {
            super(view);
            posterHolder = view.findViewById(R.id.poster_image_view);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MovieList movies = MovieAdapter.this.movies.get(position);
            onClickHandler.onClick(movies);
        }
    }
}
