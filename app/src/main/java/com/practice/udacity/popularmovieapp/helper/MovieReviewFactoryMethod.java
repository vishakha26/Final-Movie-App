package com.practice.udacity.popularmovieapp.helper;

import com.practice.udacity.popularmovieapp.model.Review;
import com.practice.udacity.popularmovieapp.model.Trailer;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieReviewFactoryMethod {
    public static final String ID = "id";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";
    public static final String URL = "url";

    public static Review create (JSONObject json) throws JSONException {
        String id = json.getString(ID);
        String author = json.getString(AUTHOR);
        String content = json.getString(CONTENT);
        String url = json.getString(URL);

        return new Review(id, author, content, url);
    }
}
