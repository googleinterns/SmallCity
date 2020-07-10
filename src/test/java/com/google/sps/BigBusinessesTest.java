
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

@RunWith(JUnit4.class)
public final class BigBusinessesTest {
  private MapLocation googlesOffice = new MapLocation(40.457177, -79.916696);
  private List<Listing> sampleListOfBusinesses = new LinkedList<>();
  private List<Listing> expectedListOfBusinesses = new LinkedList<>();  
  private Listing[] sampleListingsOfBusinesses = {
                new Listing("LA Fitness", 
                  new MapLocation(40.457091, -79.915331), 
                  3.9, 
                  null, 
                  "https://www.lafitness.com/Pages/Default.aspx"),
                new Listing("west elm", 
                  new MapLocation(40.456279, -79.915015), 
                  3.6, 
                  null, 
                  "https://www.westelm.com"),
                new Listing("McDonalds", 
                  new MapLocation(40.459450, -79.918479), 
                  2.6, 
                  null, 
                  "https://www.mcdonalds.com"),
                new Listing("East End Brewing Company", 
                  new MapLocation(40.459391, -79.911782), 
                  4.7, 
                  "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.eastendbrewing.com%2F&psig=AOvVa" + 
                  "w0kX_SAlxhA09EN3cKpt5ik&ust=1593613487774000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCLDA6oDfqeoCFQAAAAAdAAAAABAD", 
                  "http://www.eastendbrewing.com/"),
                new Listing("The Shiny Bean Coffee & Tea", 
                  new MapLocation(40.496328, -79.944862), 
                  4.9, 
                  "https://goo.gl/maps/AYH2QCL7pkoMBxHA8", 
                  "https://theshinybean.com/"),
                new Listing("Weisshouse", 
                  new MapLocation(40.456684, -79.925499), 
                  4.3, 
                  "https://goo.gl/maps/7tuXn7QF2hh7ioGYA", 
                  "https://www.weisshouse.com/")
              };

  private void populateListOfBusinessesIntoLinkedList(Listing[] arrayOfBusiness) {
    for(Listing business: sampleListingsOfBusinesses){
      sampleListOfBusinesses.add(business);
    }
  }
  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private void setUpSampleDatabase() {
    String title = "business";
    Entity taskEntity = new Entity("BigBusinesses");
    String[] businesses = {"McDonalds","west elm","LA Fitness"};
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    for(String business: businesses){
      taskEntity = new Entity("BigBusinesses");
      taskEntity.setProperty(title, business);
      datastore.put(taskEntity);
    }
    assertEquals(3, datastore.prepare(new Query("BigBusinesses")).countEntities());
  }

  // To setup a sample database and see if it is created properly 
  @Test
  public void testSampleDatabase() {
    setUpSampleDatabase();
  }

  @Test
  public void testEliminateBigBusinessesMethod() {
    setUpSampleDatabase();
    populateListOfBusinessesIntoLinkedList(sampleListingsOfBusinesses);

    SmallCityService testSmallCityService = 
                                  new SmallCityService(googlesOffice);
    testSmallCityService.setAllBusinesses(sampleListOfBusinesses);

    // These are the listings that should be in the list 
    // after the Bigbusinesses are removed from the List.
    // Currently to make sure this test runs proberly, I made it so that 
    // the refrences of the appropriate Listings are added to a seperate array, 
    // so when eliminateBigBusinesses method is called, I am sure that the bigBusinesses 
    // are being removed from the list variable in the SmallCityService class.
    expectedListOfBusinesses.add(sampleListOfBusinesses.get(3));
    expectedListOfBusinesses.add(sampleListOfBusinesses.get(4));
    expectedListOfBusinesses.add(sampleListOfBusinesses.get(5));
    
    testSmallCityService.parseThroughTheListOfBusinesses();
    
    Assert.assertEquals(expectedListOfBusinesses, testSmallCityService.getBusinesses());
  }
  
}