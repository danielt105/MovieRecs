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

//    private Fragment defaultFragment;

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

        //set default fragment and switch to it -- home screen
        switchToFragment(R.id.advSearch, R.string.adv_search, new AdvancedSearchFragment());
    }

    //Switches the fragment in the activity
    private void switchToFragment(int navId, int newTitle, Fragment newFragment) {
        navView.setCheckedItem(navId);
        toolbar.setTitle(newTitle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, newFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean displayAsSelectedItem = true;
        switch (id) {
            case R.id.home:
                break;
            case R.id.advSearch:
                switchToFragment(R.id.advSearch, R.string.adv_search, new AdvancedSearchFragment());
                break;
            case R.id.watchlist:
                switchToFragment(R.id.watchlist, R.string.watchlist, new WatchlistFragment());
                break;
            default:
                Log.e("Nav drawer selection", "gave id: " + id);
        }

        //close drawer after
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return displayAsSelectedItem;
    }
}
