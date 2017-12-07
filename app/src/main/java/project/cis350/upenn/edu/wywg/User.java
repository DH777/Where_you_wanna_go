package project.cis350.upenn.edu.wywg;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by abhaved on 2/23/17.
 */

public class User {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String password;
    String name;
    Set<Location> locations;
    public User(String n, String p, Set<Location> s) {
        password = p;
        name = n;
        locations = s;

    }
}
