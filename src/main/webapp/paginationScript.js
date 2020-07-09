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

//Variable that keeps track of the last card being shown on the page
let currentEndCard;

//Display the initial 3 cards in the list
function initialDisplay() {
  const resultsContent = document.getElementById('results-content');
  resultsContent.className = 'results-content';

  for (let i = 0; i < 3; i++) {
    resultsContent.appendChild(listingsArray[i]);
  }
  currentEndCard = 2;
}

//Navigate back one page (3 results) in the list view
function navigatePrevious() {
  const resultsContent = document.getElementById('results-content');
  resultsContent.className = 'results-content';

  if((currentEndCard - 2) == 0) {
    alert('Already at beginning of list!');
  }
  else {
    resultsContent.innerHTML = '';

    for (i = (currentEndCard - 5); i < (currentEndCard - 2); i++) {
      resultsContent.appendChild(listingsArray[i]);
    }
    currentEndCard -= 3;
  }
}

//Move forward one page (3 results) in the list view
function moveNext() {
  const resultsContent = document.getElementById('results-content');
  resultsContent.className = 'results-content';

  if((currentEndCard + 1) == count) {
    alert('Already at end of list!');
  }
  else {
    resultsContent.innerHTML = '';

    for (i = (currentEndCard + 1); i < (currentEndCard + 4); i++) {
      resultsContent.appendChild(listingsArray[i]);
    }
    currentEndCard += 3;
  }
}