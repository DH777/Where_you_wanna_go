package project.cis350.upenn.edu.wywg;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import android.util.Log;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;

import android.location.Address;
import android.location.Geocoder;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;

    DataSnapshot snap = LoginActivity.snap;
    String username = LoginActivity.username;

    private ArrayList<Location> allLocations = new ArrayList<>();
    private ArrayList<Location> locationsBeen = new ArrayList<>();
    private ArrayList<Location> locationsToGo = new ArrayList<>();
    private ArrayList<Marker> markersBeen = new ArrayList<>();
    private ArrayList<Marker> markersToGo = new ArrayList<>();
    private ArrayList<Location> recommendationListRecent = new ArrayList<>();
    private ArrayList<Location> recommendationListPopularity = new ArrayList<>();
    private ArrayList<Marker> markersRecommendRecent = new ArrayList<>();
    private ArrayList<Marker> markersRecommendPopularity = new ArrayList<>();

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private LatLng latLng;
    private String selectedPlace;

    private double myLatitude;
    private double myLongitude;

    private String locationName = "";

    protected Button buttonMyLocation;
    private Marker markerMyLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final float DEFAULT_ZOOM = 14.0f;
    private android.location.Location mLastKnownLocation;

    private Button recommendationsButton;
    private boolean recommendationsAlreadyShown = false;
    private boolean recent = false;
    private boolean popular = false;

    // recommendation filters
    RecommendationsFragment recFragment;
    //what we're filtering by
    boolean cost = false;
    boolean rating = false;
    double price;
    float ratingNum;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getLocations();

        recommendationsButton = (Button) findViewById(R.id.recommendations);
        buttonMyLocation = (Button) findViewById(R.id.buttonMyLoc);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        GPSTracker gpsTracker = new GPSTracker(MapsActivity.this);
        if (gpsTracker.canGetLocation()) {
            myLatitude = gpsTracker.getLatitude();
            myLongitude = gpsTracker.getLongitude();
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMapClickListener(this);

        // add markers retrieved from database
        for (Location location : locationsBeen) {
            addBeenMarker(location);
        }
        for (Location location : locationsToGo) {
            addToGoMarker(location);
        }

        getAllLocations();
        getRecommendations();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                selectedPlace = place.getName().toString();

                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(selectedPlace, 1);
                    if (addresses != null && !addresses.equals(""))
                        search(addresses);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });


        buttonMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_LONG).show();
                    return;
                }
                mMap.setMyLocationEnabled(true);
                try {
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        final android.location.Location  bla;
                        Task locationResult = mFusedLocationProviderClient.getLastLocation();

                        locationResult.addOnCompleteListener(MapsActivity.this, new OnCompleteListener<android.location.Location>() {
                            @Override
                            public void onComplete(@NonNull Task<android.location.Location> task) {
                                if (task.isSuccessful()) {
                                    // Set the map's camera position to the current location of the device.
                                    mLastKnownLocation = task.getResult();
                                    LatLng latlngMyLocation = new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude());

                                    MarkerOptions myLocationMarkerOpt = new MarkerOptions()
                                            .position(latlngMyLocation)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                    markerMyLocation = mMap.addMarker(myLocationMarkerOpt);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            latlngMyLocation, DEFAULT_ZOOM));
                                } else {
                                }
                            }
                        });
                    }
                } catch(SecurityException e)  {
                    Log.e("Exception: %s", e.getMessage());
                }
            }
        });

        recommendationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recommendationsAlreadyShown) {
                    // Ask whether to sort by recent or popular
                    showRadioButtonDialog();
                } else {
                    // remove recommendations
                    recent = false;
                    popular = false;
                    for (Marker m : markersRecommendRecent) {
                        m.setVisible(false);
                    }
                    for (Marker m : markersRecommendPopularity) {
                        m.setVisible(false);
                    }
                    recommendationsAlreadyShown = false;
                    rating = false;
                    cost = false;
                }
            }
        });

        Intent intent = getIntent();
        String notifLocation = intent.getStringExtra("Location");
        List<Map> locations = FirebaseHandler.getLocationsFromFirebase(username);
        if(locations != null){
            for(Map l: locations){
                if(l != null){
                    String locName = (String) l.get("name");
                    if(locName.equals(notifLocation)){
                        if(l.get("latitude") != null && l.get("longi") !=  null){
                            latLng = new LatLng((double)l.get("latitude"), (double)l.get("longi"));
                            mMap.clear();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
                        }
                    }
                }
            }
        }
    }

    private void addDrawerItems() {
        String[] osArray = { "All Locations", "All Users", "Map", "User Information","friend recommendation","public message","private message","Sign Out"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mDrawerList.getItemAtPosition(position).toString().equals("All Users")) {
                    Intent myIntent = new Intent(MapsActivity.this, UsersListActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("All Locations")) {
                    Intent myIntent = new Intent(MapsActivity.this, LocationsListActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Map")) {
                    Intent myIntent = new Intent(MapsActivity.this, MapsActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("User Information")) {
                    Intent myIntent = new Intent(MapsActivity.this, UserInformationActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("friend recommendation")) {
                    Intent myIntent = new Intent(MapsActivity.this, TravelMateRecommendationActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("public message")) {
                    Intent myIntent = new Intent(MapsActivity.this, MessageInboxActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                }else if (mDrawerList.getItemAtPosition(position).toString().equals("private message")) {
                    Intent myIntent = new Intent(MapsActivity.this, PrivateMessageActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Sign Out")) {
                    Intent myIntent = new Intent(MapsActivity.this, LoginActivity.class);
                    MapsActivity.this.startActivity(myIntent);
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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void getNotification(String name){

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        notificationIntent.putExtra("Location", name);

        PendingIntent intent = PendingIntent.getActivity(this, m,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notif = new Notification.Builder(this)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentTitle("Added to a new place")
                .setContentText("You have been added to " + name + "!")
                .setContentIntent(intent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(m, notif);
    }

    private void showRadioButtonDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);
        List<String> stringList=new ArrayList<>();  // here is list
        stringList.add("Most recent");
        stringList.add("Most popular");
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb=new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));
            rg.addView(rb);
        }

        dialog.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        if (x == 0) {
                            recent = true;
                            popular = false;
                        } else {
                            popular = true;
                            recent = false;
                        }
                        dialog.cancel();
                        showRecDialog();
                        recommendationsAlreadyShown = true;
                    }
                }
            }
        });
    }

    private void showRecDialog() {
        FragmentManager fm = getSupportFragmentManager();
        recFragment = RecommendationsFragment.newInstance("Some Title");
        recFragment.show(fm, "rec_frag");
    }

    public void toDone(View view) {
        if (recFragment.cbCost.isChecked()) {
            cost = true;
        }
        if (recFragment.cbRating.isChecked()) {
            rating = true;
        }
        if (cost) {
            EditText etCost = recFragment.etCostFilter;
            if (etCost.getText()!= null) {
                String c = etCost.getText().toString();
                if (c.length() >= 1) {
                    price = Double.parseDouble(c);
                }
            }
        }

        if (rating) {
            ratingNum = recFragment.rb.getRating();
        }

        recFragment.dismiss();
        if (recent) {
            for (Marker m : markersRecommendRecent) {
                if (cost) {
                    if (getLocationFromMarker(m).getCost() <= price) {
                        m.setVisible(true);
                    }
                }
                if (rating) {
                    if (getLocationFromMarker(m).getRating() >= ratingNum) {
                        m.setVisible(true);
                    }
                }
                if (!cost && ! rating) {
                    m.setVisible(true);
                }
            }
        } else if (popular) {
            for (Marker m : markersRecommendPopularity) {
                if (cost) {
                    if (getLocationFromMarker(m).getCost() <= price) {
                        m.setVisible(true);
                    }
                }
                if (rating) {
                    if (getLocationFromMarker(m).getRating() >= ratingNum) {
                        m.setVisible(true);
                    }
                }
                if (!cost && !rating) {
                    m.setVisible(true);
                }
            }
        }
    }

    protected void search(List<Address> addresses) {
        Address address = addresses.get(0);
        latLng = new LatLng(address.getLatitude(), address.getLongitude());

        String addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                address.getCountryName());

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(addressText);

        locationName = address.getAddressLine(0);

        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    public void getLocations() {
        //locations insert one
        List<Map> locations = FirebaseHandler.getLocationsFromFirebase(username);
        if (locations != null) {
            for (int i = 0; i < locations.size(); i++) {
                if (locations.get(i) != null) {
                    //locations.
                    Map loc = locations.get(i);
                    String name = (String) loc.get("name");
                    String description = (String) loc.get("description");
                    boolean been;
                    if (loc.get("been") == null) {
                        been = false;
                    } else {
                        been = (boolean) loc.get("been");
                    }
                    Object latitude1 = ((Map) loc.get("coordinates")).get("latitude");
                    double latitude = ((Number) latitude1).doubleValue();
                    Object longitude1 = ((Map) loc.get("coordinates")).get("longitude");
                    double longitude = ((Number) longitude1).doubleValue();

                    if (loc.get("notified") != null) {
                        if (!(boolean) loc.get("notified")) {
                            getNotification(name);
                            LoginActivity.usersRef.child(username).child("locations").child(i + "").child("notified").setValue(true);
                        }
                    }
                    List<String> pics = (List<String>) loc.get("pics");
                    if (pics == null) {
                        pics = new ArrayList<>();
                    }

                    List<String> users = (List<String>) loc.get("users");
                    if (users == null) {
                        users = new ArrayList<>();
                    }
                    if (been) {
                        Location l = new Location(name, description, latitude, longitude, been);
                        l.setPics(pics);
                        l.setUsers(users);
                        addBeenLocation(l);
                    } else {
                        Location l = new Location(name, description, latitude, longitude, been);
                        l.setPics(pics);
                        addToGoLocation(l);
                        l.setUsers(users);
                    }
                }
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return prepareInfoView(marker);
    }

    private View prepareInfoView(Marker marker) {
        final Location l = getLocationFromMarker(marker);
        WeatherServiceAsync weatherTask = new WeatherServiceAsync(l);
        weatherTask.execute();

        LinearLayout infoView = new LinearLayout(MapsActivity.this);

        LayoutInflater inflater = LayoutInflater.from(this);

        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setMinimumWidth(700);
        infoView.setMinimumHeight(300);
        infoView.setBackgroundResource(R.drawable.rounded_corner);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
        int sizeInDP = 25;

        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources()
                        .getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(30, 20, 30, 0);
        infoView.setBackgroundColor(Color.WHITE);

        TextView name = new TextView(MapsActivity.this);
        name.setText(l.getName());
        name.setTextSize(20);
        name.setTypeface(Typeface.DEFAULT_BOLD);

        TextView weather = new TextView(MapsActivity.this);
        char degree = 0x00B0;

        TextView users = new TextView(MapsActivity.this);
        users.setTextSize(15);

        LinearLayout photoInfoView = new LinearLayout(MapsActivity.this);
        photoInfoView.setOrientation(LinearLayout.HORIZONTAL);

        Iterator<String> i = l.getPics().iterator();
        boolean hasPics = false;
        while (i.hasNext()) {
            hasPics = true;
            ImageView infoImageView = new ImageView(MapsActivity.this);

            int width = 200;
            int height = 200;
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
            infoImageView.setLayoutParams(parms);

            String base64Image = i.next();
            byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
            Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

            infoImageView.setImageBitmap(b);
            photoInfoView.addView(infoImageView);
        }

        Iterator<String> i2 = l.getUsers().iterator();
        String u = "";
        while (i2.hasNext()) {
            u += i2.next() + " ";
        }
        users.setText("Users Added: " + u);
        try {
            weatherTask.get();
        } catch (InterruptedException e) {
            Log.v("MapsActivity", "weatherTask.get InterruptEx");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.v("MapsActivity", "weatherTask.get ExecutionEx");
            e.printStackTrace();
        }
        finally {
            Log.v("MapsActivity", "get returned w/o exception.");
        }
        String w = "Temperature: " + (int) l.getTemperature() + " " + degree + "F";
        weather.setText(w);
        weather.setTextSize(15);

        subInfoView.addView(name);
        subInfoView.addView(weather);

        if (!u.isEmpty()) {
            subInfoView.addView(users);
        }
        subInfoView.addView(photoInfoView);
        infoView.addView(subInfoView, layoutParams);

        return infoView;
    }

    public Location getLocationFromMarker(Marker marker) {
        Location l = null;
        if (marker.equals(markerMyLocation)) {
            l = new Location("MyLocation", "", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), false);
            return l;
        }
        int i = markersToGo.indexOf(marker);
        if (i != -1) {
            l = locationsToGo.get(i);
        } else {
            i = markersBeen.indexOf(marker);
            if (i != -1) {
                l = locationsBeen.get(i);
            } else {
                i = markersRecommendRecent.indexOf(marker);
                if (i != -1) {
                    l = recommendationListRecent.get(i);
                } else {
                    i = markersRecommendPopularity.indexOf(marker);
                    l = recommendationListPopularity.get(i);
                }
            }
        }
        return l;
    }

    @Override
    public void onMapClick(LatLng point) {
        Intent myIntent = new Intent(MapsActivity.this, LocationFormActivity.class);
        myIntent.putExtra("point", point);
        myIntent.putExtra("latitude", point.latitude);
        myIntent.putExtra("long", point.longitude);
        myIntent.putExtra("name", locationName);
        MapsActivity.this.startActivity(myIntent);
    }

    public void toUsersList(View view) {
        Intent myIntent = new Intent(MapsActivity.this, UsersListActivity.class);
        MapsActivity.this.startActivity(myIntent);
    }

    public void addBeenLocation(Location l) {
        locationsBeen.add(l);
    }

    public void addToGoLocation(Location l) {
        locationsToGo.add(l);
    }

    public void addBeenMarker(Location l) {
        Marker m = mMap.addMarker(new MarkerOptions().position(l.getCoordinates()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        markersBeen.add(m);
    }

    public void addToGoMarker(Location l) {
        Marker m = mMap.addMarker(new MarkerOptions().position(l.getCoordinates()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        markersToGo.add(m);
    }

    public void getAllLocations() {
        ArrayList<String> users = FirebaseHandler.getUsers();

        for (String p : users) {
            List<Map> locations = FirebaseHandler.getLocationsFromFirebase(p);
            for (int i = 0; i < locations.size(); i++) {
                Map loc = locations.get(i);
                if (loc != null) {
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

                    List<String> pics = (List<String>) loc.get("pics");
                    if (pics == null) {
                        pics = new ArrayList<>();
                    }

                    List<String> withUsers = (List<String>) loc.get("users");

                    if (withUsers == null) {
                        withUsers = new ArrayList<>();
                    }
                    if (loc.get("timeAdded") != null) {
                        Long lo = (Long) ((Map) loc.get("timeAdded")).get("year");
                        int year = lo.intValue() + 1900;
                        lo = (Long) ((Map) loc.get("timeAdded")).get("month");
                        int month = lo.intValue();
                        lo = (Long) ((Map) loc.get("timeAdded")).get("day");
                        int date = lo.intValue();
                        lo = (Long) ((Map) loc.get("timeAdded")).get("hours");
                        int hour = lo.intValue();
                        lo = (Long) ((Map) loc.get("timeAdded")).get("minutes");
                        int min = lo.intValue();

                        Calendar c = new GregorianCalendar();
                        c.set(year, month, date, hour, min);

                        Location l = new Location(name, description, latitude, longitude, been);
                        l.setPics(pics);
                        l.setUsers(withUsers);
                        l.setCal(c);
                        allLocations.add(l);
                    }
                }
            }
        }
    }

    public void getRecommendations() {
        if (allLocations.size() < 5) {
            recommendationListRecent.addAll(allLocations);
        } else {
            Collections.sort(allLocations);
            for (int i = 0; i < 5; i++) {
                recommendationListRecent.add(allLocations.get(i));
            }
        }
        for (Location loc: recommendationListRecent) {
            Marker m = mMap.addMarker(new MarkerOptions().position(loc.getCoordinates())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            markersRecommendRecent.add(m);
            m.setVisible(false);
        }
        Map<Location, Integer> popularity = new HashMap<>();
        for (Location l : allLocations) {
            String name = l.getName().toLowerCase();
            Location loc = null;
            if (popularity.size() > 0) {
                for (Location l2 : popularity.keySet()) {
                    if (name.equals(l2.getName().toLowerCase())) {
                        loc = l2;
                        break;
                    }
                }
            }
            if (loc != null) {
                popularity.put(loc, popularity.get(loc) + 1);
            } else {
                popularity.put(l, 1);
            }
        }
        // Remove all locations in popularity with value == 1
        popularity.values().remove(1);

        PriorityQueue<Location> pq = new PriorityQueue<>();
        for (Location l : popularity.keySet()) {
            if (pq.size() < 5) {
                pq.add(l);
            } else {
                if (popularity.get(l) > popularity.get(pq.peek())) {
                    pq.poll();
                    pq.add(l);
                }
            }
        }
        recommendationListPopularity.addAll(pq);

        for (Location l : recommendationListPopularity) {
            Marker m = mMap.addMarker(new MarkerOptions().position(l.getCoordinates())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            markersRecommendPopularity.add(m);
            m.setVisible(false);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    }
}

