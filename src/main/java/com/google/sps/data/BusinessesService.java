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
import com.google.maps.model.PlaceDetails;
import com.google.maps.PlaceDetailsRequest;
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
import java.lang.Integer;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlacesSearchResult;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;

/** BusinessesService object representing all businesses 
* components of the webapp.
 **/
public class BusinessesService {

  private Dotenv dotenv = Dotenv.configure().filename("env").load();
  private final String KEY = dotenv.get("APIKEY");
  private final static Logger LOGGER = 
        Logger.getLogger(BusinessesService.class.getName());
  private final int ALLOWED_SEARCH_REQUESTS = 3;
  private final int MIN_FOLLOWERS = 50000;
  private final int SMALL_BUSINESSES_DISPLAYED = 15;
  private final String START_SUBSTRING = "| ";
  private final String END_SUBSTRING = "followers";
  private final int ALLOWED_NUMBER_OF_MATCHING_BUSINESSES = 5;
  private final String BIG_BUSINESSES_DATABASE = "BigBusinesses";
  private final String SMALL_BUSINESSES_DATABASE = "SmallBusinesses";
  private LatLng latLng;
  private List<Listing> allBusinesses;

  /** Create a new Businesses instance
  * @param allBusinesses businesses from SmallCityService
  **/
  
  public BusinessesService(List<Listing> allBusinesses) {
    this.allBusinesses = allBusinesses;
  }
  
  public List<Listing> getBusinessesFromPlacesApi(MapLocation mapLocation) {
    latLng = new LatLng(mapLocation.lat, mapLocation.lng);
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
          String url = getUrlFromPlaceDetails(context, place.placeId);
          addListingToBusinesses(place, url);
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
  
  private String getUrlFromPlaceDetails(GeoApiContext context, String placeId) {
    try {
      PlaceDetails result = 
            new PlaceDetailsRequest(context).placeId(placeId).await();
      if (result.website != null) {
        return (result.website.toString());
      }
      else {
        return (result.url.toString());
      }
    } catch(Exception e) {
      LOGGER.warning(e.getMessage());
    }
    // Place Details failure
    return "";
  }

  private void addListingToBusinesses(PlacesSearchResult place, String url) {
    String name = place.name;
    String formattedAddress = place.vicinity;
    Geometry geometry = place.geometry;
    MapLocation placeLocation = 
          new MapLocation(geometry.location.lat, geometry.location.lng);
    double rating = place.rating;
    Photo photos[] = place.photos;
    String types[] = place.types;
    allBusinesses.add(new Listing(name, formattedAddress, 
          placeLocation, rating, photos, types, url));
  }

  public List<Listing> removeBigBusinessesFromResults() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Iterator<Listing> businesses =  allBusinesses.iterator();
    String businessName = "";
    int numberOfSmallBusinesses = 0;

    while (businesses.hasNext() && 
            (numberOfSmallBusinesses < SMALL_BUSINESSES_DISPLAYED)) {
      Listing currentBusiness = businesses.next();

      if (determineIfTheBusinessesAreBig(currentBusiness)) {
        businesses.remove();
      } else {
          numberOfSmallBusinesses++;
      }  
    }
    return allBusinesses;
  }

  private boolean determineIfTheBusinessesAreBig(Listing currentBusiness) {
    if (checkIfBusinessInDatabase(currentBusiness, SMALL_BUSINESSES_DATABASE)) {
      return false;
    } else if (checkIfBusinessInDatabase(currentBusiness, BIG_BUSINESSES_DATABASE)){
      return true;
    }

    if (checkNumberOfSimilarBusinesses(currentBusiness) || 
          checkBusinessThroughLinkedin(currentBusiness)) {
      addBusinessToDatabase(currentBusiness, BIG_BUSINESSES_DATABASE);
      return true;
    }

    addBusinessToDatabase(currentBusiness, SMALL_BUSINESSES_DATABASE);
    return false;
  }

  private boolean checkIfBusinessInDatabase(Listing currentBusiness, 
                                              String databaseEntry) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();    
    
