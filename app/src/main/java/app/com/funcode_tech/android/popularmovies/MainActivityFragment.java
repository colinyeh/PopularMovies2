package app.com.funcode_tech.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    //private ArrayAdapter<String> mForecastAdapter;

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String POPULAR_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=xxxxx";
    private String RATE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=xxxxx";

    //private ArrayList<GridItem> mMovies = null;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_IMAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_DATE
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_IMAGE = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RATING = 5;
    public static final int COL_DATE = 6;

    public MainActivityFragment() {

    }

    public interface Callback {
        void onItemSelected(GridItem movie);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_main, container, false);

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if(savedInstanceState == null || !savedInstanceState.containsKey("key") ) {
            mGridView = (GridView) rootView.findViewById(R.id.gridview);
            mGridData = new ArrayList<>();
            mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_movie, mGridData);
            mGridView.setAdapter(mGridAdapter);
            updateMovie();
        }
        else {
                mGridView = (GridView) rootView.findViewById(R.id.gridview);
                mGridData = savedInstanceState.getParcelableArrayList("key");
                mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_movie, mGridData);
                mGridView.setAdapter(mGridAdapter);
        }


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItem movie = mGridAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", mGridData);
        super.onSaveInstanceState(outState);
    }


    //@Override
    //public void onActivityCreated(Bundle savedInstanceState) {

    //    super.onActivityCreated(savedInstanceState);
    //}


    private void updateMovie() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));

        if (sortType.equals(getString(R.string.pref_sort_popular)))
            new AsyncHttpTask().execute(POPULAR_URL);
        else if (sortType.equals(getString(R.string.pref_sort_rate)))
            new AsyncHttpTask().execute(RATE_URL);
        else if (sortType.equals(getString(R.string.pref_sort_favorite)))
            new AsyncFavoriteMoiveTask(getActivity()).execute();

    }


    @Override
    public void onStart() {
        super.onStart();

        if (((MainActivity)getActivity()).getFlag() == 1)
        {
            ((MainActivity)getActivity()).setFlag(0);
            updateMovie();
        }

        //updateMovie();
    }


    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                // Log here
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                // Log here
            }
        }
    }

    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     * @param result
     */
    private void parseResult(String result) {

            try {
                mGridData.clear();

                final String MOVIE_BASE_URL =
                        "http://image.tmdb.org/t/p/w185/";

                JSONObject response = new JSONObject(result);
                JSONArray results = response.optJSONArray("results");
                GridItem item;
                if (results != null) {
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject movie = results.optJSONObject(i);
                        String id = movie.optString("id");
                        String title = movie.optString("original_title");
                        String overview = movie.optString("overview");
                        String vote_average = movie.optString("vote_average");
                        String release_date = movie.optString("release_date");
                        String poster_path = movie.optString("poster_path");
                        poster_path = MOVIE_BASE_URL + poster_path;

                        item = new GridItem();
                        item.setID(id);
                        item.setTitle(title);
                        item.setOverview(overview);
                        item.setVoteAverage(vote_average);
                        item.setReleaseDate(release_date);
                        item.setImage(poster_path);
                        mGridData.add(item);
                        }
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //public class AsyncFavoriteMoiveTask extends AsyncTask<Void, Void, ArrayList<GridItem>> {
    public class AsyncFavoriteMoiveTask extends AsyncTask<Void, Void, Integer> {

        private Context mContext;

        public AsyncFavoriteMoiveTask(Context context) {
            mContext = context;
        }

        private Integer getFavoriteMoviesDataFromCursor(Cursor cursor) {
            Integer result = 0;

            //ArrayList<GridItem> results = new ArrayList<>();
            if (cursor == null)
                result = 0;
            else if (cursor != null && cursor.moveToFirst()) {
                mGridData.clear();
                do {
                    GridItem item = new GridItem(cursor);
                    /*
                    GridItem item = new GridItem();
                    String id = cursor.getString(MainActivityFragment.COL_MOVIE_ID);
                    String title = cursor.getString(MainActivityFragment.COL_TITLE);
                    String poster_path = cursor.getString(MainActivityFragment.COL_IMAGE);
                    String overview = cursor.getString(MainActivityFragment.COL_OVERVIEW);
                    String vote_average = cursor.getString(MainActivityFragment.COL_RATING);
                    String release_date = cursor.getString(MainActivityFragment.COL_DATE);

                    //item = new GridItem();
                    item.setID(id);
                    item.setTitle(title);
                    item.setOverview(overview);
                    item.setVoteAverage(vote_average);
                    item.setReleaseDate(release_date);
                    item.setImage(poster_path);
*/
                    mGridData.add(item);
                } while (cursor.moveToNext());
                cursor.close();
                result = 1;
            }
            return result;
        }

        @Override

        protected Integer doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(Integer result) {
            mGridAdapter.setGridData(mGridData);
        }



    }

}

