package com.example.hotcold;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Nodes {

    public String nodeLabel;
    public double latitude,longtit;

    public Nodes() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
     }

    public Nodes(String name,double lat ,double log) {
        this.nodeLabel=name;
        this.latitude=lat;
        this.longtit=log;
    }
}
