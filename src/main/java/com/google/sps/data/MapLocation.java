package com.google.sps.data;

/** MapLocation representing coordinate values **/
public class MapLocation {
  public double lat;
  public double lng;
  
  /** Creates a new MapLocation
  * @param lat latitude value
  * @param lng longitude value
  * @return MapLocation object
  **/

  public MapLocation (double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }
}