#!/bin/bash
java -cp ./bin:/home/hal/workspace/jpbc/jars/jpbc-api.jar:/home/hal/workspace/jpbc/jars/jpbc-pbc.jar:/home/hal/workspace/jpbc/jars/jpbc-plaf.jar:/home/hal/workspace/jpbc/jars/jna-3.2.5.jar \
ou.ist.de.protocol.Main \
-protocol:ISDSR \
-port:10000 \
-frag:1000