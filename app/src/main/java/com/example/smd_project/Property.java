package com.example.smd_project;

import java.io.Serializable;

public class Property implements Serializable {

    private String  id;
    private String  name;
    private String  type;
    private String  location;
    private int     price;
    private boolean isFeatured;
    private String  imageUrl;
    private String  description;
    private String  ownerName;
    private String  ownerPhone;

    public Property(String id, String name, String type,
                    String location, int price, boolean isFeatured, String imageUrl,
                    String description, String ownerName, String ownerPhone) {
        this.id          = id;
        this.name        = name;
        this.type        = type;
        this.location    = location;
        this.price       = price;
        this.isFeatured  = isFeatured;
        this.imageUrl    = imageUrl;
        this.description = description;
        this.ownerName   = ownerName;
        this.ownerPhone  = ownerPhone;
    }

    public Property(String id, String name, String type,
                    String location, int price, boolean isFeatured, String imageUrl) {
        this(id, name, type, location, price, isFeatured, imageUrl, "", "", "");
    }

    public String  getId()          { return id; }
    public String  getName()        { return name; }
    public String  getType()        { return type; }
    public String  getLocation()    { return location; }
    public int     getPrice()       { return price; }
    public boolean isFeatured()     { return isFeatured; }
    public String  getImageUrl()    { return imageUrl; }
    public String  getDescription() { return description; }
    public String  getOwnerName()   { return ownerName; }
    public String  getOwnerPhone()  { return ownerPhone; }
}
