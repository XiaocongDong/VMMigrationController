package vm.migration.controller.webservice;

import vm.migration.controller.VMMigrationController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by sunny on 16-5-6.
 */
@Path("/hello")
public class MigrationService {

    @GET
    @Path("/{param}")
    public Response getMsg(@PathParam("param") String msg) {

        String output = "Jersey say : " + msg;

        return Response.status(200).entity(output).build();

    }

    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTrackInJSON(Input input) {
        VMMigrationController vmMigrationController = new VMMigrationController();
        boolean result = vmMigrationController.performFlowOptimization(input);
        String inputData = "Input Data : " + input;
        if (result){
            return Response.status(201).entity(inputData + " Flow has been optimized").build();
        }else{
            return Response.status(201).entity(inputData + " can't perform flow optimization").build();
        }
    }
}
