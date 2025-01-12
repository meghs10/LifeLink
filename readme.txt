
#LIFELINK - Emergency Medical Service System

##Overview

LifeLink is a comprehensive emergency medical service system consisting of multiple Android applications and a Spring Boot backend. The system facilitates quick ambulance booking, hospital coordination, and blood bank management during medical emergencies.

#Features

###User App

- Emergency ambulance booking

- Real-time driver tracking

- User profile management

- Secure authentication with JWT

- Automatic token refresh mechanism

- CPR instructions in case of emergency


###Driver App

- Real-time booking notifications

- Navigation to pickup and drop locations

- Live location updates

- Profile management

###Hospital App

- Emergency case notifications

- Patient information management

- Case status updates

- Profile management

###Blood Bank App

- Blood request management

- Emergency notifications


##Technical Stack

###Mobile Applications

- **Platform**: Android (Java)

- **IDE**: Android Studio

- **Minimum SDK**: 24

- **Target SDK**: 34

### Backend

- **Framework**: Spring Boot 3.2.0

- **Build Tool**: Maven

- **Java Version**: 17

- **Database**: MongoDB Atlas

###APIs & Services Used

- Google Maps SDK for Android

- Maps Places API

- Maps Distance Matrix API

- Maps Geolocation API

- Firestore realtime database

- MongoDB Atlas

###Security

- JWT Authentication

- Refresh Token Mechanism

- HTTPS encryption

- Role-based access control

##Deployment Guide

###Backend Deployment on Render

1. **Prerequisites**

   - GitHub repository

   - Render account

   - MongoDB Atlas database

   - Dockerfile in repository

2. **Dockerfile**
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```


3. **render.yaml**
```render.yaml
services:
  - type: web
    name: lifelink-api
    env: docker
    plan: free
    healthCheckPath: /api/health
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: MONGODB_URI
        sync: false  
      - key: JWT_SECRET
        sync: false
      - key: JWT_ACCESS_EXPIRATION
        value: 3600000
      - key: JWT_REFRESH_EXPIRATION
        value: 86400000
```


4. **Deployment Steps**

   - Connect GitHub repository to Render

   - Create new Web Service

   - Select repository

   - Configure environment variables

   - Deploy

###MongoDB Atlas Setup

1. Create cluster

2. Configure network access

3. Create database user

4. Get connection string

5. Configure in application

##Installation & Setup

###Backend Setup in local
```bash
#Clone repository
git clone https://github.com/milky-way-1/lifelinkbackend.git

#Navigate to backend directory
cd lifelinkbackend

#Install dependencies
mvn install

#Run application
mvn spring-boot:run
```


###Android Apps Setup

1. Open project in Android Studio

2. Configure Google Maps API key in `AndroidManifest.xml`

3. Update backend URL in `Apiservice.java`

4. Build and run

##Environment Variables
```
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/database
JWT_SECRET=your_jwt_secret_key
GOOGLE_MAPS_API_KEY=your_google_maps_api_key
```

##About Render

Render is a unified cloud platform that helps build and run applications and websites with free TLS certificates, global CDN, private networks, and auto-deploy from Git. 

**Key Features**:

- Automatic HTTPS

- Continuous deployment from Git

- Docker support

- Custom domains

- Environment variable management

##Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
