# Selja

Selja (Icelandic: Sell) is a sample project demonstrating backend and fronted written in Kotlin. Platform allows you to publish advertisements and browse other ads. added by users in your area. I've created that project to practice Spring Boot and try Koin DI for Android.

## Idea

As a user you can publish advertisement for a given period of time (1 - 7 days). You can specify title, description, phone, contact number, photo. If you enable location on the device server returns only ads. withing 10 km range. Once advertisement is added, it has red border on the ads. overview.

## Technology stack

### Backend

Spring Boot + PostgreSQL

Backend written in Spring Boot using Kotlin. Backend is responible for handling incoming POST requests, handle photo uploading, returning all advertisements, advertisements based on location. Every one hour scheduler deletes expired ads. 

Endpoint:

`GET items/` - returns all not expired ads from DB. Because location is not specified, distance field is 0.0

```json
[
    {
        "id": 1,
        "deviceId": "43242342",
        "name": "My Ad",
        "phoneObfuscated": "+12****9",
        "photoUrl": "",
        "price": 23.23,
        "currency": "zł",
        "distanceInKm": 0,
        "validUntil": 1571314858175
    },
    {
        "id": 2,
        "deviceId": "12345",
        "name": "My Super ad!",
        "phoneObfuscated": "+48****0",
        "photoUrl": "",
        "price": 99.99,
        "currency": "zł",
        "distanceInKm": 0,
        "validUntil": 1571315054686
    },
    {
        "id": 3,
        "deviceId": "12345",
        "name": "My Super ad!",
        "phoneObfuscated": "+48****0",
        "photoUrl": "",
        "price": 99.99,
        "currency": "zł",
        "distanceInKm": 0,
        "validUntil": 1571315948026
    }
]
```
`GET items?lat=52.65&long=-8.53` - returns ads. that are in 10km radius range. 

```json
[
    {
        "id": 1,
        "deviceId": "43242342",
        "name": "My Ad",
        "phoneObfuscated": "+12****9",
        "photoUrl": "",
        "price": 23.23,
        "currency": "zł",
        "distanceInKm": 5.071830232950068,
        "validUntil": 1571314858175
    }
]
```

`GET items/{id}` - returns ads that matches given id. That response contains extra field `description` that is not included in list response. Description might be long and we don't want to waste resources :)

```json
{
    "id": 1,
    "deviceId": "43242342",
    "name": "Absolute",
    "description": "test tes ttes test lorem ipsum",
    "phone": "123 456 789",
    "phoneObfuscated": "123****9",
    "photoUrl": "",
    "price": 23.23,
    "currency": "zł",
    "distanceInKm": 0,
    "validUntil": 1571314858175
}
```
`GET items/{id}?lat=52.65&long=-8.53` - same as previous but distance field is filled

`POST /items` - ads new item to the DB

Request body
```json
{	
    "deviceId" : "12345",
	  "name" : "My Super ad!",
    "price": 99.99,
    "validFor": 86400000,
    "phone" : "+48 663 000 000",
    "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
    "long": 18.00,
    "lat": 53.66
}
```

Response

```json
{
    "id": 3,
    "deviceId": "12345",
    "name": "My Super ad!",
    "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
    "phone": "+48 663 000 000",
    "phoneObfuscated": "+48****0",
    "photoUrl": "",
    "price": 99.99,
    "currency": "zł",
    "distanceInKm": 0,
    "validUntil": 1571315948026
}
```

`deviceId` - unique id for device, to distinguish ads added by user from other ads. I know this is not perfect, but I'm not using loging and this is just sample.

`PUT /images/{id}` - allows to add image to ads with given id. Response contains `photoUrl` field

`"photoUrl": "images/XcfGdsFsFR"`

To start backend server you need to have postgreesql db installed on your machine - [sample tutorial](https://www.robinwieruch.de/postgres-sql-macos-setup)

Create a user that matches configuration (or change configuration file application.properties) 

`spring.datasource.username=selja`
`spring.datasource.password=Secret123`

Once user is created and postgresql is running, go to main directory and type

`./gradlew bootRun`

### Fronted

Currently frontend is just an android app, but I have plans to write web client in ReactJS

In Android I've used:
* Retrofit for network calls
* RxKotlin, RxBindings
* Kotlin coroutines
* Android data binding
* Koin for DI (really like it)
* Fresco for image handling

![alt text](https://github.com/NieKam/Selja/blob/master/screenshots/dashboard_framed.png "Dashboard")
![alt text](https://github.com/NieKam/Selja/blob/master/screenshots/ad_details_framed.png "Ad details")![alt text](https://github.com/NieKam/Selja/blob/master/screenshots/new_framed.png "New ad")

User ad is marked by red border

![alt text](https://github.com/NieKam/Selja/blob/master/screenshots/dashboard_user_ad_framed.png "User ad")

Video

[![Android app](https://img.youtube.com/vi/rAh5DK5d9gw/0.jpg)](https://www.youtube.com/watch?v=rAh5DK5d9gw)
