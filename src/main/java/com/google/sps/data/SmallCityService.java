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

/** SmallCityService object representing all components of the webapp **/
public class SmallCityService {
  
  private User user;
  private BusinessesService businessesService;
  private List<Listing> businesses;
  private final String KEY = "AIzaSyDDIsG-SJAZ69ZoOecmfbXOB7ZIS4pZkAw";
  private final static Logger LOGGER = Logger.getLogger(SmallCityService.class.getName());
  
  /** Create a new Small City Service instance
  * @param mapLocation geolocation of user
  * @return List of small local businesses
  **/
  public SmallCityService(MapLocation mapLocation) {
    this.user = new User(mapLocation);
    businesses = new LinkedList<Listing>();
    businessesService = new BusinessesService(businesses);
    findAllBusinesses();
    eliminateBigBusinesses();
  }

  public void findAllBusinesses() {
    businesses = businessesService.getBusinessesFromPlacesApi(user);
  }

  public void eliminateBigBusinesses() {
    // TODO: Parse big business list and remove big businesses from businessList

  }

  public List<Listing> getBusinesses() {
    return businesses;
  }

  private void addListingToBusinesses(PlacesSearchResult place) {
    String name = place.name;
    String formattedAddress = place.vicinity;
    Geometry geometry = place.geometry;
    MapLocation placeLocation = new MapLocation(geometry.location.lat, geometry.location.lng);
    double rating = place.rating;
    Photo photos[] = place.photos;
    String types[] = place.types;
    businesses.add(new Listing(name, formattedAddress, placeLocation, rating, photos, types));
  }
}