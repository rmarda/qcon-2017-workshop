# Phase 6

## Goals

1. Configure CORS headers in the Lambda / API Gateway response
1. Send an HTTP GET request to API Gateway from the browser
1. Bask in the glory of a fully Serverless application

## Steps

1. Replace the API URL in the map.js file with the correct URL for your API.

    ```bash
    $ aws cloudformation list-exports --region us-west-2 --query 'Exports[?Name==`WeatherApiUrl`].Value' --output text
    ```

1. Open `map.html` in your browser. You should see 50 points on the map, each labeled with a temperature and a color based on that temperature.
