package search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.POSModel;
import java.io.InputStream;

/**
 * A websitedata object contains all important data from a website.
 *
 * @author Simon Sattelberger
 */
public class WebsiteData {
    public List<String> outgoingLinks;
    public List<String> tokenList;
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
            InputStream inputStream = WebsiteData.class.getClassLoader().getResourceAsStream("stopWords.txt");

            if (inputStream == null) {
                throw new IOException("Die Datei 'stopWords.txt' wurde nicht gefunden.");
            }
            String stopwords =  new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            List<String> stopwordList = Arrays.asList(stopwords.split(","));
            inputList.removeAll(stopwordList);
            return inputList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lemmatizes input list with StandfordCore pipeline
     *
     * @param inputList list to be lemmatized
     * @return lemmatized list
     */
    private static List<String> lemmatize(List<String> inputList) {
        try{


            // Load POS Tagger model
            InputStream modelIn = WebsiteData.class.getClassLoader().getResourceAsStream("en-pos.bin");
            if (modelIn == null) {
                throw new IOException("POS-Modell 'en-pos.bin' nicht gefunden.");
            }
            POSModel model2 = new POSModel(modelIn);
            POSTaggerME posTagger = new POSTaggerME(model2);
            LemmatizerModel model;
            // Load Lemmatizer dictionary
            InputStream dictLemmatizer = WebsiteData.class.getClassLoader().getResourceAsStream("en-lemmatizer.bin");
            if (dictLemmatizer == null) {
                throw new IOException("Lemmatizer-Modell 'en-lemmatizer.bin' nicht gefunden.");
            }
            model = new LemmatizerModel(dictLemmatizer);
            LemmatizerME lemmatizerME = new LemmatizerME(model);


            String[] tokens = inputList.toArray(new String[0]);

            // POS tagging
            String[] posTags = posTagger.tag(tokens);

            // Lemmatization
            String[] lemmas = lemmatizerME.lemmatize(tokens, posTags);

            // Add lemmatized tokens to output list
            return new ArrayList<>(Arrays.asList(lemmas));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
            return null;
        }

    }


