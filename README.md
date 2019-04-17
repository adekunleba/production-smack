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


Style guide for the project at [here](http://twitter.github.io/effectivescala/)
and [this](https://docs.scala-lang.org/style/) - main scala style guide

Make reference to collection document during the project, it can be found
[here](https://www.scala-lang.org/docu/files/collections-api/collections.html)


Refer [here](http://www.lihaoyi.com/post/BenchmarkingScalaCollections.html) and
[here](http://www.scala-lang.org/docu/files/collections-api/collections_40.html)
for scala performance evaluation
