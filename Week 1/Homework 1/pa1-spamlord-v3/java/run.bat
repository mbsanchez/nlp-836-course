@echo off
javac -d classes -target 1.6 SpamLord.java
java -cp "classes" SpamLord ../data/dev ../data/devGOLD
pause
@echo on