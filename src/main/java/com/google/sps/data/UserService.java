package com.google.sps.data;

import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.GeoApiContext;
import java.util.logging.Logger;
import io.github.cdimascio.dotenv.Dotenv;

public class UserService {
  private Dotenv dotenv = Dotenv.configure().filename("env").load();
  
  private final String KEY = dotenv.get("APIKEY");
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