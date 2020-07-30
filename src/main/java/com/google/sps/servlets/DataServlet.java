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

package com.google.sps.servlets;

import com.google.sps.data.SmallCityService;
import com.google.sps.data.Listing;
import com.google.sps.data.MapLocation;
import com.google.sps.data.SearchObject;

import java.util.List;
import java.util.LinkedList;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/** Recieves user location and sends small business output to the client **/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  SmallCityService smallCityService = new SmallCityService();
  private final static Logger LOGGER = Logger.getLogger(DataServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String latString = ""; 
    String lngString = "";
    String product = "";
    
    try {
      latString = request.getParameter("lat");
      lngString = request.getParameter("lng");
      product = request.getParameter("product");
    } catch (NullPointerException e) {
      LOGGER.warning(e.getMessage() 
           + "Unable to geolocate user, zipCode entered instead.");
    } finally {
      SearchObject searchObject = new SearchObject(product);
      if (latString != null && lngString != null) {
        double lat = convertToDouble(latString);
        double lng = convertToDouble(lngString);
        MapLocation userLocation = new MapLocation(lat, lng);
        smallCityService.createUserServiceWithGeolocation(userLocation, searchObject);
      }
      else {
        String zip = request.getParameter("zipCode");
        smallCityService.createUserServiceWithZip(zip, searchObject);
      }
      
      response.setContentType("application/json;");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().println(convertToJson(smallCityService.getSmallBusinesses()));
    }
  }

  private String convertToJson(List<Listing> businesses) {
    Gson gson = new Gson();
    String json = gson.toJson(businesses);
    return json;
  }

  private double convertToDouble(String doubleAsString) {
    double doubleAsDouble;
    try {
      doubleAsDouble = Double.parseDouble(doubleAsString);
    } catch (NumberFormatException e) {
      LOGGER.warning("Location services failure - default set");
      return 0; // Null Island
    }
    return doubleAsDouble;
  }
}
