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
import com.google.sps.data.Listing;
import com.google.sps.data.MapLocation;
import com.google.sps.data.User;

/** Test class for User Object **/
@RunWith(JUnit4.class)
public final class UserTest {
  // Pittsburgh Google Office Location
  private final MapLocation USER_MAP_LOCATION = new MapLocation(40.45717, -79.91669);
  private final String USER_ZIPCODE = "15206";
  private final String USER_CITY = "Pittsburgh";
  private final String USER_ADDRESS = "6425 Penn Ave, Pittsburgh, PA 15206";
  
  private User userWithMapLocation;
  private User userWithZipCode;
  private User userWithCity;
  private User userWithAddress;

  @Before
  public void setUp() {
    userWithMapLocation = new User(USER_MAP_LOCATION);
    userWithZipCode = new User(USER_ZIPCODE);
    userWithCity = new User(USER_CITY);
    userWithAddress = new User(USER_ADDRESS);
  }

  @Test
  public void geolocationUserWithMapLocation() {
    MapLocation actual = userWithMapLocation.getGeolocation();
    MapLocation expected = USER_MAP_LOCATION;
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void geolocationUserWithZipCode() {
    MapLocation actual = userWithZipCode.getGeolocation();
    MapLocation expected = USER_MAP_LOCATION;
    boolean inRange = false;
    // Approximate range of lat and lng coordinates in 15206 ZipCode
    if ((Math.abs(actual.lat - expected.lat) <= 0.06) 
          && (Math.abs(actual.lng - expected.lng)) <= 0.05) {
      inRange = true;
    }
    Assert.assertTrue(inRange);
  }

  @Test
  public void geolocationUserWithCity() {
    MapLocation actual = userWithCity.getGeolocation();
    MapLocation expected = USER_MAP_LOCATION;
    boolean inRange = false;
    // Approximate range of lat and lng coordinates in Pittsburgh
    if ((Math.abs(actual.lat - expected.lat) <= 0.16) 
          && (Math.abs(actual.lng - expected.lng)) <= 0.2) {
      inRange = true;
    }   
    Assert.assertTrue(inRange);
  }

  @Test
  public void geolocationUserWithAddress() {
    MapLocation actual = userWithAddress.getGeolocation();
    MapLocation expected = USER_MAP_LOCATION;
    boolean inRange = false;
    // Small range of error allowed for address geolocation
    if ((Math.abs(actual.lat - expected.lat) <= 0.001) 
          && (Math.abs(actual.lng - expected.lng)) <= 0.001) {
      inRange = true;
    }   
    Assert.assertTrue(inRange);
  }
}