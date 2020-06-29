package com.google.sps.data;

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

  public void eliminateBigBusinesses() {
    // TODO: Parse big business list and remove big businesses from businessList

  }
}