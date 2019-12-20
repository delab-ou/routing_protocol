#!/usr/bin/python

"""This example shows how to work in adhoc mode

sta1 <---> sta2 <---> sta3"""

import sys

from mininet.log import setLogLevel, info
from mn_wifi.link import wmediumd, adhoc
from mn_wifi.cli import CLI_wifi
from mn_wifi.net import Mininet_wifi
from mn_wifi.wmediumdConnector import interference


class TopologyGenerator:
  def __init__(self,width=3,height=3):
    self.x=width;
    self.y=height;
  def generate(self):
    net = Mininet_wifi(link=wmediumd, wmediumd_mode=interference,noise_threshold=-91, fading_coefficient=3)
    num=self.x*self.y;
    
    info("*** Creating nodes\n")
    self.stas = []

    print("*** Creating nodes")
    for i in range(num):
        self.stas.append(net.addStation('sta' + str(i+1), position=str(i%self.x*80)+','+str(i/self.x*80)+',0', range=100))

    info("*** Configuring Propagation Model\n")
    net.setPropagationModel(model="logDistance", exp=4)

    print("*** Configuring wifi nodes")
    net.configureWifiNodes()

    print("*** Creating links")
    for i in range(num):
        net.addLink(self.stas[i], cls=adhoc, intf='sta'+str(i+1)+'-wlan0',
                    ssid='adhocNet',
                    mode='g', channel=5, ht_cap='HT40+')
    net.plotGraph(max_x=800, max_y=800)

    return net;

  def run(self,net):
    
    info("*** Starting network\n")
    net.build()

    #self.command('xterm -e java -cp ./bin ou.ist.de.protocol.Main -protocol:DSR -port:10000 -frag:1000 &');
    info("*** Running CLI\n")
    CLI_wifi(net)

    info("*** Stopping network\n")
    net.stop()

  def command(self, cmd):
    num=self.x*self.y;
    for i in range(1,num):
      print("index:"+str(i)+ " is set cmd:"+cmd);
      self.stas[i].sendCmd(cmd);

  def setPromisc(self):
    num=self.x*self.y;
    for i in range(num):
      self.stas[i].cmd('ifconfig sta'+str(i+1)+'-wlan0 promisc');

  def command_source(self,cmd):
    self.stas[0].cmd('xterm -hold -e '+cmd);

if __name__ == '__main__':
  setLogLevel('info')
  topo=TopologyGenerator(3,2);
  net=topo.generate();
  topo.setPromisc();
  topo.run(net);
  #topo.command('java -cp ./bin ou.ist.de.Main -protocol:DSR -port:10000 -frag:1000 &');
