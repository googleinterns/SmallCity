package com.google.sps.data;

/** SmallCityService object representing all components of the webapp **/
public class SmallCityService {
  
  private User user;
  private List<Listing> businessesList;
  
  /** Create a new Small City Service instance
  * @param user User of webapp with mapLocation
  * @return List of small local businesses
  **/
  public SmallCityService(User user) {
    this.user = user;
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