package project.cis350.upenn.edu.wywg;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by abhaved on 2/14/17.
 */
@Parcel
public class Location implements Comparable<Location> {
    protected String name;
    protected double temperature;
    protected double rating;
    protected double cost;
    protected double time;

    protected LatLng coordinates;

    protected boolean visited;
    protected boolean hemisphere;
    //protected String journal = "";
    protected String description ="";
    protected List<String> pics = new ArrayList<String>();
    protected List<String> users = new ArrayList<String>();

    protected Date timeAdded = null;
    protected Calendar cal = null;
    
  public Location(){
    }

    public Location(String name, String description, double lat, double longitude,
                    Boolean hasVisited) {
        this.name = name;
        this.description = description;
        this.coordinates = new LatLng(lat, longitude);

        if (hasVisited == null) {
            this.visited = false;
        } else {
            this.visited = hasVisited;
        }

        /*if (journalEntry == null) {
            this.journal = "";
        } else {
            this.journal = journalEntry;
        }*/
            
    }
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }


    public double getTime(){return time;}

    public void setTime(double time){this.time=time;}

    public double getLongitude() {
        return coordinates.longitude;
    }

    public double getLatitude() {
        return coordinates.latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    /*public String getJournal() {
        return journal;
    }*/

    /*public void setJournal(String journal) {
        this.journal = journal;
    }*/

    public boolean getVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean getHemisphere() {
        return hemisphere;
    }

    public void setHemisphere(boolean hemisphere) {
        this.hemisphere = hemisphere;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public void addPic(String pic) {
        pics.add(pic);
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void addUser(String user) {
        users.add(user);
    }

    public Date getTimeAdded() { return timeAdded; }

    public void setTimeAdded(Date timeAdded) { this.timeAdded = timeAdded; }

    public Calendar getCal() { return cal; }

    public void setCal(Calendar cal) { this.cal = cal; }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double t) {
        temperature = t;
    }

    @Override
    public int compareTo(Location o) {
        return o.getCal().compareTo(cal);
    }

}

// protected ArrayList<Drawable> images;
//    public ArrayList<Drawable> getImages() {
//        return images;
//    }
//
//    public void addImage(Drawable image) {
//        images.add(image);
//    }

// #####################
//    public boolean ifNotified(){
//        return notified;
//    }

//    public void Notify(){
//        notified = true;
//    }

//##############
//     protected String p;
//public void setP(String pic){
//    p = pic;
//}

//public String getP(){
//    return p;
//}

//########
//     protected String date = null;
//public String getDate() { return date; }

//public void setDate(String date) { this.date = date; }