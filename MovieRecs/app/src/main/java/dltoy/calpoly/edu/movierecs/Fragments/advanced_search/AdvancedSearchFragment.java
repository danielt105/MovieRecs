package dltoy.calpoly.edu.movierecs.Fragments.advanced_search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

import dltoy.calpoly.edu.movierecs.Api.Models.AdvSearch;
import dltoy.calpoly.edu.movierecs.Api.Models.Genre;
import dltoy.calpoly.edu.movierecs.Api.Models.GenreList;
import dltoy.calpoly.edu.movierecs.Api.Models.Keyword;
import dltoy.calpoly.edu.movierecs.Api.Models.Person;
import dltoy.calpoly.edu.movierecs.Api.Models.ResultList;
import dltoy.calpoly.edu.movierecs.BuildConfig;
import dltoy.calpoly.edu.movierecs.Constants;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AdvancedSearchFragment extends Fragment /*implements TokenCompleteTextView.TokenListener*/{

    Spinner genre;
    EditText numStar;
    EditText cast;
    EditText releaseDate;
    Spinner releaseDateRel;

    KeywordCompletionView keywords;
    ArrayAdapter<Keyword> keywordAdapter;
    Keyword[] words;

    PersonCompletionView persons;
    ArrayAdapter<Person> personAdapter;
    Person[] people;

    Button searchButton;
    Button clearButton;

    private ArrayAdapter<String> genreAdapter;
    private ArrayList<String> genreTextList;
    private ArrayList<Genre> genres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.advanced_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        genre = (Spinner) getView().findViewById(R.id.genre_entry);
        numStar = (EditText) getView().findViewById(R.id.star_entry);
        numStar.addTextChangedListener(createTextWatcher(numStar, Constants.MAX_MOVIE_RATING));
        cast = (EditText) getView().findViewById(R.id.cast_entry);

        words = new Keyword[]{};
        keywords = (KeywordCompletionView) getView().findViewById(R.id.keyword_entry);
        keywords.setTextColor(ContextCompat.getColorStateList(getContext(),
                ((MainActivity)getActivity()).getTextColor()));
//        keywords.setTokenListener(this);
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

        people = new Person[]{};
        persons = (PersonCompletionView) getView().findViewById(R.id.cast_entry);
        persons.setTextColor(ContextCompat.getColorStateList(getContext(),
                ((MainActivity)getActivity()).getTextColor()));
//        persons.setTokenListener(this);
        persons.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
        setPersonAdapter();
        persons.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MainActivity.apiService.searchPerson(BuildConfig.apiKey, s.toString())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResultList<Person>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("person error", e.toString());
                            }

                            @Override
                            public void onNext(ResultList<Person> personList) {
                                people = new Person[personList.results.size()];
                                personList.results.toArray(people);
                                setPersonAdapter();
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
                if (sanitizeInput())
                    sendRequest();
            }
        });

        clearButton = (Button) getView().findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genre.setSelection(0);
                numStar.setText("");
                cast.setText("");
                releaseDate.setText("");
                releaseDateRel.setSelection(0);
                keywords.clear();
            }
        });

        //load genres
        getGenres();

        //returning from previous search
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            restoreSearch(bundle);
        }
    }

    //This is a workaround to notifyDataSetChanged not working...
    private void setKeywordAdapter() {
        keywordAdapter = new ArrayAdapter<Keyword>(getContext(), android.R.layout.simple_list_item_1, words);
        keywords.setAdapter(keywordAdapter);
    }

    private void setPersonAdapter() {
        personAdapter = new ArrayAdapter<Person>(getContext(), android.R.layout.simple_list_item_1, people);
        persons.setAdapter(personAdapter);
    }

    private void sendRequest() {
        ((MainActivity)getActivity()).sendSearch(getQueryParams());
    }

    private AdvSearch getQueryParams() {
        return new AdvSearch(new String[] {
            (genre.getSelectedItemPosition() == 0 ? "" : getGenreSelection()),
            (numStar.getText().toString().isEmpty() ? "" : numStar.getText().toString()),
            (keywords.getText().toString().isEmpty() ? "" : getKeywords()),
            (cast.getText().toString().isEmpty() ? "" : getCast()),
            (releaseDateRel.getSelectedItemPosition() == 1 ? releaseDate.getText().toString() : ""),
            (releaseDateRel.getSelectedItemPosition() == 2 ? releaseDate.getText().toString() : "")},
            (ArrayList<Keyword>)keywords.getObjects(),
            (ArrayList<Person>)persons.getObjects());
    }

    private String getKeywords() {
        List<Keyword> chosenKeywords = keywords.getObjects();
        String idList = "";
        for (Keyword k : chosenKeywords) {
            if (k.getId() != -1)
                idList += k.getId() + ",";
        }
        return idList.length() > 0 ? idList.substring(0, idList.length() - 1) : "";
    }

    private String getCast() {
        List<Person> chosenPpl = persons.getObjects();
        String idList = "";
        for (Person p : chosenPpl)
            idList += p.getId() + ",";
        return idList.length() > 0 ? idList.substring(0, idList.length() - 1) : "";
    }

    private void getGenres() {
        genres = (ArrayList<Genre>) MainActivity.db.getGenres();
        if (genres.size() == 0) {
            Log.e("making a api req", "sad face");
            MainActivity.apiService.getGenres(BuildConfig.apiKey)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<GenreList>() {
                        @Override
                        public void onCompleted() { }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("genre error", e.toString());
                        }

                        @Override
                        public void onNext(GenreList genreList) {
                            genres = (ArrayList<Genre>) genreList.results;
                            for (Genre g : genres)
                                MainActivity.db.addGenre(g);
                            populateGenreList();
                        }
                    });
        }
        else {
            populateGenreList();
        }
    }

    private void populateGenreList() {
        Log.e("genres size is pop", genres.size() + " ");

        genreTextList = new ArrayList<>();
        genreTextList.add(getResources().getString(R.string.genre_hint));
        for (Genre g : genres) {
            genreTextList.add(g.getName());
        }

        genreAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, genreTextList);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

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

