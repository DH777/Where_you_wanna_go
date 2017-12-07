package project.cis350.upenn.edu.wywg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sanjanasarkar on 3/23/17.
 */

public class UsersAdapter extends ArrayAdapter<String> {

    ArrayList<String> addedUsers;

    public UsersAdapter(Context context, ArrayList<String> users, ArrayList<String> addedUsers) {
        super(context, 0, users);
        this.addedUsers = addedUsers;
    }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvuserName);

        // Populate the data into the template view using the data object
        tvName.setText(user);
        // Return the completed view to render on screen
        if (addedUsers != null) {
            if (addedUsers.contains(user)) {
                if (parent.getChildAt(position) != null) {
                    parent.getChildAt(position).setBackgroundColor(Color.LTGRAY);
                }
            }
        }
        return convertView;
        }


}
