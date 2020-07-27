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
import com.google.maps.TextSearchRequest;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;

/** BusinessesService object representing all businesses 
* components of the webapp.
 **/
public class BusinessesService {
  private Dotenv dotenv = Dotenv.configure().filename("env").load();
  private List<Listing> allBusinesses;
  private final String KEY = dotenv.get("APIKEY");
  private final static Logger LOGGER = 
        Logger.getLogger(BusinessesService.class.getName());
  private final int ALLOWED_SEARCH_REQUESTS = 3;

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
  
  public List<Listing> 
        getBusinessesFromTextSearch(MapLocation mapLocation, String product) {
    LatLng latLng = 
          new LatLng(mapLocation.lat, mapLocation.lng);
    final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(KEY)
            .build();
    TextSearchRequest request = PlacesApi.textSearchQuery(context, product);
    try {
      PlacesSearchResponse response = request.location(latLng)
              .radius(10000)
              .await();
      for (int i=0; i<ALLOWED_SEARCH_REQUESTS; i++) {
        for(PlacesSearchResult place : response.results) {
          addListingToBusinesses(place);
        }
        //Maximum of 2 next token requests allowed
        if (i < 2) {
          Thread.sleep(2000); // Required delay before next API request
          response = PlacesApi
                .textSearchNextPage(context, response.nextPageToken).await();
        }
      }
    } catch(Exception e) {
      LOGGER.warning(e.getMessage());
    }  
    return allBusinesses;
  }

  public List<Listing> getBusinessesFromNearbySearch(MapLocation mapLocation) {
    LatLng latLng = 
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
    String formattedAddress;
    if (place.vicinity != null) {
      formattedAddress = place.vicinity;
    }
    else {
      formattedAddress = place.formattedAddress;
    }
    Geometry geometry = place.geometry;
    MapLocation placeLocation = 
          new MapLocation(geometry.location.lat, geometry.location.lng);
    double rating = place.rating;
    Photo photos[] = place.photos;
    String types[] = place.types;
    allBusinesses.add(new Listing(name, formattedAddress, 
          placeLocation, rating, photos, types));
  }
}