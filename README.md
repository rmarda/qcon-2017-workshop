# Symphonia / OSCON 2017 Tutorial

## Initial Setup

1. Make sure your AWS user has full permissions within your AWS account.

    The most flexible scenario is one in which your user is part of the `Administrators` group. 
    
    You can check from the command line:
    ```bash
    $ aws iam get-group --group-name Administrators --query 'Users[].UserName'
    ```
    The output will be the list of users in the `Administrators` group:
    ```json
      [
          "mike", 
          "john"
      ]
    ```
    Alternatively, make sure that your user has a policy that looks like this:
    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": "*",
                "Resource": "*"
            }
        ]
    }
    ```
    
1. Download and unzip the tutorial repository from Github:

    ```bash
    $ wget https://github.com/symphoniacloud/oscon-2017-tutorial/archive/master.zip
      ...
    $ unzip oscon-2017-tutorial-master.zip
      ...
    $ cd oscon-2017-tutorial-master
    ```
    
1. Create the tutorial's build pipeline using Cloudformation:
    ```bash
    $ aws cloudformation create-stack \
            --capabilities CAPABILITY_IAM \
            --stack-name oscon-2017-tutorial-build \
            --template-body file://build-pipeline.yml
    ```
   The output should look something like this:
    ```json
    {
        "StackId": "arn:aws:cloudformation:us-west-2:1234567890:stack/oscon-2017-tutorial-build/7972b720-2f5b-11e7-bd3d-503acbd4dcfd"
    }
    ```
    
1. Get the S3 url where you'll upload code during the tutorial:
    ```bash
    $ aws cloudformation describe-stacks \
            --query 'Stacks[?StackName==`oscon-2017-tutorial-build`].Outputs[0][?OutputKey==`SourceS3Bucket`].OutputValue' \
            --output text
    ```
    The output should look something like this:
    ```
    s3://oscon-2017-tutorial-build-sources3location-hdea5qp6h2o
    ```
    
## Workflow

1. Develop locally, running tests, etc...
    ```bash
    $ mvn clean test
    ```

1. Package your project sources into a zip file. Exclude unnecessary files as shown here:
    ```bash
    $ rm -f source.zip && zip -r source.zip . -x README.md -x build-pipeline.yml -x \*/target/\* -x \*.git\* -x \*.iml -x \*.idea\*
    ```
    Note that your `source.zip` file should be fairly small, not megabytes! Mine is 184 kilobytes:
    ```bash
    $ du -hs source.zip 
      184K	source.zip
    ```
    
1. Upload your `source.zip` to the S3 bucket we looked up earlier:
    
    ```bash
    $ aws s3 cp source.zip s3://oscon-2017-tutorial-build-sources3location-hdea5qp6h2o
      upload: ./source.zip to s3://oscon-2017-tutorial-build-sources3location-hdea5qp6h2o/source.zip
    ```

1. Go to your [Cloudformation Console](https://console.aws.amazon.com/cloudformation/home), and wait until you see the `CREATE_COMPLETE` status for the `oscon-2017-tutorial` stack.

1. Compile and run the event generator to send events to your API Gateway (it will be automatically discovered):
    ```bash
    $ mvn -pl event-generator compile exec:java
      ...
      [INFO] --- exec-maven-plugin:1.6.0:java (default-cli) @ event-generator ---
      API url: https://o30vnzv5ci.execute-api.us-west-2.amazonaws.com/prod/events
      request: {"timestamp":1493754876588,"locationId":"1978BE5B1CD1DFA1A247E8B3BD6827D2","city":"Montgomery","state":"AL","temperature":42.98528743998604}
      response: 200 / 8e3510b9-1e34-586e-8c5c-413bf809c4c4
      ...
    ```

## Teardown

1. Delete the build pipeline stack:
    ```bash
    $ aws cloudformation delete-stack --stack-name oscon-2017-tutorial-build
    ```

1. Delete the application stack:
    ```bash
    $ aws cloudformation delete-stack --stack-name oscon-2017-tutorial
    ```
