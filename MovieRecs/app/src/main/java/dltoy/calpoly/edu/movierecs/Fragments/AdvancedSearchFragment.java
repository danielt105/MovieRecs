package dltoy.calpoly.edu.movierecs.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.MovieList;
import dltoy.calpoly.edu.movierecs.BuildConfig;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AdvancedSearchFragment extends Fragment{

    TextView temp;
    EditText title;
    Button searchButton;
    private Observer<MovieList> movieSubscriber;

    //temp stuff for testing
    ImageView img;
    Button saveButton;
    Movie m;

    public AdvancedSearchFragment() {
        movieSubscriber = new Observer<MovieList>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onNext(MovieList movies) {
                m = movies.results.get(0);
                saveData(movies.results.get(0).getDescription(),
                        movies.results.get(0).getTitle(),
                        movies.results.get(0).getImagePath()); //purely for testing
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.advanced_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        temp = (TextView) getView().findViewById(R.id.title);
        title = (EditText) getView().findViewById(R.id.title_entry);
        searchButton = (Button) getView().findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest(); //purely for testing
                //TODO: sanitize input
            }
        });

        img = (ImageView) getView().findViewById(R.id.temp_img); //temporary img
        saveButton = (Button) getView().findViewById(R.id.save_watch);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.db.addMovie(m);
            }
        });

        temp.setText("hi");
        img.setImageDrawable(ResourcesCompat.getDrawable(
                getResources(), R.drawable.no_image_placeholder, null));
    }

    private void saveData(String data, String data2, String data3) {
        temp.setText(data2);
        title.setText(data);
        Picasso.with(getContext()).load(genURL(data3)).into(img);
    }

    private String genURL(String base) {
        return "https://image.tmdb.org/t/p/" + "w300" + base;
    }

    private void sendRequest() {
        MainActivity.apiService.searchByTitle(BuildConfig.apiKey, title.getText().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieSubscriber);
    }
}
