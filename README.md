# Fraud Detection Application

## To start the application must have Docker installed

## In the main folder there is file: docker-compose.yml, which could be started with docker-compose up command from Docker CLI

* In the Docker Compose file is Postgres DB, Redis, Kafka and Fraud Detection Application. 
* The server (the created spring-boot application) has logic to download Maven and Java, and automatically start and connect to Posgres DB.

## If the application is started successfully, this curl could be used in Postman to call transaction API and add transaction

* **Curl:**
curl --location 'localhost:8080/transactions' \
--header 'Content-Type: application/json' \
--data '{
  "user_id": "user_1",
  "amount": 200.50,
  "country": "UK",
  "lat_coord": 17.7749,
  "long_coord": -122.4194
}'

## Blacklisted countries by default are: BG, UK and ROM

## There could be simulated client environment from Google Chrome which will listen on WebSocket for Fraud Alerts

* Open Incognito Browser in Google Chrome

* Open Developer Tools and open Console tab

* **Paste the following code there and Message will be shown when there is fraud application:**

let socket = new WebSocket("ws://localhost:8080/fraud-alerts");

socket.onmessage = function(event) {
    console.log("Fraud Alert Received: " + event.data);
};

socket.onopen = function() {
    console.log("Connected to WebSocket!");
};

* If connection is established there will be message: "Connected to WebSocket!"
* If paste is not allowed you can write in the console: "allow paste"
