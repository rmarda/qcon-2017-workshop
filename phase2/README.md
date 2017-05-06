# Symphonia / OSCON Tutorial - Phase 2

## Serverless Components

1. Lambda function (events-ingest-lambda)
1. API Gateway (`Api` in the `sam.yml` file)

## Learn

### Fix the Lambda (again)!

```bash
$ cd phase2
$ mvn clean test
```

We've introduced an API Gateway, which will forward HTTP requests to our Lambda function. However, that changes the input type for the Lambda.

The previous unit test has been updated, and is failing because our Lambda code isn't handling the new input correctly.

The `EventsIngestLambdaTest` is failing. Fix `EventsIngestLambda` and make the test pass.

Once the test passes, deploy your application stack:

```bash
$ mvn install
$ mvn assembly:single
$ aws s3 cp target/oscon-2017-tutorial-phase2-1.0-SNAPSHOT.zip $S3_SOURCE/source.zip
```

Verify that the Code Pipeline deploy process has started: https://console.aws.amazon.com/codepipeline/home

### Send some data!

Once the API Gateway is deployed, we can send some test data to it using the `event-generator`.

```bash
$ cd event-generator
$ mvn clean package
$ java -jar target/event-generator-1.0-SNAPSHOT.jar --limit 1 --stack oscon-2017-tutorial-application
API url: https://o30vnzv5ci.execute-api.us-west-2.amazonaws.com/prod/events
request: {"timestamp":1494108607587,"locationId":"1978BE5B1CD1DFA1A247E8B3BD6827D2","locationName":"Montgomery, AL","latitude":32.361538,"longitude":-86.279118,"city":"Montgomery","state":"AL","temperature":85.5631845254945}
response: 200 / 8e3510b9-1e34-586e-8c5c-413bf809c4c4
```

Note that the `event-generator` tool is just a convenience - feel free to use `curl` or any other tool
to send HTTP POST requests to the `/prod/events` endpoint of your API Gateway. The format of the JSON request is 
shown above.

### Validate the data!

What happens if we send some data that's missing the `locationId` field?

We can do this using the `--invalid` flag to the `event-generator`:

```bash
$ java -jar target/event-generator-1.0-SNAPSHOT.jar --invalid --limit 1 --stack oscon-2017-tutorial-application
API url: https://o30vnzv5ci.execute-api.us-west-2.amazonaws.com/prod/events
request: {"timestamp":1494108607587,"locationName":"Montgomery, AL","latitude":32.361538,"longitude":-86.279118,"city":"Montgomery","state":"AL","temperature":85.5631845254945}
response: 500 / {:message "Internal Server Error"}
```

Find the Cloudwatch logs output for this Lambda invocation, and you'll see a `NullPointerException`. Clearly, we need
`locationId` to be set for our Lambda to run successfully.

We could put that validation in the Lambda code, but an interesting new feature of API Gateway is the ability to perform
validation within the API Gateway itself, using JSON Schema. This way, if the API Gateway detects invalid data, the Lambda
function isn't even invoked - why pay for trying to process bad data?

Look at the commented-out sections of the `sam.yml` file to see how to add validation to the API Gateway. Uncomment those line and deploy your stack.

Once it's deployed, try sending some invalid data - you should notice a different response.

## Explore

1. API Gateway console (https://console.aws.amazon.com/apigateway/home)
1. API Gateway request validation (http://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-method-request-validation.html)
