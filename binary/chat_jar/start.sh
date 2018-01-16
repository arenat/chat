#!/bin/bash
kill $(ps -ef | grep -v grep | grep "/message_history.py" | awk '{print $2}')
cd $(dirname "$0")
nohup python ./message_history.py > /dev/null &
java -cp chat.jar chat.server.ServerChat
