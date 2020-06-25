package com.google.sps.data;

/** Class representing a MapLocation as a coordinate **/
public class MapLocation {
  public long lat;
  public long lng;
  
  /** Creates a new MapLocation
  * @param lat latitude value
  * @param lng longitude value
  * @return MapLocation object
  **/
  
  public MapLocation (long lat, long lng) {
    this.lat = lat;
    this.lng = lng;
  }
}