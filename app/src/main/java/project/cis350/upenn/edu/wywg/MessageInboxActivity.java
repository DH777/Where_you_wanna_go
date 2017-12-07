package project.cis350.upenn.edu.wywg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangli on 11/27/17.
 */

public class MessageInboxActivity extends AppCompatActivity {

    DataSnapshot snap;
    String me = LoginActivity.username;
    static DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);
        snap = LoginActivity.snap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("logins");
        usersRef = ref.child("users");
        if(snap.child("users").child(me).hasChild("message")) {
            Map<String, String> messages = (Map<String, String>) snap.child("users").child(me).child("message").getValue();
            LinearLayout l = (LinearLayout) findViewById(R.id.all);
            for (String p : messages.keySet()) {
                String temp = messages.get(p);

//                l.addView(createNewTextView(temp));
            }
        }
    }

    private TextView createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setBackgroundColor(0xffffffff);
        textView.setPadding(20, 20, 20, 20);
        return textView;
    }

    protected void onStart() {
        super.onStart();
        DatabaseReference messageRef = usersRef.child(me).child("message");
        messageRef.orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String lastChild = dataSnapshot.getKey().toString();
                String value = dataSnapshot.getValue().toString();
                LinearLayout l = (LinearLayout) findViewById(R.id.all);
                l.addView(createNewTextView(value));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
