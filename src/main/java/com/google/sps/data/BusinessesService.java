package com.google.sps.data;

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
import java.util.logging.Logger;
import java.util.Iterator;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.maps.TextSearchRequest;
import com.google.api.services.customsearch.model.Search;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.CustomsearchRequestInitializer;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.util.Map;
import java.lang.Integer;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlacesSearchResult;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

/** BusinessesService object representing all businesses 
* components of the webapp.
 **/
public class BusinessesService {

  private final String KEY = "REDACTED";
  private final static Logger LOGGER = 
        Logger.getLogger(BusinessesService.class.getName());
  private final int ALLOWED_SEARCH_REQUESTS = 3;
  private final int MINFOLLOWERS = 50000;
  private final int SMALL_BUSINESSES_DISPLAYED = 15;
  private final String START_SUBSTRING = "| ";
  private final String END_SUBSTRING = "followers";
  private final int MIN_NUMBER_OF_MATCHING_BUSINESSES = 10;
  private LatLng latLng;
  private List<Listing> allBusinesses;
  private int numberOfSmallBusinesses;
  

  /** Create a new Businesses instance
  * @param allBusinesses businesses from SmallCityService
  **/
  
  public BusinessesService(List<Listing> allBusinesses) {
    this.allBusinesses = allBusinesses;
  }

  public List<Listing> removeBigBusinessesFromResults(){
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Iterator<Listing> businesses =  allBusinesses.iterator();
    String businessName;
    while (businesses.hasNext()) {
      Listing currentBusiness = businesses.next();
      try {
        businessName = 
            (String) datastore.get(KeyFactory.createKey(
                                    "BigBusinesses", 
                                    currentBusiness.getName()))
                              .getProperty("Business");
        if(businessName.equals(currentBusiness.getName())){
          businesses.remove();
        }
      } catch(EntityNotFoundException e){
        LOGGER.warning(e.getMessage());
      }
    }
    return allBusinesses;
  }
  
