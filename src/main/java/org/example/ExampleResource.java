package org.example;

import Logic.Main;
import Logic.SearchQuery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/hello")
public class ExampleResource {

    public static void main(String[] args) {
        System.out.println("Hello World");
    }

    @GET
    @Path("{word}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("word") String word) {
        List<String> results = new ArrayList<>();
        try {
            results = SearchQuery.searchPageRank(word, Main.forwardIndexMap, Main.reverseIndexMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return results.toString();
    }
}
