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

var alertMessage = 'Sorry! We cannot geolocate you. Please enter a zipcode';

function getGeolocation() {

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(displayLocation, displayError);
  }
  else {
    console.log('Browser does not support geolocation');
    alert(alertMessage);
  }
}

function displayLocation(position) {
  var lat = position.coords.latitude;
  var lng = position.coords.longitude;
  var xhttp = new XMLHttpRequest();
  xhttp.open('POST', '/data?lat=' + lat + '&lng=' + lng, true);
  xhttp.send();
}

function displayError() {
  console.log('Geolocation not enabled');
  alert(alertMessage);
}

function getZip() {
  var zip = document.getElementById('zipCode').value;
  var xhttp = new XMLHttpRequest();
  xhttp.open('POST', '/data?zipCode=' + zip, true);
  xhttp.send();
}