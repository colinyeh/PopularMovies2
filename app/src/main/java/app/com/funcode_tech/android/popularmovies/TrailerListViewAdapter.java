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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TrailerListViewAdapter extends ArrayAdapter<TrailerListItem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<TrailerListItem> mTrailerListData = new ArrayList<TrailerListItem>();

    public TrailerListViewAdapter(Context mContext, int layoutResourceId, ArrayList<TrailerListItem> mTrailerListData) {
        super(mContext, layoutResourceId, mTrailerListData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mTrailerListData = mTrailerListData;
    }


//    /**
//     * Updates grid data and refresh grid items.
//     * @param mGridData
//     */
    public void setTrailerListData(ArrayList<TrailerListItem> mTrailerListData) {
        this.mTrailerListData = mTrailerListData;
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
            holder.titleTextView = (TextView) row.findViewById(R.id.list_item_trailer_textview);
            //holder.imageView = (ImageView) row.findViewById(R.id.grid_item_movie_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        TrailerListItem item = mTrailerListData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getName()));
        //holder.titleTextView.setText(Html.fromHtml(item.getTrailerPath()));

        //Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}