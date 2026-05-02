package com.example.smd_project;
public class Property {

    private String  id;
    private String  name;
    private String  type;       // "Apartment", "Villa", "House"
    private String  location;
    private String  price;
    private boolean isFeatured;

    public Property(String id, String name, String type,
                    String location, String price, boolean isFeatured) {
        this.id         = id;
        this.name       = name;
        this.type       = type;
        this.location   = location;
        this.price      = price;
        this.isFeatured = isFeatured;
    }

    public String  getId()         { return id; }
    public String  getName()       { return name; }
    public String  getType()       { return type; }
    public String  getLocation()   { return location; }
    public String  getPrice()      { return price; }
    public boolean isFeatured()    { return isFeatured; }
}