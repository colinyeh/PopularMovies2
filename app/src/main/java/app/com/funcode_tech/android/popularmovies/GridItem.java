package app.com.funcode_tech.android.popularmovies;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 2015/8/18.
 */
public class GridItem implements Parcelable {
    private String id;
    private String image;
    private String title;
    private String overview;
    private String vote_average;
    private String release_date;

    public GridItem() {
        super();
    }

    private GridItem(Parcel in) {
        id = in.readString();
        image = in.readString();
        title = in.readString();
        overview = in.readString();
        vote_average = in.readString();
        release_date = in.readString();
    }

    public GridItem(Cursor cursor) {
        id = cursor.getString(MainActivityFragment.COL_MOVIE_ID);
        title = cursor.getString(MainActivityFragment.COL_TITLE);
        image = cursor.getString(MainActivityFragment.COL_IMAGE);
        overview = cursor.getString(MainActivityFragment.COL_OVERVIEW);
        vote_average = cursor.getString(MainActivityFragment.COL_RATING);
        release_date = cursor.getString(MainActivityFragment.COL_DATE);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(image);
        out.writeString(title);
        out.writeString(overview);
        out.writeString(vote_average);
        out.writeString(release_date);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<GridItem> CREATOR = new Parcelable.Creator<GridItem>() {
        public GridItem createFromParcel(Parcel in) {
            return new GridItem(in);
        }

        public GridItem[] newArray(int size) {
            return new GridItem[size];
        }
    };

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return vote_average;
    }
    public void setVoteAverage(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getReleaseDate() {
        return release_date;
    }
    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }
}
