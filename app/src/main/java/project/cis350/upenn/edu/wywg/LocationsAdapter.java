package project.cis350.upenn.edu.wywg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ramyarao on 3/10/2017.
 */
public class LocationsAdapter extends ArrayAdapter<Location> {
    public LocationsAdapter(Context context, ArrayList<Location> locations) {
        super(context, 0, locations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Location location = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_location, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvLocationName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.tvLocationInfo);
        // Populate the data into the template view using the data object
        tvName.setText(location.getName());
        tvHome.setText(location.getDescription());
        // Return the completed view to render on screen
        return convertView;
    }
}