package vm.migration.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.lang.System.out;
/**
 * Created by xiaocdon on 2016/4/23.
 */
public class ControllerBroker {
    String ip;

    public ControllerBroker(String ip){
        this.ip = ip;
    }

    public boolean deleteFlowEntries(List<FlowEntry> flowEntries){
        for (FlowEntry flowEntry : flowEntries){
            if (!deleteFloeEntry(flowEntry))
                return false;
        }
        return true;
    }

    private boolean deleteFloeEntry(FlowEntry flowEntry){
        return true;
    }

    public boolean addFlowEntries(List<FlowEntry> flowEntries){
        for (FlowEntry flowEntry : flowEntries){
            if (!addFlowEntry(flowEntry)){
                return false;
            }
        }
        return true;
    }

    private boolean addFlowEntry(FlowEntry flowEntry){
        String body = String.format(
                "<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n" +
                "<flow xmlns='urn:opendaylight:flow:inventory'>\n" +
                        "<strict>false</strict>\n" +
                        "<flow-name>testFlow</flow-name>\n" +
                        "<id>258</id>\n" +
                        "<cookie_mask>255</cookie_mask>\n" +
                        "<cookie>103</cookie>\n" +
                        "<table_id>2</table_id>\n" +
                        "<priority>2</priority>\n" +
                        "<hard-timeout>1200</hard-timeout>\n" +
                        "<idle-timeout>3400</idle-timeout>\n" +
                        "<installHw>false</installHw>\n" +
                        "<instructions>\n" +
                            "<instruction>\n" +
                                "<order>0</order>\n" +
                                "<apply-actions>\n" +
                                    "<action>\n" +
                                        "<order>0</order>\n" +
                                        "<output-action>\n" +
                                            "<output-node-connector>1</output-node-connector>\n" +
                                            "<max-length>60</max-length>\n" +
                                        "</output-action>\n" +
                                    "</action>\n" +
                                "</apply-actions>\n" +
                            "</instruction>\n" +
                        "</instructions>\n" +
                        "<match>\n" +
                            "<ipv4-source>10.0.0.1/8</ipv4-source>\n" +
                            "<ipv4-destination>10.0.0.2/8</ipv4-destination>\n" +
                        "</match>\n" +
                "</flow>");
        return true;
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
//                out.println(responseStr);

                JSONObject responseJson = new JSONObject(responseStr);
                JSONObject topologyObj = responseJson.getJSONObject("network-topology");
                JSONArray topoArr = topologyObj.getJSONArray("topology");
//                out.println(topoArr.toString());
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
//                        out.println(host);
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
                int i = 1;
            }else {
                throw new Exception("Can't get the topo information of the network");
            }
        }catch (IOException ioException){
            throw new Exception("Can't read the topo info from remote");
        }
        return topo;
    }

    public void updateTopoData(Topology topo){

    }
}
