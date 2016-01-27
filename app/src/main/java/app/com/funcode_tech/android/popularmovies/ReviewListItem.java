package app.com.funcode_tech.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 2015/8/18.
 */
public class ReviewListItem implements Parcelable {
    private String id;
    private String author;
    private String content;

    public ReviewListItem() {
        super();
    }

    private ReviewListItem(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(author);
        out.writeString(content);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ReviewListItem> CREATOR = new Parcelable.Creator<ReviewListItem>() {
        public ReviewListItem createFromParcel(Parcel in) {
            return new ReviewListItem(in);
        }

        public ReviewListItem[] newArray(int size) {
            return new ReviewListItem[size];
        }
    };

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }


}
