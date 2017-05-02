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

## Teardown

1. Delete the build pipeline stack:
    ```bash
    $ aws cloudformation delete-stack --stack-name oscon-2017-tutorial-build
    ```

1. Delete the application stack:
    ```bash
    $ aws cloudformation delete-stack --stack-name oscon-2017-tutorial
    ```
