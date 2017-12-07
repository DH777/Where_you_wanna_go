package project.cis350.upenn.edu.wywg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Console;
import java.util.Map;

/**
 * Created by Xiang Li on 12/4/17.
 */

public class PrivateMessageActivity extends AppCompatActivity {
    DataSnapshot snap;
    String me = LoginActivity.username;
    static DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_message);
        snap = LoginActivity.snap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("logins");
        usersRef = ref.child("users");
        if(! snap.child("users").child(me).hasChild("privateMessage")) {
//            Map<String, String> messages = (Map<String, String>) snap.child("users").child(me).child("privateMessage").getValue();
        }
    }

    private TextView createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setBackgroundColor(0xffffffff);
        textView.setPadding(20, 20, 20, 20);
        return textView;
    }

    private LinearLayout createLayout(String text) {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(lparams);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.addView(createNewTextView(text));
        Button reply = createButton(text);
        ll.addView(reply);
        return ll;
    }

    private Button createButton(String text) {
        Button reply = new Button(this);
        LinearLayout.LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        reply.setLayoutParams(lparams);
        reply.setText("reply");
        reply.setVisibility(View.VISIBLE);
//        reply.setId(id);
        int index = text.indexOf(":");
        final String sender = text.substring(1,index);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(PrivateMessageActivity.this, ReplyActivity.class);
                myIntent.putExtra("sender_name",sender);
                PrivateMessageActivity.this.startActivity(myIntent);
            }
        });
        return reply;
    }


    protected void onStart() {
        super.onStart();
        DatabaseReference messageRef = usersRef.child(me).child("privateMessage");
        messageRef.orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String lastChild = dataSnapshot.getKey().toString();
                String value = dataSnapshot.getValue().toString();
                LinearLayout l = (LinearLayout) findViewById(R.id.all);
                l.addView(createLayout(value));

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
