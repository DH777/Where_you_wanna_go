package project.cis350.upenn.edu.wywg;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static project.cis350.upenn.edu.wywg.R.layout.users_list;

/**
 * Created by sanjanasarkar on 3/23/17.
 */

public class UsersListActivity extends AppCompatActivity {
    DataSnapshot snap;
    UsersAdapter adapter;
    ArrayList<String> arrayOfUsers;
    ArrayList<String> global;
    ListView listView;

    ArrayList<String> addedUsers;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    boolean fromForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(users_list);
        snap = LoginActivity.snap;
        global = new ArrayList<>();

        addedUsers = getIntent().getStringArrayListExtra("users");

        fromForm = getIntent().getBooleanExtra("form", false);

        Map<String,Object> m = (Map<String,Object>) snap.child("users").getValue();

        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : m.entrySet()){
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            global.add((String) singleUser.get("name"));
        }

        // Construct the data source
        arrayOfUsers = new ArrayList<>();
// Create the adapter to convert the array to views
        adapter = new UsersAdapter(this, arrayOfUsers, addedUsers);
// Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.userList);
        listView.setAdapter(adapter);
        adapter.addAll(arrayOfUsers);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!fromForm) {
                    Intent intent = new Intent(view.getContext(), UserActivity.class);
                    String data = (String) parent.getItemAtPosition(position);
                    intent.putExtra("username", data);
                    //intent.putExtra("locs", (ArrayList<Location>) getLocations(data));
                    //based on item add info to intent
                    startActivity(intent);
                } else {
                    String data = (String) parent.getItemAtPosition(position);
                    if (addedUsers.contains(data)) {
                        parent.getChildAt(position).setBackgroundColor(Color.WHITE);
                        addedUsers.remove(data);
                    } else {
                        parent.getChildAt(position).setBackgroundColor(Color.LTGRAY);
                        addedUsers.add(data);
                    }
                }
            }
        });
    }

    public List<Location> getLocations(String username) {
        //locations insert one
        List<Map> locations = FirebaseHandler.getLocationsFromFirebase(username);
        List<Location> tempLocs = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            Map loc = locations.get(i);
            String name = (String) loc.get("name");
            String description = (String) loc.get("description");
            Object latitude1 = ((Map) loc.get("coordinates")).get("latitude");
            double latitude = ((Number) latitude1).doubleValue();
            Object longitude1 = ((Map) loc.get("coordinates")).get("longitude");
            double longitude = ((Number) longitude1).doubleValue();

            boolean been = false;
            if (loc.get("been") != null) {
                been = (boolean) loc.get("been");
            }
            tempLocs.add(new Location(name, description, latitude, longitude, been));
        }
        return tempLocs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locations_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                arrayOfUsers.clear();
                adapter.notifyDataSetChanged();
                ArrayList<String> included = new ArrayList<>();
                for (String u : global) {
                    String q = query.toLowerCase();
                    String n = u.toLowerCase();
                    if (n.contains(q)) {
                        included.add(u);
                    }
                }
                arrayOfUsers.addAll(included);
                adapter.notifyDataSetChanged();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        adapter.addAll(global);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

     private void addDrawerItems() {
        String[] osArray = { "All Locations", "All Users", "Map", "User Information","friend recommendation","Sign Out"};
         ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDrawerList.getItemAtPosition(position).toString().equals("All Users")) {
                    Intent myIntent = new Intent(UsersListActivity.this, UsersListActivity.class);
                    UsersListActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("All Locations")) {
                    Intent myIntent = new Intent(UsersListActivity.this, LocationsListActivity.class);
                    UsersListActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Map")) {
                    Intent myIntent = new Intent(UsersListActivity.this, MapsActivity.class);
                    UsersListActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("User Information")) {
                    Intent myIntent = new Intent(UsersListActivity.this, UserInformationActivity.class);
                    UsersListActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("friend recommendation")) {
                    Intent myIntent = new Intent(UsersListActivity.this, TravelMateRecommendationActivity.class);
                    UsersListActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Sign Out")) {
                    Intent myIntent = new Intent(UsersListActivity.this, LoginActivity.class);
                    UsersListActivity.this.startActivity(myIntent);
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent();
        myIntent.putStringArrayListExtra("addedUsers", addedUsers);

        setResult(3, myIntent);
        finish();
    }
}
