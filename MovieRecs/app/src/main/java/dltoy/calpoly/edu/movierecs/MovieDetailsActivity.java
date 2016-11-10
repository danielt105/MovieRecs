package dltoy.calpoly.edu.movierecs;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import dltoy.calpoly.edu.movierecs.Api.ImageUtil;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String MOVIE_ID_EXTRA = "MOVIE_ID_EXTRA";
    private Movie model;
    private int fragToReturnTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        setTitle(getString(R.string.movie_details_title));

        int id = getIntent().getIntExtra(MOVIE_ID_EXTRA, 0);
        getMovieData(id);

        fragToReturnTo = getIntent().getIntExtra(MainActivity.CUR_FRAG_KEY, R.id.home);
        Log.e("got", fragToReturnTo + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("setting", "intent " + fragToReturnTo);
        Intent intent = new Intent();
        intent.putExtra(MainActivity.CUR_FRAG_KEY, fragToReturnTo);
        setResult(MainActivity.PREV_FRAG, intent);
        finishActivity(MainActivity.PREV_FRAG_KEY);
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
                TextView title = (TextView) findViewById(R.id.details_title);
                title.setText(movie.getTitle());

                TextView rating = (TextView) findViewById(R.id.details_rating);
                rating.setText(ImageUtil.STAR_ICON + movie.getRating());

                ((TextView) findViewById(R.id.details_desc)).setText(movie.getDescription());

                Button btn = (Button) findViewById(R.id.details_add);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.db.addMovie(movie);
                        Toast.makeText(MovieDetailsActivity.this, "Added to watchlist", Toast.LENGTH_SHORT).show();
                    }
                });

                ImageView imageView = (ImageView) findViewById(R.id.details_poster);
                Picasso.with(MovieDetailsActivity.this).load(ImageUtil.createImageURL(movie.getImagePath(), 300)).into(imageView);
            }
        });
    }

    private void setupTheme() {
        int curTheme = PreferenceManager.getDefaultSharedPreferences(this).getInt(MainActivity.THEME_KEY, 0);
        switch (curTheme) {
            case 2:
                setTheme(R.style.ReturnOfCruGold);
                break;
            case 3:
                setTheme(R.style.Outdoorsy);
                break;
            case 4:
                setTheme(R.style.IceIceBaby);
                break;
            case 5:
                setTheme(R.style.UnderstatedVersatile);
                break;
            default:
                setTheme(R.style.AppTheme);
        }
    }
}
