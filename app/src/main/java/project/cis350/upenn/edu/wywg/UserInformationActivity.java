package project.cis350.upenn.edu.wywg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Xiang Li on 11/13/17.
 */

public class UserInformationActivity extends AppCompatActivity {

    EditText email;
    EditText city;
    EditText friend_need;
    TextView name;
    RadioButton agree;
    RadioButton disagree;
    RadioButton male;
    RadioButton female;
    RadioButton time1;
    RadioButton time2;
    RadioButton time3;
    RadioButton time4;
    String userName;
    DataSnapshot snap;
    static DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        snap = LoginActivity.snap;
        email = (EditText)findViewById(R.id.emailAdr);
        city = (EditText)findViewById(R.id.city_live);
        name = (TextView)findViewById(R.id.tvName);
        agree = (RadioButton) findViewById(R.id.yes) ;
        disagree = (RadioButton) findViewById(R.id.no);
        male = (RadioButton)findViewById(R.id.male) ;
        female = (RadioButton)findViewById(R.id.female) ;
        time1 = (RadioButton)findViewById(R.id.time1);
        time2 = (RadioButton)findViewById(R.id.time2);
        time3 = (RadioButton)findViewById(R.id.time3);
        time4 = (RadioButton)findViewById(R.id.time4);

        userName = LoginActivity.username;
        name.setText(userName);
        String e;
        if(snap.child("users").child(userName).hasChild("email")) {
            e = snap.child("users").child(userName).child("email").getValue().toString();
        } else {
            e = " ";
        }
        email.setText(e);
        String c;
        if(snap.child("users").child(userName).hasChild("city")) {
            c = snap.child("users").child(userName).child("city").getValue().toString();
        } else {
            c = " ";
        }
        city.setText(c);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("logins");
        usersRef = ref.child("users");
        Button editInfo = (Button) findViewById(R.id.save);
        Button enableEdit = (Button)findViewById(R.id.edit) ;
        Button jump = (Button) findViewById(R.id.map);

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(UserInformationActivity.this, MapsActivity.class);
                UserInformationActivity.this.startActivity(myIntent);
            }
        });


        enableEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setEnabled(true);
                email.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                city.setEnabled(true);
                city.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                agree.setEnabled(true);
                disagree.setEnabled(true);
                male.setEnabled(true);
                female.setEnabled(true);
                time1.setEnabled(true);
                time2.setEnabled(true);
                time3.setEnabled(true);
                time4.setEnabled(true);
            }
        });
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String need_origin;
                if(snap.child("users").child(userName).hasChild("need_friend")) {
                    need_origin = snap.child("users").child(userName).child("need_friend").getValue().toString();
                } else {
                    need_origin = "no such information";
                }
                String need;
                if(agree.isChecked()) {
                    need = "yes";
                } else if(disagree.isChecked()){
                    need = "no";
                } else {
                    need = need_origin;
                }
                String e = email.getText().toString();
                String c = city.getText().toString();
                String gender_origin;
                if(snap.child("users").child(userName).hasChild("gender")) {
                    gender_origin = snap.child("users").child(userName).child("gender").getValue().toString();
                } else {
                    gender_origin = "no such information";
                }
                String gender;
                if(male.isChecked()) {
                    gender = "male";
                } else if(female.isChecked()) {
                    gender = "female";
                }else{
                    gender = gender_origin;
                }
                String time_origin;
                if(snap.child("users").child(userName).hasChild("prefertime")) {
                    time_origin = snap.child("users").child(userName).child("prefertime").getValue().toString();
                } else {
                    time_origin = "no such information";
                }

                String time;
                if(time1.isChecked()) {
                    time = "Dec.-Feb.";
                } else if(time2.isChecked()){
                    time = "Mar.-May";
                }else if(time3.isChecked()){
                    time = "June-Aug.";
                } else if(time4.isChecked()) {
                    time = "Sept.-Nov.";
                }else {
                    time = time_origin;
                }
                //String need = friend_need.getText().toString();
                usersRef.child(userName).child("email").setValue(e);
                usersRef.child(userName).child("city").setValue(c);
                usersRef.child(userName).child("need_friend").setValue(need);
                usersRef.child(userName).child("gender").setValue(gender);
                usersRef.child(userName).child("prefertime").setValue(time);
                Intent myIntent = new Intent(UserInformationActivity.this, MapsActivity.class);
                UserInformationActivity.this.startActivity(myIntent);
            }
        }) ;

    }
}
