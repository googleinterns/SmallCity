package com.google.sps.data;

/** Class for Listing object with the specified attributes returned from the places API **/
public class Listing{
    private String name;
    private MapLocation mapLocation;
    private int rating;
    private String image;
    private String url;

  /**
  * Creates a new Listing
  * @param name Name of business
  * @param mapLocation Location of business (lat/long coordinate)
  * @param rating Numerical rating of business (1-5)
  * @param image Image
  * @param url Url to business' site
  * @return Listing object
  **/

  public Listing(String name, MapLocation mapLocation, int rating, String image, String url) {
    this.name = name;
    this.mapLocation = mapLocation;
    this.rating = rating;
    this.image = image;
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public MapLocation getMapLocation() {
    return mapLocation;
  }

  public int getRating() {
    return rating;
  }

  public String getImage() {
    return image;
  } 

  public String getUrl() {
    return url;
  }
}