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

test('It calculateListAugment performs as expected when at beginning of list', () => {
  currentFirstCardIndex = 0;
  expect(calculateListAugment(-TOTAL_CARDS_TO_DISPLAY)).toEqual(0);
});

test('It calculateListAugment performs as expected when at end of list', () => {
  currentFirstCardIndex = MAX_LIST_VIEW_NUMER - TOTAL_CARDS_TO_DISPLAY;
  expect(calculateListAugment(TOTAL_CARDS_TO_DISPLAY)).toEqual(0);
});

test('It calculateListAugment performs as expeced when not at beginning or end of list, moving next', () => {
  currentFirstCardIndex = 3;
  expect(calculateListAugment(TOTAL_CARDS_TO_DISPLAY)).toEqual(3);
});

test('It calculateListAugment performs as expeced when not at beginning or end of list, moving previous', () => {
  currentFirstCardIndex = 3;
  expect(calculateListAugment(-TOTAL_CARDS_TO_DISPLAY)).toEqual(0);
});

