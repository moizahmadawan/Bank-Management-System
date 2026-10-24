@echo off
REM Compiles and runs the Bank Management System on Windows
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)
java -cp out bank.Main
