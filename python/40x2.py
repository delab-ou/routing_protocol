#!/usr/bin/python

"""This example shows how to work in adhoc mode

sta1 <---> sta2 <---> sta3"""
import sys

from mininet.net import Mininet
from mininet.cli import CLI
from mininet.log import setLogLevel


def topology(autoTxPower):
    "Create a network."
    net = Mininet(enable_wmediumd=True, enable_interference=True)

    stas = []
    stanum = 80

    print "*** Creating nodes"
    for i in range(stanum):
        stas.append(net.addStation('sta' + str(i+1), position=str(i%40*80)+','+str(i/40*80)+',0', range=100))

    net.propagationModel(model="logDistance", exp=4.5)

    print "*** Configuring wifi nodes"
    net.configureWifiNodes()

    print "*** Creating links"
    for i in range(stanum):
        net.addHoc(stas[i], ssid='adhocNet', mode='g', channel=5)

    net.plotGraph(max_x=4000, max_y=1000)

    if autoTxPower:
        for i in range(stanum-2):
            stas[i+1].cmd('xterm -e java -cp ./bin:../ibsas/bin:../jpbc-2.0.0/jars/jpbc-api-2.0.0.jar:../jpbc-2.0.0/jars/jpbc-plaf-2.0.0.jar ou.ist.de.srp.Main rsa:1024:f &')

    else:
        for i in range(stanum-2):
            stas[i+1].cmd('xterm -e java -cp ./bin:../ibsas/bin:../jpbc-2.0.0/jars/jpbc-api-2.0.0.jar:../jpbc-2.0.0/jars/jpbc-plaf-2.0.0.jar ou.ist.de.srp.Main ibsas:a:f &')

    print "*** Starting network"
    net.build()

    print "*** Running CLI"
    CLI(net)

    print "*** Run protocol"

    print "*** Stopping network"
    net.stop()


if __name__ == '__main__':
    setLogLevel('info')
    autoTxPower = True if '-a' in sys.argv else False
    topology(autoTxPower)

    #sta2.cmd('java -cp ./bin:../ibsas/bin:../jpbc-2.0.0/jars/jpbc-api-2.0.0.jar:../jpbc-2.0.0/jars/jpbc-plaf-2.0.0.jar ou.ist.de.srp.Main ibsas:a:f')
