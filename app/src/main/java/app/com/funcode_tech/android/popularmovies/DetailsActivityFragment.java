package app.com.funcode_tech.android.popularmovies;

/**
 * Created by User on 2015/8/21.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

public class DetailsActivityFragment extends Fragment {
    private TextView idTextView;
    private TextView titleTextView;
    private TextView overviewTextView;
    private TextView vote_averageTextView;
    private TextView release_dateTextView;
    private ImageView imageView;

    LinearLayout trailersLinearLayout;
    LinearLayout reviewsLinearLayout;

    private ListView mTrailerListView;
    private TrailerListViewAdapter mTrailerListAdapter;
    private ArrayList<TrailerListItem> mTrailerListData;
    private ArrayAdapter<String> mTrailerAdapter;
    private String TRAILER_URL_BEFORE_MOVIEID = "http://api.themoviedb.org/3/movie/";
    private String TRAILER_URL_AFTER_MOVIEID = "/videos?api_key=5dfa169715733447336aacaab11a2e67";

    private ListView mReviewListView;
    private ReviewListViewAdapter mReviewListAdapter;
    private ArrayList<ReviewListItem> mReviewListData;
    private ArrayAdapter<String> mReviewAdapter;
    private String REVIEW_URL_BEFORE_MOVIEID = "http://api.themoviedb.org/3/movie/";
    private String REVIEW_URL_AFTER_MOVIEID = "/reviews?api_key=5dfa169715733447336aacaab11a2e67";

    private Button favoriteButton;

    private ScrollView mDetailLayout;
    private String id, title, overview, vote_average, release_date, image;

    GridItem mGridItem;
    Intent intent;


    public DetailsActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Colin 20151109
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        mDetailLayout = (ScrollView) rootView.findViewById(R.id.detail_layout);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mGridItem = arguments.getParcelable("DETAIL_MOVIE");
        }

        if (mGridItem != null)
        {
            id = mGridItem.getID();
            title = mGridItem.getTitle();
            overview = mGridItem.getOverview();
            vote_average = mGridItem.getVoteAverage();
            release_date = mGridItem.getReleaseDate();
            image = mGridItem.getImage();
        }
        else {
            id = getActivity().getIntent().getStringExtra("id");
            title = getActivity().getIntent().getStringExtra("title");
            overview = getActivity().getIntent().getStringExtra("overview");
            vote_average = getActivity().getIntent().getStringExtra("vote_average");
            release_date = getActivity().getIntent().getStringExtra("release_date");
            image = getActivity().getIntent().getStringExtra("image");
        }

        titleTextView = (TextView) rootView.findViewById(R.id.item_movie_title);
        overviewTextView = (TextView) rootView.findViewById(R.id.item_movie_overview);
        vote_averageTextView = (TextView) rootView.findViewById(R.id.item_movie_vote_average);
        release_dateTextView = (TextView) rootView.findViewById(R.id.item_movie_release_date);
        imageView = (ImageView) rootView.findViewById(R.id.item_movie_image);

        trailersLinearLayout = (LinearLayout) rootView.findViewById(R.id.linearLayoutTrailers);
        reviewsLinearLayout = (LinearLayout) rootView.findViewById(R.id.linearLayoutReviews);

        favoriteButton = (Button) rootView.findViewById(R.id.btn_favorite);

        if (id != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
            titleTextView.setText(Html.fromHtml(title));
            overviewTextView.setText(Html.fromHtml(overview));
            vote_averageTextView.setText(Html.fromHtml(vote_average));
            release_dateTextView.setText(Html.fromHtml(release_date));
            Picasso.with(getActivity()).load(image).into(imageView);

            mTrailerListData = new ArrayList<>();
            mTrailerListAdapter = new TrailerListViewAdapter(getActivity(), R.layout.list_item_trailer, mTrailerListData);

            updateMovieTrailer(id);
            inflateTrailers();

            mReviewListData = new ArrayList<>();
            mReviewListAdapter = new ReviewListViewAdapter(getActivity(), R.layout.list_item_review, mReviewListData);

            updateMovieReview(id);
            inflateReviews();
        }
        else
        {
            mDetailLayout.setVisibility(View.INVISIBLE);
        }

        trailersLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((ViewGroup) trailersLinearLayout.getParent()).indexOfChild(v);

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mTrailerListData.get(position).getTrailerPath())));
            }
        });


        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (id != null) {

                    if (isFavorited(getActivity(), id) == 0) {
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                        values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                        values.put(MovieContract.MovieEntry.COLUMN_IMAGE, image);
                        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                        values.put(MovieContract.MovieEntry.COLUMN_RATING, vote_average);
                        values.put(MovieContract.MovieEntry.COLUMN_DATE, release_date);

                        getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
                    }
                }


            }
        });

        return rootView;
    }

    public static int isFavorited(Context context, String id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] { id },
                null
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }


    private void updateMovieTrailer(String movie_id) {
        new AsyncHttpTask_Trailer().execute(TRAILER_URL_BEFORE_MOVIEID + movie_id + TRAILER_URL_AFTER_MOVIEID);
    }

    private void updateMovieReview(String movie_id) {
        new AsyncHttpTask_Review().execute(REVIEW_URL_BEFORE_MOVIEID + movie_id + REVIEW_URL_AFTER_MOVIEID);
    }


    public class AsyncHttpTask_Trailer extends AsyncTask<String, Void, Integer> {

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
                    parseResult_Trailer(response);
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
                mTrailerListAdapter.setTrailerListData(mTrailerListData);
            } else {
                // Log here
            }

            inflateTrailers();
        }
    }


    public class AsyncHttpTask_Review extends AsyncTask<String, Void, Integer> {

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
                    parseResult_Review(response);
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
                mReviewListAdapter.setReviewListData(mReviewListData);
            } else {
                // Log here
            }
            inflateReviews();
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
    private void parseResult_Trailer(String result) {

        try {
            mTrailerListData.clear();

            final String MOVIE_BASE_URL =
                    "http://www.youtube.com/watch?v=";

            JSONObject response = new JSONObject(result);
            JSONArray results = response.optJSONArray("results");
            TrailerListItem item;
            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject movie = results.optJSONObject(i);
                    String id = movie.optString("id");
                    String name = movie.optString("name");
                    String key = movie.optString("key");
                    String trailer_path = MOVIE_BASE_URL + key;
                    item = new TrailerListItem();

                    item.setID(id);
                    item.setName(name);
                    item.setTrailerPath(trailer_path);
                    mTrailerListData.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void parseResult_Review(String result) {

        try {
            mReviewListData.clear();

            JSONObject response = new JSONObject(result);
            JSONArray results = response.optJSONArray("results");
            ReviewListItem item;
            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject movie = results.optJSONObject(i);
                    String id = movie.optString("id");
                    String author = movie.optString("author");
                    String content = movie.optString("content");
                    item = new ReviewListItem();

                    item.setID(id);
                    item.setAuthor(author);
                    item.setContent(content);
                    mReviewListData.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void inflateTrailers() {
        for (int i = 0; i < mTrailerListData.size(); i++) {
            View row = View.inflate(getActivity(), R.layout.list_item_trailer, null);
            TextView title = (TextView) row.findViewById(R.id.list_item_trailer_textview);
            trailersLinearLayout.removeView(row);
            title.setText(mTrailerListData.get(i).getName());
            trailersLinearLayout.addView(row);
        }
    }

    private void inflateReviews() {
        for (int i = 0; i < mReviewListData.size(); i++) {
            View row = View.inflate(getActivity(), R.layout.list_item_review, null);
            TextView author = (TextView) row.findViewById(R.id.list_item_review_author_textview);
            TextView content = (TextView) row.findViewById(R.id.list_item_review_content_textview);
            reviewsLinearLayout.removeView(row);
            author.setText(mReviewListData.get(i).getAuthor());
            content.setText(mReviewListData.get(i).getContent());
            reviewsLinearLayout.addView(row);
        }
    }
}