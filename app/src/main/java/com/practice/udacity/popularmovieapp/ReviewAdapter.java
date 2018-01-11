package com.practice.udacity.popularmovieapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.practice.udacity.popularmovieapp.model.Review;

import java.util.ArrayList;

public class ReviewAdapter extends ArrayAdapter<Review>{

    private final Context mContext;
    private ArrayList<Review> mReviews;
    private int mRowLayout;

    public ReviewAdapter(Context context, int resource, ArrayList<Review> reviews) {
        super(context, resource);
        this.mContext = context;
        this.mRowLayout = resource;
        this.mReviews = reviews;
    }

    public int getCount() {
        return mReviews.size();
    }

    public Review getItem(int position) {
        return mReviews.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mRowLayout, parent, false);
            holder = new ViewHolder();
            holder.author = (TextView)row.findViewById(R.id.list_item_review_author_text_view);
            holder.review = (TextView)row.findViewById(R.id.list_item_review_text_view);
            row.setTag(holder);
        } else{
            holder = (ViewHolder) row.getTag();
        }

        Review review = mReviews.get(position);
        holder.author.setText(review.getAuthor());
        holder.review.setText(review.getContent());
        return row;
    }


    public static class ViewHolder{
        public TextView author;
        public TextView review;

    }
}


