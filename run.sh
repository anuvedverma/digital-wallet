#!/usr/bin/env bash

# I'll execute my programs, with the input directory paymo_input and output the files in the directory paymo_output
# python ./src/antifraud.py ./paymo_input/batch_payment.txt ./paymo_input/stream_payment.txt ./paymo_output/output1.txt ./paymo_output/output2.txt ./paymo_output/output3.txt

# SUN JAVAC BUG: enum class Feature must be compiled first, otherwise we get errors
javac -classpath ./src/Feature.java ./src/*.java

# Unable to find main class and pass in arguments with current directory structure
java PaymoDriver ./paymo_input/batch_payment.csv ./paymo_input/stream_payment.csv ./paymo_output/output1.txt ./paymo_output/output2.txt ./paymo_output/output3.txt