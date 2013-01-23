package rtkaczyk.eris.api;

import rtkaczyk.eris.api.Packet;

interface IErisApi {
    
    void xmlConfigure(String xml);
    String getXmlConfig();
    
    int selectPackets(long from, long to, String device, int limit);
    List<Packet> getQuery(int queryId);
    void countPackets();
    void clearStorage();
    void clearCache();
    
    boolean isDiscovering();
    boolean startDiscovery();
    void cancelDiscovery();
    
    boolean isReceiving();
    boolean receivePackets(String device, long from, long to, int limit);
    void cancelReceiving();
    
    boolean isForwarding();
    boolean forwardPackets();
    void cancelForwarding();
}