package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Math;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.SmallCityService;
import com.google.sps.data.UserService;
import com.google.sps.data.Listing;
import com.google.sps.data.MapLocation;
import com.google.sps.data.User;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.AddressType;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.google.maps.model.LocationType;
import java.util.logging.Logger;
import io.github.cdimascio.dotenv.Dotenv;

/** Test class for User Object **/
@RunWith(JUnit4.class)
public final class UserServiceTest {
  private Dotenv dotenv = Dotenv.configure().load();

  // Pittsburgh Google Office Location
  private final MapLocation TEST_USER_MAP_LOCATION 
        = new MapLocation(40.45717, -79.91669);
  private final String TEST_USER_ZIPCODE = "15206";
  private final String TEST_USER_CITY = "Pittsburgh";
  private final String TEST_USER_ADDRESS 
        = "6425 Penn Ave, Pittsburgh, PA 15206";
  
  private UserService test_userServiceWithMapLocation;
  private UserService test_userServiceWithZipCode;
  private UserService test_userServiceWithCity;
  private UserService test_userServiceWithAddress;

  private final String KEY = dotenv.get("APIKey");
  private final static Logger LOGGER 
        = Logger.getLogger(UserServiceTest.class.getName());

  @Test
  public void geolocationUserWithMapLocation() {
    test_userServiceWithMapLocation = new UserService();
    test_userServiceWithMapLocation.createUserWithGeolocation(TEST_USER_MAP_LOCATION);
    MapLocation actual = test_userServiceWithMapLocation.user.geolocation;
    MapLocation expected = TEST_USER_MAP_LOCATION;
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void geolocationUserWithZipCode() {
    test_userServiceWithZipCode = new UserService();
    test_userServiceWithZipCode.createUserWithZipCode(TEST_USER_ZIPCODE);
    MapLocation actual = test_userServiceWithZipCode.user.geolocation;
    String address = reverseGeocode(actual);
    Assert.assertTrue(address.contains(TEST_USER_ZIPCODE));
  }

  @Test
  public void geolocationUserWithCity() {
    test_userServiceWithCity = new UserService();
    test_userServiceWithCity.createUserWithZipCode(TEST_USER_CITY);
    MapLocation actual = test_userServiceWithCity.user.geolocation;
    String address = reverseGeocode(actual);  
    Assert.assertTrue(address.contains(TEST_USER_CITY));
  }

  @Test
  public void geolocationUserWithAddress() {
    test_userServiceWithAddress = new UserService();
    test_userServiceWithAddress.createUserWithZipCode(TEST_USER_ADDRESS);
    MapLocation actual = test_userServiceWithAddress.user.geolocation;
    String address = reverseGeocode(actual); 
    Assert.assertTrue(address.contains(TEST_USER_ADDRESS));
  }
  
  /** Returns formatted address from coordinate using Geocoding API **/
  private String reverseGeocode(MapLocation mapLocation) {
    final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(KEY)
            .build();
    final GeocodingResult[] results;
    final LatLng latLng = new LatLng(mapLocation.lat, mapLocation.lng);
    try {
        results = GeocodingApi.reverseGeocode(context, latLng).await();
        String address = results[0].formattedAddress;
        return address;
    } catch (final Exception e) {
        LOGGER.warning(e.getMessage());
    }
    return "FAILURE";
  }
}