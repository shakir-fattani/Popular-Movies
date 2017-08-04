package com.appsys.android.popularmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsys.android.popularmovie.classes.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    TextView mTitleTextView;
    TextView mReleaseTextView;
    TextView mRatingTextView;
    TextView mDurationTextView;
    TextView mOverviewTextView;
    ImageView mPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mTitleTextView = (TextView) findViewById(R.id.detail_title);
        mReleaseTextView = (TextView) findViewById(R.id.detail_release);
//        mDurationTextView = (TextView) findViewById(R.id.detail_duration);
        mRatingTextView = (TextView) findViewById(R.id.rating);
        mOverviewTextView = (TextView) findViewById(R.id.detail_overview);
        mPoster = (ImageView) findViewById(R.id.detail_poster);

        Intent i = getIntent();
        if (i != null && i.hasExtra(Intent.EXTRA_TEXT)) {
            Movie m = i.getParcelableExtra(Intent.EXTRA_TEXT);
            if (m != null) {
                mTitleTextView.setText(m.getTitle());
                Picasso.with(this).load(m.getPoster()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.not_found).into(mPoster);
                mOverviewTextView.setText(m.getOverview());
                mRatingTextView.setText("Rating: " + m.getRating() + "/10");
                mReleaseTextView.setText("Release: " + m.getRelease());
            }
        }

    }
}
