# Symphonia / OSCON 2017 Tutorial

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
$ aws cloudformation delete-stack --stack-name <stack name>

```
