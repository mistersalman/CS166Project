#! /bin/bash
rm -rf ./bin
mkdir bin
javac -cp ".;lib/postgresql-42.1.4.jar;" src/AirBooking.java -d bin/
