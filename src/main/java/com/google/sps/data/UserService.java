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

public class UserService {
  
  private Dotenv dotenv = Dotenv.configure().load();
  private final String KEY = dotenv.get("APIKey");
  private final static Logger LOGGER = Logger.getLogger(UserService.class.getName());

  public User user;

  /** Creates a userService with a geolocation
  * @param mapLocation lat/lng coordinate
  * @return UserService with user
  **/
  public UserService(MapLocation mapLocation) {
    user = new User(mapLocation);
  }

  /** Creates a userService with a zipCode
  * @param zipCode zipCode
  * @return UserService with user
  **/
  public UserService(String zipCode) {
    MapLocation mapLocation = zipCodeToMapLocation(zipCode);
    user = new User(mapLocation);
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