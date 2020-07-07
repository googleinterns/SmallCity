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
  private List<Listing> businesses = new LinkedList<>();
  
  /** Create a new Small City Service instance
  * @param mapLocation geolocation of user
  * @return List of small local businesses
  **/
  public SmallCityService(MapLocation mapLocation) {
    this.user = new User(mapLocation);
    getAllBusinesses();
    eliminateBigBusinesses();
  }

  public void getAllBusinesses() {
    // TODO: Get businesses from Place API given user location
    
  }
  
  // Function is only here until the places API is implemented,
  // and for test file to be able to set any static business LinkedList we want
  // to try to use
  public void setAllBusinesses(List<Listing> allBusinesses){
   businesses = allBusinesses;
  }

  public List<Listing> eliminateBigBusinesses() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("BigBusinesses").addSort("business", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    Iterator<Listing> businessesList =  businesses.iterator();
    Listing tempListing;
    while (businessesList.hasNext()){
      tempListing = businessesList.next();
      for (Entity entity : results.asIterable()) {
        String businessName = (String) entity.getProperty("business");
        if(businessName.equals(tempListing.getName())){
          businessesList.remove();
          break;
        }
      }
    }
    return businesses;
  }
}