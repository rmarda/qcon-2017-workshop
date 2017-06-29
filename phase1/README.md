# Phase 1

## Steps

1. Build the basic synchronous Lambda, in Java
    ```bash
    $ mvn clean package
    ```
1. Create and deploy the Lambda using the AWS web console
1. Invoke the Lambda using the AWS web console
1. Delete the Lambda using the AWS command line interface
    ```bash
    $ aws lambda delete-function --function-name HelloWorldLambda
    ```
1. Create and deploy the Lambda using the AWS command line interface
    ```bash
    $ LAMBDA_ROLE=`aws iam get-role --role-name lambda_basic_execution --query Role.Arn --output text`
    $ aws lambda create-function \
                 --function-name HelloWorldLambda \
                 --runtime java8 \
                 --handler io.symphonia.HelloWorldLambda::handler \
                 --memory-size 512 \
                 --timeout 10 \
                 --role ${LAMBDA_ROLE} \
                 --zip-file fileb://target/lambda.jar
    ```
1. Invoke the Lambda using the AWS command line interface
    ```bash
    $ aws lambda invoke --function-name HelloWorldLambda --payload '"world"' out
    $ cat out && echo
    "Hello, world!"
    ```
1. View Cloudwatch Metrics
1. View Cloudwatch Logs output
