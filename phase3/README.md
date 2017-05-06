# Symphonia / OSCON Tutorial - Phase 3

## Serverless Components

1. Lambda function (events-ingest-lambda)
1. API Gateway (`Api` in the `sam.yml` file)
1. DynamoDB table (`LocationsTable` in the `sam.yml` file)

## Learn

### Fix the Lambda (yet again)!

```bash
$ cd phase3
$ mvn clean test
```

Now that we're receiving validated events from API Gateway into our Lambda function, let's pull out
the location information and store that in DynamoDB.

The previous unit test has been updated, and is failing because our Lambda code isn't retrying throttled DynamoDB `put` requests.

The `EventsIngestLambdaTest` is failing. Fix `EventsIngestLambda` and make the test pass.

Once the test passes, deploy your application stack:

```bash
$ mvn install
$ mvn assembly:single
$ aws s3 cp target/oscon-2017-tutorial-phase3-1.0-SNAPSHOT.zip $S3_SOURCE/source.zip
```

Verify that the Code Pipeline deploy process has started: https://console.aws.amazon.com/codepipeline/home

### Send some data!

Once the stack is deployed, we can send some test data to it using the `event-generator`.

```bash
$ cd event-generator
$ mvn clean package
$ java -jar target/event-generator-1.0-SNAPSHOT.jar --limit 1 --stack oscon-2017-tutorial-application
API url: https://o30vnzv5ci.execute-api.us-west-2.amazonaws.com/prod/events
request: {"timestamp":1494108607587,"locationId":"1978BE5B1CD1DFA1A247E8B3BD6827D2","locationName":"Montgomery, AL","latitude":32.361538,"longitude":-86.279118,"city":"Montgomery","state":"AL","temperature":85.5631845254945}
response: 200 / 8e3510b9-1e34-586e-8c5c-413bf809c4c4
```

Feel free to send more than just one request - adjust the `--limit` parameter.

Once you've sent some events successfully, visit the DynamoDB console (https://console.aws.amazon.com/dynamodb/home), click
on the `Locations` table, and the `Items` tab to see the items in the table.

## Explore

1. The other parts of the DynamoDB console (https://console.aws.amazon.com/dynamodb/home)