//    //TODO: remove these
//    @Override
//    public void onTokenAdded(Object token) {
////        savedWords.add((Keyword)token);
//        Log.e("Added Keyword: ", token.toString());
//    }
//
//    @Override
//    public void onTokenRemoved(Object token) {
////        savedWords.remove((Keyword)token);
//        Log.e("Removed Keyword: ", "" + token);
//    }

    //checks input
    private boolean sanitizeInput() {
        String releaseDateText = releaseDate.getText().toString();
        int releaseDateNdx = releaseDateRel.getSelectedItemPosition();

        if ((releaseDateText.isEmpty() && releaseDateNdx > 0) ||
                (!releaseDateText.isEmpty() && releaseDateNdx == 0)) {
            releaseDate.setError(getString(R.string.release_date_error));
            return false;
        }

        return true;
    }

    private void restoreSearch(Bundle savedData) {
        String[] data = savedData.getStringArray(Constants.SAVED_SEARCH);

        if (!data[0].isEmpty()) {
            int target = Integer.parseInt(data[0]);
            for (int iter = 0; iter < genres.size(); iter++) {
                if (genres.get(iter).getId() == target) {
                    genre.setSelection(iter + 1); //first index is "pick"
                    break;
                }
            }
        }
        numStar.setText(data[1]);

        ArrayList<Keyword> kList = savedData.getParcelableArrayList(Constants.KEYWORD_SEARCH);
        for (Keyword k : kList)
            keywords.addObject(k);
        ArrayList<Person> pList = savedData.getParcelableArrayList(Constants.CAST_SEARCH);
        for (Person p : pList)
            persons.addObject(p);

        if (!data[4].isEmpty()) {
            releaseDate.setText(data[4]);
            releaseDateRel.setSelection(1);
        }
        else if (!data[5].isEmpty()) {
            releaseDate.setText(data[5]);
            releaseDateRel.setSelection(2);
        }
    }
}
