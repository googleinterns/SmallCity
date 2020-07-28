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

const TOTAL_CARDS_TO_DISPLAY = 3;
const MAX_LIST_VIEW_NUMBER = 15;

let testResultsContent = [];
let testResultsCardsList = [];

for (let i = 0; i < 6; i++) {
  let testCardObject = {
    id: i,
    testPhotoReference: ("Card " + i +"\'s Photo Reference")
  };

  testResultsCardsList.push(testCardObject);
}

function testDisplayCards(listAugment){
  let currentFirstCardIndex = 0;

  if (testResultsContent.length != 0) {
    currentFirstCardIndex = parseInt(testResultsContent[0].id);
  }

  if ((currentFirstCardIndex == 0) && (listAugment < 0)) {
    alert('Already at beginning of list!');
  }
  else if (currentFirstCardIndex == (MAX_LIST_VIEW_NUMBER - TOTAL_CARDS_TO_DISPLAY) && (listAugment > 0)) {
    alert('Already at end of list!');
  }
  else {
    currentFirstCardIndex += listAugment; 

    testResultsContent = [];
    for (let i = currentFirstCardIndex; i < (currentFirstCardIndex + TOTAL_CARDS_TO_DISPLAY); i++) {
      //Card being appended to the testResultsContent
      let cardToAppend = testResultsCardsList[i];

      if (cardToAppend.testPhotoReference != 'none') {
        let imageElement = cardToAppend.testPhotoReference;
      }

      testResultsContent.push(cardToAppend);
    }
  }
}

test('If displayCards functions as expected - this also tests initialDisplay', () => {
  testDisplayCards(0);

  // Expect that the 3 cards now in testResultsContent all equal the 3 cards from
  // testResultsCardsList, in the same order
  expect(testResultsContent[0].id).toEqual(0);
  expect(testResultsContent[1].id).toEqual(1);
  expect(testResultsContent[2].id).toEqual(2);
});


test('If the moveNext function works as expected', () => {

  // Move next (what is being tested)
  testDisplayCards(3);

  expect(testResultsContent[0].id).toEqual(3);
  expect(testResultsContent[1].id).toEqual(4);
  expect(testResultsContent[2].id).toEqual(5);
});

test(' If the navigatePrevious works as expected', () => {

  // Move Backwards (what is being tested)
  testDisplayCards((-3));

  expect(testResultsContent[0].id).toEqual(0);
  expect(testResultsContent[1].id).toEqual(1);
  expect(testResultsContent[2].id).toEqual(2);

});