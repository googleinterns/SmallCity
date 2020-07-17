package com.google.sps.data;

import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.AddressType;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.google.maps.model.LocationType;
import java.util.Arrays;
import java.util.logging.Logger;
import io.github.cdimascio.dotenv.Dotenv;

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
  
  public MapLocation getGeolocation() {
    return geolocation;
  }
}