package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * A websitedata object contains all important data from a website.
 *
 * @author Simon Sattelberger
 */
public class WebsiteData {
    public List<String> outgoingLinks = new ArrayList<>();
    public List<String> tokenList = new ArrayList<>();
    private double[] vector;
    public String title;
    public String completeContent;
    public double pageRank;
    public String url;
    public String body;
    public String header;

    public WebsiteData(String pUrl, String pTitle, String pHeader, String pContent, List<String> pOutgoingLinks) {
        this.url = pUrl;
        this.title = pTitle;
        this.outgoingLinks = pOutgoingLinks;
        this.completeContent = "".concat(title).concat(" ").concat(pHeader).concat(" ").concat(pContent);
        this.tokenList = createTokens(completeContent);
        this.body = pContent;
        this.header = pHeader;
    }

    /**
     * creates a printable string from object
     */
    @Override
    public String toString() {
        return "URL : " + url + System.lineSeparator() + "Title: " + title;
    }

    /**
     * @return returns all links
     */
    List<String> getLinks() {
        return outgoingLinks;
    }

    /**
     * @return retuns own url
     */
    String getUrl() {
        return url;
    }

    /**
     * @return returns pagerak
     */
    double getPageRank() {
        return pageRank;
    }

    /**
     * Sets pagerank
     *
     * @param pPageRank
     */
    void setPageRank(double pPageRank) {
        this.pageRank = pPageRank;
    }

    /**
     * @return returns vector
     */
    double[] getVector() {
        return vector;
    }

    /**
     * sets vector
     *
     * @param pVector
     */
    void setVector(double[] pVector) {
        this.vector = pVector;
    }

    /**
     * @return returns tokenized list
     */
    List<String> getTokenList() {
        return tokenList;
    }

    /**
     * tokenizes, lemmetizes and removes stopwords from a text
     *
     * @param input text from which to create tokens
     * @return list of tokens
     */
    public static List<String> createTokens(final String input) {
        List<String> completeContentList = tokenize(input);

        // removing stop words from list
        completeContentList = removeStopWords(completeContentList);
        // lemmatization
        completeContentList = lemmatize(completeContentList);

        return completeContentList;
    }

    /**
     * tokenizes the list with a regex
     *
     * @param input list to be tokenized
     * @return tokenized list
     */
    private static List<String> tokenize(final String input) {
        // removing punctiation from String
        String tempString = input.replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase(Locale.ENGLISH);
        String[] tokens = tempString.split(" ");
        List<String> tokenizedList = new ArrayList<>();
        for (String string : tokens) {
            if (!string.isEmpty()) {
                tokenizedList.add(string);
            }
        }
        return tokenizedList;
    }

    /**
     * removes all stopwords from list. stopwards are saved in stopWords.txt
     *
     * @param inputList list from which to remove stopwords
     * @return list without stopwords
     */
    private static List<String> removeStopWords(List<String> inputList) {
        try {
            // removing all stopwords
            String stopwords = Files.readString(Path.of("src/main/resources/stopWords.txt"));
            List<String> stopwordList = Arrays.asList(stopwords.split(","));
            inputList.removeAll(stopwordList);
            return inputList;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    /**
     * Lemmatizes input list with StandfordCore pipeline
     *
     * @param inputList list to be lemmatized
     * @return lemmatized list
     */
    private static List<String> lemmatize(List<String> inputList) {
        List<String> outputList = new ArrayList<>();

        // create a NLP Pipeline for tokenizing
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // looping through the input list to create the tokenized version and adding it
        // to the output list
        for (String token : inputList) {
            CoreDocument document = pipeline.processToCoreDocument(token);
            CoreLabel label = document.tokens().get(0);
            outputList.add(label.lemma());

        }
        return outputList;
    }

}
