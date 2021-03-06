package dltoy.calpoly.edu.movierecs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import br.com.mauker.materialsearchview.MaterialSearchView;
import dltoy.calpoly.edu.movierecs.Api.Models.AdvSearch;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.ResultList;
import dltoy.calpoly.edu.movierecs.Api.MovieApi;
import dltoy.calpoly.edu.movierecs.Api.MovieClient;
import dltoy.calpoly.edu.movierecs.Database.DBHandler;
import dltoy.calpoly.edu.movierecs.Fragments.AdvancedSearchResults;
import dltoy.calpoly.edu.movierecs.Fragments.advanced_search.AdvancedSearchFragment;
import dltoy.calpoly.edu.movierecs.Fragments.GridFragment;
import dltoy.calpoly.edu.movierecs.Fragments.SettingsFragment;
import dltoy.calpoly.edu.movierecs.Fragments.watchlist.NotWatchedFragment;
import dltoy.calpoly.edu.movierecs.Fragments.watchlist.WatchedFragment;
import dltoy.calpoly.edu.movierecs.Fragments.grid_recycler.QueryType;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navView;
    private Toolbar toolbar;
    private FrameLayout mainPane;
    private FrameLayout otherPane;
    private static boolean splitable;
    private boolean isSplit = false;

    public static MovieApi apiService;
    public static DBHandler db;

    private int curFragId;
    private SharedPreferences pref;
    private Menu menu;
    private boolean isSearching = false;

    private AdvSearch savedSearch;
    private boolean sentSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        mainPane = (FrameLayout) findViewById(R.id.content);
        otherPane = (FrameLayout) findViewById(R.id.other_content);
        splitable = otherPane != null;
