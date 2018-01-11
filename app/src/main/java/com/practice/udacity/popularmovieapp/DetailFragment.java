package com.practice.udacity.popularmovieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.udacity.popularmovieapp.common.Constants;
import com.practice.udacity.popularmovieapp.common.Utility;
import com.practice.udacity.popularmovieapp.helper.MovieReviewFactoryMethod;
import com.practice.udacity.popularmovieapp.helper.MovieTrailerFactoryMethod;
import com.practice.udacity.popularmovieapp.model.Movie;
import com.practice.udacity.popularmovieapp.model.Review;
import com.practice.udacity.popularmovieapp.model.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.practice.udacity.popularmovieapp.service.MovieService;

public class DetailFragment extends Fragment {

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMovies";
    private static final String MOVIE_KEY = "movie";
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private View mRootView;
    private Movie mMovie;
    ViewHolder mViewHolder;
    private boolean mError;
    private String mErrorMessage;
    com.practice.udacity.popularmovieapp.TrailerAdapter mTrailerAdapter;
    com.practice.udacity.popularmovieapp.ReviewAdapter mReviewAdapter;
    FloatingActionButton mFab;
    MovieService mMovieService;
    ShareActionProvider mShareActionProvider;

    OnFavoriteChangeListener mCallback;


    public DetailFragment() {
    }

