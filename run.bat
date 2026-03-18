@echo off
echo Compiling Banking Application...
javac -d bin src\bank\*.java

if %ERRORLEVEL% equ 0 (
    echo Compilation successful! Running application...
    java -cp bin bank.BankApp
) else (
    echo Compilation failed.
    pause
)
