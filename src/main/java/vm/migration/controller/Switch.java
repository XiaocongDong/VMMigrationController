package vm.migration.controller;

import java.util.*;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class Switch {
    private String nodeId;
    private Map<String, Port> portMap;
    private List<String> hosts;

    public Switch(String nodeId){
        this.nodeId = nodeId;
        this.portMap = new HashMap<String, Port>();
        this.hosts = new ArrayList<String>();
    }

    public void addPort(String portNumber, Port port){
        this.portMap.put(portNumber, port);
    }

    public void addPorts(Collection<Port> ports){
        for (Port port : ports){
            portMap.put(Integer.toString(port.getPortNumber()), port);
        }
    }


    public void addHost(String hostIp){
        this.hosts.add(hostIp);
    }

    public Port getPortByPortNumber(String portNumber){
        return this.portMap.get(portNumber);
    }

    public Map<String, Port> getPortMap(){
        return portMap;
    }


    public String getNodeId() {
        return nodeId;
    }

    public String toString(){
        StringBuilder switchInfo = new StringBuilder();
        switchInfo.append("NodeId " + nodeId + "\n");
        for(Port port : portMap.values()){
            switchInfo.append("\t" + port + "\n");
        }
        return switchInfo.toString();
    }
}