    public interface OnFavoriteChangeListener{
        public void onMovieFavoriteChange();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnFavoriteChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFavoriteChangeListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
        {
            mMovie = (Movie) savedInstanceState.get(MOVIE_KEY);
        }
        mMovieService = new MovieService(getActivity());
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mFab = (FloatingActionButton) mRootView.findViewById(R.id.favorite_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMovie.isFavorite()) {
                    mFab.setImageResource(R.drawable.ic_add_favorite);
                    mMovie.setFavorite(false);
                    mMovieService.deleteMovie(mMovie);
                    mCallback.onMovieFavoriteChange();
                } else {
                    mFab.setImageResource(R.drawable.ic_remove_favorite);
                    mMovie.setFavorite(true);
                    mMovieService.addMovie(mMovie);
                    mCallback.onMovieFavoriteChange();
                }
            }
        });

        NestedScrollView scrollView = (NestedScrollView)mRootView.findViewById(R.id.detail_nested_scrool_view);
        if (mMovie != null){
            scrollView.setVisibility(View.VISIBLE);
            initializeView();
        } else{
            scrollView.setVisibility(View.GONE);
        }

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_KEY, mMovie);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateTrailersAndReviews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null && mMovie != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMovie(Movie movie){
        this.mMovie = movie;

        NestedScrollView scrollView = (NestedScrollView)mRootView.findViewById(R.id.detail_nested_scrool_view);
        if (mMovie != null){
            scrollView.setVisibility(View.VISIBLE);
            initializeView();
        } else{
            scrollView.setVisibility(View.GONE);
        }

    }

    private void initializeView() {
        createViewHolder();

        initializeFavoriteIcon();
        initializeTrailers();
        initializeReviews();

        setMovieAdditionalInfo();

        mViewHolder.trailersRecyclerView.setAdapter(mTrailerAdapter);
        mViewHolder.reviewsListView.setAdapter(mReviewAdapter);
        populateTrailersAndReviews();

        updateDetails();
        loadImages();
        if (mShareActionProvider != null && mMovie != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
    }

    private void initializeFavoriteIcon() {
        if (mMovie.isFavorite()) {
            mFab.setImageResource(R.drawable.ic_remove_favorite);
        } else {
            mFab.setImageResource(R.drawable.ic_add_favorite);
        }

        }

    private void initializeTrailers() {
        mTrailerAdapter = new com.practice.udacity.popularmovieapp.TrailerAdapter(getActivity(), mMovie.getTrailers(), R.layout.list_item_trailer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mViewHolder.trailersRecyclerView = (RecyclerView)mRootView.findViewById(R.id.trailers_recycler_view);

        mViewHolder.trailersRecyclerView.setLayoutManager(linearLayoutManager);
        mViewHolder.trailersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mViewHolder.trailersRecyclerView.setHasFixedSize(true);
    }

    private void initializeReviews() {
        mReviewAdapter = new com.practice.udacity.popularmovieapp.ReviewAdapter(getActivity(), R.layout.list_item_review, mMovie.getReviews());
        mViewHolder.reviewsListView = (ListView)mRootView.findViewById(R.id.reviews_list_view);
        View empty = mRootView.findViewById(R.id.empty);
        mViewHolder.reviewsListView.setEmptyView(empty);
    }

    private void populateTrailersAndReviews() {
        if (mTrailerAdapter != null){
            mTrailerAdapter.notifyDataSetChanged();
        }
        if (mReviewAdapter != null) {
            mReviewAdapter.notifyDataSetChanged();
        }
    }

    private void setMovieAdditionalInfo() {
        if (!Utility.isNetworkAvailable(getActivity())) {
            return;
        }
        if (!mMovie.isFavorite()) {
            FetchMovieInfoTask movieInfoTask = new FetchMovieInfoTask();
            movieInfoTask.execute();
        }
    }

    private void createViewHolder() {
        mViewHolder = new ViewHolder();
        mViewHolder.titleTextView = (TextView)mRootView.findViewById(R.id.movie_title_textview);
        mViewHolder.plotSynopsisTextView = (TextView)mRootView.findViewById(R.id.plot_synopsis_textview);
        mViewHolder.releaseDateTextView = (TextView)mRootView.findViewById(R.id.release_date_textview);
        mViewHolder.userRatingBar = (RatingBar)mRootView.findViewById(R.id.user_rating_bar);
        mViewHolder.trailersRecyclerView = (RecyclerView)mRootView.findViewById(R.id.trailers_recycler_view);
        mViewHolder.reviewsListView = (ListView)mRootView.findViewById(R.id.reviews_list_view);
    }

    private void updateDetails() {
        mViewHolder.titleTextView.setText(mMovie.getTitle());
        mViewHolder.plotSynopsisTextView.setText(this.getText(R.string.not_available));
        mViewHolder.releaseDateTextView.setText(this.getText(R.string.not_available));
        if (mMovie.getPlotSynopsis() != null && !mMovie.getPlotSynopsis().equals("null")) {
            mViewHolder.plotSynopsisTextView.setText(mMovie.getPlotSynopsis());
        }
        if (mMovie.getReleaseDate() != null && !mMovie.getReleaseDate().equals("null")) {
            mViewHolder.releaseDateTextView.setText(mMovie.getReleaseDate());
        }
        mViewHolder.userRatingBar.setRating(mMovie.getVoteAverage().floatValue() / 2);
    }

    private void loadImages() {
        final ImageView imageView = (ImageView)mRootView.findViewById(R.id.movie_poster);
        Uri builtUri = Uri.parse("http://image.tmdb.org/t/p/w185" + mMovie.getPosterPath()).buildUpon()
                .build();
        Picasso.with(getActivity()).load(builtUri.toString()).into(imageView);

    }


    public class FetchMovieInfoTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();

        public FetchMovieInfoTask() {
        }


        private ArrayList<Trailer> getMovieTrailersFromJson(String jsonString)
                throws JSONException {

            final String MDB_RESULTS = "results";

            ArrayList<Trailer> results = new ArrayList<>();

            Log.v(LOG_TAG, jsonString);

            try {
                JSONObject trailersJson = new JSONObject(jsonString);
                JSONArray trailerArray = trailersJson.getJSONArray(MDB_RESULTS);

                for(int i = 0; i < trailerArray.length(); i++) {

                    JSONObject trailer = trailerArray.getJSONObject(i);

                    results.add(MovieTrailerFactoryMethod.create(trailer));
                }

                Log.d(LOG_TAG, "Complete. " + trailerArray.length() + " Trailers Fetched");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return results;
        }

        private ArrayList<Review> getMovieReviewsFromJson(String jsonString)
                throws JSONException {

            final String MDB_RESULTS = "results";

            ArrayList<Review> results = new ArrayList<>();

            Log.v(LOG_TAG, jsonString);

            try {
                JSONObject reviewsJson = new JSONObject(jsonString);
                JSONArray reviewArray = reviewsJson.getJSONArray(MDB_RESULTS);

                for(int i = 0; i < reviewArray.length(); i++) {

                    JSONObject review = reviewArray.getJSONObject(i);

                    results.add(MovieReviewFactoryMethod.create(review));
                }

                Log.d(LOG_TAG, "Complete. " + reviewArray.length() + " Reviews Fetched");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected Void doInBackground(String... params) {

            mError = false;
            mErrorMessage = "";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if (getTrailers(urlConnection, reader)) return null;
            if (getReviews(urlConnection, reader)) return null;
            return null;
        }

        private boolean getTrailers(HttpURLConnection urlConnection, BufferedReader reader) {
            String jsonString;
            try {
                URL url = new URL(getTrailersUri(mMovie.getId()).toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return true;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return true;
                }
                jsonString = buffer.toString();
                mMovie.addTrailers(getMovieTrailersFromJson(jsonString));
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                mError = true;
                mErrorMessage = e.getMessage();
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                mError = true;
                mErrorMessage = e.getMessage();
            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return false;
        }

        private boolean getReviews(HttpURLConnection urlConnection, BufferedReader reader) {
            String jsonString;
            try {
                URL url = new URL(getReviewsUri(mMovie.getId()).toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return true;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return true;
                }
                jsonString = buffer.toString();
                mMovie.addReviews(getMovieReviewsFromJson(jsonString));
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                mError = true;
                mErrorMessage = e.getMessage();
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                mError = true;
                mErrorMessage = e.getMessage();
            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return false;
        }

        private Uri getTrailersUri(String id) {
            final String BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+ id +"/videos?";
            final String API_KEY_PARAM = "api_key";

            return Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Constants.API_KEY)
                    .build();
        }

        private Uri getReviewsUri(String id) {
            final String BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+ id +"/reviews?";
            final String API_KEY_PARAM = "api_key";

            return Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Constants.API_KEY)
                    .build();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if (mError){
                showErrorMessage(mErrorMessage);
                return;
            }
            populateTrailersAndReviews();
        }
    }

    private Intent createShareTrailerIntent() {
        Trailer firstTrailer = mMovie.trailersSize() > 0 ?mMovie.getTrailers().get(0) : null;
        String textToShare = firstTrailer != null ? getTrailerLink(firstTrailer) : "ups!(no trailer)";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch the " + mMovie.getTitle() + "'s trailer " + textToShare + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }

    private String getTrailerLink(Trailer trailer){
        return Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()).toString();
    }

    private void showErrorMessage(String errorMessage) {

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getActivity(), errorMessage, duration);
        toast.show();
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView plotSynopsisTextView;
        TextView releaseDateTextView;
        RatingBar userRatingBar;
        RecyclerView trailersRecyclerView;
        ListView reviewsListView;
    }
}
