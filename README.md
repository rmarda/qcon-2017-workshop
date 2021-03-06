# Symphonia / QCon 2017 Workshop

## Chat room

Join this public Hipchat room to discuss and share information with your fellow attendees: https://www.hipchat.com/gLOf5k7xF

## Resources

After the tutorial, you can find links and resources here: https://bit.ly/symph-qcon-workshop

## Initial Setup

#### :warning: *Windows users may need edit the example commands. Here are some tips:*

- Double-quotes (`"`) can be escaped with backslashes like this: (`\"`)
- Variables substitution is `%VARIABLE_NAME%`
- Instead of the `cat` command, use the `type` command
- The backslashes used to indicate multi-line commands in the examples will need to be removed.

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

1. Download and unzip the tutorial project:

    ```bash
    $ wget https://github.com/symphoniacloud/qcon-2017-workshop/archive/master.zip
      ...
    $ unzip qcon-2017-workshop-master.zip
      ...
    $ cd qcon-2017-workshop-master
    ```

1. Create the tutorial's build pipeline using Cloudformation. The build pipeline will create the application, also
with Cloudformation. Note that the build pipeline stack name is specified using the `--stack-name` command line flag, while
the application stack name is specified as a parameter to the stack template.

   :warning: Stack names must be unique within an AWS account. If multiple attendees are working in the same account,
   pick unique stack names for both stacks!

    ```bash
    $ aws cloudformation create-stack \
            --capabilities CAPABILITY_IAM \
            --stack-name serverless-weather-build \
            --parameters ParameterKey=ApplicationStackName,ParameterValue=serverless-weather \
            --template-body file://build-pipeline.yml
    ```
   The output should look something like this:
    ```json
    {
        "StackId": "arn:aws:cloudformation:us-west-2:1234567890:stack/serverless-weather-build/7972b720-2f5b-11e7-bd3d-503acbd4dcfd"
    }
    ```

## Tutorial

Look at the `README.md` file in the `phase1` directory to get started.

Work through each phase as directed by the instructors.

Because each phase is standalone, don't worry if you're not able to finish or make a mistake. The next phase will have everything you need to keep going along with the rest of the group.

## Teardown

:warning: These steps must be completed in order, or resources might be stranded.

1. Delete the application stack:
    ```bash
    $ aws cloudformation delete-stack --stack-name serverless-weather
    ```

1. Find the physical resource IDs for the build pipeline's S3 buckets
    ```bash
    $ aws cloudformation list-stack-resources --stack-name serverless-weather-build \
          --query 'StackResourceSummaries[?ResourceType==`AWS::S3::Bucket`].PhysicalResourceId' \
          --output text
    ```

1. Delete those buckets using the the S3 web console (https://console.aws.amazon.com/s3/home)

1. Delete the build pipeline stack:
    ```bash
    $ aws cloudformation delete-stack --stack-name serverless-weather-build
    ```

-----
Copyright 2017, Symphonia LLC
