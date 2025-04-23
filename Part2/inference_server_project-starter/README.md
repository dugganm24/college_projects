# Overview 

This directory includes a simple inference server that serves a pretrained image classification model.
The top 1 result, both the prediction label and the probability, will be returned. 


To start the server, in the command line, type in:

```bash 
# you will need to install the required python packages first
# check out poetry if you aren't familiar with it
python image_classification_server.py
```

To test the functionality of the server, you can use `curl` like this:

```bash
curl -X POST -F "file=@test-images/dog.jpg" http://127.0.0.1:5050/predict
```

If the server setup is successful, you should see the output like this:
Now updated to return top 5 predictions:
```bash 
# output
{"predictions":[{"prediction_label":"Doberman","score":0.8287530541419983},
{"prediction_label":"German short-haired pointer","score":0.002210764680057764},
{"prediction_label":"Rhodesian ridgeback","score":0.0014806906692683697},{
  "prediction_label":"gas pump","score":0.001086001400835812},
  {"prediction_label":"kelpie","score":0.0009684535325504839}]}
```



There is also a simple python client that uses the default camera of the development machine and sends every frame for inference.
Start it in command line with:

```bash 
python client.py
```

