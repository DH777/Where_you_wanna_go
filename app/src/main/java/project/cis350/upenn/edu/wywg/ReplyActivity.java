package project.cis350.upenn.edu.wywg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Created by Xiang Li on 12/4/17.
 */

public class ReplyActivity extends AppCompatActivity {
    DataSnapshot snap;
    static DatabaseReference usersRef;
    String me = LoginActivity.username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reply);
        snap = LoginActivity.snap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("logins");
        usersRef = ref.child("users");
        Button send = (Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText e = (EditText) findViewById(R.id.message);
                String content = e.getText().toString();
                Intent myIntent = new Intent(ReplyActivity.this, PrivateMessageActivity.class);
                ReplyActivity.this.startActivity(myIntent);
                String s = getIntent().getStringExtra("sender_name");
                usersRef.child(s).child("privateMessage").push().setValue("@"+me+": "+content);
            }
        });
    }
}
