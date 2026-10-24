#!/bin/sh
# Compiles and runs the Bank Management System on Linux / macOS
mkdir -p out
javac -d out $(find src -name "*.java") || exit 1
java -cp out bank.Main
