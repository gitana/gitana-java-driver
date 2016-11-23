#!/bin/bash

# build everything to a single jar file
mvn clean install

# run the sample using jdk 1.6
java -classpath target/sample-1.0.0.jar org.sample.Sample
