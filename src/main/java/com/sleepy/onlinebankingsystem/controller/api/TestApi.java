package com.sleepy.onlinebankingsystem.controller.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestApi {

    @GET
    public Response test() {
        return Response.ok("{\"message\": \"API is working!\"}").build();
    }
}