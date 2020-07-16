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

function initMap(mapLocation) {
  let myLatLng = {lat: mapLocation.lat, lng: mapLocation.lng};
  map = new google.maps.Map(document.getElementById('map'), {
    zoom: 13,
    center: myLatLng,
    mapTypeControl: false
  });
}

function createMarker(listing, cardNumber) {
  let marker = new google.maps.Marker({
    position: {lat: listing.mapLocation.lat, lng: listing.mapLocation.lng},
  });

  marker.addListener('click', function() {
    map.setZoom(16);
    map.setCenter(marker.getPosition());
    let firstId = document.getElementById('results-content').firstChild.id;
    console.log(firstId);
    displayCards(cardNumber-firstId);
    document.getElementById(cardNumber).style.backgroundColor = '#b3ffb3';
  });
  marker.setMap(map);
}