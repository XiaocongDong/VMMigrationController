package vm.migration.controller;

import java.util.*;

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

    public Collection<Switch> getSwitchs(){
        return switchMap.values();
    }

    public List<FlowEntry> getFlowEntries(String srcIp, String destIp){
        List<FlowEntry> flowEntries = new ArrayList<FlowEntry>();
        Set<String> knownSwitches = new HashSet<String>();
        String srcSwitch = hostSwitchMap.get(srcIp);
        String destSwitch = hostSwitchMap.get(destIp);
        knownSwitches.add(srcSwitch);
        String currentSwitch = srcSwitch;
        Map<String, SwitchToSwitch> paths = new HashMap<String, SwitchToSwitch>();
        Map<String, Long> pathMap = new HashMap<String, Long>();
        for (String nodeId : switchMap.keySet()){
            if (!nodeId.equals(srcIp)){
                pathMap.put(nodeId, Long.MAX_VALUE);
            }else {
                pathMap.put(nodeId, (long)0);
            }
        }
        while(!currentSwitch.equals(destSwitch)){
            for (Port port : switchMap.get(currentSwitch).getPortMap().values()){
                String connectedSwitch = port.getConnectedSwitchNodeId();
                if (!knownSwitches.contains(connectedSwitch)){
                    long distance = pathMap.get(currentSwitch) + port.getRate();
                    if (pathMap.get(connectedSwitch) > distance){
                        pathMap.replace(connectedSwitch, distance);
                        if (!paths.containsKey(connectedSwitch))
                            paths.put(connectedSwitch, new SwitchToSwitch(currentSwitch, port.getPortNumber(),
                                    connectedSwitch, srcIp, destIp));
                        else
                            paths.replace(connectedSwitch, new SwitchToSwitch(currentSwitch, port.getPortNumber(),
                                    connectedSwitch, srcIp, destIp));
                    }
                }
            }
            String nextHop = getTheMinimumPathFromMap(pathMap);
            pathMap.remove(nextHop);
            currentSwitch = nextHop;
        }
        currentSwitch = destSwitch;
        while(!currentSwitch.equals(srcSwitch)){
            SwitchToSwitch swithToSwitch = paths.get(currentSwitch);
            flowEntries.add(swithToSwitch.getFlowEntry());
            currentSwitch = swithToSwitch.getLastHopNodeId();
        }
        return flowEntries;
    }

    private String getTheMinimumPathFromMap(Map<String, Long> pathMap){
            String nodeId = "";
            long shortestPath = Long.MAX_VALUE;
            for (Map.Entry<String, Long> pathEntry : pathMap.entrySet()){
                String key = pathEntry.getKey();
                Long distance = pathEntry.getValue();
                if (distance == 0){
                    continue;
                }
                if (distance < shortestPath)
                    nodeId = key;
            }
            return nodeId;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("switch information\n");
        for (Switch sw : switchMap.values()){
            stringBuilder.append(sw.toString());
        }
        return stringBuilder.toString();
    }

    private class SwitchToSwitch{
        String lastHop;
        String nextHop;
        int portNumber;
        String srcIp;
        String destIp;
        int id = 20;
        int tableId = 0;
        int priority = 100;

        public SwitchToSwitch(String lastHop, int portNumber, String nextHop,
                              String srcIp, String destIp){
            this.lastHop = lastHop;
            this.nextHop = nextHop;
            this.portNumber = portNumber;
            this.srcIp = srcIp;
            this.destIp = destIp;
        }

        public String getLastHopNodeId(){
            return lastHop;
        }

        public FlowEntry getFlowEntry(){
            FlowEntry flowEntry = new FlowEntry(id, tableId, priority, lastHop, portNumber,
                    srcIp, destIp);
            return flowEntry;
        }
    }
}
