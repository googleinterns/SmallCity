# SmallCity

| Created by Abakir Hanna, Ethan Mooney, and Lindsey Weiskopf [SmallCity Live Demo](https://step2020-smallcity.appspot.com/)

### Objective

We aim to create a web platform for users to find products from local small businesses. Our web app will increase exposure to local businesses of interest in their community. 

### Background

The U.S Small Business Administration Office of Advocacy defines a small business as a company with fewer than 500 employees. These small businesses are an integral part of the world’s economy and are often overlooked. Making up 44% of economic activity in the US, small businesses often struggle to keep up with the growing demand for instant delivery via online shopping. Only amplified during crises like the Covid-19 pandemic, the uphill battle that these businesses face poses a real threat to their stability and, in turn, the US economy.

Right now, many small local businesses get lost in the shadow of large businesses on online shopping platforms. But, as buyers continue to purchase more products from the comfort of their own homes, we want to give them an option to support the businesses in their community with ease. In a 2020 survey by Salesforce, 86% of consumers reported that they would pay more to support a small local business. Giving users the ability to filter for small local businesses on an already trusted platform like Google Shopping would allow users to shop locally from wherever they are while boosting local economies. 

# Build Instruction
```
mvn install
```

# Making the env file instructions

1. Make an env file that is the same format as the envSample file with 
   your correct credentials in the same directory
2. Find your Google's API Key credential and paste it next to the 
   APIKEY field 
3. Follow the steps "Set up CX credential" to get your CX Key
4. To make this file seen by Google's app engine for local and deployment 
   services copy this file in the target/SmallCity-1/ folder `cp env target/SmallCity-1/`
   - The target directory is created after you follow the build instructions

# Get CX Credential

1. Go to Google's programmable [search console](https://cse.google.com/all)
2. Press the add button 
3. Under Sites to search, type https://www.linkedin.com/company/
4. Name the search engine, or keep the default Name
5. Press the create button
6. After the search engine is created, click on the Control Panel button
7. Copy the Search Engine ID which is the CX credential you need in your env file

<img src='https://imgur.com/axyAU2J.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LICEcap](http://www.cockos.com/licecap/)

# Test Instructions

Run all tests
```
mvn test
```

Run one test class file 
```
mvn -Dtest=FileNameOfTestClass test
```

# Run App Locally Instruction

```
mvn package appengine:run
```

# Live Server Instructions 
To deploy to a live server:
  1. Navigate to https://console.cloud.google.com/home/dashboard.
  2. Make sure your project is selected in the dropdown at the top.
  3. Find the Project ID on that page.
  4. Open pom.xml file
  5. Find <deploy.projectId>YOUR_PROJECT_ID_HERE</deploy.projectId> 
  6. Change "YOUR_PROJECT_ID_HERE" to your project app engine ID
  7. Enable Cloud Build on your project by visiting https://console.developers.google.com/apis/api/cloudbuild.googleapis.com/overview?project=YOUR_PROJECT_ID_HERE
      - Will require enabling billing.
  8. Run the command `mvn package appengine:deploy`
