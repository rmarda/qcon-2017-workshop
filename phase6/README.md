# Phase 6

## Goals

1. Configure an API Gateway GET resource using SAM
1. Configure CORS headers in the Lambda / API Gateway response
1. Send an HTTP GET request to API Gateway from the browser
1. Bask in the glory of a fully Serverless application

## Tips

Retrieving the base URL of the API:

    ```bash
    $ aws cloudformation list-exports --region us-west-2 --query 'Exports[?Name==`WeatherApiUrl`].Value' --output text
    ```
