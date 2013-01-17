package rtkaczyk.eris.api;

import rtkaczyk.eris.api.Packet;

interface IErisApi {
    List<Packet> getAllPackets();
    //List<Packet> getLastPackets(int n);
    //List<Packet> getLastPackets(String node, int n);
    
    void xmlConfigure(String xml);
    String getXmlConfig();
}