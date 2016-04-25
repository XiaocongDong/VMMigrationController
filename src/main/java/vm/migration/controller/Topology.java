package vm.migration.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class Topology {
    private Map<String, String> hostSwitchMap;
    private Map<String, Switch> switchMap;
    private Map<String, Host> hostMap;

    public Topology(){
        this.hostSwitchMap = new HashMap<String, String>();
        this.switchMap = new HashMap<String, Switch>();
        this.hostMap = new HashMap<String, Host>();
    }

    public void addHostSwitchPair(String hostIp, String connectedSwitchNodeId){
        this.hostSwitchMap.put(hostIp, connectedSwitchNodeId);
    }

    public void addSwitch(Switch sw){
        this.switchMap.put(sw.getNodeId(), sw);
    }

//    public void addLink(String srcNodeId, String portNumber, String destNodeId){
//        Switch srcSwitch = this.switchMap.get(srcNodeId);
//        Port connectedPort = new Port(portNumber);
//        connectedPort.setConnectedSwitch(destNodeId);
//        srcSwitch.addPort(portNumber, connectedPort);
//    }

    public void addHost(String nodeId, Host host){
        this.hostMap.put(nodeId, host);
    }

    public String getHostIpByNodeId(String nodeId){
        if (hostMap.containsKey(nodeId)){
            return hostMap.get(nodeId).getIp();
        }
        return "";
    }

    public Switch getSwitchByNodeId(String nodeId){
        return switchMap.get(nodeId);
    }

    public List<FlowEntry> getFlowEntries(String srcIp, String destIp){
        List<FlowEntry> flowEntries = new ArrayList<FlowEntry>();

        return flowEntries;
    }

//    public String toString(){
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("host map : " + hostMap.toString() + "\n");
//        stringBuilder.append("switch information\n");
//        for (Switch sw : switchMap.values()){
//            stringBuilder.append(sw.toString());
//        }
//        return stringBuilder.toString();
//    }
}
