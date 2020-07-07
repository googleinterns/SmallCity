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

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  SmallCityService smallCityService;
  private final static Logger LOGGER = Logger.getLogger(DataServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String latString = request.getParameter("lat");
    String lngString = request.getParameter("lng");
    double lat = convertToDouble(latString);
    double lng = convertToDouble(lngString);
    // Google Pittsburgh Office Location (hardcoded prototype)
    // Will be populated with lat and lng doubles
    MapLocation userLocation = new MapLocation(40.457410, -79.916573);
    smallCityService = new SmallCityService(userLocation);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(convertToJson(smallCityService.getBusinesses()));
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
    } catch(NumberFormatException e) {
      LOGGER.warning("Location services failure - default set");
      return 0; // Null Island
    }
    return doubleAsDouble;
  }
}
