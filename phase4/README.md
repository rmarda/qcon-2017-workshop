# Phase 4

## Goals

1. Configure a DynamoDB table using SAM
1. Use environment variables in a Lambda
1. Write to DynamoDB from a Lambda

## Steps

1. Address TODOs in Java source code.
1. Deploy your updated Lambda code and application stack:

    ```bash
    $ mvn clean package
    $ mvn assembly:single
    $ aws s3 cp target/source.zip $BUILD_S3/source.zip
    ```

1. Send some more events:

    $ cd event-generator
    $ mvn clean package
    $ java -jar target/event-generator-1.0-SNAPSHOT.jar --limit 1 --stack serverless-weather
    API url: https://o30vnzv5ci.execute-api.us-west-2.amazonaws.com/prod/events
    request: {"timestamp":1494108607587,locationName":"Montgomery, AL","latitude":32.361538,"longitude":-86.279118,temperature":85.5631845254945}
    response: 200 / 8e3510b9-1e34-586e-8c5c-413bf809c4c4
    ```

1. Observe that the events were persisted to DynamoDB.

## Solutions

### TODO C

```java
this.dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
this.tableName = System.getenv("LOCATIONS_TABLE");
```

### TODO D

```java
Table table = dynamoDB.getTable(tableName);
Item item = new Item()
        .withPrimaryKey("locationName", weatherEvent.locationName)
        .withDouble("temperature", weatherEvent.temperature)
        .withLong("timestamp", weatherEvent.timestamp)
        .withDouble("longitude", weatherEvent.longitude)
        .withDouble("latitude", weatherEvent.latitude);
table.putItem(item);
```
