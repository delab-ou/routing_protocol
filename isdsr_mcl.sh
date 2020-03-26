#!/bin/bash
export LD_LIBRARY_PATH=~/lib:$LD_LIBRARY_PATH
java -cp ./bin:./mcl.jar \
ou.ist.de.protocol.Main \
-protocol:ISDSR_MCL \
-port:10000 \
-frag:1000