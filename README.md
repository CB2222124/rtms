# RTMS | Road Tax Management System
RESTful web service for a road tax management system.

## Getting started
To get the service up and running, execute the following commands:
```bash
git clone https://github.com/CB2222124/rtms.git
cd rtms
docker compose up --build -d
```
The service will now be exposed at http://localhost:8081.

## Postman
It is recommended to test the service using the provided `postman-collection.json`:
- Recieve a bearer token from one of the authentication endpoints at the top package level.
- Configure postman to use the token for authorization within the Service package.
- All requests within the Service package will be authenticated using the token.

Note: A tax class and owner must be created before a vehicle can be created. There is no pre-populated data.