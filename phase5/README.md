# Phase 5

## Goals

1. Use Maven sub-modules to build multiple Lambdas in a single Maven project
1. Use the AWS SDK Bill of Materials (BOM) in the Maven parent
1. Query DynamoDB from a Lambda
1. Configure an API Gateway GET resource using SAM

## Steps

1. Address TODOs in Java source code.
1. Deploy your updated Lambda code and application stack:

    ```bash
    $ mvn clean package
    $ mvn assembly:single
    $ aws s3 cp target/source.zip $BUILD_S3/source.zip
    ```

1. Use a browser to send a GET request to the new /locations endpoint.

## Solutions

### TODO D

    ```java
    int limit = Integer.parseInt(request.queryStringParameters.getOrDefault(LIMIT, DEFAULT_LIMIT));

    ScanRequest scanRequest = new ScanRequest()
            .withTableName(tableName)
            .withLimit(limit);

    ScanResult scanResult = amazonDynamoDB.scan(scanRequest);
    FeatureCollection featureCollection = toFeatureCollection(scanResult.getItems());
    String json = objectMapper.writeValueAsString(featureCollection);
    ```
