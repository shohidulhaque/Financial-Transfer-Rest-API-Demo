#Financial Transfer Rest API Demo

A Java RESTful API fof financial transfers between users accounts

### Technologies
- JDK 8
- JAX-RS API
- Jetty Container
- H2 in memory database
- Apache HTTP Client

### How to run
```sh
mvn exec:java
```

Application starts on localhost port 8080. Some sample data is added to a embedded H2 database.

### Available Services

| POST | /transaction/v1/ | perform transaction between 2 user accounts | 


#### User Transaction:
```sh
request json:

{
    "amount":10.0000,
    "fromAccountNumber":"31223123",
    "toAccountNumber":"21223123"
}
```
```
response json:
{
    "responseCode":"SUCCESS",
    "timestampOfTransfer":1528346017761,
    "fromAccountNumber":"31223123",
    "toAccountNumber":"21223123",
    "amount":10.0000
}
```