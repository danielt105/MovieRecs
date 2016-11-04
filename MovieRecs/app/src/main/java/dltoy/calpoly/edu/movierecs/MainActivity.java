package dltoy.calpoly.edu.movierecs;

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
import android.view.MenuItem;

import dltoy.calpoly.edu.movierecs.Api.MovieApi;
import dltoy.calpoly.edu.movierecs.Api.MovieClient;
import dltoy.calpoly.edu.movierecs.Database.DBHandler;
import dltoy.calpoly.edu.movierecs.Fragments.AdvancedSearchFragment;
import dltoy.calpoly.edu.movierecs.Fragments.GridFragment;
import dltoy.calpoly.edu.movierecs.Fragments.SettingsFragment;
import dltoy.calpoly.edu.movierecs.Fragments.WatchlistFragment;
import dltoy.calpoly.edu.movierecs.Fragments.grid_recycler.QueryType;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String THEME_KEY = "current theme :D";
    private NavigationView navView;
    private Toolbar toolbar;
    public static MovieApi apiService;
    public static DBHandler db;

    private static final String CUR_FRAG_KEY = "current_fragment";
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

    //Switches the fragment in the activity
    private void switchToFragment(int navId) {
        Fragment temp = (Fragment) getSupportFragmentManager().findFragmentById(R.id.content);
        switch (navId) {
            case R.id.home:
                curFragId = navId;
                if (temp == null || !(temp instanceof GridFragment)) {
                    GridFragment gf = new GridFragment();
                    Bundle bundle = new Bundle();

                    bundle.putInt(QueryType.QUERY_TYPE, QueryType.QUERY_SEARCH);
                    bundle.putString(QueryType.QUERY_SEARCH_VALUE, "apes");
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
                color = R.color.cruBlack;
        }
        return color;
    }
}
