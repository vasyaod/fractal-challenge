#!/bin/bash

mkdir words

JAVA_OPTS="$JAVA_OPTS -Xmx1g"

exec java $JAVA_OPTS -cp "./consumer/target/lib/*:./" com.fractal.test.consumer.ConsumerApp > consumer.log 2>&1 &
