Problem Statement:
<https://github.com/Kong/konnect-team-interview-ingest-exercise>

Javadoc:
<https://banerjeerajarshi.github.io/konnect-team-interview-ingest-exercise/javadoc/index.html>

Assumptions:
1. You have kafka running on localhost:9092
2. You have created a kafka topic named cdc-events
3. You have opensearch running on localhost:9200
4. You have created an index on opensearch named cdc-events

Core Capabilities:
1. Kafka producer which can efficiently read a large file and stream its contents to a topic.
2. Multithreaded kafka consumer which can rapidly pull the data from kafka and persist it to opensearch.
3. Number of threads = number of cores for maximum efficiency. This information is picked up at runtime from the system.


Code features:
1. Fully modularized and decoupled. Producer and Consumer are independent modules which can be separated into their own codebases if needed and ran on their own.
2. Producer and Consumer implement an interface, which means that we can have producers and consumers of all kinds.
In this case we have implemented a kafka producer and consumer to implement the producer and consumer interface.
3. Unit tests for critical features are provided. Build fails if tests fail. 
4. Maven profiles enabled so that you can run it with dev or prod properties as needed. No need to make code changes in this case.

FAQ:

1. How to build the producer jar independently?
```
mvn clean package -Pdev -pl producer
```

2. How to build the consumer jar independently?
```
mvn clean package -Pdev -pl consumer
```


3. How to run the producer jar independently (change Denv environment value as per environment)?
```
java -Denv=dev -jar producer/target/producer-1.0-SNAPSHOT.jar
```

4. How to run the consumer jar independently?

```
java -Denv=dev -jar consumer/target/consumer-1.0-SNAPSHOT.jar
```


5. Where can I edit producer related config values?
```
 producer/src/main/resources/application-dev.properties
```


6. Where can I edit consumer related config values?
```
consumer/src/main/resources/application-dev.properties
```
7. Why is this system not dockerized?
```
Because the producer and consumer should ideally be in their own repos.
In the real world these two components have nothing to do with one another. 
Hence packing them as docker containers and then having a docker compose at the
project root to start both of these containers introduces a degree of tight coupling
in the independent build and deploy approach. Which is why even the root pom is kept simple
at the cost of writing duplicate dependencies in the nested poms, which ensures that each module can
be built into its own independent jar
```
 
