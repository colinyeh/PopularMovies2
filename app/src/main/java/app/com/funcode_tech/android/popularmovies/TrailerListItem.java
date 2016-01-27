package app.com.funcode_tech.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 2015/8/18.
 */
public class TrailerListItem implements Parcelable {
    private String id;
    private String name;
    private String trailer_path;

    public TrailerListItem() {
        super();
    }

    private TrailerListItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        trailer_path = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(trailer_path);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<TrailerListItem> CREATOR = new Parcelable.Creator<TrailerListItem>() {
        public TrailerListItem createFromParcel(Parcel in) {
            return new TrailerListItem(in);
        }

        public TrailerListItem[] newArray(int size) {
            return new TrailerListItem[size];
        }
    };

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTrailerPath() {
        return trailer_path;
    }
    public void setTrailerPath(String trailer_path) {
        this.trailer_path = trailer_path;
    }


}
