# Symphonia / OSCON Tutorial - Phase 1

## Serverless Components

1. Lambda function (events-ingest-lambda)

## Learn

### Fix the Lambda!

```bash
$ cd phase1
$ mvn clean test
```

The `EventsIngestLambdaTest` is failing. Fix `EventsIngestLambda` and make the test pass.

Once the test passes, deploy your application stack:

```bash
$ mvn install
$ mvn assembly:single
$ aws s3 cp target/oscon-2017-tutorial-phase1-1.0-SNAPSHOT.zip $S3_SOURCE/source.zip
```

Verify that the Code Pipeline deploy process has started: https://console.aws.amazon.com/codepipeline/home

### Invoke the Lambda!

Once your Lambda function is deployed, invoke it using the AWS API (or the web console):

```bash
$ aws lambda invoke --function-name FUNCTION_NAME \
                    --invocation-type RequestResponse \
                    --log Tail \
                    --payload '{"locationId": "test123"}' \
                    out
```

## Explore

1. CodeBuild `buildspec.yml` file
1. Serverless Application Model `sam.yml` file
1. Lambda console (https://console.aws.amazon.com/lambda)
1. Cloudwatch Logs console (https://console.aws.amazon.com/cloudwatch/home?#logs)
