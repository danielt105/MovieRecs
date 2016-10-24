package dltoy.calpoly.edu.movierecs;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean displayAsSelectedItem = true;
        switch (id) {
            case R.id.home:
                break;
            case R.id.advSearch:
                break;
            case R.id.watchlist:
                break;
        }
        return displayAsSelectedItem;
    }
}
