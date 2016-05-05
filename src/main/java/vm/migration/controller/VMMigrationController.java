package vm.migration.controller;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        try {
            topo = controllerBroker.getNetWorkTopo();
        }catch (Exception e){
            out.print(e.toString());
        }
        if (topo != null){
            controllerBroker.updateTopoInfo(topo);
            Thread topoRefreshTask = new Thread(new TopoRefresher(topo, controllerBroker));
            topoRefreshTask.start();
        }else{
            out.println("can't get the topo information");
            return;
        }

        List<FlowEntry> flowEntries = new ArrayList<FlowEntry>();
        List<FlowEntry> tempFlowEntries = new ArrayList<FlowEntry>();
        while(true){
            tempFlowEntries = topo.getFlowEntries(srcIp, destIp);
            if (checkFlowEntries(flowEntries, tempFlowEntries)){
                continue;
            }else {
                if (flowEntries.size() != 0)
                    controllerBroker.writeFlowEntries(flowEntries, TYPE.DELETE);
                flowEntries = tempFlowEntries;
                controllerBroker.writeFlowEntries(flowEntries, TYPE.ADD);
            }
            Thread.yield();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//         flowEntries = topo.getFlowEntries(srcIp, destIp);
//        controllerBroker.writeFlowEntries(flowEntries, TYPE.ADD);
    }

    /**
     * check whether two flow entries is the same
     * @return true {same}, false {no the same}
     */
    public static boolean checkFlowEntries(List<FlowEntry> oldFlowEntries, List<FlowEntry> newFlowEntries){
        if (oldFlowEntries.size() != newFlowEntries.size())
            return false;

        for (int i = 0; i < oldFlowEntries.size(); i++){
            if (!oldFlowEntries.get(i).equals(newFlowEntries.get(i)))
                return false;
        }
        return true;
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
