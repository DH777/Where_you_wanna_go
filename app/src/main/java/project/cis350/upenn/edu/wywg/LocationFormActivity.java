package project.cis350.upenn.edu.wywg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class LocationFormActivity extends AppCompatActivity implements View.OnClickListener {
    DataSnapshot snap = LoginActivity.snap;
    String username = LoginActivity.username;
    double lat;
    double longi;
    String locName;
    boolean edit;


    //private RadioGroup radioGroup;
    private RadioGroup radioBeenOrToGo;
    private RadioGroup radioNorthOrSouth;

    private RadioButton radioButton;
    private RatingBar rating;
    private EditText etCost;
    private EditText etTime;
    private EditText etDescription;

    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView imageToUpload;
    Uri selectedImage;
    List<Uri> selectedImages;
    List<ImageView> imageViews;
    List<String> oldPics;

    ArrayList<String> addedUsers;

    int imageCounter = 0;

    Button submitB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_form);

        lat = getIntent().getDoubleExtra("latitude", .0001);
        longi = getIntent().getDoubleExtra("long", .0001);
        locName = getIntent().getStringExtra("name");
        edit = getIntent().getBooleanExtra("edit", false);

        selectedImages = new ArrayList<Uri>();
        imageViews = new ArrayList<ImageView>();
        oldPics = new ArrayList<String>();
        addedUsers = new ArrayList<String>();


        radioBeenOrToGo = (RadioGroup) findViewById(R.id.radioBeenOrToGo);
        radioNorthOrSouth = (RadioGroup) findViewById(R.id.radioNorthOrSouth);

        etDescription = (EditText) findViewById(R.id.etDescription);

        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        rating = (RatingBar) findViewById(R.id.rbRating);
        etCost = (EditText) findViewById(R.id.etCost);

        etTime=(EditText) findViewById(R.id.etTime);

        imageToUpload.setOnClickListener(this);

        submitB = (Button) findViewById(R.id.submit_button);

        Button picB = (Button) findViewById(R.id.add_pic_button);

        Button userB = (Button) findViewById(R.id.add_user_button);

        final EditText name = (EditText) findViewById(R.id.edit_name);
        name.setText(locName);

        if (edit) {
            name.setText(getIntent().getStringExtra("name"));
            etDescription.setText(getIntent().getStringExtra("desc"));
            etCost.setText((getIntent().getDoubleExtra("cost", 0)) + "");
            etTime.setText((getIntent().getDoubleExtra("time", 0)) + "");
            float r = getIntent().getFloatExtra("rating", 0);
            rating.setRating(r);

            boolean been = getIntent().getBooleanExtra("been", false);
            if (!been) {
                radioBeenOrToGo.check(R.id.radioToGo);
            } else {
                radioBeenOrToGo.check(R.id.radioBeen);
            }
            boolean hemisphere = getIntent().getBooleanExtra("hemisphere", false);
            if(!hemisphere){
                radioNorthOrSouth.check(R.id.radioSouth);
            }else{
                radioNorthOrSouth.check(R.id.radioNorth);
            }
            List<Map> locations = FirebaseHandler.getLocationsFromFirebase(username);
            for (int i = 0; i < locations.size(); i++) {
                Map loc = locations.get(i);
                if (loc != null) {
                String locName = (String) loc.get("name");
                if (locName.equals(getIntent().getStringExtra("name"))) {
                    Object latitude1 = ((Map) loc.get("coordinates")).get("latitude");
                    lat = ((Number) latitude1).doubleValue();
                    Object longitude1 = ((Map) loc.get("coordinates")).get("longitude");
                    longi = ((Number) longitude1).doubleValue();

                    addedUsers = (ArrayList<String>) loc.get("users");

                    if (addedUsers == null) {
                        addedUsers = new ArrayList<String>();
                    }

                    List<String> pics = (List<String>) loc.get("pics");
                    if (pics == null) break;
                    if (pics.size() == 0) break;

                    //display pics
                    for (String im : pics) {
                        String base64Image = im;
                        byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                        Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

                        ImageView iv = new ImageView(LocationFormActivity.this);
                        iv.setImageBitmap(b);

                        imageCounter++;
                        LinearLayout picLL = new LinearLayout(LocationFormActivity.this);
                        picLL.layout(400, 300, 500, 0);
                        picLL.setBackgroundColor(Color.WHITE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
                        params.topMargin = getWindowManager().getDefaultDisplay().getHeight() - 550;
                        params.leftMargin = 5 + 220 * (imageCounter - 1);
                        picLL.setLayoutParams(params);
                        picLL.setOrientation(LinearLayout.HORIZONTAL);

                        addContentView(iv, picLL.getLayoutParams());

                        oldPics.add(im);

                        imageViews.add(iv);

                        final ImageView iv2 = iv;

                        iv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // delete
                                iv2.setImageBitmap(null);
                                // get index
                                int toDelete = -1;
                                for (int i = 0; i < imageViews.size(); i++) {
                                    if (imageViews.get(i).equals(iv2)) {
                                        toDelete = i;
                                        break;
                                    }
                                }

                                if (toDelete != -1) {
                                    imageViews.remove(toDelete);
                                    oldPics.remove(toDelete);
                                }
                                imageCounter--;
                            }
                        });
                    }
                    break;
                }
                }
            }
        }

        //Listener on Submit button
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit) {
                    // get selected radio button from radioGroup
                    int selectedId = radioBeenOrToGo.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);
                    String radioText = radioButton.getText().toString();
                    boolean been = false;
                    if (radioText.equals("I have been here!")) {
                        been = true;
                    }

                    List<Location> td2 = (ArrayList<Location>) snap.child("users").child(username).child("locations").getValue();
                    Location l = new Location(name.getText().toString(), etDescription.getText().toString(), lat, longi, been);
                    l.setRating(rating.getRating());
                    if (etCost.getText() != null) {
                        String c = etCost.getText().toString();
                        if (c != null && c.length() > 0) {
                            l.setCost(Double.parseDouble(c));
                        }
                    }


                    if (etTime.getText()!=null){
                        String t = etTime.getText().toString();
                        if (t != null && t.length() > 0) {
                            l.setTime(Double.parseDouble(t));
                        }
                    }

                    int selectedId1 = radioNorthOrSouth.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(selectedId1);
                    String radioText1 = radioButton.getText().toString();
                    boolean hemisphere=false;
                    if (radioText1.equals("North Hemisphere")) {
                        hemisphere=true;
                        l.setHemisphere(hemisphere);
                    }

                    if (selectedImage != null) {
                        for (Uri im : selectedImages) {
                            String base64Image = null;
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory
                            Bitmap bitmap = uriToBitmap(selectedImage);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] bytes = baos.toByteArray();
                            base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
                            l.addPic(base64Image);
                        }
                    }

                    for (String im: oldPics) {
                        l.addPic(im);
                    }

                    // remove old
                    List<Map> locations = FirebaseHandler.getLocationsFromFirebase(username);
                    int oldIndex = 0;
                    findOld(oldIndex,locations);
                    td2.remove(oldIndex);

                    for (String u : addedUsers) {
                        List<Location> u_loc = (ArrayList<Location>) snap.child("users").child(u).child("locations").getValue();

                        u_loc.add(l);

                        // remove old
                        List<Map> locations2 = (ArrayList<Map>) snap.child("users").child(u).child("locations").getValue();
                        int oldIndex2 = 0;
                        findOld(oldIndex2,locations2);
                    
                        td2.remove(oldIndex2);

                        LoginActivity.usersRef.child(u).child("locations").setValue(u_loc);
                        l.addUser(u);
                    }

                    Date d = new Date();
                    l.setTimeAdded(d);

                    td2.add(l);

                    LoginActivity.usersRef.child(username).child("locations").setValue(td2);
                } else {
                    // get selected radio button from radioGroup
                    int selectedId = radioBeenOrToGo.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);
                    String radioText = radioButton.getText().toString();
                    boolean been = false;
                    if (radioText.equals("I have been here!")) {
                        been = true;
                    }

                    List<Location> td2 = (ArrayList<Location>) snap.child("users").child(username).child("locations").getValue();
                    Location l = new Location(name.getText().toString(), etDescription.getText().toString(), lat, longi, been);
                    l.setRating(rating.getRating());
                    if (etCost.getText() != null) {
                        String c = etCost.getText().toString();
                        if (c != null && c.length() > 0) {
                            l.setCost(Double.parseDouble(c));
                        }
                    }


                    if (etTime.getText() != null) {
                        String t = etTime.getText().toString();
                        if (t != null && t.length() > 0) {
                            l.setTime(Double.parseDouble(t));
                        }
                    }

                    int selectedId1 = radioNorthOrSouth.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId1);
                    String radioText1 = radioButton.getText().toString();
                    boolean hemisphere = false;
                    if (radioText1.equals("North Hemisphere")) {
                        hemisphere = true;
                        l.setHemisphere(hemisphere);
                    }


                    if (selectedImage != null) {
                        for (Uri im : selectedImages) {
                            String base64Image = null;

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory
                            Bitmap bitmap = uriToBitmap(selectedImage);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] bytes = baos.toByteArray();
                            base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
                            l.addPic(base64Image);
                        }
                    }

                    for (String u : addedUsers) {
                        List<Location> u_loc = (ArrayList<Location>) snap.child("users").child(u).child("locations").getValue();

                        u_loc.add(l);
                        LoginActivity.usersRef.child(u).child("locations").setValue(u_loc);
                        l.addUser(u);
                    }

                    Date d = new Date();
                    l.setTimeAdded(d);

                    td2.add(l);
                    LoginActivity.usersRef.child(username).child("locations").setValue(td2);
                }

                Intent myIntent = new Intent(LocationFormActivity.this, MapsActivity.class);
                LocationFormActivity.this.startActivity(myIntent);
            }
        });

        picB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                imageCounter++;
            }


        });

        userB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(LocationFormActivity.this, UsersListActivity.class);
                myIntent.putExtra("form", true);
                myIntent.putStringArrayListExtra("users", addedUsers);
                LocationFormActivity.this.startActivityForResult(myIntent, 3);

            }


        });
    }

    private void findOld(int index, List<Map> location) {
        for(int i = 0; i < location.size(); i++) {
            Map loc = location.get(i);
            if(loc != null) {
                String name = (String) loc.get("name");
                if (name.equals(locName)) {
                    index = i;
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add_pic_button:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            selectedImages.add(selectedImage);
            imageToUpload.setImageURI(selectedImage);

            ImageView i = new ImageView(LocationFormActivity.this);
            i.setImageURI(selectedImage);

            LinearLayout picLL = new LinearLayout(LocationFormActivity.this);
            picLL.layout(400, 500, 500, 0);
            picLL.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
            params.topMargin = getWindowManager().getDefaultDisplay().getHeight() - 550;

            params.leftMargin = 5 + 220*(imageCounter-1);
            picLL.setLayoutParams(params);
            picLL.setOrientation(LinearLayout.HORIZONTAL);

            addContentView(i, picLL.getLayoutParams());

            imageViews.add(i);

            final ImageView iv = i;

            i.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // delete
                    iv.setImageBitmap(null);

                    // get index
                    int toDelete = -1;
                    for (int i = 0; i < imageViews.size(); i++) {
                        if (imageViews.get(i).equals(iv)) {
                            toDelete = i;
                            break;
                        }
                    }

                    if (toDelete != -1) {
                        imageViews.remove(toDelete);
                        selectedImages.remove(toDelete);
                    }

                    imageCounter--;
                }
            });
        }
        if (requestCode == 3 || resultCode == 3) {
            addedUsers = data.getStringArrayListExtra("addedUsers");
        }
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        Bitmap image = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public boolean userExists(String name) {
        return snap.child("users").child(name).getValue() != null;
    }
}
