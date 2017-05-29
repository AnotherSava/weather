#!/usr/bin/env bash

#
# command line runner for the weather service REST endpoint
# run from project folder
#

function cleanup() {
    kill ${SERVER_PID}
#    rm -f cp.txt
}

trap cleanup EXIT

mvn compile dependency:build-classpath -Dmdep.outputFile=cp.txt
CLASSPATH=$(cat cp.txt)\;target/classes
java -classpath ${CLASSPATH} com.crossover.trial.weather.WeatherServer &
SERVER_PID=$$

while true; do sleep 1; done