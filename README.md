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
    
1. Create the tutorial's build pipeline using Cloudformation. The build pipeline will create the application, also 
with Cloudformation. Note that the build pipeline stack name is specified using the `--stack-name` command line flag, while
the application stack name is specified as a parameter to the stack template.
   
   :warning: Stack names must be unique within an AWS account. If multiple attendees are working in the same account, 
   pick unique stack names for both stacks!
   
    ```bash
    $ aws cloudformation create-stack \
            --capabilities CAPABILITY_IAM \
            --stack-name oscon-2017-tutorial-build-pipeline \
            --parameters ParameterKey=ApplicationStackName,ParameterValue=oscon-2017-tutorial-application \
            --template-body file://build-pipeline.yml
    ```
   The output should look something like this:
    ```json
    {
        "StackId": "arn:aws:cloudformation:us-west-2:1234567890:stack/oscon-2017-tutorial-build-pipeline/7972b720-2f5b-11e7-bd3d-503acbd4dcfd"
    }
    ```
    
1. Get the S3 url where you'll upload code during the tutorial. Use your build pipeline stack name if it's different from this:
    ```bash
    $ aws cloudformation describe-stacks \
            --query 'Stacks[?StackName==`oscon-2017-tutorial-build-pipeline`].Outputs[0][?OutputKey==`SourceS3Bucket`].OutputValue' \
            --output text
    ```
    The output should look something like this:
    ```
    s3://oscon-2017-tutorial-build-pipeline-sources3location-hdea5qp6h2o
    ```
    It may be helpful to save this information in an environment variables (we'll use it later):
    ```bash
    export S3_SOURCE=YOUR_S3_BUCKET
    ```
    
## Tutorial

Look at the `README.md` file in the `phase1` directory to get started.

Work through each phase as directed by the instructors.

Because each phase is standalone, don't worry if you're not able to finish or make a mistake. The next phase will have everything you need to keep going along with the rest of the group.

## Teardown

1. Delete the application stack:
    ```bash
    $ aws cloudformation delete-stack --stack-name oscon-2017-tutorial-application
    ```

1. Find the physical resource IDs for the build pipeline's S3 buckets
    ```bash
    $ aws cloudformation list-stack-resources --stack-name oscon-2017-tutorial-build-pipeline \
          --query 'StackResourceSummaries[?ResourceType==`AWS::S3::Bucket`].PhysicalResourceId' \
          --output text
    ```

1. Delete those buckets using the physical resource IDs.
    ```bash
    $ aws s3 rb s3://bucket-physical-resource-id --force
    ```
    If this command fails, you'll need to use the S3 web console (https://console.aws.amazon.com/s3/home)

1. Delete the build pipeline stack:
    ```bash
    $ aws cloudformation delete-stack --stack-name oscon-2017-tutorial-build-pipeline
    ```
