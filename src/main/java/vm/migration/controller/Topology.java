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

    public Topology(){
        this.hostSwitchMap = new HashMap<String, String>();
        this.switchMap = new HashMap<String, Switch>();
    }

    public void addHostSwitchPair(String hostIp, String connectedSwitchNodeId){
        this.hostSwitchMap.put(hostIp, connectedSwitchNodeId);
    }

    public void addSwitch(Switch sw){
        this.switchMap.put(sw.getNodeId(), sw);
    }

    public void addLink(String srcNodeId, String portNumber, String destNodeId){
        Switch srcSwitch = this.switchMap.get(srcNodeId);
        Port connectedPort = new Port(portNumber);
        connectedPort.setConnectedSwitch(destNodeId, switchMap.get(destNodeId));
        srcSwitch.addPort(portNumber, connectedPort);
    }

    public List<FlowEntry> getFlowEntries(String srcIp, String destIp){
        List<FlowEntry> flowEntries = new ArrayList<FlowEntry>();

        return flowEntries;
    }
}
