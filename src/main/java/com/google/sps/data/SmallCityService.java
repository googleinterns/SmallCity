package com.google.sps.data;
import java.util.List;
import java.util.LinkedList;

/** SmallCityService object representing all components of the webapp **/
public class SmallCityService {
  
  private User user;
  private List<Listing> businesses;
  
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
  // and for test purposes
  public void setAllBusinesses(List<Listing> allBusinesses){
    businesses = allBusinesses;
  }

  public void eliminateBigBusinesses() {
    // TODO: Parse big business list and remove big businesses from businessList
  }
}