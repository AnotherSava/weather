#!/usr/bin/env bash

#
# command line runner for the weather service REST endpoint
# run from project folder
#

function cleanup() {
    kill ${CLIENT_PID}
    rm -f cp.txt
}

trap cleanup EXIT

mvn compile dependency:build-classpath -Dmdep.outputFile=cp.txt
CLASSPATH=$(cat cp.txt)\;target/classes
java -classpath ${CLASSPATH} com.crossover.trial.weather.WeatherClientPerformanceProfiler
CLIENT_PID=$$
cleanup

echo CLIENT FINISHED

while ! nc localhost 9090 > /dev/null 2>&1 < /dev/null; do
    sleep 1
done
