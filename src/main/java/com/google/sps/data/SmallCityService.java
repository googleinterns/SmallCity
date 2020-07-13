package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.io.Serializable;
import java.net.URL;
import java.util.logging.Logger;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.model.LatLng;
import com.google.maps.model.LocationType;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceType;
import com.google.maps.model.RankBy;
import com.google.maps.model.Geometry;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import java.util.Iterator;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** SmallCityService object representing all components of the webapp **/
public class SmallCityService {
  
  private User user;
  private BusinessesService businessesService;
  private List<Listing> businesses;
  private final static Logger LOGGER = Logger.getLogger(SmallCityService.class.getName());
  private PreparedQuery databaseResults;

  public SmallCityService() { }
  
  /** 
  * Create User instance from zipCode and get businesses list
  * @param zipCode inputted zipcode of user
  **/
  public void createUserWithZip(String zipCode) {
    this.user = new User(zipCode);
    getSmallBusinesses();
  }
  
  /** 
  * Create User instance from geolocation and get businesses list
  * @param mapLocation found geolocation of user
  **/
  public void createUserWithGeolocation(MapLocation mapLocation) {
    this.user = new User(mapLocation);
    getSmallBusinesses();
  }
  
  public void findAllBusinesses() {
    businesses = businessesService.getBusinessesFromPlacesApi(user);
  }
  
  // To be used for unit testing file to be able to 
  // set any static business LinkedList we want to try to use
  public void setAllBusinesses(List<Listing> allBusinesses) {
    businessesService.setAllBusinesses(allBusinesses);
  }

  // To remove the big businesses from the list 
  // that will be returned from the use of the Places API 
  public void filterTheListOfBusinessesIntoSmallBusinesses() {
    databaseResults = businessesService.getBigBusinessFromDatabase();
    businesses = businessesService.removeBigBusinessesFromResults(databaseResults);
  }

  public List<Listing> getBusinesses() {
    return businesses;
  }

  private void getSmallBusinesses() {
    businesses = new LinkedList<Listing>();
    businessesService = new BusinessesService(businesses);
    findAllBusinesses();
    filterTheListOfBusinessesIntoSmallBusinesses();
  }
}