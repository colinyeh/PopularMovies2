package app.com.funcode_tech.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


//public class MainActivity extends ActionBarActivity {
public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private Integer i_is_setting_changed;
    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i_is_setting_changed = 0;

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailsActivityFragment(),
                                DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    public Integer getFlag() {
        return i_is_setting_changed;
    }
    public void setFlag(Integer i_is_setting_changed) {
        this.i_is_setting_changed = i_is_setting_changed;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class) );
            i_is_setting_changed = 1;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(GridItem movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable("DETAIL_MOVIE", movie);

            DetailsActivityFragment fragment = new DetailsActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);

            intent.putExtra("id", movie.getID()).
                    putExtra("title", movie.getTitle()).
                    putExtra("overview", movie.getOverview()).
                    putExtra("vote_average", movie.getVoteAverage()).
                    putExtra("release_date", movie.getReleaseDate()).
                    putExtra("image", movie.getImage());

            startActivity(intent);
        }
    }
}
