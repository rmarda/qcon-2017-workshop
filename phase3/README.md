# Phase 3

## Goals

1. Use API Gateway Request/Response classes
1. Configure an API Gateway POST resource using SAM
1. Inspect SAM outputs using the AWS CLI
1. Send an HTTP POST request to API Gateway
1. Validate API Gateway requests using JSON Schema

## Steps

1. Address the various TODOs in the Java source.
1. Deploy your updated Lambda code and application stack:

    ```bash
    $ mvn clean package
    $ mvn assembly:single
    $ aws s3 cp target/source.zip $BUILD_S3/source.zip
    ```

1. Send some data!

    Once the stack is deployed, we can send some test data to it using the `event-generator`.

    ```bash
    $ cd event-generator
    $ mvn clean package
    $ java -jar target/event-generator-1.0-SNAPSHOT.jar --limit 1 --stack serverless-weather
    API url: https://o30vnzv5ci.execute-api.us-west-2.amazonaws.com/prod/events
    request: {"timestamp":1494108607587,locationName":"Montgomery, AL","latitude":32.361538,"longitude":-86.279118,temperature":85.5631845254945}
    response: 200 / 8e3510b9-1e34-586e-8c5c-413bf809c4c4
    ```

    Feel free to send more than just one request - adjust the `--limit` parameter.

1. Send an invalid request:

    ```bash
    $ java -jar target/event-generator-1.0-SNAPSHOT.jar --limit 1 --stack serverless-weather --invalid
    ```
