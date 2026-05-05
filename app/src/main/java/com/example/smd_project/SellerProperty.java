package com.example.smd_project;

import java.io.Serializable;

public class SellerProperty implements Serializable {
    private String id;
    private String title;
    private double price;
    private String location;
    private String description;
    private String category;
    private String type;
    private int    bedrooms;
    private int    bathrooms;
    private int    livingRooms;
    private int    kitchen;
    private int    garage;
    private int    garden;
    private int    livingArea;
    private int    yearBuilt;
    private String imageUrl;
    private String ownerId;
    private String ownerName;
    private String ownerPhone;

    public SellerProperty() {}

    public SellerProperty(String id, String title, double price, String location,
                          String description, String category, String type,
                          int bedrooms, int bathrooms, int livingRooms,
                          int kitchen, int garage, int garden,
                          int livingArea, int yearBuilt,
                          String imageUrl, String ownerId, String ownerName, String ownerPhone) {
        this.id          = id;
        this.title       = title;
        this.price       = price;
        this.location    = location;
        this.description = description;
        this.category    = category;
        this.type        = type;
        this.bedrooms    = bedrooms;
        this.bathrooms   = bathrooms;
        this.livingRooms = livingRooms;
        this.kitchen     = kitchen;
        this.garage      = garage;
        this.garden      = garden;
        this.livingArea  = livingArea;
        this.yearBuilt   = yearBuilt;
        this.imageUrl    = imageUrl;
        this.ownerId     = ownerId;
        this.ownerName   = ownerName;
        this.ownerPhone  = ownerPhone;
    }

    public String getId()          { return id; }
    public void   setId(String id) { this.id = id; }

    public String getTitle()             { return title; }
    public void   setTitle(String title) { this.title = title; }

    public double getPrice()              { return price; }
    public void   setPrice(double price)  { this.price = price; }

    public String getLocation()                { return location; }
    public void   setLocation(String location) { this.location = location; }

    public String getDescription()                   { return description; }
    public void   setDescription(String description) { this.description = description; }

    public String getCategory()                { return category; }
    public void   setCategory(String category) { this.category = category; }

    public String getType()            { return type; }
    public void   setType(String type) { this.type = type; }

    public int  getBedrooms()             { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    public int  getBathrooms()              { return bathrooms; }
    public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }

    public int  getLivingRooms()                { return livingRooms; }
    public void setLivingRooms(int livingRooms) { this.livingRooms = livingRooms; }

    public int  getKitchen()             { return kitchen; }
    public void setKitchen(int kitchen)  { this.kitchen = kitchen; }

    public int  getGarage()             { return garage; }
    public void setGarage(int garage)   { this.garage = garage; }

    public int  getGarden()             { return garden; }
    public void setGarden(int garden)   { this.garden = garden; }

    public int  getLivingArea()               { return livingArea; }
    public void setLivingArea(int livingArea) { this.livingArea = livingArea; }

    public int  getYearBuilt()              { return yearBuilt; }
    public void setYearBuilt(int yearBuilt) { this.yearBuilt = yearBuilt; }

    public String getImageUrl()                { return imageUrl; }
    public void   setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getOwnerId()                 { return ownerId; }
    public void   setOwnerId(String ownerId)   { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
}