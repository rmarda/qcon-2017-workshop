# Symphonia Pipeline Demo

## Lifecycle

#### Build Java artifacts using Maven

```bash
$ mvn clean package
```

#### Deploy Serverless application using AWS Cloudformation

```bash
$ ./bin/deploy.sh
```

#### Send HTTP requests

```bash
$ ./bin/events.sh
```

#### Build/deploy cycle

```bash
$ mvn package && ./bin/deploy.sh
```

#### Tear down application and infrastructure

```bash
$ aws cloudformation delete-stack --stack-name pipeline-demo

```

## Architecture

```text
+---------------------------------------------+
| API Gateway                                 |
|  GET or POST -> https://.../prod/<any path> |
+----------------------+----------------------+
                       |
                (HTTP request)
                       |
                       V
+----------------------+----------------------------------+
| Lambda (io.symphonia.LocationsPersistLambda::handler) |
+----------------------+----------------------------------+
                       |
        (io.symphonia.domain.WeatherEvent)
                       |
                       V
                  +----+----+
                  | Kinesis |
                  +----+----+
                       |
                       V
+----------------------+-------------------------------+
| Lambda (io.symphonia.PipelineKinesisLambda::handler) |
+----------------------+-------------------------------+
             |                              |
  (request_id, lambda_id)              (lambda_id)
             |                              |
             V                              V
+---------------------------+  +--------------------------+
| DynamoDB 'requests' table |  | DynamoDB 'lambdas' table |
+---------------------------+  +--------------------------+
```

## TODO

1. Split out infrastructure into separate Cloudformation template
1. Parameterize stack name in scripts
1. Rename components?

