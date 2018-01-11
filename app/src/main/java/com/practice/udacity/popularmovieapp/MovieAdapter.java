package com.practice.udacity.popularmovieapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.practice.udacity.popularmovieapp.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieAdapter extends ArrayAdapter<Movie> {
    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<Movie> mMovies;

    public MovieAdapter(Context c, int layoutResourceId, ArrayList<Movie> movies) {
        super(c, layoutResourceId, movies);
        mContext = c;
        this.mLayoutResourceId = layoutResourceId;
        this.mMovies = movies;
    }


    public int getCount() {
        return mMovies.size();
    }

    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_poster_imageView);
            holder.imageView.setAdjustViewBounds(true);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Movie movie = mMovies.get(position);

        Uri builtUri = Uri.parse("http://image.tmdb.org/t/p/w185" + movie.getPosterPath()).buildUpon()
                .build();
        Picasso.with(mContext)
                .load(builtUri.toString())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .centerCrop()
                .fit()
                .into(holder.imageView);
        return row;
    }

    public void updateData(ArrayList<Movie> newData) {
        if (newData != null){
            mMovies.clear();
            mMovies.addAll(newData);
            notifyDataSetChanged();
        }


    }

    static class ViewHolder {
        ImageView imageView;
    }
}