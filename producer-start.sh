#!/bin/bash

exec java $JAVA_OPTS -cp "./producer/target/lib/*:./" com.fractal.test.producer.ProducerApp > producer.log 2>&1 &
