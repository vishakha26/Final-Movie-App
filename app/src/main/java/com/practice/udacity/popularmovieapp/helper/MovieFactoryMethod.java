package com.practice.udacity.popularmovieapp.helper;

import com.practice.udacity.popularmovieapp.model.Movie;

import org.json.JSONException;
import org.json.JSONObject;


public class MovieFactoryMethod {
    public static final String ID = "id";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String OVERVIEW = "overview";
    public static final String POSTER_PATH = "poster_path";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String RELEASE_DATE = "release_date";

    public static Movie create (JSONObject json) throws JSONException {
        String id = json.getString(ID);
        String title = json.getString(ORIGINAL_TITLE);
        String plot = json.getString(OVERVIEW);
        String path = json.getString(POSTER_PATH);
        Double vote = json.getDouble(VOTE_AVERAGE);
        String date = json.getString(RELEASE_DATE);

        return new Movie(id, title, path, plot, vote, date);
    }
}
