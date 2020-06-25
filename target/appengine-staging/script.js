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

/**
 * Adds a random greeting to the page.
 */
const nameOfForm = "contact";
const nameOfContact = "Name";
const emailOfContact = "Email";
const numberOfContact = "Number";
const deleteButton = "Delete";
const idOfContactList = "contact-list";

let positions = [ 'Freelance Web Developer', 'Robotics Builder', 'Graphic Designer', 'Entrepreneur' ];
let i = 0;
let contactIds = [];
let onlyElementInArray = 0; 
let checkIfAnElementIsEmpty = 0;
setInterval(changePositionDisplayed, 3000);

function  changePositionDisplayed(){
    $('#position').fadeTo(300, 0).fadeTo(300, 1); 
    $('#position').text(positions[i]);
    i++;
    if (i === positions.length) {
      i = 0;
    }
}

function playGame(){
  window.open('https://abakirh.github.io/pokebattle/index.html', '_blank');
}

function viewGame(){
  window.open('https://github.com/AbakirH/AbakirH.github.io/blob/master/pokebattle/sketch.js', '_blank');
}

function getGreeting() {
  fetch('/data')
  .then(response => response.text())
  .then((name) => {
    document.getElementById('greeting').innerText = name;
  });
}

function getJSONData(){
  fetch('/data')  
  .then(response => response.json()) 
  .then((message) => {

      let messageConntainer = document.getElementById("messages");

      for(let i = 0; i < message.length; i++){
        let pTag = document.createElement('p');
        let text = document.createTextNode(message[i]);
        pTag.appendChild(text);
        messageConntainer.appendChild(pTag);
      }

  });
}

function getComment(){
  fetch('/comment')  
  .then(response => response.json()) 
  .then((comments) => {
    checkNumberOfComments(comments);
    let commentContainer = document.getElementById("comments");

    comments.forEach((comment) => {
      commentContainer.appendChild(createListElement(comment));
    });
  });
}
function checkNumberOfComments(array){
  let commentsMax = 5;  // This is an arbitrary number because I do no want that many comments on my page
  if(array.length > commentsMax ){
    array.clear();
  }
}
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

function loadContacts(){
  fetch('/list-contacts')
  .then(response => response.json())
  .then((contacts) => {
    const taskListElement = document.getElementById(idOfContactList);
    contacts.forEach((contact) => {
      taskListElement.appendChild(createcontactListElement(contact));
    });
  });
}

function createcontactListElement(contact) {
  contactIds.push(contact.id);

  const contactListElement = document.createElement('li');
  contactListElement.className = nameOfForm;

  const nameOfPerson = document.createElement('span');
  nameOfPerson.innerText = nameOfContact + ": " + contact.name;

  const emailOfPerson = document.createElement('span');
  emailOfPerson.innerText = emailOfContact + ": " + contact.email;

  const numberOfPerson = document.createElement('span');
  numberOfPerson.innerText = numberOfContact + ": " + contact.number;

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = deleteButton;
  deleteButtonElement.addEventListener('click', () => {
    deleteTask(contact);
    contactListElement.remove();
  });
  contactListElement.appendChild(nameOfPerson);
  contactListElement.appendChild(emailOfPerson);
  contactListElement.appendChild(numberOfPerson);
  contactListElement.appendChild(deleteButtonElement);
  
  return contactListElement;
}

function deleteTask(contact) {
  const params = new URLSearchParams();
  params.append('id', contact.id);
  fetch('/delete-contact', {
        method: 'POST', 
        body: params
        }
      );
}

function deleteAll() {
  contactIds.forEach((contact) => {
    const params = new URLSearchParams();
    params.append('id', contact);
    fetch('/delete-contact', {
          method: 'POST', 
          body: params
          }
        );
  })
  location.reload();
}

function fetchBlobstoreUrlAndShowImage() {
  fetch('/blobstore-upload-url')
  .then((response) => {
    return response.text();
  })
  .then((imageUploadUrl) => {
    const messageForm = document.getElementById('my-form');
    messageForm.action = imageUploadUrl;
    messageForm.classList.remove('hidden');
    getImageSRC();
  });
}

function getImageSRC() {
  fetch('/my-form-handler')
  .then(response => response.json())
  .then((images) => {
    if(images.length == checkIfAnElementIsEmpty ){
      //This is not an error, just to show the user that it is waiting for an image to be uploaded
      throw new Error('Image is not uploaded yet');
    }else{
      const divImageId = document.getElementById("myImg");
      const uploadedImageFile = document.createElement('IMG');
      //Their should only ever be one element inside the array allowing for multiple submits
      uploadedImageFile.src =images[onlyElementInArray];
      divImageId.appendChild(uploadedImageFile);
    }
  })
  .catch(warning => {
    console.log('Waiting', warning);
  });
}

