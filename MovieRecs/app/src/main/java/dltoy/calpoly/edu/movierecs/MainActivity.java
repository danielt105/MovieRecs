package dltoy.calpoly.edu.movierecs;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import dltoy.calpoly.edu.movierecs.Api.MovieApi;
import dltoy.calpoly.edu.movierecs.Api.MovieClient;
import dltoy.calpoly.edu.movierecs.Database.DBHandler;
import dltoy.calpoly.edu.movierecs.Fragments.AdvancedSearchFragment;
import dltoy.calpoly.edu.movierecs.Fragments.WatchlistFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navView;
    private Toolbar toolbar;
    public static MovieApi apiService;
    public static DBHandler db;

    private static final String CUR_FRAG_KEY = "current_fragment";
    private int curFragId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //set up api client
        apiService = MovieClient.getClient().create(MovieApi.class);
        db = new DBHandler(this);

        curFragId = savedInstanceState == null ? -1 : savedInstanceState.getInt(CUR_FRAG_KEY);
        switchToFragment(curFragId == -1 ? R.id.advSearch : curFragId);
    }

    //Switches the fragment in the activity
    private void switchToFragment(int navId) {
        Fragment temp = (Fragment) getSupportFragmentManager().findFragmentById(R.id.content);
        switch (navId) {
            case R.id.home:
                curFragId = navId;
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
            default:
                Log.e("Switching to Fragent", "unrecognized id: " + navId);
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
}
