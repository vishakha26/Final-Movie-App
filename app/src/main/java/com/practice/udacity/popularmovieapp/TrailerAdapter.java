package com.practice.udacity.popularmovieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.practice.udacity.popularmovieapp.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{

    private ArrayList<Trailer> mTrailers;
    private int mRowLayout;
    private Context mContext;

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers, int rowLayout) {
        this.mTrailers = trailers;
        this.mRowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Trailer trailer = mTrailers.get(i);
        viewHolder.setContext(mContext);
        viewHolder.setTrailer(trailer);

        viewHolder.name.setText(trailer.getName());
        Uri url = getImageUrl(trailer.getKey(), i);
        Log.d("TEST", url.toString());
        Picasso.with(mContext)
                .load(url.toString())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .centerCrop()
                .fit()
                .into(viewHolder.image);
    }

    private Uri getImageUrl(String key, int i) {
        final String BASE_URL =
                "https://img.youtube.com/vi/" + key + "/" + i +".jpg";

        return Uri.parse(BASE_URL).buildUpon()
                .build();
    }

    @Override
    public int getItemCount() {
        return mTrailers == null ? 0 : mTrailers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public ImageView image;
        Trailer mTrailer;
        Context mContext;



        public void setTrailer(Trailer trailer){
            mTrailer = trailer;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.list_item_trailer_text_view);
            image = (ImageView)itemView.findViewById(R.id.list_item_trailer_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + mTrailer.getKey())));
        }

        public void setContext(Context context) {
            this.mContext = context;
        }
    }
}


