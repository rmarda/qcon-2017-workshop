# Phase 2

## Goals

1. Use POJO input and output classes
1. Build and deploy a Lambda using CodePipeline, CodeBuild, and the Serverless Application Model
1. Demonstrate JSON serialization / deserialization

## Steps

1. Create a zip file containing the source code:
    ```bash
    $ mvn clean assembly:single
    $ l$ ls target/source.zip
    target/source.zip
    ```
1. Find the S3 bucket associated with the build pipeline:
    ```bash
    $ BUILD_S3=$(aws cloudformation list-exports --query 'Exports[?Name==`ServerlessWeatherSourceBucket`].Value' --output text)
    $ echo $BUILD_S3
    s3://serverless-weather-build-sourcebucket-9ffxyj7aujts
    ```

1. Upload the zip file to the S3 bucket:
    ```bash
    $ aws s3 cp target/source.zip $BUILD_S3
    ```

1. Observe the build pipeline deploy the Lambda function

1. Get the Lambda name from the AWS web console (or via the CLI).

1. Invoke the Lambda using the AWS CLI:
    ```bash
    $ aws lambda invoke --function-name <LAMBDA NAME> \
          --payload '{"locationName": "New York, NY", "temperature": 75.0}' out
    $ cat out && echo
    {"locationName":"New York, NY","temperature":75.0}
    ```