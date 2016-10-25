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

import dltoy.calpoly.edu.movierecs.Api.Models.MovieList;
import dltoy.calpoly.edu.movierecs.BuildConfig;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by main on 10/24/2016.
 */
public class AdvancedSearchFragment extends Fragment{

    TextView temp;
    EditText title;
    Button searchButton;

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
            }
        });

        temp.setText("hi");
        title.setText("Enter title here");
    }

    private void sendRequest() {
        Call<MovieList> call = MainActivity.apiService.searchByTitle(BuildConfig.apiKey, "Frozen");
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                Log.d("API response", "Response received: " + response.body() + " code: " +
                response.code());
                if (response.code() != 404) {
                    Log.d("movie data", response.body().results.get(0).title);
                    temp.setText(response.body().results.get(0).title);
                }
            }

            @Override
            public void onFailure(Call<MovieList>call, Throwable t) {
                // Log error here since request failed
                Log.e("API response", t.toString());
            }
        });
    }
}
