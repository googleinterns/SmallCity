package com.google.sps.data;

import com.google.maps.model.Photo;

/** Listing object with the specified attributes returned from the places API **/
public class Listing {
    private String name;
    private String formattedAddress;
    private MapLocation mapLocation;
    private double rating;
    private Photo photos[];
    private String businessTypes[];
    public String placeId;

  /**
  * Creates a new Listing
  * @param name Name of business
  * @param formattedAddress business address
  * @param mapLocation Location of business (lat/long coordinate)
  * @param rating Numerical rating of business (1-5)
  * @param photos Array of Google Photo objects
  * @param businessType specifies business type from Places API
  * @return Listing object
  **/

  public Listing(String name, String formattedAddress, MapLocation mapLocation,
        double rating, Photo photos[], String businessTypes[]) {
    this.name = name;
    this.formattedAddress = formattedAddress;
    this.mapLocation = mapLocation;
    this.rating = rating;
    this.photos = photos;
    this.businessTypes = businessTypes;
  }

  public void addPlaceId(String placeId) {
    this.placeId = placeId;
  }

  public String getName() {
    return name;
  }

  public String getFormattedAddress() {
    return formattedAddress;
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