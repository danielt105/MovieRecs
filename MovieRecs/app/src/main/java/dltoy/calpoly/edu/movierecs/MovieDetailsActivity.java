package dltoy.calpoly.edu.movierecs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dltoy.calpoly.edu.movierecs.Api.ImageUtil;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String MOVIE_ID_EXTRA = "MOVIE_ID_EXTRA";
    private Movie model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        setTitle(getString(R.string.movie_details_title));

        int id = getIntent().getIntExtra(MOVIE_ID_EXTRA, 0);
        getMovieData(id);
    }

    private void getMovieData(int id) {
        MainActivity.apiService.getById(id, BuildConfig.apiKey)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Movie>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Movie movie) {
                        model = movie;
                        setUpUi(movie);
                    }
                });
    }

    private void setUpUi(final Movie movie) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageUtil.insertImage(movie.getImagePath(), 400, ((ImageView) findViewById(R.id.details_poster)));
                TextView title = (TextView) findViewById(R.id.details_title);
                title.setText(movie.getTitle());

                TextView rating = (TextView) findViewById(R.id.details_rating);
                rating.setText(ImageUtil.starIcon + movie.getRating());

                Button btn = (Button) findViewById(R.id.details_add);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.db.addMovie(movie);
                        Toast.makeText(MovieDetailsActivity.this, "Added to watchlist", Toast.LENGTH_SHORT).show();
                    }
                });

                ((TextView) findViewById(R.id.details_desc)).setText(movie.getDescription());
            }
        });
    }
}
