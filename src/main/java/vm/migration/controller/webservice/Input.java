package vm.migration.controller.webservice;

/**
 * Created by sunny on 16-5-7.
 */
public class Input {
    String srcIp;
    String destIp;
    String controllerIp;

    public void setSrcIp(String srcIp){
        this.srcIp = srcIp;
    }

    public void setDestIp(String destIp){
        this.destIp = destIp;
    }

    public void setControllerIp(String controllerIp){
        this.controllerIp = controllerIp;
    }

    public String getSrcIp(){
        return this.srcIp;
    }

    public String getDestIp(){
        return this.destIp;
    }

    public String getControllerIp(){
        return this.controllerIp;
    }

    public String toString(){
        return "The flow from " + srcIp + " to " + destIp;
    }
}
