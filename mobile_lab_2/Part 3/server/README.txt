This is a simple flask server that we provide for your convenience. 
Again, feel free to design and write your own server logic to process the accelerometer data to return the detected step counts. 


To use the /data endpoint, your mobile app needs to send the sensor data in the following JSON format:

[
    {
        "timestamp": 1711132800000,  
        "x": 0.123,  
        "y": -0.456,  
        "z": 0.789  
    },
    {
        "timestamp": 1711132801000,  
        "x": -0.321,  
        "y": 0.654,  
        "z": -0.987  
    }
]

The timestamps should be in milliseconds (Unix time format).



The server will return the result in the following JSON format: 

{ "status": "success", "results": 7 }


--

If you are not familiar with JSON, here are some key concepts
1. A JSON object is enclosed in curly braces {} and consists of key-value pairs.
2. A JSON array is an ordered collection of values, enclosed in square brackets [].
3. JSON allows objects and arrays to be nested inside each other.
4. JSON is commonly used in web APIs to exchange data between a server and a client (e.g., mobile apps). 


