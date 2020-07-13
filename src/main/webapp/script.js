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
  let lat = position.coords.latitude;
  let lng = position.coords.longitude;
  let xhttp = new XMLHttpRequest();
  xhttp.open('POST', '/data?lat=' + lat + '&lng=' + lng, true);
  xhttp.send();

  setTimeout(fetchList, 4000);
}

function displayError() {
  console.log('Geolocation not enabled');
  alert(alertMessage);
}

//Array of the (currently 6 for this demo build) 15 listings gathered from the fetch request
let listingsArray = [];

//Count of the total businesses in the fetch request, used to set a unique id for each card
let totalCardCount = 0;

function fetchList() {
  fetch('/data').then(response => response.json()).then((listings) => {
    listingsArray = [];
    listings.forEach((listing) => {
      listingsArray.push(createResultCard(listing.name, listing.formattedAddress, listing.photos, listing.rating, totalCardCount));
      totalCardCount++;
    });
    initialDisplay();
  });
}

/**
 * @param {string} name The name of the business
 * @param {string} address The address of the business
 * @param {string} image The url of the business image
 * @param {double} rating The numerical rating of the business, passed to the createRating() function
 * @param {string} website The url of the business' website
 * @param {int} totalCardCount The number of businesses in the list, used to set a specific id to each card
 */
function createResultCard(name, address, photos, rating, totalCardCount) {
  const resultsCard = document.createElement('div');
  resultsCard.className = 'results-card';
  resultsCard.id = totalCardCount;

  const imageDiv = document.createElement('div');
  imageDiv.className = 'results-image';
  // TODO: Link image

  const nameHeader = document.createElement('h2');
  nameHeader.innerText = name;
  const addressParagraph = document.createElement('p');
  addressParagraph.innerText = address;

  const nameAndAddressDiv = document.createElement('div');
  nameAndAddressDiv.className = 'results-business-description';
  nameAndAddressDiv.appendChild(nameHeader);
  // TODO: Actually add address once lweiskopf@ pr is approved, is just 'undefined' for now
  nameAndAddressDiv.appendChild(addressParagraph);

  const ratingDiv = createRating(rating);

  const websiteButton = document.createElement('button');
  websiteButton.className = 'results-website-button';
  websiteButton.innerText = 'Visit Website';
  // TODO: Add website link

  resultsCard.appendChild(imageDiv);
  resultsCard.appendChild(nameAndAddressDiv);
  resultsCard.appendChild(ratingDiv);
  resultsCard.appendChild(websiteButton);

  return resultsCard;
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

function getZipCode() {
  let zip = document.getElementById('zipCode').value;
  let xhttp = new XMLHttpRequest();
  xhttp.open('POST', '/data?zipCode=' + zip, true);
  xhttp.send();

  setTimeout(fetchList, 4000);
}