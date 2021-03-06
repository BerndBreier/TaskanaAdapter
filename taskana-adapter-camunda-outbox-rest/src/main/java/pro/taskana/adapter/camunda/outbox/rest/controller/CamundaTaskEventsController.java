package pro.taskana.adapter.camunda.outbox.rest.controller;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pro.taskana.adapter.camunda.outbox.rest.model.CamundaTaskEvent;
import pro.taskana.adapter.camunda.outbox.rest.model.CamundaTaskEventList;
import pro.taskana.adapter.camunda.outbox.rest.resource.CamundaTaskEventListResource;
import pro.taskana.adapter.camunda.outbox.rest.resource.CamundaTaskEventListResourceAssembler;
import pro.taskana.adapter.camunda.outbox.rest.service.CamundaTaskEventsService;

/** Controller for the Outbox REST service. */
@Path("/rest-api/events")
public class CamundaTaskEventsController {

  CamundaTaskEventsService camundaTaskEventService = new CamundaTaskEventsService();
  CamundaTaskEventListResourceAssembler camundaTaskEventListResourceAssembler =
      new CamundaTaskEventListResourceAssembler();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getEvents(@QueryParam("type") final List<String> requestedEventTypes) {

    List<CamundaTaskEvent> camundaTaskEvents =
        camundaTaskEventService.getEvents(requestedEventTypes);

    CamundaTaskEventList camundaTaskEventList = new CamundaTaskEventList();
    camundaTaskEventList.setCamundaTaskEvents(camundaTaskEvents);

    CamundaTaskEventListResource camundaTaskEventListResource =
        camundaTaskEventListResourceAssembler.toResource(camundaTaskEventList);

    return Response.status(200).entity(camundaTaskEventListResource).build();
  }

  @Path("/delete")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteEvents(String idsAsString) {

    camundaTaskEventService.deleteEvents(idsAsString);

    return Response.status(200).build();
  }
}
