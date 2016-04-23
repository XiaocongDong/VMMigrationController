package vm.migration.controller;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class Host {
    private String nodeId;
    private String mac;
    private String ip;

    public Host(String nodeId, String mac, String ip){
        this.nodeId = nodeId;
        this.mac = mac;
        this.ip = ip;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }
}