//        Log.e("splitable is ", splitable + "");

        //set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        setupTheme();

        //set up api client
        apiService = MovieClient.getClient().create(MovieApi.class);
        db = new DBHandler(this);

        savedSearch = (AdvSearch) getLastCustomNonConfigurationInstance();

        if (savedInstanceState == null) {
            curFragId = -1;
            isSearching = false;
            sentSearch = false;
        } else {
            isSearching = savedInstanceState.getBoolean(Constants.IS_SEARCHING);
            sentSearch = savedInstanceState.getBoolean(Constants.REMEMBER_SEARCH);

            curFragId = savedInstanceState.getInt(Constants.CUR_FRAG_KEY);
            toolbar.setTitle(savedInstanceState.getString(Constants.TOOLBAR_TITLE));
        }

        Fragment cur = getSupportFragmentManager().findFragmentById(R.id.content);
        if (cur == null) {
            switchToFragment(curFragId == -1 ? R.id.home : curFragId);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return savedSearch;
    }

    //Switches the fragment in the activity
    private void switchToFragment(int navId) {
        Fragment temp = (Fragment) getSupportFragmentManager().findFragmentById(R.id.content);
        switch (navId) {
            case R.id.home:
                curFragId = navId;
                if (temp == null || !(temp instanceof GridFragment)) {
                    revertLayout();
                    GridFragment gf = new GridFragment();
                    Bundle bundle = new Bundle();

                    bundle.putInt(QueryType.QUERY_TYPE, QueryType.QUERY_NOW_PLAYING);
                    bundle.putBoolean(Constants.IS_HOME, true);
                    gf.setArguments(bundle);

                    loadFragment(R.string.home, R.id.movie_grid, gf);
                }
                break;
            case R.id.advSearch:
                curFragId = navId;
                if (otherPane != null){
                    isSplit = true;
                    changeLayoutWeight(Constants.ADV_SRC_RATIO);
                }

                if (temp == null || !(temp instanceof AdvancedSearchFragment)) {
                    loadFragment(R.string.adv_search, R.id.advSearch, new AdvancedSearchFragment());
                }
                break;
            case R.id.watchlist:
                curFragId = navId;
                if (temp == null || !(temp instanceof WatchedFragment)) {
                    revertLayout();
                    loadFragment(R.string.watchlist_title, R.id.watchlist, new WatchedFragment());
                }
                break;
            case R.id.not_watchedlist:
                curFragId = navId;
                if (temp == null || !(temp instanceof NotWatchedFragment)) {
                    revertLayout();
                    loadFragment(R.string.not_watchlist_title, R.id.not_watchedlist, new NotWatchedFragment());
                }
                break;
            case R.id.settings:
                curFragId = navId;
                if (temp == null || !(temp instanceof SettingsFragment)) {
                    revertLayout();
                    loadFragment(R.string.settings, R.id.settings, new SettingsFragment());
                }
                break;
            default:
                Log.e("Switching to Fragment", "unrecognized id: " + navId + " " + R.id.settings);
        }
    }

    private void loadFragment(int newTitle, int layoutId, Fragment newFragment) {
        toolbar.setTitle(newTitle);
        navView.setCheckedItem(layoutId);
        newFragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, newFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.CUR_FRAG_KEY, curFragId);
        outState.putBoolean(Constants.IS_SEARCHING, isSearching);
        outState.putBoolean(Constants.REMEMBER_SEARCH, sentSearch);
        outState.putString(Constants.TOOLBAR_TITLE, toolbar.getTitle().toString());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean displayAsSelectedItem = true;
        switchToFragment(id);

        //close drawer after
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return displayAsSelectedItem;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.menu = menu;
        if (!(getSupportFragmentManager().findFragmentById(R.id.content) instanceof GridFragment))
            return true;

        getMenuInflater().inflate(R.menu.options, menu);

        final MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content);

                if (frag instanceof GridFragment) {
                    final GridFragment gf = (GridFragment) frag;

                    toggleMenuItems(false);
                    toolbar.setTitle(getString(R.string.search_header) + query);

                    apiService.searchByTitle(BuildConfig.apiKey, query)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<ResultList<Movie>>() {
                                @Override
                                public void onCompleted() {
                                    isSearching = true;
                                    gf.setIsSearching(true);

                                    searchView.closeSearch();
                                    createClearMenuItem();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(MainActivity.this,
                                            getString(R.string.movie_search_error) + query,
                                            Toast.LENGTH_LONG).show();
                                    searchView.closeSearch();
                                }

                                @Override
                                public void onNext(ResultList<Movie> movieList) {
                                    gf.resetMovies(movieList.results);
                                }
                            });
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content);

                if (frag instanceof GridFragment) {
                    apiService.searchByTitle(BuildConfig.apiKey, newText)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<ResultList<Movie>>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                }

                                @Override
                                public void onNext(ResultList<Movie> movieList) {
                                    for (Movie m : movieList.results) {
                                        searchView.addSuggestion(m.getTitle());
                                    }
                                }
                            });
                }
                return false;
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery((String) parent.getItemAtPosition(position), true);
                searchView.closeSearch();
            }
        });

        searchView.setSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewOpened() {
                ((EditText) findViewById(br.com.mauker.materialsearchview.R.id.et_search)).setSingleLine();
            }

            @Override
            public void onSearchViewClosed() {

            }
        });

        if (isSearching) {
            createClearMenuItem();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                ((MaterialSearchView) findViewById(R.id.search_view)).openSearch();
                break;
            default:
                Log.e("Actionbar menu"," Invalid item selected");
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupTheme() {
        int curTheme = pref.getInt(Constants.THEME_KEY, 0);
        switch (curTheme) {
            case 2:
                setTheme(R.style.ReturnOfCruGold);
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.cruGold));
                toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.cruBlack));
                navView.setBackgroundColor(ContextCompat.getColor(this, R.color.cruGold));
                navView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.cru_text_color));
                break;
            case 3:
                setTheme(R.style.Outdoorsy);
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.fauna));
                toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.grass));
                navView.setBackgroundColor(ContextCompat.getColor(this, R.color.lime));
                navView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.outdoor_text));
                break;
            case 4:
                setTheme(R.style.IceIceBaby);
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.warmGray));
                toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.glacierBlue));
                navView.setBackgroundColor(ContextCompat.getColor(this, R.color.ice));
                navView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.ice_text));
                break;
            case 5:
                setTheme(R.style.BlueForest);
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.forestPart));
                toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.blueTitle));
                navView.setBackgroundColor(ContextCompat.getColor(this, R.color.blueBack));
                navView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.blue_forest));
                break;
            default:
                setTheme(R.style.AppTheme);
        }
    }

