package project.cis350.upenn.edu.wywg;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mathias on 12/2/2017.
 */

public class FirebaseHandler {
    public static List<Map> getLocationsFromFirebase(String uname) {
        Object databaseObj = LoginActivity.snap.child("users").child(uname).child("locations").getValue();
        List<Map> locations = new ArrayList<>();
        if (databaseObj instanceof List) {
            locations = (ArrayList<Map>) LoginActivity.snap.child("users").child(uname).child("locations").getValue();
        } else if (databaseObj instanceof Map) {
            Map dataMap = (Map) databaseObj;
            Set dataSet = dataMap.entrySet();
            Iterator it = dataSet.iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                locations.add((Map) pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
            Log.v("FirebaseHandler", "Map");
        } else {
            throw new IllegalStateException();
        }
        return locations;
    }
    public static ArrayList<String> getUsers() {
        ArrayList<String> users = new ArrayList<>();
        Map<String, Object> m = (Map<String, Object>) LoginActivity.snap.child("users").getValue();

        for (Map.Entry<String, Object> entry : m.entrySet()) {
            Map singleUser = (Map) entry.getValue();
            users.add((String) singleUser.get("name"));
        }
        return users;
    }


}
