package com.example.raiko.top10downloader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by raiko on 01/03/2017.
 */

public class FeedAdapter<T extends FeedEntry> extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<T> applications;

    public FeedAdapter(Context context, int resource, List<T> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        // Creates a new view only if listview didn't give a view to use
        if (convertView == null) {
            Log.d(TAG, "getView: called with null convertView");
            // If view did not exist inflates a new view from list_record
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            // Since convertview is null give info about object references in the view
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            Log.d(TAG, "getView: provided convertView");
            // Since convertview != null retrieve object references that are in the view (from setTag method).
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // new instance of view to get widgets on screen ... parent in this case would be list_record.xml
        //View view = layoutInflater.inflate(layoutResource, parent, false);

        // New instance of list
        T currentApp = applications.get(position);

        // Set textview values
        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());


        return convertView;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder (View v) {
            this.tvName = (TextView) v.findViewById(R.id.tvName);
            this.tvArtist = (TextView) v.findViewById(R.id.tvArtist);
            this.tvSummary = (TextView) v.findViewById(R.id.tvSummary);
        }
    }

}
