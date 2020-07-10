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

public class BusinessesService {

  private List<Listing> businesses;

  /** Create a new Businesses instance
  * @param businesses businesses from SmallCityService
  **/
  
  public BusinessesService(List<Listing> businesses) {
    this.businesses = businesses;
  }

  public PreparedQuery connectToBigBusinessDatabase(){
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("BigBusinesses");
    PreparedQuery dateabaseResults = datastore.prepare(query);
    return dateabaseResults;
  }

  public List<Listing> removeBigBusinessesFromResults(PreparedQuery results){
    Iterator <Listing> businessesList =  businesses.iterator();
    Listing currentListing;
    while (businessesList.hasNext()) {
      currentListing = businessesList.next();
      for (Entity entity : results.asIterable()) {
        String businessName = (String) entity.getProperty("business");
        if(businessName.equals(currentListing.getName())) {
          businessesList.remove();
          break;
        }
      }
    }
    return businesses;
  }

  public void setAllBusinesses(List<Listing> allBusinesses) {
   businesses = allBusinesses;
  }
}