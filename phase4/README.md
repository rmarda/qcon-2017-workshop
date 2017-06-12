# Phase 4

## Serverless Components

1. Lambda function (events-ingest-lambda)
1. API Gateway (`Api` in the `sam.yml` file)
1. DynamoDB table (`LocationsTable` in the `sam.yml` file)
1. Another Lambda function (locations-query-lambda)

## Learn

### Fix a different Lambda!

```bash
$ cd phase4
$ mvn clean test
```

With data now being stored in DynamoDB, it would sure be nice if we could get it using HTTP GET requests. To that end
we've added a LocationsQueryLambda, which will handle GET requests to the API Gateway's `/prod/locations` endpoint.

Along with our new Lambda, we have a new unit test, `LocationsQueryLambdaTest`, which is failing because the `LocationsQueryLambda`
isn't setting the DynamoDB scan limit correctly.

Fix `LocationsQueryLambda` and make the test pass.

Once the test passes, deploy your application stack:

```bash
$ mvn install
$ mvn assembly:single
$ aws s3 cp target/source.zip $S3_SOURCE/source.zip
```

Verify that the Code Pipeline deploy process has started: https://console.aws.amazon.com/codepipeline/home

### Send some data!

Once the stack is deployed, we can send some test data to it using the `event-generator`.

```bash
$ cd event-generator
$ mvn clean package
$ java -jar target/event-generator-1.0-SNAPSHOT.jar --limit 20 --stack serverless-weather
API url: https://o30vnzv5ci.execute-api.us-west-2.amazonaws.com/prod/events
request: {"timestamp":1494108607587,"locationId":"1978BE5B1CD1DFA1A247E8B3BD6827D2","locationName":"Montgomery, AL","latitude":32.361538,"longitude":-86.279118,"city":"Montgomery","state":"AL","temperature":85.5631845254945}
response: 200 / 8e3510b9-1e34-586e-8c5c-413bf809c4c4
```

Use your web browser to visit the API Gateway's `/prod/locations` endpoint (use your API url):

`https://o30vnzv5ci.execute-api.us-west-2.amazonaws.com/prod/locations`

After a few seconds, you should see a JSON array with data in it!

## Explore

1. The new parts of the `sam.yml` file, which map the `/locations` endpoint to our new Lambda function.