  public List<Listing> getBusinessesFromPlacesApi(MapLocation mapLocation) {
    latLng = 
          new LatLng(mapLocation.lat, mapLocation.lng);
    final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(KEY)
            .build();
    NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, latLng);
    try {
      PlacesSearchResponse response = request.type(PlaceType.STORE)
              .rankby(RankBy.DISTANCE)
              .await();
      for (int i=0; i<ALLOWED_SEARCH_REQUESTS; i++) {
        for(PlacesSearchResult place : response.results) {
          addListingToBusinesses(place);
        }
        //Maximum of 2 next token requests allowed
        if (i < 2) {
          Thread.sleep(2000); // Required delay before next API request
          response = PlacesApi
                .nearbySearchNextPage(context, response.nextPageToken).await();
        }
      }
    } catch(Exception e) {
      LOGGER.warning(e.getMessage());
    }  
    return allBusinesses;
  }

  private void addListingToBusinesses(PlacesSearchResult place) {
    String name = place.name;
    String formattedAddress = place.vicinity;
    Geometry geometry = place.geometry;
    MapLocation placeLocation = 
          new MapLocation(geometry.location.lat, geometry.location.lng);
    double rating = place.rating;
    Photo photos[] = place.photos;
    String types[] = place.types;
    allBusinesses.add(new Listing(name, formattedAddress, 
          placeLocation, rating, photos, types));
  }

  public void checkNumberOfLocationsOfBusiness() {
    GeoApiContext context = 
      new GeoApiContext.Builder().apiKey(KEY).build();
    numberOfSmallBusinesses = 0;
    Iterator<Listing> businesses =  allBusinesses.iterator();
    while(businesses.hasNext() 
          && numberOfSmallBusinesses < SMALL_BUSINESSES_DISPLAYED) {
      Listing currentBusiness = businesses.next();
      TextSearchRequest request = 
        new TextSearchRequest(context).query(currentBusiness.getName())
          .location(latLng).radius(50000);
      try {                                       
        PlacesSearchResult[] similarBusinessesInTheArea = 
          request.await().results;
        if (similarBusinessesInTheArea.length > 1){
          checkBusinessThroughLinkedin(currentBusiness, similarBusinessesInTheArea);
        }else if(similarBusinessesInTheArea.length == 1){
          numberOfSmallBusinesses++;
        }
      } catch(GeneralSecurityException | IOException | InterruptedException | ApiException e ) {
          LOGGER.warning(e.getMessage());
        } 
    }
  }
  
  private void checkBusinessThroughLinkedin(Listing currentBusiness, 
                          PlacesSearchResult[] similarBusinessesInTheArea) 
                              throws GeneralSecurityException, IOException {
    String cx = "REDACTED"; 
    Customsearch cs = new Customsearch.Builder(
        GoogleNetHttpTransport.newTrustedTransport(), 
        JacksonFactory.getDefaultInstance(), null) 
              .setApplicationName("linkedinSearch") 
              .setGoogleClientRequestInitializer(new CustomsearchRequestInitializer(KEY)) 
              .build();

    Customsearch.Cse.List list = cs.cse().list(currentBusiness.getName()).setCx(cx); 
    List<Result> searchJsonResults = list.execute().getItems();
    String[] numberOfFollowers;
    int companyFollowers = 0;
    if (searchJsonResults != null && searchJsonResults.size() != 0) {
      Result linkedinBusiness = searchJsonResults.get(0);
      String businessDescription = 
        (String) linkedinBusiness.getPagemap().get("metatags").get(0).get("og:description");
      if(businessDescription.contains(START_SUBSTRING) != false 
          && businessDescription.contains(END_SUBSTRING) != false){
        String followers = businessDescription.substring(
                                businessDescription.indexOf(START_SUBSTRING) + 2, 
                                businessDescription.indexOf(END_SUBSTRING) - 1);
        try{
          companyFollowers = Integer.parseInt(followers.replaceAll(",", ""));
        } catch (NumberFormatException e){
            LOGGER.warning(e.getMessage());
        }
      }
      if (companyFollowers > MINFOLLOWERS) {
        addBigBusinessToDatabase(currentBusiness);
      } else {
        checkNumberOfSimilarBusinessesInTheArea(currentBusiness,
                                                similarBusinessesInTheArea);
      }
    }
  }

  private void checkNumberOfSimilarBusinessesInTheArea(Listing currentBusiness, 
                          PlacesSearchResult[] similarBusinessesInTheArea) {
    int numberOfMatchingBusinesses = 0;
    int i = 0;
    while (i < similarBusinessesInTheArea.length 
          && numberOfMatchingBusinesses < MIN_NUMBER_OF_MATCHING_BUSINESSES) {
        if (similarBusinessesInTheArea[i].name.contains(currentBusiness.getName())
            && !similarBusinessesInTheArea[i].formattedAddress
                  .equals(currentBusiness.getFormattedAddress())) {
          numberOfMatchingBusinesses++;
        }
      i++;
     }
     if (numberOfMatchingBusinesses >= MIN_NUMBER_OF_MATCHING_BUSINESSES) {
       addBigBusinessToDatabase(currentBusiness);
     }else{
       numberOfSmallBusinesses++;
     }
   }

  private void addBigBusinessToDatabase(Listing currentBusiness) {
    String title = "Business";
    String businessTypes = "BusinessTypes";
    String address = "Address";
    String rating = "Rating";
    String photos = "Photos";
    Entity businessEntity = new Entity("BigBusinesses", currentBusiness.getName());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    businessEntity.setProperty(title, currentBusiness.getName());
    businessEntity.setProperty(address, currentBusiness.getFormattedAddress());
    businessEntity.setProperty(rating, currentBusiness.getRating());
    businessEntity.setProperty(businessTypes, 
                            Arrays.asList(currentBusiness.getBusinessTypes()));
    datastore.put(businessEntity);
  }
}
