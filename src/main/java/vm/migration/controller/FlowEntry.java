package vm.migration.controller;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class FlowEntry {
    private int id;
    private int tableId;
    private int priority;
    private String nodeId;
    private int portNumber;
    private String srcIp;
    private String destIp;

    public FlowEntry(int id, int tableId, int priority, String nodeId,
                     int portNumber, String srcIp, String destIp){
        this.id = id;
        this.tableId = tableId;
        this.priority = priority;
        this.nodeId = nodeId;
        this.portNumber = portNumber;
        this.srcIp = srcIp;
        this.destIp = destIp;
    }

    public int getId() {
        return id;
    }

    public int getTableId() {
        return tableId;
    }

    public int getPriority() {
        return priority;
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getPort() {
        return portNumber;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public String getDestIp() {
        return destIp;
    }

    public String toString(){
        return "nodeId " + nodeId +"\n" +
                "portNumber" + portNumber;
    }

    public boolean equals(FlowEntry flowEntry){
        if (this.nodeId.equals(flowEntry.getNodeId())){
            if (this.portNumber == flowEntry.getPort()){
                if (this.srcIp.equals(flowEntry.getSrcIp())){
                    if (this.destIp.equals(flowEntry.getDestIp())){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
