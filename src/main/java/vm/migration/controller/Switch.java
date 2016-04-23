package vm.migration.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class Switch {
    private String nodeId;
    private Map<String, Port> portMap;
    private Map<String, Host> hostMap;

    public Switch(String nodeId){
        this.nodeId = nodeId;
        this.portMap = new HashMap<String, Port>();
        this.hostMap = new HashMap<String, Host>();
    }

    public void addPort(String portNumber, Port port){
        this.portMap.put(portNumber, port);
    }

    public void addHost(String src, Host host){
        this.hostMap.put(src, host);
    }

    public Map<String, Port> getPortMap(){
        return portMap;
    }

    public Map<String, Host> getHostMap(){
        return hostMap;
    }

    public String getNodeId() {
        return nodeId;
    }
}
