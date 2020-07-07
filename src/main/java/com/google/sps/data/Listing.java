package com.google.sps.data;

/** Listing object with the specified attributes returned from the places API **/
public class Listing {
    private String name;
    private MapLocation mapLocation;
    private double rating;
    private String image;
    private String url;
    private String businessType;

  /**
  * Creates a new Listing
  * @param name Name of business
  * @param mapLocation Location of business (lat/long coordinate)
  * @param rating Numerical rating of business (1-5)
  * @param image Image
  * @param url Url to business' site
  * @param businessType specifies business type from Places API (establishment, food, etc.)
  * @return Listing object
  **/

  public Listing(String name, MapLocation mapLocation, double rating, String image, String url) {
    this.name = name;
    this.mapLocation = mapLocation;
    this.rating = rating;
    this.image = image;
    this.url = url;
    this.businessType = businessType;
  }

  public String getName() {
    return name;
  }

  public MapLocation getMapLocation() {
    return mapLocation;
  }

  public double getRating() {
    return rating;
  }

  public String getImage() {
    return image;
  } 

  public String getUrl() {
    return url;
  }

  public String getBusinessType() {
    return businessType;
  }
}