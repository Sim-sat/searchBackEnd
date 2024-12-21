package search;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a forward index. A forward index is a data structure that maps
 * documents to the terms they contain. The forward index is represented as a
 * map which maps a
 * websiteData object to the url.
 *
 * @author Simon Sattelberger
 */

public class ForwardIndex {
    private Map<String, WebsiteData> websiteDataMap = new TreeMap<>();
    private final double DAMPING_FACTOR = 0.85;
    private static double min = 1;
    private static double max = 0;

    /**
     * adds multiple entries to the forward index
     *
     * @param data List of WebsiteData {@link WebsiteData}
     */
    public void addEntries(List<WebsiteData> data) {
        for (WebsiteData websiteData : data) {
            websiteDataMap.put(websiteData.getUrl(), websiteData);
        }
    }

    /**
     * adds a single entry to the forward index
     *
     * @param data WebsiteData object {@link WebsiteData}
     */
    public void addEntry(WebsiteData data) {
        websiteDataMap.put(data.getUrl(), data);
    }

    /**
     * calculates all vectors from a given reverse index and adds them to the
     * websiteData objects{@link WebsiteData}
     *
     * @param reverseIndex reverseIndex {@link ReverseIndex}
     */
    public void calculateVector(Map<String, Map<String, Double>> reverseIndex) {
        int counter = 0;
        Map<String, double[]> vectorMap = new HashMap<>();
        // initilizing the vectorMap with zeroes
        for (String token : reverseIndex.keySet()) {
            for (String document : reverseIndex.get(token).keySet()) {
                if (!vectorMap.containsKey(document)) {
                    double[] vector = new double[reverseIndex.keySet().size()];
                    Arrays.fill(vector, 0.0);
                    vectorMap.put(document, vector);
                }
            }
        }

        // adding values to vector
        for (String token : reverseIndex.keySet()) {
            for (String document : reverseIndex.get(token).keySet()) {
                double tfidf = reverseIndex.get(token).get(document);
                // if vectormap contains the document, get the List/Vector and add the score for
                // the token
                vectorMap.get(document)[counter] = tfidf;
            }
            counter++;
        }
        // saving the vectors to the websiteData objects
        for (String url : vectorMap.keySet()) {
            websiteDataMap.get(url).setVector(normalize(vectorMap.get(url)));
        }

    }

    /***
     *
     * @param vector vector to be normalized
     * @return normalized vector
     */
    public static double[] normalize(double[] vector) {
        double norm = 0.0;
        for (double v : vector) {
            norm += v * v;
        }
        if (norm == 0) {
            norm++;
        }
        norm = Math.sqrt(norm);
        double[] normalizedVector = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalizedVector[i] = vector[i] / norm;
        }
        return normalizedVector;
    }

    /**
     * calculates the damped pagerank for the whole forward index.
     * {@link PageGradingUtil}
     */
    public void calculatePageRankDamped() {
        Map<String, Double> rankMap = PageGradingUtil.calculatePageRankDamped(websiteDataMap, DAMPING_FACTOR);
        for (String url : rankMap.keySet()) {
            double pageRank = rankMap.get(url);
            if (pageRank > max) {
                max = pageRank;
            }
            if (pageRank < min) {
                min = pageRank;
            }
            websiteDataMap.get(url).setPageRank(pageRank);
        }
    }

    /**
     * calculates the normal pagerank for the whole forward index.
     * {@link PageGradingUtil}
     */
    public void calculatePageRank() {
        Map<String, Double> rankMap = PageGradingUtil.calculatePageRank(websiteDataMap);
        for (String url : rankMap.keySet()) {
            double pageRank = rankMap.get(url);
            if (pageRank > max) {
                max = pageRank;
            }
            if (pageRank < min) {
                min = pageRank;
            }
            websiteDataMap.get(url).setPageRank(pageRank);
        }
    }

    /**
     * @return forwardIndex
     */
    public Map<String, WebsiteData> getForwardIndex() {
        return websiteDataMap;
    }

    /**
     * @return minimum of alle Pageranks
     */
    public static double getMin() {
        return min;
    }

    /**
     * @return maximum of alle Pageranks
     */
    public static double getMax() {
        return max;
    }
}
