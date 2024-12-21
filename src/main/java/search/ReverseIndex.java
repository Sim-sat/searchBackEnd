package search;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a reverse index. A reverse index is a
 * Data structure that maps terms to the documents in which they are.
 */

public final class ReverseIndex {

    private ReverseIndex() {
    }

    /**
     * creates a reverse index from a forward index
     *
     * @param mapOfWebsiteData forward index {@link ForwardIndex}
     * @return reverse index
     * @throws IOException
     */
    public static Map<String, Map<String, Double>> getReverseIndex(Map<String, WebsiteData> mapOfWebsiteData)
            throws IOException {
        // Map of key=token, value=(Map of key=document, value=tfidf score)
        Map<String, Map<String, Double>> reverseIndex = new TreeMap<>();

        for (String url : mapOfWebsiteData.keySet()) {
            List<String> completeTokenList = mapOfWebsiteData.get(url).getTokenList();

            // loops through all tokens and creates the reverse index
            for (String token : completeTokenList) {

                // when the token doesnt exist create a new entry to the hashmap
                if (!reverseIndex.containsKey(token)) {
                    // second map saving document an tfidf score
                    Map<String, Double> documents = new HashMap<>();
                    // initilizing the tf score

                    documents.put(url, calculateTF(token, completeTokenList));
                    reverseIndex.put(token, documents);
                } else {
                    // when the tokens exists, get the map and add an entry
                    reverseIndex.get(token).put(url, calculateTF(token, completeTokenList));
                }
            }
        }

        // calculating the IDF Score

        for (String token : reverseIndex.keySet()) {
            // counting of the documents containing token
            double invertedDocumentFrequency = getIDF(token, mapOfWebsiteData, reverseIndex);

            // looping through inner map to set tfidf score
            for (String document : reverseIndex.get(token).keySet()) {
                double currentTFScore = reverseIndex.get(token).get(document);
                reverseIndex.get(token).put(document, currentTFScore * invertedDocumentFrequency);

            }

        }
        return reverseIndex;
    }

    /**
     * calculates the idf score for a token
     * {@see https://en.wikipedia.org/wiki/Tf-idf}
     *
     * @param token           token
     * @param forwardIndexMap forward index{@link ForwardIndex}
     * @param reverseIndexMap reverse index {@link ReverseIndex}
     * @return idf score
     */
    static double getIDF(String token, Map<String, WebsiteData> forwardIndexMap,
                         Map<String, Map<String, Double>> reverseIndexMap) {
        double numberOfDocuments = forwardIndexMap.keySet().size();
        double documentFrequency = reverseIndexMap.get(token).keySet().size();
        return Math.log(numberOfDocuments / documentFrequency);
    }

    /**
     * calculates the tf score for a token in a token list
     * {@see https://en.wikipedia.org/wiki/Tf-idf}
     *
     * @param token token
     * @param doc   token list
     * @return tf score
     */
    static double calculateTF(String token, List<String> doc) {
        double numOfWords = doc.size();
        double countOfToken = Collections.frequency(doc, token);

        return countOfToken / numOfWords;
    }

}
