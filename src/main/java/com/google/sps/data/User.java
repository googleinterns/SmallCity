package com.google.sps.data;

/** User representing a location **/
public class User {

  private MapLocation geolocation;

  /** Creates a user with a geolocation
  * @param geolocation lat/lng coordinate
  * @return User with geolocation
  **/
  public User(MapLocation geolocation) {
    this.geolocation = geolocation;
  }
  
  /** Creates a user with a zipcode
  * @param zipcode
  * @return User with zipCode
  **/
  public User(int zipCode) {
    this.geolocation = zipToMapLocation(zipCode);
  }

  public MapLocation getGeolocation() {
    return geolocation;
  }

  private MapLocation zipToMapLocation(int zipCode) [
    // TODO: Implement algorithm with Geocoding API
  ]
}