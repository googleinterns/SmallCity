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

/** BusinessesService object representing all businesses 
* components of the webapp.
 **/
public class BusinessesService {
  
  private List<Listing> businesses;
  private final String KEY = "REDACTED";
  private final static Logger LOGGER = 
        Logger.getLogger(BusinessesService.class.getName());
  private final int ALLOWED_SEARCH_REQUESTS = 3;

  /** Create a new Businesses instance
  * @param businesses businesses from SmallCityService
  **/
  
  public BusinessesService(List<Listing> businesses) {
    this.businesses = businesses;
  }

  public PreparedQuery getBigBusinessFromDatabase(){
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("BigBusinesses");
    PreparedQuery dateabaseResults = datastore.prepare(query);
    return dateabaseResults;
  }

  public List<Listing> removeBigBusinessesFromResults(PreparedQuery dateabaseResults){
    Iterator<Listing> businessesList =  businesses.iterator();
    Entity entity;
    Boolean bigBusinessIsFound = true;
    String businessName;
    while (businessesList.hasNext()) {
      Listing currentListing = businessesList.next();
      Iterator<Entity> bigBusinessesEntities =  dateabaseResults.asIterable().iterator();
      while(bigBusinessesEntities.hasNext() && bigBusinessIsFound) {
        entity = bigBusinessesEntities.next();
        businessName = (String) entity.getProperty("business");
        if(businessName.equals(currentListing.getName())) {
          businessesList.remove();
          bigBusinessIsFound = false;
        }
      }
      bigBusinessIsFound = true;
    }
    return businesses;
  }

 public void setAllBusinesses(List<Listing> allBusinesses) {
   businesses = allBusinesses;
  }
  public List<Listing> getBusinessesFromPlacesApi(User user) {
    LatLng latLng = 
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
        Thread.sleep(2000); // Required delay before next API request
	      response = PlacesApi
              .nearbySearchNextPage(context, response.nextPageToken).await();
      }
    } catch(Exception e) {
      LOGGER.warning(e.getMessage());
    }  
    return businesses;
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
    businesses.add(new Listing(name, formattedAddress, 
          placeLocation, rating, photos, types));
  }
}