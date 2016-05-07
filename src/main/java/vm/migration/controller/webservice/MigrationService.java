package vm.migration.controller.webservice;

import vm.migration.controller.VMMigrationController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by sunny on 16-5-6.
 */
@Path("/MigrationService")
public class MigrationService {
    @POST
    @Path("/Optimize")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTrackInJSON(Input input) {
        VMMigrationController vmMigrationController = new VMMigrationController();
        boolean result = vmMigrationController.performFlowOptimization(input);
        String inputData = "Input Data : " + input;
        if (result){
            return Response.status(201).entity(inputData + " flow has been optimized").build();
        }else{
            return Response.status(201).entity(inputData + " can't perform flow optimization").build();
        }
    }
}
