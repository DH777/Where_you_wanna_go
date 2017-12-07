package project.cis350.upenn.edu.wywg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Xiang Li on 11/14/17.
 */

public class TravelMateRecommendationActivity extends AppCompatActivity {
    DataSnapshot snap;
    static DatabaseReference usersRef;
    RadioButton livingClose;
    RadioButton notClose;
    RadioButton male,female,both;
    Button search;
    TextView friends;
    String username = LoginActivity.username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_recommend);
        snap = LoginActivity.snap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("logins");
        usersRef = ref.child("users");
        livingClose = (RadioButton)findViewById(R.id.close_yes);
        notClose = (RadioButton)findViewById(R.id.close_no);
        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);
        both = (RadioButton)findViewById(R.id.both);
        search = (Button)findViewById(R.id.search);
        friends = (TextView)findViewById(R.id.friends);
//        final String city;
//        if(livingClose.isChecked()) {
//            city = "yes";
//        } else {
//            city = "no";
//        }
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city;
                if(livingClose.isChecked()) {
                    city = "yes";
                } else {
                    city = "no";
                }
                String user_gender;
                if(male.isChecked()) {
                    user_gender = "male";
                } else if(female.isChecked()) {
                    user_gender = "female";
                } else {
                    user_gender = "both";
                }
                ArrayList<String> result = recommend(user_gender,city);
                String show = "recommend users: ";
                for(String u : result) {
                    show = show + u + ",";
                }
                friends.setText(show);
            }
        });
        //ArrayList<String> result = recommend("male",city);



    }

    private ArrayList<String> recommend(String gender, String city) {
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> qualified = new ArrayList<>();
        ArrayList<String> recommendUsers = new ArrayList<String>();
        Map<String, Object> m = (Map<String, Object>) snap.child("users").getValue();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            Map singleUser = (Map) entry.getValue();
            users.add((String) singleUser.get("name"));
        }
        users.remove(username);
        String prefer_time;
        if(snap.child("users").child(username).hasChild("prefertime")) {
            prefer_time = snap.child("users").child(username).child("prefertime").getValue().toString();
        }else {
            prefer_time = "none";
        }
        for(String u:users) {
            if(snap.child("users").child(u).hasChild("need_friend")) {
                if(snap.child("users").child(u).child("need_friend").getValue().toString().equals("yes")) {
                    if(snap.child("users").child(u).hasChild("prefertime")) {
                        if(snap.child("users").child(u).child("prefertime").getValue().toString().equals(prefer_time)) {
                            qualified.add(u);
                        }
                    }
                }
            }
        }
        String userCity;
        if(snap.child("users").child(username).hasChild("city")) {
            userCity = snap.child("users").child(username).child("city").getValue().toString().toLowerCase();
            userCity = userCity.replaceAll(" ","");
        } else {
            userCity = "-1";
        }
        if(city.equals("yes")) {
            for(String u : qualified){
                if(snap.child("users").child(u).hasChild("city")) {
                    String uCity = snap.child("users").child(u).child("city").getValue().toString().toLowerCase();
                    uCity = uCity.replaceAll(" ","");
                    if(uCity.equals(userCity)) {
                        recommendUsers.add(u);
                    }

                }
            }
        } else {
            recommendUsers = qualified;
        }
        ArrayList<String> result = new ArrayList<String>();
        if(gender.equals("both")) {
            result = recommendUsers;
        } else {
            for (String u : recommendUsers) {
//            String user_gender;
//            if(male.isChecked()) {
//                user_gender = "male";
//            } else if(female.isChecked()) {
//                user_gender = "female";
//            } else {
//                user_gender = "both";
//            }
                String get_gender;
                if (snap.child("users").child(u).hasChild("gender")) {
                    get_gender = snap.child("users").child(u).child("gender").getValue().toString();
                } else {
                    get_gender = "-1";
                }
                if(get_gender.equals(gender)) {
                    result.add(u);
                }

            }
        }
        return result;
    }
}
