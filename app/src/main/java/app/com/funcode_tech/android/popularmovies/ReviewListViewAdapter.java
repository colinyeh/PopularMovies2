package app.com.funcode_tech.android.popularmovies;

/**
 * Created by User on 2015/8/18.
 */


import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewListViewAdapter extends ArrayAdapter<ReviewListItem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<ReviewListItem> mReviewListData = new ArrayList<ReviewListItem>();

    public ReviewListViewAdapter(Context mContext, int layoutResourceId, ArrayList<ReviewListItem> mReviewListData) {
        super(mContext, layoutResourceId, mReviewListData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mReviewListData = mReviewListData;
    }


//    /**
//     * Updates grid data and refresh grid items.
//     * @param mGridData
//     */
    public void setReviewListData(ArrayList<ReviewListItem> mReviewListData) {
        this.mReviewListData = mReviewListData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.authorTextView = (TextView) row.findViewById(R.id.list_item_review_author_textview);
            holder.contentTextView = (TextView) row.findViewById(R.id.list_item_review_content_textview);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ReviewListItem item = mReviewListData.get(position);
        holder.authorTextView.setText(Html.fromHtml(item.getAuthor()));
        holder.contentTextView.setText(Html.fromHtml(item.getContent()));

        return row;
    }

    static class ViewHolder {
        TextView authorTextView;
        TextView contentTextView;
    }
}