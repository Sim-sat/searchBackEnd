package search;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/search")
public class ExampleResource {

    public static void main(String[] args) {
        System.out.println("Hello World");
    }

    @GET
    @Path("/query/")
    @Produces(MediaType.TEXT_PLAIN)
    public String search(@QueryParam("word") String word, @QueryParam("algorithm") String algo) {
        List<String> results = new ArrayList<>();
        List<String> websiteData = new ArrayList<>();
        Map<String, WebsiteData> forwardIndexMap = Main.forwardIndexMap;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            results = switch (algo) {
                case "tfidf" -> SearchQuery.search(word, forwardIndexMap, Main.reverseIndexMap);
                case "pagerank" -> SearchQuery.searchPageRank(word, forwardIndexMap, Main.reverseIndexMap);
                case "cosine" -> SearchQuery.searchCosine(word, forwardIndexMap, Main.reverseIndexMap);
                default -> results;
            };
            for (String result : results) {

                websiteData.add(objectMapper.writeValueAsString(forwardIndexMap.get(result)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return websiteData.toString();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAll() {
        List<String> websiteData = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (String entry : Main.forwardIndexMap.keySet()) {

                websiteData.add(objectMapper.writeValueAsString(Main.forwardIndexMap.get(entry)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return websiteData.toString();
    }
}
