package project.cis350.upenn.edu.wywg;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by sanjanasarkar on 3/23/17.
 */

public class UserActivity extends AppCompatActivity {
    DataSnapshot snap;
    String username;
    String me = LoginActivity.username;
    Set<String> addedToPlaces = new HashSet<String>();

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    static DatabaseReference usersRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        snap = LoginActivity.snap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("logins");
        usersRef = ref.child("users");
        Bundle extras = getIntent().getExtras();
        List<Location> locs;

        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (extras != null) {
            TextView usernameView = (TextView)findViewById(R.id.user_profile_name);
            username = extras.getString("username");
            usernameView.setText(username);
            String email_adr = "none";
            String city = "none";
            String friend = "none";
            String time = "none";
            if(snap.child("users") .child(username).hasChild("email")) {
                email_adr = snap.child("users").child(username).child("email").getValue().toString();
            }
            if(snap.child("users") .child(username).hasChild("city")) {
                city = snap.child("users").child(username).child("city").getValue().toString();
            }
            if(snap.child("users") .child(username).hasChild("need_friend")) {
                friend = snap.child("users").child(username).child("need_friend").getValue().toString();
            }
            if(snap.child("users") .child(username).hasChild("prefertime")) {
                time = snap.child("users").child(username).child("prefertime").getValue().toString();
            }
//            if(!snap.child("users").child(username).hasChild("message")) {
//                usersRef.child(username).child("message").push().setValue(" ");
//            }
            TextView emailView = (TextView)findViewById(R.id.email_address) ;
            TextView cityView = (TextView)findViewById(R.id.city) ;
            TextView friendView = (TextView)findViewById(R.id.travel);
            TextView timeView = (TextView)findViewById(R.id.time);
            LinearLayout personal_info = (LinearLayout) findViewById(R.id.info) ;
            emailView.setText("email address: "+email_adr);
            cityView.setText("city live in: "+city);
            friendView.setText("look for a friend to travel with: "+friend);
            timeView.setText("preferred travel time:"+time);
            //String city_live_in = snap
            TextView locView = (TextView)findViewById(R.id.locs);
            locs = getLocations(username);
            String been = "";
            String other = "";
            for (Location l : locs) {
                if (l.getVisited()) {
                    been += l.getName() + ", ";
                } else {
                    other += l.getName() + ", ";
                }

            }
            if(been.length() > 2) {
                been = been.substring(0, been.length() - 2);
            }
            if(other.length() > 2) {
                other = other.substring(0, other.length() - 2);
            }
            locView.setText("Been: " + been + "\n" + "Want to go: " + other);
            
            TextView addedLocView = (TextView)findViewById(R.id.addedLocs);
            getAllLocations();
            String addedTo = "";
            if (!addedToPlaces.isEmpty()) {
                for (String s : addedToPlaces) {
                    addedTo += s + ", ";
                }
            } else {
                addedTo = "None";
            }
            if(!addedTo.equals("None") && addedTo.length() > 2) {
                addedTo = addedTo.substring(0,addedTo.length() - 2);
            }
            addedLocView.setText("Locations added to: " + addedTo);
            if(snap.child("users").child(username).hasChild("message")) {
                Map<String, String> messages = (Map<String, String>) snap.child("users").child(username).child("message").getValue();
                for (String p : messages.keySet()) {
                    String temp = messages.get(p);

                    personal_info.addView(createNewTextView(temp));
                }
            }

        }

        Button send = (Button) findViewById(R.id.send);
        Button edit = (Button) findViewById(R.id.edit);
        Button private_message = (Button) findViewById(R.id.priv);
        //final TextView textView1 = new TextView(this);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText message = (EditText) findViewById(R.id.leave_message) ;
                String m = message.getText().toString();
                message.setVisibility(View.INVISIBLE);
                m = "@"+ me + ": " + m;
                LinearLayout l = (LinearLayout) findViewById(R.id.info);
                l.addView(createNewTextView(m));
                Button send = (Button) findViewById(R.id.send);
                Button private_message = (Button) findViewById(R.id.priv);
                usersRef.child(username).child("message").push().setValue(m);
                send.setVisibility(View.INVISIBLE);
                private_message.setVisibility(View.INVISIBLE);
            }
        });


        private_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText message = (EditText) findViewById(R.id.leave_message) ;
                String m = message.getText().toString();
                message.setVisibility(View.INVISIBLE);
                m = "@"+ me + ": " + m;
                LinearLayout l = (LinearLayout) findViewById(R.id.info);
                l.addView(createNewTextView(m));
                Button send = (Button) findViewById(R.id.send);
                Button private_message = (Button) findViewById(R.id.priv);
                usersRef.child(username).child("privateMessage").push().setValue(m);
                send.setVisibility(View.INVISIBLE);
                private_message.setVisibility(View.INVISIBLE);
                //TextView textView1 = new TextView(this);
