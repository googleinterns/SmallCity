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
  private final MapLocation TEST_USER_MAP_LOCATION = new MapLocation(40.45717, -79.91669);
  private final String TEST_USER_ZIPCODE = "15206";
  private final String TEST_USER_CITY = "Pittsburgh";
  private final String TEST_USER_ADDRESS = "6425 Penn Ave, Pittsburgh, PA 15206";
  
  private User test_userWithMapLocation;
  private User test_userWithZipCode;
  private User test_userWithCity;
  private User test_userWithAddress;

  @Test
  public void geolocationUserWithMapLocation() {
    test_userWithMapLocation = new User(TEST_USER_MAP_LOCATION);
    MapLocation actual = test_userWithMapLocation.getGeolocation();
    MapLocation expected = TEST_USER_MAP_LOCATION;
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void geolocationUserWithZipCode() {
    test_userWithZipCode = new User(TEST_USER_ZIPCODE);
    MapLocation actual = test_userWithZipCode.getGeolocation();
    MapLocation expected = TEST_USER_MAP_LOCATION;
    boolean inRange = false;
    // Approximate range of lat and lng coordinate difference of corners in 15206 ZipCode
    if ((Math.abs(actual.lat - expected.lat) <= 0.06) 
          && (Math.abs(actual.lng - expected.lng)) <= 0.05) {
      inRange = true;
    }
    Assert.assertTrue(inRange);
  }

  @Test
  public void geolocationUserWithCity() {
    test_userWithCity = new User(TEST_USER_CITY);
    MapLocation actual = test_userWithCity.getGeolocation();
    MapLocation expected = TEST_USER_MAP_LOCATION;
    boolean inRange = false;
    // Approximate range of lat and lng coordinate difference of corners in Pittsburgh
    if ((Math.abs(actual.lat - expected.lat) <= 0.16) 
          && (Math.abs(actual.lng - expected.lng)) <= 0.2) {
      inRange = true;
    }   
    Assert.assertTrue(inRange);
  }

  @Test
  public void geolocationUserWithAddress() {
    test_userWithAddress = new User(TEST_USER_ADDRESS);
    MapLocation actual = test_userWithAddress.getGeolocation();
    MapLocation expected = TEST_USER_MAP_LOCATION;
    boolean inRange = false;
    // Small range of error allowed for address geolocation
    if ((Math.abs(actual.lat - expected.lat) <= 0.001) 
          && (Math.abs(actual.lng - expected.lng)) <= 0.001) {
      inRange = true;
    }   
    Assert.assertTrue(inRange);
  }
}