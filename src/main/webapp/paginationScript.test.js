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

const calculateListAugment = require('./paginationScript');

const TOTAL_CARDS_TO_DISPLAY = 3;
const MAX_LIST_VIEW_NUMER = 15;

test('It calculateAugment performs as expected given certain inputs', () => {
  //Beginning of list
  currentFirstCardIndex = 0;
  expect(calculateListAugment(-TOTAL_CARDS_TO_DISPLAY)).toEqual(0);

  //End of list
  currentFirstCardIndex = MAX_LIST_VIEW_NUMER - TOTAL_CARDS_TO_DISPLAY;
  expect(calculateListAugment(TOTAL_CARDS_TO_DISPLAY)).toEqual(0);

  //Some random acceptable location in list, next
  currentFirstCardIndex = 3;
  expect(calculateListAugment(TOTAL_CARDS_TO_DISPLAY)).toEqual(3);

  //Some random acceptable location in list, previous
  currentFirstCardIndex = 3;
  expect(calculateListAugment(-TOTAL_CARDS_TO_DISPLAY)).toEqual(0);
});
