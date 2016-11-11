#!/usr/bin/env bash

# Save current directory
DIR=$(pwd)

# UNRESOLVED SUN JAVAC BUG: ENUM CLASSES MUST BE COMPILED FIRST, OTHERWISE JAVAC THROWS INCORRECT ERRORS
javac -classpath ./src/Feature.java ./src/*.java

# Unable to find main class and pass in arguments with current directory structure
cd ./src
java PaymoDriver $DIR/paymo_input/batch_payment.csv $DIR/paymo_input/stream_payment.csv $DIR/paymo_output/output1.txt $DIR/paymo_output/output2.txt $DIR/paymo_output/output3.txt $DIR/paymo_output/output4.txt