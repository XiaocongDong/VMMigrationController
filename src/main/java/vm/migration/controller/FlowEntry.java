package vm.migration.controller;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class FlowEntry {
    private int id;
    private int tableId;
    private int priority;
    private Switch sw;
    private Port port;
    private String srcIp;
    private String destIp;

    public FlowEntry(int id, int tableId, int priority, Switch sw,
                     Port port, String srcIp, String destIp){
        this.id = id;
        this.tableId = tableId;
        this.priority = priority;
        this.sw = sw;
        this.port = port;
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

    public Switch getSw() {
        return sw;
    }

    public Port getPort() {
        return port;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public String getDestIp() {
        return destIp;
    }
}
