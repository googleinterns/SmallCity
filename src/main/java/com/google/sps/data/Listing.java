package com.google.sps.data;
import com.google.maps.model.Photo;

/** Listing object with the specified attributes returned from the places API **/
public class Listing {
    private String name;
    private MapLocation mapLocation;
    private double rating;
    private Photo photos[];
    private String businessTypes[];

  /**
  * Creates a new Listing
  * @param name Name of business
  * @param mapLocation Location of business (lat/long coordinate)
  * @param rating Numerical rating of business (1-5)
  * @param photos Array of Google Photo objects
  * @param businessType specifies business type from Places API (establishment, food, etc.)
  * @return Listing object
  **/

  public Listing(String name, MapLocation mapLocation, double rating, Photo photos[], String businessTypes[]) {
    this.name = name;
    this.mapLocation = mapLocation;
    this.rating = rating;
    this.photos = photos;
    this.businessTypes = businessTypes;
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

  public Photo[] getPhotos() {
    return photos;
  } 

  public String[] getBusinessTypes() {
    return businessTypes;
  }
}