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
  displayInformationDiv();
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
  displayInformationDiv();
  zip = document.getElementById('entryZipCode').value;
  if (isValidInput(zip)) {
    document.getElementById('zipCode').innerText = zip;
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

/* Background blur, loader, information div, and popup control */

let backgroundBlurDiv = document.getElementById('background-blur-div');
let popupFormCenterWrapper = document.getElementById('popup-form-center-wrapper');
let informationDivCenterWrapper = document.getElementById('information-div-center-wrapper');
let loaderCircleElement = document.getElementById('loader-circle-center-wrapper');
let mapElement = document.getElementById('map');

function displayEntryContainer() {
  backgroundBlurDiv.className = 'blurred-element-display';
  popupFormCenterWrapper.className = 'centered-element-display';
  mapElement.className = 'map-to-back';
  document.getElementById('entryZipCode').value 
        = document.getElementById('zipCode').innerText;
}

function hideEntryContainer() {
  backgroundBlurDiv.className = 'element-hide';
  popupFormCenterWrapper.className = 'element-hide';
  mapElement.className = 'map-to-front';
}

function initiateLoaderCircle() {
  backgroundBlurDiv.className = 'blurred-element-display';
  loaderCircleElement.className = 'centered-element-display';
  mapElement.className = 'map-to-back';
}

function removeLoaderCircle() {
  backgroundBlurDiv.className = 'element-hide';
  loaderCircleElement.className = 'element-hide';
  mapElement.className = 'map-to-front';
}

function displayInformationDiv() {
  backgroundBlurDiv.className = 'blurred-element-display';
  informationDivCenterWrapper.className = 'centered-element-display';
  mapElement.className = 'map-to-back';
}

function hideInformationDiv() {
  backgroundBlurDiv.className = 'element-hide';
  informationDivCenterWrapper.className = 'element-hide';
  mapElement.className = 'map-to-front';
}

// Allows user to close information div if they've already read it before
informationDivCenterWrapper.addEventListener('click', function() {
  if(informationDivCenterWrapper.className === 'centered-element-display') {
    hideInformationDiv();
    initiateLoaderCircle();
  }
});

// Allows user to close popup div if they're changing their location (but not when they first come to the site)
popupFormCenterWrapper.addEventListener('click', function() {
  if(popupFormCenterWrapper.className === 'centered-element-display' && (document.getElementById('zipCode').innerText != null)) {
    hideEntryContainer();
  }
});

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

// Storing the most recent listings from the latest fetch to get the list of businesses
let listingsLocalStorage = [];

function fetchList(queryString) {
  bounds = new google.maps.LatLngBounds();
  fetch(queryString).then(response => response.json()).then((listings) => {
    resultsCardsArray = [];
    totalCardCount = 0;
    listingsLocalStorage = listings;
    addResultCardsAndMapToTheScreen(listings);
    hideInformationDiv();
    removeLoaderCircle();
  });
}

function addResultCardsAndMapToTheScreen(listings){
  initMap(listings[0].mapLocation);
    listings.forEach((listing) => {
      resultsCardsArray.push(createResultCard(listing.name, listing.formattedAddress, 
            listing.photos, listing.rating, listing.placeId, totalCardCount));

      if (totalCardCount < 15) createMarker(listing, totalCardCount);
      totalCardCount++;
    }); 
    initialDisplay();
    map.fitBounds(bounds); 
} 

/**
 * @param {string} name The name of the business
 * @param {string} address The address of the business
 * @param {string} image The url of the business image
 * @param {double} rating The numerical rating of the business, passed to the createRating() function
 * @param {string} websiteUrl The url of the business' website
 * @param {int} totalCardCount The number of businesses in the list, used to set a specific id to each card
 */
function createResultCard(name, address, photos, rating, passedPlaceId, totalCardCount) {

  const resultsCard = document.createElement('div');
  resultsCard.className = 'results-card';
  resultsCard.id = totalCardCount;

  const resultsCardRight = document.createElement('div');
  resultsCardRight.className = 'results-card-right';

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
    
  resultsCardRight.appendChild(nameAndAddressDiv);
  resultsCardRight.appendChild(ratingDiv);
  resultsCardRight.appendChild(websiteButton);

  resultsCard.appendChild(imageDiv);
  resultsCard.appendChild(resultsCardRight);
  
  //Creates object that contains the resultCard and photoReference to append to array
  let resultsCardObject = {
    card: resultsCard,
    photoReference: resultPhotoReference,
    placeId: passedPlaceId
  };

  return resultsCardObject;
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

window.onbeforeunload = function() {
  localStorage.setItem("listings", JSON.stringify(listingsLocalStorage));
  localStorage.setItem("location", locationQuery);
  localStorage.setItem("zipcode", document.getElementById('zipCode').innerText);
  localStorage.setItem("product", document.getElementById('product').value);
}

window.onload = function() {
  listingsLocalStorage = JSON.parse(localStorage.getItem("listings"));
  locationQuery = localStorage.getItem("location");
  bounds = new google.maps.LatLngBounds();
  let zipcode = localStorage.getItem("zipcode");
  let product = localStorage.getItem("product");

  if (listingsLocalStorage != null && locationQuery != null && zipcode != null) {
    hideEntryContainer();
    document.getElementById('zipCode').innerText = zipcode;
    addResultCardsAndMapToTheScreen(listingsLocalStorage);

    if (product !== "") {
      document.getElementById('product').value = product;
    }
  }
}
