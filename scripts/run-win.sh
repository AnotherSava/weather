#!/usr/bin/env bash

#
# command line runner for the weather service REST endpoint
# run from project folder
#

function cleanup() {
    kill ${SERVER_PID} ${CLIENT_PID}
#    rm -f cp.txt
}

trap cleanup EXIT

mvn test dependency:build-classpath -Dmdep.outputFile=cp.txt
CLASSPATH=$(cat cp.txt)\;target/classes
java -classpath ${CLASSPATH} com.crossover.trial.weather.WeatherServer &
SERVER_PID=$$

echo "$(date) - 3 waiting for server at localhost:9090..."
sleep 2
echo "$(date) - 2 waiting for server at localhost:9090..."
sleep 2
echo "$(date) - 1 waiting for server at localhost:9090..."
sleep 2

java -classpath ${CLASSPATH} com.crossover.trial.weather.WeatherClient
CLIENT_PID=$$
cleanup

echo FINISHED

while ! nc localhost 9090 > /dev/null 2>&1 < /dev/null; do
    sleep 1
done