//    public void setToolbarText(String text) {
//        toolbar.setTitle(text);
//    }

    public int getTextColor() {
        int curTheme = pref.getInt(Constants.THEME_KEY, 0);
        int color;
        switch (curTheme) {
            case 2:
                color = R.color.cru_text_color;
                break;
            case 3:
                color = R.color.outdoor_text;
                break;
            case 4:
                color = R.color.ice_text;
                break;
            case 5:
                color = R.color.blue_forest;
                break;
            default:
                color = R.color.white;
        }
        return color;
    }

    @Override
    public void onBackPressed() {
        MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
        if (searchView.isOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.PREV_FRAG) {
            curFragId = data.getIntExtra(Constants.CUR_FRAG_KEY, R.id.home);
        }
    }

    public void sendSearch(AdvSearch search) {
        savedSearch = search;
        GridFragment gf = (otherPane == null) ? new AdvancedSearchResults() : new GridFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(QueryType.QUERY_TYPE, QueryType.QUERY_ADV_SEARCH);
        bundle.putBoolean(Constants.ADV_SRC_SPLIT_SPAN, true);
        bundle.putStringArray(QueryType.QUERY_ADV_SEARCH_DATA, search.query);
        gf.setArguments(bundle);
        if (otherPane == null) {
            sentSearch = true;
            loadFragment(R.string.adv_search_results, R.id.movie_grid, gf);
        }
        else
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.other_content, gf).commit();
    }

    public void restoreSearch() {
        if (sentSearch) {
            sentSearch = false;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.KEYWORD_SEARCH, savedSearch.keywords);
            bundle.putParcelableArrayList(Constants.CAST_SEARCH, savedSearch.cast);
            bundle.putStringArray(Constants.SAVED_SEARCH, savedSearch.query);
            AdvancedSearchFragment adf = new AdvancedSearchFragment();
            adf.setArguments(bundle);
            loadFragment(R.string.adv_search, R.id.advSearch, adf);
        }
    }

    private void changeLayoutWeight(float contentPercent) {
        if (otherPane != null) {
            mainPane.setLayoutParams(new LinearLayout.LayoutParams(
                    0, FrameLayout.LayoutParams.MATCH_PARENT,
                    contentPercent));
            otherPane.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1 - contentPercent));
        }
    }

    //hides the other framelayout from view (shrinks it)
    private void revertLayout() {
        if (isSplitPane() && isSplit) {
            isSplit = false;
            Fragment temp = getSupportFragmentManager().findFragmentById(R.id.other_content);
            if (temp != null)
                getSupportFragmentManager().beginTransaction().remove(temp).commit();
            changeLayoutWeight(Constants.DEFAULT_RATIO);
        }
    }

    public static boolean isSplitPane() {
        return splitable;
    }

    private void createClearMenuItem() {
        MenuItem clear = menu.add(R.string.clear_results);
        clear.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        clear.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.clear));
        clear.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                toggleMenuItems(true);
                GridFragment gf = (GridFragment)getSupportFragmentManager().findFragmentById(R.id.content);
                gf.resetFragment(QueryType.QUERY_NOW_PLAYING);
                gf.setIsSearching(false);

                menu.close();
                menu.removeItem(item.getItemId());
                toolbar.setTitle(getString(R.string.home));
                isSearching = false;
                return false;
            }
        });
    }

    private void toggleMenuItems(boolean on) {
        if (on) {
            menu.add(R.id.upcoming, Menu.FIRST, Menu.NONE, R.string.upcoming);
            menu.add(R.id.now_playing, Menu.FIRST + 1, Menu.NONE, R.string.now_playing);
            menu.add(R.id.popular, Menu.FIRST + 2, Menu.NONE, R.string.popular);
            menu.add(R.id.top_rated, Menu.FIRST + 3, Menu.NONE, R.string.top_rated);
        }
        else {
            menu.removeItem(R.id.upcoming);
            menu.removeItem(R.id.now_playing);
            menu.removeItem(R.id.popular);
            menu.removeItem(R.id.top_rated);
        }
    }
}
