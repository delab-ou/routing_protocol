#!/bin/bash
java -Xmx32g -Xms8g -cp ./bin \
ou.ist.de.protocol.Main \
-protocol:RSA \
-port:10000 \
-frag:100000