    try {
        String businessName = 
            (String) datastore.get(KeyFactory.createKey(
                                    databaseEntry, 
                                    currentBusiness.getName()))
                              .getProperty("Business");
        return true;
    } catch (EntityNotFoundException e) {
        // Sometimes the small or big business will be in the database,
        // but if not additional checks will follow to determine it's size.
        LOGGER.warning(e.getMessage());
        return false;
    }
  }

  private boolean checkNumberOfSimilarBusinesses(Listing currentBusiness) {
    GeoApiContext context = 
      new GeoApiContext.Builder().apiKey(KEY).build();
    TextSearchRequest request = 
        new TextSearchRequest(context).query(currentBusiness.getName())
          .location(latLng).radius(50000);
    PlacesSearchResult[] similarBusinessesInTheArea = null;

    try {                                       
         similarBusinessesInTheArea = request.await().results;
    } catch(IOException | InterruptedException | ApiException e ) {
        LOGGER.warning(e.getMessage());
    } 

    return checkNumberOfSimilarBusinessesInTheArea(currentBusiness, similarBusinessesInTheArea);  
  }

  private boolean checkNumberOfSimilarBusinessesInTheArea(Listing currentBusiness, 
                          PlacesSearchResult[] similarBusinessesInTheArea) {
    int numberOfMatchingBusinesses = 0;
    int i = 0;

    while (i < similarBusinessesInTheArea.length 
      && numberOfMatchingBusinesses < ALLOWED_NUMBER_OF_MATCHING_BUSINESSES) {
      if (similarBusinessesInTheArea[i].name.equals(currentBusiness.getName())
          && !similarBusinessesInTheArea[i].formattedAddress
                .equals(currentBusiness.getFormattedAddress())) {
        numberOfMatchingBusinesses++;
      }
      i++;
    }

    if (numberOfMatchingBusinesses >= ALLOWED_NUMBER_OF_MATCHING_BUSINESSES) {
      return true;
    } 
    
    return false;
  }

  private boolean checkBusinessThroughLinkedin(Listing currentBusiness) {
    String searchEngineID = dotenv.get("CX"); 
    List<Result> searchJsonResults = new ArrayList<>();
    int companyFollowers = 0;

    try {
      Customsearch cs = new Customsearch.Builder(
          GoogleNetHttpTransport.newTrustedTransport(), 
          JacksonFactory.getDefaultInstance(), null) 
                .setApplicationName("linkedinSearch") 
                .setGoogleClientRequestInitializer(new CustomsearchRequestInitializer(KEY)) 
                .build();
      Customsearch.Cse.List list = cs.cse().list(currentBusiness.getName()).setCx(searchEngineID); 
      searchJsonResults = list.execute().getItems();                   
    } catch (GeneralSecurityException | IOException e) {
      LOGGER.warning(e.getMessage());
    }

    if (searchJsonResults != null && searchJsonResults.size() != 0) {
      Result linkedinBusiness = searchJsonResults.get(0);
      String businessDescription = 
        (String) linkedinBusiness.getPagemap().get("metatags").get(0).get("og:description");

      if (businessDescription.contains(START_SUBSTRING) 
          && businessDescription.contains(END_SUBSTRING)) {
        String followers = businessDescription.substring(
                                businessDescription.indexOf(START_SUBSTRING) + 2, 
                                businessDescription.indexOf(END_SUBSTRING) - 1);

        try {
          companyFollowers = Integer.parseInt(followers.replaceAll(",", ""));
        } catch (NumberFormatException e) {
          // Sometimes businessDescription does not contain a string that 
          // follows a certain pattern, so the string of the company followers 
          // is not a number
          LOGGER.warning(e.getMessage());
        }
      }

      if (companyFollowers > MIN_FOLLOWERS) {
        return true;
      } 
    }

    return false;
  }

  private void addBusinessToDatabase(Listing currentBusiness, String databaseEntry) {
    String title = "Business";
    String businessTypes = "BusinessTypes";
    String address = "Address";
    String rating = "Rating";
    String photos = "Photos";
    Entity businessEntity = new Entity(databaseEntry, currentBusiness.getName());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    businessEntity.setProperty(title, currentBusiness.getName());
    businessEntity.setProperty(address, currentBusiness.getFormattedAddress());
    businessEntity.setProperty(rating, currentBusiness.getRating());
    businessEntity.setProperty(businessTypes, 
                            Arrays.asList(currentBusiness.getBusinessTypes()));
    datastore.put(businessEntity);
  }
}
