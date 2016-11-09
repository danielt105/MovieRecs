package dltoy.calpoly.edu.movierecs.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.Models.Genre;
import dltoy.calpoly.edu.movierecs.Api.Models.GenreList;
import dltoy.calpoly.edu.movierecs.Api.Models.Keyword;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.ResultList;
import dltoy.calpoly.edu.movierecs.BuildConfig;
import dltoy.calpoly.edu.movierecs.KeywordCompletionView;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AdvancedSearchFragment extends Fragment implements TokenCompleteTextView.TokenListener{

    public static final int MAX_MOVIE_RATING = 10;

    EditText title;
    Spinner genre;
    EditText numStar;
    EditText cast;
    EditText releaseDate;
    Spinner releaseDateRel;

    KeywordCompletionView keywords;
    ArrayAdapter<Keyword> keywordAdapter;
    public static Keyword[] words;

    Button searchButton;
    Button clearButton;

    private Observer<ResultList<Movie>> movieSubscriber;
    private ArrayAdapter<String> genreAdapter;
    private ArrayList<String> genreList;
    private ArrayList<Genre> genres;

    public AdvancedSearchFragment() {
        movieSubscriber = new Observer<ResultList<Movie>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("movielist error", e.toString());
            }

            @Override
            public void onNext(ResultList<Movie> movies) {
                //TODO: populate some global list
            }
        };
        getGenres();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.advanced_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = (EditText) getView().findViewById(R.id.title_entry);
        genre = (Spinner) getView().findViewById(R.id.genre_entry);
        numStar = (EditText) getView().findViewById(R.id.star_entry);
        numStar.addTextChangedListener(createTextWatcher(numStar, MAX_MOVIE_RATING));
        cast = (EditText) getView().findViewById(R.id.cast_entry);

        words = new Keyword[]{};
        keywords = (KeywordCompletionView) getView().findViewById(R.id.keyword_entry);
        keywords.setTextColor(ContextCompat.getColorStateList(getContext(),
                ((MainActivity)getActivity()).getTextColor()));
        keywords.setTokenListener(this);
        keywords.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
        setKeywordAdapter();
        keywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MainActivity.apiService.searchKeyword(BuildConfig.apiKey, s.toString())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResultList<Keyword>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("keyword error", e.toString());
                            }

                            @Override
                            public void onNext(ResultList<Keyword> keywordList) {
//                                Log.e("response", "got the list back");
                                words = new Keyword[keywordList.results.size()];
                                keywordList.results.toArray(words);
                                setKeywordAdapter();
                            }
                        });
            }
        });

        releaseDate = (EditText) getView().findViewById(R.id.release_date_date);
        releaseDate.addTextChangedListener(createTextWatcher(
                releaseDate, Calendar.getInstance().get(Calendar.YEAR)));
        releaseDateRel = (Spinner) getView().findViewById(R.id.release_date_relative);

        searchButton = (Button) getView().findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: sanitize input
                sendRequest();
            }
        });

        clearButton = (Button) getView().findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
                genre.setSelection(0);
                numStar.setText("");
                cast.setText("");
                releaseDate.setText("");
                releaseDateRel.setSelection(0);
                keywords.clear();
            }
        });
    }

    //This is a workaround to notifyDataSetChanged not working...
    private void setKeywordAdapter() {
        keywordAdapter = new ArrayAdapter<Keyword>(getContext(), android.R.layout.simple_list_item_1, words);
        keywords.setAdapter(keywordAdapter);
    }

    private void sendRequest() {
        Log.e("Search", "would have sent " + buildQueryString());
//        MainActivity.apiService.searchByTitle(BuildConfig.apiKey + buildQueryString(), 1)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(movieSubscriber);
    }

    private String buildQueryString() {
        return //title.getText().toString().isEmpty() ? "" : title.getText().toString() +
                (genre.getSelectedItemPosition() == 0 ? "" : "with_genres=" + getGenreSelection()) +
                (numStar.getText().toString().isEmpty() ? "" : "vote_count.gte=" + numStar.getText().toString()) +
                (keywords.getText().toString().isEmpty() ? "" : "with_keywords=" + getKeywords()) +
                (cast.getText().toString().isEmpty() ? "" : "with_cast=" + cast.getText().toString());
    }

    private String getKeywords() {
        List<Keyword> chosenKeywords = keywords.getObjects();
        String idList = "";
        for (Keyword k : chosenKeywords) {
            idList += k.getId() + ",";
        }
        return idList.substring(0, idList.length() - 1);
    }

    private void getGenres() {
        genres = (ArrayList<Genre>) MainActivity.db.getGenres();
        if (genres.size() == 0) {
            MainActivity.apiService.getGenres(BuildConfig.apiKey)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<GenreList>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("genre error", e.toString());
                        }

                        @Override
                        public void onNext(GenreList genreList) {
                            genres = (ArrayList<Genre>) genreList.results;
                            populateGenreList();
                        }
                    });
        }
        else {
            populateGenreList();
        }
    }

    private void populateGenreList() {
        genreList = new ArrayList<>();
        genreList.add(getResources().getString(R.string.genre_hint));
        for (Genre g : genres) {
            genreList.add(g.getName());
        }

        genreAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, genreList);
        genre.setAdapter(genreAdapter);
    }

    private String getGenreSelection() {
        String target = (String) genre.getSelectedItem();
        for (Genre g : genres) {
            if (g.getName() == target) {
                return g.getId() + "";
            }
        }
        return "";
    }

    private TextWatcher createTextWatcher(final EditText component, final int maxDate) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (Integer.parseInt(s.toString()) > maxDate) {
                        component.setText(maxDate + "");
                    }
                }
                catch (NumberFormatException nf) {
                    //only ever catching "" case so don't have to worry
                }
            }
        };
    }

    @Override
    public void onTokenAdded(Object token) {
//        savedWords.add((Keyword)token);
        Log.e("Added Keyword: ", token + " " + ((Keyword) token).getId());
    }

    @Override
    public void onTokenRemoved(Object token) {
//        savedWords.remove((Keyword)token);
        Log.e("Removed Keyword: ", "" + token + " " + ((Keyword) token).getId());
    }
}
