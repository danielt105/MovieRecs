package dltoy.calpoly.edu.movierecs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import br.com.mauker.materialsearchview.MaterialSearchView;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.ResultList;
import dltoy.calpoly.edu.movierecs.Api.MovieApi;
import dltoy.calpoly.edu.movierecs.Api.MovieClient;
import dltoy.calpoly.edu.movierecs.Database.DBHandler;
import dltoy.calpoly.edu.movierecs.Fragments.advanced_search.AdvancedSearchFragment;
import dltoy.calpoly.edu.movierecs.Fragments.GridFragment;
import dltoy.calpoly.edu.movierecs.Fragments.SettingsFragment;
import dltoy.calpoly.edu.movierecs.Fragments.watchlist.WatchlistFragment;
import dltoy.calpoly.edu.movierecs.Fragments.grid_recycler.QueryType;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String THEME_KEY = "current theme :D";
    private NavigationView navView;
    private Toolbar toolbar;
    public static MovieApi apiService;
    public static DBHandler db;

    public static final int PREV_FRAG_KEY = 1;
    public static final int PREV_FRAG = 2;
    public static final String CUR_FRAG_KEY = "current_fragment";
    private int curFragId;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

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

        curFragId = savedInstanceState == null ? -1 : savedInstanceState.getInt(CUR_FRAG_KEY);
        switchToFragment(curFragId == -1 ? R.id.home : curFragId);
    }

    public void sendSearch(String[] query) {
        GridFragment gf = new GridFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(QueryType.QUERY_TYPE, QueryType.QUERY_ADV_SEARCH);
        bundle.putStringArray(QueryType.QUERY_ADV_SEARCH_DATA, query);
        gf.setArguments(bundle);
        loadFragment(R.string.adv_search_results, R.id.movie_grid, gf);
    }

    //Switches the fragment in the activity
    private void switchToFragment(int navId) {
        Fragment temp = (Fragment) getSupportFragmentManager().findFragmentById(R.id.content);
        switch (navId) {
            case R.id.home:
                curFragId = navId;
                if (temp == null || !(temp instanceof GridFragment)) {
                    GridFragment gf = new GridFragment();
                    Bundle bundle = new Bundle();

                    bundle.putInt(QueryType.QUERY_TYPE, QueryType.QUERY_TOP_RATED);
                    gf.setArguments(bundle);

                    loadFragment(R.string.home, R.id.movie_grid, gf);
                }
                break;
            case R.id.advSearch:
                curFragId = navId;
                if (temp == null || !(temp instanceof AdvancedSearchFragment)) {
                    loadFragment(R.string.adv_search, R.id.advSearch, new AdvancedSearchFragment());
                }
                break;
            case R.id.watchlist:
                curFragId = navId;
                if (temp == null || !(temp instanceof WatchlistFragment)) {
                    loadFragment(R.string.watchlist, R.id.watchlist, new WatchlistFragment());
                }
                break;
            case R.id.settings:
                curFragId = navId;
                if (temp == null || !(temp instanceof SettingsFragment)) {
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
        getSupportFragmentManager().beginTransaction().replace(R.id.content, newFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CUR_FRAG_KEY, curFragId);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);

        final MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content);

                if (frag instanceof GridFragment) {
                    final GridFragment gf = (GridFragment) frag;
                    gf.showLoadingIcon(true);

                    toolbar.setTitle(getString(R.string.search_header) + query);

                    apiService.searchByTitle(BuildConfig.apiKey, query)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<ResultList<Movie>>() {
                                @Override
                                public void onCompleted() {
                                    searchView.closeSearch();
                                    gf.showLoadingIcon(false);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(MainActivity.this,
                                            getString(R.string.movie_search_error) + query,
                                            Toast.LENGTH_LONG).show();
                                    gf.showLoadingIcon(false);
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            ((MaterialSearchView) findViewById(R.id.search_view)).openSearch();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTheme() {
        int curTheme = pref.getInt(THEME_KEY, 0);
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
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.forest));
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
                setTheme(R.style.UnderstatedVersatile);
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.fog));
                toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.stormSea));
                navView.setBackgroundColor(ContextCompat.getColor(this, R.color.charcoal));
                navView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.understated_versatile));
                break;
            default:
                setTheme(R.style.AppTheme);
        }
    }

    public void setToolbarText(String text) {
        toolbar.setTitle(text);
    }

    public int getTextColor() {
        int curTheme = pref.getInt(THEME_KEY, 0);
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
                color = R.color.understated_versatile;
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
        Log.e("this was called", "activity result");
        if (resultCode == PREV_FRAG) {
            curFragId = data.getIntExtra(CUR_FRAG_KEY, R.id.home);
        }
    }
}
