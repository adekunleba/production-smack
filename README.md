# production-smack
A production grade SMACK application for ingesting and processing big data

## Description
The project makes use of the Spark, Mesos, Akka, Cassandra and Kafka stack to process Big data from tweets, iot, timeseries and a typical recommender system
engine.

Thus, there are multiple data flows that are continously coming in and being processed together.

AIM - Is to learn as much about building such stacks, integrating diverse algorithms for data processing and learn the scala functional paradigm on a production like
project.

### TODO
Create a general operation trait for the various servers.

//We are using Type class to handle this.
Operations include:

process
getData
extractData
checkbackpressure
persist
persistLater
