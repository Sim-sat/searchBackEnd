package search;


import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for caluclation of pagerank, damped pagerank and cosine
 * similarity.
 */
public final class PageGradingUtil {
    final static double epsilon = 0.0001;

    private PageGradingUtil() {
    }

    /**
     * calculates pagerank iteratively
     *
     * @param forwardIndex forward index{@link ForwardIndex}
     * @return map that maps the pagerank value to url
     * @see <a href="https://en.wikipedia.org/wiki/PageRank">...</a>
     */
    public static Map<String, Double> calculatePageRank(Map<String, WebsiteData> forwardIndex) {
        Map<String, Double> rankMap = new HashMap<>();
        double size = forwardIndex.keySet().size();
        double summedDifference = 1.0;
        final double initValue = 1.0 / size;
        // initilize
        for (String website : forwardIndex.keySet()) {
            rankMap.put(website, initValue);
        }

        // while the sum of all differences is smaller than epsilon, calculate the
        // pagerank iteratively
        while (summedDifference > epsilon) {
            summedDifference = 0.0;
            Map<String, Double> newRankMap = new HashMap<>();
            for (String website : forwardIndex.keySet()) {
                // going through all sites that link to website
                double score = 0;
                for (String website2 : forwardIndex.keySet()) {
                    if (forwardIndex.get(website2).getLinks().contains(website)) {
                        double outgoingLinks = forwardIndex.get(website2).getLinks().size();
                        score += rankMap.get(website2) / outgoingLinks;
                    }
                }
                summedDifference += Math.abs(score - rankMap.get(website));
                newRankMap.put(website, score);
            }
            rankMap = newRankMap;
        }

        return rankMap;
    }

    /**
     * calculates pagerank iteratively with damping factor
     *
     * @param forwardIndex  forward index{@link ForwardIndex}
     * @param dampingFactor damping factor
     * @return map that maps the pagerank value to url
     * @see <a href="https://en.wikipedia.org/wiki/PageRank">...</a>
     */
    public static Map<String, Double> calculatePageRankDamped(Map<String, WebsiteData> forwardIndex,
                                                              double dampingFactor) {
        Map<String, Double> rankMap = new HashMap<>();
        double size = forwardIndex.keySet().size();
        final double initValue = 1.0 / size;
        double summedDifference = 1.0;
        // initilize
        for (String website : forwardIndex.keySet()) {
            rankMap.put(website, initValue);
        }
        // while the sum of all differences is smaller than epsilon, calculate the
        // pagerank iteratively
        while (summedDifference > epsilon) {
            summedDifference = 0.0;
            Map<String, Double> newRankMap = new HashMap<>();
            for (String website : forwardIndex.keySet()) {
                double score = 0;
                // going through all sites that link to website
                for (String website2 : forwardIndex.keySet()) {
                    if (forwardIndex.get(website2).getLinks().contains(website)) {
                        double outgoingLinks = forwardIndex.get(website2).getLinks().size();
                        score += rankMap.get(website2) / outgoingLinks;
                    }
                }
                score = score * dampingFactor + (1 - dampingFactor) * initValue;
                summedDifference += Math.abs(score - rankMap.get(website));
                newRankMap.put(website, score);
            }
            rankMap = newRankMap;
        }
        return rankMap;
    }

    /**
     * calculates the cosine similarity
     *
     * @param vector1 vector1
     * @param vector2 vector 2
     * @return cosine similarity
     * @see <a href="https://en.wikipedia.org/wiki/Cosine_similarity">...</a>
     */
    public static double cosineSimilarity(final double[] vector1, final double[] vector2) {
        // check if both vectors are of the same length
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Both Vectors should be equal");
        }

        double numerator = 0;
        double a = 0;
        double b = 0;

        for (int i = 0; i < vector1.length; i++) {
            double v1 = vector1[i];
            double v2 = vector2[i];
            numerator = numerator + v1 * v2;
            a = a + v1 * v1;
            b = b + v2 * v2;
        }
        a = Math.sqrt(a);
        b = Math.sqrt(b);
        double denominator = a * b;

        return numerator / denominator;
    }

    /**
     * calculates the cosine similarity when the vectors are normalized
     *
     * @param vector1
     * @param vector2
     * @return cosine similarity
     */
    public static double cosineSimilarityImproved(final double[] vector1, final double[] vector2) {
        // check if both vectors are of the same length
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Both Vectors should be equal");
        }
        double result = 0;
        for (int i = 0; i < vector1.length; i++) {
            result += vector1[i] * vector2[i];
        }
        return result;
    }
}
