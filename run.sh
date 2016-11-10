#!/usr/bin/env bash

# I'll execute my programs, with the input directory paymo_input and output the files in the directory paymo_output
# python ./src/antifraud.py ./paymo_input/batch_payment.txt ./paymo_input/stream_payment.txt ./paymo_output/output1.txt ./paymo_output/output2.txt ./paymo_output/output3.txt

DIR=$(pwd)

# UNRESOLVED SUN JAVAC BUG: ENUM CLASSES MUST BE COMPILED FIRST, OTHERWISE JAVAC THROWS INCORRECT ERRORS
javac -classpath ./src/Feature.java ./src/*.java

# Unable to find main class and pass in arguments with current directory structure
cd ./src
#java PaymoDriver $DIR/paymo_input/batch_payment_test.csv $DIR/paymo_input/stream_payment_test.csv $DIR/paymo_output/output1.txt $DIR/paymo_output/output2.txt $DIR/paymo_output/output3.txt
#java PaymoDriver $DIR/paymo_input/batch_payment.txt $DIR/paymo_input/stream_payment.txt $DIR/paymo_output/output1.txt $DIR/paymo_output/output2.txt $DIR/paymo_output/output3.txt
java PaymoDriver $DIR/paymo_input/batch_payment.txt $DIR/paymo_input/stream_payment.txt $DIR/paymo_output/output1.txt $DIR/paymo_output/output2.txt $DIR/paymo_output/output3.txt $DIR/paymo_output/output4.txt
cd ..