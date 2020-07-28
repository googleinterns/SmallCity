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
  
  public List<Listing> getBusinessesFromPlacesApi(MapLocation mapLocation) {
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
}