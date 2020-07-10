package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
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
  private List<Listing> businesses = new LinkedList<>();
  private PreparedQuery databaseResults;

  /** Create a new Small City Service instance
  * @param mapLocation geolocation of user
  * @return List of small local businesses
  **/
  public SmallCityService(MapLocation mapLocation) {
    this.user = new User(mapLocation);
    businessesService = new BusinessesService(businesses);
    findAllBusinesses();
    filterTheListOfBusinessesIntoSmallBusinesses();
  }

  public void findAllBusinesses() {
    // TODO: Get businesses from Place API given user location
    businesses = new LinkedList<Listing>();
    businesses.add(
      new Listing("LA Fitness", 
                  new MapLocation(40.457091, -79.915331), 
                  3.9, 
                  null, 
                  "https://www.lafitness.com/Pages/Default.aspx"));
    businesses.add(
      new Listing("west elm", 
                  new MapLocation(40.456279, -79.915015), 
                  3.6, 
                  null, 
                  "https://www.westelm.com"));
    businesses.add(
      new Listing("McDonalds", 
                  new MapLocation(40.459450, -79.918479), 
                  2.6, 
                  null, 
                  "https://www.mcdonalds.com"));
    businesses.add(
      new Listing("East End Brewing Company", 
                  new MapLocation(40.459391, -79.911782), 
                  4.7, 
                  "https://www.google.com/url?sa=i&url=https%3A%2F%2F" +
                  "www.eastendbrewing.com%2F&psig=AOvVa" + 
                  "w0kX_SAlxhA09EN3cKpt5ik&ust=1593613487774000&source=" + 
                  "images&cd=vfe&ved=0CAIQjRxqFwoTCLDA6oDfqeoCFQAAAAAdAAAAABAD", 
                  "http://www.eastendbrewing.com/"));
    businesses.add(
      new Listing("The Shiny Bean Coffee & Tea", 
                  new MapLocation(40.496328, -79.944862), 
                  4.9, 
                  "https://goo.gl/maps/AYH2QCL7pkoMBxHA8", 
                  "https://theshinybean.com/"));
    businesses.add(
      new Listing("Weisshouse", 
                  new MapLocation(40.456684, -79.925499), 
                  4.3, 
                  "https://goo.gl/maps/7tuXn7QF2hh7ioGYA", 
                  "https://www.weisshouse.com/"));
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
}