//                textView1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//                        LayoutParams.WRAP_CONTENT));
//
//                textView1.setText(m);
//                textView1.setBackgroundColor(0xffffffff); // hex color 0xAARRGGBB
//                textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
//                l.addView(textView1);
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText message = (EditText) findViewById(R.id.leave_message) ;
                message.setVisibility(View.VISIBLE);
                message.setText("type here");
                Button send = (Button) findViewById(R.id.send);
                send.setVisibility(View.VISIBLE);
                Button private_message = (Button) findViewById(R.id.priv);
                private_message.setVisibility(View.VISIBLE);


            }
        });


    }

    private TextView createNewTextView(String text) {
        final LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setBackgroundColor(0xffffffff);
        textView.setPadding(20, 20, 20, 20);
        return textView;
    }


    public void setAllProperties() {
        ArrayList<String> users = new ArrayList<>();
        Map<String, Object> m = (Map<String, Object>) snap.child("users").getValue();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            Map singleUser = (Map) entry.getValue();
            users.add((String) singleUser.get("name"));
        }
        for(String u : users) {
            if(!(snap.child("users").child(u).hasChild("prefertime"))) {
                usersRef.child(u).child("prefertime").setValue(" ");
            }
            if(!(snap.child("users").child(u).hasChild("gender"))) {
                usersRef.child(u).child("gender").setValue(" ");
            }
//            if(!(snap.child("users").child(u).hasChild("need_friend"))) {
//                usersRef.child(u).child("need_friend").setValue(" ");
//            }
        }
    }

    public List<Location> getLocations(String username) {
        //locations insert one
        List<Map> locations = (ArrayList<Map>) snap.child("users").child(username).child("locations").getValue();
        System.out.println("all: " + locations);
        System.out.println("one: " + locations.get(0));
        List<Location> tempLocs = new ArrayList<Location>();
        for (int i = 0; i < locations.size(); i++) {
            Map loc = locations.get(i);
            if (loc != null) {
                String name = (String) loc.get("name");
                String description = (String) loc.get("description");
                System.out.println("name: " + name + ", ");
                Object latitude1 = ((Map) loc.get("coordinates")).get("latitude");
                double latitude = ((Number) latitude1).doubleValue();
                Object longitude1 = ((Map) loc.get("coordinates")).get("longitude");
                double longitude = ((Number) longitude1).doubleValue();

                String journal = (String) loc.get("journal");
                boolean been = false;

                if (loc.get("been") != null) {
                    been = (boolean) loc.get("been");
                }
                ;

                tempLocs.add(new Location(name, description, latitude, longitude, been));


                System.out.println("name: " + name + ", " + latitude + ":" + longitude + ",");
            }
        }
        return tempLocs;
    }
    
    public void getAllLocations() {
        ArrayList<String> users = new ArrayList<>();
        Map<String, Object> m = (Map<String, Object>) snap.child("users").getValue();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            Map singleUser = (Map) entry.getValue();
            users.add((String) singleUser.get("name"));
        }
        for (String p : users) {
            List<Map> locations = (ArrayList<Map>) snap.child("users").child(p).child("locations").getValue();
            for (int i = 0; i < locations.size(); i++) {
                Map loc = locations.get(i);
                if (loc != null) {
                    System.out.println("HERE 1");
                    List<String> withUsers = (List<String>) loc.get("users");
                    if (withUsers == null) {
                        System.out.println("NULL");
                    } else {
                        System.out.println("HERE again");
                    }
                    if (withUsers != null && !withUsers.isEmpty()) {
                        for (String s : withUsers) {
                            if (s.equals(username)) {
                                addedToPlaces.add((String) loc.get("name"));
                            }
                        }
                    }
                }
            }
        }
    }

    private void addDrawerItems() {
        String[] osArray = { "All Locations", "All Users", "Map","User Information","friend recommendation", "public message","private message","Sign Out"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mDrawerList.getItemAtPosition(position).toString().equals("All Users")) {
                    Intent myIntent = new Intent(UserActivity.this, UsersListActivity.class);
                    UserActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("All Locations")) {
                    Intent myIntent = new Intent(UserActivity.this, LocationsListActivity.class);
                    UserActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Map")) {
                    Intent myIntent = new Intent(UserActivity.this, MapsActivity.class);
                    UserActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("User Information")) {
                    Intent myIntent = new Intent(UserActivity.this, UserInformationActivity.class);
                    UserActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("public message")) {
                    Intent myIntent = new Intent(UserActivity.this, MessageInboxActivity.class);
                    UserActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("private message")) {
                    Intent myIntent = new Intent(UserActivity.this, PrivateMessageActivity.class);
                    UserActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("friend recommendation")) {
                    Intent myIntent = new Intent(UserActivity.this, TravelMateRecommendationActivity.class);
                    UserActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Sign Out")) {
                    Intent myIntent = new Intent(UserActivity.this, LoginActivity.class);
                    UserActivity.this.startActivity(myIntent);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
