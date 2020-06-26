package com.google.sps.data;

public class SmallCityService {
  
  private User user;
  private List<Listing> businessList;

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