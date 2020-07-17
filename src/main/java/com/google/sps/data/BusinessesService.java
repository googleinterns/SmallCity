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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
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


/** BusinessesService object representing all businesses 
* components of the webapp.
 **/
public class BusinessesService {
  
  private List<Listing> allBusinesses;
  private final String KEY = "REDACTED";
  private final static Logger LOGGER = 
        Logger.getLogger(BusinessesService.class.getName());
  private final int ALLOWED_SEARCH_REQUESTS = 3;
  private Listing currentBusiness;
  private PlacesSearchResponse similarBusinessesInTheArea;
  private LatLng latLng;
  private int minFollowers = 50000;

  /** Create a new Businesses instance
  * @param allBusinesses businesses from SmallCityService
  **/
  
  public BusinessesService(List<Listing> allBusinesses) {
    this.allBusinesses = allBusinesses;
  }

  public PreparedQuery getBigBusinessFromDatabase(){
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("BigBusinesses");
    PreparedQuery queryOfDatabase = datastore.prepare(query);
    return queryOfDatabase;
  }

  public List<Listing> removeBigBusinessesFromResults(PreparedQuery queryOfDatabase){
    Iterator<Listing> businesses =  allBusinesses.iterator();
    Entity entity;
    String businessName;
    while (businesses.hasNext()) {
      Listing currentBusiness = businesses.next();
      Iterator<Entity> bigBusinessEntities = queryOfDatabase.asIterator();
      while(bigBusinessEntities.hasNext()) {
        businessName = 
              (String) bigBusinessEntities.next().getProperty("Business");
        if(businessName.equals(currentBusiness.getName())) {
          businesses.remove();
        }
      }
    }
    return allBusinesses;
  }
  
  public List<Listing> getBusinessesFromPlacesApi(User user) {
    latLng = 
          new LatLng(user.getGeolocation().lat, user.getGeolocation().lng);
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
    GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(KEY)
            .build();
    try {
      for(int i = 0; i<allBusinesses.size();i++){
        currentBusiness = allBusinesses.get(i);
        TextSearchRequest request = new TextSearchRequest(context)
                                            .query(currentBusiness.getName())
                                            .location(latLng)
                                            .radius(50000);
        similarBusinessesInTheArea = request.await();
        if(similarBusinessesInTheArea.results.length > 1){
          checkBusinessThroughLinkedin(currentBusiness.getName());
        }
      }

    }catch(Exception e) {
      LOGGER.warning(e.getMessage());
    }  
  }
  
  private void checkBusinessThroughLinkedin(String currentBusinessName) 
                          throws GeneralSecurityException, IOException {
    String cx = "001390425498086947771:mhzyevhmmxq"; //Your search engine
    //Instance Customsearch
    Customsearch cs = new Customsearch.Builder(
                                GoogleNetHttpTransport.newTrustedTransport(), 
                                JacksonFactory.getDefaultInstance(), 
                                null) 
                                .setApplicationName("linkedinSearch") 
                                .setGoogleClientRequestInitializer(
                                      new CustomsearchRequestInitializer(KEY)) 
                                .build();
    Customsearch.Cse.List list = cs.cse().list(currentBusinessName).setCx(cx); 
    List<Result> searchJsonResults = list.execute().getItems();
    String[] numberOfFollowers;
    if (searchJsonResults!=null){
      if(searchJsonResults.size() != 0){
        Result linkedinBusiness = searchJsonResults.get(0);
        List<Map<String, Object>> resultsMetatags = 
                        (List) linkedinBusiness.getPagemap().get("metatags");
        for (Map<String, Object> tag : resultsMetatags) {
          String b_title = (String) tag.get("og:description");
          numberOfFollowers = b_title.split(" ");
          findNumberOfFollowers(numberOfFollowers);
        }
      }
    }
  }

  private void findNumberOfFollowers(String[] numberOfFollowers) {
    int companyFollowers = 0;
    for(int i = 0; i < numberOfFollowers.length; i++) {
      if(numberOfFollowers[i].equals("followers")) {
        companyFollowers = 
                Integer.parseInt(numberOfFollowers[i-1].replaceAll(",", ""));
      }
    }
    if(companyFollowers > minFollowers) {
      addBigBusinessToDatabase();
    }else {
      checkNumberOfSimilarBusinessesInTheArea(currentBusiness.getName());
    }
  }

  private void checkNumberOfSimilarBusinessesInTheArea(String businessName){
    int countNumberOfMatchingBusiness = 0;
    int i = 0;
    while(i < similarBusinessesInTheArea.results.length 
          && countNumberOfMatchingBusiness < 10) {
      if(similarBusinessesInTheArea.results[i].name.contains(businessName) 
          && similarBusinessesInTheArea.results[i].vicinity != currentBusiness.getFormattedAddress()) {
        countNumberOfMatchingBusiness++;
      }
      i++;
     }
     if(countNumberOfMatchingBusiness >= 10) {
       addBigBusinessToDatabase();
     }
   }

  private void addBigBusinessToDatabase(){
    String title = "Business";
    String businessTypes = "BusinessTypes";
    String address = "Address";
    String rating = "Rating";
    String photos = "Photos";
    Entity businessEntity = new Entity("BigBusinesses");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    businessEntity.setProperty(title, currentBusiness.getName());
    businessEntity.setProperty(address, currentBusiness.getFormattedAddress());
    businessEntity.setProperty(rating, currentBusiness.getRating());
    businessEntity.setProperty(businessTypes, 
                            Arrays.asList(currentBusiness.getBusinessTypes()));
    datastore.put(businessEntity);
  }
}