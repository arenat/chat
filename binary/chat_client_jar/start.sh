#!/bin/bash
cd $(dirname "$0")
java -cp chat-client.jar chat.client.ClientChat
