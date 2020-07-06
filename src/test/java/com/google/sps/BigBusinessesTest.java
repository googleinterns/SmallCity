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

@RunWith(JUnit4.class)
public final class BigBusinessesTest {
  private MapLocation googlesOffice = new MapLocation(40.457177, -79.916696);
  private List<Listing> sampleListOfBusinesses = new LinkedList<>();
  private List<Listing> expectedBusinesses = new LinkedList<>();
  
  @Test
  public void testBusinesses() {
    sampleListOfBusinesses.add(new Listing("LA Fitness", new MapLocation(40.457091, -79.915331), 3.9, null, "https://www.lafitness.com/Pages/Default.aspx"));
    sampleListOfBusinesses.add(new Listing("west elm", new MapLocation(40.456279, -79.915015), 3.6, null, "https://www.westelm.com"));
    sampleListOfBusinesses.add(new Listing("McDonalds", new MapLocation(40.459450, -79.918479), 2.6, null, "https://www.mcdonalds.com"));
    sampleListOfBusinesses.add(new Listing("East End Brewing Company", new MapLocation(40.459391, -79.911782), 4.7, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.eastendbrewing.com%2F&psig=AOvVaw0kX_SAlxhA09EN3cKpt5ik&ust=1593613487774000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCLDA6oDfqeoCFQAAAAAdAAAAABAD", "http://www.eastendbrewing.com/"));
    sampleListOfBusinesses.add(new Listing("The Shiny Bean Coffee & Tea", new MapLocation(40.496328, -79.944862), 4.9, "https://goo.gl/maps/AYH2QCL7pkoMBxHA8", "https://theshinybean.com/"));
    sampleListOfBusinesses.add(new Listing("Weisshouse", new MapLocation(40.456684, -79.925499), 4.3, "https://goo.gl/maps/7tuXn7QF2hh7ioGYA", "https://www.weisshouse.com/"));
 
    SmallCityService testServiceUser = new SmallCityService(googlesOffice);
    testServiceUser.setAllBusinesses(sampleListOfBusinesses);
    
    sampleListOfBusinesses = testServiceUser.eliminateBigBusinesses();
    
    expectedBusinesses.add(new Listing("East End Brewing Company", new MapLocation(40.459391, -79.911782), 4.7, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.eastendbrewing.com%2F&psig=AOvVaw0kX_SAlxhA09EN3cKpt5ik&ust=1593613487774000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCLDA6oDfqeoCFQAAAAAdAAAAABAD", "http://www.eastendbrewing.com/"));
    expectedBusinesses.add(new Listing("The Shiny Bean Coffee & Tea", new MapLocation(40.496328, -79.944862), 4.9, "https://goo.gl/maps/AYH2QCL7pkoMBxHA8", "https://theshinybean.com/"));
    expectedBusinesses.add(new Listing("Weisshouse", new MapLocation(40.456684, -79.925499), 4.3, "https://goo.gl/maps/7tuXn7QF2hh7ioGYA", "https://www.weisshouse.com/"));
    Assert.assertEquals(expectedBusinesses, sampleListOfBusinesses);
  }
}