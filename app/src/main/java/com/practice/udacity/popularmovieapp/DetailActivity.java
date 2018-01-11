package com.practice.udacity.popularmovieapp;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.practice.udacity.popularmovieapp.model.Movie;

public class DetailActivity extends AppCompatActivity implements DetailFragment.OnFavoriteChangeListener {
    Movie mMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mMovie = (Movie)getIntent().getParcelableExtra(this.getString(R.string.movie_key));
        DetailFragment fragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
        fragment.setMovie(mMovie);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mMovie.getTitle());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onMovieFavoriteChange() {


    }
}
