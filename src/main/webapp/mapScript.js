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
    center: myLatLng,
    mapTypeControl: false
  });
}

function createMarker(listing, cardNumber) {
  let marker = new google.maps.Marker({
    position: {lat: listing.mapLocation.lat, lng: listing.mapLocation.lng},
  });
  bounds.extend(marker.getPosition());

  marker.addListener('click', function() {
    map.setCenter(marker.getPosition());
    map.setZoom(18);
    let firstCardId = document.getElementById('results-content').firstChild.id;
    // If card is not on the current page
    if ((cardNumber-firstCardId < 0) || (cardNumber-firstCardId >= 3)) {
      displayCards(Math.floor((cardNumber-firstCardId)/3)*3);
    }

    let currentCard = document.getElementById(cardNumber);
    // Corresponding card flashes for 5 seconds
    currentCard.style.border = '8px solid #b3ffb3';
    setTimeout(function() {
      currentCard.style.border = 'none';
    }, 5000);
  });
  marker.setMap(map);
}

function reverseGeocodeGeolocation(lat, lng) {
  let latLng = {lat: lat, lng: lng};
  let geocoder = new google.maps.Geocoder;
  geocoder.geocode({'location': latLng}, function(results, status) {
    if (results[0] && status == 'OK') {
      // Address component at index 7 is postal code
      let zipCode = results[0].address_components[7].long_name;
      document.getElementById('zipCode').value = zipCode;
    }
    else {
      console.log('Unable to identify geolocation');
    }
  });
}