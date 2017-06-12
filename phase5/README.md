# Phase 5

## Serverless Components

1. Lambda function (events-ingest-lambda)
1. API Gateway (`Api` in the `sam.yml` file)
1. DynamoDB table (`LocationsTable` in the `sam.yml` file)
1. Another Lambda function (locations-query-lambda)

## Learn

In this phase, we've added an HTML/Javascript front-end, to display each location's current temperature on a map.

Open `map.html` in your web browser. If nothing appears on the map, check the Javascript console. You might see an error
related to "Cross Origin Resource Sharing".

How can we fix this?

### Fix the LocationsQueryLambda (again)!

```bash
$ cd phase5
$ mvn clean test
```

Now that we want to call the `locations` endpoint from a web site, we'll need to add a header to the HTTP
response that indicates to the browser it's safe to proceed.

A `LocationsQueryLambdaTest` test is failing, because we haven't correctly configured the HTTP response in
the `LocationsQueryLambda` to return the necessary header.

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
$ java -jar target/event-generator-1.0-SNAPSHOT.jar --stack serverless-weather
API url: https://o30vnzv5ci.execute-api.us-west-2.amazonaws.com/prod/events
request: {"timestamp":1494108607587,"locationId":"1978BE5B1CD1DFA1A247E8B3BD6827D2","locationName":"Montgomery, AL","latitude":32.361538,"longitude":-86.279118,"city":"Montgomery","state":"AL","temperature":85.5631845254945}
response: 200 / 8e3510b9-1e34-586e-8c5c-413bf809c4c4
```

Note that we've omitted the `--limit` parameter - the default value is `50`, which will populate our
map nicely.

Open the `map.html` file again in your browser. You should see 50 points on the map, each labeled
with a temperature and a color based on that temperature.

## Explore

1. `GeoJsonFeatureMapper` - see if you can add other information to the HTTP GET response, and display it on the map!
