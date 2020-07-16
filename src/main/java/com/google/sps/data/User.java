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
  private Dotenv dotenv = Dotenv.configure().load();

  private MapLocation geolocation;
  private final String KEY = dotenv.get("APIKey");
  private final static Logger LOGGER = Logger.getLogger(User.class.getName());

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
  public User(String zipCode) {
    this.geolocation = zipCodeToMapLocation(zipCode);
  }

  public MapLocation getGeolocation() {
    return geolocation;
  }

  private MapLocation zipCodeToMapLocation(String zipCode) {
    final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(KEY)
            .build();
    final GeocodingResult[] results;
    try {
        results = GeocodingApi.geocode(context, zipCode).await();
        MapLocation geolocation = new MapLocation(results[0].geometry.location.lat, results[0].geometry.location.lng);
        return geolocation;
    } catch (final Exception e) {
        LOGGER.warning(e.getMessage());
    }
    return new MapLocation(0,0);  // null Island
  }
}