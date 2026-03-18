@echo off
echo Compiling ATM Simulator...
javac -d bin src\bank\*.java

if %ERRORLEVEL% equ 0 (
    echo Compilation successful! Starting ATM...
    java -cp bin bank.AtmApp
) else (
    echo Compilation failed.
    pause
)
