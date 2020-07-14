
// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.List;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import com.google.maps.model.Photo;

@RunWith(JUnit4.class)
public final class BigBusinessesTest {
  private Photo[] samplePhotos = new Photo[0];
  private String[] sampleBusinessTypes = new String[0];
  private MapLocation testLocation = new MapLocation(40.457177, -79.916696);
  private DatastoreService datastore;
  
  private List<Listing> sampleListOfBusinesses = new LinkedList<Listing>(Arrays.asList(
                new Listing("LA Fitness",
                  "Address", 
                  new MapLocation(40.457091, -79.915331), 
                  3.9,
                  samplePhotos, 
                  sampleBusinessTypes),
                new Listing("west elm", 
                  "Address",
                  new MapLocation(40.456279, -79.915015), 
                  3.6, 
                  samplePhotos,
                  sampleBusinessTypes),
                new Listing("McDonalds",
                  "Address", 
                  new MapLocation(40.459450, -79.918479), 
                  2.6, 
                  samplePhotos,
                  sampleBusinessTypes),
                new Listing("East End Brewing Company", 
                  "Address",
                  new MapLocation(40.459391, -79.911782), 
                  4.7,
                  samplePhotos, 
                  sampleBusinessTypes),
                new Listing("The Shiny Bean Coffee & Tea", 
                  "Address",
                  new MapLocation(40.496328, -79.944862), 
                  4.9, 
                  samplePhotos,
                  sampleBusinessTypes),
                new Listing("Weisshouse", 
                  "Address",
                  new MapLocation(40.456684, -79.925499), 
                  4.3,
                  samplePhotos, 
                  sampleBusinessTypes)
  ));  

  private List<Listing> sampleDatabaseOfBigBusinesses = new LinkedList<Listing>(Arrays.asList(
                new Listing("LA Fitness",
                  "Address", 
                  new MapLocation(40.457091, -79.915331), 
                  3.9,
                  samplePhotos, 
                  sampleBusinessTypes),
                new Listing("west elm", 
                  "Address",
                  new MapLocation(40.456279, -79.915015), 
                  3.6, 
                  samplePhotos,
                  sampleBusinessTypes),
                new Listing("McDonalds",
                  "Address", 
                  new MapLocation(40.459450, -79.918479), 
                  2.6, 
                  samplePhotos,
                  sampleBusinessTypes)
  ));

  private List<Listing> expectedListOfBusinesses = new LinkedList<>();  
         
  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
    setUpSampleDatabase();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private void setUpSampleDatabase() {
    String title = "business";
    datastore = DatastoreServiceFactory.getDatastoreService();
    Entity businessEntity = new Entity("BigBusinesses");
    for(Listing business: sampleDatabaseOfBigBusinesses) {
      businessEntity = new Entity("BigBusinesses");
      businessEntity.setProperty(title, business.getName());
      datastore.put(businessEntity);
    }
  }

  // To setup a sample database and see if it is created properly 
  @Test
  public void testSampleDatabase() {
    assertEquals(
        3, datastore.prepare(new Query("BigBusinesses")).countEntities());
  }

  @Test
  public void testEliminateBigBusinessesMethod() {
    SmallCityService testSmallCityService = new SmallCityService();
    testSmallCityService.createUserWithGeolocation(testLocation);
    testSmallCityService.setAllBusinesses(sampleListOfBusinesses);

    // These are the listings that should be in the list 
    // after the Bigbusinesses are removed from the List.
    // Currently to make sure this test runs properly, I made it so that the
    // references of the appropriate Listings are added to a seperate array, 
    // so when eliminateBigBusinesses method is called, I am sure that the bigBusinesses 
    // are being removed from the list variable in the SmallCityService class.
    expectedListOfBusinesses.add(sampleListOfBusinesses.get(3));
    expectedListOfBusinesses.add(sampleListOfBusinesses.get(4));
    expectedListOfBusinesses.add(sampleListOfBusinesses.get(5));
    
    testSmallCityService.filterBySmallBusinesses();
    
    Assert.assertEquals(
        expectedListOfBusinesses, testSmallCityService.getBusinesses());
  }
  
}