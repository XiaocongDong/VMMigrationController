package vm.migration.controller;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class Port {
    private String portNumber;
    private long rate;
    private String connectedSwitch;

    public Port(String portNumber){
        this.portNumber = portNumber;
    }

    public Port(String portNumber, long rate, String connectedSwitch){
        this.portNumber = portNumber;
        this.rate = rate;
        this.connectedSwitch = connectedSwitch;
    }

    public void setConnectedSwitch(String connectedSwitchId){
        this.connectedSwitch = connectedSwitchId;
    }

    public void setRate(long rate){
        this.rate = rate;
    }

    public long getRate() {
        return rate;
    }
    public String getConnectedSwitchNodeId(){
        return this.connectedSwitch;
    }

    public String toString(){
        return "portNumber " + portNumber + "\t" +
                "rate" + rate + "\t" +
                "Switch " + connectedSwitch;
    }

    public int getPortNumber(){
        return Integer.parseInt(portNumber);
    }
}
