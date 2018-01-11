package com.practice.udacity.popularmovieapp.service;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.practice.udacity.popularmovieapp.data.MovieContract;
import com.practice.udacity.popularmovieapp.model.Movie;
import com.practice.udacity.popularmovieapp.model.Review;
import com.practice.udacity.popularmovieapp.model.Trailer;

import java.util.ArrayList;

public class MovieService {

    private final String LOG_TAG = MovieService.class.getSimpleName();

    private static final int INDEX_COLUMN_ID = 0;
    private static final int INDEX_COLUMN_TITLE = 1;
    private static final int INDEX_COLUMN_PATH = 2;
    private static final int INDEX_COLUMN_PLOT = 3;
    private static final int INDEX_COLUMN_VOTE = 4;
    private static final int INDEX_COLUMN_DATE = 5;

    private static final int INDEX_COLUMN_NAME = 0;
    private static final int INDEX_COLUMN_KEY = 1;

    private static final int INDEX_COLUMN_AUTHOR = 0;
    private static final int INDEX_COLUMN_CONTENT = 1;


    private Context mContext;

    public MovieService(Context context){

        mContext = context;
    }

    public long addMovie(Movie movie) {
        long movieId;

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{movie.getId()},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            movieId = movieCursor.getLong(movieIdIndex);
        } else {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry._ID, movie.getId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, movie.getPlotSynopsis());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE, movie.getVoteAverage());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());


            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );

            movieId = ContentUris.parseId(insertedUri);
            
            for (Trailer trailer:movie.getTrailers()) {
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, trailer.getKey());
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, trailer.getName());
                Uri trailerInsertedUri = mContext.getContentResolver().insert(
                        MovieContract.TrailerEntry.CONTENT_URI,
                        trailerValues
                );
            }
            for (Review review:movie.getReviews()) {
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
                Uri reviewInsertedUri = mContext.getContentResolver().insert(
                        MovieContract.ReviewEntry.CONTENT_URI,
                        reviewValues
                );
            }

        }

        movieCursor.close();

        return movieId;
    }

    public long deleteMovie(Movie movie) {
        int rowsDeleted = 0;

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{movie.getId()},
                null);

        if (movieCursor.moveToFirst()) {
            rowsDeleted = mContext.getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry._ID.toString() + " = ?",
                    new String[]{movie.getId()}

            );

        }
        movieCursor.close();
        return rowsDeleted;
    }

    public ArrayList<Movie> getMovies() {
        ArrayList<Movie> result = new ArrayList<>();

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_TITLE, MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                        MovieContract.MovieEntry.COLUMN_PLOT, MovieContract.MovieEntry.COLUMN_VOTE, MovieContract.MovieEntry.COLUMN_RELEASE_DATE},
                null,
                null, null
        );

        while (movieCursor.moveToNext()){
            String id = movieCursor.getString(INDEX_COLUMN_ID);
            String title = movieCursor.getString(INDEX_COLUMN_TITLE);
            String path = movieCursor.getString(INDEX_COLUMN_PATH);
            String plot = movieCursor.getString(INDEX_COLUMN_PLOT);
            Double vote = movieCursor.getDouble(INDEX_COLUMN_VOTE);
            String date = movieCursor.getString(INDEX_COLUMN_DATE);
            Movie movie = new Movie(id, title, path, plot, vote, date);

            Cursor trailerCursor = mContext.getContentResolver().query(
                    MovieContract.TrailerEntry.CONTENT_URI,
                    new String[]{MovieContract.TrailerEntry.COLUMN_NAME, MovieContract.TrailerEntry.COLUMN_KEY},
                    MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{movie.getId()}, null
            );

            while (trailerCursor.moveToNext()) {
                String key = trailerCursor.getString(INDEX_COLUMN_KEY);
                String name = trailerCursor.getString(INDEX_COLUMN_NAME);
                movie.addTrailer(new Trailer(key, name));
            }
            trailerCursor.close();

            Cursor reviewCursor = mContext.getContentResolver().query(
                    MovieContract.ReviewEntry.CONTENT_URI,
                    new String[]{MovieContract.ReviewEntry.COLUMN_AUTHOR, MovieContract.ReviewEntry.COLUMN_CONTENT},
                    MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{movie.getId()}, null
            );

            while (reviewCursor.moveToNext()) {
                String author = reviewCursor.getString(INDEX_COLUMN_AUTHOR);
                String content = reviewCursor.getString(INDEX_COLUMN_CONTENT);
                movie.addReview(new Review(author, content));
            }
            reviewCursor.close();

            result.add(movie);
        }

        movieCursor.close();
        return result;
    }

    public boolean getFavorite(Movie movie){
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{movie.getId()},
                null, null
        );
        boolean result = movieCursor.moveToFirst();
        Log.d(LOG_TAG, "getFavorite " + result);
        movieCursor.close();
        return result;

    }
}
