package vm.migration.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.console;
import static java.lang.System.out;
/**
 * Created by xiaocdon on 2016/4/23.
 */
public class ControllerBroker {
    String ip;

    public ControllerBroker(String ip){
        this.ip = ip;
    }

    public boolean writeFlowEntries(List<FlowEntry> flowEntries, TYPE type){
        for (FlowEntry flowEntry : flowEntries){
            if (!writeFlowEntry(flowEntry, type))
                return false;
        }
        return true;
    }

    public boolean writeFlowEntry(FlowEntry flowEntry, TYPE type){
        String nodeId = flowEntry.getNodeId();
        String srcIp = flowEntry.getSrcIp();
        String destIp = flowEntry.getDestIp();
        int port = flowEntry.getPort();
        int tableId = flowEntry.getTableId();
        int priority = flowEntry.getPriority();
        String body = String.format(
                "<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n" +
                        "<input xmlns='urn:opendaylight:flow:service'>\n" +
                        "   <barrier>false</barrier>\n" +
                        "   <node xmlns:inv='urn:opendaylight:inventory'>/inv:nodes/inv:node[inv:id='%s']</node>\n" +
                        "   <cookie>500</cookie>\n" +
                        "   <hard-timeout>0</hard-timeout>\n" +
                        "   <idle-timeout>0</idle-timeout>\n" +
                        "   <installHw>false</installHw>\n" +
                        "   <match>\n" +
                        "    <ethernet-match>\n" +
                        "     <ethernet-type>\n" +
                        "       <type>2048</type>\n" +
                        "     </ethernet-type>\n" +
                        "    </ethernet-match>\n" +
                        "    <ipv4-source>%s/32</ipv4-source>\n" +
                        "    <ipv4-destination>%s/32</ipv4-destination>\n" +
                        "   </match>\n" +
                        "   <instructions>\n" +
                        "    <instruction>\n" +
                        "     <order>0</order>\n" +
                        "     <apply-actions>\n" +
                        "       <action>\n" +
                        "        <order>0</order>\n" +
                        "        <output-action>\n" +
                        "          <output-node-connector>%d</output-node-connector>\n" +
                        "          <max-length>60</max-length>\n" +
                        "        </output-action>\n" +
                        "       </action>\n" +
                        "     </apply-actions>\n" +
                        "    </instruction>\n" +
                        "   </instructions>\n" +
                        "   <priority>%d</priority>\n" +
                        "   <strict>false</strict>\n" +
                        "   <table_id>%d</table_id>\n" +
                        "</input>", nodeId, srcIp, destIp, port, priority, tableId);
        String url;
        if (type == TYPE.ADD){
            url = String.format("http://%s:8181/restconf/operations/sal-flow:add-flow", ip);
        }else {
            url = String.format("http://%s:8181/restconf/operations/sal-flow:remove-flow", ip);
        }
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost(url);
        out.println(type + " " + body);
        postRequest.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        postRequest.setHeader("Accept", "application/json");

        try{
            StringEntity input = new StringEntity(body);
            input.setContentType("application/xml");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() == 200){
                out.println("succeed");
                return true;
            }else{

                return false;
            }
        }catch (IOException e){
            out.println("exception");
            return false;
        }
    }


    public Topology getNetWorkTopo() throws Exception{
        Topology topo = new Topology();
        String url = String.format("http://%s:8181/restconf/operational/network-topology:network-topology/", this.ip);

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        getRequest.setHeader("Accept", "application/json");

        try{
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() == 200){
                InputStream inputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder strBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null){
                    strBuilder.append(line);
                }

                String responseStr = strBuilder.toString();

                JSONObject responseJson = new JSONObject(responseStr);
                JSONObject topologyObj = responseJson.getJSONObject("network-topology");
                JSONArray topoArr = topologyObj.getJSONArray("topology");
                JSONArray nodeArr = topoArr.getJSONObject(0).getJSONArray("node");
                //get all the nodes information
                for (int i = 0; i < nodeArr.length(); i++){
                    JSONObject nodeObj = nodeArr.getJSONObject(i);
                    String nodeId = nodeObj.getString("node-id");

                    if (nodeId.contains("host")){
                        JSONObject hostObj = nodeObj.getJSONArray("host-tracker-service:addresses").getJSONObject(0);
                        String mac = hostObj.getString("mac");
                        String ip = hostObj.getString("ip");
                        Host host = new Host(nodeId, mac, ip);
                        topo.addHost(nodeId, host);
                    }else {
                        Switch sw = new Switch(nodeId);
                        JSONArray portArr = nodeObj.getJSONArray("termination-point");
                        for (int j = 0; j < portArr.length(); j++){
                            JSONObject portObj = portArr.getJSONObject(j);
                            String[] portInfo = portObj.getString("tp-id").split(":");
                            String portNumber = portInfo[portInfo.length - 1];
                            if (!portNumber.equals("LOCAL")){
                                Port port = new Port(portNumber);
                                sw.addPort(portNumber, port);
                            }
                        }
                        topo.addSwitch(sw);
                    }
                }
                //get all the links
                JSONArray linkArr = topoArr.getJSONObject(0).getJSONArray("link");
                for (int i = 0; i < linkArr.length(); i++){
                    JSONObject linkObj = linkArr.getJSONObject(i);
                    String linkId = linkObj.getString("link-id");
                    JSONObject destObj = linkObj.getJSONObject("destination");
                    JSONObject srcObj = linkObj.getJSONObject("source");
                    //link between the host and switch
                    String destNodeId = destObj.getString("dest-node");
                    String srcNodeId = srcObj.getString("source-node");
                    if (destNodeId.contains("host")){
                        continue;
                    }else if (srcNodeId.contains("host")){
                        topo.addHostSwitchPair(topo.getHostIpByNodeId(srcNodeId), destNodeId);
                    }else {
                        String[] destTp = destObj.getString("dest-tp").split(":");
                        String portNumber = destTp[destTp.length - 1];
                        Switch destSwitch = topo.getSwitchByNodeId(destNodeId);
                        destSwitch.getPortByPortNumber(portNumber).setConnectedSwitch(srcNodeId);
                    }
                }
            }else {
                throw new Exception("Can't get the topo information of the network");
            }
        }catch (IOException ioException){
            throw new Exception("Can't read the topo info from remote");
        }
        return topo;
    }

    public void updateTopoInfo(Topology topo){
         for (Switch sw : topo.getSwitchs()){
             updateSwitchInfo(sw);
         }
        out.println(topo);
    }

    public void updateSwitchInfo(Switch sw){
        String nodeId = sw.getNodeId();
        String url = String.format(
                "http://%s:8181/restconf/operational/opendaylight-inventory:nodes/node/%s/",
                ip, nodeId);
        Map<String, Long> rateMap = new HashMap<String, Long>();
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        getRequest.setHeader("Accept", "application/json");

        try {
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream inputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder strBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strBuilder.append(line);
                }

                String responseStr = strBuilder.toString();
                JSONObject responseJson = new JSONObject(responseStr);
                JSONArray nodeInfoArr = responseJson.getJSONArray("node");
                JSONObject nodeInfo = nodeInfoArr.optJSONObject(0);
                JSONArray nodeArr = nodeInfo.getJSONArray("node-connector");

                //iterate to get all the port infomation of the node
                for (int i = 0; i < nodeArr.length(); i++){
                    JSONObject node = nodeArr.getJSONObject(i);
                    String[] nodeIdArr = node.getString("id").split(":");
                    String portNumber = nodeIdArr[nodeIdArr.length - 1];
                    if (portNumber.equals("LOCAL"))
                        continue;
                    JSONObject statistics = node.getJSONObject(
                            "opendaylight-port-statistics:flow-capable-node-connector-statistics");
                    JSONObject bytes = statistics.getJSONObject("bytes");
                    long transmitted = bytes.getLong("transmitted");
                    Long received = bytes.getLong("received");
                    long transport = transmitted + received;
                    rateMap.put(portNumber, transport);
                }
            }
        }catch (IOException e){
            out.println("can't update the switch port rate");
        }
        try{
          Thread.sleep(3000);
        }catch (InterruptedException e){

        }

        try{
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream inputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder strBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strBuilder.append(line);
                }
                String responseStr = strBuilder.toString();
                JSONObject responseJson = new JSONObject(responseStr);
                JSONArray nodeInfoArr = responseJson.getJSONArray("node");
                JSONObject nodeInfo = nodeInfoArr.optJSONObject(0);
                JSONArray nodeArr = nodeInfo.getJSONArray("node-connector");

                for (int i = 0; i < nodeArr.length(); i++) {
                    JSONObject node = nodeArr.getJSONObject(i);
                    String[] nodeIdArr = node.getString("id").split(":");
                    String portNumber = nodeIdArr[nodeIdArr.length - 1];
                    if (portNumber.equals("LOCAL"))
                        continue;
                    JSONObject statistics = node.getJSONObject(
                            "opendaylight-port-statistics:flow-capable-node-connector-statistics");
                    JSONObject bytes = statistics.getJSONObject("bytes");
                    long transmitted = bytes.getLong("transmitted");
                    Long received = bytes.getLong("received");
                    long transport = transmitted + received;
                    long interval = 3;
                    long rate = (transport - rateMap.get(portNumber)) / (interval);
                    rateMap.replace(portNumber, rate);
                }

                for (Map.Entry<String, Long> mapEntry : rateMap.entrySet()) {
                    sw.getPortByPortNumber(mapEntry.getKey()).setRate(mapEntry.getValue());
                }
            }
        }catch (IOException e){

        }
    }

}
