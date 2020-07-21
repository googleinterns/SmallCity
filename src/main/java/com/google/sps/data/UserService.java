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

public class UserService {
  
  private final String KEY = "REDACTED";
  private final static Logger LOGGER = Logger.getLogger(UserService.class.getName());

  public User user;

  public UserService() { }
  
  public void createUserWithGeolocation(MapLocation mapLocation) {
    user = new User();
    user.geolocation = mapLocation;
  }
  
  public void createUserWithZipCode(String zipCode) {
    user = new User();
    user.geolocation = zipCodeToMapLocation(zipCode);
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