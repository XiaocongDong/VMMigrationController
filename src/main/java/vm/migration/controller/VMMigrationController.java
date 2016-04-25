package vm.migration.controller;

import java.util.List;

import static java.lang.System.out;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class VMMigrationController {
    public static void main(String[] args){
        String ip = "192.168.191.147";
        ControllerBroker controllerBroker = new ControllerBroker(ip);
        try {
            Topology topo = controllerBroker.getNetWorkTopo();
        }catch (Exception e){
            out.print(e.toString());
        }
        FlowEntry flowEntry = new FlowEntry(10, 0, 100, "openflow:1", 1, "10.0.0.2/32", "10.0.0.1/32");
        controllerBroker.writeFlowEntry(flowEntry, TYPE.ADD);
    }

}
