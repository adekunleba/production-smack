# production-smack
A production grade SMACK application for ingesting and processing big data


## Description
The project makes use of the Spark, Mesos, Akka, Cassandra and Kafka stack to process Big data from various sources

Thus, there are multiple data flows that are continously coming in and being processed together.

Furthermore, the project is divided into several modules:
- Denegee - A data ingestion module
- ditto-sc - iot digital twin engine based on Eclipse ditto


AIM - Is to learn as much about building such stacks, integrating diverse algorithms for data processing and learn the scala functional paradigm on a production like
project.


Operations can include:
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


### Some Applications
- Real-time calculation
- Time window analysis
- Application log analysis
- Project risk management
- Intelligent prediction
- Real-time recommendation



#### LESSON LEARNT
To run a test on a particular project in a multi module sbt project it is importatant
to switch to the module in your interactive sbt shell with `project projectname`
Then you can run your test with `test:testOnly *TestSuit` and for continous
testing with code changes add `~test:testOnly *TestSuit`

