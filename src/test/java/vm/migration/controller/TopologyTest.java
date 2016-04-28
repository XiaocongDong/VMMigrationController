package vm.migration.controller;

import com.google.common.collect.Lists;

import java.util.List;

import static java.lang.System.out;

/**
 * Created by sunny on 16-4-27.
 */
public class TopologyTest {
    public static void main(String[] args){
        Topology topo = new Topology();
        topo.addHostSwitchPair("10.0.0.1", "1");
        topo.addHostSwitchPair("10.0.0.2", "2");
        topo.addHostSwitchPair("10.0.0.3", "3");
        topo.addHostSwitchPair("10.0.0.4", "4");
        topo.addHostSwitchPair("10.0.0.5", "5");

        Switch sw1 = new Switch("1");
        List<Port> ports = Lists.newArrayList(new Port("1", 200, "2"),new Port("2", 30, "3"),
                new Port("3", 30, "4"), new Port("4", 40, "5"));
        sw1.addPorts(ports);

        Switch sw2 = new Switch("2");
        ports = Lists.newArrayList(new Port("1", 200, "1"),new Port("2", 50, "4"));
        sw2.addPorts(ports);

        Switch sw3 = new Switch("3");
        ports = Lists.newArrayList(new Port("1", 30, "1"),new Port("2", 20, "5"));
        sw3.addPorts(ports);

        Switch sw4 = new Switch("4");
        ports = Lists.newArrayList(new Port("1", 50, "2"),new Port("2", 100, "5"),
                new Port("3", 30, "1"));
        sw4.addPorts(ports);

        Switch sw5 = new Switch("5");
        ports = Lists.newArrayList(new Port("1", 100, "4"),new Port("2", 20, "3"),
                new Port("3", 40, "1"));
        sw5.addPorts(ports);

        topo.addSwitchs(sw1, sw2, sw3, sw4, sw5);

        List<FlowEntry> flowEntries = topo.getFlowEntries("10.0.0.3", "10.0.0.4");
        for (FlowEntry flowEntry : flowEntries){
            out.println(flowEntry);
        }
    }
}
