# Weather event generator

## Usage

Given a Cloudformation stack name, the `event-generator` will automatically discover the correct API base URL.

Sending 50 valid weather events:

    ```bash
    $ java -jar target/event-generator-1.0-SNAPSHOT.jar --stack serverless-weather
    ```

Sending a single invalid weather event:

    ```bash
    $ java -jar target/event-generator-1.0-SNAPSHOT.jar --stack serverless-weather --invalid --limit 1
    ```
