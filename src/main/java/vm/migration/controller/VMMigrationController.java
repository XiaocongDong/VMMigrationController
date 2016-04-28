package vm.migration.controller;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class VMMigrationController {
    public static void main(String[] args){
        String ip = "192.168.191.149";
        String srcIp = "10.0.0.1";
        String destIp = "10.0.0.5";
        ControllerBroker controllerBroker = new ControllerBroker(ip);
        Topology topo = null;
//        try {
//            topo = controllerBroker.getNetWorkTopo();
//        }catch (Exception e){
//            out.print(e.toString());
//        }
//        FlowEntry flowEntry = new FlowEntry(10, 0, 100, "openflow:1", 1, "10.0.0.2/32", "10.0.0.1/32");
//        controllerBroker.writeFlowEntry(flowEntry, TYPE.DELETE);
//        if (topo != null){
//            controllerBroker.updateTopoInfo(topo);
////            Thread topoRefreshTask = new Thread(new TopoRefresher(topo, controllerBroker));
////            topoRefreshTask.run();
//        }else{
//            out.println("can't get the topo information");
//        }
//
//        List<FlowEntry> flowEntries = new ArrayList<FlowEntry>();
//        while(true){
//            if (flowEntries.size() != 0)
//                controllerBroker.writeFlowEntries(flowEntries, TYPE.DELETE);
//            flowEntries = topo.getFlowEntries(srcIp, destIp);
//            controllerBroker.writeFlowEntries(flowEntries, TYPE.ADD);
//        }
    }

    static private class TopoRefresher implements Runnable{
        Topology topo;
        ControllerBroker controllerBroker;

        public TopoRefresher(Topology topo, ControllerBroker controllerBroker){
            this.topo = topo;
            this.controllerBroker = controllerBroker;
        }

        public void run() {
            while (true) {
                controllerBroker.updateTopoInfo(topo);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
