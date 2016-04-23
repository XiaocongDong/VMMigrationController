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

/**
 * Created by xiaocdon on 2016/4/23.
 */
public class ControllerBroker {
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
        return true;
    }

    public Topology getNetWorkTopo(){
        Topology topo = new Topology();
        return topo;
    }

    public void updateTopoData(Topology topo){

    }

    public static void getFlowEntry(String uri){
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        httpGet.setHeader("Accept", "application/json");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
            InputStream inputStream = response.getEntity().getContent();
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder strBuider = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null){
                    strBuider.append(line + "\n");
                }

                String responseStr = strBuider.toString();
                System.out.println(responseStr);
                JSONObject jsonObject = new JSONObject(responseStr);
                JSONArray value = (JSONArray)jsonObject.get("node");
                JSONObject idEntity = (JSONObject)value.get(0);
                String id =idEntity.getString("id");
                System.out.println(id);


            }catch (Exception e){
                System.out.print(e.toString());
            }
        }catch (IOException e){

        }
    }
}
