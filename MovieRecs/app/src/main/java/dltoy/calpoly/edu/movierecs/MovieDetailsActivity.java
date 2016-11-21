package dltoy.calpoly.edu.movierecs;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.ImageUtil;
import dltoy.calpoly.edu.movierecs.Api.Models.Genre;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Fragments.GridFragment;
import dltoy.calpoly.edu.movierecs.Fragments.grid_recycler.QueryType;
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

        fragToReturnTo = getIntent().getIntExtra(Constants.CUR_FRAG_KEY, R.id.home);
        Log.e("got", fragToReturnTo + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("setting", "intent " + fragToReturnTo);
        Intent intent = new Intent();
        intent.putExtra(Constants.CUR_FRAG_KEY, fragToReturnTo);
        setResult(Constants.PREV_FRAG, intent);
        finishActivity(Constants.PREV_FRAG_KEY);
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
                ((TextView) findViewById(R.id.details_genre)).setText("Genre: " + getGenreString(movie.getGenres()));
                ((TextView) findViewById(R.id.details_release)).setText(getString(R.string.release_lable) + movie.getDate());
                ((TextView) findViewById(R.id.details_runtime)).setText("Length: " + movie.getRuntime() + " minutes");

                final Button btn = (Button) findViewById(R.id.details_add);
                btn.setCompoundDrawablesWithIntrinsicBounds(MainActivity.db.containsMovie(movie) ? R.drawable.minus : R.drawable.add, 0, 0, 0);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInList = MainActivity.db.containsMovie(movie);

                        if (isInList) {
                            MainActivity.db.deleteMovieById(movie.getId());
                        } else {
                            MainActivity.db.addMovie(movie);
                        }

                        btn.setCompoundDrawablesWithIntrinsicBounds(isInList ? R.drawable.add : R.drawable.minus, 0, 0, 0);
                        String msg = isInList ? " removed from watchlist" : " added to watchlist";

                        Snackbar snackbar = Snackbar.make(v, movie.getTitle() + msg, Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(ContextCompat
                                        .getColor(MovieDetailsActivity.this, R.color.colorPrimary));
                        snackbar.show();
                    }
                });

                // set the the movie poster
                ImageView imageView = (ImageView) findViewById(R.id.details_poster);
                Picasso.with(MovieDetailsActivity.this).load(ImageUtil.createImageURL(movie.getImagePath(), 300)).into(imageView);

                // set recommendations for the movie
                GridFragment gf = new GridFragment();
                Bundle bundle = new Bundle();

                bundle.putInt(QueryType.QUERY_TYPE, QueryType.QUERY_RECS);
                bundle.putInt(QueryType.QUERY_MOVIE_ID, movie.getId());
                bundle.putBoolean(GridFragment.USE_HORIZONTAL, true);
                bundle.putInt(GridFragment.SPAN_COUNT, GridFragment.DEFAULT_HORIZ_SPAN_COUNT);
                gf.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.recommendation_container, gf)
                        .commit();
            }
        });
    }

    private String getGenreString(List<Genre> genres) {
        return genres.toString().replace("[", "").replace("]", "");
    }

    private void setupTheme() {
        int curTheme = PreferenceManager.getDefaultSharedPreferences(this).getInt(Constants.THEME_KEY, 0);
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
                setTheme(R.style.BlueForest);
                break;
            default:
                setTheme(R.style.AppTheme);
        }
    }
}
