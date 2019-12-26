#!/usr/bin/python

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
    self.prot="";
    self.jars=["jpbc-pbc-2.0.0.jar","jpbc-plaf-2.0.0.jar","jpbc-pbc-2.0.0.jar"];

  def params(self,args):
    for param in args:
      if param.startswith("-w"):
        self.x=int(param.split(":")[1]);
      if param.startswith("-h"):
        self.y=int(param.split(":")[1]);
      if param.startswith("-p"):
        self.prot=param.split(":")[1];

    print("w:"+str(self.x)+" h:"+str(self.y)+" p:"+self.prot);

  def generate(self):
    #net = Mininet_wifi(link=wmediumd, wmediumd_mode=interference,noise_threshold=-91, fading_coefficient=2)
    net = Mininet_wifi(link=wmediumd, wmediumd_mode=interference)
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
        net.addLink(self.stas[i], cls=adhoc, intf='sta'+str(i+1)+'-wlan0',ssid='adhocNet',mode='a', channel=5, ht_cap='HT40+')
        #net.addLink(self.stas[i], cls=adhoc, intf='sta'+str(i+1)+'-wlan0',ssid='adhocNet',mode='g', channel=5)
    net.plotGraph(max_x=800, max_y=800)

    return net;

  def run(self,net):
    
    info("*** Starting network\n")
    net.build()

    self.sendCommand();
    info("*** Running CLI\n")
    cliwifi=CLI_wifi(net)
    info("*** Stopping network\n")
    net.stop()

  def sendCommand(self):
    num=self.x*self.y;
    for i in range(1,num):
      print("index:"+str(i)+ " is set cmd: "+self.prot.lower()+".sh");
      #self.stas[i].sendCmd("xterm -e "+self.cmdf);
      self.stas[i].sendCmd("xterm -hold -e bash "+self.prot.lower()+".sh &");

  def setPromisc(self):
    info("*** Interface configuration ***")
    num=self.x*self.y;
    for i in range(num):
      self.stas[i].cmd('ifconfig sta'+str(i+1)+'-wlan0 promisc');
      self.stas[i].cmd('iwconfig sta'+str(i+1)+'-wlan0 rts off');
      self.stas[i].cmd('iwconfig sta'+str(i+1)+'-wlan0 power off');

  def command_source(self,cmd):
    self.stas[0].cmd('xterm -hold -e '+cmd);

if __name__ == '__main__':
  setLogLevel('info')
  args=sys.argv;

  if len(args) != 4:
    print("usage python location.py -w:width -h:height -p:protocol");
  
  
  topo=TopologyGenerator();
  topo.params(args);
  net=topo.generate();
  topo.setPromisc();
  topo.run(net);
  #topo.command('java -cp ./bin ou.ist.de.Main -protocol:DSR -port:10000 -frag:1000 &');
