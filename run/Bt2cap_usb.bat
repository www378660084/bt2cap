@echo off
echo processing %1
java -jar Bt2cap.jar usb %*
echo process done
pause