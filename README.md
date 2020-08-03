# SmallCity

| Created by Abakir Hanna, Ethan Mooney, and Lindsey Weiskopf


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
```
1. Make a env file that is the same format as the envSample file with 
   your correct creditionals in the same directory
2. Find your Google's API Key creditional and paste it next to the 
   APIKEY field 
3. Follow the steps "Set up search engine that searches within linkedin
   for the CX creditional" to get your CX Key
4. To make this file seen by Google's app engine for local and deployement 
   services copy this file in the target/SmallCity-1/ folder
  - The targe directory is created after you follow the build instructions
```

# Set up search engine that searches within linkedin for the CX creditional
```
1. Go to the Google's programmable search console https://cse.google.com/all
2. Press the add button 
3. Under Sites to search, type https://www.linkedin.com/company/
4. Name the search engine, or keep the default Name
5. Press the create button
6. After the search engine is created, click on the Control Panel button
7. Copy the Search Engine ID which is the CX creditional you need in your env file
```

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

# Deploy Instructions 

```
1. Open pom.xml file
2. Find <deploy.projectId>Name_Of_App_Engine_Project_ID</deploy.projectId> 
3. Change the "Name_Of_App_Engine_ID" to your project app engine ID
4. Run the command mvn package appengine:deploy
```