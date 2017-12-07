package project.cis350.upenn.edu.wywg;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import org.parceler.Parcels;

import java.util.Iterator;
import java.util.List;

public class LocationActivity extends AppCompatActivity {
    TextView tvName;
    //TextView tvJournal;
    TextView tvDescription;
    TextView tvRating;
    TextView tvCost;
    TextView tvTime;
    Intent intent;
    byte[] byteArray;

    Button editButton;
    Button deleteButton;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tvName = (TextView)findViewById(R.id.tvName);
        //tvJournal = (TextView)findViewById(R.id.tvJournal);
        tvDescription = (TextView)findViewById(R.id.tvDescription);
        tvRating = (TextView)findViewById(R.id.tvRating);
        tvCost = (TextView)findViewById(R.id.tvCost);
        tvTime=(TextView)findViewById(R.id.tvTime);
        final Location loc = (Location) Parcels.unwrap(getIntent().getParcelableExtra("loc"));
        final String position = getIntent().getStringExtra("Position");
        tvName.setText(loc.getName());
        /*if (!loc.getJournal().isEmpty()) {
            tvJournal.setText(loc.getJournal());
        } else {
            tvJournal.setText("No journal entries.");
        }*/
        tvDescription.setText(loc.getDescription());
        intent = getIntent();
        editButton = (Button) findViewById(R.id.edit_button);
        deleteButton = (Button) findViewById(R.id.delete_button);

        tvRating.setText("Rating: " + loc.getRating());

        tvCost.setText("Cost: " + loc.getCost());
        tvTime.setText("Time: " + loc.getTime());

        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
//// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.past, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        spinner.setAdapter(adapter);



        ImageView iv = (ImageView) findViewById(R.id.image1);
        if(getIntent().getByteArrayExtra("pic")!=null){
            byteArray = getIntent().getByteArrayExtra("pic");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            iv.setImageBitmap(bmp);
        }
        LinearLayout photoInfoView = new LinearLayout(LocationActivity.this);
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LocationActivity.this, LocationFormActivity.class);
                myIntent.putExtra("edit", true);
                myIntent.putExtra("name", loc.getName());
                myIntent.putExtra("desc", loc.getDescription());
                //myIntent.putExtra("journal", loc.getJournal());
                myIntent.putExtra("cost", loc.getCost());
                myIntent.putExtra("time", loc.getTime());
                myIntent.putExtra("rating", (float) loc.getRating());
                myIntent.putExtra("been", loc.getVisited());
                myIntent.putExtra("hemisphere", loc.getHemisphere());
                // rating, been/togo
                LocationActivity.this.startActivity(myIntent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LocationActivity.this, LocationsListActivity.class);
                myIntent.putExtra("Delete", "delete");
                myIntent.putExtra("Position", position);
                LocationActivity.this.startActivity(myIntent);

            }

        });

    }

    private void addDrawerItems() {
        String[] osArray = { "All Locations", "All Users", "Map", "User Information","friend recommendation","Sign Out"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mDrawerList.getItemAtPosition(position).toString().equals("All Users")) {
                    Intent myIntent = new Intent(LocationActivity.this, UsersListActivity.class);
                    LocationActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("All Locations")) {
                    Intent myIntent = new Intent(LocationActivity.this, LocationsListActivity.class);
                    LocationActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Map")) {
                    Intent myIntent = new Intent(LocationActivity.this, MapsActivity.class);
                    LocationActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("User Information")) {
                    Intent myIntent = new Intent(LocationActivity.this, UserInformationActivity.class);
                    LocationActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("friend recommendation")) {
                    Intent myIntent = new Intent(LocationActivity.this, TravelMateRecommendationActivity.class);
                    LocationActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Sign Out")) {
                    Intent myIntent = new Intent(LocationActivity.this, LoginActivity.class);
                    LocationActivity.this.startActivity(myIntent);
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
