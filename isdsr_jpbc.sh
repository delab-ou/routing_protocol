#!/bin/bash
java -cp ./bin:\
/home/hal/workspace/jpbc/jars/jpbc-api-2.0.0.jar:\
/home/hal/workspace/jpbc/jars/jpbc-pbc-2.0.0.jar:\
/home/hal/workspace/jpbc/jars/jpbc-plaf-2.0.0.jar:\
/home/hal/workspace/jna-5.5.0.jar \
ou.ist.de.protocol.Main \
-protocol:ISDSR_JPBC \
-port:10000 \
-frag:1000