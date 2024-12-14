package org.example;

import Logic.Main;
import Logic.SearchQuery;
import Logic.WebsiteData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/hello")
public class ExampleResource {

    public static void main(String[] args) {
        System.out.println("Hello World");
    }

    @GET
    @Path("{word}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("word") String word) {
        List<String> results;
        List<String> websiteData = new ArrayList<>();
        Map<String, WebsiteData> forwardIndexMap = Main.forwardIndexMap;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            results = SearchQuery.searchPageRank(word, forwardIndexMap, Main.reverseIndexMap);
            for (String result : results) {

                websiteData.add(objectMapper.writeValueAsString(forwardIndexMap.get(result)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return websiteData.toString();
    }
}
