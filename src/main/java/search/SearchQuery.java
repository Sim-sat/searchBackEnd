package search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utlitiy class for search queries with different rankings algorithms.
 *
 * @author Simon Sattelberger
 */
public final class SearchQuery {

    private static final double WEIGHT = 0.85;
    // only used for graph creation
    public static Map<String, Double> finalScoreMap;

    private SearchQuery() {
    }

    /**
     * Searching for all website containing the search query. The results are
     * ordered by tfidf score
     *
     * @param query            query which is searched for
     * @param mapOfWebsiteData forward index {@link ForwardIndex}
     * @param reverseIndexMap  reverse index {@link ReverseIndex}
     * @return list of all found urls
     * @throws IOException
     * @see <a href="https://en.wikipedia.org/wiki/Tf-idf">...</a>
     */
    public static List<String> search(final String query, Map<String, WebsiteData> mapOfWebsiteData,
                                      Map<String, Map<String, Double>> reverseIndexMap)
            throws IOException {
        Map<String, Double> summedMetric = new HashMap<>();

        // processing the search query
        List<String> queryList = WebsiteData.createTokens(query);

        for (String token : queryList) {
            if (reverseIndexMap.keySet().contains(token)) {
                for (String url : reverseIndexMap.get(token).keySet()) {
                    double currentMetric = reverseIndexMap.get(token).get(url);
                    // if the summedMetric map already contains the url, adding the new tfdidf to
                    // the old one
                    if (summedMetric.containsKey(url)) {
                        double currentScoreForUrl = summedMetric.get(url);
                        summedMetric.put(url, currentScoreForUrl + currentMetric);
                        // if the summedMetric map doesnt contain the url, add a new entry with key=url
                        // and value=tfidf score
                    } else {
                        summedMetric.put(url, currentMetric);
                    }
                }
            }
        }

        // descending sorting the keys by the value of tfidf score
        List<String> sortedUrls = new ArrayList<>(summedMetric.keySet());
        sortedUrls.sort((k1, k2) -> summedMetric.get(k2).compareTo(summedMetric.get(k1)));
        return sortedUrls;
    }


    /**
     * Searching for all website containing the search query. The results are
     * ordered by pagerank and cosine similarity.
     *
     * @param query            query which is searched for
     * @param mapOfWebsiteData forward index {@link ForwardIndex}
     * @param reverseIndexMap  reverse index {@link ReverseIndex}
     * @return list of all found urls
     * @throws IOException
     * @see <a href="https://en.wikipedia.org/wiki/Cosine_similarity">...</a>
     * @see <a href="https://en.wikipedia.org/wiki/PageRank">...</a>
     */
    public static List<String> searchPageRank(final String query, Map<String, WebsiteData> mapOfWebsiteData,
                                                Map<String, Map<String, Double>> reverseIndexMap)
            throws IOException {
        Map<String, Double> combinedScoreMap = new HashMap<>();

        // calculating combined score and saving it to combinedScoreMap
        double[] queryVector = getQueryVector(query, mapOfWebsiteData, reverseIndexMap);
        List<String> result = search(query, mapOfWebsiteData, reverseIndexMap);
        for (String url : result) {
            double similarity = PageGradingUtil.cosineSimilarityImproved(queryVector,
                    mapOfWebsiteData.get(url).getVector());

            double pagerank = mapOfWebsiteData.get(url).getPageRank();
            double score = WEIGHT * similarity + (1 - WEIGHT) * normalizePagerRank(pagerank);
            combinedScoreMap.put(url, score);

        }
        // only used for graph creation for extra task
        finalScoreMap = combinedScoreMap;

        // descending sorting the keys by the value of tfidf score
        List<String> sortedUrls = new ArrayList<>(combinedScoreMap.keySet());
        sortedUrls.sort((k1, k2) -> combinedScoreMap.get(k2).compareTo(combinedScoreMap.get(k1)));
        if (sortedUrls.size() > 30) {
            sortedUrls = sortedUrls.subList(0, 30);
        }
        return sortedUrls;
    }

    /**
     * Searching for all website containing the search query. The results are
     * ordered by cosine similarity.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Cosine_similarity">...</a>
     *
     * @param query            query which is searched for
     * @param mapOfWebsiteData forward index {@link ForwardIndex}
     * @param reverseIndexMap  reverse index {@link ReverseIndex}
     * @return list of all found urls
     * @throws IOException
     */
    public static List<String> searchCosine(final String query, Map<String, WebsiteData> mapOfWebsiteData,
                                            Map<String, Map<String, Double>> reverseIndexMap)
            throws IOException {
        Map<String, Double> cosineMap = new HashMap<>();

        double[] queryVector = getQueryVector(query, mapOfWebsiteData, reverseIndexMap);

        // calculating cosine Similarity and saving it to cosineMap
        List<String> result = search(query, mapOfWebsiteData, reverseIndexMap);
        for (String url : result) {
            double similarity = PageGradingUtil.cosineSimilarityImproved(queryVector,
                    mapOfWebsiteData.get(url).getVector());
            cosineMap.put(url, similarity);
        }
        // descending sorting the keys by the value of tfidf score
        List<String> sortedUrls = new ArrayList<>(cosineMap.keySet());
        sortedUrls.sort((k1, k2) -> cosineMap.get(k2).compareTo(cosineMap.get(k1)));
        return sortedUrls;
    }

    /**
     * creates vector for given query, entries are weighted based on their tf score
     *
     * @param query
     * @param forwardIndexMap
     * @param reverseIndexMap
     * @return query vector
     */
    private static double[] getQueryVector(final String query, Map<String, WebsiteData> forwardIndexMap,
                                           Map<String, Map<String, Double>> reverseIndexMap) {
        double[] vector = new double[reverseIndexMap.keySet().size()];
        Arrays.fill(vector, 0.0);

        // processing the search query
        List<String> queryList = WebsiteData.createTokens(query);

        int counter = 0;
        for (String token : reverseIndexMap.keySet()) {
            if (queryList.contains(token)) {

                double tf = ReverseIndex.calculateTF(token, queryList);
                vector[counter] = tf;
            }
            counter++;
        }
        return ForwardIndex.normalize(vector);
    }

    /**
     * normalizes pagerank with min-max scaling
     *
     * @param pagerank
     * @return normalized pagerank
     */
    private static double normalizePagerRank(double pagerank) {
        double min = ForwardIndex.getMin();
        double max = ForwardIndex.getMax();
        return (pagerank - min) / (max - min);
    }
}
