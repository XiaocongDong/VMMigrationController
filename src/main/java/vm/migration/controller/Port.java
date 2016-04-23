package vm.migration.controller;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class Port {
    private String portNumber;
    private float rate;
    private Switch connectedSwitch;

    public Port(String portNumber){
        this.portNumber = portNumber;
    }

    public void setConnectedSwitch(String portNumber, Switch connectedSwitch){
        this.connectedSwitch = connectedSwitch;
    }

    public void setRate(String portNumber, float rate){
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }
}
