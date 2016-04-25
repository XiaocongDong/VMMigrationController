package vm.migration.controller;

import static java.lang.System.out;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class VMMigrationController {
    public static void main(String[] args){
//        getFlowEntry("http://192.168.191.147:8181/restconf/operational/opendaylight-inventory:nodes/node/openflow:1"
// );
        String ip = "192.168.191.147";
        ControllerBroker controllerBroker = new ControllerBroker(ip);
        try {
            Topology topo = controllerBroker.getNetWorkTopo();
        }catch (Exception e){
            out.print(e.toString());
        }
    }

}
