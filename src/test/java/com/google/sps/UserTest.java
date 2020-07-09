package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserTest {
  // Pittsburgh Google Office Location
  private final MapLocation USER_MAP_LOCATION = new MapLocation(40.457177, -79.916696);
  private final String USER_ZIPCODE = "15206";
  private final String USER_CITY = "Pittsburgh";
  
  private User userWithMapLocation;
  private User userWithZipCode;
  private User userWithCity;

  @Before
  public void setUp() {
    userWithMapLocation = new User(USER_MAP_LOCATION);
    userWithZipCode = new User(USER_ZIPCODE);
    userWithCity = new User(USER_CITY);
  }

  @Test
  public void geolocationUserWithMapLocation() {
    MapLocation actual = userWithMapLocation.getGeolocation();
    MapLocation expected = new MapLocation(USER_MAP_LOCATION);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void geolocationUserWithZipCode() {
    MapLocation actual = userWithZipCode.getGeolocation();
    MapLocation expected = 
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void geolocationUserWithCity() {
    MapLocation actual = userWithCity.getGeolocation();
    MapLocation expected = 
    Assert.assertEquals(expected, actual);
  }