#!/bin/bash
java -Xmx32g -Xms8g -cp ./bin \
ou.ist.de.protocol.Main \
-protocol:RSAIndividual \
-port:10000 \
-frag:1000
