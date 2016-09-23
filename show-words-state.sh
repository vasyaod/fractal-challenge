#!/bin/bash

while :
do
    echo "Press [CTRL+C] to stop.."
    tail -n +1 words/*
    sleep 10
done