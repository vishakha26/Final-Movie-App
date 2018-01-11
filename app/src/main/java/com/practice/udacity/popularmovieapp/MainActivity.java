package com.practice.udacity.popularmovieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.practice.udacity.popularmovieapp.common.Constants;
import com.practice.udacity.popularmovieapp.model.Movie;

public class MainActivity extends AppCompatActivity implements MoviesFragment.OnMovieSelectedListener, DetailFragment.OnFavoriteChangeListener{

    public static final String DETAILFRAGMENT_TAG = "DTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.e("MainActivity","Main Activity is about to run");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_detail) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void setActivityTitle(String title){
        setTitle(getString(R.string.main_title) + title);
    }


    public void onMovieSelected(Movie movie){
        if (mTwoPane){
            DetailFragment detailFragment = (DetailFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
            detailFragment.setMovie(movie);

        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(this.getString(R.string.movie_key), movie);
            this.startActivity(intent);

        }


    }

    public void onMovieFavoriteChange() {
        MoviesFragment moviesFragment = (MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
        moviesFragment.updateFavorites();
    }



}
