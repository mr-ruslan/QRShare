# QR share


## Requirements
There must be a server, that can accept websocket connections and POST data load requests.
There also must be a web page that uses websocket connection part only and shows the QR code.

## Functionality
The application can scan the QR code (it is possible to get deeplink in other ways and open it automatically) and send any text data to the related session device.
The data can also be gotten from other apps with intents.

The application can start a session and show the QR code. When some data is sent, it is displayed on the screen and can be copied.

There is a local database history of previous sharings and the data can be used properly.
