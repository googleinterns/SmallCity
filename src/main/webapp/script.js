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

const alertMessage = 'Sorry! We cannot geolocate you. Please enter a zipcode';
let map;
let locationQuery = '';
let product = '';

function getGeolocation() {
  hideEntryContainer();
  initiateLoaderCircle();
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(displayLocation, displayError);
  }
  else {
    console.log('Browser does not support geolocation');
    alert(alertMessage);
  }
}

function displayLocation(position) {
  let lat = position.coords.latitude;
  let lng = position.coords.longitude;
  reverseGeocodeGeolocation(lat, lng);
  locationQuery = '/data?lat=' + lat + '&lng=' + lng;
  fetchByQueryString();
}

function displayError() {
  console.log('Geolocation not enabled');
  alert(alertMessage);
}

function getZipCode() {
  hideEntryContainer();
  initiateLoaderCircle();
  zip = document.getElementById('entryZipCode').value;
  if (isValidInput(zip)) {
    document.getElementById('zipCode').value = zip;
    locationQuery = '/data?zipCode=' + zip;
    fetchByQueryString();
  }
  else {
    window.alert('Invalid input. Try again.'); 
  }
}

function getProduct() {
  initiateLoaderCircle();
  fetchByQueryString();
}

function fetchByQueryString() {
product = document.getElementById('product').value;
  if (product === '') {
    fetchList(locationQuery + '&product=');
  }
  else {
    fetchList(locationQuery + '&product=' + product);
  }
}

function displayEntryContainer() {
  document.getElementById('entry-container').className = 'element-display';
  document.getElementById('options-container').className = 'element-display';
  mapElement.className = 'map-transparent';
  document.getElementById('entryZipCode').value 
        = document.getElementById('zipCode').value;
}

function hideEntryContainer() {
  document.getElementById('entry-container').className = 'element-hide';
  document.getElementById('options-container').className = 'element-hide';
  mapElement.className = 'map-opaque';
}

function isValidInput(zip) {
  let len = zip.length;
  if (len === 0) {  
    return false; 
  }
  
  for (let i=0; i<len; i++) {
    let charCode = zip.charCodeAt(i);
    if (!(charCode >= 48 && charCode <= 57) &&      // digits
          !(charCode >= 65 && charCode <= 90) &&    // uppercase letters
          !(charCode >= 97 && charCode <= 122) &&   // lowercase letters
          !(charCode === 44) &&                     // comma
          !(charCode === 32)) {                     // space
      return false;
    }
  }
  
  return true;
}

//Array of the (currently 6 for this demo build) 15 listings gathered from the fetch request
let resultsCardsArray = [];

//Count of the total businesses in the fetch request, used to set a unique id for each card
let totalCardCount = 0;
let bounds = 0;

function fetchList(queryString) {
  bounds = new google.maps.LatLngBounds();
  fetch(queryString).then(response => response.json()).then((listings) => {
    resultsCardsArray = [];
    totalCardCount = 0;
    initMap(listings[0].mapLocation);
    listings.forEach((listing) => {
      resultsCardsArray.push(createResultCard(listing.name, listing.formattedAddress, 
            listing.photos, listing.rating, listing.url, totalCardCount));

      if (totalCardCount < 15) createMarker(listing, totalCardCount);
      totalCardCount++;
    }); 
    initialDisplay();
    map.fitBounds(bounds);  
    removeLoaderCircle();
  });
}

// Style elements being alterned by loader
let loaderCircleElement = document.getElementById('loader-circle');
let loaderCircleContainerElement = document.getElementById('loader-circle-container');
let mapElement = document.getElementById('map');

function initiateLoaderCircle() {
  loaderCircleElement.className = 'element-display';
  loaderCircleContainerElement.className = 'element-display';
  mapElement.className = 'map-transparent';
}

function removeLoaderCircle() {
  loaderCircleElement.className = 'element-hide';
  loaderCircleContainerElement.className = 'element-hide';
  mapElement.className = 'map-opaque';
}

/**
 * @param {string} name The name of the business
 * @param {string} address The address of the business
 * @param {string} image The url of the business image
 * @param {double} rating The numerical rating of the business, passed to the createRating() function
 * @param {string} websiteUrl The url of the business' website
 * @param {int} totalCardCount The number of businesses in the list, used to set a specific id to each card
 */
function createResultCard(name, address, photos, rating, websiteUrl, totalCardCount) {
  const resultsCard = document.createElement('div');
  resultsCard.className = 'results-card';
  resultsCard.id = totalCardCount;

  const imageDiv = document.createElement('div');
  imageDiv.className = 'results-image';

  const imageElement = document.createElement('img');
  imageElement.id = 'results-image-element';
  imageElement.src = '/images/image_not_found_two.png';

  let resultPhotoReference = '';
  if ((photos != null) && (photos.length > 0)) {
    resultPhotoReference = photos[0].photoReference;
  }
  else {
    resultPhotoReference = 'none';
  }

  imageDiv.appendChild(imageElement);

  const nameHeader = document.createElement('h2');
  nameHeader.innerText = name;

  const addressParagraph = document.createElement('p');
  addressParagraph.innerText = address;

  const nameAndAddressDiv = document.createElement('div');
  nameAndAddressDiv.className = 'results-business-description';
  nameAndAddressDiv.appendChild(nameHeader);
  nameAndAddressDiv.appendChild(addressParagraph);

  const ratingDiv = createRating(rating);

  const websiteButton = document.createElement('button');
  websiteButton.className = 'results-website-button';
  if (websiteUrl.includes('maps.google.com')) {
    websiteButton.innerText = 'Visit Location on Google Maps';
    linkWebsite(websiteUrl, websiteButton);
  }
  else if (websiteUrl === '') {
    websiteButton.innerText = 'Website Unavailable';
    websiteButton.className = 'unavailable-website';
  }
  else {
    websiteButton.innerText = 'Visit Website';
    linkWebsite(websiteUrl, websiteButton);
  }
    

  resultsCard.appendChild(imageDiv);
  resultsCard.appendChild(nameAndAddressDiv);
  resultsCard.appendChild(ratingDiv);
  resultsCard.appendChild(websiteButton);

  //Creates object that contains the resultCard and photoReference to append to array
  let resultsCardObject = {
    card: resultsCard,
    photoReference: resultPhotoReference
  };

  return resultsCardObject;
}

function linkWebsite(websiteUrl, websiteButton) {
  // Equivalent to HTML's 'onClick'
  websiteButton.addEventListener('click', function() {
    window.open(websiteUrl);
  });
}

/**
 * @param {double} rating The numerical rating for the business, determins the number of stars
 */
function createRating(rating) {
  const ratingDiv = document.createElement('div');
  ratingDiv.className = 'results-rating';

  let roundedRating = Math.round(rating);

  for(i = 0; i < roundedRating; i++) {
    ratingDiv.innerText += '★';
  }

  for (i = roundedRating; i < 5; i++) {
    ratingDiv.innerText += '☆';
  }

  ratingDiv.innerText += (' ' + rating.toFixed(1));
  
  return ratingDiv;
}
