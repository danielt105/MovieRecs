package dltoy.calpoly.edu.movierecs.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    public AdvancedSearchFragment() {
        movieSubscriber = new Subscriber<MovieList>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onNext(MovieList movies) {
                saveData(movies.results.get(0).description, movies.results.get(0).title); //purely for testing
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

        temp.setText("hi");
        title.setText("Enter title here");
    }

    private void saveData(String data, String data2) {
        temp.setText(data2);
        title.setText(data);
    }

    private void sendRequest() {
        MainActivity.apiService.searchByTitle(BuildConfig.apiKey, "Frozen")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieSubscriber);
    }
}
